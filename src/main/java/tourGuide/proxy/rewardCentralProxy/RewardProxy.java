package tourGuide.proxy.rewardCentralProxy;

import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;

import java.util.UUID;

@Service
public class RewardProxy {
  RewardCentral rewardCentral=new RewardCentral();

  public RewardProxy() {
  }

  public int getAttractionRewardPoints(UUID attractionId, UUID userId){
    return rewardCentral.getAttractionRewardPoints(attractionId, userId);
  }
}
