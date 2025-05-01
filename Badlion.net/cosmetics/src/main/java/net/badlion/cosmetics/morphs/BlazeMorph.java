package net.badlion.cosmetics.morphs;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.managers.FlightGCheatManager;
import net.badlion.cosmetics.utils.MorphUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlazeMorph extends Morph {

    public Map<UUID, Long> lastBlazeShootTimes = new HashMap<>();
    public Map<UUID, Long> lastBlazeFlyTimes = new HashMap<>();

    public BlazeMorph() {
        super("blaze_morph", ItemRarity.COMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, 1, (byte) 61, ChatColor.GREEN + "Blaze Morph",
                ChatColor.GRAY + "Right click to screech.", ChatColor.GRAY + "Left click to shoot a fireball.",
                ChatColor.GRAY + "Shift to fly."));
        this.morphType = MorphUtil.MorphType.BLAZE;
    }

    @Override
    protected void handleLeftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (lastBlazeShootTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - lastBlazeShootTimes.get(player.getUniqueId()) <= 1000 * 5) {
            player.sendMessage(ChatColor.RED + "Please wait " + (5 - (Math.round(System.currentTimeMillis() - lastBlazeShootTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
            return;
        }
        Fireball fireball = player.getWorld().spawn(player.getLocation().add(player.getLocation().getDirection().multiply(2)).add(0.0D, 1.0D, 0.0D), Fireball.class);
        fireball.setShooter(player);
        fireball.setMetadata("morphexplosion", new FixedMetadataValue(Cosmetics.getInstance(), "morphexplosion"));
        fireball.setVelocity(player.getLocation().getDirection().multiply(3));

        lastBlazeShootTimes.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @Override
    protected void handleRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "BLAZE_DEATH", "ENTITY_BLAZE_DEATH"), 1.0f, 1.0f);
    }

    @Override
    public void handleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!event.isSneaking()) return;
        if (lastBlazeFlyTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - lastBlazeFlyTimes.get(player.getUniqueId()) <= 1000 * 10) {
            player.sendMessage(ChatColor.RED + "Please wait " + (10 - (Math.round(System.currentTimeMillis() - lastBlazeFlyTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
            return;
        }
        if (event.isSneaking()) {
            FlightGCheatManager.addToMapping(player, 20 * 5);
            player.setVelocity(player.getLocation().getDirection().multiply(3));
            player.setVelocity(new Vector(player.getVelocity().getX(), 1.15D, player.getVelocity().getZ()));
            player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "BLAZE_BREATH", "ENTITY_BLAZE_AMBIENT"), 1.0f, 1.0f);
        }

        lastBlazeFlyTimes.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @Override
    public void setMorph(Player player) {
        new MorphUtil(MorphUtil.MorphType.BLAZE, player).sendServerSetMorph();
    }
}
