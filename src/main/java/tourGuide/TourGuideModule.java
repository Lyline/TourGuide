package tourGuide;

import feign.Feign;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tourGuide.proxies.gpsProxy.GpsProxy;
import tourGuide.proxies.rewardCentralProxy.RewardProxy;
import tourGuide.proxies.tripPricerProxy.TripPricerProxy;
import tourGuide.repository.UserGeneratorRepositoryImpl;
import tourGuide.repository.UserRepository;
import tourGuide.service.RewardsService;

@Configuration
public class TourGuideModule {

	Logger logger= LoggerFactory.getLogger(TourGuideModule.class);

	@Value("${gpsURL}")
	private String gpsURL;
	@Value("${tripURL}")
	private String tripURL;
	@Value("${rewardURL}")
	private String rewardURL;

	@Bean
	public RewardsService getRewardsService() {
		return new RewardsService(getGpsProxy(), getRewardProxy());
	}

	@Bean
	public UserRepository getUserRepository() {
		return new UserGeneratorRepositoryImpl();
	}

	@Bean
	public TripPricerProxy getTripPricerProxy(){
		logger.info("Initializing tripProxy @ "+ tripURL);
		return Feign.builder().client(new OkHttpClient())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.target(TripPricerProxy.class,tripURL);
	}

	@Bean
	public GpsProxy getGpsProxy(){
		logger.info("Initializing gpsProxy @ "+ gpsURL);
		return Feign.builder().client(new OkHttpClient())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.target(GpsProxy.class, gpsURL);
	}

	@Bean
	public RewardProxy getRewardProxy(){
		logger.info("Initializing rewardProxy @ "+ rewardURL);
		return Feign.builder().client(new OkHttpClient())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.target(RewardProxy.class,rewardURL);
	}

}
