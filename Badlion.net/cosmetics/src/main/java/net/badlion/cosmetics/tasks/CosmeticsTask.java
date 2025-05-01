package net.badlion.cosmetics.tasks;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.gadgets.*;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.managers.FlightGCheatManager;
import net.badlion.cosmetics.managers.GadgetManager;
import net.badlion.cosmetics.managers.ParticleManager;
import net.badlion.cosmetics.morphs.*;
import net.badlion.cosmetics.particles.LandTrail;
import net.badlion.cosmetics.particles.Particle;
import net.badlion.cosmetics.utils.MorphUtil;
import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.PlayerRunnable;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class CosmeticsTask extends BukkitRunnable {

    private int tickTime = 0;

    private Map<Item, Long> peeItems = new HashMap<>();
    private Map<Item, Long> gemShowerItems = new HashMap<>();

    private Map<UUID, Location> previousPlayerLocations = new HashMap<>();
    private Map<Location, Long> paintTrailLocations = new HashMap<>();

    @Override
    public void run() {
        CosmeticsTask.this.tickTime++;

        // Don't loop if they are disabled
        if (Cosmetics.getInstance().isAllDisabled()) {
            return;
        }

        // Loop through all cosmetics settings
        Gberry.distributeTask(Cosmetics.getInstance(), new PlayerRunnable() {
            @Override
            public void run(Player player) {
                CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());
                if (cosmeticsSettings == null || cosmeticsSettings.getUuid() == null) {
                    return;
                }

                if (!Gberry.isPlayerOnline(player)) {
                    CosmeticsManager.removeCosmeticsSettings(player.getUniqueId());
                    return;
                }

                // Arrow Trail
                CosmeticsTask.this.handleArrowtrails(cosmeticsSettings);

                // Rod Trail
                CosmeticsTask.this.handleRodTrails(cosmeticsSettings);

                // Particles
                CosmeticsTask.this.handleParticles(cosmeticsSettings);

                // Gadgets
                CosmeticsTask.this.handleGadgets(cosmeticsSettings);

                // Morphs
                CosmeticsTask.this.handleMorphs(cosmeticsSettings);
            }
        });

        if (CosmeticsTask.this.tickTime % 2 == 0) {
            // Morphs
            if (Cosmetics.getInstance().isMorphsEnabled()) {
                if (CosmeticsTask.this.tickTime % 10 == 0) {
                    // Play the creeper sound
                    for (UUID uuid : CreeperMorph.creeperSneakPlayers) {
                        Player player = Cosmetics.getInstance().getServer().getPlayer(uuid);
                        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "CREEPER_HISS", "ENTITY_CREEPER_PRIMED"), 1.0f, 1.0f);
                    }
                }

                CosmeticsTask.this.handleMagmaSlimes();
                CosmeticsTask.this.handleOtherMorphs();
            }

            if (Cosmetics.getInstance().isGadgetsEnabled()) {
                CosmeticsTask.this.handleFreezer();
                CosmeticsTask.this.handleGadgets();
            }

        }
    }

    private void handleArrowtrails(CosmeticsManager.CosmeticsSettings cosmeticsSettings) {
        if (Cosmetics.getInstance().isArrowTrailsEnabled() && !CosmeticsManager.isCosmeticDisabled(cosmeticsSettings.getUuid(), Cosmetics.CosmeticType.ARROW_TRAIL)) {
            if (cosmeticsSettings.getActiveArrowTrail() != null) {
                if (CosmeticsTask.this.tickTime % cosmeticsSettings.getActiveArrowTrail().getSpeed() == 0) {
                    Player player = Bukkit.getPlayer(cosmeticsSettings.getUuid());
                    // Spawn arrow trail
                    cosmeticsSettings.getActiveArrowTrail().spawnTrail(player);

                    // Give them back arrow, on Lobby servers only
                    if (Gberry.serverType == Gberry.ServerType.LOBBY) {
                        if (player.getInventory().getItem(9) == null) {
                            player.getInventory().setItem(9, new ItemStack(Material.ARROW));
                            player.updateInventory();
                        }
                    }
                }
            }
        }
    }

    private void handleRodTrails(CosmeticsManager.CosmeticsSettings cosmeticsSettings) {
        if (Cosmetics.getInstance().isRodTrailsEnabled() && !CosmeticsManager.isCosmeticDisabled(cosmeticsSettings.getUuid(), Cosmetics.CosmeticType.ROD_TRAIL)) {
            if (cosmeticsSettings.getActiveRodTrail() != null) {
                if (CosmeticsTask.this.tickTime % cosmeticsSettings.getActiveRodTrail().getSpeed() == 0) {
                    Player player = Bukkit.getPlayer(cosmeticsSettings.getUuid());
                    // Spawn rod trail
                    cosmeticsSettings.getActiveRodTrail().spawnTrail(player);
                }
            }
        }
    }

    private void handleGadgets(CosmeticsManager.CosmeticsSettings cosmeticsSettings) {
        if (Cosmetics.getInstance().isGadgetsEnabled() && !CosmeticsManager.isCosmeticDisabled(cosmeticsSettings.getUuid(), Cosmetics.CosmeticType.GADGET)) {
            if (cosmeticsSettings.getActiveGadget() != null) {
                if (CosmeticsTask.this.tickTime % 4 == 0) {
                    if (cosmeticsSettings.getActiveGadget().getName().equals("jetpack")) {
                        Player player = Bukkit.getPlayer(cosmeticsSettings.getUuid());
                        // Set the new name for the jetpack item
                        player.getInventory().setItem(5, ItemStackUtil.createItem(Material.BLAZE_ROD, ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "JetPack " + JetPackGadget.getJetPackFuelString(player)));

                        // Check if they are recharging
                        if (JetPackGadget.playerJetPackRecharging.contains(player.getUniqueId())) {
                            if (JetPackGadget.getJetPackFuel(player) > 0) {
                                // They are still recharging
                                JetPackGadget.playerJetPackFuel.put(cosmeticsSettings.getUuid(), JetPackGadget.getJetPackFuel(player) - 1.0D);
                            } else {
                                // They are no longer recharging
                                JetPackGadget.playerJetPackRecharging.remove(player.getUniqueId());

                                player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1f, 0.7331f);
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleParticles(CosmeticsManager.CosmeticsSettings cosmeticsSettings) {
        if (Cosmetics.getInstance().isParticlesEnabled() && !CosmeticsManager.isCosmeticDisabled(cosmeticsSettings.getUuid(), Cosmetics.CosmeticType.PARTICLE)) {
            if (cosmeticsSettings.getActiveParticle() != null) {
                if (CosmeticsTask.this.tickTime % cosmeticsSettings.getActiveParticle().getSpeed() == 0) {
                    // Spawn the particle
                    cosmeticsSettings.getActiveParticle().spawnParticle(Cosmetics.getInstance().getServer().getPlayer(cosmeticsSettings.getUuid()));
                }
            }
        }
    }

    private void handleMorphs(CosmeticsManager.CosmeticsSettings cosmeticsSettings) {
        if (Cosmetics.getInstance().isMorphsEnabled() && !CosmeticsManager.isCosmeticDisabled(cosmeticsSettings.getUuid(), Cosmetics.CosmeticType.MORPH)) {
            Morph activeMorph = cosmeticsSettings.getActiveMorph();
            // Check for spider morph (for the ability)
            if (activeMorph != null && activeMorph.getMorphType() == MorphUtil.MorphType.SPIDER) {
                Player player = Bukkit.getPlayer(cosmeticsSettings.getUuid());
                Location prevLoc = this.previousPlayerLocations.get(player.getUniqueId());
                if (prevLoc == null || prevLoc.distance(player.getLocation()) > 0.3D) {
                    // Make them climb the wall
                    if (isAbleToClimb(player) && player.getVelocity().length() > 0 && player.getLocation().getBlock().getType() == Material.AIR && player.isSneaking()) {
                        FlightGCheatManager.addToMapping(player, 20);
                        player.setVelocity(new Vector(0.0D, 0.25D, 0.0D));
                    }

                    // Reset prev location
                    this.previousPlayerLocations.put(player.getUniqueId(), player.getLocation());
                }
            }
        }
    }

    private void handleFreezer() {
        Map<UUID, Long> creatingFreezersCopy = new HashMap<>();
        creatingFreezersCopy.putAll(FreezerGadget.creatingFreezerTimes);
        for (Map.Entry<UUID, Long> entry : creatingFreezersCopy.entrySet()) {
            Long time = entry.getValue();
            Location freezerLocation = FreezerGadget.frozenPlayerLocations.get(entry.getKey());
            ParticleManager.getParticle("helix_trail").spawnParticle(freezerLocation);

            if (System.currentTimeMillis() - time >= 1000 * 2) {
                FreezerGadget.creatingFreezerTimes.remove(entry.getKey());

                for (Player pl : Cosmetics.getInstance().getServer().getPlayer(entry.getKey()).getWorld().getPlayers()) {
                    pl.sendBlockChange(freezerLocation, Material.ICE, (byte) 0);
                    pl.sendBlockChange(freezerLocation.clone().add(0.0D, 1.0D, 0.0D), Material.ICE, (byte) 0);
                    pl.sendBlockChange(freezerLocation.clone().add(0.0D, 2.0D, 0.0D), Material.STAINED_GLASS, (byte) 0);
                }

                FreezerGadget.frozenPlayerTimes.put(entry.getKey(), System.currentTimeMillis());
            }
        }

        creatingFreezersCopy.clear();
        creatingFreezersCopy.putAll(FreezerGadget.frozenPlayerTimes);

        for (Map.Entry<UUID, Long> entry : creatingFreezersCopy.entrySet()) {
            long time = entry.getValue();

            if (System.currentTimeMillis() - time >= 1000 * 10) {
                Location freezerLocation = FreezerGadget.frozenPlayerLocations.get(entry.getKey());
                ParticleManager.getParticle("helix_trail").spawnParticle(freezerLocation);

                if (System.currentTimeMillis() - time >= 1000 * 12) {
                    FreezerGadget.frozenPlayerTimes.remove(entry.getKey());
                    FreezerGadget.frozenPlayerLocations.remove(entry.getKey());

                    for (Player pl : Cosmetics.getInstance().getServer().getPlayer(entry.getKey()).getWorld().getPlayers()) {
                        pl.sendBlockChange(freezerLocation, freezerLocation.getBlock().getType(), (byte) 0);
                        pl.sendBlockChange(freezerLocation.clone().add(0.0D, 1.0D, 0.0D), freezerLocation.clone().add(0.0D, 1.0D, 0.0D).getBlock().getType(), (byte) 0);
                        pl.sendBlockChange(freezerLocation.clone().add(0.0D, 2.0D, 0.0D), freezerLocation.clone().add(0.0D, 2.0D, 0.0D).getBlock().getType(), (byte) 0);
                    }
                }
            }
        }
    }

    private void handleMagmaSlimes() {
        // Clone it, so no ConcurrentModificationException
        List<UUID> magmaJumpingPlayers = new ArrayList<>();
        magmaJumpingPlayers.addAll(MagmaCubeMorph.magmaJumpingPlayers);

        // Loop through all jumping players
        for (UUID uuid : magmaJumpingPlayers) {
            Player player = Cosmetics.getInstance().getServer().getPlayer(uuid);

            // Did this player log off?
            if (player == null) {
                MagmaCubeMorph.magmaJumpingPlayers.remove(uuid);
                continue;
            }

            Location prevLoc = this.previousPlayerLocations.get(player.getUniqueId());

            // They have a previous location
            if (prevLoc != null) {
                prevLoc.subtract(0.0D, 1.0D, 0.0D);
                Location loc = player.getLocation().subtract(0.0D, 1.0D, 0.0D);

                // If they are landing (prev location is in air, and current location is not)
                if (loc.getBlock().getType() != Material.AIR && prevLoc.getBlock().getType() == Material.AIR) {
                    // Remove them, and play the particle
                    MagmaCubeMorph.magmaJumpingPlayers.remove(player.getUniqueId());
                    LandTrail.land(player, ParticleLibrary.ParticleType.FLAME);
                }
            }

            // Reset previous location
            this.previousPlayerLocations.put(player.getUniqueId(), player.getLocation());
        }

        // Clone it, so no ConcurrentModificationException
        List<UUID> slimeJumpingPlayers = new ArrayList<>();
        slimeJumpingPlayers.addAll(SlimeMorph.slimeJumpingPlayers);

        // Loop through all jumping players
        for (UUID uuid : slimeJumpingPlayers) {
            Player player = Cosmetics.getInstance().getServer().getPlayer(uuid);
            Location prevLoc = this.previousPlayerLocations.get(player.getUniqueId());

            // They have a previous location
            if (prevLoc != null) {
                prevLoc.subtract(0.0D, 1.0D, 0.0D);
                Location loc = player.getLocation().subtract(0.0D, 1.0D, 0.0D);

                // If they are landing (prev location is in air, and current location is not)
                if (loc.getBlock().getType() != Material.AIR && prevLoc.getBlock().getType() == Material.AIR) {
                    // Remove them, and play the particle
                    SlimeMorph.slimeJumpingPlayers.remove(player.getUniqueId());
                    LandTrail.land(player, ParticleLibrary.ParticleType.SLIME);
                }
            }

            // Reset previous location
            this.previousPlayerLocations.put(player.getUniqueId(), player.getLocation());
        }
    }

    private void handleGadgets() {
        // Loop through all Gem Shower players
        Map<UUID, Long> gemShowerCopy = new HashMap<>();
        gemShowerCopy.putAll(GemShowerGadget.playerGemShowerTimes);
        for (Map.Entry<UUID, Long> entry : gemShowerCopy.entrySet()) {
            Long time = entry.getValue();
            if (System.currentTimeMillis() - time >= 1000 * 10) {
                GemShowerGadget.playerGemShowerTimes.remove(entry.getKey());
            } else {
                Player player = Cosmetics.getInstance().getServer().getPlayer(entry.getKey());

                Item gold = player.getWorld().dropItem(player.getEyeLocation().add(0.0D, 1.0D, 0.0D), ItemStackUtil.createItem(Material.GOLD_INGOT, String.valueOf(Math.random() * 100.0D)));
                gold.setMetadata("takeable", new FixedMetadataValue(Cosmetics.getInstance(), "takeable"));
                gold.setVelocity(new Vector(Math.random() - Math.random(), 0.5D, Math.random() - Math.random()));

                Item diamond = player.getWorld().dropItem(player.getEyeLocation().add(0.0D, 1.0D, 0.0D), ItemStackUtil.createItem(Material.DIAMOND, String.valueOf(Math.random() * 100.0D)));
                diamond.setMetadata("takeable", new FixedMetadataValue(Cosmetics.getInstance(), "takeable"));
                diamond.setVelocity(new Vector(Math.random() - Math.random(), 0.5D, Math.random() - Math.random()));

                Item emerald = player.getWorld().dropItem(player.getEyeLocation().add(0.0D, 1.0D, 0.0D), ItemStackUtil.createItem(Material.EMERALD, String.valueOf(Math.random() * 100.0D)));
                emerald.setMetadata("takeable", new FixedMetadataValue(Cosmetics.getInstance(), "takeable"));
                emerald.setVelocity(new Vector(Math.random() - Math.random(), 0.5D, Math.random() - Math.random()));

                Item iron = player.getWorld().dropItem(player.getEyeLocation().add(0.0D, 1.0D, 0.0D), ItemStackUtil.createItem(Material.IRON_INGOT, String.valueOf(Math.random() * 100.0D)));
                iron.setMetadata("takeable", new FixedMetadataValue(Cosmetics.getInstance(), "takeable"));
                iron.setVelocity(new Vector(Math.random() - Math.random(), 0.5D, Math.random() - Math.random()));

                this.gemShowerItems.put(gold, System.currentTimeMillis());
                this.gemShowerItems.put(diamond, System.currentTimeMillis());
                this.gemShowerItems.put(emerald, System.currentTimeMillis());
                this.gemShowerItems.put(iron, System.currentTimeMillis());
            }
        }

        // Reset gem shower items
        Map<Item, Long> gemShowerItemsCopy = new HashMap<>();
        gemShowerItemsCopy.putAll(this.gemShowerItems);
        for (Map.Entry<Item, Long> entry : gemShowerItemsCopy.entrySet()) {
            Long time = entry.getValue();
            if (System.currentTimeMillis() - time >= 1000 * 2) {
                entry.getKey().remove();
                this.gemShowerItems.remove(entry.getKey());
            }
        }
        // Reset old pee
        Map<Item, Long> peeItemsCopy = new HashMap<>();
        peeItemsCopy.putAll(this.peeItems);
        for (Map.Entry<Item, Long> entry : peeItemsCopy.entrySet()) {
            Long time = entry.getValue();
            if (System.currentTimeMillis() - time >= 1000 / 2) {
                entry.getKey().remove();
                this.peeItems.remove(entry.getKey());
            }
        }

        // Loop through all peeing players
        Map<UUID, Long> peeingCopy = new HashMap<>();
        peeingCopy.putAll(PeeGadget.peeingPlayers);
        for (Map.Entry<UUID, Long> entry : peeingCopy.entrySet()) {
            Long time = entry.getValue();
            if (System.currentTimeMillis() - time >= 1000 * 3) {
                PeeGadget.peeingPlayers.remove(entry.getKey());
            } else {
                Player player = Cosmetics.getInstance().getServer().getPlayer(entry.getKey());
                Item pee = player.getWorld().dropItem(player.getLocation().add(player.getLocation().getDirection()).add(0.0D, 0.25D, 0.0D), ItemStackUtil.createItem(Material.GOLD_BLOCK, String.valueOf(Math.random() * 100.0D)));
                pee.setMetadata("takeable", new FixedMetadataValue(Cosmetics.getInstance(), "takeable"));
                pee.setVelocity(player.getLocation().getDirection().multiply(0.5D));
                pee.setVelocity(new Vector(0.0D, 0.25D, 0.0D));
                this.peeItems.put(pee, System.currentTimeMillis());
            }
        }

        // Reset old paint trail paint
        Map<Location, Long> paintTrailLocationsCopy = new HashMap<>();
        paintTrailLocationsCopy.putAll(this.paintTrailLocations);
        for (Map.Entry<Location, Long> entry : paintTrailLocationsCopy.entrySet()) {
            Long time = entry.getValue();
            if (System.currentTimeMillis() - time >= 1000) {
                for (Player pl : Cosmetics.getInstance().getServer().getOnlinePlayers()) {
                    pl.sendBlockChange(entry.getKey(), entry.getKey().getBlock().getType(), entry.getKey().getBlock().getData());
                }
                this.paintTrailLocations.remove(entry.getKey());
            }
        }

        // Loop through all players that have their paint trail on
        Random random = new Random();
        for (UUID uuid : PaintTrailGadget.togglePaint) {
            if (CosmeticsManager.getCosmeticsSettings(uuid).getActiveGadget() != null && CosmeticsManager.getCosmeticsSettings(uuid).getActiveGadget() == GadgetManager.getGadget("paint_trail")) {
                Player player = Cosmetics.getInstance().getServer().getPlayer(uuid);

                Location prevLoc = this.previousPlayerLocations.get(player.getUniqueId());

                // They have a previous location
                if (prevLoc != null) {
                    prevLoc.subtract(0.0D, 1.0D, 0.0D);
                    Location loc = player.getLocation().subtract(0.0D, 1.0D, 0.0D);

                    // Check if it can be changed
                    if (loc.getBlock().getType().isOccluding() && loc.getBlock().getType().isSolid()
                            && !this.paintTrailLocations.containsKey(loc.getBlock().getLocation())) {
                        for (Player pl : player.getWorld().getPlayers()) {
                            // Send the block change
                            pl.sendBlockChange(loc, Material.WOOL, (byte) random.nextInt(15));
                        }

                        this.paintTrailLocations.put(loc.getBlock().getLocation(), System.currentTimeMillis());
                    }
                }

                this.previousPlayerLocations.put(player.getUniqueId(), player.getLocation());
            }
        }
    }

    private void handleOtherMorphs() {
        // Loop through infected players
        for (Map.Entry<UUID, Long> entry : ZombieMorph.zombieInfectParticlePlayers.entrySet()) {
            Player player = Cosmetics.getInstance().getServer().getPlayer(entry.getKey());

            // Player the sound
            if (CosmeticsTask.this.tickTime % 6 == 0) {
                player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "ZOMBIE_DEATH", "ENTITY_ZOMBIE_DEATH"), 1.0f, 1.0f);
            }

            // If it's been 1.4 seconds
            if ((System.currentTimeMillis() - entry.getValue()) / 1000 >= 1.4) {
                // Remove morph
                new MorphUtil(MorphUtil.MorphType.ZOMBIE, player).sendServerRemoveMorph();

                ZombieMorph.zombieInfectParticlePlayers.remove(entry.getKey());
            } else {
                // Or, spawn the particles
                ZombieMorph.zombieInfectParticle.spawnParticle(player);
            }
        }

        // For when they are 'healing' back to a human
        for (Map.Entry<UUID, Long> entry : ZombieMorph.zombieInfectParticle2Players.entrySet()) {
            Player player = Cosmetics.getInstance().getServer().getPlayer(entry.getKey());

            // Play the sound
            if (CosmeticsTask.this.tickTime % 4 == 0) {
                player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "ZOMBIE_INFECT", "ENTITY_ZOMBIE_INFECT"), 1.0f, 1.0f);
            }

            // If it's been 1.4 seconds
            if ((System.currentTimeMillis() - entry.getValue()) / 1000 >= 1.4) {
                // Morph them into Zombie
                new MorphUtil(MorphUtil.MorphType.ZOMBIE, player).sendServerSetMorph();

                ZombieMorph.zombieInfectParticle2Players.remove(entry.getKey());
            } else {
                // Play the helix particle
                ParticleManager.getParticle("helix_trail").spawnParticle(player);
            }
        }

        // Loop through squid sneaked players
        ParticleLibrary particle = new ParticleLibrary(ParticleLibrary.ParticleType.WATER_SPLASH, 0, 1, 0);
        for (UUID uuid : SquidMorph.squidSneakPlayers) {
            Player player = Cosmetics.getInstance().getServer().getPlayer(uuid);

            List<Location> locations = new ArrayList<>();
            // Play it another x2 times
            for (int i2 = 0; i2 < 2; i2++) {
                // Spawn particles twice, adding the value from loop
                locations.add(Particle.getParticleLocation(player.getLocation().add(player.getLocation().getDirection().multiply(i2)), 0.3D));
                locations.add(Particle.getParticleLocation(player.getLocation().add(player.getLocation().getDirection().multiply(-i2)), 0.3D));
            }

            particle.sendToLocation(player, locations, 5);

            // Play splash sound
            if (CosmeticsTask.this.tickTime % 10 == 0) {
                player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "SPLASH", "ENTITY_GENERIC_SPLASH"), 1.0f, 1.0f);
            }
        }
    }

    // Check if the player should be allowed to climb the wall next to them
    private boolean isAbleToClimb(Player player) {
        Block facing = player.getLocation().add(player.getLocation().getDirection()).getBlock();

        Block north = player.getLocation().getBlock().getRelative(BlockFace.NORTH);
        Block south = player.getLocation().getBlock().getRelative(BlockFace.SOUTH);
        Block east = player.getLocation().getBlock().getRelative(BlockFace.EAST);
        Block west = player.getLocation().getBlock().getRelative(BlockFace.WEST);

        return ((north.getType() != Material.AIR && north.getType() != Material.STEP && north.getType() != Material.WOOD_STEP && north.getType() != Material.CARPET && north.getType() != Material.SNOW) ||
                (south.getType() != Material.AIR && south.getType() != Material.STEP && south.getType() != Material.WOOD_STEP && south.getType() != Material.CARPET && south.getType() != Material.SNOW) ||
                (east.getType() != Material.AIR && east.getType() != Material.STEP && south.getType() != Material.WOOD_STEP && east.getType() != Material.CARPET && east.getType() != Material.SNOW) ||
                (west.getType() != Material.AIR && west.getType() != Material.STEP && south.getType() != Material.WOOD_STEP && west.getType() != Material.CARPET && west.getType() != Material.SNOW)) &&
                (facing.getType() != Material.AIR && facing.getType() != Material.STEP && facing.getType() != Material.WOOD_STEP && facing.getType() != Material.CARPET && facing.getType() != Material.SNOW);
    }

    private boolean isDifferentBlock(Location loc1, Location loc2) {
        return Math.round(loc1.getX()) != Math.round(loc2.getX()) && Math.round(loc1.getY()) != Math.round(loc2.getY()) &&
                Math.round(loc1.getZ()) != Math.round(loc2.getZ());
    }

}
