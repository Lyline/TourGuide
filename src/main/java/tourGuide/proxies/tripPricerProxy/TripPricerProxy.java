package tourGuide.proxies.tripPricerProxy;

import feign.Param;
import feign.RequestLine;
import tourGuide.proxies.tripPricerProxy.beans.Provider;

import java.util.List;
import java.util.UUID;

public interface TripPricerProxy {

  @RequestLine("GET /tripPricer/{apiKey}/{attractionId}/{adults}/{children}/{nightsStay}/{rewardsPoints}")
  List<Provider> getPrice(@Param("apiKey") String apiKey, @Param("attractionId") UUID attractionId,
                          @Param("adults") int adults, @Param("children") int children,
                          @Param("nightsStay") int nightsStay, @Param("rewardsPoints") int rewardsPoints);

}
