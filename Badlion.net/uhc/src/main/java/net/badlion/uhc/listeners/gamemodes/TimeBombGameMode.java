package net.badlion.uhc.listeners.gamemodes;

import net.badlion.combattag.LoggerNPC;
import net.badlion.combattag.events.CombatTagDestroyEvent;
import net.badlion.combattag.events.CombatTagDropInventoryEvent;
import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.events.GoldenHeadDroppedEvent;
import net.badlion.uhc.events.PlayerDeathItemEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class TimeBombGameMode implements GameMode {

    private DoubleChestInventory lastDeathInventory;
    private Random random = new Random();

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
	    if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.STARTED) return;

        Player player = event.getEntity().getPlayer();

        // Set chests and air
        player.getLocation().getBlock().setType(Material.CHEST);
        player.getLocation().add(0, 1, 0).getBlock().setType(Material.AIR);
        player.getLocation().add(1, 0, 0).getBlock().setType(Material.CHEST);
        player.getLocation().add(1, 1, 0).getBlock().setType(Material.AIR);

        // Put items in chest
        Chest chest = (Chest) player.getLocation().getBlock().getState();
        if (chest.getInventory().getHolder() instanceof DoubleChest) {
            DoubleChest doubleChest = (DoubleChest) chest.getInventory().getHolder();
            DoubleChestInventory doubleChestInventory = (DoubleChestInventory) doubleChest.getInventory();

            for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    continue;
                }

                doubleChestInventory.addItem(itemStack);
            }

            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    continue;
                }

                doubleChestInventory.addItem(itemStack);
            }

            this.lastDeathInventory = doubleChestInventory; // Store for later use
        }

        this.lastDeathInventory.addItem(ItemStackUtil.createGoldenHead());

	    // Drop EXP orb
	    player.getWorld().spawn(player.getLocation(), ExperienceOrb.class).setExperience(player.getLevel() * 7);

        new TimeBomb(player.getDisguisedName(), player.getLocation()).runTaskLater(BadlionUHC.getInstance(), 20L * 30);

        event.getDrops().clear();
    }

    @EventHandler
    public void onCombatTagDie(CombatTagDestroyEvent event) {
        if (event.getReason() == LoggerNPC.REMOVE_REASON.DEATH) {
            Zombie zombie = event.getLoggerNPC().getEntity();

            // Set chests and air
            zombie.getLocation().getBlock().setType(Material.CHEST);
            zombie.getLocation().add(0, 1, 0).getBlock().setType(Material.AIR);
            zombie.getLocation().add(1, 0, 0).getBlock().setType(Material.CHEST);
            zombie.getLocation().add(1, 1, 0).getBlock().setType(Material.AIR);

            // Put items in chest
            Chest chest = (Chest) zombie.getLocation().getBlock().getState();
            if (chest.getInventory().getHolder() instanceof DoubleChest) {
                DoubleChest doubleChest = (DoubleChest) chest.getInventory().getHolder();
                DoubleChestInventory doubleChestInventory = (DoubleChestInventory) doubleChest.getInventory();

                for (ItemStack itemStack : event.getLoggerNPC().getInventory()) {
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        continue;
                    }

                    doubleChestInventory.addItem(itemStack);
                }

                for (ItemStack itemStack : event.getLoggerNPC().getArmor()) {
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        continue;
                    }

                    doubleChestInventory.addItem(itemStack);
                }

                this.lastDeathInventory = doubleChestInventory; // Store for later use
            }

            this.lastDeathInventory.addItem(ItemStackUtil.createGoldenHead());

	        // Drop EXP orb
	        zombie.getWorld().spawn(zombie.getLocation(), ExperienceOrb.class).setExperience(event.getLoggerNPC().getPlayer().getLevel() * 7);

            new TimeBomb(zombie.getCustomName(), zombie.getLocation()).runTaskLater(BadlionUHC.getInstance(), 20L * 30);
        }
    }

    @EventHandler
    public void onCombatTagDropItems(CombatTagDropInventoryEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onGoldenHeadMade(GoldenHeadDroppedEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDeathItemEvent event) {
        this.lastDeathInventory.addItem(event.getItemStack());
        event.setCancelled(true);
    }

	@EventHandler
	public void onBedrockExplode(EntityExplodeEvent event) {
		Iterator<Block> it = event.blockList().iterator();
		while (it.hasNext()) {
			Block block = it.next();
			if (block.getType() == Material.BEDROCK) {
				// Should stop bedrock being broken,
				// Never knew it could be blown up lol
				it.remove();
			}
		}
	}

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.TNT);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Time Bomb");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- All items on death are put into a double chest");
        lore.add(ChatColor.AQUA + "- The body/chest blow up 30 seconds after dying");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "http://reddit.com/u/Tman1829765";
    }

    private class TimeBomb extends BukkitRunnable {

        private Location location;
        private String name;

        public TimeBomb(String name, Location location) {
            this.name = name;
            this.location = location;
        }

        @Override
        public void run() {
            this.location.getWorld().spigot().strikeLightning(this.location, true);
            // Three chunks
            this.location.getWorld().playSound(this.location, EnumCommon.getEnumValueOf(Sound.class, "AMBIENCE_THUNDER", "ENTITY_LIGHTNING_THUNDER"), 3f, 0.8F + TimeBombGameMode.this.random.nextFloat() * 0.2F);
            this.location.getWorld().createExplosion(this.location, 10f);
            Gberry.broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.WHITE + "TimeBomb" + ChatColor.DARK_RED + "] " + ChatColor.WHITE + this.name + "'s corpse has exploded!");
        }

    }

    @Override
    public void unregister() {
        GoldenHeadDroppedEvent.getHandlerList().unregister(this);
        PlayerDeathEvent.getHandlerList().unregister(this);
        PlayerDeathItemEvent.getHandlerList().unregister(this);
    }

}
