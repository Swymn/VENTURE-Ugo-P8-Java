package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.tracker.Tracker;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;

import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideService {
    private final GpsUtil gpsUtil;
    private final RewardsService rewardsService;
    private final TripPricer tripPricer = new TripPricer();

    private ExecutorService executorService;
    private Logger logger = LoggerFactory.getLogger(TourGuideService.class);

    public final Tracker tracker;

    boolean testMode = true;

    public TourGuideService(final GpsUtil gpsUtil, final RewardsService rewardsService) {
        this.gpsUtil = gpsUtil;
        this.rewardsService = rewardsService;

        executorService = Executors.newFixedThreadPool(100);

        Locale.setDefault(Locale.US);

        if (testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            initializeInternalUsers();
            logger.debug("Finished initializing users");
        }
        tracker = new Tracker(this);
        addShutDownHook();
    }

    public List<UserReward> getUserRewards(final User user) {
        return user.getUserRewards();
    }

    public VisitedLocation getUserLocation(final User user) {
        return (!user.getVisitedLocations().isEmpty()) ? user.getLastVisitedLocation()
                : trackUserLocation(user);
    }

    public User getUser(final String userName) {
        return internalUserMap.get(userName);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(internalUserMap.values());
    }

    public void addUser(final User user) {
        if (!internalUserMap.containsKey(user.getUserName())) {
            internalUserMap.put(user.getUserName(), user);
        }
    }

    public List<Provider> getTripDeals(final User user) {
        final var cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(UserReward::getRewardPoints).sum();
        final List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(),
                user.getUserPreferences().getNumberOfAdults(), user.getUserPreferences().getNumberOfChildren(),
                user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
        user.setTripDeals(providers);
        return providers;
    }

    public VisitedLocation trackUserLocation(final User user) {
        final var visitedLocation = gpsUtil.getUserLocation(user.getUserId());
        user.addToVisitedLocations(visitedLocation);
        rewardsService.calculateRewards(user);
        return visitedLocation;
    }

    public List<Attraction> getNearByAttractions(final VisitedLocation visitedLocation) {
        final var attractions = gpsUtil.getAttractions();
        attractions.sort(Comparator.comparingDouble(attraction -> rewardsService.getDistance(visitedLocation.location, attraction)));
        return attractions.subList(0, 5);
    }

    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                tracker.stopTracking();
            }
        });
    }

    /**********************************************************************************
     *
     * Methods Below: For Internal Testing
     *
     **********************************************************************************/
    private static final String tripPricerApiKey = "test-server-api-key";
    // Database connection will be used for external users, but for testing purposes
    // internal users are provided and stored in memory
    private final Map<String, User> internalUserMap = new HashMap<>();

    private void initializeInternalUsers() {
        IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
            final var userName = "internalUser" + i;
            final var phone = "000";
            final var email = userName + "@tourGuide.com";
            final var user = new User(UUID.randomUUID(), userName, phone, email);
            generateUserLocationHistory(user);

            internalUserMap.put(userName, user);
        });
        logger.debug("Created {} internal test users.", InternalTestHelper.getInternalUserNumber());
    }

    private void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i -> {
            user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
                    new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
        });
    }

    private double generateRandomLongitude() {
        final var leftLimit = -180;
        final var rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private Date getRandomTime() {
        final var localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

    public void stopTrackingUsersAndCompleteTasks() {
        tracker.stopTracking();
        logger.debug("Tracker stopped. Completing tasks . . .");
        executorService.shutdown();
        int minutes = 0;
        while (true) {
            try {
                if (executorService.awaitTermination(1, TimeUnit.MINUTES)) break;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            logger.debug("Completing tasks . . . (elapsed {} minutes)", ++minutes);
        }
    }
}
