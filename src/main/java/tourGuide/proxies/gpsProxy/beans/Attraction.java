package tourGuide.proxies.gpsProxy.beans;

import java.util.UUID;

public class Attraction extends Location{
  public String attractionName;
  public String city;
  public String state;
  public UUID attractionId;

  public Attraction(String attractionName, String city, String state, double latitude, double longitude) {
    super(latitude, longitude);
    this.attractionName = attractionName;
    this.city = city;
    this.state = state;
    this.attractionId = UUID.randomUUID();
  }

  public String getAttractionName() {
    return attractionName;
  }

  public String getCity() {
    return city;
  }

  public String getState() {
    return state;
  }

  public UUID getAttractionId() {
    return attractionId;
  }

  public void setAttractionName(String attractionName) {
    this.attractionName = attractionName;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setState(String state) {
    this.state = state;
  }

  public void setAttractionId(UUID attractionId) {
    this.attractionId = attractionId;
  }
}
