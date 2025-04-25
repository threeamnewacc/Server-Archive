package us.zonix.api.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import us.zonix.api.model.ClientBan;
import us.zonix.api.model.ClientCosmetics;
import us.zonix.api.model.ClientWhitelist;
import us.zonix.api.model.Player;
import us.zonix.api.repository.ClientBanRepository;
import us.zonix.api.repository.ClientCosmeticsRepository;
import us.zonix.api.repository.ClientWhitelistRepository;
import us.zonix.api.repository.PlayerRepository;
import us.zonix.api.util.Constants;

@Controller
@RequestMapping("/api/{key}/client/")
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
public class ClientController {

	@Autowired private ClientWhitelistRepository clientWhitelistRepository;
	@Autowired private ClientCosmeticsRepository cosmeticsRepository;
	@Autowired private ClientBanRepository clientBanRepository;
	@Autowired private PlayerRepository playerRepository;

	@RequestMapping("/cosmetic/{player}/{command}")
	public ResponseEntity<String> handleCosmetic(
			@PathVariable("key") String key,
			@PathVariable("player") String name,
			@PathVariable("command") String command,
			HttpServletRequest request) {
		if (!Constants.isValidKey(key)) {
			return null;
		}

		Player player = PlayerController.fetchByName(this.playerRepository, name);
		if (player == null) {
			return null;
		}

		ClientCosmetics cosmetics = this.cosmeticsRepository.findFirstByUuid(player.getUuid());
		if (cosmetics == null) {
			cosmetics = new ClientCosmetics();
			cosmetics.setUuid(player.getUuid());
		}

		String type = request.getParameter("type");
		String data = request.getParameter("data");

		switch (type.toLowerCase()) {
			case "wings":
				if (command.equalsIgnoreCase("add")) {
					cosmetics.setActiveWings(true);
					cosmetics.setHasWings(true);
				} else {
					cosmetics.setActiveWings(false);
					cosmetics.setHasWings(false);
				}
				break;
			default:
				if (command.equalsIgnoreCase("add")) {
					cosmetics.addCosmetic(data.toLowerCase());

					if(cosmetics.getActiveCape() == null) {
						cosmetics.setActiveCape(data.toLowerCase());
					}

				} else {
					cosmetics.removeCosmetic(data.toLowerCase());

					if(cosmetics.getActiveCape() != null) {
						cosmetics.setActiveCape(null);
					}
				}
				break;
		}

		this.cosmeticsRepository.save(cosmetics);
		return new ResponseEntity<>(Constants.SUCCESS, HttpStatus.OK);
	}

	@RequestMapping("/whitelist/get")
	public ResponseEntity<String> getWhitelist(@PathVariable("key") String key, HttpServletRequest request) {
		if (!Constants.isValidKey(key)) {
			return null;
		}

		String param = request.getParameter("data");

		if (param == null) {
			return null;
		}

		System.out.println("got data: " + param);

		JsonObject data = new JsonParser().parse(param).getAsJsonObject();

		if (!data.has("ip")) {
			return null;
		}

		ClientWhitelist whitelist = this.clientWhitelistRepository.findByIp(data.get("ip").getAsString());

		if (whitelist == null) {
			return null;
		}

		JsonObject object = new JsonObject();
		object.addProperty("ip", whitelist.getIp());
		object.addProperty("uuid", whitelist.getUuid());

		return new ResponseEntity<>(object.toString(), HttpStatus.OK);
	}

	@RequestMapping("/whitelist/update")
	public ResponseEntity<String> addWhitelist(@PathVariable("key") String key, HttpServletRequest request) {
		if (!Constants.isValidKey(key)) {
			return null;
		}

		String param = request.getParameter("data");

		if (param == null) {
			return null;
		}

		JsonObject data = new JsonParser().parse(param).getAsJsonObject();

		if (data.has("ip") && data.has("uuid")) {
			String ip = data.get("ip").getAsString();
			String uuid = data.get("uuid").getAsString();

			if (this.clientWhitelistRepository.findByIp(ip) != null) {
				return new ResponseEntity<>(Constants.FAILED, HttpStatus.OK);
			}

			ClientWhitelist whitelist = this.clientWhitelistRepository.findByUuid(uuid);

			if (whitelist == null) {
				whitelist = new ClientWhitelist();
				whitelist.setUuid(uuid);
			}

			whitelist.setIp(ip);

			this.clientWhitelistRepository.save(whitelist);

			return new ResponseEntity<>(Constants.SUCCESS, HttpStatus.OK);
		} else {
			return null;
		}
	}

