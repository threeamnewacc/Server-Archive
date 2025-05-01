package net.badlion.cosmetics.morphs;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.utils.MorphUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class VillagerMorph extends Morph {

    public VillagerMorph() {
        super("villager_morph", ItemRarity.COMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, 1, (byte) 120, ChatColor.GREEN + "Villager Morph",
                Arrays.asList(ChatColor.GRAY + "Left click to idle.", ChatColor.GRAY + "Right click for emeralds.")));
        this.morphType = MorphUtil.MorphType.VILLAGER;

    }

    @Override
    protected void handleLeftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "VILLAGER_IDLE", "ENTITY_VILLAGER_AMBIENT"), 1.0f, 1.0f);
    }


    @Override
    protected void handleRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        final Item emerald = player.getWorld().dropItem(player.getLocation().add(0.0D, 2.0D, 0.0D), ItemStackUtil.createItem(Material.EMERALD, String.valueOf(Math.random() * 100.0D)));
        emerald.setMetadata("takeable", new FixedMetadataValue(Cosmetics.getInstance(), "takeable"));
        emerald.setVelocity(new Vector(0.0D, 1.0D, 0.0D));
        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "LEVEL_UP", "ENTITY_PLAYER_LEVELUP"), 1.0f, 1.0f);
        Cosmetics.getInstance().getServer().getScheduler().runTaskLater(Cosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                emerald.remove();
            }
        }, 20L);
    }

    @Override
    public void setMorph(Player player) {
        new MorphUtil(MorphUtil.MorphType.VILLAGER, player).sendServerSetMorph();
    }
}
