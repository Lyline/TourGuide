package tourGuide.proxies.rewardCentralProxy;

import feign.Param;
import feign.RequestLine;

import java.util.UUID;

public interface RewardProxy {

  @RequestLine("GET /getAttractionReward/{attractionId}/{userId}")
  int getAttractionRewardPoints(@Param("attractionId") UUID attractionId, @Param("userId") UUID userId);
}
