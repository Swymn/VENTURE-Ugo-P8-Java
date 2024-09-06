package com.openclassrooms.tourguide.user;

public class UserPreferences {
	
	private int attractionProximity = Integer.MAX_VALUE;
	private int tripDuration = 1;
	private int ticketQuantity = 1;
	private int numberOfAdults = 1;
	private int numberOfChildren = 0;

	public int getAttractionProximity() {
		return attractionProximity;
	}

	public void setAttractionProximity(final int attractionProximity) {
		this.attractionProximity = attractionProximity;
	}
	
	public int getTripDuration() {
		return tripDuration;
	}

	public void setTripDuration(final int tripDuration) {
		this.tripDuration = tripDuration;
	}

	public int getTicketQuantity() {
		return ticketQuantity;
	}

	public void setTicketQuantity(final int ticketQuantity) {
		this.ticketQuantity = ticketQuantity;
	}
	
	public int getNumberOfAdults() {
		return numberOfAdults;
	}

	public void setNumberOfAdults(final int numberOfAdults) {
		this.numberOfAdults = numberOfAdults;
	}

	public int getNumberOfChildren() {
		return numberOfChildren;
	}

	public void setNumberOfChildren(final int numberOfChildren) {
		this.numberOfChildren = numberOfChildren;
	}
}
