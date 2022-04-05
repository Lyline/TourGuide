package tourGuide.proxies.tripPricerProxy;

import tourGuide.proxies.tripPricerProxy.beans.Provider;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

public class TripPricerTask implements Callable<List<Provider>> {
  private TripPricerProxy tripPricerProxy;
  private final UUID attractionId;
  private final String apiKey;
  private final int adults;
  private final int children;
  private final int nightsStay;

  public TripPricerTask(String apiKey, UUID attractionId, int adults, int children, int nightsStay) {
    this.apiKey = apiKey;
    this.attractionId = attractionId;
    this.adults = adults;
    this.children = children;
    this.nightsStay = nightsStay;
  }

  public List<Provider> call() throws Exception {
    return tripPricerProxy.getPrice(this.apiKey, this.attractionId, this.adults, this.children, this.nightsStay, 5);
  }
}
