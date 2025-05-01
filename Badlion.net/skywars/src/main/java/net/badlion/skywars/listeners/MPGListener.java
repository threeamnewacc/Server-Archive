package net.badlion.skywars.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.blocks.CraftMassBlockUpdate;
import net.badlion.gberry.utils.blocks.MassBlockUpdate;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.bukkitevents.*;
import net.badlion.mpg.managers.MPGMapManager;
import net.badlion.skywars.SkyPlayer;
import net.badlion.skywars.SkyWars;
import net.badlion.skywars.SkyWorld;
import net.badlion.skywars.SkyGame;
import net.badlion.worldrotator.GWorld;
import net.badlion.worldrotator.WorldRotator;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MPGListener implements Listener {

    private boolean tryingToConfigChest = false;
    private Map<UUID, Integer> chestSetter = new HashMap<>();
    private Location minScan;
    private Location maxScan;

    @EventHandler
    public void onMapManagerInitialize(MapManagerInitializeEvent event) {
        for (GWorld world : WorldRotator.getInstance().getWorlds()) {
            event.getWorlds().add(new SkyWorld(world));
        }
    }

    @EventHandler
    public void onServerLoaded(MPGServerStateChangeEvent event) {
        if (event.getNewState() == MPG.ServerState.LOBBY) {
            SkyGame skyGame = new SkyGame((SkyWorld) MPGMapManager.getRandomWorld());
            MPG.getInstance().setMPGGame(skyGame);
        }
    }

    @EventHandler
    public void onMPGPlayerCreated(MPGCreatePlayerEvent event) {
        SkyPlayer skyPlayer = new SkyPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName());
        event.setMpgPlayer(skyPlayer);

        // Force others into spectator mode
        if (!Gberry.serverName.contains("test") && MPG.getInstance().getMPGGame().getGameState().ordinal() < MPGGame.GameState.GAME.ordinal()) {
            skyPlayer.setState(MPGPlayer.PlayerState.SPECTATOR);
        }
    }

    @EventHandler
    public void onConfigMapCommand(ConfigMapCommandEvent event) {
        if (event.getArgs()[0].equalsIgnoreCase("lowermiddle")) {
            WorldRotator.getInstance().getGWorld(event.getPlayer().getWorld().getName()).getYml().set("lower_middle", event.getLocString());
            event.getPlayer().sendMessage(ChatColor.GREEN + "Lower Middle set");
        } else if (event.getArgs()[0].equalsIgnoreCase("uppermiddle")) {
            WorldRotator.getInstance().getGWorld(event.getPlayer().getWorld().getName()).getYml().set("upper_middle", event.getLocString());
            event.getPlayer().sendMessage(ChatColor.GREEN + "Upper Middle set");
        } else if (event.getArgs()[0].equalsIgnoreCase("lowermap")) {
            WorldRotator.getInstance().getGWorld(event.getPlayer().getWorld().getName()).getYml().set("lower_map", event.getLocString());
            event.getPlayer().sendMessage(ChatColor.GREEN + "Lower Map set");
        } else if (event.getArgs()[0].equalsIgnoreCase("uppermap")) {
            WorldRotator.getInstance().getGWorld(event.getPlayer().getWorld().getName()).getYml().set("upper_map", event.getLocString());
            event.getPlayer().sendMessage(ChatColor.GREEN + "Upper Map set");
        } else if (event.getArgs()[0].equalsIgnoreCase("scan")) {
            Location lowerMiddle = Gberry.parseLocation(WorldRotator.getInstance().getGWorld(event.getPlayer().getWorld().getName()).getYml().getString("lower_middle"));
            Location upperMiddle = Gberry.parseLocation(WorldRotator.getInstance().getGWorld(event.getPlayer().getWorld().getName()).getYml().getString("upper_middle"));
            Location lowerMap = Gberry.parseLocation(WorldRotator.getInstance().getGWorld(event.getPlayer().getWorld().getName()).getYml().getString("lower_map"));
            Location upperMap = Gberry.parseLocation(WorldRotator.getInstance().getGWorld(event.getPlayer().getWorld().getName()).getYml().getString("upper_map"));

            List<String> blocksToBlowAway = new ArrayList<>();
            for (int x = lowerMap.getBlockX(); x <= upperMap.getBlockX(); x++) {
                for (int z = lowerMap.getBlockZ(); z <= upperMap.getBlockZ(); z++) {
                    for (int y = 0; y <= 255; y++) {
                        Block block = lowerMap.getWorld().getBlockAt(x, y, z);
                        if (block != null && block.getType() != Material.AIR) {
                            if (!Gberry.isLocationInBetween(lowerMiddle, upperMiddle, block.getLocation())) {
                                blocksToBlowAway.add(Gberry.getLocationString(block.getLocation()));
                            }
                        }
                    }
                }
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("blocks_to_destroy", blocksToBlowAway);
            String jsonString = jsonObject.toJSONString();

            File jsonFile = new File(WorldRotator.getInstance().getGWorld(event.getPlayer().getWorld().getName()).getDirectory(), event.getPlayer().getWorld().getName() + "_blocks.json");
            try {
                FileUtils.write(jsonFile, jsonString);
            } catch (IOException e) {
                SkyWars.getInstance().getLogger().info("Failed to write arena string " + event.getPlayer().getWorld().getName());
            }

            event.getPlayer().sendMessage(ChatColor.GREEN + "Scanning done");
        } else if (event.getArgs()[0].equalsIgnoreCase("chestspawn")) {
            this.chestSetter.put(event.getPlayer().getUniqueId(), Integer.parseInt(event.getArgs()[1]));
            event.getPlayer().sendMessage("Ready for spawn chest");
            this.tryingToConfigChest = true;
        } else if (event.getArgs()[0].equalsIgnoreCase("minytilldeath")) {
            WorldRotator.getInstance().getGWorld(event.getPlayer().getWorld().getName()).getYml().set("minytilldeath", Integer.parseInt(event.getArgs()[1]));
            event.getPlayer().sendMessage(ChatColor.GREEN + "Y coordinate set");
        } else if (event.getArgs()[0].equalsIgnoreCase("minscan")) {
            this.minScan = Gberry.parseLocation(event.getLocString());
            event.getPlayer().sendMessage(ChatColor.GREEN + "Min scan set");
        } else if (event.getArgs()[0].equalsIgnoreCase("maxscan")) {
            this.maxScan = Gberry.parseLocation(event.getLocString());
            event.getPlayer().sendMessage(ChatColor.GREEN + "Max scan set");
        } else if (event.getArgs()[0].equalsIgnoreCase("appendscan")) {
            if (this.maxScan == null || this.minScan == null) {
                event.getPlayer().sendMessage(ChatColor.RED + "Min/Max not set properly");
            } else if (!this.maxScan.getWorld().equals(this.minScan.getWorld())) {
                event.getPlayer().sendMessage(ChatColor.RED + "Min/Max not set properly");
            } else {
                if (this.minScan.getX() > this.maxScan.getX()) {
                    double x = this.maxScan.getX();
                    this.maxScan.setX(this.minScan.getX());
                    this.minScan.setX(x);
                }

                if (this.minScan.getY() > this.maxScan.getY()) {
                    double y = this.maxScan.getY();
                    this.maxScan.setY(this.minScan.getY());
                    this.minScan.setY(y);
                }

                if (this.minScan.getZ() > this.maxScan.getZ()) {
                    double z = this.maxScan.getZ();
                    this.maxScan.setZ(this.minScan.getZ());
                    this.minScan.setZ(z);
                }

                List<Location> blocksToDestroy = this.getDestroyedBlocks(event.getPlayer(), WorldRotator.getInstance().getGWorld(event.getPlayer().getWorld().getName()));

                for (int x = this.minScan.getBlockX(); x <= this.maxScan.getBlockX(); x++) {
                    for (int z = this.minScan.getBlockZ(); z <= this.maxScan.getBlockZ(); z++) {
                        for (int y = 0; y <= 255; y++) {
                            Block block = this.minScan.getWorld().getBlockAt(x, y, z);
                            if (block != null && block.getType() != Material.AIR) {
                                if (!blocksToDestroy.contains(block.getLocation())) {
                                    blocksToDestroy.add(block.getLocation());
                                }
                            }
                        }
                    }
                }

                List<String> stringLocations = new ArrayList<>();
                for (Location location : blocksToDestroy) {
                    stringLocations.add(Gberry.getLocationString(location));
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("blocks_to_destroy", stringLocations);
                String jsonString = jsonObject.toJSONString();

                File jsonFile = new File(WorldRotator.getInstance().getGWorld(event.getPlayer().getWorld().getName()).getDirectory(), event.getPlayer().getWorld().getName() + "_blocks.json");
                try {
                    FileUtils.write(jsonFile, jsonString);
                } catch (IOException e) {
                    SkyWars.getInstance().getLogger().info("Failed to write arena string " + event.getPlayer().getWorld().getName());
                }

                event.getPlayer().sendMessage(ChatColor.GREEN + "Blocks added");
            }
        } else if (event.getArgs()[0].equalsIgnoreCase("destroy")) {
            List<Location> blocksToDestroy = this.getDestroyedBlocks(event.getPlayer(), WorldRotator.getInstance().getGWorld(event.getPlayer().getWorld().getName()));

            MassBlockUpdate massBlockUpdate = new CraftMassBlockUpdate(WorldRotator.getInstance().getGWorld(event.getPlayer().getWorld().getName()).getBukkitWorld());
            massBlockUpdate.setRelightingStrategy(MassBlockUpdate.RelightingStrategy.NEVER);
            massBlockUpdate.setMaxRelightTimePerTick(2, TimeUnit.MILLISECONDS);

            for (Location location : blocksToDestroy) {
                massBlockUpdate.setBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ(), 0);
            }

            massBlockUpdate.notifyClients();
        }
    }

    public List<Location> getDestroyedBlocks(Player player, GWorld gWorld) {
        List<Location> locationsToDestroy = new ArrayList<>();
        File jsonFile = new File(gWorld.getDirectory(), gWorld.getInternalName() + "_blocks.json");
        if (jsonFile.exists()) {
            try {
                JSONObject jsonObject = (JSONObject) JSONValue.parse(new String(Files.readAllBytes(Paths.get(jsonFile.getAbsolutePath()))));

                for (String locationString : (List<String>) jsonObject.get("blocks_to_destroy")) {
                    Location location = Gberry.parseLocation(locationString);
                    locationsToDestroy.add(location);
                }
            } catch (IOException e) {
                player.sendMessage("Error reading " + gWorld.getInternalName());
            }
        } else {
            player.sendMessage("File missing for SkyWarsArena " + gWorld.getInternalName());
        }

        return locationsToDestroy;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!this.tryingToConfigChest) {
            return;
        }

        Integer spawnNumber = this.chestSetter.remove(event.getPlayer().getUniqueId());
        if (spawnNumber != null && event.getBlock().getType() == Material.CHEST) {
            List<String> chests = WorldRotator.getInstance().getGWorld(event.getPlayer().getWorld().getName()).getYml().getStringList("spawn-" + spawnNumber);
            String locationString = Gberry.getLocationString(event.getBlock().getLocation());
            if (!chests.contains(locationString)) {
                chests.add(locationString);
            }

            event.getPlayer().sendMessage(ChatColor.GREEN + "Spawn chest added");
            this.tryingToConfigChest = false;
        } else {
            event.getPlayer().sendMessage(ChatColor.RED + "No spawn chest added");
            this.tryingToConfigChest = false;
        }
    }

}
