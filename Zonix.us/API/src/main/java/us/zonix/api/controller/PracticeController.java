package us.zonix.api.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.zonix.api.model.PracticeMatch;
import us.zonix.api.model.PracticeStats;
import us.zonix.api.model.PremiumMatches;
import us.zonix.api.repository.PracticeMatchRepository;
import us.zonix.api.repository.PracticeStatsRepository;
import us.zonix.api.repository.PremiumMatchesRepository;
import us.zonix.api.util.Constants;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.UUID;

@RestController
@RequestMapping("/api/{key}/practice")
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
public class PracticeController {

    @Autowired private PracticeStatsRepository practiceStatsRepository;
    @Autowired private PremiumMatchesRepository premiumMatchesRepository;
    @Autowired private PracticeMatchRepository practiceMatchRepository;

    @RequestMapping("/fetch_by_uuid/{uuid}")
    public PracticeStats fetchByUuid(@PathVariable("key") String key, @PathVariable("uuid") String uuid) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        return fetchByUuid(this.practiceStatsRepository, uuid);
    }

    @RequestMapping("/match/fetch_by_uuid/{uuid}")
    public ResponseEntity<String> fetchByUuid(@PathVariable("key") String key, @PathVariable("uuid") UUID uuid) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        JsonArray matches = new JsonArray();

        for (PracticeMatch match : this.practiceMatchRepository.findTop10ByWinnerUuidOrLoserUuidOrderByTimestampDesc(uuid.toString(), uuid.toString())) {
            matches.add(match.toJson());
        }

        return new ResponseEntity<>(matches.toString(), HttpStatus.OK);
    }

    @RequestMapping("/reset")
    public ResponseEntity<String> reset(@PathVariable(name = "key") String key, HttpServletRequest request) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        if (request.getParameter("uuid") == null) {
            return null;
        }

        String uuid = request.getParameter("uuid");

        PracticeStats practiceStats = fetchByUuid(this.practiceStatsRepository, uuid);

        if (practiceStats == null) {
            return new ResponseEntity<>(Constants.PLAYER_NOT_FOUND, HttpStatus.OK);
        }

        practiceStats.setArcherElo(1000);
        practiceStats.setArcherEloParty(1000);
        practiceStats.setArcherWins(0);
        practiceStats.setArcherLosses(0);

        practiceStats.setAxeElo(1000);
        practiceStats.setAxeEloParty(1000);
        practiceStats.setAxeWins(0);
        practiceStats.setAxeLosses(0);

        practiceStats.setBuilduhcElo(1000);
        practiceStats.setBuilduhcEloParty(1000);
        practiceStats.setBuilduhcWins(0);
        practiceStats.setBuilduhcLosses(0);

        practiceStats.setClassicElo(1000);
        practiceStats.setClassicEloParty(1000);
        practiceStats.setClassicWins(0);
        practiceStats.setClassicLosses(0);

        practiceStats.setComboElo(1000);
        practiceStats.setComboEloParty(1000);
        practiceStats.setComboWins(0);
        practiceStats.setComboLosses(0);

        practiceStats.setSumoElo(1000);
        practiceStats.setSumoEloParty(1000);
        practiceStats.setSumoWins(0);
        practiceStats.setSumoLosses(0);

        practiceStats.setSkywarsElo(1000);
        practiceStats.setSkywarsEloParty(1000);
        practiceStats.setSkywarsWins(0);
        practiceStats.setSkywarsLosses(0);

        practiceStats.setSoupElo(1000);
        practiceStats.setSoupEloParty(1000);
        practiceStats.setSoupWins(0);
        practiceStats.setSoupLosses(0);

        practiceStats.setGappleElo(1000);
        practiceStats.setGappleEloParty(1000);
        practiceStats.setGappleWins(0);
        practiceStats.setGappleLosses(0);

        practiceStats.setSpleefElo(1000);
        practiceStats.setSpleefEloParty(1000);
        practiceStats.setSpleefWins(0);
        practiceStats.setSpleefLosses(0);

        practiceStats.setHcfElo(1000);
        practiceStats.setHcfEloParty(1000);
        practiceStats.setHcfWins(0);
        practiceStats.setHcfLosses(0);

        practiceStats.setVanillaElo(1000);
        practiceStats.setVanillaEloParty(1000);
        practiceStats.setVanillaWins(0);
        practiceStats.setVanillaLosses(0);

        practiceStats.setNodebuffElo(1000);
        practiceStats.setNodebuffEloParty(1000);
        practiceStats.setNodebuffWins(0);
        practiceStats.setNodebuffLosses(0);

        practiceStats.setDebuffElo(1000);
        practiceStats.setDebuffEloParty(1000);
        practiceStats.setDebuffWins(0);
        practiceStats.setDebuffLosses(0);

        this.practiceStatsRepository.save(practiceStats);

        return new ResponseEntity<>(Constants.SUCCESS, HttpStatus.OK);
    }

    @RequestMapping("/update")
    public ResponseEntity<String> update(@PathVariable(name = "key") String key, HttpServletRequest request) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        JsonObject data = new JsonParser().parse(request.getParameter("data")).getAsJsonObject();

        PracticeStats practiceStats = fetchByUuid(this.practiceStatsRepository, data.get("uuid").getAsString());

        if (practiceStats == null) {
            return new ResponseEntity<>(Constants.PLAYER_NOT_FOUND, HttpStatus.OK);
        }

        practiceStats.setUuid(data.get("uuid").getAsString());
        practiceStats.setPingRange(data.get("ping_range").getAsInt());
        practiceStats.setEloRange(data.get("elo_range").getAsInt());

        practiceStats.setSumoEventWins(data.get("sumo_event_wins").getAsInt());
        practiceStats.setSumoEventLosses(data.get("sumo_event_losses").getAsInt());

        practiceStats.setParkourEventWins(data.get("parkour_event_wins").getAsInt());
        practiceStats.setParkourEventLosses(data.get("parkour_event_losses").getAsInt());
        practiceStats.setRedroverEventWins(data.get("redrover_event_wins").getAsInt());
        practiceStats.setRedroverEventLosses(data.get("redrover_event_losses").getAsInt());

        practiceStats.setOitcEventWins(data.get("oitc_event_wins").getAsInt());
        practiceStats.setOitcEventLosses(data.get("oitc_event_losses").getAsInt());
        practiceStats.setOitcEventKills(data.get("oitc_event_kills").getAsInt());
        practiceStats.setOitcEventDeaths(data.get("oitc_event_deaths").getAsInt());

        practiceStats.setNodebuffEloParty(data.get("nodebuff_elo_party").getAsInt());
        practiceStats.setNodebuffElo(data.get("nodebuff_elo").getAsInt());
        practiceStats.setNodebuffWins(data.get("nodebuff_wins").getAsInt());
        practiceStats.setNodebuffLosses(data.get("nodebuff_losses").getAsInt());

        practiceStats.setDebuffEloParty(data.get("debuff_elo_party").getAsInt());
        practiceStats.setDebuffElo(data.get("debuff_elo").getAsInt());
        practiceStats.setDebuffWins(data.get("debuff_wins").getAsInt());
        practiceStats.setDebuffLosses(data.get("debuff_losses").getAsInt());
        
        practiceStats.setGappleEloParty(data.get("gapple_elo_party").getAsInt());
        practiceStats.setGappleElo(data.get("gapple_elo").getAsInt());
        practiceStats.setGappleWins(data.get("gapple_wins").getAsInt());
        practiceStats.setGappleLosses(data.get("gapple_losses").getAsInt());

        practiceStats.setArcherEloParty(data.get("archer_elo_party").getAsInt());
        practiceStats.setArcherElo(data.get("archer_elo").getAsInt());
        practiceStats.setArcherWins(data.get("archer_wins").getAsInt());
        practiceStats.setArcherLosses(data.get("archer_losses").getAsInt());

        practiceStats.setClassicEloParty(data.get("classic_elo_party").getAsInt());
        practiceStats.setClassicElo(data.get("classic_elo").getAsInt());
        practiceStats.setClassicWins(data.get("classic_wins").getAsInt());
        practiceStats.setClassicLosses(data.get("classic_losses").getAsInt());

        practiceStats.setAxeEloParty(data.get("axe_elo_party").getAsInt());
        practiceStats.setAxeElo(data.get("axe_elo").getAsInt());
        practiceStats.setAxeWins(data.get("axe_wins").getAsInt());
        practiceStats.setAxeLosses(data.get("axe_losses").getAsInt());

        practiceStats.setVanillaEloParty(data.get("vanilla_elo_party").getAsInt());
        practiceStats.setVanillaElo(data.get("vanilla_elo").getAsInt());
        practiceStats.setVanillaWins(data.get("vanilla_wins").getAsInt());
        practiceStats.setVanillaLosses(data.get("vanilla_losses").getAsInt());

        practiceStats.setSoupEloParty(data.get("soup_elo_party").getAsInt());
        practiceStats.setSoupElo(data.get("soup_elo").getAsInt());
        practiceStats.setSoupWins(data.get("soup_wins").getAsInt());
        practiceStats.setSoupLosses(data.get("soup_losses").getAsInt());

        practiceStats.setSumoEloParty(data.get("sumo_elo_party").getAsInt());
        practiceStats.setSumoElo(data.get("sumo_elo").getAsInt());
        practiceStats.setSumoWins(data.get("sumo_wins").getAsInt());
        practiceStats.setSumoLosses(data.get("sumo_losses").getAsInt());

        practiceStats.setSpleefEloParty(data.get("spleef_elo_party").getAsInt());
        practiceStats.setSpleefElo(data.get("spleef_elo").getAsInt());
        practiceStats.setSpleefWins(data.get("spleef_wins").getAsInt());
        practiceStats.setSpleefLosses(data.get("spleef_losses").getAsInt());

        practiceStats.setComboEloParty(data.get("combo_elo_party").getAsInt());
        practiceStats.setComboElo(data.get("combo_elo").getAsInt());
        practiceStats.setComboWins(data.get("combo_wins").getAsInt());
        practiceStats.setComboLosses(data.get("combo_losses").getAsInt());

        practiceStats.setBuilduhcEloParty(data.get("builduhc_elo_party").getAsInt());
        practiceStats.setBuilduhcElo(data.get("builduhc_elo").getAsInt());
        practiceStats.setBuilduhcWins(data.get("builduhc_wins").getAsInt());
        practiceStats.setBuilduhcLosses(data.get("builduhc_losses").getAsInt());

        practiceStats.setHcfEloParty(data.get("hcf_elo_party").getAsInt());
        practiceStats.setHcfElo(data.get("hcf_elo").getAsInt());
        practiceStats.setHcfWins(data.get("hcf_wins").getAsInt());
        practiceStats.setHcfLosses(data.get("hcf_losses").getAsInt());

        practiceStats.setSkywarsEloParty(data.get("skywars_elo_party").getAsInt());
        practiceStats.setSkywarsElo(data.get("skywars_elo").getAsInt());
        practiceStats.setSkywarsWins(data.get("skywars_wins").getAsInt());
        practiceStats.setSkywarsLosses(data.get("skywars_losses").getAsInt());

        this.practiceStatsRepository.save(practiceStats);

        return new ResponseEntity<>(practiceStats.toJson().toString(), HttpStatus.OK);
    }

    private static PracticeStats fetchByUuid(PracticeStatsRepository repository, String uuid) {
        PracticeStats practiceStats = repository.findFirstByUuid(uuid);

        if (practiceStats == null) {
            practiceStats = new PracticeStats();

            practiceStats.setUuid(uuid);
            practiceStats.setPingRange(-1);
            practiceStats.setEloRange(-1);

            practiceStats.setOitcEventWins(0);
            practiceStats.setOitcEventLosses(0);
            practiceStats.setOitcEventKills(0);
            practiceStats.setOitcEventDeaths(0);
            practiceStats.setRedroverEventLosses(0);
            practiceStats.setRedroverEventWins(0);
            practiceStats.setParkourEventWins(0);
            practiceStats.setParkourEventLosses(0);
            practiceStats.setSumoEventWins(0);
            practiceStats.setSumoLosses(0);

            practiceStats.setNodebuffEloParty(1000);
            practiceStats.setNodebuffElo(1000);
            practiceStats.setNodebuffWins(0);
            practiceStats.setNodebuffLosses(0);

            practiceStats.setDebuffEloParty(1000);
            practiceStats.setDebuffElo(1000);
            practiceStats.setDebuffWins(0);
            practiceStats.setDebuffLosses(0);

            practiceStats.setGappleEloParty(1000);
            practiceStats.setGappleElo(1000);
            practiceStats.setGappleWins(0);
            practiceStats.setGappleLosses(0);

            practiceStats.setArcherEloParty(1000);
            practiceStats.setArcherElo(1000);
            practiceStats.setArcherWins(0);
            practiceStats.setArcherLosses(0);

            practiceStats.setClassicEloParty(1000);
            practiceStats.setClassicElo(1000);
            practiceStats.setClassicWins(0);
            practiceStats.setClassicLosses(0);

            practiceStats.setAxeEloParty(1000);
            practiceStats.setAxeElo(1000);
            practiceStats.setAxeWins(0);
            practiceStats.setAxeLosses(0);

            practiceStats.setVanillaEloParty(1000);
            practiceStats.setVanillaElo(1000);
            practiceStats.setVanillaWins(0);
            practiceStats.setVanillaLosses(0);

            practiceStats.setSoupEloParty(1000);
            practiceStats.setSoupElo(1000);
            practiceStats.setSoupWins(0);
            practiceStats.setSoupLosses(0);

            practiceStats.setSumoEloParty(1000);
            practiceStats.setSumoElo(1000);
            practiceStats.setSumoWins(0);
            practiceStats.setSumoLosses(0);

            practiceStats.setSpleefEloParty(1000);
            practiceStats.setSpleefElo(1000);
            practiceStats.setSpleefWins(0);
            practiceStats.setSpleefLosses(0);

            practiceStats.setComboEloParty(1000);
            practiceStats.setComboElo(1000);
            practiceStats.setComboWins(0);
            practiceStats.setComboLosses(0);

            practiceStats.setBuilduhcEloParty(1000);
            practiceStats.setBuilduhcElo(1000);
            practiceStats.setBuilduhcWins(0);
            practiceStats.setBuilduhcLosses(0);

            practiceStats.setHcfEloParty(1000);
            practiceStats.setHcfElo(1000);
            practiceStats.setHcfWins(0);
            practiceStats.setHcfLosses(0);

            practiceStats.setSkywarsEloParty(1000);
            practiceStats.setSkywarsElo(1000);
            practiceStats.setSkywarsWins(0);
            practiceStats.setSkywarsLosses(0);


            repository.save(practiceStats);
        }

        return practiceStats;
    }

    @RequestMapping("/match/insert/")
    public ResponseEntity<String> insert(@PathVariable("key") String key, HttpServletRequest request) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        JsonObject data = new JsonParser().parse(request.getParameter("data")).getAsJsonObject();

        PracticeMatch match = new PracticeMatch();
        match.setWinnerUuid(data.get("winner_uuid").getAsString());
        match.setLoserUuid(data.get("loser_uuid").getAsString());
        match.setWinnerEloChange(data.get("winner_elo_change").getAsInt());
        match.setWinnerNewElo(data.get("winner_new_elo").getAsInt());
        match.setLoserEloChange(data.get("loser_elo_change").getAsInt());
        match.setLoserNewElo(data.get("loser_new_elo").getAsInt());
        match.setTimestamp(new Timestamp(System.currentTimeMillis()));

        practiceMatchRepository.save(match);

        return new ResponseEntity<>(match.toJson().toString(), HttpStatus.OK);
    }

    @RequestMapping("/fetch_leaderboards")
    public ResponseEntity<String> fetchLeaderboards(@PathVariable(name = "key") String key) {
        if (!Constants.isValidKey(key)) {
            return null;
        }

        JsonArray premiumEntries = new JsonArray();
        JsonArray nodebuffEntries = new JsonArray();
        JsonArray debuffEntries = new JsonArray();
        JsonArray comboEntries = new JsonArray();
        JsonArray gappleEntries = new JsonArray();
        JsonArray soupEntries = new JsonArray();
        JsonArray archerEntries = new JsonArray();
        JsonArray sumoEntries = new JsonArray();
        JsonArray builduhcEntries = new JsonArray();
        JsonArray vanillaEntries = new JsonArray();
        JsonArray spleefEntries = new JsonArray();
        JsonArray axeEntries = new JsonArray();
        JsonArray classicEntries = new JsonArray();
        JsonArray hcfEntries = new JsonArray();
        JsonArray skywarsEntries = new JsonArray();

        int ranking = 1;

        for (PremiumMatches premium : this.premiumMatchesRepository.findAll(new PageRequest(0, 10, Sort.Direction.DESC, "premiumElo"))) {
            JsonObject entry = new JsonObject();
            entry.addProperty("ranking", ranking);
            entry.addProperty("uuid", premium.getUuid());
            entry.addProperty("elo", premium.getPremiumElo());
            premiumEntries.add(entry);

            ranking++;
        }

        ranking = 1;

        for (PracticeStats practiceStats : this.practiceStatsRepository.findAll(new PageRequest(0, 10, Sort.Direction.DESC, "nodebuffElo"))) {
            JsonObject entry = new JsonObject();
            entry.addProperty("ranking", ranking);
            entry.addProperty("uuid", practiceStats.getUuid());
            entry.addProperty("elo", practiceStats.getNodebuffElo());
            nodebuffEntries.add(entry);

            ranking++;
        }

        ranking = 1;

        for (PracticeStats practiceStats : this.practiceStatsRepository.findAll(new PageRequest(0, 10, Sort.Direction.DESC, "debuffElo"))) {
            JsonObject entry = new JsonObject();
            entry.addProperty("ranking", ranking);
            entry.addProperty("uuid", practiceStats.getUuid());
            entry.addProperty("elo", practiceStats.getDebuffElo());
            debuffEntries.add(entry);

            ranking++;
        }

        ranking = 1;

        for (PracticeStats practiceStats : this.practiceStatsRepository.findAll(new PageRequest(0, 10, Sort.Direction.DESC, "comboElo"))) {
            JsonObject entry = new JsonObject();
            entry.addProperty("ranking", ranking);
            entry.addProperty("uuid", practiceStats.getUuid());
            entry.addProperty("elo", practiceStats.getComboElo());
            comboEntries.add(entry);

            ranking++;
        }

        ranking = 1;

        for (PracticeStats practiceStats : this.practiceStatsRepository.findAll(new PageRequest(0, 10, Sort.Direction.DESC, "gappleElo"))) {
            JsonObject entry = new JsonObject();
            entry.addProperty("ranking", ranking);
            entry.addProperty("uuid", practiceStats.getUuid());
            entry.addProperty("elo", practiceStats.getGappleElo());
            gappleEntries.add(entry);

            ranking++;
        }

        ranking = 1;

        for (PracticeStats practiceStats : this.practiceStatsRepository.findAll(new PageRequest(0, 10, Sort.Direction.DESC, "soupElo"))) {
            JsonObject entry = new JsonObject();
            entry.addProperty("ranking", ranking);
            entry.addProperty("uuid", practiceStats.getUuid());
            entry.addProperty("elo", practiceStats.getSoupElo());
            soupEntries.add(entry);

            ranking++;
        }

        ranking = 1;

        for (PracticeStats practiceStats : this.practiceStatsRepository.findAll(new PageRequest(0, 10, Sort.Direction.DESC, "archerElo"))) {
            JsonObject entry = new JsonObject();
            entry.addProperty("ranking", ranking);
            entry.addProperty("uuid", practiceStats.getUuid());
            entry.addProperty("elo", practiceStats.getArcherElo());
            archerEntries.add(entry);

            ranking++;
        }

        ranking = 1;

        for (PracticeStats practiceStats : this.practiceStatsRepository.findAll(new PageRequest(0, 10, Sort.Direction.DESC, "sumoElo"))) {
            JsonObject entry = new JsonObject();
            entry.addProperty("ranking", ranking);
            entry.addProperty("uuid", practiceStats.getUuid());
            entry.addProperty("elo", practiceStats.getSumoElo());
            sumoEntries.add(entry);

            ranking++;
        }

        ranking = 1;

        for (PracticeStats practiceStats : this.practiceStatsRepository.findAll(new PageRequest(0, 10, Sort.Direction.DESC, "builduhcElo"))) {
            JsonObject entry = new JsonObject();
            entry.addProperty("ranking", ranking);
            entry.addProperty("uuid", practiceStats.getUuid());
            entry.addProperty("elo", practiceStats.getBuilduhcElo());
            builduhcEntries.add(entry);

            ranking++;
        }

        ranking = 1;

        for (PracticeStats practiceStats : this.practiceStatsRepository.findAll(new PageRequest(0, 10, Sort.Direction.DESC, "vanillaElo"))) {
            JsonObject entry = new JsonObject();
            entry.addProperty("ranking", ranking);
            entry.addProperty("uuid", practiceStats.getUuid());
            entry.addProperty("elo", practiceStats.getVanillaElo());
            vanillaEntries.add(entry);

            ranking++;
        }

        ranking = 1;

        for (PracticeStats practiceStats : this.practiceStatsRepository.findAll(new PageRequest(0, 10, Sort.Direction.DESC, "spleefElo"))) {
            JsonObject entry = new JsonObject();
            entry.addProperty("ranking", ranking);
            entry.addProperty("uuid", practiceStats.getUuid());
            entry.addProperty("elo", practiceStats.getSpleefElo());
            spleefEntries.add(entry);

            ranking++;
        }

        ranking = 1;

        for (PracticeStats practiceStats : this.practiceStatsRepository.findAll(new PageRequest(0, 10, Sort.Direction.DESC, "axeElo"))) {
            JsonObject entry = new JsonObject();
            entry.addProperty("ranking", ranking);
            entry.addProperty("uuid", practiceStats.getUuid());
            entry.addProperty("elo", practiceStats.getAxeElo());
            axeEntries.add(entry);

            ranking++;
        }

        ranking = 1;

        for (PracticeStats practiceStats : this.practiceStatsRepository.findAll(new PageRequest(0, 10, Sort.Direction.DESC, "classicElo"))) {
            JsonObject entry = new JsonObject();
            entry.addProperty("ranking", ranking);
            entry.addProperty("uuid", practiceStats.getUuid());
            entry.addProperty("elo", practiceStats.getClassicElo());
            classicEntries.add(entry);

            ranking++;
        }

        for (PracticeStats practiceStats : this.practiceStatsRepository.findAll(new PageRequest(0, 10, Sort.Direction.DESC, "hcfElo"))) {
            JsonObject entry = new JsonObject();
            entry.addProperty("ranking", ranking);
            entry.addProperty("uuid", practiceStats.getUuid());
            entry.addProperty("elo", practiceStats.getHcfElo());
            hcfEntries.add(entry);

            ranking++;
        }

        for (PracticeStats practiceStats : this.practiceStatsRepository.findAll(new PageRequest(0, 10, Sort.Direction.DESC, "skywarsElo"))) {
            JsonObject entry = new JsonObject();
            entry.addProperty("ranking", ranking);
            entry.addProperty("uuid", practiceStats.getUuid());
            entry.addProperty("elo", practiceStats.getSkywarsElo());
            skywarsEntries.add(entry);

            ranking++;
        }

        JsonObject premium = new JsonObject();
        premium.addProperty("type", "Premium");
        premium.add("entries", premiumEntries);

        JsonObject nodebuff = new JsonObject();
        nodebuff.addProperty("type", "NoDebuff");
        nodebuff.add("entries", nodebuffEntries);

        JsonObject debuff = new JsonObject();
        debuff.addProperty("type", "Debuff");
        debuff.add("entries", debuffEntries);

        JsonObject combo = new JsonObject();
        combo.addProperty("type", "Combo");
        combo.add("entries", comboEntries);

        JsonObject gapple = new JsonObject();
        gapple.addProperty("type", "Gapple");
        gapple.add("entries", gappleEntries);

        JsonObject soup = new JsonObject();
        soup.addProperty("type", "Soup");
        soup.add("entries", soupEntries);

        JsonObject archer = new JsonObject();
        archer.addProperty("type", "Archer");
        archer.add("entries", archerEntries);

        JsonObject sumo = new JsonObject();
        sumo.addProperty("type", "Sumo");
        sumo.add("entries", sumoEntries);

        JsonObject builduhc = new JsonObject();
        builduhc.addProperty("type", "BuildUHC");
        builduhc.add("entries", builduhcEntries);

        JsonObject vanilla = new JsonObject();
        vanilla.addProperty("type", "Vanilla");
        vanilla.add("entries", vanillaEntries);

        JsonObject spleef = new JsonObject();
        spleef.addProperty("type", "Spleef");
        spleef.add("entries", spleefEntries);

        JsonObject axe = new JsonObject();
        axe.addProperty("type", "Axe");
        axe.add("entries", axeEntries);

        JsonObject classic = new JsonObject();
        classic.addProperty("type", "Classic");
        classic.add("entries", classicEntries);

        JsonObject hcf = new JsonObject();
        classic.addProperty("type", "HCF");
        classic.add("entries", hcfEntries);

        JsonObject skywars = new JsonObject();
        classic.addProperty("type", "SkyWars");
        classic.add("entries", skywarsEntries);

        JsonObject ladders = new JsonObject();
        ladders.add("Premium", premium);
        ladders.add("NoDebuff", nodebuff);
        ladders.add("Debuff", debuff);
        ladders.add("Combo", combo);
        ladders.add("Gapple", gapple);
        ladders.add("Soup", soup);
        ladders.add("Archer", archer);
        ladders.add("Sumo", sumo);
        ladders.add("BuildUHC", builduhc);
        ladders.add("Vanilla", vanilla);
        ladders.add("Spleef", spleef);
        ladders.add("Axe", axe);
        ladders.add("Classic", classic);
        ladders.add("HCF", hcf);
        ladders.add("SkyWars", skywars);

        return new ResponseEntity<>(ladders.toString(), HttpStatus.OK);
    }

}
