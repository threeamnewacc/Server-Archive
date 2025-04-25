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
import us.zonix.api.model.Player;
import us.zonix.api.model.Punishment;
import us.zonix.api.repository.PlayerRepository;
import us.zonix.api.repository.PunishmentRepository;
import us.zonix.api.util.Constants;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

@RestController
@RequestMapping("/api/{key}/punishment")
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
public class PunishmentController {

    @Autowired private PlayerRepository playerRepository;
    @Autowired private PunishmentRepository punishmentRepository;

    @RequestMapping("/fetch_by_uuid/{uuid}")
    public ResponseEntity<String> fetchByUuid(@PathVariable("key") String key, @PathVariable("uuid") String uuid) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        Player player = this.playerRepository.findFirstByUuid(uuid);

        if (player == null) {
            return new ResponseEntity<>(new JsonArray().toString(), HttpStatus.OK);
        }

        JsonArray punishments = new JsonArray();

        for (Punishment punishment : this.punishmentRepository.findByUuid(player.getUuid())) {
            punishments.add(punishment.toJson());
        }

        return new ResponseEntity<>(punishments.toString(), HttpStatus.OK);
    }

    @RequestMapping("/fetch_by_staff_uuid/{uuid}")
    public ResponseEntity<String> fetchByAddedBy(@PathVariable("key") String key, @PathVariable("uuid") String uuid) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        Player player = this.playerRepository.findFirstByUuid(uuid);

        if (player == null) {
            return new ResponseEntity<>(new JsonArray().toString(), HttpStatus.OK);
        }

        JsonArray punishments = new JsonArray();

        for (Punishment punishment : this.punishmentRepository.findByAddedBy(player.getUuid())) {
            punishments.add(punishment.toJson());
        }

        return new ResponseEntity<>(punishments.toString(), HttpStatus.OK);
    }

    @RequestMapping("/insert")
    public ResponseEntity<String> insert(@PathVariable(name = "key") String key, HttpServletRequest request) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        JsonObject data = new JsonParser().parse(request.getParameter("data")).getAsJsonObject();

        Punishment punishment = new Punishment();

        if(data.get("type") != null) {
            punishment.setType(data.get("type").getAsString());
        }

        punishment.setUuid(data.get("uuid").getAsString());

        if (!data.get("added_by").isJsonNull()) {
            punishment.setAddedBy(data.get("added_by").getAsString());
        }

        punishment.setAddedAt(new Timestamp(System.currentTimeMillis()));
        punishment.setReason(data.get("reason").getAsString());
        punishment.setDuration(data.get("duration").getAsLong());

        this.punishmentRepository.save(punishment);

        return new ResponseEntity<>(punishment.toJson().toString(), HttpStatus.OK);
    }

    @RequestMapping("/remove")
    public ResponseEntity<String> remove(@PathVariable(name = "key") String key, HttpServletRequest request) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        JsonObject data = new JsonParser().parse(request.getParameter("data")).getAsJsonObject();

        Punishment punishment = this.punishmentRepository.findFirstById(data.get("id").getAsInt());

        if (punishment == null) {
            return null;
        }

        punishment.setRemovedAt(new Timestamp(data.get("removed_at").getAsLong()));
        punishment.setRemovedReason(data.get("removed_reason").getAsString());

        if (!data.get("removed_by").isJsonNull()) {
            punishment.setRemovedBy(data.get("removed_by").getAsString());
        }

        this.punishmentRepository.save(punishment);

        return new ResponseEntity<>(punishment.toJson().toString(), HttpStatus.OK);
    }

}