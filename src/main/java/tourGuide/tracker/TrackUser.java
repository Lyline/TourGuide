package tourGuide.tracker;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TrackUser {
  private Logger logger= LoggerFactory.getLogger(TrackUser.class);

  public void trackUser(TourGuideService tourGuideService){
    StopWatch timerWatch = new StopWatch();

    List<User> users = tourGuideService.getAllUsers();

    logger.debug("Begin Tracker. Tracking " + users.size() + " users.");
    timerWatch.start();
    users.forEach(u -> tourGuideService.trackUserLocation(u));
    timerWatch.stop();
    logger.debug("Tracker Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(timerWatch.getTime()) + " seconds.");
    timerWatch.reset();
  }
}
