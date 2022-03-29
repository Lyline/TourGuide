package tourGuide;

import org.junit.Test;
import tourGuide.proxy.gpsProxy.GpsProxy;
import tourGuide.proxy.gpsProxy.location.Attraction;
import tourGuide.proxy.gpsProxy.location.VisitedLocation;
import tourGuide.proxy.rewardCentralProxy.RewardProxy;
import tourGuide.proxy.tripPricerProxy.TripPricer;
import tourGuide.repository.UserGeneratorRepositoryImpl;
import tourGuide.repository.UserRepository;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestRewardsService {
  GpsProxy gpsProxy = new GpsProxy();
  RewardsService rewardsService = new RewardsService(gpsProxy, new RewardProxy());
  UserRepository repository=new UserGeneratorRepositoryImpl();
  TourGuideService tourGuideService = new TourGuideService(gpsProxy, rewardsService,new TripPricer(),repository);

	@Test
	public void userGetRewards() {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		Attraction attraction = gpsProxy.getAttractions().get(0);

		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));

		tourGuideService.trackUserLocation(user);

		List<UserReward> userRewards = user.getUserRewards();

		assertTrue(userRewards.size() == 1);
	}
	
	@Test
	public void isWithinAttractionProximity() {
		Attraction attraction = gpsProxy.getAttractions().get(0);
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
	}
	
	//@Ignore // Needs fixed - can throw ConcurrentModificationException
	@Test
	public void nearAllAttractions() {
		rewardsService.setProximityBuffer(Integer.MAX_VALUE);

    repository.initializeInternalUsers(1);

		rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));

    List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));

		assertEquals(gpsProxy.getAttractions().size(), userRewards.size());
	}
}
