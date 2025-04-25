package us.zonix.api.controller;

import static java.lang.Math.toIntExact;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import us.zonix.api.repository.PunishmentRepository;
import us.zonix.api.util.Constants;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/{key}/admin")
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
public class AdminController {

    @Autowired private PlayerRepository playerRepository;
    @Autowired private PunishmentRepository punishmentRepository;

    @RequestMapping("/fetch-players-by-ranks")
    public ResponseEntity<String> fetchStaff(@PathVariable("key") String key, HttpServletRequest request) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        JsonObject toReturn = new JsonObject();

        for (JsonElement rank : new JsonParser().parse(request.getParameter("ranks")).getAsJsonArray()) {
            JsonArray entries = new JsonArray();

            for (Player player : this.playerRepository.findByRank(rank.getAsString())) {
                entries.add(player.getUuid());
            }

            toReturn.add(rank.getAsString(), entries);
        }

        return new ResponseEntity<>(toReturn.toString(), HttpStatus.OK);
    }

    @RequestMapping("/fetch-stats")
    public ResponseEntity<String> fetchStats(@PathVariable("key") String key) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        JsonObject object = new JsonObject();
        object.addProperty("players", this.playerRepository.countBy());
        object.addProperty("bans", this.punishmentRepository.countByType("BAN"));
        object.addProperty("blacklists", this.punishmentRepository.countByType("BLACKLIST"));

        return new ResponseEntity<>(object.toString(), HttpStatus.OK);
    }

    @RequestMapping("/fetch-proxy-data")
    public ResponseEntity<String> fetchProxyData(@PathVariable("key") String key) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        int usCount = 0;
        int euCount= 0;
        int saCount = 0;
        int asCount = 0;

        Jedis jedis = null;

        try {
            jedis = Application.getController().getPool().getResource();
            jedis.auth(Application.getController().getPassword());

            Long usProxy = jedis.scard("proxy:zonix-us:usersOnline");
            Long euProxy = jedis.scard("proxy:zonix-eu:usersOnline");
            Long saProxy = jedis.scard("proxy:zonix-sa:usersOnline");
            Long auProxy = jedis.scard("proxy:zonix-as2:usersOnline");

            if (usProxy != null) {
                usCount = toIntExact(usProxy);
            }

            if (euProxy != null) {
                euCount = toIntExact(euProxy);
            }

            if (saProxy != null) {
                saCount = toIntExact(saProxy);
            }

            if (auProxy != null) {
                asCount = toIntExact(auProxy);
            }
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        JsonObject object = new JsonObject();
        object.addProperty("us-proxy-count", usCount);
        object.addProperty("eu-proxy-count", euCount);
        object.addProperty("sa-proxy-count", saCount);
        object.addProperty("as-proxy-count", asCount);

        return new ResponseEntity<>(object.toString(), HttpStatus.OK);
    }

}