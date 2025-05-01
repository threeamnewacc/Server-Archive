package net.badlion.cosmetics.morphs;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.managers.FlightGCheatManager;
import net.badlion.cosmetics.utils.MorphUtil;
import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.*;

public class CreeperMorph extends Morph {

    public static ArrayList<UUID> creeperSneakPlayers = new ArrayList<>();
    public ArrayList<UUID> creeperUnSneakedPlayers = new ArrayList<>();

    public Map<UUID, Long> lastCreeperSneakTimes = new HashMap<>();

    private ParticleLibrary particle = new ParticleLibrary(ParticleLibrary.ParticleType.EXPLOSION_HUGE, 0, 1, 0);
    ;

    public CreeperMorph() {
        super("creeper_morph", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.MONSTER_EGG, 1, (byte) 50, ChatColor.GREEN + "Creeper Morph",
                Arrays.asList(ChatColor.GRAY + "Left click to hiss.", ChatColor.GRAY + "Shift to explode.")));
        this.morphType = MorphUtil.MorphType.CREEPER;
    }

    @Override
    protected void handleLeftClick(PlayerInteractEvent event) {
        event.getPlayer().playSound(event.getPlayer().getLocation(), EnumCommon.getEnumValueOf(Sound.class, "CREEPER_HISS", "ENTITY_CREEPER_PRIMED"), 1.0f, 1.0f);
    }

    @Override
    public void handleSneak(PlayerToggleSneakEvent event) {
        final Player player = event.getPlayer();
        if (!event.isSneaking()) {
            creeperSneakPlayers.remove(player.getUniqueId());
            creeperUnSneakedPlayers.add(player.getUniqueId());
            return;
        } else {
            creeperUnSneakedPlayers.remove(player.getUniqueId());
        }

        if (lastCreeperSneakTimes.containsKey(player.getUniqueId()) && System.currentTimeMillis() - lastCreeperSneakTimes.get(player.getUniqueId()) <= 1000 * 5) {
            player.sendMessage(ChatColor.RED + "Please wait " + (5 - (Math.round(System.currentTimeMillis() - lastCreeperSneakTimes.get(player.getUniqueId()))) / 1000) + " seconds to do this again.");
            return;
        }

        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "CREEPER_HISS", "ENTITY_CREEPER_PRIMED"), 1.0f, 1.0f);
        creeperSneakPlayers.add(player.getUniqueId());

        Bukkit.getScheduler().runTaskLater(Cosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (player.isSneaking() && creeperSneakPlayers.contains(player.getUniqueId()) && !creeperUnSneakedPlayers.contains(player.getUniqueId())) {
                    CreeperMorph.this.particle.sendToLocation(player, player.getLocation(), 5);

                    player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "EXPLODE", "ENTITY_GENERIC_EXPLODE"), 1.0f, 1.0f);

                    for (Player player1 : player.getWorld().getPlayers()) {
                        if (player.getLocation().distance(player1.getLocation()) <= 5) {
                            FlightGCheatManager.addToMapping(player, 20 * 5);
                            player.setVelocity(player.getLocation().getDirection().multiply(3));
                            player.setVelocity(new Vector(player.getVelocity().getX(), 1.1, player.getVelocity().getZ()));
                        }
                    }

                    creeperSneakPlayers.remove(player.getUniqueId());
                    CreeperMorph.this.lastCreeperSneakTimes.put(player.getUniqueId(), System.currentTimeMillis());
                }

                CreeperMorph.this.creeperUnSneakedPlayers.remove(player.getUniqueId());
            }
        }, 20L);
    }

    @Override
    public void setMorph(Player player) {
        new MorphUtil(MorphUtil.MorphType.CREEPER, player).sendServerSetMorph();
    }
}
