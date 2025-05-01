package net.badlion.cosmetics.morphs;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.managers.FlightGCheatManager;
import net.badlion.cosmetics.utils.MorphUtil;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.*;

public class IronGolemMorph extends Morph {

    public Map<UUID, Long> lastIronGolemSneakTimes = new HashMap<>();

    public IronGolemMorph() {
        super("iron_golem_morph", ItemRarity.SUPER_RARE, ItemStackUtil.createItem(Material.PUMPKIN, ChatColor.GREEN + "Iron Golem Morph", ChatColor.GRAY + "Shift to bounce players."));
        this.morphType = MorphUtil.MorphType.GOLEM;
    }

    @Override
    public void handleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!event.isSneaking()) return;
        if (lastIronGolemSneakTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - lastIronGolemSneakTimes.get(player.getUniqueId()) <= 1000 * 30) {
            player.sendMessage(ChatColor.RED + "Please wait " + (30 - (Math.round(System.currentTimeMillis() - lastIronGolemSneakTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
            return;
        }
        for (int x2 = 1; x2 <= 5; x2++) {
            for (int z2 = 1; z2 <= 5; z2++) {
                Block block = getBlockWithinThreshold(player.getLocation().add(x2, -1.0D, z2), (int) player.getLocation().getY() + 2);
                if (block != null) {
                    FallingBlock fallingBlock = player.getWorld().spawnFallingBlock(block.getLocation().add(0.0D, 1.0D, 0.0D), block.getType(), (byte) 0);
                    fallingBlock.setMetadata("fallfix", new FixedMetadataValue(Cosmetics.getInstance(), "fallfix"));
                    fallingBlock.setDropItem(false);
                    fallingBlock.setVelocity(new Vector(-0.5F + (float) (Math.random() * 1.0D), 1.0D, -0.5F + (float) (Math.random() * 1.0D)));
                }
                block = getBlockWithinThreshold(player.getLocation().add(-x2, -1.0D, -z2), (int) player.getLocation().getY() + 2);
                if (block != null) {
                    FallingBlock fallingBlock = player.getWorld().spawnFallingBlock(block.getLocation().add(0.0D, 1.0D, 0.0D), block.getType(), (byte) 0);
                    fallingBlock.setMetadata("fallfix", new FixedMetadataValue(Cosmetics.getInstance(), "fallfix"));
                    fallingBlock.setDropItem(false);
                    fallingBlock.setVelocity(new Vector(-0.5F + (float) (Math.random() * 1.0D), 1.0D, -0.5F + (float) (Math.random() * 1.0D)));
                }
                block = getBlockWithinThreshold(player.getLocation().add(-x2, -1.0D, z2), (int) player.getLocation().getY() + 2);
                if (block != null) {
                    FallingBlock fallingBlock = player.getWorld().spawnFallingBlock(block.getLocation().add(0.0D, 1.0D, 0.0D), block.getType(), (byte) 0);
                    fallingBlock.setMetadata("fallfix", new FixedMetadataValue(Cosmetics.getInstance(), "fallfix"));
                    fallingBlock.setDropItem(false);
                    fallingBlock.setVelocity(new Vector(-0.5F + (float) (Math.random() * 1.0D), 1.0D, -0.5F + (float) (Math.random() * 1.0D)));
                }
                block = getBlockWithinThreshold(player.getLocation().add(x2, -1.0D, -z2), (int) player.getLocation().getY() + 2);
                if (block != null) {
                    FallingBlock fallingBlock = player.getWorld().spawnFallingBlock(block.getLocation().add(0.0D, 1.0D, 0.0D), block.getType(), (byte) 0);
                    fallingBlock.setMetadata("fallfix", new FixedMetadataValue(Cosmetics.getInstance(), "fallfix"));
                    fallingBlock.setDropItem(false);
                    fallingBlock.setVelocity(new Vector(-0.5F + (float) (Math.random() * 1.0D), 1.0D, -0.5F + (float) (Math.random() * 1.0D)));
                }
            }
        }
        for (Player pl : player.getWorld().getPlayers()) {
            if (pl != player && pl.getLocation().distance(player.getLocation()) <= 10) {
                FlightGCheatManager.addToMapping(pl, 20 * 7);
                pl.setVelocity(pl.getVelocity().add(new Vector(0.0D, 3.0D, 0.0D)));
            }
        }
        player.getLocation().getWorld().playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "IRONGOLEM_HIT", "ENTITY_IRONGOLEM_ATTACK"), 1.0f, 1.0f);
        lastIronGolemSneakTimes.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @Override
    public void setMorph(Player player) {
        new MorphUtil(MorphUtil.MorphType.GOLEM, player).sendServerSetMorph();
    }

    private Block getBlockWithinThreshold(Location location, int limit) {
        Set<Material> blacklistedMaterials = new HashSet<>();
        blacklistedMaterials.add(Material.AIR);
        return Gberry.getBlockWithinThreshold(location.getWorld(), (int) location.getX(), (int) location.getY(), (int) location.getZ(), limit, blacklistedMaterials);
    }
}
