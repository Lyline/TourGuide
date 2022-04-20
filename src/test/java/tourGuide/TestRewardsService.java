package tourGuide;

import org.junit.jupiter.api.Test;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tourGuide.proxies.gpsProxy.GpsProxy;
import tourGuide.proxies.gpsProxy.beans.Attraction;
import tourGuide.proxies.gpsProxy.beans.VisitedLocation;
import tourGuide.proxies.rewardCentralProxy.RewardProxy;
import tourGuide.proxies.tripPricerProxy.TripPricerProxy;
import tourGuide.repository.UserGeneratorRepositoryImpl;
import tourGuide.repository.UserRepository;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRewardsService {
  private TourGuideModule tourGuideModule= new TourGuideModule();
	private GpsProxy gpsProxy= tourGuideModule.getGpsProxyTest();
	private RewardProxy rewardProxy= tourGuideModule.getRewardProxyTest();
	private TripPricerProxy tripPricerProxy= tourGuideModule.getTripPricerProxyTest();

  RewardsService rewardsService = new RewardsService(gpsProxy, rewardProxy);
  UserRepository repository=new UserGeneratorRepositoryImpl();
  TourGuideService tourGuideService = new TourGuideService(gpsProxy, rewardsService,tripPricerProxy,repository);

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

	@Test
	public void nearAllAttractions() {
		rewardsService.setProximityBuffer(Integer.MAX_VALUE);

    repository.initializeInternalUsers(1);

		rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));

    List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));

		assertEquals(gpsProxy.getAttractions().size(), userRewards.size());
	}
}
