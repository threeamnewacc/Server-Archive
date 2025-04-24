package me.devkevin.practice.leaderboard.menu.button;

import club.inverted.chatcolor.CC;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCursor;
import lombok.RequiredArgsConstructor;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.menu.Button;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 10/03/2023 @ 15:19
 * LeaderboardGlobalButton / me.devkevin.practice.leaderboard.menu.button / Practice
 */
@RequiredArgsConstructor
public class LeaderboardGlobalButton extends Button {
    private final Material material;
    private final String document;
    private final String type;

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = Lists.newArrayList();
        AtomicInteger lineNum = new AtomicInteger();

        lore.add(CC.MENU_BAR);

        try (MongoCursor<Document> iterator = plugin.getProfileManager().getPlayersSortedByDocumentElo(this.document)) {
            while (iterator.hasNext()) {
                lineNum.getAndIncrement();
                try {
                    Document document = iterator.next();
                    if (document.getString("uuid") == null) {
                        continue;
                    }

                    UUID uuid = UUID.fromString(document.getString("uuid"));
                    Document stat = (Document) document.get("global");
                    int statElo = stat.getInteger(this.document);

                    lore.add(CC.YELLOW + "#" + lineNum + " " + CC.GOLD + (Bukkit.getOfflinePlayer(uuid).getName() != null ? Bukkit.getOfflinePlayer(uuid).getName() : "???") + CC.GRAY + " (" + statElo + ")");
                } catch (Exception ignored) {
                }
            }
        }

        lore.add(CC.MENU_BAR);

        return new ItemBuilder(material).name(CC.GOLD + "Global ELO " + CC.GRAY + "(Top 10)").lore(lore).durability(0).build();
    }
}
