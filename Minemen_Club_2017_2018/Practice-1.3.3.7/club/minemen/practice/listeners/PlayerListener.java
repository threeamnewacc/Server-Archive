// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.listeners;

import club.minemen.core.event.player.MinemanRetrieveEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import java.util.Iterator;
import org.bukkit.plugin.Plugin;
import club.minemen.practice.ffa.killstreak.KillStreak;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import java.util.UUID;
import java.util.Map;
import org.bukkit.inventory.PlayerInventory;
import club.minemen.practice.kit.Kit;
import org.bukkit.inventory.Inventory;
import org.bukkit.command.CommandSender;
import club.minemen.practice.kit.PlayerKit;
import club.minemen.practice.match.MatchState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.GameMode;
import org.bukkit.event.player.PlayerInteractEvent;
import club.minemen.practice.events.PracticeEvent;
import club.minemen.practice.party.Party;
import org.bukkit.event.player.PlayerQuitEvent;
import club.minemen.core.util.finalutil.CC;
import club.minemen.core.util.finalutil.StringUtil;
import org.bukkit.event.player.PlayerJoinEvent;
import club.minemen.practice.match.Match;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import club.minemen.practice.player.PlayerData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import club.minemen.practice.player.PlayerState;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import club.minemen.practice.Practice;
import org.bukkit.event.Listener;

public class PlayerListener implements Listener
{
    private final Practice plugin;
    
    public PlayerListener() {
        this.plugin = Practice.getInstance();
    }
    
