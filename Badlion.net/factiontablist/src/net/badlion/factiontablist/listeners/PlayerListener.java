package net.badlion.factiontablist.listeners;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.badlion.factiontablist.TabList;
import net.badlion.factiontablist.TabMain;
import net.badlion.factiontablist.TinyProtocol;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;

public class PlayerListener implements Listener {

    public TabMain plugin;
    public TinyProtocol protocol;
    public static ArrayList<Player> adminsOnline;
	public static ArrayList<Player> wardensOnline;
    public static ArrayList<Player> modsOnline;
	public static ArrayList<Player> chatModsOnline;
    public static ArrayList<Player> emperorsOnline;
    public static ArrayList<Player> emeraldOnline;
    public static ArrayList<Player> diamondOnline;
    public static ArrayList<Player> ironOnline;
    public static ArrayList<Player> goldOnline;
    public static ArrayList<Player> coalOnline;
    public static ArrayList<Player> stoneOnline;
    public static ArrayList<Player> squiresOnline;

    public PlayerListener(TabMain plugin, TinyProtocol protocol) {
        this.plugin = plugin;
        this.protocol = protocol;

        PlayerListener.adminsOnline = new ArrayList<Player>();
	    PlayerListener.wardensOnline = new ArrayList<Player>();
	    PlayerListener.modsOnline = new ArrayList<Player>();
	    PlayerListener.chatModsOnline = new ArrayList<Player>();
        PlayerListener.emperorsOnline = new ArrayList<>();
        PlayerListener.emeraldOnline = new ArrayList<Player>();
        PlayerListener.diamondOnline = new ArrayList<Player>();
        PlayerListener.ironOnline = new ArrayList<Player>();
        PlayerListener.goldOnline = new ArrayList<Player>();
        PlayerListener.coalOnline = new ArrayList<Player>();
        PlayerListener.stoneOnline = new ArrayList<Player>();
        PlayerListener.squiresOnline = new ArrayList<Player>();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        final TabList tl = new TabList(this.plugin, p.getName(), this.protocol);
        this.plugin.tabs.put(p.getName(), tl);

        // Update our internal lists
        if (e.getPlayer().hasPermission("GFactions.admin")) {
            PlayerListener.adminsOnline.add(p);
        } else if (e.getPlayer().hasPermission("GFactions.warden")) {
	        PlayerListener.wardensOnline.add(p);
        } else if (e.getPlayer().hasPermission("bm.ban")) {
	        PlayerListener.modsOnline.add(p);
        } else if (e.getPlayer().hasPermission("GFactions.mod")) {
            PlayerListener.chatModsOnline.add(p);
        } else if (p.hasPermission("GFactions.emperor")) {
            PlayerListener.emperorsOnline.add(p);
        } else if (p.hasPermission("GFactions.emerald")) {
            PlayerListener.emeraldOnline.add(p);
        } else if (p.hasPermission("GFactions.diamond")) {
            PlayerListener.diamondOnline.add(p);
        } else if (p.hasPermission("GFactions.iron")) {
            PlayerListener.ironOnline.add(p);
        } else if (p.hasPermission("GFactions.gold")) {
            PlayerListener.goldOnline.add(p);
        } else if (p.hasPermission("GFactions.coal")) {
            PlayerListener.coalOnline.add(p);
        } else if (p.hasPermission("GFactions.stone")) {
            PlayerListener.stoneOnline.add(p);
        } else if (p.hasPermission("GFactions.squire")) {
            PlayerListener.squiresOnline.add(p);
        }


        // Update tab lists of faction members to update online members list
        final Faction faction = FPlayers.i.get(p).getFaction();
        if (!faction.getTag().equals("§2Wilderness")) {
            for (Player p2 : faction.getOnlinePlayers()) {
                if (p != p2) {
                    this.plugin.updatePlayerNoQueries(p2);
                }
            }
        }

        // Update player money
        tl.money = this.plugin.archMoney.checkBalance(p.getUniqueId().toString());

        // Update faction money
        if(!faction.getTag().equals("§2Wilderness")) {

            // Grab faction money from database
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                @Override
                public void run() {

                    // Get faction money
                    final int factionMoolah = PlayerListener.this.plugin.archMoney.checkBalanceSQL("~faction_" + faction.getId());

                    // Update tab list
                    PlayerListener.this.plugin.getServer().getScheduler().runTask(PlayerListener.this.plugin, new Runnable() {
                        @Override
                        public void run() {
                            tl.factionMoney = factionMoolah;
                            PlayerListener.this.plugin.updatePlayer(p);
                        }
                    });
                }
            });
        } else {
            // Update tab list
            PlayerListener.this.plugin.updatePlayer(p);
        }

    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        String name = p.getName();

        PlayerListener.adminsOnline.remove(p);
	    PlayerListener.wardensOnline.remove(p);
	    PlayerListener.modsOnline.remove(p);
	    PlayerListener.chatModsOnline.remove(p);
        PlayerListener.emperorsOnline.remove(p);
        PlayerListener.emeraldOnline.remove(p);
        PlayerListener.diamondOnline.remove(p);
        PlayerListener.ironOnline.remove(p);
        PlayerListener.goldOnline.remove(p);
        PlayerListener.coalOnline.remove(p);
        PlayerListener.stoneOnline.remove(p);

        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
            @Override
            public void run() {
                Faction faction = FPlayers.i.get(p).getFaction();
                if (!faction.getTag().equals("§2Wilderness")) {
                    for (Player p2 : faction.getOnlinePlayers()) {
                        plugin.updatePlayerNoQueries(p2);
                    }
                }
            }
        }, 1L);
    }

}
