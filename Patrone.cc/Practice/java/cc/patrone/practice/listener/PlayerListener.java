package cc.patrone.practice.listener;

import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.duel.Duel;
import cc.patrone.practice.kit.CustomKit;
import cc.patrone.practice.kit.Kit;
import cc.patrone.practice.match.Match;
import cc.patrone.practice.match.MatchState;
import cc.patrone.practice.player.PlayerState;
import cc.patrone.practice.player.PracticeProfile;
import cc.patrone.practice.queue.Queue;
import cc.patrone.practice.util.InventorySnapshot;
import cc.patrone.practice.util.InventoryUtil;
import cc.patrone.practice.util.Items;
import cc.patrone.practice.util.ScoreHelper;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class PlayerListener implements Listener {

    private PracticePlugin plugin;

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        this.plugin.getManagerHandler().getProfileManager().addPlayer(player);
        if (!this.plugin.getManagerHandler().getConfigurationManager().hasFile(player.getUniqueId())) {
            this.plugin.getManagerHandler().getConfigurationManager().createFile(player.getUniqueId());
        }
        this.plugin.getManagerHandler().getConfigurationManager().loadFile(player.getUniqueId());
        PracticeProfile practiceProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(player);
        FileConfiguration file = this.plugin.getManagerHandler().getConfigurationManager().getFile(player.getUniqueId());
        this.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(this.plugin, () -> {
            Arrays.stream(Kit.values()).forEach(k -> {
                try {
                    if (file.get("kit." + k.getName()) != null) {
                        practiceProfile.setElo(k, file.getInt("kit." + k.getName() + ".elo"));
                    }
                    if (file.get("kit." + k.getName() + ".inventory") != null && file.get("kit." + k.getName() + ".armor") != null) {
                        practiceProfile.setCustomKit(k, new CustomKit(InventoryUtil.fromBase64(file.getString("kit." + k.getName() + ".inventory")), InventoryUtil.itemStackArrayFromBase64(file.getString("kit." + k.getName() + ".armor"))));
                    }
                } catch (Exception ex) {
                }
            });
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ScoreHelper.createScore(player);
        this.plugin.getManagerHandler().getPlayerManager().giveItems(player, false);
        this.plugin.getManagerHandler().getPlayerManager().teleportSpawn(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PracticeProfile practiceProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(player);
        if (practiceProfile.getPlayerState() == PlayerState.SPECTATING) {
            practiceProfile.getSpectating().getSpectators().remove(player.getUniqueId());
            return;
        }
        if (practiceProfile.getPlayerState() == PlayerState.MATCH) {
            practiceProfile.getMatch().end(player);
            return;
        }
        FileConfiguration file = this.plugin.getManagerHandler().getConfigurationManager().getFile(player.getUniqueId());
        Arrays.stream(Kit.values()).forEach(k -> {
            file.set("kit." + k.getName() + ".elo", practiceProfile.getElo(k));
            if (practiceProfile.getCustomKit(k) != null) {
                file.set("kit." + k.getName() + ".inventory", InventoryUtil.toBase64(practiceProfile.getCustomKit(k).getInventory()));
                file.set("kit." + k.getName() + ".armor", InventoryUtil.itemStackArrayToBase64(practiceProfile.getCustomKit(k).getArmor()));
            } else {
                file.set("kit." + k.getName() + ".inventory", null);
                file.set("kit." + k.getName() + ".armor", null);
            }
        });
        this.plugin.getManagerHandler().getConfigurationManager().saveFile(player.getUniqueId());
        this.plugin.getManagerHandler().getProfileManager().removePlayer(player);
        this.plugin.getManagerHandler().getPlayerManager().removeFromQueue(player);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PracticeProfile practiceProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(player);
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (player.getItemInHand().equals(Items.MATCH_INFO.getItem())) {
                event.setCancelled(true);
                Match match = practiceProfile.getSpectating();
                player.sendMessage(ChatColor.DARK_PURPLE + "Kit " + ChatColor.LIGHT_PURPLE + match.getKit().getName());
                player.sendMessage(ChatColor.DARK_PURPLE + "Ranked: " + ChatColor.LIGHT_PURPLE + match.isRanked());
                return;
            }
            if (player.getItemInHand().equals(Items.STOP_SPECTATING.getItem())) {
                event.setCancelled(true);
                if (!player.hasPermission("practice.staff") && practiceProfile.getSpectating() != null) {
                    practiceProfile.getSpectating().broadcast(ChatColor.GRAY + " * " + ChatColor.DARK_PURPLE + player.getName() + ChatColor.LIGHT_PURPLE + " is no longer spectating.");
                }
                practiceProfile.getSpectating().getSpectators().remove(player.getUniqueId());
                practiceProfile.setSpectating(null);
                this.plugin.getManagerHandler().getPlayerManager().giveItems(player, false);
                this.plugin.getManagerHandler().getPlayerManager().teleportSpawn(player);
                return;
            }
            if (player.getItemInHand().equals(Items.UNRANKED.getItem())) {
                event.setCancelled(true);
                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getUnrankedInventory());
                return;
            }
            if (player.getItemInHand().equals(Items.RANKED.getItem())) {
                event.setCancelled(true);
                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getRankedInventory());
                return;
            }
            if (player.getItemInHand().equals(Items.LEAVE_QUEUE.getItem())) {
                event.setCancelled(true);
                practiceProfile.setPlayerState(PlayerState.LOBBY);
                this.plugin.getManagerHandler().getPlayerManager().removeFromQueue(player);
                this.plugin.getManagerHandler().getPlayerManager().giveItems(player, false);
                practiceProfile.setQueue(null);
                player.sendMessage(ChatColor.RED + "You have left the queue.");
                return;
            }
            if (player.getItemInHand().equals(Items.VIEW_QUEUE.getItem())) {
                event.setCancelled(true);
                Queue queue = practiceProfile.getQueue();
                player.sendMessage(ChatColor.DARK_PURPLE + "Kit: " + ChatColor.LIGHT_PURPLE + queue.getKit().getName());
                player.sendMessage(ChatColor.DARK_PURPLE + "Ranked: " + ChatColor.LIGHT_PURPLE + queue.isRanked());
                if (queue.isRanked()) {
                    player.sendMessage(ChatColor.DARK_PURPLE + "Searching: " + ChatColor.LIGHT_PURPLE + queue.getMinElo() + " -> " + queue.getMaxElo());
                }
                return;
            }
            if (player.getItemInHand().equals(Items.EDIT_KIT.getItem())) {
                event.setCancelled(true);
                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getEditKitInventory());
                return;
            }
            if (player.getItemInHand().equals(Items.DEFAULT_KIT.getItem())) {
                event.setCancelled(true);
                Match match = practiceProfile.getMatch();
                this.plugin.getManagerHandler().getPlayerManager().resetPlayer(player);
                player.getInventory().setContents(match.getKit().getInventory().getContents());
                player.getInventory().setArmorContents(match.getKit().getArmor());
                player.updateInventory();
                player.sendMessage(ChatColor.LIGHT_PURPLE + "You have equipped the default kit for " + ChatColor.DARK_PURPLE + match.getKit().getName() + ChatColor.LIGHT_PURPLE + ".");
                return;
            }
            if (player.getItemInHand().equals(Items.CUSTOM_KIT.getItem())) {
                event.setCancelled(true);
                Match match = practiceProfile.getMatch();
                this.plugin.getManagerHandler().getPlayerManager().resetPlayer(player);
                CustomKit customKit = practiceProfile.getCustomKit(match.getKit());
                player.getInventory().setContents(customKit.getInventory().getContents());
                player.getInventory().setArmorContents(customKit.getArmor());
                player.updateInventory();
                player.sendMessage(ChatColor.LIGHT_PURPLE + "You have equipped the custom kit for " + ChatColor.DARK_PURPLE + match.getKit().getName() + ChatColor.LIGHT_PURPLE + ".");
                return;
            }
            if (player.getItemInHand().equals(Items.STATS.getItem())) {
                event.setCancelled(true);
                this.plugin.getManagerHandler().getPlayerManager().showStats(player);
                return;
            }
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType() == Material.ANVIL && practiceProfile.getPlayerState() == PlayerState.EDITING) {
                event.setCancelled(true);
                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getEditKitOptionsInventory());
                return;
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PracticeProfile practiceProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(player);
        try {
            if (event.getInventory().equals(this.plugin.getManagerHandler().getInventoryManager().getDuelInventory())) {
                Kit kit = Kit.getKit(event.getSlot());
                if (kit != null) {
                    event.setCancelled(true);
                    player.closeInventory();
                }
                if (!practiceProfile.getDueling().isOnline()) {
                    player.sendMessage(ChatColor.RED + "That player is no longer online.");
                    return;
                }
                PracticeProfile targetProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(practiceProfile.getDueling());
                if (targetProfile.getPlayerState() != PlayerState.LOBBY) {
                    player.sendMessage(ChatColor.RED + "That player is not in the lobby.");
                    return;
                }
                targetProfile.getDuelList().add(new Duel(player, kit));
                player.sendMessage(ChatColor.LIGHT_PURPLE + "You have sent " + ChatColor.DARK_PURPLE + practiceProfile.getDueling().getName() + ChatColor.LIGHT_PURPLE + " a duel request with the kit " + ChatColor.DARK_PURPLE + kit.getName() + ChatColor.LIGHT_PURPLE + ".");
                TextComponent clickableMessage = new TextComponent(ChatColor.DARK_PURPLE + player.getName() + ChatColor.LIGHT_PURPLE + " has sent you a duel request with the kit " + ChatColor.DARK_PURPLE + kit.getName() + ChatColor.GREEN + " [Accept]");
                clickableMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accept " + player.getName()));
                practiceProfile.getDueling().spigot().sendMessage(clickableMessage);
                return;
            }
            if (event.getInventory().equals(this.plugin.getManagerHandler().getInventoryManager().getUnrankedInventory())) {
                Kit kit = Kit.getKit(event.getSlot());
                if (kit != null) {
                    event.setCancelled(true);
                    player.closeInventory();
                    if (kit.getUnrankedQueue().size() == 0) {
                        practiceProfile.setPlayerState(PlayerState.QUEUE);
                        practiceProfile.setQueue(new Queue(kit, false, 0, 0));
                        kit.getUnrankedQueue().add(player.getUniqueId());
                        this.plugin.getManagerHandler().getPlayerManager().giveLeaveQueueItems(player);
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "You have joined the queue for " + ChatColor.DARK_PURPLE + "Unranked " + kit.getName() + ChatColor.LIGHT_PURPLE + ".");
                        return;
                    }
                    Player foundPlayer = this.plugin.getServer().getPlayer(kit.getUnrankedQueue().get(0));
                    if (foundPlayer != player) new Match(this.plugin, player, foundPlayer, kit, false).start();
                }
                return;
            }
            if (event.getInventory().equals(this.plugin.getManagerHandler().getInventoryManager().getRankedInventory())) {
                Kit kit = Kit.getKit(event.getSlot());
                if (kit != null) {
                    event.setCancelled(true);
                    player.closeInventory();
                    if (!kit.getRankedQueue().contains(player.getUniqueId())) {
                        practiceProfile.setPlayerState(PlayerState.QUEUE);
                        practiceProfile.setQueue(new Queue(kit, true, practiceProfile.getElo(kit) - 20, practiceProfile.getElo(kit) + 20));
                        kit.getRankedQueue().add(player.getUniqueId());
                        this.plugin.getManagerHandler().getPlayerManager().giveLeaveQueueItems(player);
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "You have joined the queue for " + ChatColor.DARK_PURPLE + "Ranked " + kit.getName() + ChatColor.LIGHT_PURPLE + " with " + ChatColor.DARK_PURPLE + practiceProfile.getElo(kit) + ChatColor.LIGHT_PURPLE + " elo.");
                        new BukkitRunnable() {
                            public void run() {
                                if (practiceProfile.getPlayerState() != PlayerState.QUEUE) {
                                    this.cancel();
                                    return;
                                }
                                for (int i = 0; i < kit.getRankedQueue().size(); i++) {
                                    Player foundPlayer = plugin.getServer().getPlayer(kit.getRankedQueue().get(i));
                                    PracticeProfile foundProfile = plugin.getManagerHandler().getProfileManager().getProfile(foundPlayer);
                                    Queue queue = practiceProfile.getQueue();
                                    Queue foundQueue = foundProfile.getQueue();
                                    int elo = practiceProfile.getElo(kit);
                                    int foundElo = foundProfile.getElo(kit);
                                    if (foundPlayer != player) {
                                        if (queue.getMinElo() <= foundElo && foundElo <= queue.getMaxElo() && foundQueue.getMinElo() <= elo && elo <= foundQueue.getMaxElo()) {
                                            new Match(plugin, player, foundPlayer, kit, true).start();
                                            this.cancel();
                                            return;
                                        }
                                    }
                                    queue.setMinElo(queue.getMinElo() - 20);
                                    queue.setMaxElo(queue.getMaxElo() + 20);
                                    return;
                                }
                            }
                        }.runTaskTimer(this.plugin, 20L, 20L);
                        return;
                    }
                }
            }
            if (event.getInventory().equals(this.plugin.getManagerHandler().getInventoryManager().getEditKitInventory())) {
                Kit kit = Kit.getKit(event.getSlot());
                if (kit != null) {
                    event.setCancelled(true);
                    player.closeInventory();
                    if (!kit.isEditable()) {
                        player.sendMessage(ChatColor.RED + "That kit is not editable at this time.");
                        return;
                    }
                    practiceProfile.setEditing(kit);
                    practiceProfile.setPlayerState(PlayerState.EDITING);
                    this.plugin.getManagerHandler().getPlayerManager().resetPlayer(player);
                    player.getInventory().setContents(kit.getInventory().getContents());
                    player.getInventory().setArmorContents(kit.getArmor());
                    player.updateInventory();
                    player.teleport(this.plugin.getManagerHandler().getSettingsManager().getEditor());
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "You are now editing the default kit for " + ChatColor.DARK_PURPLE + kit.getName() + ChatColor.LIGHT_PURPLE + ".");
                    return;
                }
            }
            if (event.getInventory().equals(this.plugin.getManagerHandler().getInventoryManager().getEditKitOptionsInventory())) {
                if (event.getSlot() == 4) {
                    event.setCancelled(true);
                    Kit kit = practiceProfile.getEditing();
                    try {
                        practiceProfile.setCustomKit(kit, new CustomKit(InventoryUtil.fromBase64(InventoryUtil.toBase64(player.getInventory())), InventoryUtil.itemStackArrayFromBase64(InventoryUtil.itemStackArrayToBase64(player.getInventory().getArmorContents()))));
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "You have saved your custom kit for " + ChatColor.DARK_PURPLE + kit.getName() + ChatColor.LIGHT_PURPLE + ".");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return;
                }
                if (event.getSlot() == 13) {
                    event.setCancelled(true);
                    Kit kit = practiceProfile.getEditing();
                    practiceProfile.getCustomKitMap().remove(kit);
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "You have deleted your custom kit for " + ChatColor.DARK_PURPLE + kit.getName() + ChatColor.LIGHT_PURPLE + ".");
                    return;
                }
                if (event.getSlot() == 17) {
                    event.setCancelled(true);
                    this.plugin.getManagerHandler().getPlayerManager().giveItems(player, false);
                    this.plugin.getManagerHandler().getPlayerManager().teleportSpawn(player);
                    return;
                }
            }
            if (event.getInventory().getName().startsWith(ChatColor.GRAY + "Inventory of ")) {
                event.setCancelled(true);
                if (event.getSlot() == 53) {
                    if (InventorySnapshot.getByPlayer(practiceProfile.getOpenInventory().getOpponent()) != null) {
                        player.openInventory(InventorySnapshot.getByPlayer(practiceProfile.getOpenInventory().getOpponent()).getInventory());
                        practiceProfile.setOpenInventory(InventorySnapshot.getByPlayer(practiceProfile.getOpenInventory().getOpponent()));
                        return;
                    }
                }
            }
            if (practiceProfile.getPlayerState() == PlayerState.LOBBY && !practiceProfile.isBuildMode()) {
                event.setCancelled(true);
                return;
            }
        } catch (Exception ex) {
            // remove dumb errors
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player attacked = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();
            PracticeProfile attackedProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(attacked);
            PracticeProfile damagerProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(damager);
            if (damagerProfile.getPlayerState() == PlayerState.SPECTATING) {
                event.setCancelled(true);
                return;
            }
            if (!event.isCancelled() && attackedProfile.getPlayerState() == PlayerState.MATCH && damagerProfile.getPlayerState() == PlayerState.MATCH) {
                Match match = attackedProfile.getMatch();
                if (match.getMatchState() != MatchState.STARTED) {
                    event.setCancelled(true);
                    return;
                }
                damagerProfile.setHits(damagerProfile.getHits() + 1);
                damagerProfile.setCombo(damagerProfile.getCombo() + 1);
                if (damagerProfile.getCombo() > damagerProfile.getLongestCombo()) {
                    damagerProfile.setLongestCombo(damagerProfile.getCombo());
                }
                attackedProfile.setCombo(0);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        event.getDrops().clear();
        Player player = event.getEntity();
        PracticeProfile practiceProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(player);
        if (practiceProfile.getPlayerState() == PlayerState.MATCH) {
            Match match = practiceProfile.getMatch();
            match.end(player);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        PracticeProfile practiceProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(player);
        if (practiceProfile.getPlayerState() == PlayerState.MATCH) {
            Match match = practiceProfile.getMatch();
            if (match.getMatchState() == MatchState.STARTING && (from.getX() != to.getX() || from.getZ() != to.getZ())) {
                event.setTo(from.setDirection(to.getDirection()));
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        PracticeProfile practiceProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(player);
        if (practiceProfile.getPlayerState() == PlayerState.LOBBY && !practiceProfile.isBuildMode()) {
            event.setCancelled(true);
            return;
        }
        if (event.getItemDrop().getItemStack() == Items.CUSTOM_KIT.getItem() || event.getItemDrop().getItemStack() == Items.DEFAULT_KIT.getItem()) {
            event.setCancelled(true);
            return;
        }
        if (practiceProfile.getPlayerState() == PlayerState.MATCH) {
            Match match = practiceProfile.getMatch();
            match.getDroppedItems().add(event.getItemDrop());
            new BukkitRunnable() {
                public void run() {
                    event.getItemDrop().remove();
                }
            }.runTaskLater(this.plugin, 100L);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PracticeProfile practiceProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(player);
        if (!practiceProfile.isBuildMode()) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PracticeProfile practiceProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(player);
        if (!practiceProfile.isBuildMode()) {
            event.setCancelled(true);
            return;
        }
    }


    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        PracticeProfile practiceProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(player);
        if (practiceProfile.getPlayerState() != PlayerState.MATCH) {
            event.setFoodLevel(20);
            return;
        }
        Match match = practiceProfile.getMatch();
        if (match.getMatchState() != MatchState.STARTED) {
            event.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            PracticeProfile practiceProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(player);
            if (practiceProfile.getPlayerState() != PlayerState.MATCH) {
                event.setCancelled(true);
                return;
            }
            Match match = practiceProfile.getMatch();
            if (match.getMatchState() != MatchState.STARTED) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            for (PotionEffect effect : event.getEntity().getEffects()) {
                if (effect.getType().equals(PotionEffectType.HEAL)) {
                    Player player = (Player) event.getEntity().getShooter();
                    PracticeProfile practiceProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(player);
                    practiceProfile.setThrownPots(practiceProfile.getThrownPots() + 1);
                    if (event.getIntensity(player) >= 0.95 && event.getIntensity(player) <= 1) {
                        practiceProfile.setFullyLandedPots(practiceProfile.getFullyLandedPots() + 1);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPearl(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player && event.getEntity() instanceof EnderPearl) {
            Player player = (Player) event.getEntity().getShooter();
            PracticeProfile practiceProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(player);
            if (practiceProfile.getPlayerState() == PlayerState.MATCH) {
                Match match = practiceProfile.getMatch();
                if (match.getMatchState() != MatchState.STARTED) {
                    event.setCancelled(true);
                    player.getItemInHand().setAmount(player.getItemInHand().getAmount() + 1);
                    player.updateInventory();
                    return;
                }
            }
            if (System.currentTimeMillis() - practiceProfile.getLastPearl() <= 16000) {
                event.setCancelled(true);
                player.getItemInHand().setAmount(player.getItemInHand().getAmount() + 1);
                player.updateInventory();
                long difference = System.currentTimeMillis() - practiceProfile.getLastPearl();
                player.sendMessage(ChatColor.RED + "You must wait " + (16 - TimeUnit.MILLISECONDS.toSeconds(difference)) + "s to do that again.");
                return;
            }
            practiceProfile.setLastPearl(System.currentTimeMillis());
            this.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(this.plugin, () -> {
                if (practiceProfile.getLastPearl() != 0) {
                    practiceProfile.setLastPearl(0);
                    player.sendMessage(ChatColor.GREEN + "You may pearl again!");
                }
            }, 320L);
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        PracticeProfile practiceProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(player);
        if (practiceProfile.getPlayerState() == PlayerState.MATCH) {
            Match match = practiceProfile.getMatch();
            if (match.getMatchState() == MatchState.STARTING && event.getReason().contains("Flying is not enabled")) {
                event.setCancelled(true);
            }
        }
    }


}
