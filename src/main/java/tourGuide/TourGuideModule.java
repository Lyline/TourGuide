package tourGuide;

import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tourGuide.proxies.gpsProxy.GpsProxy;
import tourGuide.proxies.rewardCentralProxy.RewardProxy;
import tourGuide.proxies.tripPricerProxy.TripPricer;
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
		return Feign.builder().client(new OkHttpClient())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.target(RewardProxy.class,"http://localhost:9002");
	}

}
