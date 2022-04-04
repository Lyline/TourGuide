package tourGuide.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.proxies.gpsProxy.GpsProxy;
import tourGuide.proxies.gpsProxy.beans.Attraction;
import tourGuide.proxies.gpsProxy.beans.Location;
import tourGuide.proxies.gpsProxy.beans.VisitedLocation;
import tourGuide.proxies.tripPricerProxy.Provider;
import tourGuide.proxies.tripPricerProxy.TripPricer;
import tourGuide.repository.UserRepository;
import tourGuide.service.dto.AttractionDto;
import tourGuide.service.user.User;
import tourGuide.service.user.UserReward;
import tourGuide.tracker.Tracker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static tourGuide.repository.UserGeneratorRepositoryImpl.tripPricerApiKey;

@Service
public class TourGuideService {
	private UserRepository repository;
	private final GpsProxy gpsProxy;
	private final RewardsService rewardsService;
	private final TripPricer tripPricerProxy;

	public Tracker tracker;

	private Logger logger= LoggerFactory.getLogger(TourGuideService.class);

	boolean testMode = true;
	
	public TourGuideService(GpsProxy gpsProxy, RewardsService rewardsService, TripPricer tripPricerProxy,
													UserRepository repository) {
		this.gpsProxy = gpsProxy;
		this.rewardsService = rewardsService;
		this.tripPricerProxy = tripPricerProxy;
		this.repository= repository;
	}
	
	public User getUser(String userName) {
		return repository.getUser(userName);
	}

	public List<User> getAllUsers() {
		return repository.getAllUser();
	}

	public void addUser(User user) {
		List<User> users=repository.getAllUser();

		User userExist=users.stream()
				.filter(u->u.getUserName().equals(user.getUserName()))
				.findAny()
				.orElse(null);

		if (userExist==null){
			repository.saveUser(user);
			logger.info("New user added");
		}else logger.info("User is already exist");
	}

	public VisitedLocation trackUserLocation(User user) {
		VisitedLocation visitedLocation = gpsProxy.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		return visitedLocation;
	}

	public VisitedLocation getUserLocation(User user) {
		VisitedLocation userLocation;

		if(user.getVisitedLocations().size()>0){
			userLocation= user.getLastVisitedLocation();
		}else {
			userLocation= gpsProxy.getUserLocation(user.getUserId());
		}
		return userLocation;
	}

	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

	public List<Provider> getTripDeals(User user) {
		int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();

		List<Provider> providers = tripPricerProxy.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);

		user.setTripDeals(providers);

		return providers;
	}

	public List<Provider> getTripCustomPricer(User user, int adultsNumber, int childrenNumber, int nightStay){
		int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();

		List<Provider> providers = tripPricerProxy.getPrice(tripPricerApiKey, user.getUserId(), adultsNumber,
				childrenNumber, nightStay, cumulativeRewardPoints);

		user.setTripDeals(providers);

		return providers;
	}

	public List<AttractionDto> getNearByAttractions(User user) {
		List<AttractionDto> sortedAttractions= new ArrayList<>();
		List<Attraction> attractions= gpsProxy.getAttractions();

		for(Attraction attract: attractions){
			double distance= rewardsService.getDistance(
					new Location(attract.latitude,attract.longitude),
					new Location(user.getLastVisitedLocation().location.latitude,
							user.getLastVisitedLocation().location.longitude));

			sortedAttractions.add(new AttractionDto(attract,user.getLastVisitedLocation(),distance));
		}

			List<AttractionDto> selectedAttractions=sortedAttractions.stream()
					.sorted(Comparator.comparingDouble(AttractionDto::getDistance))
					.collect(Collectors.toList());

		if (selectedAttractions.size()>=5){
			return selectedAttractions.subList(0,5);
		}else return selectedAttractions;
	}

	public void initTracker(){
		tracker=new Tracker(this);
		addShutDownHook();
	}

	public void initUsers(int nbUsers){
		if(testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			repository.initializeInternalUsers(nbUsers);
			logger.debug("Finished initializing users");
		}
	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() { 
		      public void run() {
		        tracker.stopTracking();
		      } 
		    }); 
	}
}
