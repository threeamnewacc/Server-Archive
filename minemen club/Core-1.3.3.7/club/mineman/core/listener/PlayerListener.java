// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.listener;

import java.util.Arrays;
import java.beans.ConstructorProperties;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.player.PlayerJoinEvent;
import club.mineman.core.event.player.MinemanRetrieveEvent;
import club.mineman.core.util.BanWrapper;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import java.util.Iterator;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.Collection;
import java.util.ArrayList;
import club.mineman.core.util.finalutil.StringUtil;
import club.mineman.core.util.finalutil.PlayerUtil;
import club.mineman.core.rank.Rank;
import java.util.function.Predicate;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import club.mineman.core.mineman.Mineman;
import club.mineman.core.util.finalutil.TimeUtil;
import club.mineman.core.util.finalutil.CC;
import java.sql.Timestamp;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import java.util.List;
import club.mineman.core.CorePlugin;
import java.util.regex.Pattern;
import org.bukkit.event.Listener;

public class PlayerListener implements Listener
{
    private static final Pattern URL_REGEX;
    private static final Pattern IP_REGEX;
    private final CorePlugin plugin;
    private static final List<String> COMMAND_BLACKLIST;
    
    @EventHandler
    void onPreCommand(final PlayerCommandPreprocessEvent event) {
        final String[] args = event.getMessage().split(" ");
        try {
            final String command = args[0];
            if (command.equalsIgnoreCase("msg") || command.equalsIgnoreCase("tell") || command.equalsIgnoreCase("w") || command.equalsIgnoreCase("m") || command.equalsIgnoreCase("message") || command.equalsIgnoreCase("reply") || command.equalsIgnoreCase("r")) {
                this.handleEvent(event.getPlayer(), (Cancellable)event);
            }
        }
        catch (final Exception ex) {}
    }
    
    private boolean handleEvent(final Player player, final Cancellable cancellable) {
        final Mineman mineman = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (!mineman.isMuted()) {
            return false;
        }
        if (mineman.getMuteTime() != null && System.currentTimeMillis() - mineman.getMuteTime().getTime() > 0L) {
            mineman.setMuted(false);
            mineman.setMuteTime(new Timestamp(0L));
            return false;
        }
        if (mineman.getMuteTime() == null) {
            player.sendMessage(CC.RED + "You are permanently muted.");
        }
        else {
            player.sendMessage(CC.RED + "You are muted for " + TimeUtil.millisToRoundedTime(Math.abs(System.currentTimeMillis() - mineman.getMuteTime().getTime())) + ".");
        }
        cancellable.setCancelled(true);
        return true;
    }
    
