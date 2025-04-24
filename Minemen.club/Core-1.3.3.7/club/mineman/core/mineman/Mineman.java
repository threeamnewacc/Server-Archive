// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.mineman;

import java.beans.ConstructorProperties;
import club.mineman.core.api.impl.IPCheckRequest;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import club.mineman.core.api.request.RequestCallback;
import org.bukkit.plugin.Plugin;
import java.util.function.Consumer;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import club.mineman.core.util.finalutil.TimeUtil;
import club.mineman.core.util.finalutil.StringUtil;
import java.util.Iterator;
import org.json.simple.JSONObject;
import org.bukkit.event.Event;
import club.mineman.core.event.player.MinemanRetrieveEvent;
import club.mineman.core.api.APIMessage;
import club.mineman.core.api.impl.PlayerDataRequest;
import club.mineman.core.CorePlugin;
import java.sql.Timestamp;
import club.mineman.core.util.BanWrapper;
import club.mineman.core.rank.Rank;
import java.net.InetAddress;
import java.util.UUID;
import java.util.Set;

public class Mineman
{
    private final Set<Integer> ignoring;
    private final UUID uuid;
    private final String name;
    private final InetAddress ipAddress;
    private Rank rank;
    private BanWrapper banData;
    private Timestamp muteTime;
    private Timestamp banTime;
    private String lastConversation;
    private String customColor;
    private boolean canSeeMessages;
    private boolean chatEnabled;
    private boolean errorLoadingData;
    private boolean inAdminChat;
    private boolean inStaffChat;
    private boolean blacklisted;
    private boolean dataLoaded;
    private boolean vanishMode;
    private boolean ipBanned;
    private boolean banned;
    private boolean muted;
    private int id;
    private int dataLoadStage;
    
    public boolean isIgnoring(final int id) {
        return this.ignoring.contains(id);
    }
    
    public boolean toggleIgnore(final int id) {
        if (!this.ignoring.remove(id)) {
            this.ignoring.add(id);
            return true;
        }
        return false;
    }
    
    public BanWrapper fetchData() {
        final JSONObject data = CorePlugin.getInstance().getRequestManager().sendRequestNow(new PlayerDataRequest(PlayerDataRequest.PlayerDataRequestType.GLOBAL, this.ipAddress, this.uuid, this.name));
        this.banData = this.parsePlayerData(data);
        if (!this.isBanned() && !this.isIpBanned()) {
            final JSONObject rankData = CorePlugin.getInstance().getRequestManager().sendRequestNow(new PlayerDataRequest(PlayerDataRequest.PlayerDataRequestType.RANKS, this.ipAddress, this.uuid, this.name));
            if (rankData != null) {
                this.parseRankData(rankData);
            }
            else {
                System.out.println("!!! ERROR FETCHING RANK DATA FOR " + this.getName());
            }
            final JSONObject ignoreData = CorePlugin.getInstance().getRequestManager().sendRequestNow(new PlayerDataRequest(PlayerDataRequest.PlayerDataRequestType.IGNORES, this.ipAddress, this.uuid, this.name));
            if (ignoreData != null) {
                for (final Object key : ignoreData.keySet()) {
                    final Object object = ignoreData.get(key);
                    if (object instanceof Long) {
                        this.ignoring.add(((Long)object).intValue());
                    }
                    else if (object instanceof Integer) {
                        this.ignoring.add((Integer)object);
                    }
                    else {
                        CorePlugin.getInstance().getServer().getLogger().warning("Error fetching Mineman ignore data. Object received is not an integer (or similar) " + object);
                    }
                }
            }
            else {
                System.out.println("!!! ERROR FETCHING IGNORE DATA FOR " + this.getName());
            }
        }
        this.dataLoaded = true;
        CorePlugin.getInstance().getServer().getPluginManager().callEvent((Event)new MinemanRetrieveEvent(this, this.banData));
        return this.banData;
    }
    
