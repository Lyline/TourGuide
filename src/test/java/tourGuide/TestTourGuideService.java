package tourGuide;

import gpsUtil.location.VisitedLocation;
import org.junit.Before;
import org.junit.Test;
import tourGuide.repository.UserGeneratorRepositoryImpl;
import tourGuide.repository.UserRepository;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.dto.AttractionDto;
import tourGuide.user.User;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestTourGuideService {

	TourGuideModule tourGuideModule=new TourGuideModule();

	UserRepository repository= new UserGeneratorRepositoryImpl();
	TripPricer tripPricer= new TripPricer();

	RewardsService rewardsService = tourGuideModule.getRewardsService();
	TourGuideService tourGuideService = new TourGuideService(tourGuideModule.getGpsUtil(), rewardsService,tripPricer,repository);

	User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
	User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

	@Before
	public void setUp() throws Exception {
		tourGuideService.initUsers(0);
	}

	@Test
	public void getUserLocation() {

		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		tourGuideService.tracker.stopTracking();
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}
	
	@Test
	public void addUser() {
		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);
		
		User retrivedUser = tourGuideService.getUser(user.getUserName());
		User retrivedUser2 = tourGuideService.getUser(user2.getUserName());

		tourGuideService.tracker.stopTracking();
		
		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}
	
	@Test
	public void getAllUsers() {
		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);
		
		List<User> allUsers = tourGuideService.getAllUsers();

		tourGuideService.tracker.stopTracking();
		
		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}
	
	@Test
	public void trackUser() {
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(user.getUserId(), visitedLocation.userId);
	}

	@Test
	public void getNearbyAttractions() {
		tourGuideService.trackUserLocation(user);

		List<AttractionDto> attractions = tourGuideService.getNearByAttractions(user);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(5, attractions.size());
	}

	@Test
	public void getTripDeals() {
				List<Provider> providers = tourGuideService.getTripDeals(user);
		
		tourGuideService.tracker.stopTracking();
		
		assertEquals(10, providers.size());
	}
	
	
}
