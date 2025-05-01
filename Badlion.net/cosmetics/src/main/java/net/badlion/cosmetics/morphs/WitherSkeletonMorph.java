package net.badlion.cosmetics.morphs;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.utils.MorphUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class WitherSkeletonMorph extends Morph {

    public WitherSkeletonMorph() {
        super("wither_skeleton_morph", ItemRarity.COMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, 1, (byte) 51, ChatColor.GREEN + "Wither Skeleton Morph", ChatColor.GRAY + "Left click to idle.", ChatColor.GRAY + "Run faster."));
        this.morphType = MorphUtil.MorphType.WITHER_SKELETON;
    }

    @Override
    protected void handleLeftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "SKELETON_IDLE", "ENTITY_SKELETON_AMBIENT"), 1.0f, 1.0f);
    }

    @Override
    public void setMorph(Player player) {
        new MorphUtil(MorphUtil.MorphType.WITHER_SKELETON, player).sendServerSetMorph();
    }
}
