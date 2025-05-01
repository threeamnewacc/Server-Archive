package net.badlion.cosmetics.morphs;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.utils.MorphUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class SkeletonMorph extends Morph {

    private HashMap<UUID, Long> lastSkeletonShootTimes = new HashMap<>();

    public SkeletonMorph() {
        super("skeleton_morph", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, 1, (byte) 51, ChatColor.GREEN + "Skeleton Morph",
                Arrays.asList(ChatColor.GRAY + "Left click to shoot arrow.", ChatColor.GRAY + "Right click to idle.")));
        this.morphType = MorphUtil.MorphType.SKELETON;

    }

    @Override
    protected void handleLeftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (lastSkeletonShootTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - lastSkeletonShootTimes.get(player.getUniqueId()) <= 1000 * 5) {
            player.sendMessage(ChatColor.RED + "Please wait " + (5 - (Math.round(System.currentTimeMillis() - lastSkeletonShootTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
            return;
        }
        Arrow arrow = player.getWorld().spawnArrow(player.getEyeLocation().add(0.0D, -0.2D, 0.0D).add(player.getLocation().getDirection().normalize()), player.getLocation().getDirection().multiply(3), 2.0f, 1.0f);
        arrow.setShooter(player);
        arrow.setMetadata("morpharrow", new FixedMetadataValue(Cosmetics.getInstance(), "morpharrow"));
        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "SHOOT_ARROW", "ENTITY_ARROW_SHOOT"), 1.0f, 1.0f);

        lastSkeletonShootTimes.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @Override
    protected void handleRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "SKELETON_WALK", "ENTITY_SKELETON_AMBIENT"), 1.0f, 1.0f);
    }

    @Override
    public void setMorph(Player player) {
        new MorphUtil(MorphUtil.MorphType.SKELETON, player).sendServerSetMorph();
    }
}
