package net.badlion.cosmetics.morphs;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.utils.MorphUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class CowMorph extends Morph {

    public CowMorph() {
        super("cow_morph", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, 1, (byte) 92, ChatColor.GREEN + "Cow Morph", ChatColor.GRAY + "Left click to moo.", ChatColor.GRAY + "Right click to vomit beef."));
        this.morphType = MorphUtil.MorphType.COW;
    }

    @Override
    protected void handleLeftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "COW_HURT", "ENTITY_COW_HURT"), 1.0f, 1.0f);
    }

    @Override
    protected void handleRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        final Item cow = player.getWorld().dropItem(player.getLocation().add(0.0D, 0.5D, 0.0D), ItemStackUtil.createItem(Material.RAW_BEEF, String.valueOf(Math.random() * 100.0D)));
        cow.setMetadata("takeable", new FixedMetadataValue(Cosmetics.getInstance(), "takeable"));
        cow.setVelocity(player.getLocation().getDirection());
        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "BURP", "ENTITY_PLAYER_BURP"), 1.0f, 1.0f);
        Bukkit.getScheduler().runTaskLater(Cosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                cow.remove();
            }
        }, 20L * 2);
    }

    @Override
    public void setMorph(Player player) {
        new MorphUtil(MorphUtil.MorphType.COW, player).sendServerSetMorph();
    }
}
