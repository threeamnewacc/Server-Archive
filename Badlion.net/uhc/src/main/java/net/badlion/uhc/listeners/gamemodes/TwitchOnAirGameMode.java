package net.badlion.uhc.listeners.gamemodes;

import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.disguise.events.PlayerDisguiseEvent;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.UHCTeam;
import net.badlion.uhc.events.GameStartEvent;
import net.badlion.uhc.events.RulesEvent;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.managers.UHCTeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.json.simple.JSONObject;

import java.util.*;

public class TwitchOnAirGameMode implements GameMode {

    private Random random = new Random();
    private BukkitTask task;

    public TwitchOnAirGameMode() {
        this.task = new BukkitRunnable() {
            public void run() {
                TwitchOnAirGameMode.this.sendAPIData();
            }
        }.runTaskTimer(BadlionUHC.getInstance(), 0, 100);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.GRAVEL) {
            if (this.random.nextInt(10) <= 2) {
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.FLINT));
            }
        } else if (event.getBlock().getType() == Material.LEAVES && event.getBlock().getData() % 4 == 0) {
            if (this.random.nextInt(200) == 0) {
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
            }
        } else if (event.getBlock().getType() == Material.LEAVES_2 && event.getBlock().getData() % 4 == 1) {
            if (this.random.nextInt(200) == 0) {
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
            }
        }
    }

    @EventHandler
    public void onLeafDecay(LeavesDecayEvent event) {
        if (event.getBlock().getType() == Material.LEAVES && event.getBlock().getData() % 4 == 0) {
            if (this.random.nextInt(200) == 0) {
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
            }
        } else if (event.getBlock().getType() == Material.LEAVES_2 && event.getBlock().getData() % 4 == 1) {
            if (this.random.nextInt(200) == 0) {
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
            }
        }
    }

    // Prevent Tier 2 potions
    @EventHandler
    public void potionBrewing(final InventoryClickEvent event) {
        if (event.getInventory().getType().equals(InventoryType.BREWING)) { // Brewing Inventory
            for (ItemStack invItem : event.getInventory().getContents()) {
                if (invItem == null) continue;

                if (invItem.getDurability() != 0 && Potion.fromItemStack(invItem) != null) {
                    if (event.isShiftClick() && event.getCurrentItem() != null && event.getCurrentItem().getType().equals(Material.GLOWSTONE_DUST)) {
                        event.setCancelled(true);
                        return;
                    }

                    if (event.getRawSlot() == 3) {
                        if (event.getCursor() != null && event.getCursor().getType().equals(Material.GLOWSTONE_DUST)) {
                            event.setCancelled(true);
                            return;
                        }

                        if (event.getRawSlot() == 3 && event.getHotbarButton() > -1 && event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) != null
                                    && event.getWhoClicked().getInventory().getItem(event.getHotbarButton()).getType().equals(Material.GLOWSTONE_DUST)) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void inventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getType().equals(InventoryType.BREWING)) { // Brewing Inventory
            for (ItemStack invItem : event.getInventory().getContents()) {
                if (invItem == null) continue;

                if (invItem.getDurability() != 0 && invItem.getType() == Material.POTION && Potion.fromItemStack(invItem) != null) {
                    if (event.getOldCursor().getType().equals(Material.GLOWSTONE_DUST) && event.getRawSlots().contains(new Integer(3))) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRules(RulesEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onGameStartEvent(GameStartEvent event) {
        // Fix colors of teams
        List<ChatColor> colors = Arrays.asList(BadlionUHC.validTeamColors);

        int i = 0;
        for (UHCTeam uhcTeam : UHCTeamManager.getAllAlivePlayingTeams()) {
            uhcTeam.setChatColor(colors.get(i++ % colors.size()));
        }

        for (UHCPlayer uhcPlayer : UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.PLAYER)) {
            Player pl = uhcPlayer.getPlayer();
            Scoreboard scoreboard = pl.getScoreboard();

            uhcPlayer.handleColorScoreboard(scoreboard);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (BadlionUHC.getInstance().getState().ordinal() >= BadlionUHC.BadlionUHCState.COUNTDOWN.ordinal()) {
            UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId());
            Scoreboard scoreboard = event.getPlayer().getScoreboard();

            uhcPlayer.handleColorScoreboard(scoreboard);
        }
    }

    @EventHandler
    public void onDisguise(PlayerDisguiseEvent event) {
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.RED + "Disguise is not allowed for this game mode");
    }

    private void sendAPIData() {
        final Map<String, String> uhcPlayers = new HashMap<>();
        for (Player pl : BadlionUHC.getInstance().getServer().getOnlinePlayers()) {
            UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(pl.getUniqueId());

            uhcPlayers.put(pl.getName(), uhcPlayer.getState().name());
        }

        new BukkitRunnable() {
            public void run() {
                JSONObject jsonObject = new JSONObject(uhcPlayers);

                try {
                    HTTPCommon.executePUTRequest("http://127.0.0.1:20453/UpdatePlayers", jsonObject);
                } catch (HTTPRequestFailException e) {
                    Bukkit.getLogger().info("" + e.getResponseCode());
                    Bukkit.getLogger().info(e.getResponse());
                }
            }
        }.runTaskAsynchronously(BadlionUHC.getInstance());
    }

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Twitch on Air");

        List<String> lore = new ArrayList<>();

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "http://reddit.com/u/climbing";
    }

    @Override
    public void unregister() {
        BlockBreakEvent.getHandlerList().unregister(this);
        LeavesDecayEvent.getHandlerList().unregister(this);
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryDragEvent.getHandlerList().unregister(this);
        RulesEvent.getHandlerList().unregister(this);
        GameStartEvent.getHandlerList().unregister(this);
        PlayerJoinEvent.getHandlerList().unregister(this);
        PlayerDisguiseEvent.getHandlerList().unregister(this);

        this.task.cancel();
    }

}
