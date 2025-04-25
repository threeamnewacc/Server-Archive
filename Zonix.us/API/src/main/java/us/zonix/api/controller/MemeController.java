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
import us.zonix.api.model.MemeLog;
import us.zonix.api.model.Player;
import us.zonix.api.repository.MemeRepository;
import us.zonix.api.repository.PlayerRepository;
import us.zonix.api.util.Constants;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

@RestController
@RequestMapping("/api/{key}/anticheat")
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
public class MemeController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MemeRepository memeRepository;

    @RequestMapping("/fetch_by_uuid/{uuid}")
    public ResponseEntity<String> fetchByUuid(@PathVariable("key") String key, @PathVariable("uuid") String uuid) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        Player player = this.playerRepository.findFirstByUuid(uuid);

        if (player == null) {
            return new ResponseEntity<>(new JsonArray().toString(), HttpStatus.OK);
        }

        JsonArray anticheat = new JsonArray();

        for (MemeLog memeLog : this.memeRepository.findByUuid(player.getUuid())) {
            anticheat.add(memeLog.toJson());
        }

        return new ResponseEntity<>(anticheat.toString(), HttpStatus.OK);
    }

    @RequestMapping("/insert")
    public ResponseEntity<String> insert(@PathVariable(name = "key") String key, HttpServletRequest request) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        JsonObject data = new JsonParser().parse(request.getParameter("data")).getAsJsonObject();

        MemeLog memeLog = new MemeLog();
        memeLog.setUuid(data.get("uuid").getAsString());
        memeLog.setTimestamp(new Timestamp(System.currentTimeMillis()));
        memeLog.setMessage(data.get("message").getAsString());

        this.memeRepository.save(memeLog);

        return new ResponseEntity<>(memeLog.toJson().toString(), HttpStatus.OK);
    }

}