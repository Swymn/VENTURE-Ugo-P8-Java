package com.openclassrooms.tourguide.service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

@Service
public class RewardsService {
	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
	private static final int NAUTICAL_COEF = 60;

	// proximity in miles
    private final int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;
	
	public RewardsService(final GpsUtil gpsUtil, final RewardCentral rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
	}
	
	public void setProximityBuffer(final int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}
	
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}
	
	public void calculateRewards(final User user) {
		final List<VisitedLocation> userLocations = user.getVisitedLocations();
		final List<Attraction> attractions = gpsUtil.getAttractions();

		final var executorService = Executors.newFixedThreadPool(10);

		for (final var visitedLocation : userLocations) {
			for (final var  attraction : attractions) {
				executorService.submit(() -> {
					if (user.getUserRewards().stream().noneMatch(r -> r.attraction.attractionName.equals(attraction.attractionName))) {
						if (nearAttraction(visitedLocation, attraction)) {
							user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
						}
					}
				});
			}
		}

		executorService.shutdown();
		try {
			if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
				executorService.shutdownNow();
			}
		} catch (InterruptedException e) {
			executorService.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
	
	public boolean isWithinAttractionProximity(final Attraction attraction, final Location location) {
		return !(getDistance(attraction, location) > attractionProximityRange);
	}
	
	private boolean nearAttraction(final VisitedLocation visitedLocation, final Attraction attraction) {
		return !(getDistance(attraction, visitedLocation.location) > proximityBuffer);
	}
	
	public int getRewardPoints(final Attraction attraction, final User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}
	
	public double getDistance(final Location loc1, final Location loc2) {
        final var lat1 = Math.toRadians(loc1.latitude);
        final var lon1 = Math.toRadians(loc1.longitude);
        final var lat2 = Math.toRadians(loc2.latitude);
        final var lon2 = Math.toRadians(loc2.longitude);

        final var angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        final var nauticalMiles = NAUTICAL_COEF * Math.toDegrees(angle);
        final var statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
	}

}
