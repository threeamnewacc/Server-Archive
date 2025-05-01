package net.badlion.cosmetics.managers;

import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.gadgets.*;
import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;

public class GadgetManager implements Listener {

    private static Map<String, Gadget> gadgets = new LinkedHashMap<>();

    static {
        GadgetManager.gadgets.put("ender_pearl", new EnderPearlGadget());
        GadgetManager.gadgets.put("firework", new FireworkGadget());
        GadgetManager.gadgets.put("gapple_gun", new GappleGunGadget());
        GadgetManager.gadgets.put("jetpack", new JetPackGadget());
        GadgetManager.gadgets.put("paintball_gun", new PaintballGadget());
        GadgetManager.gadgets.put("melon_bomb", new MelonBombGadget());
        GadgetManager.gadgets.put("water_bomb", new WaterBombGadget());
        GadgetManager.gadgets.put("ender_butt", new EnderButtGadget());
        GadgetManager.gadgets.put("tnt", new TNTGadget());
        GadgetManager.gadgets.put("lion_blaster", new LionBlasterGadget());

        GadgetManager.gadgets.put("railgun", new RailgunGadget());
        GadgetManager.gadgets.put("paint_trail", new PaintTrailGadget());
        GadgetManager.gadgets.put("pee", new PeeGadget());
        GadgetManager.gadgets.put("freezer", new FreezerGadget());
        GadgetManager.gadgets.put("exploding_bow", new ExplodingBowGadget());
        GadgetManager.gadgets.put("gem_shower", new GemShowerGadget());
    }

    private ParticleLibrary explosionParticle = new ParticleLibrary(ParticleLibrary.ParticleType.EXPLOSION_HUGE, 1, 1, 0);
    private ParticleLibrary spellParticle = new ParticleLibrary(ParticleLibrary.ParticleType.SPELL, 0, 1, 0);
    private ParticleLibrary flameParticle = new ParticleLibrary(ParticleLibrary.ParticleType.FLAME, 0, 1, 0);

    public static Gadget getGadget(String gadgetName) {
        return GadgetManager.gadgets.get(gadgetName.toLowerCase());
    }

    public static void addGadget(final CommandSender sender, final Player player, final String gadgetName) {
        GadgetManager.addGadget(sender, player, gadgetName, true);
    }

    public static void addGadget(final CommandSender sender, final Player player, final String gadgetName, final boolean verbose) {
        final Gadget gadget = GadgetManager.getGadget(gadgetName);

        // Is gadget valid?
        if (gadget == null) {
            sender.sendMessage(ChatColor.RED + "Gadget " + gadgetName + " not found.");
            return;
        }

        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());
        if (cosmeticsSettings == null) {
            cosmeticsSettings = CosmeticsManager.createCosmeticsSettings(player.getUniqueId());
        }

        // Does player already have the gadget?
        if (cosmeticsSettings.hasGadget(gadget)) {
            sender.sendMessage(ChatColor.RED + player.getName() + " already has the gadget " + gadget.getName());
            return;
        }

        cosmeticsSettings.addGadget(gadget);

