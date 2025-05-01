package net.badlion.cosmetics.morphs;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.utils.MorphUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SnowmanMorph extends Morph {

    public Map<UUID, Long> lastSnowmanSneakTimes = new HashMap<>();

    public SnowmanMorph() {
        super("snowman_morph", ItemRarity.RARE, ItemStackUtil.createItem(Material.SNOW_BALL, ChatColor.GREEN + "Snowman Morph",
                Arrays.asList(ChatColor.GRAY + "Left click to throw snowball.", ChatColor.GRAY + "Shift to fire snow.")));
        this.morphType = MorphUtil.MorphType.SNOWMAN;
    }

    @Override
    protected void handleLeftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Snowball snowball = player.getWorld().spawn(player.getEyeLocation(), Snowball.class);
        snowball.setShooter(player);
        snowball.setMetadata("morphsnowball", new FixedMetadataValue(Cosmetics.getInstance(), "morphsnowball"));
        snowball.setVelocity(player.getLocation().getDirection().multiply(1.5));
    }

    @Override
    public void handleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!event.isSneaking()) return;
        if (lastSnowmanSneakTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - lastSnowmanSneakTimes.get(player.getUniqueId()) <= 1000 * 10) {
            player.sendMessage(ChatColor.RED + "Please wait " + (10 - (Math.round(System.currentTimeMillis() - lastSnowmanSneakTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
            return;
        }
        for (int i = 0; i < 16; i++) {
            double x = -0.5F + (float) (Math.random() * 1.0D);
            double y = 1.0D;
            double z = -0.5F + (float) (Math.random() * 1.0D);
            FallingBlock fallingBlock = player.getWorld().spawnFallingBlock(player.getLocation(), Material.SNOW_BLOCK, (byte) 0);
            fallingBlock.setMetadata("fallfix2", new FixedMetadataValue(Cosmetics.getInstance(), "fallfix2"));
            fallingBlock.setDropItem(false);
            fallingBlock.setVelocity(player.getLocation().getDirection().multiply(2).add(new Vector(x, y, z)));
        }
        player.getLocation().getWorld().playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "WITHER_SHOOT", "ENTITY_WITHER_SHOOT"), 1.0f, 1.0f);
        lastSnowmanSneakTimes.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @Override
    public void setMorph(Player player) {
        new MorphUtil(MorphUtil.MorphType.SNOWMAN, player).sendServerSetMorph();
    }
}
