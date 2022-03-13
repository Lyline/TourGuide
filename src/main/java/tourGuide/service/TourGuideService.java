package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.repository.UserGeneratorRepositoryImpl;
import tourGuide.repository.UserRepository;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.ArrayList;
import java.util.List;

import static tourGuide.repository.UserGeneratorRepositoryImpl.tripPricerApiKey;

@Service
public class TourGuideService {
	private UserRepository repository;

	private final GpsUtil gpsUtil;
	private final RewardsService rewardsService;
	private final TripPricer tripPricer;
	//private final TripPricer tripPricer = new TripPricer();

	public /*final*/ Tracker tracker =new Tracker(this);

	private Logger logger= LoggerFactory.getLogger(TourGuideService.class);

	boolean testMode = true;
	
	public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService, TripPricer tripPricer, UserRepository repository) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;
		this.tripPricer= tripPricer;
		this.repository= repository;


		addShutDownHook();
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
		VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		return visitedLocation;
	}

	public VisitedLocation getUserLocation(User user) {
		VisitedLocation userLocation;

		if(user.getVisitedLocations().size()>0){
			userLocation= user.getLastVisitedLocation();
		}else {
			userLocation= gpsUtil.getUserLocation(user.getUserId());
		}
		return userLocation;
	}

	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

	public List<Provider> getTripDeals(User user) {
		int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();

		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);

		user.setTripDeals(providers);

		return providers;
	}

	public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
		List<Attraction> nearbyAttractions = new ArrayList<>();
		for(Attraction attraction : gpsUtil.getAttractions()) {
			if(rewardsService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
				nearbyAttractions.add(attraction);
			}
		}
		
		return nearbyAttractions;
	}

	public void initTracker(){
		tracker=new Tracker(this);
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
