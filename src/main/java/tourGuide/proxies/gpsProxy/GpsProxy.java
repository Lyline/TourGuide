package tourGuide.proxies.gpsProxy;

import feign.Param;
import feign.RequestLine;
import tourGuide.proxies.gpsProxy.beans.Attraction;
import tourGuide.proxies.gpsProxy.beans.VisitedLocation;

import java.util.List;
import java.util.UUID;

public interface GpsProxy {

  @RequestLine("GET /userLocation/{userId}")
  VisitedLocation getUserLocation(@Param("userId") UUID userId);

  @RequestLine("GET /attractions")
  List<Attraction> getAttractions();
}
