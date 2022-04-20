package tourGuide;

import com.jsoniter.output.JsonStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.model.User;
import tourGuide.proxies.gpsProxy.GpsProxy;
import tourGuide.proxies.gpsProxy.beans.Location;
import tourGuide.proxies.gpsProxy.beans.VisitedLocation;
import tourGuide.proxies.rewardCentralProxy.RewardProxy;
import tourGuide.proxies.tripPricerProxy.beans.Provider;
import tourGuide.service.TourGuideService;
import tourGuide.service.dto.AttractionDto;

import java.util.HashMap;
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

    @RequestMapping("/userNearbyAttractions")
    public List<AttractionDto> getNearbyAttractions(@RequestParam String userName) {
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
    public HashMap<UUID, Location> getAllCurrentLocations() {
      HashMap<UUID, Location> usersMap= tourGuideService.getAllCurrentLocations();
    	return usersMap;
    }
    
    @RequestMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName, @RequestParam UUID attractionId) {
    	List<Provider> providers = tourGuideService.getTripDeals(getUser(userName),attractionId);
    	return providers;
    }

    @RequestMapping("/getTripCustomDeals")
    public List<Provider> getTripCustomPricer(@RequestParam UUID attractionId,
                                              @RequestParam String userName, @RequestParam int adults,
                                              @RequestParam int children, @RequestParam int nightsStay){
      return tourGuideService.getTripCustomDeals(attractionId,userName,adults,children,nightsStay);
    }

    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }

}