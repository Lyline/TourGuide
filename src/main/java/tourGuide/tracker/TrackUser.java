package tourGuide.tracker;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tourGuide.model.User;
import tourGuide.service.TourGuideService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TrackUser {
  private Logger logger= LoggerFactory.getLogger(TrackUser.class);

  public void trackUser(TourGuideService tourGuideService){
    StopWatch timerWatch = new StopWatch();
    List<User> users = tourGuideService.getAllUsers();

    logger.debug("Begin Tracker. Tracking " + users.size() + " users.");
    timerWatch.start();

    // Distribuer le boulot à une thread pool
    ExecutorService executorService= Executors.newFixedThreadPool(200);

    users.forEach(u -> {
      //Donner tâches à un thread
      executorService.submit(new Thread(() -> tourGuideService.trackUserLocation(u)));
    });

    //Attendre que la tâche soit fini
    executorService.shutdown();

    boolean result=false;
    try {
      result=executorService.awaitTermination(25,TimeUnit.MINUTES);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    timerWatch.stop();

    logger.debug("Tracker Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(timerWatch.getTime()) + " seconds.");
    timerWatch.reset();
  }
}
