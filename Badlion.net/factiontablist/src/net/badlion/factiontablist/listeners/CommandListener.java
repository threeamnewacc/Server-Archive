package net.badlion.factiontablist.listeners;

import net.badlion.factiontablist.TabMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

    public TabMain plugin;

    private String admins;
	private String wardens;
	private String mods;
	private String chatMods;
    private String emperor;
    private String emerald;
    private String diamond;
    private String iron;
    private String gold;
    private String coal;
    private String stone;
    private String squires;

    public CommandListener(TabMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void listCommand(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().equalsIgnoreCase("/list")) {
            e.setCancelled(true);
            this.updateStrings();
            Player p = e.getPlayer();
            p.sendMessage(ChatColor.RED + "=====================================================");
            p.sendMessage(ChatColor.GREEN + "Players Online [" + ChatColor.GOLD + this.plugin.getServer().getOnlinePlayers().size() + "/" + Bukkit.getServer().getMaxPlayers() + ChatColor.GREEN + "]:");
            p.sendMessage(ChatColor.DARK_RED + "[Administrators]: " + ChatColor.RESET + this.admins);
	        p.sendMessage(ChatColor.DARK_AQUA + "[Wardens]: " + ChatColor.RESET + this.wardens);
	        p.sendMessage(ChatColor.DARK_AQUA + "[Moderators]: " + ChatColor.RESET + this.mods);
	        p.sendMessage(ChatColor.DARK_GREEN + "[Chat Moderators]: " + ChatColor.RESET + this.chatMods);
            p.sendMessage("§5[Emperors]: " + ChatColor.RESET + this.emperor);
            p.sendMessage("§6[Kings/Queens]: " + ChatColor.RESET + this.emerald);
            p.sendMessage("§b[Princes/Princesses]: " + ChatColor.RESET + this.diamond);
            p.sendMessage("§a[Archdukes]: " + ChatColor.RESET + this.iron);
            p.sendMessage("§d[Dukes]: " + ChatColor.RESET + this.gold);
            p.sendMessage("§e[Musketeers]: " + ChatColor.RESET + this.coal);
            p.sendMessage("§5[Knights]: " + ChatColor.RESET + this.stone);
            p.sendMessage("§2[Squires]: " + ChatColor.RESET + this.squires);
            p.sendMessage(ChatColor.RED + "=====================================================");
        }
    }

    public void updateStrings() {
        StringBuilder sb = new StringBuilder();
        if (PlayerListener.adminsOnline.size() > 0) {
            for (Player p : PlayerListener.adminsOnline) {
                sb.append(", ");
                sb.append(p.getDisplayName());
                sb.append(ChatColor.RESET);
            }
            admins = sb.substring(2);
            sb.setLength(0);
        } else {
            admins = "None";
        }

	    if (PlayerListener.wardensOnline.size() > 0) {
		    for (Player p : PlayerListener.wardensOnline) {
			    sb.append(", ");
			    sb.append(p.getDisplayName());
			    sb.append(ChatColor.RESET);
		    }
		    wardens = sb.substring(2);
		    sb.setLength(0);
	    } else {
		    wardens = "None";
	    }

	    if (PlayerListener.modsOnline.size() > 0) {
		    for (Player p : PlayerListener.modsOnline) {
			    sb.append(", ");
			    sb.append(p.getDisplayName());
			    sb.append(ChatColor.RESET);
		    }
		    mods = sb.substring(2);
		    sb.setLength(0);
	    } else {
		    mods = "None";
	    }

	    if (PlayerListener.chatModsOnline.size() > 0) {
		    for (Player p : PlayerListener.chatModsOnline) {
			    sb.append(", ");
			    sb.append(p.getDisplayName());
			    sb.append(ChatColor.RESET);
		    }
		    chatMods = sb.substring(2);
		    sb.setLength(0);
	    } else {
		    chatMods = "None";
	    }

        if (PlayerListener.emperorsOnline.size() > 0) {
            for (Player p : PlayerListener.emperorsOnline) {
                sb.append(", ");
                sb.append(p.getDisplayName());
                sb.append(ChatColor.RESET);
            }
            emperor = sb.substring(2);
            sb.setLength(0);
        } else {
            emperor = "None";
        }

        if (PlayerListener.emeraldOnline.size() > 0) {
            for (Player p : PlayerListener.emeraldOnline) {
                sb.append(", ");
                sb.append(p.getDisplayName());
                sb.append(ChatColor.RESET);
            }
            emerald = sb.substring(2);
            sb.setLength(0);
        } else {
            emerald = "None";
        }

        if (PlayerListener.diamondOnline.size() > 0) {
            for (Player p : PlayerListener.diamondOnline) {
                sb.append(", ");
                sb.append(p.getDisplayName());
                sb.append(ChatColor.RESET);
            }
            diamond = sb.substring(2);
            sb.setLength(0);
        } else {
            diamond = "None";
        }

        if (PlayerListener.ironOnline.size() > 0) {
            for (Player p : PlayerListener.ironOnline) {
                sb.append(", ");
                sb.append(p.getDisplayName());
                sb.append(ChatColor.RESET);
            }
            iron = sb.substring(2);
            sb.setLength(0);
        } else {
            iron = "None";
        }

        if (PlayerListener.goldOnline.size() > 0) {
            for (Player p : PlayerListener.goldOnline) {
                sb.append(", ");
                sb.append(p.getDisplayName());
                sb.append(ChatColor.RESET);
            }
            gold = sb.substring(2);
            sb.setLength(0);
        } else {
            gold = "None";
        }

        if (PlayerListener.coalOnline.size() > 0) {
            for (Player p : PlayerListener.coalOnline) {
                sb.append(", ");
                sb.append(p.getDisplayName());
                sb.append(ChatColor.RESET);
            }
            coal = sb.substring(2);
            sb.setLength(0);
        } else {
            coal = "None";
        }

        if (PlayerListener.stoneOnline.size() > 0) {
            for (Player p : PlayerListener.stoneOnline) {
                sb.append(", ");
                sb.append(p.getDisplayName());
                sb.append(ChatColor.RESET);
            }
            stone = sb.substring(2);
            sb.setLength(0);
        } else {
            stone = "None";
        }

        if (PlayerListener.squiresOnline.size() > 0) {
            for (Player p : PlayerListener.squiresOnline) {
                sb.append(", ");
                sb.append(p.getDisplayName());
                sb.append(ChatColor.RESET);
            }
            squires = sb.substring(2);
        } else {
            squires = "None";
        }
    }

}
