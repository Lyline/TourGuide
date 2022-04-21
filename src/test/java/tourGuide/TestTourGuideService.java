package tourGuide;

import org.junit.jupiter.api.Test;
import tourGuide.model.User;
import tourGuide.proxies.gpsProxy.beans.VisitedLocation;
import tourGuide.proxies.tripPricerProxy.beans.Provider;
import tourGuide.repository.UserGeneratorRepositoryImpl;
import tourGuide.repository.UserRepository;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.dto.AttractionDto;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTourGuideService {
	TourGuideModule tourGuideModule=new TourGuideModule();
	UserRepository repository= new UserGeneratorRepositoryImpl();

	RewardsService rewardsService = tourGuideModule.getRewardsServiceTest();
	TourGuideService tourGuideService = new TourGuideService(tourGuideModule.getGpsProxyTest(), rewardsService,
																						tourGuideModule.getTripPricerProxyTest(), repository);

	User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
	User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

	@Test
	public void getUserLocation() {
		//When
		tourGuideService.initTracker();
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		tourGuideService.tracker.stopTracking();

		//Then
		assertThat(visitedLocation.userId).isEqualTo(user.getUserId());
	}

	@Test
	public void trackUser() {
		//When
		tourGuideService.initTracker();
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		tourGuideService.tracker.stopTracking();

		//Then
		assertEquals(user.getUserId(), visitedLocation.userId);
	}

	@Test
	public void addUser() {
		//When
		tourGuideService.addUser(user);

		//Then
		User retrievedUser = tourGuideService.getUser(user.getUserName());
		assertEquals(user, retrievedUser);
	}
	
	@Test
	public void getAllUsers() {
		//Given
		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);

		//When
		List<User> allUsers = tourGuideService.getAllUsers();

		//Then
		assertThat(allUsers.size()).isEqualTo(2);
		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}

	@Test
	public void getNearbyAttractions() {
		//When
		tourGuideService.initTracker();
		tourGuideService.trackUserLocation(user);

		List<AttractionDto> attractions = tourGuideService.getNearByAttractions(user);

		tourGuideService.tracker.stopTracking();

		//Then
		assertEquals(5, attractions.size());
	}

	@Test
	public void getTripDeals() {
		//When
		List<Provider> providers = tourGuideService.getTripDeals(user,new UUID(1,1));

		//Then
		assertEquals(5, providers.size());
	}
}
