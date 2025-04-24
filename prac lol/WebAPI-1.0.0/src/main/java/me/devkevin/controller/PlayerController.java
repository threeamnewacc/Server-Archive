package me.devkevin.controller;

import com.google.gson.JsonObject;
import me.devkevin.model.Player;
import me.devkevin.model.Punishment;
import me.devkevin.repo.PlayerRepository;
import me.devkevin.repo.PunishmentRepository;
import me.devkevin.util.Constants;
import me.devkevin.util.PlayerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/{key}/player")
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
public class PlayerController {

	@Autowired
	private PunishmentRepository punishmentRepository;
	@Autowired
	private PlayerRepository playerRepository;

	public static Player getPlayer(PlayerRepository playerRepository, UUID uuid) {
		Player player = playerRepository.findFirstByUniqueId(uuid.toString());
		if (player == null) {
			player = new Player();

			player.setLastLogin(player.getLastLogin());
			player.setFirstLogin(new Timestamp(System.currentTimeMillis()));
			player.setUniqueId(uuid.toString());
			player.setRank("");
			player.setName(player.getName());
			player.setIpAddress(player.getIpAddress());

			playerRepository.save(player);
		}

		return player;
	}

	public static Player getPlayer(PlayerRepository playerRepository, String name) {
		List<Player> players = playerRepository.findByName(name);

		if (players.size() > 0) {
			for (Player player : players) {
				if (player.getName().equalsIgnoreCase(name)) {
					return player;
				}
			}
		}

		return null;
	}

	@RequestMapping("/from_name/{name}")
	public Player getData(@PathVariable(name = "key") String key,
						  @PathVariable(name = "name") String name,
						  HttpServletRequest request) {

		if (!Constants.validServerKey(key)) {
			return null;
		}

		return getPlayer(this.playerRepository, name);
	}

