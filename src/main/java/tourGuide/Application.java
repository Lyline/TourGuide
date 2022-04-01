package tourGuide;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tourGuide.service.TourGuideService;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private final TourGuideService tourGuideService;

    public Application(TourGuideService tourGuideService) {
        this.tourGuideService = tourGuideService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     Callback used to run the bean.

     @param args incoming main method arguments

     @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        tourGuideService.initUsers(100);
        tourGuideService.initTracker();
    }
}
