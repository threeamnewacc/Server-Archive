package me.devkevin.controller;

import com.google.gson.JsonArray;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DevKevin
 * Project: WebAPI
 * Date: 23/02/2022 @ 4:05
 */
@RestController
@RequestMapping("/api/{key}/punishments")
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
public class PunishController {

    @Autowired private PunishmentRepository punishmentRepository;
    @Autowired private PlayerRepository playerRepository;

    @RequestMapping("/fetch_by_uuid/{uuid}")
    public ResponseEntity<String> fetchByUuid(@PathVariable("key") String key,
                                              @PathVariable("uuid") String uuid) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        Player player = this.playerRepository.findFirstByUniqueId(uuid);

        if (player == null) {
            return new ResponseEntity<>(new JsonArray().toString(), HttpStatus.OK);
        }

        JsonArray punishmentsJson = new JsonArray();

        for (Punishment punishment : this.punishmentRepository.findByPlayerId(player.getPlayerId())) {
            JsonObject object = new JsonObject();
            object.addProperty("id", punishment.getId());
            object.addProperty("timestamp", punishment.getTimestamp().getTime());
            object.addProperty("expiry", punishment.getExpiry() == null ? 0 : punishment.getExpiry().getTime());
            object.addProperty("server_ip", punishment.getServerIp());
            object.addProperty("reason", punishment.getReason());
            object.addProperty("type", punishment.getType());
            object.addProperty("ip", punishment.getIp());
            object.addProperty("punisherId", punishment.getPunisherId());
            object.addProperty("playerId", punishment.getPlayerId());

            punishmentsJson.add(object);
        }

        return new ResponseEntity<>(punishmentsJson.toString(), HttpStatus.OK);
    }

    @RequestMapping("/fetch/{name}")
    public ResponseEntity<String> fetchPunishments(@PathVariable("key") String key,
                                                   @PathVariable("name") String name) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        Player player = PlayerUtil.getByName(name, this.playerRepository);
        if (player == null) {
            return new ResponseEntity<>(new JsonArray().toString(), HttpStatus.OK);
        }

        Map<Integer, Player> playerCache = new HashMap<>();

        JsonArray array = new JsonArray();

        List<Punishment> punishments = this.punishmentRepository.findByPlayerId(player.getPlayerId());
        for (Punishment punishment : punishments) {
            JsonObject object = new JsonObject();

            Player player1 = playerCache.get(punishment.getPunisherId());
            if (player1 == null) {
                player1 = this.playerRepository.findFirstByPlayerId(punishment.getPunisherId());

                playerCache.put(punishment.getPunisherId(), player1);
            }

            object.addProperty("punisher", player1 == null ? "CONSOLE" : player1.getName());
            object.addProperty("timestamp", punishment.getTimestamp().getTime());

            Long time = punishment.getExpiry() != null ? punishment.getExpiry().getTime() : null;
            if (punishment.getType().toUpperCase().startsWith("PERM") ||
                    punishment.getType().toUpperCase().startsWith("UN")) {
                time = null;
            }

            object.addProperty("expiry", time);
            object.addProperty("reason", punishment.getReason());
            object.addProperty("type", punishment.getType());

            array.add(object);
        }

        return new ResponseEntity<>(array.toString(), HttpStatus.OK);
    }

    @RequestMapping("/punish")
    public ResponseEntity<String> onPunish(HttpServletRequest request,
                                           @PathVariable("key") String key) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        Player player;

        String name = request.getParameter("name");
        if (name != null) {
            player = PlayerUtil.getByName(name, this.playerRepository);
        } else {
            player = this.playerRepository.findFirstByPlayerId(Integer.parseInt(request.getParameter("player-id")));
        }

        if (player == null) {
            return Constants.PLAYER_NOT_FOUND_STRING;
        }

        String ip = request.getParameter("ip-address");
        if (ip == null || ip.equals("UNKNOWN")) {
            ip = player.getIpAddress();
        }

        String type = request.getParameter("type").toUpperCase();
        if ((type.equals("UNBAN") && !player.isBanned()) || (type.equals("UNBLACKLIST") && !player.isBlacklisted())) {
            JsonObject object = new JsonObject();
            object.addProperty("response", "not-banned");
            return new ResponseEntity<>(object.toString(), HttpStatus.OK);
        } else if (type.equals("UNMUTE") && !player.isMuted()) {
            JsonObject object = new JsonObject();
            object.addProperty("response", "not-muted");
            return new ResponseEntity<>(object.toString(), HttpStatus.OK);
        }

        String reason = request.getParameter("reason");
        String serverIp = request.getRemoteAddr();

        String strExpiry = request.getParameter("expiry");

        Timestamp expiry = null;
        if (strExpiry != null && !strExpiry.equals("PERM")) {
            expiry = Timestamp.valueOf(strExpiry);
        }

        int punisher = Integer.parseInt(request.getParameter("punisher"));

        Punishment punishment = new Punishment();

        punishment.setPlayerId(player.getPlayerId());
        punishment.setPunisherId(punisher);
        punishment.setServerIp(serverIp);
        punishment.setExpiry(expiry);
        punishment.setReason(reason);
        punishment.setType(type);
        punishment.setIp(ip);
        punishment.setTimestamp(new Timestamp(System.currentTimeMillis()));

        this.punishmentRepository.save(punishment);

        boolean unPunish = type.toUpperCase().startsWith("UN");
        if (type.toUpperCase().contains("BAN")) {
            player.setBanned(!unPunish);

            player.setBanTime(expiry);
        } else if (type.toUpperCase().contains("MUTE")) {
            player.setMuted(!unPunish);

            player.setMuteTime(expiry);
        } else if (type.toUpperCase().contains("IPBAN")) {
            player.setIpBanned(!unPunish);
        } else if (type.toUpperCase().contains("BLACKLIST")) {
            player.setBlacklisted(!unPunish);
        }

        this.playerRepository.save(player);

        return Constants.SUCCESS_STRING;
    }
}
