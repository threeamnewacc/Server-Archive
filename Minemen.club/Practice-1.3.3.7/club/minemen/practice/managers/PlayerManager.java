// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.managers;

import club.minemen.core.timer.impl.EnderpearlTimer;
import club.minemen.practice.util.PlayerUtil;
import club.minemen.core.util.finalutil.ItemUtil;
import club.minemen.core.util.finalutil.CC;
import org.bukkit.Material;
import java.util.Collection;
import club.minemen.practice.request.UpdateStatsRequest;
import org.json.simple.JSONObject;
import org.bukkit.plugin.Plugin;
import java.net.InetAddress;
import club.minemen.core.mineman.Mineman;
import club.minemen.core.api.callback.Callback;
import club.minemen.core.api.request.Request;
import org.bukkit.configuration.ConfigurationSection;
import java.util.Iterator;
import com.google.gson.JsonObject;
import club.minemen.practice.kit.PlayerKit;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import club.minemen.core.util.Config;
import club.minemen.practice.kit.Kit;
import com.google.gson.JsonElement;
import club.minemen.core.api.abstr.AbstractBukkitCallback;
import club.minemen.practice.request.PracticeFetchStatsRequest;
import club.minemen.practice.player.PlayerState;
import club.minemen.core.CorePlugin;
import org.bukkit.entity.Player;
import java.util.concurrent.ConcurrentHashMap;
import club.minemen.practice.player.PlayerData;
import java.util.UUID;
import java.util.Map;
import club.minemen.practice.Practice;

public class PlayerManager
{
    private final Practice plugin;
    private final Map<UUID, PlayerData> playerData;
    
    public PlayerManager() {
        this.plugin = Practice.getInstance();
        this.playerData = new ConcurrentHashMap<UUID, PlayerData>();
    }
    
    public void createPlayerData(final Player player) {
        final PlayerData data = new PlayerData(player.getUniqueId());
        this.playerData.put(data.getUniqueId(), data);
        this.loadData(data);
    }
    
    private void loadData(final PlayerData playerData) {
        final Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(playerData.getUniqueId());
        if (mineman == null || !mineman.isDataLoaded() || mineman.isErrorLoadingData()) {
            return;
        }
        playerData.setMinemanID(mineman.getId());
        playerData.setPlayerState(PlayerState.SPAWN);
        final InetAddress address = mineman.getIpAddress();
        CorePlugin.getInstance().getRequestProcessor().sendRequestAsync((Request)new PracticeFetchStatsRequest(playerData.getUniqueId()), (Callback)new AbstractBukkitCallback() {
            public void callback(final JsonElement jsonElement) {
                Kit kit = null;
                if (!jsonElement.isJsonNull()) {
                    final JsonObject stats = jsonElement.getAsJsonObject();
                    JsonElement element = stats.get("allowingSpectators");
                    if (element != null && !element.isJsonNull()) {
                        playerData.setAllowingSpectators(element.getAsBoolean());
                    }
                    element = stats.get("acceptingDuels");
                    if (element != null && !element.isJsonNull()) {
                        playerData.setAcceptingDuels(element.getAsBoolean());
                    }
                    element = stats.get("scoreboardEnabled");
                    if (element != null && !element.isJsonNull()) {
                        playerData.setScoreboardEnabled(element.getAsBoolean());
                    }
                    element = stats.get("pingRange");
                    if (element != null && !element.isJsonNull()) {
                        playerData.setPingRange(element.getAsInt());
                    }
                    element = stats.get("eloRange");
                    if (element != null && !element.isJsonNull()) {
                        playerData.setEloRange(element.getAsInt());
                    }
                    element = stats.get("premiumElo");
                    if (element != null && !element.isJsonNull()) {
                        playerData.setPremiumElo(element.getAsInt());
                    }
                    element = stats.get("premiumWins");
                    if (element != null && !element.isJsonNull()) {
                        playerData.setPremiumWins(element.getAsInt());
                    }
                    element = stats.get("premiumLosses");
                    if (element != null && !element.isJsonNull()) {
                        playerData.setPremiumLosses(element.getAsInt());
                    }
                    element = stats.get("premiumMatchesExtra");
                    if (element != null && !element.isJsonNull()) {
                        playerData.setPremiumMatchesExtra(element.getAsInt());
                    }
                    element = stats.get("premiumMatchesPlayed");
                    if (element != null && !element.isJsonNull()) {
                        playerData.setPremiumMatchesPlayed(element.getAsInt());
                    }
                    final Iterator<Kit> iterator = Practice.getInstance().getKitManager().getKits().iterator();
                    while (iterator.hasNext()) {
                        kit = iterator.next();
                        final String kitName = kit.getName();
                        element = stats.get(kitName.toLowerCase() + "Elo");
                        if (element != null && !element.isJsonNull()) {
                            playerData.setElo(kitName, element.getAsInt());
                        }
                        element = stats.get(kitName + "Wins");
                        if (element != null && !element.isJsonNull()) {
                            playerData.setWins(kitName, element.getAsInt());
                        }
                        element = stats.get(kitName + "Losses");
                        if (element != null && !element.isJsonNull()) {
                            playerData.setLosses(kitName, element.getAsInt());
                        }
                        element = stats.get(kitName + "EloParty");
                        if (element != null && !element.isJsonNull()) {
                            playerData.setPartyElo(kitName, element.getAsInt());
                        }
                    }
                }
                final Config config = new Config("/players/" + playerData.getUniqueId().toString(), (JavaPlugin)PlayerManager.this.plugin);
                final ConfigurationSection playerKitsSection = config.getConfig().getConfigurationSection("playerkits");
                if (playerKitsSection != null) {
                    PlayerManager.this.plugin.getKitManager().getKits().forEach(kit -> {
                        final Object val$playerData = playerData;
                        final ConfigurationSection kitSection = playerKitsSection.getConfigurationSection(kit.getName());
                        if (kitSection != null) {
                            kitSection.getKeys(false).forEach(kitKey -> {
                                final Integer kitIndex = Integer.parseInt(kitKey);
                                final String displayName = kitSection.getString(kitKey + ".displayName");
                                final ItemStack[] contents = ((List)kitSection.get(kitKey + ".contents")).toArray(new ItemStack[0]);
                                final PlayerKit playerKit = new PlayerKit(kit.getName(), kitIndex, contents, displayName);
                                playerData.addPlayerKit(kitIndex, playerKit);
                            });
                        }
                    });
                }
            }
            
            public void onError(final String message) {
                super.onError(message);
                PlayerManager.this.plugin.getLogger().severe("Error fetching practice stats for " + playerData.getUniqueId());
            }
        });
    }
    
