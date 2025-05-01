package net.badlion.uhc.listeners.gamemodes;

import com.google.common.collect.ImmutableList;
import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.Pair;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrollGame1 implements GameMode {

    private Map<Pair, List<Location>> diamondLocations = new HashMap<>();
    private World world;

    public static int TICKS_IN_BETWEEN = 300;

    public TrollGame1() {
        this.world = BadlionUHC.getInstance().getServer().getWorld(BadlionUHC.UHCWORLD_NAME);
        new RunSounds().runTaskLater(BadlionUHC.getInstance(), TICKS_IN_BETWEEN);
    }

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.GOLD_INGOT);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Troll GameMode 1");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- It's a surprise ^.^");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "http://twitch.tv/archybot";
    }

    private class RunSounds extends BukkitRunnable {

        public void run() {
            final ImmutableList<Player> players = ImmutableList.copyOf(Bukkit.getOnlinePlayers());
            int size = players.size();
            int diff = (int) Math.ceil((double) players.size() / 20D);

            for (int i = 0, j = 0; i < size; i += diff) {
                // Overshot
                if (i >= size) {
                    return;
                }

                // Some shit for the task
                final int start = i;
                final int end = i + diff;

                Bukkit.getServer().getScheduler().runTaskLater(BadlionUHC.getInstance(), new Runnable() {

                    @Override
                    public void run() {
                        for (int i = start; i < end; ++i) {
                            // Overshot
                            if (i >= players.size()) {
                                return;
                            }

                            Player player = players.get(i);
                            if (player != null && player.isOnline()) {
                                if (player.getLocation().getWorld().getName().equals(BadlionUHC.UHCWORLD_NAME)) {
                                    UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(player.getUniqueId());
                                    if (uhcPlayer != null && uhcPlayer.getState() == UHCPlayer.State.PLAYER) {
                                        int x = player.getLocation().getChunk().getX() + Gberry.generateRandomInt(-1, 1);
                                        int z = player.getLocation().getChunk().getZ() + Gberry.generateRandomInt(-1, 1);

                                        Pair<Integer, Integer> pair = Pair.of(x, z);

                                        List<Location> locations = TrollGame1.this.diamondLocations.get(pair);

                                        // Not already loaded
                                        if (locations == null) {
                                            locations = new ArrayList<>();
                                            TrollGame1.this.diamondLocations.put(pair, locations);
                                            Chunk chunk = TrollGame1.this.world.getChunkAt(x, z);
                                            TrollGame1.this.handleChunkSection(chunk, locations);
                                        }

                                        if (locations.size() > 0) {
                                            int index = Gberry.generateRandomInt(0, locations.size() - 1);
                                            TrollGame1.this.world.playSound(locations.get(index), EnumCommon.getEnumValueOf(Sound.class, "GHAST_MOAN", "ENTITY_GHAST_SCREAM"), 1, 1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }, ++j);
            }

            new RunSounds().runTaskLater(BadlionUHC.getInstance(), TICKS_IN_BETWEEN);
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        World world = event.getChunk().getWorld();
        if (!world.getName().equals(BadlionUHC.UHCWORLD_NAME)) {
            return;
        }

        Pair<Integer, Integer> pair = Pair.of(chunk.getX(), chunk.getZ());

        List<Location> locations = this.diamondLocations.get(pair);
        if (locations == null) {
            locations = new ArrayList<>();
            this.diamondLocations.put(pair, locations);
        } else {
            // Already handled this chunk
            return;
        }

        this.handleChunkSection(chunk, locations);
    }

    private void handleChunkSection(Chunk chunk, List<Location> locations) {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    locations.add(chunk.getBlock(chunk.getX() * 16 + i, j, chunk.getZ() * 16 + k).getLocation());
                }
            }
        }
    }

    @EventHandler
    public void onDiamondsBroken(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.DIAMOND_ORE) {
            Pair<Integer, Integer> pair = Pair.of(event.getBlock().getChunk().getX(), event.getBlock().getChunk().getZ());
            List<Location> locations = this.diamondLocations.get(pair);
            if (locations != null) {
                locations.remove(event.getBlock().getLocation());
            }
        }
    }

    @Override
    public void unregister() {
        BlockBreakEvent.getHandlerList().unregister(this);
        ChunkLoadEvent.getHandlerList().unregister(this);
    }

}
