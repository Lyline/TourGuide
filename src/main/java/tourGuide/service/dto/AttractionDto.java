package tourGuide.service.dto;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

public class AttractionDto {
  private gpsUtil.location.Attraction attraction;
  private gpsUtil.location.VisitedLocation userLocation;
  private double distance;

  public AttractionDto(Attraction attraction, gpsUtil.location.VisitedLocation userLocation, double distance) {
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
