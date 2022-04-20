package tourGuide;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tourGuide.model.User;
import tourGuide.proxies.gpsProxy.beans.Attraction;
import tourGuide.proxies.gpsProxy.beans.VisitedLocation;
import tourGuide.repository.UserGeneratorRepositoryImpl;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.tracker.TrackUser;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPerformance {
	
	/*
	 * A note on performance improvements:
	 *     
	 *     The number of users generated for the high volume tests can be easily adjusted via this method:
	 *     
	 *     		InternalTestHelper.setInternalUserNumber(100000);
	 *     
	 *     
	 *     These tests can be modified to suit new solutions, just as long as the performance metrics
	 *     at the end of the tests remains consistent. 
	 * 
	 *     These are performance metrics that we are trying to hit:
	 *     
	 *     highVolumeTrackLocation: 100,000 users within 15 minutes:
	 *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     *
     *     highVolumeGetRewards: 100,000 users within 20 minutes:
	 *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 */

	private TourGuideModule tourGuideModule= new TourGuideModule();
	private UserGeneratorRepositoryImpl repository= new UserGeneratorRepositoryImpl();
	private RewardsService rewardsService = new RewardsService(tourGuideModule.getGpsProxyTest(),
			tourGuideModule.getRewardProxyTest());
	private TourGuideService tourGuideService = new TourGuideService(tourGuideModule.getGpsProxyTest(),
																					rewardsService,tourGuideModule.getTripPricerProxyTest(), repository);


	@BeforeAll
	static void setUp() throws Exception {
		Locale.setDefault(Locale.US);
	}

	@Test
	public void highVolumeTrackLocation() {
		//Given
		// Users should be incremented up to 100,000, and test finishes within 15 minutes
		tourGuideService.initUsers(100000);

		//When
		StopWatch stopWatch = new StopWatch();

		stopWatch.start();
		TrackUser trackUser=new TrackUser();
		trackUser.trackUser(tourGuideService);
		stopWatch.stop();

		//Then
		System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds."); 
		assertTrue(TimeUnit.MINUTES.toSeconds(5) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

	@Test
	public void highVolumeGetRewards() {
		StopWatch stopWatch = new StopWatch();
		//Given
		// Users should be incremented up to 100,000, and test finishes within 20 minutes
		tourGuideService.initUsers(100000);

		Attraction attraction= tourGuideModule.getGpsProxyTest().getAttractions().get(0);
		List<User> allUsers= tourGuideService.getAllUsers();

		allUsers.forEach(u -> {
			u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date()));
		});

		//When
		stopWatch.start();

		tourGuideService.getAllUserRewardCalculate(tourGuideService);
	    
		for(User user : allUsers) {
			assertTrue(user.getUserRewards().size() > 0);
		}

		stopWatch.stop();

		//Then
		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds."); 
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}
}