    @EventHandler
    public void onPlayerItemConsume(final PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.GOLDEN_APPLE) {
            if (!event.getItem().hasItemMeta() || !event.getItem().getItemMeta().getDisplayName().contains("Golden Head")) {
                return;
            }
            final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(event.getPlayer().getUniqueId());
            if (playerData.getPlayerState() == PlayerState.FIGHTING) {
                final Player player = event.getPlayer();
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
                player.setFoodLevel(Math.min(player.getFoodLevel() + 6, 20));
            }
        }
    }
    
    @EventHandler
    public void onRegenerate(final EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED) {
            return;
        }
        final Player player = (Player)event.getEntity();
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            final Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
            if (match.getKit().isBuild()) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        this.plugin.getPlayerManager().createPlayerData(player);
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
        player.sendMessage("");
        player.sendMessage(StringUtil.getBorderLine());
        player.sendMessage("");
        player.sendMessage(StringUtil.center(CC.PRIMARY + "Welcome to Minemen Club Practice."));
        player.sendMessage("");
        player.sendMessage(StringUtil.getBorderLine());
        player.sendMessage("");
        player.sendMessage(StringUtil.center(CC.BD_PURPLE + "Want to earn " + CC.BL_PURPLE + "FREE MONEY?"));
        player.sendMessage(StringUtil.center(CC.BL_PURPLE + "Premium Matches " + CC.BD_PURPLE + "are now LIVE!"));
        player.sendMessage(StringUtil.center(CC.BD_PURPLE + "Purchase a " + CC.BL_PURPLE + "Rank " + CC.BD_PURPLE + "or " + CC.BL_PURPLE + "Premium Matches " + CC.BD_PURPLE + "now!"));
        player.sendMessage(StringUtil.center(CC.BL_PURPLE + " https://store.minemen.club "));
        player.sendMessage("");
    }
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            return;
        }
        switch (playerData.getPlayerState()) {
            case FIGHTING: {
                this.plugin.getMatchManager().removeFighter(player, playerData, false);
                break;
            }
            case SPECTATING: {
                this.plugin.getMatchManager().removeSpectator(player);
                break;
            }
            case EDITING: {
                this.plugin.getEditorManager().removeEditor(player.getUniqueId());
                break;
            }
            case QUEUE: {
                if (party == null) {
                    this.plugin.getQueueManager().removePlayerFromQueue(player);
                    break;
                }
                if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                    this.plugin.getQueueManager().removePartyFromQueue(party);
                    break;
                }
                break;
            }
            case FFA: {
                this.plugin.getFfaManager().removePlayer(player);
                break;
            }
            case EVENT: {
                final PracticeEvent practiceEvent = this.plugin.getEventManager().getEventPlaying(player);
                if (practiceEvent != null) {
                    practiceEvent.leave(player, true);
                    break;
                }
                break;
            }
        }
        this.plugin.getTournamentManager().leaveTournament(player);
        this.plugin.getPartyManager().leaveParty(player);
        this.plugin.getMatchManager().removeMatchRequests(player.getUniqueId());
        this.plugin.getPartyManager().removePartyInvites(player.getUniqueId());
        this.plugin.getPlayerManager().removePlayerData(player.getUniqueId());
    }
    
    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() == PlayerState.SPECTATING) {
            event.setCancelled(true);
        }
        if (event.getAction().name().endsWith("_BLOCK")) {
            if (event.getClickedBlock().getType().name().contains("SIGN") && event.getClickedBlock().getState() instanceof Sign) {
                final Sign sign = (Sign)event.getClickedBlock().getState();
                if (ChatColor.stripColor(sign.getLine(1)).equals("[Soup]")) {
                    event.setCancelled(true);
                    final Inventory inventory = this.plugin.getServer().createInventory((InventoryHolder)null, 54, CC.DARK_GRAY + "Soup Refill");
                    for (int i = 0; i < 54; ++i) {
                        inventory.setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
                    }
                    event.getPlayer().openInventory(inventory);
                }
            }
            if (event.getClickedBlock().getType() == Material.CHEST || event.getClickedBlock().getType() == Material.ENDER_CHEST) {
                event.setCancelled(true);
            }
        }
        Label_2049: {
            if (event.getAction().name().startsWith("RIGHT_")) {
                final ItemStack item = event.getItem();
                final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
                switch (playerData.getPlayerState()) {
                    case LOADING: {
                        player.sendMessage(CC.RED + "You must wait until your player data has loaded before you can use items.");
                        break;
                    }
                    case FFA: {
                        if (item == null) {
                            return;
                        }
                        switch (item.getType()) {
                            case MUSHROOM_SOUP: {
                                if (player.getHealth() <= 19.0 && !player.isDead()) {
                                    if (player.getHealth() < 20.0 || player.getFoodLevel() < 20) {
                                        player.getItemInHand().setType(Material.BOWL);
                                    }
                                    player.setHealth((player.getHealth() + 7.0 > 20.0) ? 20.0 : (player.getHealth() + 7.0));
                                    player.setFoodLevel((player.getFoodLevel() + 2 > 20) ? 20 : (player.getFoodLevel() + 2));
                                    player.setSaturation(12.8f);
                                    player.updateInventory();
                                    break;
                                }
                                break;
                            }
                        }
                        break;
                    }
                    case FIGHTING: {
                        if (item == null) {
                            return;
                        }
                        final Match match = this.plugin.getMatchManager().getMatch(playerData);
                        switch (item.getType()) {
                            case ENDER_PEARL: {
                                if (match.getMatchState() == MatchState.STARTING) {
                                    event.setCancelled(true);
                                    player.sendMessage(CC.RED + "You can't throw pearls right now!");
                                    player.updateInventory();
                                    break;
                                }
                                break;
                            }
                            case MUSHROOM_SOUP: {
                                if (player.getHealth() <= 19.0 && !player.isDead()) {
                                    if (player.getHealth() < 20.0 || player.getFoodLevel() < 20) {
                                        player.getItemInHand().setType(Material.BOWL);
                                    }
                                    player.setHealth((player.getHealth() + 7.0 > 20.0) ? 20.0 : (player.getHealth() + 7.0));
                                    player.setFoodLevel((player.getFoodLevel() + 2 > 20) ? 20 : (player.getFoodLevel() + 2));
                                    player.setSaturation(12.8f);
                                    player.updateInventory();
                                    break;
                                }
                                break;
                            }
                            case ENCHANTED_BOOK: {
                                final Kit kit = match.getKit();
                                final PlayerInventory inventory2 = player.getInventory();
                                final int kitIndex = inventory2.getHeldItemSlot();
                                if (kitIndex == 8) {
                                    kit.applyToPlayer(player);
                                    break;
                                }
                                final Map<Integer, PlayerKit> kits = playerData.getPlayerKits(kit.getName());
                                final PlayerKit playerKit = kits.get(kitIndex + 1);
                                if (playerKit != null) {
                                    playerKit.applyToPlayer(player);
                                    break;
                                }
                                break;
                            }
                        }
                        break;
                    }
                    case SPAWN: {
                        if (item == null) {
                            return;
                        }
                        switch (item.getType()) {
                            case DIAMOND_SWORD: {
                                if (party != null) {
                                    player.sendMessage(CC.RED + "You can't join the Premium Queue while in a party.");
                                    return;
                                }
                                if (playerData.getPremiumMatches() <= 0) {
                                    player.sendMessage(CC.SECONDARY + "You don't have any " + CC.PRIMARY + "Premium Matches " + CC.SECONDARY + "remaining! Purchase more here: " + CC.PRIMARY + "https://store.minemen.club");
                                    return;
                                }
                                player.openInventory(this.plugin.getInventoryManager().getJoinPremiumInventory().getCurrentPage());
                                break;
                            }
                            case IRON_SWORD: {
                                if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                                    player.sendMessage(CC.RED + "Only the party leader can join the 2v2 queue.");
                                    return;
                                }
                                player.openInventory(this.plugin.getInventoryManager().getRankedInventory().getCurrentPage());
                                break;
                            }
                            case STONE_SWORD: {
                                if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                                    player.sendMessage(CC.RED + "Only the party leader can join the 2v2 queue.");
                                    return;
                                }
                                player.openInventory(this.plugin.getInventoryManager().getUnrankedInventory().getCurrentPage());
                                break;
                            }
                            case BLAZE_POWDER: {
                                final UUID rematching = this.plugin.getMatchManager().getRematcher(player.getUniqueId());
                                final Player rematcher = this.plugin.getServer().getPlayer(rematching);
                                if (rematcher == null) {
                                    player.sendMessage(CC.RED + "Player is no longer online.");
                                    return;
                                }
                                if (this.plugin.getMatchManager().getMatchRequest(rematcher.getUniqueId(), player.getUniqueId()) != null) {
                                    this.plugin.getServer().dispatchCommand((CommandSender)player, "accept " + rematcher.getName());
                                    break;
                                }
                                this.plugin.getServer().dispatchCommand((CommandSender)player, "duel " + rematcher.getName());
                                break;
                            }
                            case PAPER: {
                                if (this.plugin.getMatchManager().isRematching(player.getUniqueId())) {
                                    this.plugin.getServer().dispatchCommand((CommandSender)player, "inv " + this.plugin.getMatchManager().getRematcherInventory(player.getUniqueId()));
                                    break;
                                }
                                break;
                            }
                            case NAME_TAG: {
                                this.plugin.getPartyManager().createParty(player);
                                break;
                            }
                            case BOOK: {
                                player.openInventory(this.plugin.getInventoryManager().getEditorInventory().getCurrentPage());
                                break;
                            }
                            case WATCH: {
                                player.performCommand("settings");
                                break;
                            }
                            case DIAMOND_AXE: {
                                if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                                    player.sendMessage(CC.RED + "Only the party leader can start events.");
                                    return;
                                }
                                player.openInventory(this.plugin.getInventoryManager().getPartyEventInventory().getCurrentPage());
                                break;
                            }
                            case IRON_AXE: {
                                if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                                    player.sendMessage(CC.RED + "Only the party leader can start events.");
                                    return;
                                }
                                player.openInventory(this.plugin.getInventoryManager().getPartyInventory().getCurrentPage());
                                break;
                            }
                            case NETHER_STAR: {
                                this.plugin.getPartyManager().leaveParty(player);
                                this.plugin.getTournamentManager().leaveTournament(player);
                                break;
                            }
                        }
                        break;
                    }
                    case QUEUE: {
                        if (item == null) {
                            return;
                        }
                        if (item.getType() != Material.REDSTONE) {
                            break;
                        }
                        if (party == null) {
                            this.plugin.getQueueManager().removePlayerFromQueue(player);
                            break;
                        }
                        this.plugin.getQueueManager().removePartyFromQueue(party);
                        break;
                    }
                    case SPECTATING: {
                        if (item == null) {
                            return;
                        }
                        if (item.getType() == Material.REDSTONE) {
                            if (party == null) {
                                this.plugin.getMatchManager().removeSpectator(player);
                                break;
                            }
                            break;
                        }
                        else {
                            if (item.getType() == Material.NETHER_STAR) {
                                this.plugin.getPartyManager().leaveParty(player);
                                break;
                            }
                            break;
                        }
                        break;
                    }
                    case EDITING: {
                        if (event.getClickedBlock() == null) {
                            return;
                        }
                        switch (event.getClickedBlock().getType()) {
                            case WALL_SIGN:
                            case SIGN:
                            case SIGN_POST: {
                                this.plugin.getEditorManager().removeEditor(player.getUniqueId());
                                this.plugin.getPlayerManager().sendToSpawnAndReset(player);
                                break Label_2049;
                            }
                            case CHEST: {
                                final Kit kit = this.plugin.getKitManager().getKit(this.plugin.getEditorManager().getEditingKit(player.getUniqueId()));
                                if (kit.getKitEditContents()[0] != null) {
                                    final Inventory editorInventory = this.plugin.getServer().createInventory((InventoryHolder)null, 36);
                                    editorInventory.setContents(kit.getKitEditContents());
                                    player.openInventory(editorInventory);
                                    event.setCancelled(true);
                                    break Label_2049;
                                }
                                break Label_2049;
                            }
                            case ANVIL: {
                                player.openInventory(this.plugin.getInventoryManager().getEditingKitInventory(player.getUniqueId()).getCurrentPage());
                                event.setCancelled(true);
                                break Label_2049;
                            }
                        }
                        break;
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        final Material drop = event.getItemDrop().getItemStack().getType();
        switch (playerData.getPlayerState()) {
            case FFA: {
                if (drop != Material.BOWL) {
                    event.setCancelled(true);
                    break;
                }
                event.getItemDrop().remove();
                break;
            }
            case FIGHTING: {
                if (drop == Material.ENCHANTED_BOOK) {
                    event.setCancelled(true);
                    break;
                }
                if (drop == Material.GLASS_BOTTLE) {
                    event.getItemDrop().remove();
                    break;
                }
                final Match match = this.plugin.getMatchManager().getMatch(event.getPlayer().getUniqueId());
                this.plugin.getMatchManager().addDroppedItem(match, event.getItemDrop());
                break;
            }
            default: {
                event.setCancelled(true);
                break;
            }
        }
    }
    
    @EventHandler
    public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
        final Player player = event.getPlayer();
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            final Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
            if (match.getEntitiesToRemove().contains(event.getItem())) {
                match.removeEntityToRemove((Entity)event.getItem());
            }
            else {
                event.setCancelled(true);
            }
        }
        else if (playerData.getPlayerState() != PlayerState.FFA) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        final String chatMessage = event.getMessage();
        if (party != null) {
            if (chatMessage.startsWith("!") || chatMessage.startsWith("@")) {
                event.setCancelled(true);
                final String message = CC.PRIMARY + "[Party] " + CC.PRIMARY + player.getName() + CC.R + ": " + chatMessage.replaceFirst("!", "").replaceFirst("@", "");
                party.broadcast(message);
            }
        }
        else {
            final PlayerKit kitRenaming = this.plugin.getEditorManager().getRenamingKit(player.getUniqueId());
            if (kitRenaming != null) {
                kitRenaming.setDisplayName(ChatColor.translateAlternateColorCodes('&', chatMessage));
                event.setCancelled(true);
                event.getPlayer().sendMessage(CC.PRIMARY + "Set kit " + CC.SECONDARY + kitRenaming.getIndex() + CC.PRIMARY + "'s name to " + CC.SECONDARY + kitRenaming.getDisplayName());
                this.plugin.getEditorManager().removeRenamingKit(event.getPlayer().getUniqueId());
            }
        }
    }
    
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        switch (playerData.getPlayerState()) {
            case FIGHTING: {
                this.plugin.getMatchManager().removeFighter(player, playerData, true);
                break;
            }
            case EVENT: {
                final PracticeEvent currentEvent = this.plugin.getEventManager().getEventPlaying(player);
                if (currentEvent == null) {
                    break;
                }
                currentEvent.getPlayers().remove(player.getUniqueId());
                if (currentEvent.onDeath() != null) {
                    currentEvent.onDeath().accept(player);
                    break;
                }
                break;
            }
            case FFA: {
                for (final ItemStack item : player.getInventory().getContents()) {
                    if (item != null && item.getType() == Material.MUSHROOM_SOUP) {
                        this.plugin.getFfaManager().getItemTracker().put(player.getWorld().dropItemNaturally(player.getLocation(), item), System.currentTimeMillis());
                    }
                }
                this.plugin.getFfaManager().getKillStreakTracker().put(player.getUniqueId(), 0);
                String deathMessage = CC.PRIMARY + player.getName() + CC.SECONDARY + " was ";
                if (player.getKiller() == null) {
                    deathMessage += "killed.";
                }
                else {
                    deathMessage = deathMessage + "slain by " + CC.PRIMARY + player.getKiller().getName();
                    final int ks = this.plugin.getFfaManager().getKillStreakTracker().compute(player.getKiller().getUniqueId(), (k, v) -> ((v == null) ? 0 : v) + 1);
                    for (final KillStreak killStreak : this.plugin.getFfaManager().getKillStreaks()) {
                        if (killStreak.getStreaks().contains(ks)) {
                            killStreak.giveKillStreak(player.getKiller());
                            for (final PlayerData data : this.plugin.getPlayerManager().getAllData()) {
                                if (data.getPlayerState() == PlayerState.FFA) {
                                    deathMessage = deathMessage + "\n" + CC.PRIMARY + player.getKiller().getName() + CC.SECONDARY + " is on a " + CC.PRIMARY + ks + CC.SECONDARY + " kill streak!";
                                }
                            }
                            break;
                        }
                    }
                }
                for (final PlayerData data2 : this.plugin.getPlayerManager().getAllData()) {
                    if (data2.getPlayerState() == PlayerState.FFA) {
                        final Player ffaPlayer = this.plugin.getServer().getPlayer(data2.getUniqueId());
                        ffaPlayer.sendMessage(deathMessage);
                    }
                }
                this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> this.plugin.getFfaManager().removePlayer(event.getEntity()));
                break;
            }
        }
        event.getDrops().clear();
    }
    
    @EventHandler
    public void onFoodLevelChange(final FoodLevelChangeEvent event) {
        final Player player = (Player)event.getEntity();
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            final Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
            if (match.getKit().isSumo()) {
                event.setCancelled(true);
            }
        }
        else {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onProjectileLaunch(final ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            final Player shooter = (Player)event.getEntity().getShooter();
            final PlayerData shooterData = this.plugin.getPlayerManager().getPlayerData(shooter.getUniqueId());
            if (shooterData.getPlayerState() == PlayerState.FIGHTING) {
                final Match match = this.plugin.getMatchManager().getMatch(shooter.getUniqueId());
                match.addEntityToRemove((Entity)event.getEntity());
            }
        }
    }
    
    @EventHandler
    public void onProjectileHit(final ProjectileHitEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            final Player shooter = (Player)event.getEntity().getShooter();
            final PlayerData shooterData = this.plugin.getPlayerManager().getPlayerData(shooter.getUniqueId());
            if (shooterData != null && shooterData.getPlayerState() == PlayerState.FIGHTING) {
                final Match match = this.plugin.getMatchManager().getMatch(shooter.getUniqueId());
                match.removeEntityToRemove((Entity)event.getEntity());
                if (event.getEntityType() == EntityType.ARROW) {
                    event.getEntity().remove();
                }
            }
        }
    }
    
    @EventHandler
    public void onMinemanRetrieve(final MinemanRetrieveEvent event) {
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(event.getUniqueId());
        if (playerData != null) {
            playerData.setMinemanID(event.getMineman().getId());
        }
    }
}
