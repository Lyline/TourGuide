package tourGuide;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rewardCentral.RewardCentral;
import tourGuide.proxy.gpsProxy.GpsProxy;
import tourGuide.repository.UserGeneratorRepositoryImpl;
import tourGuide.repository.UserRepository;
import tourGuide.service.RewardsService;
import tripPricer.TripPricer;

import java.util.Locale;

@Configuration
public class TourGuideModule {
	
	@Bean
	public RewardsService getRewardsService() {
		return new RewardsService(getGpsProxy(), getRewardCentral());
	}
	
	@Bean
	public RewardCentral getRewardCentral() {
		return new RewardCentral();
	}

	@Bean
	public UserRepository getUserRepository() {
		return new UserGeneratorRepositoryImpl();
	}

	@Bean
	public TripPricer getTripPricer(){return new TripPricer();}

	@Bean
	public GpsProxy getGpsProxy(){
		Locale.setDefault(Locale.US);
		return new GpsProxy();
	}
}
