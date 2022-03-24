package tourGuide;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tourGuide.proxy.gpsProxy.GpsProxy;
import tourGuide.proxy.rewardCentralProxy.RewardProxy;
import tourGuide.proxy.tripPricerProxy.TripPricer;
import tourGuide.repository.UserGeneratorRepositoryImpl;
import tourGuide.repository.UserRepository;
import tourGuide.service.RewardsService;

@Configuration
public class TourGuideModule {
	
	@Bean
	public RewardsService getRewardsService() {
		return new RewardsService(getGpsProxy(), getRewardProxy());
	}

	@Bean
	public UserRepository getUserRepository() {
		return new UserGeneratorRepositoryImpl();
	}

	@Bean
	public TripPricer getTripPricerProxy(){return new TripPricer();}

	@Bean
	public GpsProxy getGpsProxy(){
		return new GpsProxy();
	}

	@Bean
	public RewardProxy getRewardProxy(){
		return new RewardProxy();
	}

}
