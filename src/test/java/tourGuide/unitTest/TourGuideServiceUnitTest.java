package tourGuide.unitTest;

import gpsUtil.GpsUtil;
import org.junit.Test;
import tourGuide.repository.UserGeneratorRepositoryImpl;
import tourGuide.repository.UserRepository;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
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

  private GpsUtil mockGps= mock(GpsUtil.class);
  private RewardsService mockRewards= mock(RewardsService.class);
  private TripPricer mockPricer= mock(TripPricer.class);

  private UserGeneratorRepositoryImpl repository= mock(UserGeneratorRepositoryImpl.class);

  private TourGuideService classUnderTest=new TourGuideService(mockGps,mockRewards,mockPricer,repository);

  User user = new User(UUID.randomUUID(), "Jean", "000", "jon@tourGuide.com");
  User user2 = new User(UUID.randomUUID(), "Astrid", "000", "jon2@tourGuide.com");

  gpsUtil.location.VisitedLocation userPosition=
      new gpsUtil.location.VisitedLocation(
          user.getUserId(),
          new gpsUtil.location.Location(48.,2.),
          new Date());

  gpsUtil.location.VisitedLocation userPosition1=
      new gpsUtil.location.VisitedLocation(
          user.getUserId(),
          new gpsUtil.location.Location(50.,2.),
          new Date());

  gpsUtil.location.Attraction attraction=
      new gpsUtil.location.Attraction("Eurodisney","Marne la Vallée","Seine et Marne",
          48.871900,2.776623);
  gpsUtil.location.Attraction attraction1=
      new gpsUtil.location.Attraction("Futuroscope","Poitiers","Vendée",
          46.580224,0.340375);

  Provider provider= new Provider(new UUID(1,1),"Travel Agency",120.);
  Provider provider1= new Provider(new UUID(1,1),"Dream Travel",115.);

  @Test
  public void givenAUserWhenGetUserLocationThenGetUserLocation() {
    //Given
    when(mockGps.getUserLocation(any())).thenReturn(userPosition);

    //When
    gpsUtil.location.VisitedLocation actual=classUnderTest.getUserLocation(user);

    //Then
    assertSame(actual,user.getLastVisitedLocation());
    verify(mockGps,times(1)).getUserLocation(user.getUserId());
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
    when(mockGps.getUserLocation(any())).thenReturn(userPosition);

    //When
    gpsUtil.location.VisitedLocation actual=classUnderTest.trackUserLocation(user);

    //Then
    assertSame(actual,user.getLastVisitedLocation());
    verify(mockGps,times(1)).getUserLocation(user.getUserId());
  }

  @Test
  public void givenAUserWithoutVisitedLocationWhenGetUserLocationThenTrackUserLocation(){
    //Given
    when(mockGps.getUserLocation(any())).thenReturn(userPosition);

    //When
    gpsUtil.location.VisitedLocation actual= classUnderTest.getUserLocation(user);

    //Then
    assertSame(userPosition,actual);
  }

  @Test
  public void givenAUserWithVisitedLocationWhenGetUserLocationThenTheLastVisitedUserLocation() {
    //Given
    user.addToVisitedLocations(userPosition);

    //When
    gpsUtil.location.VisitedLocation actual= classUnderTest.getUserLocation(user);

    //Then
    assertSame(userPosition,actual);
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
    user.addUserReward(new UserReward(userPosition1,attraction1));
    user.addUserReward(new UserReward(userPosition,attraction));

    //When
    List<UserReward> actual= classUnderTest.getUserRewards(user);

    //Then
    assertThat(actual.size()).isEqualTo(2);
  }

  @Test
  public void givenAUserWithSameRewardsWhenGetUserRewardsThenAListOfUserRewardWithoutDuplicateReard() {
    //Given
    user.addUserReward(new UserReward(userPosition,attraction));
    user.addUserReward(new UserReward(userPosition,attraction));

    //When
    List<UserReward> actual= classUnderTest.getUserRewards(user);

    //Then
    assertThat(actual.size()).isEqualTo(1);
  }

  @Test
  public void givenAUserWithRewardPointsWhenGetTripDealsThenReturnListOfTwoProviders() {
    //Given
    user.addUserReward(new UserReward(userPosition,attraction,30));

    when(mockPricer.getPrice(anyString(),any(),anyInt(),anyInt(),anyInt(),anyInt()))
        .thenReturn(Arrays.asList(provider,provider1));

    //When
    List<Provider>actual= classUnderTest.getTripDeals(user);

    //Then
    assertThat(actual.size()).isEqualTo(2);
  }
}