    public void removePlayerData(final UUID uuid) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> {
            this.saveData(this.playerData.get(uuid));
            this.playerData.remove(uuid);
        });
    }
    
    public void saveData(final PlayerData playerData) {
        final Config config = new Config("/players/" + playerData.getUniqueId().toString(), (JavaPlugin)this.plugin);
        this.plugin.getKitManager().getKits().forEach(kit -> {
            final Map<Integer, PlayerKit> playerKits = playerData.getPlayerKits(kit.getName());
            if (playerKits != null) {
                playerKits.forEach((key, value) -> {
                    config.getConfig().set("playerkits." + kit.getName() + "." + key + ".displayName", (Object)value.getDisplayName());
                    config.getConfig().set("playerkits." + kit.getName() + "." + key + ".contents", (Object)value.getContents());
                });
            }
            return;
        });
        config.save();
        final JSONObject data = new JSONObject();
        for (final String kitName : this.plugin.getKitManager().getRankedKits()) {
            final JSONObject kitData = new JSONObject();
            kitData.put((Object)"elo", (Object)playerData.getElo(kitName));
            kitData.put((Object)"party-elo", (Object)playerData.getPartyElo(kitName));
            data.put((Object)kitName, (Object)kitData);
        }
        final UpdateStatsRequest request = UpdateStatsRequest.builder().uuid(playerData.getUniqueId()).scoreboardEnabled(playerData.isScoreboardEnabled()).acceptingDuels(playerData.isAcceptingDuels()).allowingSpectators(playerData.isAllowingSpectators()).pingRange(playerData.getPingRange()).eloRange(playerData.getEloRange()).id(playerData.getMinemanID()).nodebuffEloParty(playerData.getPartyElo("NoDebuff")).nodebuffLosses(playerData.getLosses("NoDebuff")).nodebuffWins(playerData.getWins("NoDebuff")).nodebuffElo(playerData.getElo("NoDebuff")).debuffEloParty(playerData.getPartyElo("Debuff")).debuffLosses(playerData.getLosses("Debuff")).debuffWins(playerData.getWins("Debuff")).debuffElo(playerData.getElo("Debuff")).gappleEloParty(playerData.getPartyElo("Gapple")).gappleLosses(playerData.getLosses("Gapple")).gappleWins(playerData.getWins("Gapple")).gappleElo(playerData.getElo("Gapple")).archerEloParty(playerData.getPartyElo("Archer")).archerLosses(playerData.getLosses("Archer")).archerWins(playerData.getWins("Archer")).archerElo(playerData.getElo("Archer")).axeEloParty(playerData.getPartyElo("Axe")).axeLosses(playerData.getLosses("Axe")).axeWins(playerData.getWins("Axe")).axeElo(playerData.getElo("Axe")).classicEloParty(playerData.getPartyElo("Classic")).classicLosses(playerData.getLosses("Classic")).classicWins(playerData.getWins("Classic")).classicElo(playerData.getElo("Classic")).hcfEloParty(playerData.getPartyElo("HCF")).hcfLosses(playerData.getLosses("HCF")).hcfWins(playerData.getWins("HCF")).hcfElo(playerData.getElo("HCF")).sumoEloParty(playerData.getPartyElo("Sumo")).sumoLosses(playerData.getLosses("Sumo")).sumoWins(playerData.getWins("Sumo")).sumoElo(playerData.getElo("Sumo")).builduhcEloParty(playerData.getPartyElo("BuildUHC")).builduhcLosses(playerData.getLosses("BuildUHC")).builduhcWins(playerData.getWins("BuildUHC")).builduhcElo(playerData.getElo("BuildUHC")).premiumMatchesPlayed(playerData.getPremiumMatchesPlayed()).premiumMatchesExtra(playerData.getPremiumMatchesExtra()).premiumLosses(playerData.getPremiumLosses()).premiumWins(playerData.getPremiumWins()).premiumElo(playerData.getPremiumElo()).build();
        CorePlugin.getInstance().getRequestProcessor().sendRequestAsync((Request)request);
    }
    
    public Collection<PlayerData> getAllData() {
        return this.playerData.values();
    }
    
    public PlayerData getPlayerData(final UUID uuid) {
        return this.playerData.get(uuid);
    }
    
    public void giveLobbyItems(final Player player) {
        final boolean inParty = this.plugin.getPartyManager().getParty(player.getUniqueId()) != null;
        final boolean inTournament = this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null;
        final boolean isRematching = this.plugin.getMatchManager().isRematching(player.getUniqueId());
        ItemStack[] items = this.plugin.getItemManager().getSpawnItems();
        if (inTournament) {
            items = this.plugin.getItemManager().getTournamentItems();
        }
        else if (inParty) {
            items = this.plugin.getItemManager().getPartyItems();
        }
        player.getInventory().setContents(items);
        if (isRematching && !inParty && !inTournament) {
            player.getInventory().setItem(3, ItemUtil.createItem(Material.BLAZE_POWDER, CC.PRIMARY + "Request Rematch"));
            player.getInventory().setItem(6, ItemUtil.createItem(Material.PAPER, CC.PRIMARY + "View Opponent's Inventory"));
        }
        player.updateInventory();
    }
    
    public void sendToSpawnAndReset(final Player player) {
        final PlayerData playerData = this.getPlayerData(player.getUniqueId());
        playerData.setPlayerState(PlayerState.SPAWN);
        PlayerUtil.clearPlayer(player);
        ((EnderpearlTimer)CorePlugin.getInstance().getTimerManager().getTimer((Class)EnderpearlTimer.class)).clearCooldown(player.getUniqueId());
        this.giveLobbyItems(player);
        if (!player.isOnline()) {
            return;
        }
        this.plugin.getServer().getOnlinePlayers().forEach(p -> {
            player.hidePlayer(p);
            p.hidePlayer(player);
            return;
        });
        player.teleport(this.plugin.getSpawnManager().getSpawnLocation().toBukkitLocation());
    }
    
    public int getPremiumMatches(final UUID uuid) {
        final Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(uuid);
        switch (mineman.getRank()) {
            case MOD:
            case MODPLUS:
            case NORMAL:
            case SR_MOD:
            case TRAINEE:
            case TRIAL_MOD:
            case BUILDER: {
                return 0;
            }
            case MEMBER: {
                return 10;
            }
            case CLUBBER: {
                return 20;
            }
            case BARTENDER: {
                return 30;
            }
            case PARTYMAN: {
                return 40;
            }
            case PREMIUM: {
                return 50;
            }
            case VIP: {
                return 30;
            }
            case YOUTUBER: {
                return 15;
            }
            case ADMIN:
            case PLAT_ADMIN:
            case SR_ADMIN:
            case DEVELOPER:
            case OWNER: {
                return 1337;
            }
            default: {
                return 0;
            }
        }
    }
}
