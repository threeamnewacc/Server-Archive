package us.zonix.api.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.zonix.api.model.PremiumMatches;
import us.zonix.api.repository.PremiumMatchesRepository;
import us.zonix.api.util.Constants;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

@RestController
@RequestMapping("/api/{key}/premium")
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
public class PremiumController {

    @Autowired
    private PremiumMatchesRepository premiumMatchesRepository;

    @RequestMapping("/fetch_by_uuid/{uuid}")
    public PremiumMatches fetchByUuid(@PathVariable("key") String key, @PathVariable("uuid") String uuid) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        return fetchByUuid(this.premiumMatchesRepository, uuid);
    }

    @RequestMapping("/update")
    public ResponseEntity<String> update(@PathVariable(name = "key") String key, HttpServletRequest request) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        JsonObject data = new JsonParser().parse(request.getParameter("data")).getAsJsonObject();
        

        PremiumMatches practiceStats = fetchByUuid(this.premiumMatchesRepository, data.get("uuid").getAsString());

        if (practiceStats == null) {
            return new ResponseEntity<>(Constants.PLAYER_NOT_FOUND, HttpStatus.OK);
        }

        practiceStats.setUuid(data.get("uuid").getAsString());
        practiceStats.setPremiumElo(data.get("premium_elo").getAsInt());
        practiceStats.setPremiumMatchesExtra(data.get("premium_matches_extra").getAsInt());
        practiceStats.setPremiumMatchesPlayed(data.get("premium_matches_played").getAsInt());
        practiceStats.setPremiumWins(data.get("premium_wins").getAsInt());
        practiceStats.setPremiumLosses(data.get("premium_losses").getAsInt());
        
        this.premiumMatchesRepository.save(practiceStats);

        return new ResponseEntity<>(practiceStats.toJson().toString(), HttpStatus.OK);
    }

    @RequestMapping("/reset")
    public ResponseEntity<String> reset(@PathVariable(name = "key") String key) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        Iterable<PremiumMatches> premiumMatches = fetchFindAll(this.premiumMatchesRepository);
        Iterator<PremiumMatches> iterator = premiumMatches.iterator();

        iterator.forEachRemaining((premium) -> {
            premium.setPremiumMatchesExtra(0);
        });

        this.premiumMatchesRepository.save(premiumMatches);

        return new ResponseEntity<>(Constants.SUCCESS, HttpStatus.OK);
    }

    @RequestMapping("/{action}/{amount}")
    public ResponseEntity<String> premium(@PathVariable(name = "key") String key, @PathVariable("action") String action, @PathVariable("amount") int amount, HttpServletRequest request) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        JsonObject data = new JsonParser().parse(request.getParameter("data")).getAsJsonObject();

        PremiumMatches practiceStats = fetchByUuid(this.premiumMatchesRepository, data.get("uuid").getAsString());

        if (practiceStats == null) {
            return new ResponseEntity<>(Constants.PLAYER_NOT_FOUND, HttpStatus.OK);
        }

        int premiumMatches = data.get("premium_matches_extra").getAsInt();

        if (action.toLowerCase().equalsIgnoreCase("add")) {
            premiumMatches =+ amount;
        }
        else if(action.toLowerCase().equalsIgnoreCase("remove")) {
            premiumMatches =- amount;
        }
        else if(action.toLowerCase().equalsIgnoreCase("set")) {
            premiumMatches = amount;
        }
        else if(action.toLowerCase().equalsIgnoreCase("reset")) {
            premiumMatches = 0;
        }

        practiceStats.setPremiumMatchesExtra(premiumMatches);

        this.premiumMatchesRepository.save(practiceStats);

        return new ResponseEntity<>(practiceStats.toJson().toString(), HttpStatus.OK);
    }

    private static PremiumMatches fetchByUuid(PremiumMatchesRepository repository, String uuid) {
        PremiumMatches premiumMatches = repository.findFirstByUuid(uuid);

        if (premiumMatches == null) {
            premiumMatches = new PremiumMatches();

            premiumMatches.setUuid(uuid);
            premiumMatches.setPremiumWins(0);
            premiumMatches.setPremiumLosses(0);
            premiumMatches.setPremiumMatchesPlayed(0);
            premiumMatches.setPremiumMatchesExtra(0);
            premiumMatches.setPremiumElo(1000);

            repository.save(premiumMatches);
        }

        return premiumMatches;
    }

    private static Iterable<PremiumMatches> fetchFindAll(PremiumMatchesRepository repository) {
        return repository.findAll();
    }

}
