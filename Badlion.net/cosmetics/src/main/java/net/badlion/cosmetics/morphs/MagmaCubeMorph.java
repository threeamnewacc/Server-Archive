package net.badlion.cosmetics.morphs;

import net.badlion.cosmetics.managers.CosmeticsManager;
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

public class MagmaCubeMorph extends Morph {

    public static ArrayList<UUID> magmaJumpingPlayers = new ArrayList<>();

    public Map<UUID, Long> lastMagmaSneakTimes = new HashMap<>();

    public MagmaCubeMorph() {
        super("magma_cube_morph", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, 1, (byte) 62, ChatColor.GREEN + "Magma Cube Morph", ChatColor.GRAY + "Shift to bounce."));
        this.morphType = MorphUtil.MorphType.MAGMACUBE;
    }

    @Override
    public void handleSneak(PlayerToggleSneakEvent event) {
        final Player player = event.getPlayer();
        if (!event.isSneaking()) return;

        FlightGCheatManager.addToMapping(player, 20 * 7);

        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());
        if (cosmeticsSettings.getActiveMorph() == null || cosmeticsSettings.getActiveMorph().getMorphType() != MorphUtil.MorphType.MAGMACUBE)
            return;
        if (this.lastMagmaSneakTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - lastMagmaSneakTimes.get(player.getUniqueId()) <= 1000 * 30) {
            player.sendMessage(ChatColor.RED + "Please wait " + (30 - (Math.round(System.currentTimeMillis() - lastMagmaSneakTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
            return;
        }
        if (!player.isOnGround()) {
            player.sendMessage(ChatColor.RED + "You must be on the ground to do this.");
            return;
        }
        player.setVelocity(player.getVelocity().add(new Vector(0.0D, 3.0D, 0.0D)));

        MagmaCubeMorph.magmaJumpingPlayers.add(player.getUniqueId());
        this.lastMagmaSneakTimes.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @Override
    public void setMorph(Player player) {
        new MorphUtil(MorphUtil.MorphType.MAGMACUBE, player).sendServerSetMorph();
    }
}
