package club.minemen.practice.request;

import club.minemen.core.api.request.Request;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InsertMatchRequest implements Request {

	private final UUID matchId;

	private final Integer winners;
	private final Integer losers;

	private final int inventory;

	private final int[] eloBefore;
	private final int[] eloAfter;

	@Override
	public String getPath() {
		return "/matches/insert/match";
	}

	@Override
	public Map<String, Object> toMap() {
		return new ImmutableMap.Builder<String, Object>()
				.put("match-id", this.matchId.toString())
				.put("inventory", this.inventory)
				.put("winner-elo-before", this.eloBefore[0])
				.put("loser-elo-before", this.eloBefore[1])
				.put("winner-elo-after", this.eloAfter[0])
				.put("loser-elo-after", this.eloAfter[1])
				.put("winners", this.winners)
				.put("losers", this.losers).build();
	}

}
