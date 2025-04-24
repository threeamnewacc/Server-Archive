package me.devkevin.practice.match.history;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.devkevin.practice.Practice;
import me.devkevin.practice.util.InventoryUtil;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Copyright 02/01/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter @Setter @NoArgsConstructor
public class MatchLocatedData {

    private final Practice plugin = Practice.getInstance();

    private String id;

    private UUID winnerUUID;
    private UUID loserUUID;

    private int winnerEloModifier;
    private int loserEloModifier;

    private int winnerElo;
    private int loserElo;

    private String date;
    private String kit;

    private ItemStack[] winnerArmor;
    private ItemStack[] winnerContents;

    private ItemStack[] loserArmor;
    private ItemStack[] loserContents;

    // new
    private int missedPotsWinner, missedPotsLoser;
    private int thrownPotsWinner, thrownPotsLoser;
    private int longestComboWinner, longestComboLoser;
    private int comboWinner, comboLoser;
    private int hitsWinner, hitsLoser;
    private double potionAccuracyWinner, potionAccuracyLoser;

    private String matchDuration;
    private String arenaName;

    private MatchHistoryInvSnap matchHistoryInvSnap;

    public MatchLocatedData(Document document) {
        this.id = document.getString("id");

        this.winnerUUID = UUID.fromString(document.getString("winnerUuid"));
        this.loserUUID = UUID.fromString(document.getString("loserUuid"));

        this.winnerEloModifier = document.getInteger("winnerEloModifier");
        this.loserEloModifier = document.getInteger("loserEloModifier");

        this.winnerElo = document.getInteger("winnerElo");
        this.loserElo = document.getInteger("loserElo");

        this.date = document.getString("date");
        this.kit = document.getString("kit");

        this.winnerArmor = InventoryUtil.deserializeInventory(document.getString("winnerArmor"));
        this.winnerContents = InventoryUtil.deserializeInventory(document.getString("winnerContents"));

        this.loserArmor = InventoryUtil.deserializeInventory(document.getString("loserArmor"));
        this.loserContents = InventoryUtil.deserializeInventory(document.getString("loserContents"));

        this.missedPotsWinner = document.getInteger("missedPotsWinner");
        this.missedPotsLoser = document.getInteger("missedPotsLoser");

        this.thrownPotsWinner = document.getInteger("thrownPotsWinner");
        this.thrownPotsLoser = document.getInteger("thrownPotsLoser");

        this.longestComboWinner = document.getInteger("longestComboWinner");
        this.longestComboLoser = document.getInteger("longestComboLoser");

        this.comboWinner = document.getInteger("comboWinner");
        this.comboLoser = document.getInteger("comboLoser");

        this.hitsWinner = document.getInteger("hitsWinner");
        this.hitsLoser = document.getInteger("hitsLoser");

        this.potionAccuracyWinner = document.getDouble("potionAccuracyWinner");
        this.potionAccuracyLoser = document.getDouble("potionAccuracyLoser");

        this.matchDuration = document.getString("matchDuration");
        this.arenaName = document.getString("arenaName");

        this.matchHistoryInvSnap = new MatchHistoryInvSnap(this);
    }

    public void save() {
        Document document = new Document();
        document.put("id", this.id);

        document.put("winnerUuid", this.winnerUUID.toString());
        document.put("loserUuid", this.loserUUID.toString());

        document.put("winnerEloModifier", this.winnerEloModifier);
        document.put("loserEloModifier", this.loserEloModifier);

        document.put("winnerElo", this.winnerElo);
        document.put("loserElo", this.loserElo);

        document.put("date", this.date);
        document.put("kit", this.kit);

        document.put("winnerArmor", InventoryUtil.serializeInventory(this.winnerArmor));
        document.put("winnerContents", InventoryUtil.serializeInventory(this.winnerContents));

        document.put("loserArmor", InventoryUtil.serializeInventory(this.loserArmor));
        document.put("loserContents", InventoryUtil.serializeInventory(this.loserContents));

        // new
        document.put("missedPotsWinner", this.missedPotsWinner);
        document.put("missedPotsLoser", this.missedPotsLoser);

        document.put("thrownPotsWinner", this.thrownPotsWinner);
        document.put("thrownPotsLoser", this.thrownPotsLoser);

        document.put("longestComboWinner", this.longestComboWinner);
        document.put("longestComboLoser", this.longestComboLoser);

        document.put("comboWinner", this.comboWinner);
        document.put("comboLoser", this.comboLoser);

        document.put("hitsWinner", this.hitsWinner);
        document.put("hitsLoser", this.hitsLoser);

        document.put("potionAccuracyWinner", this.potionAccuracyWinner);
        document.put("potionAccuracyLoser", this.potionAccuracyLoser);

        document.put("matchDuration", this.matchDuration);
        document.put("arenaName", this.arenaName);

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            plugin.getPracticeDatabase().getMatchHistory().replaceOne(Filters.eq("id", this.id), document, (new UpdateOptions()).upsert(true));
        });
    }

    public List<MatchLocatedData> getMatchesByUser(UUID uuid) {
        List<MatchLocatedData> locatedData = new ArrayList<>();
        List<Document> documents = this.plugin.getPracticeDatabase().getMatchHistory().find().into(new ArrayList<>());

        for (Document document : documents) {
            if (document.getString("winnerUuid").equalsIgnoreCase(uuid.toString()) || document.getString("loserUuid").equalsIgnoreCase(uuid.toString())) {
                locatedData.add(new MatchLocatedData(document));
            }
        }

        return locatedData;
    }
}
