package tourGuide.unitTest.repository;

import org.junit.jupiter.api.Test;
import tourGuide.repository.UserGeneratorRepositoryImpl;
import tourGuide.user.User;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class UserGeneratorRepositoryTest {
  private UserGeneratorRepositoryImpl classUnderTest= new UserGeneratorRepositoryImpl();


  @Test
  public void givenAUserExistWhenGetUserThenUserIsFound() {
    //Given
    classUnderTest.initializeInternalUsers(1);

    //When
    User actual= classUnderTest.getUser("internalUser0");

    //Then
    assertNotNull(actual);
  }

  @Test
  public void givenAUserNotExistWhenGetUserThenReturnNull() {
    //Given
    classUnderTest.initializeInternalUsers(1);

    //When
    User actual= classUnderTest.getUser("noUser");

    //Then
    assertNull(actual);
  }

  @Test
  public void givenTwoUsersWhenGetAllUsersThenReturnAListOfUsersFound() {
    //Given
    classUnderTest.initializeInternalUsers(2);

    //When
    List<User> actual= classUnderTest.getAllUser();

    //Then
    assertThat(actual.size()).isEqualTo(2);
    assertThat(actual.get(0).getUserName()).isEqualTo("internalUser1");
    assertThat(actual.get(1).getUserName()).isEqualTo("internalUser0");
  }

  @Test
  public void givenNoUsersWhenGetAllUsersThenReturnAnEmptyList() {
    //Given
    classUnderTest.initializeInternalUsers(0);

    //When
    List<User> actual= classUnderTest.getAllUser();

    //Then
    assertTrue(actual.isEmpty());
  }

  @Test
  public void givenANewUserWhenSaveUserThenUserIsSaved() {
    //Given
    classUnderTest.initializeInternalUsers(0);

    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

    //When
    User actual= classUnderTest.saveUser(user);

    //Then
    assertThat(classUnderTest.getAllUser().size()).isEqualTo(1);
    assertSame(actual,user);
  }
}
