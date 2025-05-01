package net.badlion.cosmetics.morphs;

import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.utils.MorphUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WitchMorph extends Morph {

    public Map<UUID, Long> lastWitchSneakTimes = new HashMap<>();

    public WitchMorph() {
        super("witch_morph", ItemRarity.COMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, 1, (byte) 66, ChatColor.GREEN + "Witch Morph", ChatColor.GRAY + "Left click to throw potion.", ChatColor.GRAY + "Shift for speed."));
        this.morphType = MorphUtil.MorphType.WITCH;
    }

    @Override
    public void handleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!event.isSneaking()) return;
        if (lastWitchSneakTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - lastWitchSneakTimes.get(player.getUniqueId()) <= 1000 * 30) {
            player.sendMessage(ChatColor.RED + "Please wait " + (20 - (Math.round(System.currentTimeMillis() - lastWitchSneakTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
            return;
        }
        for (Player op : player.getWorld().getPlayers()) {
            if (op.getLocation().distance(player.getLocation()) <= 5)
                op.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 5, 1, true));
        }
        lastWitchSneakTimes.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @Override
    protected void handleLeftClick(PlayerInteractEvent event) {
        if (event.getItem().getType() == Material.POTION) {
            Player player = event.getPlayer();
            final Item potion = player.getWorld().dropItem(player.getLocation().add(player.getLocation().getDirection()), ItemStackUtil.createItem(Material.POTION, String.valueOf(Math.random() * 100.0D)));
            potion.setMetadata("takeable", new FixedMetadataValue(Cosmetics.getInstance(), "takeable"));
            potion.setVelocity(player.getLocation().getDirection().multiply(0.2D).add(new Vector(0.0D, 0.5D, 0.0D)));
            Bukkit.getScheduler().runTaskLater(Cosmetics.getInstance(), new Runnable() {
                @Override
                public void run() {
                    potion.remove();
                }
            }, 20L);
        }
    }

    @Override
    public void setMorph(Player player) {
        player.setBypassGCheat(true);
        new MorphUtil(MorphUtil.MorphType.WITCH, player).sendServerSetMorph();
    }
}
