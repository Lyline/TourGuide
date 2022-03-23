package tourGuide.service;

import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;
import tourGuide.proxy.gpsProxy.GpsProxy;
import tourGuide.proxy.gpsProxy.location.Attraction;
import tourGuide.proxy.gpsProxy.location.Location;
import tourGuide.proxy.gpsProxy.location.VisitedLocation;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import java.util.List;

@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
	private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final GpsProxy gpsProxy;
	private final RewardCentral rewardsCentral;
	
	public RewardsService(GpsProxy gpsProxy, RewardCentral rewardCentral) {
		this.gpsProxy = gpsProxy;
		this.rewardsCentral = rewardCentral;
	}
	
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}
	
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}
	
	public void calculateRewards(User user) {
		List<VisitedLocation> userLocations = user.getVisitedLocations();
		List<Attraction> attractions = gpsProxy.getAttractions();
		
		for(VisitedLocation visitedLocation : userLocations) {
			for(Attraction attraction : attractions) {
				if(user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
					if(nearAttraction(visitedLocation, attraction)) {
						user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
					}
				}
			}
		}
	}
	
	/*
		Method is not used

	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}*/
	
	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}
	
	private int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}
	
	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
	}

}