    @EventHandler
    void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final Mineman mineman = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (this.handleEvent(player, (Cancellable)event)) {
            return;
        }
        String msg = event.getMessage().toLowerCase().replace("3", "e").replace("1", "i").replace("!", "i").replace("@", "a").replace("7", "t").replace("0", "o").replace("5", "s").replace("8", "b");
        msg = msg.replaceAll("\\p{Punct}|\\d", "").trim();
        boolean shouldFilter = false;
        if (msg.equalsIgnoreCase("L") || msg.endsWith("L")) {
            shouldFilter = true;
        }
        final String[] split;
        final String[] words = split = msg.trim().split(" ");
        for (final String word : split) {
            if (word.equalsIgnoreCase("kys") || word.equals("L") || word.equalsIgnoreCase("dos") || word.equalsIgnoreCase("ddos") || word.equalsIgnoreCase("dox")) {
                shouldFilter = true;
                break;
            }
        }
        for (final String word : event.getMessage().replace("(dot)", ".").replace("[dot]", ".").trim().split(" ")) {
            if (!word.toLowerCase().endsWith("mineman.club")) {
                Matcher matcher = PlayerListener.IP_REGEX.matcher(word);
                if (matcher.matches()) {
                    shouldFilter = true;
                    break;
                }
                matcher = PlayerListener.URL_REGEX.matcher(word);
                if (matcher.matches()) {
                    shouldFilter = true;
                    break;
                }
            }
        }
        final Optional<String> optional = this.plugin.getFilterList().stream().map(phrase -> phrase.replaceAll(" ", "")).filter(msg::contains).findFirst();
        if (optional.isPresent()) {
            shouldFilter = true;
        }
        final Rank rank = mineman.getRank();
        if (shouldFilter) {
            if (!mineman.hasRank(Rank.TRAINEE)) {
                event.setCancelled(true);
                final String color = (mineman.getCustomColor() != null) ? mineman.getCustomColor() : rank.getColor();
                final String formattedMessage = String.format(rank.getPrefix() + color + "%1$s" + CC.R + ": %2$s", player.getName(), event.getMessage());
                PlayerUtil.messageStaff(CC.GRAY + "[" + CC.RED + "Filter" + CC.GRAY + "] " + formattedMessage);
                player.sendMessage(formattedMessage);
                return;
            }
            player.sendMessage(CC.RED + "That would have been filtered.");
        }
        if (mineman.isInAdminChat()) {
            event.setCancelled(true);
            PlayerUtil.messageStaff(CC.RED + "[Admin] " + rank.getColor() + player.getName() + CC.R + ": " + event.getMessage(), Rank.ADMIN);
            return;
        }
        if (mineman.isInStaffChat()) {
            event.setCancelled(true);
            PlayerUtil.messageStaff(CC.AQUA + "[Staff] " + rank.getColor() + player.getName() + CC.R + ": " + event.getMessage());
            return;
        }
        if (this.plugin.getPlayerManager().isChatSilenced() && !rank.hasRank(Rank.ADMIN)) {
            event.setCancelled(true);
            player.sendMessage(CC.RED + "Chat is currently silenced!");
            return;
        }
        if (!mineman.hasRank(Rank.TRAINEE)) {
            if (this.plugin.getPlayerManager().isChatCoolingDown(player)) {
                event.setCancelled(true);
                player.sendMessage(StringUtil.CHAT_COOLDOWN);
                return;
            }
            this.plugin.getPlayerManager().addChatCoolingDown(player);
        }
        final String color = (mineman.getCustomColor() != null) ? mineman.getCustomColor() : rank.getColor();
        event.setFormat(rank.getPrefix() + color + "%1$s" + CC.R + ": %2$s");
        final List<Player> recipientList = new ArrayList<Player>(event.getRecipients());
        for (final Player recipient : recipientList) {
            final Mineman recipientMineman = this.plugin.getPlayerManager().getPlayer(recipient.getUniqueId());
            if (!mineman.hasRank(Rank.TRAINEE) && recipientMineman != null && recipientMineman.isDataLoaded() && (recipientMineman.isIgnoring(mineman.getId()) || !recipientMineman.isChatEnabled())) {
                event.getRecipients().remove(recipient);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    void onAsyncPlayerPreLoginLow(final AsyncPlayerPreLoginEvent e) {
        if (!CorePlugin.SETUP) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, CC.RED + "Server is setting up...");
            return;
        }
        this.plugin.getPlayerManager().addPlayer(e.getUniqueId(), e.getName(), e.getAddress());
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    void onAsyncPlayerPreLoginHigh(final AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }
        final Mineman mineman = this.plugin.getPlayerManager().getPlayer(e.getUniqueId());
        final BanWrapper wrapper = mineman.fetchData();
        if (wrapper.isBanned()) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, wrapper.getMessage());
        }
    }
    
    @EventHandler
    void onMineman(final MinemanRetrieveEvent event) {
        if (event.getBanWrapper().isBanned()) {
            final Player player = event.getMineman().getPlayer();
            if (player != null) {
                player.kickPlayer(event.getBanWrapper().getMessage());
            }
        }
        else {
            final Mineman mineman = event.getMineman();
            final Player player2 = this.plugin.getServer().getPlayer(mineman.getUuid());
            if (player2 != null) {
                mineman.onJoin();
                player2.setPlayerListName(mineman.getRank().getColor() + player2.getName() + CC.R);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    void onJoin(final PlayerJoinEvent e) {
        e.setJoinMessage((String)null);
        final Player player = e.getPlayer();
        final Mineman mineman = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (mineman != null && mineman.isErrorLoadingData()) {
            player.kickPlayer(StringUtil.LOAD_ERROR);
            return;
        }
        if (mineman == null || !mineman.isDataLoaded()) {
            player.kickPlayer(StringUtil.LOAD_ERROR);
            return;
        }
        if (mineman.getBanData().isBanned()) {
            player.kickPlayer(mineman.getBanData().getMessage());
            return;
        }
        mineman.onJoin();
        this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, () -> {
            final Mineman mineman2 = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
            if (mineman2 != null) {
                player.setPlayerListName(mineman.getRank().getColor() + player.getName() + CC.R);
            }
        }, 5L);
    }
    
    @EventHandler
    void onPlayerCommandPreProcess(final PlayerCommandPreprocessEvent e) {
        String command = e.getMessage().split(" ")[0];
        if (command.startsWith("/")) {
            command = command.substring(1, command.length());
        }
        if (PlayerListener.COMMAND_BLACKLIST.contains(command.toLowerCase())) {
            e.setCancelled(true);
            return;
        }
        final Player player = e.getPlayer();
        final Mineman mineman = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (mineman.hasRank(Rank.TRAINEE)) {
            return;
        }
        if (this.plugin.getPlayerManager().isCommandCoolingDown(player)) {
            e.setCancelled(true);
            player.sendMessage(StringUtil.COMMAND_COOLDOWN);
        }
        else {
            this.plugin.getPlayerManager().addCommandCoolingDown(player);
        }
    }
    
    @EventHandler
    void onItemPickup(final PlayerPickupItemEvent e) {
        if (this.plugin.getPlayerManager().getPlayer(e.getPlayer().getUniqueId()).isVanishMode()) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    void onPlayerChatTabComplete(final PlayerChatTabCompleteEvent e) {
        e.getTabCompletions().removeIf(str -> this.plugin.getServer().getPlayer(str) != null && !e.getPlayer().canSee(this.plugin.getServer().getPlayer(str)));
    }
    
    @EventHandler
    void onQuit(final PlayerQuitEvent e) {
        e.setQuitMessage((String)null);
        this.plugin.getPlayerManager().removePlayer(e.getPlayer().getUniqueId());
    }
    
    @ConstructorProperties({ "plugin" })
    public PlayerListener(final CorePlugin plugin) {
        this.plugin = plugin;
    }
    
    static {
        URL_REGEX = Pattern.compile("^(http://www\\.|https://www\\.|http://|https://)?[a-z0-9]+([\\-.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?$");
        IP_REGEX = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])([.,])){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
        COMMAND_BLACKLIST = Arrays.asList("me");
    }
}