    private BanWrapper parsePlayerData(final JSONObject jsonObject) {
        final Object id = jsonObject.get((Object)"player-id");
        if (id instanceof Long) {
            this.id = ((Long)id).intValue();
        }
        else if (id instanceof Integer) {
            this.id = (int)id;
        }
        final Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        final Object color = jsonObject.get((Object)"custom-color");
        if (color != null) {
            this.customColor = (String)color;
        }
        final Object muted = jsonObject.get((Object)"muted");
        if (muted != null) {
            this.muted = (boolean)muted;
        }
        final Object muteTime = jsonObject.get((Object)"mute-time");
        if (muteTime != null) {
            this.muteTime = Timestamp.valueOf((String)muteTime);
            if (this.muteTime.before(currentTime)) {
                this.muteTime = null;
                this.muted = false;
            }
            else {
                this.muted = true;
            }
        }
        final Object ipBanned = jsonObject.get((Object)"ip-banned");
        if (ipBanned != null) {
            this.ipBanned = (boolean)ipBanned;
        }
        final Object blacklisted = jsonObject.get((Object)"blacklisted");
        if (blacklisted != null) {
            this.blacklisted = (boolean)blacklisted;
        }
        final Object banned = jsonObject.get((Object)"banned");
        if (banned != null) {
            this.banned = (boolean)banned;
        }
        final Object banTime = jsonObject.get((Object)"ban-time");
        if (banTime != null) {
            this.banTime = Timestamp.valueOf((String)banTime);
            if (this.banTime.before(currentTime)) {
                this.banTime = null;
                this.banned = false;
                this.ipBanned = false;
            }
            else {
                this.banned = true;
            }
        }
        final BanWrapper wrapper = this.checkIPBan();
        if (wrapper != null) {
            return wrapper;
        }
        final Timestamp now = new Timestamp(System.currentTimeMillis());
        if (this.blacklisted) {
            return new BanWrapper(StringUtil.BLACKLIST, true);
        }
        if (this.banned && this.banTime == null) {
            return new BanWrapper(StringUtil.PERMANENT_BAN, true);
        }
        if (this.banned && this.banTime != null && this.banTime.after(now)) {
            return new BanWrapper(String.format(StringUtil.TEMPORARY_BAN, TimeUtil.millisToRoundedTime(Math.abs(System.currentTimeMillis() - this.banTime.getTime()))), this.banTime.after(now));
        }
        if (this.ipBanned) {
            return new BanWrapper(StringUtil.IP_BAN, true);
        }
        return new BanWrapper("", false);
    }
    
    private void parseRankData(final JSONObject jsonObject) {
        final Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        final Map<Rank, Timestamp> mostRecentStart = new HashMap<Rank, Timestamp>();
        final Set<Rank> notExpired = new HashSet<Rank>();
        for (final Object key : jsonObject.keySet()) {
            final Map<String, Object> ranks = (Map<String, Object>)jsonObject.get(key);
            final Rank rank = Rank.getByName(ranks.get("name"));
            if (rank != null) {
                final Timestamp recentStart = mostRecentStart.get(rank);
                final Timestamp endTime = Timestamp.valueOf(ranks.get("end-time"));
                final Timestamp startTime = Timestamp.valueOf(ranks.get("start-time"));
                if (recentStart == null) {
                    mostRecentStart.put(rank, endTime);
                    if (!endTime.after(currentTime)) {
                        continue;
                    }
                    notExpired.add(rank);
                }
                else {
                    if (!recentStart.before(startTime)) {
                        continue;
                    }
                    mostRecentStart.put(rank, startTime);
                    if (!endTime.after(currentTime)) {
                        continue;
                    }
                    notExpired.add(rank);
                }
            }
        }
        notExpired.forEach(this::setRank);
    }
    
    public void onJoin() {
        final PermissionAttachment attachment = this.getPlayer().addAttachment((Plugin)CorePlugin.getInstance());
        if (this.hasRank(Rank.ADMIN)) {
            attachment.setPermission("bukkit.command.*", true);
        }
        else if (this.hasRank(Rank.MOD)) {
            attachment.setPermission("bukkit.command.clear", true);
            attachment.setPermission("bukkit.command.effect", true);
            attachment.setPermission("bukkit.command.enchant", true);
            attachment.setPermission("bukkit.command.gamemode", true);
        }
        else if (this.hasRank(Rank.NORMAL)) {
            attachment.setPermission("bukkit.command.me", false);
            attachment.setPermission("minecraft.command.me", false);
        }
        CorePlugin.getInstance().getRequestManager().sendRequest(new PlayerDataRequest(PlayerDataRequest.PlayerDataRequestType.JOINS, this.ipAddress, this.uuid, this.name), new RequestCallback() {
            @Override
            public void callback(final JSONObject data) {
                if (!data.get((Object)"response").equals("success")) {
                    CorePlugin.getInstance().getServer().getLogger().warning("Server-side and unknown error when updating Mineman join data. " + data.toJSONString());
                }
            }
            
            @Override
            public void error(final String message) {
                CorePlugin.getInstance().getServer().getLogger().warning("Error updating Mineman join data. " + message);
            }
        });
    }
    
    public Player getPlayer() {
        return CorePlugin.getInstance().getServer().getPlayer(this.uuid);
    }
    
    public boolean hasRank(final Rank rank) {
        return this.rank.hasRank(rank);
    }
    
