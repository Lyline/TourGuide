package tourGuide.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tourGuide.proxies.gpsProxy.GpsProxy;
import tourGuide.proxies.gpsProxy.beans.Attraction;
import tourGuide.proxies.gpsProxy.beans.Location;
import tourGuide.proxies.gpsProxy.beans.VisitedLocation;
import tourGuide.proxies.tripPricerProxy.TripPricerProxy;
import tourGuide.proxies.tripPricerProxy.beans.Provider;
import tourGuide.repository.UserRepository;
import tourGuide.service.dto.AttractionDto;
import tourGuide.tracker.Tracker;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static tourGuide.repository.UserGeneratorRepositoryImpl.tripPricerApiKey;

@Service
public class TourGuideService {
	private UserRepository repository;
	private final GpsProxy gpsProxy;
	private final RewardsService rewardsService;
	private final TripPricerProxy tripPricerProxy;

	public Tracker tracker;

	private Logger logger= LoggerFactory.getLogger(TourGuideService.class);

	boolean testMode = true;
	
	public TourGuideService(GpsProxy gpsProxy, RewardsService rewardsService, TripPricerProxy tripPricerProxy,
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

	public List<Provider> getTripDeals(User user,UUID attractionId) {
		int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();

		List<Provider> providers = tripPricerProxy.getPrice(tripPricerApiKey, attractionId,
				user.getUserPreferences().getNumberOfAdults(),
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);

		user.setTripDeals(providers);

		return providers;
	}

	public List<Provider> getTripCustomPricer(UUID attractionId,String userName, int adultsNumber, int childrenNumber,
																						int nightStay){
		User user=getUser(userName);
		int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();

		List<Provider> providers = tripPricerProxy.getPrice(tripPricerApiKey, attractionId, adultsNumber,
				childrenNumber, nightStay, cumulativeRewardPoints);

		user.setTripDeals(providers);

		return providers;
	}

	public List<AttractionDto> getNearByAttractions(User user) {
		List<AttractionDto> sortedAttractions= new ArrayList<>();
		List<Attraction> attractions= gpsProxy.getAttractions();

		for(Attraction attract: attractions){
			Location attractionLocation= new Location(attract.latitude,attract.longitude);
			Location userLocation= new Location(user.getLastVisitedLocation().getLocation().latitude,
					user.getLastVisitedLocation().getLocation().longitude);

			double distance= rewardsService.getDistance(attractionLocation,userLocation);
			int rewardPoint= rewardsService.getRewardPoints(attract,user);

			sortedAttractions.add(new AttractionDto(attract.attractionName,attractionLocation,userLocation,distance, rewardPoint));
		}

			List<AttractionDto> selectedAttractions=sortedAttractions.stream()
					.sorted(Comparator.comparingDouble(AttractionDto::getDistance))
					.collect(Collectors.toList());

		if (selectedAttractions.size()>=5){
			return selectedAttractions.subList(0,5);
		}else return selectedAttractions;
	}

	public HashMap<UUID,Location> getAllCurrentLocations(){
		List<User> users= repository.getAllUser();
		HashMap<UUID,Location> usersMap= new HashMap<>();

		for(User user:users){
			usersMap.put(user.getUserId(), user.getLastVisitedLocation().getLocation());
		}

		return usersMap;
	}

	public void getAllUserRewardCalculate(TourGuideService tourGuideService){
		List<User> users= tourGuideService.getAllUsers();
		ExecutorService executorService= Executors.newFixedThreadPool(200);

		users.forEach(user ->
			executorService.submit(new Thread(()->rewardsService.calculateRewards(user)))
		);

		executorService.shutdown();

		boolean result= false;
		try{
			result=executorService.awaitTermination(20, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
