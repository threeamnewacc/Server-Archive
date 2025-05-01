package net.badlion.cosmetics.morphs;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.utils.MorphUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ZombiePigMorph extends Morph {

    public Map<UUID, Long> lastZombiePigScreamTimes = new HashMap<>();
    public Map<UUID, Long> lastZombiePigSpeedTimes = new HashMap<>();

    public ZombiePigMorph() {
        super("zombie_pig_morph", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, 1, (byte) 57, ChatColor.GREEN + "Zombie Pig Morph", ChatColor.GRAY + "Left click to scream.", ChatColor.GRAY + "Right click for speed."));
        this.morphType = MorphUtil.MorphType.ZOMBIEPIG;
    }

    @Override
    public void setMorph(Player player) {
        new MorphUtil(MorphUtil.MorphType.ZOMBIEPIG, player).sendServerSetMorph();
    }

    @Override
    protected void handleLeftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (lastZombiePigScreamTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - lastZombiePigScreamTimes.get(player.getUniqueId()) <= 1000 * 2) {
            player.sendMessage(ChatColor.RED + "Please wait " + (2 - (Math.round(System.currentTimeMillis() - lastZombiePigScreamTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
            return;
        }
        for (int i = 0; i < 10; i++) {
            player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "ZOMBIE_PIG_ANGRY", "ENTITY_ZOMBIE_PIG_ANGRY"), 1.0f, 1.0f);
        }
        lastZombiePigScreamTimes.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @Override
    protected void handleRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getItem().getType() != Material.AIR) return;
        if (lastZombiePigSpeedTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - lastZombiePigSpeedTimes.get(player.getUniqueId()) <= 1000 * 30) {
            player.sendMessage(ChatColor.RED + "Please wait " + (30 - (Math.round(System.currentTimeMillis() - lastZombiePigSpeedTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
            return;
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 15, 2, false));
        lastZombiePigSpeedTimes.put(player.getUniqueId(), System.currentTimeMillis());
    }
}
