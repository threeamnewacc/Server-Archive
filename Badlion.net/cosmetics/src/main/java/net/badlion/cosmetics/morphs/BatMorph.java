package net.badlion.cosmetics.morphs;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.utils.MorphUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class BatMorph extends Morph {

    public BatMorph() {
        super("bat_morph", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, 1, (byte) 65, ChatColor.GREEN + "Bat Morph", ChatColor.GRAY + "Left click to screech."));
        this.morphType = MorphUtil.MorphType.BAT;
    }

    @Override
    protected void handleLeftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "BAT_HURT", "ENTITY_BAT_HURT"), 1.0f, 1.0f);
    }

    @Override
    public void setMorph(Player player) {
        new MorphUtil(MorphUtil.MorphType.BAT, player).sendServerSetMorph();
    }
}
