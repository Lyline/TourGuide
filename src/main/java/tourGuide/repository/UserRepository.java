package tourGuide.repository;

import org.springframework.stereotype.Repository;
import tourGuide.model.User;

import java.util.List;

@Repository
public interface UserRepository {
  User getUser(String username);
  List<User> getAllUser();
  User saveUser(User user);
  void initializeInternalUsers(int nbUser);
}
