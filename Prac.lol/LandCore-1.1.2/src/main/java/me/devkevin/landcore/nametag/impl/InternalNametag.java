package me.devkevin.landcore.nametag.impl;

import com.google.common.primitives.Ints;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.ScoreboardTeamBase;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 17/01/2023 @ 11:37
 * InternalNametag / land.pvp.core.nametag.impl / LandCore
 */
public  class InternalNametag {

    private JavaPlugin plugin;

    @Getter
    private static Map<String, Map<String, NametagInfo>> teamMap = new ConcurrentHashMap<>();
    private static final List<NametagInfo> registeredTeams = Collections.synchronizedList(new ArrayList<NametagInfo>());
    private static int teamCreateIndex = 1;
    private static final List<NametagProvider> providers = new ArrayList<>();

    private final static boolean async = true;
    @Getter
    private static int updateInterval = 2; // In ticks

    public InternalNametag(JavaPlugin plugin, NametagProvider provider) {
        this.plugin = plugin;

        (new NametagThread()).start();
        registerProvider(provider); // fix async provider

        this.plugin.getServer().getPluginManager().registerEvents(new NametagListener(plugin), plugin);
    }

    /**
     * Registers a new NametagProvider. Note that the newProvider
     * will not always be used. It will only be used if it is the highest
     * weighted provider available.
     *
     * @param newProvider The NametagProvider to register.
     */
    public static void registerProvider(NametagProvider newProvider) {
        providers.add(newProvider);
        providers.sort((weight, target) -> (Ints.compare(target.getWeight(), weight.getWeight())));
    }

    /**
     * Refreshes one player for all players online.
     * NOTE: This is not an instant refresh, this is queued and async.
     *
     * @param toRefresh The player to refresh.
     */
    public static void reloadPlayer(Player toRefresh) {
        NametagUpdate update = new NametagUpdate(toRefresh);

        if (async) {
            NametagThread.getPendingUpdates().put(update, true);
        } else {
            applyUpdate(update);
        }
    }

    /**
     * Reloads all OTHER players for the player provided.
     *
     * @param refreshFor The player who should have all viewable nametags refreshed.
     */
    public static void reloadOthersFor(Player refreshFor) {
        for (Player toRefresh : Bukkit.getOnlinePlayers()) {
            if (refreshFor == toRefresh) continue;
            reloadPlayer(toRefresh, refreshFor);
        }
    }

    /**
     * Refreshes one player for another player only.
     * NOTE: This is not an instant refresh, this is queued and async.
     *
     * @param toRefresh  The player to refresh.
     * @param refreshFor The player to refresh toRefresh for.
     */
    public static void reloadPlayer(Player toRefresh, Player refreshFor) {
        NametagUpdate update = new NametagUpdate(toRefresh, refreshFor);

        if (async) {
            NametagThread.getPendingUpdates().put(update, true);
        } else {
            applyUpdate(update);
        }
    }

    /**
     * Applies a pending nametag update. Only for internal use.
     *
     * @param nametagUpdate The nametag update to apply.
     */
    protected static void applyUpdate(NametagUpdate nametagUpdate) {
        Player toRefreshPlayer = Bukkit.getPlayerExact(nametagUpdate.getToRefresh());

        // Just ignore it if they logged off since the request to update was submitted
        if (toRefreshPlayer == null) {
            return;
        }

        if (nametagUpdate.getRefreshFor() == null) {
            for (Player refreshFor : Bukkit.getOnlinePlayers()) {
                reloadPlayerInternal(toRefreshPlayer, refreshFor);
            }
        } else {
            Player refreshForPlayer = Bukkit.getPlayerExact(nametagUpdate.getRefreshFor());

            if (refreshForPlayer != null) {
                reloadPlayerInternal(toRefreshPlayer, refreshForPlayer);
            }
        }
    }

    /**
     * Reloads a player sync. Only for internal use.
     *
     * @param toRefresh  The player to refresh.
     * @param refreshFor The player to refresh 'toRefresh' for.
     */
    protected static void reloadPlayerInternal(Player toRefresh, Player refreshFor) {
        if (!refreshFor.hasMetadata("NameTag-LoggedIn")) {
            return;
        }

        NametagInfo provided = null;
        int providerIndex = 0;

        while (provided == null) {
            provided = providers.get(providerIndex++).fetchNametag(toRefresh, refreshFor);
        }

        Map<String, NametagInfo> teamInfoMap = new HashMap<>();

        if (teamMap.containsKey(refreshFor.getName())) {
            teamInfoMap = teamMap.get(refreshFor.getName());
        }

        (new NametagMethod(provided.getName(), Collections.singletonList(toRefresh.getName()), 3)).sendToPlayer(refreshFor);
        teamInfoMap.put(toRefresh.getName(), provided);
        teamMap.put(refreshFor.getName(), teamInfoMap);
    }

    /**
     * 'Sets up' a player. This sends them all existing teams
     * and their members. This does NOT send new nametag
     * packets for the given player. Only for internal use.
     *
     * @param player The player to setup.
     */
    protected static void initiatePlayer(Player player) {
        for (NametagInfo teamInfo : registeredTeams) {
            teamInfo.getNametagMethod().sendToPlayer(player);
        }
    }