    private BanWrapper checkIPBan() {
        final Timestamp now = new Timestamp(System.currentTimeMillis());
        if (this.banned || this.ipBanned || (this.banTime != null && this.banTime.after(now))) {
            return null;
        }
        final JSONObject data = CorePlugin.getInstance().getRequestManager().sendRequestNow(new IPCheckRequest(this.ipAddress));
        final String response = (String)data.get((Object)"response");
        if (response.equals("banned")) {
            this.ipBanned = true;
            final Player player = this.getPlayer();
            if (player == null) {
                return new BanWrapper(String.format(StringUtil.IP_BAN_OTHER, data.get((Object)"player")), true);
            }
            player.kickPlayer(String.format(StringUtil.IP_BAN_OTHER, data.get((Object)"player")));
        }
        return null;
    }
    
    public void setRank(final Rank rank) {
        this.rank = rank;
    }
    
    public void setBanData(final BanWrapper banData) {
        this.banData = banData;
    }
    
    public void setMuteTime(final Timestamp muteTime) {
        this.muteTime = muteTime;
    }
    
    public void setBanTime(final Timestamp banTime) {
        this.banTime = banTime;
    }
    
    public void setLastConversation(final String lastConversation) {
        this.lastConversation = lastConversation;
    }
    
    public void setCustomColor(final String customColor) {
        this.customColor = customColor;
    }
    
    public void setCanSeeMessages(final boolean canSeeMessages) {
        this.canSeeMessages = canSeeMessages;
    }
    
    public void setChatEnabled(final boolean chatEnabled) {
        this.chatEnabled = chatEnabled;
    }
    
    public void setErrorLoadingData(final boolean errorLoadingData) {
        this.errorLoadingData = errorLoadingData;
    }
    
    public void setInAdminChat(final boolean inAdminChat) {
        this.inAdminChat = inAdminChat;
    }
    
    public void setInStaffChat(final boolean inStaffChat) {
        this.inStaffChat = inStaffChat;
    }
    
    public void setBlacklisted(final boolean blacklisted) {
        this.blacklisted = blacklisted;
    }
    
    public void setDataLoaded(final boolean dataLoaded) {
        this.dataLoaded = dataLoaded;
    }
    
    public void setVanishMode(final boolean vanishMode) {
        this.vanishMode = vanishMode;
    }
    
    public void setIpBanned(final boolean ipBanned) {
        this.ipBanned = ipBanned;
    }
    
    public void setBanned(final boolean banned) {
        this.banned = banned;
    }
    
    public void setMuted(final boolean muted) {
        this.muted = muted;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public void setDataLoadStage(final int dataLoadStage) {
        this.dataLoadStage = dataLoadStage;
    }
    
    public Set<Integer> getIgnoring() {
        return this.ignoring;
    }
    
    public UUID getUuid() {
        return this.uuid;
    }
    
    public String getName() {
        return this.name;
    }
    
    public InetAddress getIpAddress() {
        return this.ipAddress;
    }
    
    public Rank getRank() {
        return this.rank;
    }
    
    public BanWrapper getBanData() {
        return this.banData;
    }
    
    public Timestamp getMuteTime() {
        return this.muteTime;
    }
    
    public Timestamp getBanTime() {
        return this.banTime;
    }
    
    public String getLastConversation() {
        return this.lastConversation;
    }
    
    public String getCustomColor() {
        return this.customColor;
    }
    
    public boolean isCanSeeMessages() {
        return this.canSeeMessages;
    }
    
    public boolean isChatEnabled() {
        return this.chatEnabled;
    }
    
    public boolean isErrorLoadingData() {
        return this.errorLoadingData;
    }
    
    public boolean isInAdminChat() {
        return this.inAdminChat;
    }
    
    public boolean isInStaffChat() {
        return this.inStaffChat;
    }
    
    public boolean isBlacklisted() {
        return this.blacklisted;
    }
    
    public boolean isDataLoaded() {
        return this.dataLoaded;
    }
    
    public boolean isVanishMode() {
        return this.vanishMode;
    }
    
    public boolean isIpBanned() {
        return this.ipBanned;
    }
    
    public boolean isBanned() {
        return this.banned;
    }
    
    public boolean isMuted() {
        return this.muted;
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getDataLoadStage() {
        return this.dataLoadStage;
    }
    
    @ConstructorProperties({ "uuid", "name", "ipAddress" })
    public Mineman(final UUID uuid, final String name, final InetAddress ipAddress) {
        this.ignoring = new HashSet<Integer>();
        this.rank = Rank.NORMAL;
        this.canSeeMessages = true;
        this.chatEnabled = true;
        this.uuid = uuid;
        this.name = name;
        this.ipAddress = ipAddress;
    }
}
