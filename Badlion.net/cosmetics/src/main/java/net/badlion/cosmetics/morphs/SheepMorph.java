package net.badlion.cosmetics.morphs;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.utils.MorphUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.*;

public class SheepMorph extends Morph {

    public Map<UUID, Long> lastSheepSneakTimes = new HashMap<>();

    public SheepMorph() {
        super("sheep_morph", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, 1, (byte) 91, ChatColor.GREEN + "Sheep Morph",
                Arrays.asList(ChatColor.GRAY + "Left click to baa.", ChatColor.GRAY + "Shift to sheer.")));
        this.morphType = MorphUtil.MorphType.SHEEP;
    }

    @Override
    protected void handleLeftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "SHEEP_IDLE", "ENTITY_SHEEP_AMBIENT"), 1.0f, 1.0f);
    }

    @Override
    public void handleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!event.isSneaking()) return;
        if (lastSheepSneakTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - lastSheepSneakTimes.get(player.getUniqueId()) <= 1000 * 10) {
            player.sendMessage(ChatColor.RED + "Please wait " + (10 - (Math.round(System.currentTimeMillis() - lastSheepSneakTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
            return;
        }
        final ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i <= 16; i++) {
            double x = -0.5F + (float) (Math.random() * 1.0D);
            double z = -0.5F + (float) (Math.random() * 1.0D);
            Item wool = player.getWorld().dropItem(player.getLocation().add(0.0D, 0.5D, 0.0D), ItemStackUtil.createItem(Material.WOOL, 1, (short) (Math.random() * 14.0D), String.valueOf(Math.random() * 100.0D)));
            wool.setMetadata("takeable", new FixedMetadataValue(Cosmetics.getInstance(), "takeable"));
            wool.setVelocity(new Vector(x, 1.0D, z));
            items.add(wool);
            player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "SHEEP_SHEAR", "ENTITY_SHEEP_SHEAR"), 20.0f, 1.0f);
        }
        Bukkit.getScheduler().runTaskLater(Cosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Item item : items) {
                    item.remove();
                }
            }
        }, 20L * 5);
        lastSheepSneakTimes.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @Override
    public void setMorph(Player player) {
        new MorphUtil(MorphUtil.MorphType.SHEEP, player).sendServerSetMorph();
    }
}
