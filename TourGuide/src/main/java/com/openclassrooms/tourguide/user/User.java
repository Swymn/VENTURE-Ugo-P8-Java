package com.openclassrooms.tourguide.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import gpsUtil.location.VisitedLocation;
import tripPricer.Provider;

public class User {
	private final UUID userId;
	private final String userName;

	private String phoneNumber;
	private String emailAddress;
	private Date latestLocationTimestamp;
	private List<VisitedLocation> visitedLocations = new ArrayList<>();
	private List<UserReward> userRewards = new ArrayList<>();
	private List<Provider> tripDeals = new ArrayList<>();
	private UserPreferences userPreferences = new UserPreferences();

	public User(final UUID userId, final String userName, final String phoneNumber, final String emailAddress) {
		this.userId = userId;
		this.userName = userName;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
	}
	
	public UUID getUserId() {
		return userId;
	}
	
	public String getUserName() {
		return userName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(final String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public Date getLatestLocationTimestamp() {
		return latestLocationTimestamp;
	}
	
	public void setLatestLocationTimestamp(final Date latestLocationTimestamp) {
		this.latestLocationTimestamp = latestLocationTimestamp;
	}

	public List<VisitedLocation> getVisitedLocations() {
		return visitedLocations;
	}

	public VisitedLocation getLastVisitedLocation() {
		return visitedLocations.get(visitedLocations.size() - 1);
	}

	public void addToVisitedLocations(final VisitedLocation visitedLocation) {
		visitedLocations.add(visitedLocation);
	}
	
	public void clearVisitedLocations() {
		visitedLocations.clear();
	}

	public List<UserReward> getUserRewards() {
		return userRewards;
	}

	public void addUserReward(final UserReward userReward) {
		if(userRewards.stream().filter(r -> !r.attraction.attractionName.equals(userReward.attraction)).count() == 0) {
			userRewards.add(userReward);
		}
	}
	
	public UserPreferences getUserPreferences() {
		return userPreferences;
	}
	
	public void setUserPreferences(final UserPreferences userPreferences) {
		this.userPreferences = userPreferences;
	}

	public List<Provider> getTripDeals() {
		return tripDeals;
	}

	public void setTripDeals(final List<Provider> tripDeals) {
		this.tripDeals = tripDeals;
	}
}
