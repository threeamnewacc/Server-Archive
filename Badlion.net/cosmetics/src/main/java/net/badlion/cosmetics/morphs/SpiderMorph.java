package net.badlion.cosmetics.morphs;

import net.badlion.cosmetics.utils.MorphUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SpiderMorph extends Morph {

    public SpiderMorph() {
        super("spider_morph", ItemRarity.RARE, ItemStackUtil.createItem(Material.MONSTER_EGG, 1, (byte) 52, ChatColor.GREEN + "Spider Morph", ChatColor.GRAY + "Walk into walls to climb them."));
        this.morphType = MorphUtil.MorphType.SPIDER;

    }

    @Override
    public void setMorph(Player player) {
        player.setBypassGCheat(true);
        new MorphUtil(MorphUtil.MorphType.SPIDER, player).sendServerSetMorph();
    }
}
