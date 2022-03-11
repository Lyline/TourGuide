package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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

	public /*final*/ Tracker tracker =new Tracker(this);

	private Logger logger= LoggerFactory.getLogger(TourGuideService.class);

	//boolean testMode = true;
	
	public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService, UserRepository repository) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;
		this.repository= repository;

		/*if(testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		}*/
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

	public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
		List<Attraction> nearbyAttractions = new ArrayList<>();
		for(Attraction attraction : gpsUtil.getAttractions()) {
			if(rewardsService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
				nearbyAttractions.add(attraction);
			}
		}
		
		return nearbyAttractions;
	}
	
	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() { 
		      public void run() {
		        tracker.stopTracking();
		      } 
		    }); 
	}
}
