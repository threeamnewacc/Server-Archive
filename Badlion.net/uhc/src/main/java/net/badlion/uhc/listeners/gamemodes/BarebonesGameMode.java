package net.badlion.uhc.listeners.gamemodes;

import net.badlion.combattag.events.CombatTagKilledEvent;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.events.GameStartEvent;
import net.badlion.uhc.events.GoldenHeadRecipeEvent;
import net.badlion.uhc.events.PlayerDeathItemEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BarebonesGameMode implements GameMode {

    private Random random = new Random();

    public BarebonesGameMode() {
        BadlionUHC.getInstance().getConfigurator().getBooleanOption(BadlionUHC.CONFIG_OPTIONS.NETHER.name()).setValue("false");
    }

    @EventHandler
    public void onFishingRodCrafted(CraftItemEvent event) {
        if (event.getRecipe().getResult().getType() == Material.ENCHANTMENT_TABLE
                    || event.getRecipe().getResult().getType() == Material.ANVIL
                    || event.getRecipe().getResult().getType() == Material.GOLDEN_APPLE) {
            event.getInventory().setResult(new ItemStack(Material.AIR));

            ((Player) event.getView().getPlayer()).sendMessage(ChatColor.RED + "You cannot craft this");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onUseFishingRod(PlayerInteractEvent event) {
        if (event.getItem() != null && (event.getItem().getType() == Material.ENCHANTMENT_TABLE || event.getItem().getType() == Material.ANVIL)) {
            event.getPlayer().setItemInHand(null);
            event.getPlayer().sendFormattedMessage(ChatColor.RED + "This item is not allowed");
            event.getPlayer().updateInventory();
        } else if (event.getClickedBlock() != null && (event.getClickedBlock().getType() == Material.ANVIL || event.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE)) {
            event.setCancelled(true);
            event.getPlayer().sendFormattedMessage(ChatColor.RED + "You cannot use this");
        }
    }

    @EventHandler
    public void onMiningOre(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.DIAMOND_ORE || event.getBlock().getType() == Material.EMERALD_ORE) {
            event.setCancelled(true);

            event.getBlock().setType(Material.AIR);
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.IRON_INGOT));

            // Give EXP still
            BadlionUHC.getInstance().createExpOrb(event.getBlock().getLocation(), this.random.nextInt(5) + 3);
        } else if (event.getBlock().getType() == Material.REDSTONE_ORE || event.getBlock().getType() == Material.GLOWING_REDSTONE_ORE) {
            event.setCancelled(true);

            event.getBlock().setType(Material.AIR);
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.IRON_INGOT));

            // Give EXP still
            BadlionUHC.getInstance().createExpOrb(event.getBlock().getLocation(), this.random.nextInt(5) + 1);
        } else if (event.getBlock().getType() == Material.LAPIS_ORE) {
            event.setCancelled(true);

            event.getBlock().setType(Material.AIR);
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.IRON_INGOT));

            // Give EXP still
            BadlionUHC.getInstance().createExpOrb(event.getBlock().getLocation(), this.random.nextInt(4) + 2);
        } else if (event.getBlock().getType() == Material.GOLD_ORE) {
            event.setCancelled(true);

            event.getBlock().setType(Material.AIR);
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.IRON_INGOT));

            // Give EXP still
            BadlionUHC.getInstance().createExpOrb(event.getBlock().getLocation(), 1);
        }
    }

    @EventHandler
    public void onDiamondBlastMined(EntityExplodeEvent event) {
        Iterator<Block> it = event.blockList().iterator();
        while (it.hasNext()) {
            Block block = it.next();
            if (block.getType() == Material.DIAMOND_ORE || block.getType() == Material.EMERALD_ORE
                        || block.getType() == Material.REDSTONE_ORE || block.getType() == Material.LAPIS_ORE
                        || block.getType() == Material.GLOWING_REDSTONE_ORE || block.getType() == Material.GOLD_ORE) {
                block.setType(Material.AIR);
                it.remove();
            }
        }
    }

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.STARTED) return;

		ItemStack[] itemStacks = new ItemStack[] {new ItemStack(Material.DIAMOND), new ItemStack(Material.GOLDEN_APPLE), new ItemStack(Material.ARROW, 32), new ItemStack(Material.STRING, 2)};

		for (ItemStack itemStack : itemStacks) {
			PlayerDeathItemEvent playerDeathItemEvent = new PlayerDeathItemEvent(itemStack);
			BadlionUHC.getInstance().getServer().getPluginManager().callEvent(playerDeathItemEvent);

			if (!playerDeathItemEvent.isCancelled()) {
				event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), playerDeathItemEvent.getItemStack());
			}
		}
	}

	@EventHandler
	public void onCombatTagDeath(CombatTagKilledEvent event) {
        ItemStack[] itemStacks = new ItemStack[] {new ItemStack(Material.DIAMOND), new ItemStack(Material.GOLDEN_APPLE), new ItemStack(Material.ARROW, 32), new ItemStack(Material.STRING, 2)};

        for (ItemStack itemStack : itemStacks) {
            PlayerDeathItemEvent playerDeathItemEvent = new PlayerDeathItemEvent(itemStack);
            BadlionUHC.getInstance().getServer().getPluginManager().callEvent(playerDeathItemEvent);

            if (!playerDeathItemEvent.isCancelled()) {
                event.getLoggerNPC().getEntity().getWorld().dropItemNaturally(event.getLoggerNPC().getEntity().getLocation(), playerDeathItemEvent.getItemStack());
            }
        }
	}

    @EventHandler
    public void onGoldenHeadRecipe(GoldenHeadRecipeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        BadlionUHC.getInstance().getConfigurator().getBooleanOption(BadlionUHC.CONFIG_OPTIONS.NETHER.name()).setValue("false");
    }

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.BONE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Barebones");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- Nether is disabled");
        lore.add(ChatColor.AQUA + "- All ores except iron/coal drop an iron ingot");
        lore.add(ChatColor.AQUA + "- On player death they drop 1 diamond, 1 golden apple, 32 arrows, & 2 string");
        lore.add(ChatColor.AQUA + "- You cannot craft Enchanting Tables, Anvils, or Golden Apples");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "http://reddit.com/u/Audicyy";
    }

    @Override
    public void unregister() {
        CraftItemEvent.getHandlerList().unregister(this);
        BlockBreakEvent.getHandlerList().unregister(this);
        EntityExplodeEvent.getHandlerList().unregister(this);
        PlayerDeathEvent.getHandlerList().unregister(this);
        GoldenHeadRecipeEvent.getHandlerList().unregister(this);
        GameStartEvent.getHandlerList().unregister(this);
        PlayerInteractEvent.getHandlerList().unregister(this);
    }

}
