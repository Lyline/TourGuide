package tourGuide.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tourGuide.helper.InternalTestHelper;
import tourGuide.user.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UserGeneratorRepositoryImpl implements UserRepository {

  public static final String tripPricerApiKey = "test-server-api-key";

  // Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
  private final Map<String, User> internalUserMap = new HashMap<>();
  private Logger logger= LoggerFactory.getLogger(UserGeneratorRepositoryImpl.class);

  @Override
  public User getUser(String username) {
    if(internalUserMap.containsKey(username)){
      return internalUserMap.get(username);
    }else{
      logger.info("Username : "+ username +" is not found");
      return null;
    }
  }

  @Override
  public List<User> getAllUser() {
    return internalUserMap.values().stream().collect(Collectors.toList());
  }

  @Override
  public User saveUser(User user) {
    internalUserMap.put(user.getUserName(),user);
    return internalUserMap.get(user.getUserName());
  }

  /**********************************************************************************
   *
   * Methods Below: For Internal Testing
   *
   **********************************************************************************/

  public void initializeInternalUsers() {
    IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
      String userName = "internalUser" + i;
      String phone = "000";
      String email = userName + "@tourGuide.com";
      User user = new User(UUID.randomUUID(), userName, phone, email);
      generateUserLocationHistory(user);

      internalUserMap.put(userName, user);
    });
    logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
  }

  private void generateUserLocationHistory(User user) {
    IntStream.range(0, 3).forEach(i-> {
      user.addToVisitedLocations(new gpsUtil.location.VisitedLocation(user.getUserId(), new gpsUtil.location.Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
    });
  }

  private double generateRandomLongitude() {
    double leftLimit = -180;
    double rightLimit = 180;
    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
  }

  private double generateRandomLatitude() {
    double leftLimit = -85.05112878;
    double rightLimit = 85.05112878;
    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
  }

  private Date getRandomTime() {
    LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
  }

}