        // Send message to command sender
        if (verbose) {
            sender.sendMessage(ChatColor.GREEN + "Gave gadget " + gadget.getName() + " to " + player.getName());
        }
    }

    public static void equipGadget(Player player, String gadgetName) {
        Gadget gadget = GadgetManager.getGadget(gadgetName);
        CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());

        // Is gadget valid?
        if (gadget == null) {
            player.sendMessage(ChatColor.RED + "Gadget " + gadgetName + " not found.");
            return;
        }

        // Do they already have the gadget equipped?
        if (cosmeticsSettings.getActiveGadget() == gadget) {
            player.sendMessage(ChatColor.RED + "You already have + " + gadget.getName() + " equipped!");
            return;
        }

        cosmeticsSettings.setActiveGadget(gadget);
        gadget.giveGadget(player);
        player.updateInventory();
    }

    public static Map<String, Gadget> getGadgets() {
        return gadgets;
    }

    @EventHandler
    public void onPaintballLand(final ProjectileHitEvent event) {
        if (!Cosmetics.getInstance().isGadgetsEnabled()) {
            return;
        }
        if (event.getEntity().hasMetadata("paintballegg")) {
            Location location = event.getEntity().getLocation();
            final ArrayList<Block> blocks = new ArrayList<>();

            for (Block block : getBlocks(location, 2.1D, 256.0D).keySet()) {
                if (block.getType().isSolid() && block.getType().isOccluding()) {
                    blocks.add(block);
                }
            }

            for (Block block : blocks) {
                for (Player players : block.getWorld().getPlayers()) {
                    int type = new Random().nextInt(15);
                    players.sendBlockChange(block.getLocation(), Material.WOOL, (byte) type);
                }
            }

            Bukkit.getScheduler().runTaskLater(Cosmetics.getInstance(), new Runnable() {
                @Override
                public void run() {
                    List<Location> blockLocations = new ArrayList<>();
                    for (Block block : blocks) {
                        blockLocations.add(block.getLocation());
                        for (Player players : block.getWorld().getPlayers()) {
                            players.sendBlockChange(block.getLocation(), block.getLocation().getBlock().getType(), block.getLocation().getBlock().getData());
                        }
                    }

                    GadgetManager.this.spellParticle.sendToLocation((Player) event.getEntity().getShooter(), blockLocations);

                    blocks.clear();
                }
            }, 20L);
        } else if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player) {
            CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(((Player) event.getEntity().getShooter()).getUniqueId());
            if (cosmeticsSettings.getActiveGadget() != null && cosmeticsSettings.getActiveGadget() == getGadget("exploding_bow")) {
                event.getEntity().remove();
                this.explosionParticle.sendToLocation((Player) event.getEntity().getShooter(), event.getEntity().getLocation(), true, 0.3D, 3);

                ((Player) event.getEntity().getShooter()).getInventory().setItem(10, ItemStackUtil.createItem(Material.ARROW, ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Exploding Bow Ammo"));
            }
        }
    }

    public HashMap<Block, Double> getBlocks(Location loc, double dRadius, double heightLimit) {
        HashMap<Block, Double> blockList = new HashMap<>();
        int iRadius = (int) dRadius + 1;
        for (int x = -iRadius; x <= iRadius; x++) {
            for (int z = -iRadius; z <= iRadius; z++) {
                for (int y = -iRadius; y <= iRadius; y++) {
                    if (Math.abs(y) <= heightLimit) {
                        Block curBlock = loc.getWorld().getBlockAt((int) (loc.getX() + x), (int) (loc.getY() + y), (int) (loc.getZ() + z));
                        double d = loc.toVector().subtract(curBlock.getLocation().add(0.5D, 0.5D, 0.5D).toVector()).length();
                        if (d <= dRadius) {
                            blockList.put(curBlock, 1.0D - d / dRadius);
                        }
                    }
                }
            }
        }
        return blockList;
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            if (!Cosmetics.getInstance().isGadgetsEnabled()) {
                return;
            }
            final Player player = event.getPlayer();
            CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());
            Gadget gadget = cosmeticsSettings.getActiveGadget();

            if (gadget == null) return;

            if (gadget.getName().equals("ender_pearl")) {
                gadget.giveGadget(player);
                player.updateInventory();
            }

            if (gadget.getName().equals("ender_butt")) {
                player.spigot().setCollidesWithEntities(true);
                gadget.giveGadget(player);
                player.updateInventory();
            }
            Bukkit.getScheduler().runTaskLater(Cosmetics.getInstance(), new Runnable() {
                @Override
                public void run() {
                    flameParticle.sendToLocation(player, player.getLocation(), true, 0.3, 3);
                }
            }, 1L);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (!Cosmetics.getInstance().isGadgetsEnabled()) {
            return;
        }

        if (event.getItem() != null) {
            CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());
            if (cosmeticsSettings == null || cosmeticsSettings.getActiveGadget() == null) {
                return;
            }

            cosmeticsSettings.getActiveGadget().handlePlayerInteractEvent(event);
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof EnderPearl && event.getEntity().getShooter() instanceof Player) {
            if (!Cosmetics.getInstance().isGadgetsEnabled()) {
                return;
            }
            Player player = (Player) event.getEntity().getShooter();
            CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(player.getUniqueId());
            Gadget gadget = cosmeticsSettings.getActiveGadget();

            if (gadget == null) {
                return;
            }

            if (gadget.getName().equals("ender_butt")) {
                player.spigot().setCollidesWithEntities(false);
                event.getEntity().setPassenger(player);
            }
        }
    }

}
