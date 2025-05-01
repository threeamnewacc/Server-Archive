package net.badlion.factiontablist;

import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedList;

public class TabList {

    private TabMain plugin;
    private TinyProtocol protocol;

    public String name;

    public String[][] tabStrings = new String[20][3];

    public String rank;
    public Integer money;
    public Integer factionMoney;

    public Integer numOfHomes = -1;
    public LinkedList<String> homes = new LinkedList<String>();

    public TabList(TabMain plugin, String name, TinyProtocol protocol) {
        this.plugin = plugin;
        this.protocol = protocol;

        this.name = name;

        // Setup static tab list strings
        this.plugin.setTabString(this, 0, 0, "§cHomes Set:");

        this.plugin.setTabString(this, 0, 3, "§cHomes:");

        this.plugin.setTabString(this, 1, 0, "§cFaction Info:");

        this.plugin.setTabString(this, 1, 5, "§cRank:");

        this.plugin.setTabString(this, 1, 8, "§6§lBadlion");
        this.plugin.setTabString(this, 1, 9, "§6§lFactions");

        this.plugin.setTabString(this, 1, 11, "§cEvents:");

        this.plugin.setTabString(this, 1, 16, "§cPlayer Info:");

        this.plugin.setTabString(this, 2, 0, "§cFaction");
        this.plugin.setTabString(this, 2, 1, "§cMembers On:");
    }

    public void updateTabInterface() {
        Player p = this.plugin.getServer().getPlayerExact(this.name);
        if(p == null) return;

        // 1.8 Start
        if (((CraftPlayer) p).getHandle().playerConnection.networkManager.getVersion() >= 20) { // Not in 1.7
            return;
        }
        // 1.8 End

        ArrayList<String> blankStringsCopy = new ArrayList<String>(this.plugin.blankStrings);
        ArrayList<String> packetStrings = new ArrayList<String>();

        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 3; x++) {
                String tabString = this.tabStrings[y][x];

                if(tabString == null) {
                    tabString = blankStringsCopy.get(0);
                    blankStringsCopy.remove(0);
                }

                if(tabString.length() > 16) {
                    tabString = blankStringsCopy.get(0);
                    blankStringsCopy.remove(0);
                }

                packetStrings.add(tabString);

                try {
                    Object packet = this.plugin.packetClass.newInstance();
                    this.plugin.packetName.set(packet, "!" + tabString);
                    this.plugin.packetOnline.set(packet, 0); // Add
                    this.protocol.sendPacket(p, packet);
                } catch (IllegalAccessException e) {
                    System.out.println("[TabMain] Error sending packet to client");
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    System.out.println("[TabMain] Error sending packet to client");
                    e.printStackTrace();
                }
            }
        }

        this.plugin.sentPackets.put(this.name, packetStrings);
    }

}
