package tourGuide.service.dto;

import tourGuide.proxies.gpsProxy.beans.Location;

public class AttractionDto {
  private final String attractionName;
  private final Location attractionLocation;
  private final Location userLocation;
  private final double distance;
  private final int rewardPoint;

  public AttractionDto(String attractionName, Location attractionLocation, Location userLocation, double distance, int rewardPoint) {
    this.attractionName = attractionName;
    this.attractionLocation = attractionLocation;
    this.userLocation = userLocation;
    this.distance = distance;
    this.rewardPoint = rewardPoint;
  }

  public String getAttractionName() {
    return attractionName;
  }

  public Location getAttractionLocation() {
    return attractionLocation;
  }

  public Location getUserLocation() {
    return userLocation;
  }

  public double getDistance() {
    return distance;
  }

  public int getRewardPoint() {
    return rewardPoint;
  }
}