    /**
     * Gets or created a NametagInfo objetc
     * with the specified prefix and suffix. Only for internal use.
     *
     * @param prefix The prefix the NametagInfo object should have.
     * @param suffix The suffix the NametagInfo object should have.
     * @return The NametagInfo object with the prefix and suffix given.
     */
    protected static NametagInfo getOrCreate(String prefix, String suffix, ScoreboardTeamBase.EnumNameTagVisibility tagVisibility) {
        for (NametagInfo teamInfo : registeredTeams) {
            if (teamInfo.getPrefix().equals(prefix) && teamInfo.getSuffix().equals(suffix)) {
                return (teamInfo);
            }
        }

        NametagInfo newTeam = new NametagInfo(String.valueOf(teamCreateIndex++), prefix, suffix, tagVisibility);
        registeredTeams.add(newTeam);

        NametagMethod addPacket = newTeam.getNametagMethod();

        for (Player player : Bukkit.getOnlinePlayers()) {
            addPacket.sendToPlayer(player);
        }

        return (newTeam);
    }

    @Getter
    public static class NametagInfo {

        private String name, prefix, suffix;
        private ScoreboardTeamBase.EnumNameTagVisibility tagVisibility;
        private NametagMethod nametagMethod;

        protected NametagInfo(String name, String prefix, String suffix, ScoreboardTeamBase.EnumNameTagVisibility tagVisibility) {
            this.name = name;
            this.prefix = prefix;
            this.suffix = suffix;
            this.tagVisibility = tagVisibility;

            // add nametag visibility reader
            nametagMethod = new NametagMethod(name, prefix, suffix, tagVisibility, new ArrayList<String>(), 0);
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof NametagInfo) {
                NametagInfo otherNametag = (NametagInfo) other;
                return (name.equals(otherNametag.name) && prefix.equals(otherNametag.prefix) && suffix.equals(otherNametag.suffix));
            }

            return (false);
        }
    }

    /**
     * A nametag update that is queued to happen.
     * Commonly the update is queued from a sync. thread.
     */
    @Getter
    public static class NametagUpdate {

        private String toRefresh;
        private String refreshFor;

        /**
         * Refreshes one player for all players online.
         *
         * @param toRefresh The player to refresh.
         */
        public NametagUpdate(Player toRefresh) {
            this.toRefresh = toRefresh.getName();
        }

        /**
         * Refreshes one player for another player only.
         *
         * @param toRefresh  The player to refresh.
         * @param refreshFor The player to refresh toRefresh for.
         */
        public NametagUpdate(Player toRefresh, Player refreshFor) {
            this.toRefresh = toRefresh.getName();
            this.refreshFor = refreshFor.getName();
        }
    }

    /**
     * A simple scoreboard team reflection
     * */
    public static class NametagMethod {

        private PacketPlayOutScoreboardTeam scoreboardTeam;

        public NametagMethod(String name, String prefix, String suffix, ScoreboardTeamBase.EnumNameTagVisibility tagVisibility, Collection<String> players, int paramInt) {
            scoreboardTeam = new PacketPlayOutScoreboardTeam();

            setField("a", name);
            setField("h", paramInt);

            if(paramInt == 0 || paramInt == 2) {
                setField("b", name);
                setField("c", prefix);
                setField("d", suffix);
                setField("e", tagVisibility.e);
                setField("i", 1);
            }

            if(paramInt == 0) {
                addAll(players);
            }
        }

        public NametagMethod(String name, Collection<String> players, int paramInt) {
            scoreboardTeam = new PacketPlayOutScoreboardTeam();

            if(players == null) {
                players = new ArrayList<String>();
            }

            setField("a", name);
            setField("h", paramInt);

            addAll(players);
        }

        public void sendToPlayer(Player bukkitPlayer) {
            ((CraftPlayer) bukkitPlayer).getHandle().playerConnection.sendPacket(scoreboardTeam);
        }

        private void setField(String field, Object value) {
            try {
                Field fieldObject = scoreboardTeam.getClass().getDeclaredField(field);

                fieldObject.setAccessible(true);
                fieldObject.set(scoreboardTeam, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void addAll(Collection<String> collection) {
            try {
                Field fieldObject = scoreboardTeam.getClass().getDeclaredField("g");

                fieldObject.setAccessible(true);
                ((Collection<String>) fieldObject.get(scoreboardTeam)).addAll(collection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class NametagThread extends Thread {

        // We use a Map here for a few reasons...
        // 1) Why the heck not
        // 2) There's no good concurrent set implementation
        // 3) (Concurrent) Sets are backed by Maps anyway so...
        @Getter private static Map<NametagUpdate, Boolean> pendingUpdates = new ConcurrentHashMap<>();

        public NametagThread() {
            super("InternalNametag Thread");

            this.setDaemon(false);
        }

        public void run() {
            while (true) {
                Iterator<NametagUpdate> pendingUpdatesIterator = pendingUpdates.keySet().iterator();

                while (pendingUpdatesIterator.hasNext()) {
                    NametagUpdate pendingUpdate = pendingUpdatesIterator.next();

                    try {
                        InternalNametag.applyUpdate(pendingUpdate);
                        pendingUpdatesIterator.remove();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(InternalNametag.getUpdateInterval() * 50L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

