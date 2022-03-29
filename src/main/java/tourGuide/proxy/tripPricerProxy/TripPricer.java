package tourGuide.proxy.tripPricerProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class TripPricer {

  private tripPricer.TripPricer tripPricer= new tripPricer.TripPricer();

  public TripPricer() {
  }

  public List<Provider> getPrice(String apiKey, UUID attractionId, int adults,
                                 int children, int nightsStay, int rewardsPoints){
    List<Provider>providersProxy= new ArrayList<>();

    List<tripPricer.Provider> providers= tripPricer.getPrice(apiKey, attractionId,adults, children,
                                                              nightsStay, rewardsPoints);
    for(tripPricer.Provider p:providers){
      Provider provider= new Provider();
      provider.setName(p.name);
      provider.setPrice(p.price);
      provider.setTripId(p.tripId);

      providersProxy.add(provider);
    }
    return providersProxy;
  }

  public String getProviderName(String apiKey, int adults){
    return tripPricer.getProviderName(apiKey,adults);
  }

}
