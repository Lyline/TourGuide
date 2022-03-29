package tourGuide.service.dto;

import tourGuide.proxy.gpsProxy.location.Attraction;
import tourGuide.proxy.gpsProxy.location.VisitedLocation;

public class AttractionDto {
  //supprimer dependance a gpsUtil -> creer champs indépendants
  private Attraction attraction;
  private VisitedLocation userLocation;
  private double distance;

  public AttractionDto(Attraction attraction, VisitedLocation userLocation, double distance) {
    this.attraction = attraction;
    this.userLocation = userLocation;
    this.distance = distance;
  }

  public Attraction getAttraction() {
    return attraction;
  }

  public void setAttraction(Attraction attraction) {
    this.attraction = attraction;
  }

  public VisitedLocation getUserLocation() {
    return userLocation;
  }

  public void setUserLocation(VisitedLocation userLocation) {
    this.userLocation = userLocation;
  }

  public double getDistance() {
    return distance;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }
}
