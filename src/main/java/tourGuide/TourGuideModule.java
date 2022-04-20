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
import org.springframework.context.annotation.Primary;
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
	@Primary
	public RewardsService getRewardsService() {
		return new RewardsService(getGpsProxy(), getRewardProxy());
	}

	@Bean
	@Primary
	public UserRepository getUserRepository() {
		return new UserGeneratorRepositoryImpl();
	}

	@Bean
	@Primary
	public TripPricerProxy getTripPricerProxy(){
		logger.info("Initializing tripProxy @ "+ tripURL);
		return Feign.builder().client(new OkHttpClient())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.target(TripPricerProxy.class,tripURL);
	}

	@Bean
	@Primary
	public GpsProxy getGpsProxy(){
		logger.info("Initializing gpsProxy @ "+ gpsURL);
		return Feign.builder().client(new OkHttpClient())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.target(GpsProxy.class, gpsURL);
	}

	@Bean
	@Primary
	public RewardProxy getRewardProxy(){
		logger.info("Initializing rewardProxy @ "+ rewardURL);
		return Feign.builder().client(new OkHttpClient())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.target(RewardProxy.class,rewardURL);
	}

	@Bean
	public RewardsService getRewardsServiceTest() {
		return new RewardsService(getGpsProxyTest(), getRewardProxyTest());
	}

	@Bean
	public TripPricerProxy getTripPricerProxyTest(){
		return Feign.builder().client(new OkHttpClient())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.target(TripPricerProxy.class,"http://localhost:9003");
	}

	@Bean
	public GpsProxy getGpsProxyTest(){
		return Feign.builder().client(new OkHttpClient())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.target(GpsProxy.class, "http://localhost:9001");
	}

	@Bean
	public RewardProxy getRewardProxyTest(){
		return Feign.builder().client(new OkHttpClient())
				.encoder(new GsonEncoder())
				.decoder(new GsonDecoder())
				.target(RewardProxy.class,"http://localhost:9002");
	}
}
