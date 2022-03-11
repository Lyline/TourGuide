package tourGuide.unitTest.repository;

import org.junit.Test;
import tourGuide.helper.InternalTestHelper;
import tourGuide.repository.UserGeneratorRepositoryImpl;
import tourGuide.user.User;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class UserGeneratorRepositoryTest {
  private UserGeneratorRepositoryImpl classUnderTest= new UserGeneratorRepositoryImpl();


  @Test
  public void givenAUserExistWhenGetUserThenUserIsFound() {
    //Given
    InternalTestHelper.setInternalUserNumber(1);
    classUnderTest.initializeInternalUsers();

    //When
    User actual= classUnderTest.getUser("internalUser0");

    //Then
    assertNotNull(actual);
  }

  @Test
  public void givenAUserNotExistWhenGetUserThenReturnNull() {
    //Given
    InternalTestHelper.setInternalUserNumber(1);
    classUnderTest.initializeInternalUsers();

    //When
    User actual= classUnderTest.getUser("noUser");

    //Then
    assertNull(actual);
  }

  @Test
  public void givenTwoUsersWhenGetAllUsersThenReturnAListOfUsersFound() {
    //Given
    InternalTestHelper.setInternalUserNumber(2);
    classUnderTest.initializeInternalUsers();

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
    InternalTestHelper.setInternalUserNumber(0);
    classUnderTest.initializeInternalUsers();

    //When
    List<User> actual= classUnderTest.getAllUser();

    //Then
    assertTrue(actual.isEmpty());
  }

  @Test
  public void givenANewUserWhenSaveUserThenUserIsSaved() {
    //Given
    InternalTestHelper.setInternalUserNumber(0);
    classUnderTest.initializeInternalUsers();

    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

    //When
    User actual= classUnderTest.saveUser(user);

    //Then
    assertThat(classUnderTest.getAllUser().size()).isEqualTo(1);
    assertSame(actual,user);
  }
}
