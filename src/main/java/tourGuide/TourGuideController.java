package tourGuide;

import com.jsoniter.output.JsonStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.proxies.gpsProxy.GpsProxy;
import tourGuide.proxies.gpsProxy.beans.Location;
import tourGuide.proxies.gpsProxy.beans.VisitedLocation;
import tourGuide.proxies.rewardCentralProxy.RewardProxy;
import tourGuide.proxies.tripPricerProxy.Provider;
import tourGuide.service.TourGuideService;
import tourGuide.service.dto.AttractionDto;
import tourGuide.service.user.User;

import java.util.List;
import java.util.UUID;

@RestController
public class TourGuideController {

	@Autowired
	TourGuideService tourGuideService;

  private final RewardProxy rewardProxy;
  private final GpsProxy gpsProxy;

  public TourGuideController(RewardProxy rewardProxy, GpsProxy gpsProxy) {
    this.rewardProxy = rewardProxy;
    this.gpsProxy = gpsProxy;
  }


  @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/userLocation")
    public Location getLocation(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
		  return visitedLocation.getLocation();
    }
    
    //  TODO: Change this method to no longer return a List of Attractions.
 	  //  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
 	  //  Return a new JSON object that contains:
    	// => Name of Tourist attraction,
      // =>Tourist attractions lat/long,
      // =>The user's location lat/long,
      // =>The distance in miles between the user's location and each of the attractions.
      // != The reward points for visiting each Attraction.
      //    Note: Attraction reward points can be gathered from RewardsCentral
    @RequestMapping("/userNearbyAttractions")
    public List<AttractionDto> getNearbyAttractions(@RequestParam String userName) {
    	tourGuideService.getUserLocation(getUser(userName));
    	return tourGuideService.getNearByAttractions(getUser(userName));
    }
    
    @RequestMapping("/getRewards") 
    public String getRewards(@RequestParam String userName) {
    	return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }

    @RequestMapping("/getRewardPoint")
    public int getRewardPoints(@RequestParam UUID attractionId,@RequestParam UUID userId){
      return rewardProxy.getAttractionRewardPoints(attractionId,userId);
    }
    
    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
    	// TODO: Get a list of every user's most recent location as JSON
    	//- Note: does not use gpsUtil to query for their current location, 
    	//        but rather gathers the user's current location from their stored location history.
    	//
    	// Return object should be the just a JSON mapping of userId to Locations similar to:
    	//     {
    	//        "019b04a9-067a-4c76-8817-ee75088c3822": {"longitude":-48.188821,"latitude":74.84371} 
    	//        ...
    	//     }
    	
    	return JsonStream.serialize("");
    }
    
    @RequestMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
    	List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
    	return providers;
    }

    @RequestMapping("/getTripCustomPricer")
    public List<Provider> getTripCustomPricer(@RequestParam String username, @RequestParam int adults,
                                        @RequestParam int children, @RequestParam int nightsStay){
      return tourGuideService.getTripCustomPricer(getUser(username),adults,children,nightsStay);
    }
    
    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }

}