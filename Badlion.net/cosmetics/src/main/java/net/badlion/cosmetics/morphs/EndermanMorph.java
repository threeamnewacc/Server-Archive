package net.badlion.cosmetics.morphs;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.utils.MorphUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.HashSet;

public class EndermanMorph extends Morph {

    public EndermanMorph() {
        super("enderman_morph", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, 1, (byte) 58, ChatColor.GREEN + "Enderman Morph",
                Arrays.asList(ChatColor.GRAY + "Left click to screech.", ChatColor.GRAY + "Right click to teleport.")));
        this.morphType = MorphUtil.MorphType.ENDERMAN;
    }

    @Override
    protected void handleLeftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "ENDERMAN_SCREAM", "ENTITY_ENDERMEN_SCREAM"), 1.0f, 1.0f);
    }

    @Override
    protected void handleRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getTargetBlock((HashSet<Byte>) null, 20).getLocation().add(0.0D, 1.0D, 0.0D);
        loc.setPitch(player.getLocation().getPitch());
        loc.setYaw(player.getLocation().getYaw());
        player.teleport(loc);
        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "ENDERMAN_TELEPORT", "ENTITY_ENDERMEN_TELEPORT"), 1.0f, 1.0f);
    }

    @Override
    public void setMorph(Player player) {
        new MorphUtil(MorphUtil.MorphType.ENDERMAN, player).sendServerSetMorph();
    }
}