	@RequestMapping("/whitelist/remove")
	public ResponseEntity<String> removeWhitelist(@PathVariable("key") String key, HttpServletRequest request) {
		if (!Constants.isValidKey(key)) {
			return null;
		}

		String param = request.getParameter("data");

		if (param == null) {
			return null;
		}

		JsonObject data = new JsonParser().parse(param).getAsJsonObject();

		if (data.has("ip")) {
			ClientWhitelist whitelist = this.clientWhitelistRepository.findByIp(data.get("ip").getAsString());

			if (whitelist != null) {
				this.clientWhitelistRepository.delete(whitelist);
			}

			return new ResponseEntity<>(Constants.SUCCESS, HttpStatus.OK);
		} else {
			return null;
		}
	}

	@RequestMapping("/ban/get/hwid/")
	public ResponseEntity<String> getBanByHwid(@PathVariable("key") String key, HttpServletRequest request) {
		if (!Constants.isValidKey(key)) {
			return null;
		}

		String param = request.getParameter("data");

		if (param == null) {
			return null;
		}

		JsonObject data = new JsonParser().parse(param).getAsJsonObject();

		if (!data.has("hwid")) {
			return null;
		}

		ClientBan ban = this.clientBanRepository.findByHwid(data.get("hwid").getAsString());

		if (ban == null) {
			return null;
		}

		JsonObject object = new JsonObject();
		object.addProperty("id", ban.getId());
		object.addProperty("ip", ban.getIp());
		object.addProperty("hwid", ban.getHwid());
		object.addProperty("reason", ban.getReason());
		object.addProperty("timestamp", ban.getTimestamp().getTime());

		return new ResponseEntity<>(object.toString(), HttpStatus.OK);
	}

	@RequestMapping("/ban/get/ip/")
	public ResponseEntity<String> getBanByIp(@PathVariable("key") String key, HttpServletRequest request) {
		if (!Constants.isValidKey(key)) {
			return null;
		}

		String param = request.getParameter("data");

		if (param == null) {
			return null;
		}

		JsonObject data = new JsonParser().parse(param).getAsJsonObject();

		if (!data.has("ip")) {
			return null;
		}

		ClientBan ban = this.clientBanRepository.findByIp(data.get("ip").getAsString());

		if (ban == null) {
			return null;
		}

		JsonObject object = new JsonObject();
		object.addProperty("id", ban.getId());
		object.addProperty("ip", ban.getIp());
		object.addProperty("hwid", ban.getHwid());
		object.addProperty("reason", ban.getReason());
		object.addProperty("timestamp", ban.getTimestamp().getTime());

		return new ResponseEntity<>(object.toString(), HttpStatus.OK);
	}

	@RequestMapping("/ban/add")
	public ResponseEntity<String> addBan(@PathVariable("key") String key, HttpServletRequest request) {
		if (!Constants.isValidKey(key)) {
			return null;
		}

		String param = request.getParameter("data");

		if (param == null) {
			return null;
		}

		JsonObject data = new JsonParser().parse(param).getAsJsonObject();

		if (data.has("ip") && data.has("hwid") && data.has("reason")) {
			ClientBan ban = new ClientBan();
			ban.setIp(data.get("ip").getAsString());
			ban.setHwid(data.get("hwid").getAsString());
			ban.setReason(data.get("reason").getAsString());

			this.clientBanRepository.save(ban);

			return new ResponseEntity<>(Constants.SUCCESS, HttpStatus.OK);
		} else {
			return null;
		}
	}

	@RequestMapping("/ban/banwave")
	public ResponseEntity<String> setBansActive(@PathVariable("key") String key) {
		if (!Constants.isValidKey(key)) {
			return null;
		}

		for (ClientBan ban : this.clientBanRepository.findAll()) {
			ban.setActive(true);

			this.clientBanRepository.save(ban);
		}

		return new ResponseEntity<>(Constants.SUCCESS, HttpStatus.OK);
	}

	@RequestMapping("/cosmetics/get/{uuid}")
	public ResponseEntity<String> getCosmetics(@PathVariable("key") String key, @PathVariable("uuid") String uuid, HttpServletRequest request) {
		if (!Constants.isValidKey(key)) {
			return null;
		}

		ClientCosmetics cosmetics = this.cosmeticsRepository.findFirstByUuid(uuid);

		JsonObject object = new JsonObject();

		if (cosmetics == null) {
			object.addProperty("cape", "none");
			object.addProperty("wings", false);
		}
		else {
			object.addProperty("cape", cosmetics.getActiveCape() == null || cosmetics.getActiveCape().isEmpty() ? "none" : cosmetics.getActiveCape());
			object.addProperty("wings", cosmetics.isHasWings() ? cosmetics.isActiveWings() : false);
		}

		return new ResponseEntity<>(object.toString(), HttpStatus.OK);
	}

}