package com.openclassrooms.tourguide;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

import tripPricer.Provider;

@RestController
public class TourGuideController {

    private final TourGuideService tourGuideService;

    public TourGuideController(final TourGuideService tourGuideService) {
        this.tourGuideService = tourGuideService;
    }

    @GetMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @GetMapping("/getLocation") 
    public VisitedLocation getLocation(@RequestParam final String userName) {
    	return tourGuideService.getUserLocation(getUser(userName));
    }
    
    @GetMapping("/getNearbyAttractions") 
    public List<Attraction> getNearbyAttractions(@RequestParam final String userName) {
    	var visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return tourGuideService.getNearByAttractions(visitedLocation);
    }
    
    @GetMapping("/getRewards") 
    public List<UserReward> getRewards(@RequestParam final String userName) {
    	return tourGuideService.getUserRewards(getUser(userName));
    }
       
    @GetMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam final String userName) {
    	return tourGuideService.getTripDeals(getUser(userName));
    }

    /**
     * Get user by name
     * @param userName user name
     * @return user
     */
    private User getUser(final String userName) {
    	return tourGuideService.getUser(userName);
    }
}