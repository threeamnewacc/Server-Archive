package us.zonix.api.controller;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import us.zonix.api.Application;
import us.zonix.api.model.Player;
import us.zonix.api.repository.PlayerRepository;
import us.zonix.api.util.Constants;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import static java.lang.Math.toIntExact;

@RestController
@RequestMapping("/api/{key}/player")
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
public class PlayerController {

    @Autowired
    private PlayerRepository playerRepository;

    @RequestMapping("/fetch_by_name_similar/{name}")
    public ResponseEntity<String> fetchByNameSimilar(@PathVariable(name = "key") String key, @PathVariable(name = "name") String name) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        JsonArray results = new JsonArray();

        for (String username : Application.getSearch().getUsernames()) {
            String lowered = username.toLowerCase();

            if (lowered.equalsIgnoreCase(name) || lowered.startsWith(name) || lowered.contains(name)) {
                results.add(username);
            }

            if (results.size() >= 5) {
                break;
            }
        }

        return new ResponseEntity<>(results.toString(), HttpStatus.OK);
    }

    @RequestMapping("/fetch_by_uuid/{uuid}")
    public Player fetchByUuid(@PathVariable(name = "key") String key, @PathVariable(name = "uuid") UUID uuid) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        return fetchByUuid(this.playerRepository, uuid);
    }

    @RequestMapping("/fetch_by_name/{name}")
    public Player fetchByName(@PathVariable(name = "key") String key, @PathVariable(name = "name") String name) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        return fetchByName(this.playerRepository, name);
    }

    @RequestMapping("/fetch_by_ip/{uuid}")
    public ResponseEntity<String> fetchByIp(@PathVariable("key") String key, @PathVariable("uuid") String uuid) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        Player player = this.playerRepository.findFirstByUuid(uuid);
        JsonArray alts = new JsonArray();

        for (Player check : this.playerRepository.findByIp(player.getIp())) {
            alts.add(check.toJson());
        }

        return new ResponseEntity<>(alts.toString(), HttpStatus.OK);
    }

    @RequestMapping("/fetch_by_email/{email}")
    public Player fetchByEmail(@PathVariable("key") String key, @PathVariable("email") String email) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        return this.playerRepository.findFirstByEmailAddress(email);
    }

    @RequestMapping("/fetch_by_confirmation/{confirmation}")
    public Player fetchByConfirmationId(@PathVariable("key") String key, @PathVariable("confirmation") String confirmation) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        return this.playerRepository.findFirstByConfirmationId(confirmation);
    }

    @RequestMapping("/get_status/{uuid}")
    public ResponseEntity<String> isOnline(@PathVariable("key") String key, @PathVariable("uuid") UUID uuid) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        if (uuid == null) {
            return null;
        }

        boolean online = false;
        String server = "";

        Jedis jedis = null;

        try {
            jedis = Application.getController().getPool().getResource();

            if (jedis != null) {
                Long retrieved = Long.valueOf(jedis.hget("player:" + uuid.toString(), "online"));

                online = (retrieved == 0);

                if (online) {
                    server = jedis.hget("player:" + uuid.toString(), "server");
                }
            }
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        JsonObject object = new JsonObject();
        object.addProperty("online", online);
        object.addProperty("server", server);

        return new ResponseEntity<>(object.toString(), HttpStatus.OK);
    }

    @RequestMapping("/save")
    public ResponseEntity<String> save(@PathVariable(name = "key") String key, HttpServletRequest request) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        JsonObject data = new JsonParser().parse(request.getParameter("data")).getAsJsonObject();

        Player player = this.playerRepository.findFirstByUuid(data.get("uuid").getAsString());

        if (player == null) {
            return new ResponseEntity<>(Constants.PLAYER_NOT_FOUND, HttpStatus.OK);
        }

        if (!data.get("name").isJsonNull()) {
            player.setName(data.get("name").getAsString());
        }

        if (!data.get("rank").isJsonNull()) {
            player.setRank(data.get("rank").getAsString());
        }

        if (!data.get("symbol").isJsonNull()) {
            player.setSymbol(data.get("symbol").getAsString());
        }

        if (!data.get("bought_symbols").isJsonNull()) {
            player.setBoughtSymbols(data.get("bought_symbols").getAsBoolean());
        }

        if (!data.get("two_factor_authentication").isJsonNull()) {
            player.setTwoFactorAuthentication(data.get("two_factor_authentication").getAsString());
        }

        if (!data.get("authenticated").isJsonNull()) {
            player.setAuthenticated(data.get("authenticated").getAsBoolean());
        }


        player.setLastLogin(new Timestamp(data.get("last_login").getAsLong()));
        player.setLastServer(data.get("last_server").getAsString());
        player.setIp(data.get("ip").getAsString());

        this.playerRepository.save(player);

        return new ResponseEntity<>(Constants.SUCCESS, HttpStatus.OK);
    }

    @RequestMapping("/update-register")
    public ResponseEntity<String> updateRegister(@PathVariable(name = "key") String key, HttpServletRequest request) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        UUID uuid = UUID.fromString(request.getParameter("uuid"));
        String emailAddress = request.getParameter("emailAddress");
        String confirmationId = request.getParameter("confirmationId");
        boolean registered = Boolean.valueOf(request.getParameter("registered"));

        Player player = fetchByUuid(this.playerRepository, uuid);

        if (player == null) {
            return null;
        }

        player.setEmailAddress(emailAddress);
        player.setConfirmationId(confirmationId);
        player.setRegistered(registered);

        this.playerRepository.save(player);

        return new ResponseEntity<>(Constants.SUCCESS, HttpStatus.OK);
    }

    @RequestMapping("/update-rank")
    public ResponseEntity<String> updateRank(@PathVariable(name = "key") String key, HttpServletRequest request) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        UUID uuid = UUID.fromString(request.getParameter("uuid"));
        String rank = request.getParameter("rank");

        Player player = fetchByUuid(this.playerRepository, uuid);

        if (player == null) {
            return null;
        }

        player.setRank(rank);

        this.playerRepository.save(player);

        return new ResponseEntity<>(Constants.SUCCESS, HttpStatus.OK);
    }

    @RequestMapping("/update-symbol")
    public ResponseEntity<String> updateSymbol(@PathVariable(name = "key") String key, HttpServletRequest request) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        UUID uuid = UUID.fromString(request.getParameter("uuid"));
        String symbol = request.getParameter("symbol");

        Player player = fetchByUuid(this.playerRepository, uuid);

        if (player == null) {
            return null;
        }

        player.setSymbol(symbol);

        this.playerRepository.save(player);

        return new ResponseEntity<>(Constants.SUCCESS, HttpStatus.OK);
    }

    @RequestMapping("/update-bought-symbols")
    public ResponseEntity<String> updateBoughtSymbol(@PathVariable(name = "key") String key, HttpServletRequest request) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        UUID uuid = UUID.fromString(request.getParameter("uuid"));
        boolean boughtSymbols = Boolean.parseBoolean(request.getParameter("boughtSymbols"));

        Player player = fetchByUuid(this.playerRepository, uuid);

        if (player == null) {
            return null;
        }

        player.setBoughtSymbols(boughtSymbols);

        this.playerRepository.save(player);

        return new ResponseEntity<>(Constants.SUCCESS, HttpStatus.OK);
    }

    @RequestMapping("/update-auth")
    public ResponseEntity<String> updateAuth(@PathVariable(name = "key") String key, HttpServletRequest request) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        UUID uuid = UUID.fromString(request.getParameter("uuid"));
        String twoFactorAuthentication = request.getParameter("twoFactorAuthentication");
        boolean authenticated = Boolean.valueOf(request.getParameter("authenticated"));
        Player player = fetchByUuid(this.playerRepository, uuid);

        if (player == null) {
            return null;
        }
        player.setTwoFactorAuthentication(twoFactorAuthentication);
        player.setAuthenticated(authenticated);
        this.playerRepository.save(player);

        return new ResponseEntity<>(Constants.SUCCESS, HttpStatus.OK);
    }

    private static Player fetchByUuid(PlayerRepository repository, UUID uuid) {
        Player player = repository.findFirstByUuid(uuid.toString());

        if (player == null) {
            player = new Player();
            player.setUuid(uuid.toString());
            player.setName("");
            player.setRank("DEFAULT");
            player.setFirstLogin(new Timestamp(System.currentTimeMillis()));
            player.setLastLogin(new Timestamp(System.currentTimeMillis()));
            player.setIp("");

            repository.save(player);
        }

        return player;
    }

    public static Player fetchByName(PlayerRepository repository, String name) {
        List<Player> players = repository.findByName(name);

        if (players.size() > 0) {
            for (Player player : players) {
                if (player.getName().equalsIgnoreCase(name)) {
                    return player;
                }
            }
        }

        return null;
    }
}