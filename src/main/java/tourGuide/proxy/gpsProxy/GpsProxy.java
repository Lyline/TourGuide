package tourGuide.proxy.gpsProxy;


import org.springframework.stereotype.Service;
import tourGuide.proxy.gpsProxy.location.Attraction;
import tourGuide.proxy.gpsProxy.location.Location;
import tourGuide.proxy.gpsProxy.location.VisitedLocation;
import tourGuide.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class GpsProxy {
  gpsUtil.GpsUtil gpsUtil= new gpsUtil.GpsUtil();


  public GpsProxy() {
  }

  public VisitedLocation getUserLocation(User user){
    Locale.setDefault(Locale.US);

    VisitedLocation location= new VisitedLocation();
    gpsUtil.location.VisitedLocation userLocation=gpsUtil.getUserLocation(user.getUserId());

    location.setUserId(user.getUserId());
    location.setLocation(new Location(userLocation.location.latitude,userLocation.location.longitude));
    location.setTimeVisited(userLocation.timeVisited);

    return location;
  }

  public List<Attraction> getAttractions(){

    List<Attraction> proxyAttractions= new ArrayList<>();
    List<gpsUtil.location.Attraction>attractions= gpsUtil.getAttractions();

    for(gpsUtil.location.Attraction attraction:attractions){
      Attraction attract= new Attraction(attraction.attractionName, attraction.city, attraction.state,
                                attraction.latitude, attraction.longitude);
      proxyAttractions.add(attract);
    }

    return proxyAttractions;
  }
}