	@RequestMapping("/{uuid}")
	public Player getData(@PathVariable(name = "key") String key,
						  @PathVariable(name = "uuid") UUID uuid,
						  HttpServletRequest request) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		return this.getGlobalData(key, uuid, request);
	}

	@RequestMapping("/{uuid}/global")
	public Player getGlobalData(@PathVariable(name = "key") String key,
								@PathVariable(name = "uuid") UUID uuid,
								HttpServletRequest request) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		String name = request.getParameter("name");
		String ip = request.getParameter("ip");

		Player player = getPlayer(this.playerRepository, uuid);
		boolean changesApplied = false;
		if (player.getIpAddress() == null || ip != null && !player.getIpAddress().equals(ip)) {
			player.setIpAddress(ip);
			changesApplied = true;
		} else if (player.getName() == null || name != null && !player.getName().equals(name)) {
			player.setName(name);
			changesApplied = true;
		}

		if (changesApplied) {
			this.playerRepository.save(player);
		}

		return player;
	}

	@RequestMapping("/{name}/ban-info")
	public ResponseEntity<String> getBanInfo(@PathVariable(name = "key") String key,
											 @PathVariable(name = "name") String name) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		JsonObject object = new JsonObject();

		Player player = PlayerUtil.getByName(name, this.playerRepository);
		if (player == null) {
			object.addProperty("response", "player-not-found");
			return new ResponseEntity<>(object.toString(), HttpStatus.OK);
		}

		object.addProperty("response", "success");

		object.addProperty("name", player.getName());
		object.addProperty("muted", player.isMuted());
		object.addProperty("banned", player.isBanned());
		object.addProperty("ip-banned", player.isIpBanned());
		object.addProperty("blacklisted", player.isBlacklisted());
		object.addProperty("ban-time", player.getBanTime() != null ? player.getBanTime().getTime() : 0);
		object.addProperty("mute-time", player.getMuteTime() != null ? player.getMuteTime().getTime() : 0);

		if (player.isMuted()) {
			Punishment punishment = this.punishmentRepository
					.findFirstByPlayerIdAndTypeContainingOrderByTimestampDesc(
							player.getPlayerId(), "MUTE");
			//			Punishment punishment = punishments.get(punishments.size() - 1);


			object.addProperty("mute-reason", punishment.getReason());
		}

		if (player.isBanned()) {
			Punishment punishment = this.punishmentRepository
					.findFirstByPlayerIdAndTypeContainingOrderByTimestampDesc(
							player.getPlayerId(), "%BAN%");
			//			Punishment punishment = punishments.get(punishments.size() - 1);

			object.addProperty("ban-reason", punishment.getReason());
		}

		if (player.isBlacklisted()) {
			Punishment punishment = this.punishmentRepository
					.findFirstByPlayerIdAndTypeContainingOrderByTimestampDesc(
							player.getPlayerId(), "%BLACKLIST%");
			//			Punishment punishment = punishments.get(punishments.size() - 1);

			object.addProperty("ban-reason", punishment.getReason());
		}

		return new ResponseEntity<>(object.toString(), HttpStatus.OK);
	}

	@RequestMapping("/{uuid}/ip-check/{ip}")
	public ResponseEntity<Boolean> doIPCheck(@PathVariable("key") String key,
											 @PathVariable("ip") String ip) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		List<Player> players = this.playerRepository.findByIpAddress(ip);

		for (Player player : players) {
			if (player.isBanned() || player.isIpBanned() || player.isBlacklisted()) {
				return new ResponseEntity<>(true, HttpStatus.OK);
			}
		}

		return new ResponseEntity<>(false, HttpStatus.OK);
	}

	@RequestMapping("/{name}/update-rank")
	public ResponseEntity<String> updateRank(HttpServletRequest request,
											 @PathVariable("key") String key,
											 @PathVariable("name") String name) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = PlayerUtil.getByName(name, this.playerRepository);
		if (player == null) {
			return Constants.PLAYER_NOT_FOUND_STRING;
		}

		player.setRank(request.getParameter("rank"));

		this.playerRepository.save(player);

		return Constants.SUCCESS_STRING;
	}

	@RequestMapping("/{name}/update/last_login")
	public ResponseEntity<String> updateLastJoin(HttpServletRequest request,
											 @PathVariable("key") String key,
											 @PathVariable("name") String name) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = PlayerUtil.getByName(name, this.playerRepository);
		if (player == null) {
			return Constants.PLAYER_NOT_FOUND_STRING;
		}

		player.setLastLogin(request.getParameter("last_login"));

		this.playerRepository.save(player);

		return Constants.SUCCESS_STRING;
	}

	@RequestMapping("/{name}/update/ip")
	public ResponseEntity<String> updateIP(HttpServletRequest request,
												 @PathVariable("key") String key,
												 @PathVariable("name") String name) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = PlayerUtil.getByName(name, this.playerRepository);
		if (player == null) {
			return Constants.PLAYER_NOT_FOUND_STRING;
		}

		player.setIpAddress(request.getParameter("ip"));

		this.playerRepository.save(player);

		return Constants.SUCCESS_STRING;
	}

	@RequestMapping("/{name}/update/country_code")
	public ResponseEntity<String> updateCountryCode(HttpServletRequest request,
										   @PathVariable("key") String key,
										   @PathVariable("name") String name) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = PlayerUtil.getByName(name, this.playerRepository);
		if (player == null) {
			return Constants.PLAYER_NOT_FOUND_STRING;
		}

		player.setCountryCode(request.getParameter("country_code"));

		this.playerRepository.save(player);


		return Constants.SUCCESS_STRING;
	}

	@RequestMapping("/{name}/update/country_name")
	public ResponseEntity<String> updateCountryName(HttpServletRequest request,
													@PathVariable("key") String key,
													@PathVariable("name") String name) {
		if (!Constants.validServerKey(key)) {
			return null;
		}

		Player player = PlayerUtil.getByName(name, this.playerRepository);
		if (player == null) {
			return Constants.PLAYER_NOT_FOUND_STRING;
		}

		player.setCountryName(request.getParameter("country_name"));

		this.playerRepository.save(player);


		return Constants.SUCCESS_STRING;
	}
}
