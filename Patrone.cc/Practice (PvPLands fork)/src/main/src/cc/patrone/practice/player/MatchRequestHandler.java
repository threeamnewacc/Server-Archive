package cc.patrone.practice.player;

import zone.potion.utils.message.CC;
import zone.potion.utils.message.ClickableMessage;
import cc.patrone.practice.arena.Arena;
import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.match.MatchRequest;
import cc.patrone.practice.party.Party;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.entity.Player;

public class MatchRequestHandler {
	private static final Object DUMMY = new Object();
	private final Cache<MatchRequest, Object> requests = CacheBuilder
			.newBuilder()
			.expireAfterWrite(15L, TimeUnit.SECONDS)
			.build();

	public MatchRequest getMatchRequest(UUID requester) {
		return requests.asMap().keySet().stream().filter(request -> request.getRequester().equals(requester)).findAny().orElse(null);
	}

	public MatchRequest getMatchRequest(UUID requester, String kitName) {
		return requests.asMap().keySet().stream().filter(req -> req.getRequester().equals(requester) && req.getKitName().equals(kitName)).findAny().orElse(null);
	}

	public void removeMatchRequest(MatchRequest request) {
		requests.asMap().remove(request);
	}

	public void sendMatchRequest(Player sender, Player selected, Kit kit, boolean partyDuel, Party party, Party targetParty, Arena arena) {
		MatchRequest request = new MatchRequest(sender.getUniqueId(), selected.getUniqueId(), kit.getName(), arena, partyDuel);
		requests.put(request, DUMMY);
		sender.closeInventory();

		ClickableMessage requestMsg = new ClickableMessage(sender.getName())
				.color(CC.SECONDARY)
				.add(" has sent you a " + (partyDuel ? "party " : "") + "duel request on arena ")
				.color(CC.PRIMARY)
				.add(arena.getName())
				.color(CC.SECONDARY)
				.add(" with kit ")
				.color(CC.PRIMARY)
				.add(kit.getName())
				.color(CC.SECONDARY)
				.add(". ")
				.color(CC.PRIMARY)
				.add("(Accept)")
				.color(CC.ACCENT)
				.hover(CC.ACCENT + "Click to Accept")
				.command("/accept " + sender.getName() + " " + kit.getName());

		if (partyDuel) {
			targetParty.broadcast(requestMsg);
			party.broadcast(CC.PRIMARY + "Sent a duel request to " + CC.SECONDARY + selected.getName() + CC.PRIMARY + "'s party with kit "
					+ CC.SECONDARY + kit.getName() + CC.PRIMARY + " on arena " + CC.SECONDARY + arena.getName() + CC.PRIMARY + ".");
		} else {
			requestMsg.sendToPlayer(selected, true);
			sender.sendMessage(CC.PRIMARY + "Sent a duel request to " + CC.SECONDARY + selected.getName() + CC.PRIMARY + " with kit "
					+ CC.SECONDARY + kit.getName() + CC.PRIMARY + " on arena " + CC.SECONDARY + arena.getName() + CC.PRIMARY + ".", true);
		}
	}
}
