package us.zonix.hcfactions.statracker;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Accessors(chain = true)
public class StatTracker {

    private static SimpleDateFormat format = new SimpleDateFormat("MM.dd.yy HH:mm");

    @Getter private StatTrackerType type;
    @Getter private int count;
    @Getter private final ItemStack itemStack;

    public StatTracker(ItemStack itemStack, StatTrackerType type) {
        this.itemStack = itemStack;
        this.type = type;

        ItemMeta meta = itemStack.getItemMeta();

        if (!(meta.hasLore())) {
            count = 0;
        } else {
            count = meta.getLore().size() - 3;
        }
    }

    public StatTracker add(String killer, String killed) {
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();

        if (count == 0) {
            lore.add(" ");
            lore.add(type.getHeader().replace("%COUNT%", count + 1 + ""));
            lore.add(" ");
            lore.add(type.getLine().replace("%KILLER%", killer).replace("%PLAYER%", killed).replace("%TIME%", format.format(new Date())));
        } else {
            lore = meta.getLore();

            if (count == 1) {
                lore.add(4, lore.get(3));
            } else if (count == 2) {
                lore.add(5, lore.get(4));
                lore.set(4, lore.get(3));
            } else {
                lore.set(5, lore.get(4));
                lore.set(4, lore.get(3));
            }

            lore.set(3, type.getLine().replace("%KILLER%", killer).replace("%PLAYER%", killed).replace("%TIME%", format.format(new Date())));
            lore.set(1, type.getHeader().replace("%COUNT%", Integer.parseInt(ChatColor.stripColor(lore.get(1)).replaceAll("[^0-9]", "")) + 1 + ""));
        }

        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        return this;
    }

}
