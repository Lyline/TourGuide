package tourGuide.unitTest;

import org.junit.Test;
import tourGuide.proxy.gpsProxy.GpsProxy;
import tourGuide.proxy.gpsProxy.location.Attraction;
import tourGuide.proxy.gpsProxy.location.Location;
import tourGuide.proxy.gpsProxy.location.VisitedLocation;
import tourGuide.repository.UserGeneratorRepositoryImpl;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.dto.AttractionDto;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TourGuideServiceUnitTest {

  private GpsProxy mockGps= mock(GpsProxy.class);
  private RewardsService mockRewards= mock(RewardsService.class);
  private TripPricer mockPricer= mock(TripPricer.class);

  private UserGeneratorRepositoryImpl repository= mock(UserGeneratorRepositoryImpl.class);

  private TourGuideService classUnderTest=new TourGuideService(mockGps,mockRewards,mockPricer,repository);

  User user = new User(UUID.randomUUID(), "Jean", "000", "jon@tourGuide.com");
  User user2 = new User(UUID.randomUUID(), "Astrid", "000", "jon2@tourGuide.com");

 VisitedLocation locationParis =
      new VisitedLocation(
          user.getUserId(),
          new Location(48.856614,2.3522219),
          new Date());

  VisitedLocation locationRennes =
      new VisitedLocation(
          user.getUserId(),
          new Location(	48.117266 ,-1.6777926),
          new Date());

  Attraction attraction=
      new Attraction("Eurodisney","Marne la Vallée","Seine et Marne",
          48.871900,2.776623);
  Attraction attraction1=
      new Attraction("Futuroscope","Poitiers","Vendée",
          46.580224,0.340375);
  Attraction attraction2=
      new Attraction("Musée Jules Verne","Nantes","Loire Atlantique",
          47.201616, -1.577347);
  Attraction attraction3=
      new Attraction("Musée Grévin","Paris","Paris",
          48.8718378, 2.3422204);
  Attraction attraction4=
      new Attraction("Stade de France","Saint Denis","Seine Saint Denis",
          48.921329648, 2.355998576);
  Attraction attraction5=
      new Attraction("Le Moulin Rouge","Paris","Paris",
          48.883829798, 2.325998696);
  Attraction attraction6=
      new Attraction("Vulcania","Saint Ours","Puy de Dôme",
          45.813797, 2.942556);


  Provider provider= new Provider(new UUID(1,1),"Travel Agency",120.);
  Provider provider1= new Provider(new UUID(1,1),"Dream Travel",115.);

  @Test
  public void givenAUserWhenGetUserLocationThenGetUserLocation() {
    //Given
    when(mockGps.getUserLocation(any())).thenReturn(locationParis);

    //When
    VisitedLocation actual=classUnderTest.getUserLocation(user);

    //Then
    assertSame(actual,user.getLastVisitedLocation());
    verify(mockGps,times(1)).getUserLocation(user);
  }

  @Test
  public void givenAUserWhenGetUserThenUserIsFound() {
    //Given
    when(repository.getUser(any())).thenReturn(user);

    //When
    User actual= classUnderTest.getUser("Jean");
    //Then
    assertSame(user,actual);
  }

  @Test
  public void givenTwoUsersWhenGetAllUserSThenReturnListOfUsers() {
    //Given
    when(repository.getAllUser()).thenReturn(Arrays.asList(user,user2));

    //When
    List<User> actual= classUnderTest.getAllUsers();

    //Then
    assertThat(actual.size()).isEqualTo(2);
    assertTrue(actual.contains(user));
    assertTrue(actual.contains(user2));
  }

  @Test
  public void givenAUserWhenAddUserThenUserAdded() {
    //Given
    //When
    classUnderTest.addUser(user);
    //Then
    assertThat(classUnderTest.getAllUsers().size()).isEqualTo(1);
  }

  @Test
  public void givenAUserWhenTrackUserLocationThenUserLocationFound() {
    //Given
    when(mockGps.getUserLocation(any())).thenReturn(locationParis);

    //When
    VisitedLocation actual=classUnderTest.trackUserLocation(user);

    //Then
    assertSame(actual,user.getLastVisitedLocation());
    verify(mockGps,times(1)).getUserLocation(user);
  }

  @Test
  public void givenAUserWithoutVisitedLocationWhenGetUserLocationThenTrackUserLocation(){
    //Given
    when(mockGps.getUserLocation(any())).thenReturn(locationParis);

    //When
    VisitedLocation actual= classUnderTest.getUserLocation(user);

    //Then
    assertSame(locationParis,actual);
  }

  @Test
  public void givenAUserWithVisitedLocationWhenGetUserLocationThenTheLastVisitedUserLocation() {
    //Given
    user.addToVisitedLocations(locationParis);

    //When
    VisitedLocation actual= classUnderTest.getUserLocation(user);

    //Then
    assertSame(locationParis,actual);
  }

  @Test
  public void givenAUserWithoutRewardWhenGetUserRewardsThenReturnAnEmptyList() {
    //Given
    //When
    List<UserReward> actual= classUnderTest.getUserRewards(user);

    //Then
    assertTrue(actual.isEmpty());
  }

  @Test
  public void givenAUserWithTwoRewardsWhenGetUserRewardsThenAListOfUserReward() {
    //Given
    user.addUserReward(new UserReward(locationRennes,attraction1));
    user.addUserReward(new UserReward(locationParis,attraction));

    //When
    List<UserReward> actual= classUnderTest.getUserRewards(user);

    //Then
    assertThat(actual.size()).isEqualTo(2);
  }

  @Test
  public void givenAUserWithSameRewardsWhenGetUserRewardsThenAListOfUserRewardWithoutDuplicateReard() {
    //Given
    user.addUserReward(new UserReward(locationParis,attraction));
    user.addUserReward(new UserReward(locationParis,attraction));

    //When
    List<UserReward> actual= classUnderTest.getUserRewards(user);

    //Then
    assertThat(actual.size()).isEqualTo(1);
  }

  @Test
  public void givenAUserWithRewardPointsWhenGetTripDealsThenReturnListOfTwoProviders() {
    //Given
    user.addUserReward(new UserReward(locationParis,attraction,30));

    when(mockPricer.getPrice(anyString(),any(),anyInt(),anyInt(),anyInt(),anyInt()))
        .thenReturn(Arrays.asList(provider,provider1));

    //When
    List<Provider>actual= classUnderTest.getTripDeals(user);

    //Then
    assertThat(actual.size()).isEqualTo(2);
  }

  @Test
  public void givenAUserAtParisWhenGetNearByAttractionsThenReturnListOfFiveNearlyAttractions() {
    //Given
    user.getVisitedLocations().add(locationParis);

    when(mockGps.getAttractions()).thenReturn(Arrays.asList(attraction,attraction1,attraction2,attraction3,
        attraction4,attraction5,attraction6));


    //When
    List<AttractionDto>actual= classUnderTest.getNearByAttractions(user);

    //Then
    assertThat(actual.size()).isEqualTo(5);
  }
}
