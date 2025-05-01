package net.badlion.cosmetics.morphs;

import net.badlion.cosmetics.managers.FlightGCheatManager;
import net.badlion.cosmetics.utils.MorphUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SlimeMorph extends Morph {

    public static ArrayList<UUID> slimeJumpingPlayers = new ArrayList<>();

    public Map<UUID, Long> lastSlimeSneakTimes = new HashMap<>();

    public SlimeMorph() {
        super("slime_morph", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, 1, (byte) 55, ChatColor.GREEN + "Slime Morph", ChatColor.GRAY + "Shift to bounce."));
        this.morphType = MorphUtil.MorphType.SLIME;
    }

    @Override
    public void handleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!event.isSneaking()) return;
        if (lastSlimeSneakTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - lastSlimeSneakTimes.get(player.getUniqueId()) <= 1000 * 10) {
            player.sendMessage(ChatColor.RED + "Please wait " + (10 - (Math.round(System.currentTimeMillis() - lastSlimeSneakTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
            return;
        }
        if (!player.isOnGround()) {
            player.sendMessage(ChatColor.RED + "You must be on the ground to do this.");
            return;
        }
        FlightGCheatManager.addToMapping(player, 20 * 7);
        player.setVelocity(player.getVelocity().add(new Vector(0.0D, 3.0D, 0.0D)));
        slimeJumpingPlayers.add(player.getUniqueId());
        lastSlimeSneakTimes.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @Override
    public void setMorph(Player player) {
        new MorphUtil(MorphUtil.MorphType.SLIME, player).sendServerSetMorph();
    }

}
