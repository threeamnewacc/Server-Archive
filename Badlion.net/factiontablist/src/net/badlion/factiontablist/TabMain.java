package net.badlion.factiontablist;

import com.google.common.collect.ImmutableList;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.badlion.archmoney.ArchMoney;
import net.badlion.gfactions.GFactions;
import net.badlion.factiontablist.listeners.CommandListener;
import net.badlion.factiontablist.listeners.CustomEventListener;
import net.badlion.factiontablist.listeners.PlayerListener;
import net.badlion.gberry.Gberry;
import net.minecraft.util.io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class TabMain extends JavaPlugin implements Listener {


    private TabMain plugin;
    public ArchMoney archMoney;
    public GFactions factions;
    public static boolean allowConnections = false;

    public TinyProtocol protocol;

    public LinkedList<String> events = new LinkedList<String>();
    public HashMap<String, String> donationNames = new HashMap<String, String>();

    public HashMap<String, TabList> tabs = new HashMap<String, TabList>();

    // Packet stuff
    public ArrayList<String> blankStrings = new ArrayList<String>();
    public HashMap<String, ArrayList<String>> sentPackets = new HashMap<String, ArrayList<String>>();

    // Player info packet
    public static Class<?> packetClass = Reflection.getClass("{nms}.PacketPlayOutPlayerInfo");
    public static Reflection.FieldAccessor<String> packetName = Reflection.getField(packetClass, String.class, 0);
    public static Reflection.FieldAccessor<Integer> packetOnline = Reflection.getField(packetClass, int.class, 5);

    public TabMain() {
        this.donationNames.put("default", "Peasant");
        this.donationNames.put("member", "Villager");
        this.donationNames.put("squire", "Squire");
        this.donationNames.put("stone", "Knight");
        this.donationNames.put("coal", "Musketeer");
        this.donationNames.put("gold", "Duke");
        this.donationNames.put("iron", "Archduke");
        this.donationNames.put("princess", "Princess");
        this.donationNames.put("diamond", "Prince");
        this.donationNames.put("emerald", "King");
        this.donationNames.put("queen", "Queen");
        this.donationNames.put("mod", "Chat Mod");
        this.donationNames.put("admin", "Admin");
    }

    public void onEnable() {
        this.plugin = this;
        this.archMoney = (ArchMoney) getServer().getPluginManager().getPlugin("ArchMoney");
        this.factions = (GFactions) getServer().getPluginManager().getPlugin("GFactions");

        // Don't let players connect for the first five seconds
        this.getServer().getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                TabMain.allowConnections = true;
            }
        }, 100L);

        // Packet listener shit
        this.protocol = new TinyProtocol(this) {
            @Override
            public Object onPacketOutAsync(Player reciever, Channel channel, Object packet) {
                if (packetClass.isInstance(packet)) {
                    // 1.8 Start
                    if (((CraftPlayer) reciever).getHandle().playerConnection.networkManager.getVersion() >= 20) { // Not in 1.7
                        return packet;
                    }
                    // 1.8 End

                    String name = packetName.get(packet);
                    if (name.startsWith("!")) {
                        packetName.set(packet, name.substring(1));
                    } else {
                        return null;
                    }
                    return packet;
                }
                return super.onPacketOutAsync(reciever, channel, packet);
            }
        };

        this.fillBlankStrings();

        this.getServer().getPluginManager().registerEvents(new CommandListener(this), this);
        this.getServer().getPluginManager().registerEvents(new CustomEventListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this, this.protocol), this);
    }

    public void onDisable() {
    }

    public void remakeTabList(Player p, TabList tl) {
        this.flushPackets(p);

        //this.setTabString(tl, 0, 0, "§cHomes Set:");
        synchronized (tl.numOfHomes) {
            if (tl.numOfHomes != -1) {
                this.setTabString(tl, 0, 1, "§9" + tl.numOfHomes);
            } else {
                this.setTabString(tl, 0, 1, "§9None");
            }
        }

        //this.setTabString(tl, 0, 3, "§cHomes:");

        // Clear the homes list
        for (int x = 0; x < 16; x++) {
            this.setTabString(tl, 0, 4 + x, null);
        }

        // List homes
        synchronized (tl.homes) {
            if (!tl.homes.isEmpty()) {
                for (int x = 0; x < tl.homes.size() && x < 16; x++) {
                    this.setTabString(tl, 0, 4 + x, "§9" + tl.homes.get(x));
                }
            } else {
                this.setTabString(tl, 0, 4, "§9None ");
            }
        }

        // Faction Info
        //this.setTabString(tl, 1, 0, "§cFaction Info:");

        Faction faction = FPlayers.i.get(p).getFaction();
        if (faction.getTag().equals("§2Wilderness")) {
            this.setTabString(tl, 1, 1, "§9Bank: N/A");
            this.setTabString(tl, 1, 2, "§9Claimed: N/A");
            this.setTabString(tl, 1, 3, "§9Power: N/A");
        } else {
            this.setTabString(tl, 1, 1, "§9Bank: $" + tl.factionMoney);
            this.setTabString(tl, 1, 2, "§9Claimed: " + faction.getLandRounded());
            this.setTabString(tl, 1, 3, "§9Power: " + faction.getPowerRounded() + "/" + faction.getPowerMaxRounded());
        }

        //this.setTabString(tl, 1, 5, "§cRank:");
        this.setTabString(tl, 1, 6, "§9" + tl.rank);

        //this.setTabString(tl, 1, 8, "§6§lBadlion");
        //this.setTabString(tl, 1, 9, "§6§lFactions");

        // Clear the events list
        for (int x = 0; x < 3; x++) {
            this.setTabString(tl, 1, 12 + x, null);
        }

        //this.setTabString(tl, 1, 11, "§cEvents:");

        // List the current events
        for (int x = 0; x < 3; x++) {
            if (x < this.events.size()) {
                this.setTabString(tl, 1, 12 + x, "§9" + this.events.get(x));
            } else {
                if (x == 0) {
                    this.setTabString(tl, 1, 12, "§9None  ");
                }
                break;
            }
        }

        //this.setTabString(tl, 1, 16, "§cPlayer Info:");
        this.setTabString(tl, 1, 17, "§9Kills - " + p.getStatistic(Statistic.PLAYER_KILLS));
        this.setTabString(tl, 1, 18, "§9Deaths - " + p.getStatistic(Statistic.DEATHS));
        this.setTabString(tl, 1, 19, "§9$" + tl.money);

        //this.setTabString(tl, 2, 0, "§cMembers On:");

        // Clear faction member list
        for (int x = 0; x < 18; x++) {
            this.setTabString(tl, 2, 2 + x, null);
        }

        // List all faction members online
        int x = 0;
        for (FPlayer fp : faction.getFPlayersWhereOnline(true)) {
            if(2 + x > 19) break;

            if (!fp.getPlayer().equals(p)) {
                String name = fp.getPlayer().getName();

                this.setTabString(tl, 2, 2 + x, name);
                x++;
            }
        }
    }

    public void updatePlayer(final Player p) {
        final TabList tl = this.tabs.get(p.getName());

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                // Run all queries needed
                updateDonationInfo(p, tl);
                updateNumberOfHomes(p, tl);
                updateHomes(p, tl);

                // Once queries are done running, update the tab list
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    public void run() {
                        TabMain.this.remakeTabList(p, tl);
                        tl.updateTabInterface();
                    }
                });
            }
        });
    }

    public void updatePlayerMoney(Player p) {
        TabList tl = this.tabs.get(p.getName());

        tl.money = this.archMoney.checkBalance(p.getUniqueId().toString());

        this.remakeTabList(p, tl);
        tl.updateTabInterface();
    }

    public void updatePlayerFactionMoney(Player p, int moolah) {
        TabList tl = this.tabs.get(p.getName());

        tl.factionMoney = moolah;

        this.remakeTabList(p, tl);
        tl.updateTabInterface();
    }

    public void updatePlayerNoQueries(final Player p) {
        TabList tl = this.tabs.get(p.getName());

        this.remakeTabList(p, tl);
        tl.updateTabInterface();
    }

    public void updateAllTabListsNoQueries() {
		final ImmutableList<Player> players = ImmutableList.copyOf(Bukkit.getOnlinePlayers());
        int size = players.size();
        int diff = (int) Math.ceil((double) players.size() / 40D);
        for (int i = 0, j = 0; i < size; i += diff) {
            // Overshot
            if (i >= size) {
                return;
            }

            // Some shit for the task
            final int start = i;
            final int end = i + diff;
            this.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {

                @Override
                public void run() {
                    for (int i = start; i < end; ++i) {
                        // Overshot
                        if (i >= players.size()) {
                            return;
                        }

                        updatePlayerNoQueries(players.get(i));
                    }
                }

            }, ++j);
        }
    }

    public void updateNumberOfHomes(final Player p, final TabList tl) {
        String query = "SELECT COUNT(*) FROM faction_homes WHERE uuid = ?;";
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, p.getUniqueId().toString());
            rs = Gberry.executeQuery(connection, ps);

            if (rs.next()) {
                synchronized(tl.numOfHomes) {
                    tl.numOfHomes = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateHomes(final Player p, final TabList tl) {
        String query = "SELECT * FROM faction_homes WHERE uuid = ?;";
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, p.getUniqueId().toString());
            rs = Gberry.executeQuery(connection, ps);

            // Clear internal list
            synchronized (tl.homes) {
                tl.homes.clear();

                while (rs.next()) {
                    tl.homes.add(rs.getString("home"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateDonationInfo(final Player p, final TabList tl) {
        String query = "SELECT * FROM faction_gperms_users WHERE uuid = ?;";
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, p.getUniqueId().toString());
            rs = Gberry.executeQuery(connection, ps);

            if (rs.next()) {
                String group = rs.getString("group");
                tl.rank = this.donationNames.get(group);
            } else {
                tl.rank = "Peasant";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setTabString(TabList tl, int x, int y, String msg) { // Starts at 0,0 & max at 2, 19
        tl.tabStrings[y][x] = msg;
    }

    public void flushPackets(final Player p) {
        if (p == null) {
            return;
        }

        if (this.sentPackets.containsKey(p.getName())) {
            ArrayList<String> packetStrings = this.sentPackets.get(p.getName());
            for (String s : packetStrings) {
                try {
                    Object packet = this.packetClass.newInstance();
                    packetName.set(packet, "!" + s);
                    packetOnline.set(packet, 4); // Remove player
                    this.protocol.sendPacket(p, packet);
                } catch (IllegalAccessException e) {
                    System.out.println("[TabMain] Error sending packet to client");
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    System.out.println("[TabMain] Error sending packet to client");
                    e.printStackTrace();
                }
            }
            this.sentPackets.remove(p.getName());
        }
    }

    public void fillBlankStrings() {
        int characterCounter = 0;

        while (characterCounter < 15) {
            StringBuilder sb = new StringBuilder();
            characterCounter++;
            for (int x = 0; x <= characterCounter; x++) {
                sb.append((char) 0x0020);
            }
            this.blankStrings.add(sb.toString());
        }
        characterCounter = 0;

        while (characterCounter < 15) {
            StringBuilder sb = new StringBuilder();
            characterCounter++;
            for (int x = 0; x <= characterCounter; x++) {
                sb.append((char) 0x26f7);
            }
            this.blankStrings.add(sb.toString());
        }
        characterCounter = 0;

        while (characterCounter < 15) {
            StringBuilder sb = new StringBuilder();
            characterCounter++;
            for (int x = 0; x <= characterCounter; x++) {
                sb.append((char) 0x26f8);
            }
            this.blankStrings.add(sb.toString());
        }
        characterCounter = 0;

        while (characterCounter < 15) {
            characterCounter++;
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x <= characterCounter; x++) {
                sb.append((char) 0x26c7);
            }
            this.blankStrings.add(sb.toString());
        }
    }

}
