package me.devkevin.practice.leaderboard.menu.button;

import club.inverted.chatcolor.CC;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCursor;
import lombok.AllArgsConstructor;
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
 * 10/03/2023 @ 15:56
 * WinstreakGlobalButton / me.devkevin.practice.leaderboard.menu.button / Practice
 */
@AllArgsConstructor
public class WinstreakGlobalButton extends Button {

    private final Material material;
    private final String document;

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
                    Document winstreak = (Document) document.get("global");
                    int winstreakInt = winstreak.getInteger(this.document);

                    lore.add(CC.YELLOW + "#" + lineNum + " " + CC.GOLD + (Bukkit.getOfflinePlayer(uuid).getName() != null ? Bukkit.getOfflinePlayer(uuid).getName() : "???") + CC.GRAY + " (" + winstreakInt + ")");
                } catch (Exception ignored) {
                }
            }
        }

        lore.add(CC.MENU_BAR);

        return new ItemBuilder(material).name(CC.GOLD + "Global Winstreak " + CC.GRAY + "(Top 10)").lore(lore).durability(0).build();
    }
}
