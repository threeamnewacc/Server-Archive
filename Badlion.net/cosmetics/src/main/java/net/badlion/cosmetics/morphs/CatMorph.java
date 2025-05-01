package net.badlion.cosmetics.morphs;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.utils.MorphUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Random;

public class CatMorph extends Morph {

    public CatMorph() {
        super("cat_morph", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, 1, (byte) 98, ChatColor.GREEN + "Cat Morph", ChatColor.GRAY + "Left click to meow."));
        this.morphType = MorphUtil.MorphType.CAT;
    }

    @Override
    protected void handleLeftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (new Random().nextBoolean())
            player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "CAT_MEOW", "ENTITY_CAT_PURREOW"), 1.0f, 1.0f);
        else
            player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "CAT_HISS", "ENTITY_CAT_HISS"), 1.0f, 1.0f);
    }

    @Override
    public void setMorph(Player player) {
        new MorphUtil(MorphUtil.MorphType.CAT, player).sendServerSetMorph();
    }
}
