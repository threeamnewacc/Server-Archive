package us.zonix.practice.commands.management;

import me.maiko.dexter.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.practice.Practice;
import us.zonix.practice.kit.Kit;
import us.zonix.practice.tournament.Tournament;
import us.zonix.practice.tournament.TournamentState;
import us.zonix.practice.util.Clickable;
import us.zonix.practice.util.ItemUtil;
import us.zonix.practice.util.inventory.InventoryUI;

public class TournamentCommand extends Command {
    private final Practice plugin = Practice.getInstance();
    private static final String[] HELP_ADMIN_MESSAGE = new String[]{
        ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
        ChatColor.RED + "Tournament Commands:",
        ChatColor.GOLD + "(*) /tournament start " + ChatColor.GRAY + "- Start a Tournament",
        ChatColor.GOLD + "(*) /tournament stop " + ChatColor.GRAY + "- Stop a Tournament",
        ChatColor.GOLD + "(*) /tournament alert " + ChatColor.GRAY + "- Alert a Tournament",
        ChatColor.GOLD + "(*) /tournament host " + ChatColor.GRAY + "- Open tournament GUI",
        ChatColor.GOLD + "(*) /tournament forcestart " + ChatColor.GRAY + "- Force start a tournament",
        ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------"
    };
    private static final String[] HELP_REGULAR_MESSAGE = new String[]{
        ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
        ChatColor.RED + "Tournament Commands:",
        ChatColor.GOLD + "(*) /join <id> " + ChatColor.GRAY + "- Join a Tournament",
        ChatColor.GOLD + "(*) /leave " + ChatColor.GRAY + "- Leave a Tournament",
        ChatColor.GOLD + "(*) /status " + ChatColor.GRAY + "- Status of a Tournament",
        ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------"
    };
    private final InventoryUI inventoryUI = new InventoryUI(ChatColor.GRAY + "Start a tournament", 2);

    public TournamentCommand() {
        super("tournament");
        this.setUsage(ChatColor.RED + "Usage: /tournament [args]");
        (new BukkitRunnable() {
            public void run() {
                TournamentCommand.this.setItems();
            }
        }).runTaskLater(this.plugin, 30L);
    }

    private void setItems() {
        this.plugin
            .getKitManager()
            .getKits()
            .forEach(
                kit -> {
                    if (kit.isEnabled()) {
                        this.inventoryUI
                            .setItem(
                                kit.getPriority(),
                                new InventoryUI.ClickableItem() {
                                    private final ItemStack def = ItemUtil.renameItem(
                                        ItemUtil.reloreItem(kit.getIcon(), ChatColor.GRAY + "Click to host tournament"), ChatColor.GRAY + kit.getName()
                                    );
                                    private ItemStack itemStack = def.clone();

                                    @Override
                                    public void onClick(InventoryClickEvent event) {
                                        InventoryUI inventoryUI = new InventoryUI(ChatColor.GRAY + "Select team size", 1);

                                        for (int i = 1; i < 5; i++) {
                                            final int finalI = i;
                                            inventoryUI.addItem(
                                                new InventoryUI.ClickableItem() {
                                                    private final ItemStack def = ItemUtil.renameItem(
                                                        ItemUtil.reloreItem(new ItemStack(Material.NAME_TAG), ChatColor.GRAY + "Teamsize: " + finalI),
                                                        ChatColor.GRAY + kit.getName()
                                                    );
                                                    private ItemStack itemStack = def.clone();

                                                    @Override
                                                    public void onClick(InventoryClickEvent event) {
                                                        if (!TournamentCommand.this.plugin.getTournamentManager().getTournaments().isEmpty()) {
                                                            event.getWhoClicked().sendMessage(ChatColor.RED + "There already is an ongoing tournament.");
                                                            event.getWhoClicked().closeInventory();
                                                        } else {
                                                            TournamentCommand.this.plugin
                                                                .getTournamentManager()
                                                                .createTournament(event.getWhoClicked(), 10, finalI, 150, kit.getName());
                                                        }
                                                    }

                                                    @Override
                                                    public ItemStack getItemStack() {
                                                        return this.itemStack;
                                                    }

                                                    @Override
                                                    public void setItemStack(ItemStack itemStack) {
                                                        this.itemStack = itemStack;
                                                    }

                                                    @Override
                                                    public ItemStack getDefaultItemStack() {
                                                        return this.def;
                                                    }
                                                }
                                            );
                                        }

                                        event.getWhoClicked().openInventory(inventoryUI.getCurrentPage());
                                    }

                                    @Override
                                    public ItemStack getItemStack() {
                                        return this.itemStack;
                                    }

                                    @Override
                                    public void setItemStack(ItemStack itemStack) {
                                        this.itemStack = itemStack;
                                    }

                                    @Override
                                    public ItemStack getDefaultItemStack() {
                                        return this.def;
                                    }
                                }
                            );
                    }
                }
            );
    }

    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        } else {
            Player player = (Player)commandSender;
            if (args.length == 0) {
                commandSender.sendMessage(player.isOp() ? HELP_ADMIN_MESSAGE : HELP_REGULAR_MESSAGE);
                return true;
            } else {
                String var5 = args[0].toLowerCase();
                switch (var5) {
                    case "host":
                        if (player.hasPermission("practice.tournament.host")) {
                            player.openInventory(this.inventoryUI.getCurrentPage());
                        } else {
                            player.sendMessage(CC.RED + "No permission.");
                        }

                        return true;
                    case "start":
                        if (args.length == 5) {
                            try {
                                int id = Integer.parseInt(args[1]);
                                int teamSize = Integer.parseInt(args[3]);
                                int size = Integer.parseInt(args[4]);
                                String kitName = args[2];
                                if (size % teamSize != 0) {
                                    commandSender.sendMessage(ChatColor.RED + "Tournament size & team sizes are invalid. Please try again.");
                                    return true;
                                }

                                if (this.plugin.getTournamentManager().getTournament(id) != null) {
                                    commandSender.sendMessage(ChatColor.RED + "This tournament already exists.");
                                    return true;
                                }

                                Kit kit = this.plugin.getKitManager().getKit(kitName);
                                if (kit == null) {
                                    commandSender.sendMessage(ChatColor.RED + "That kit does not exist.");
                                    return true;
                                }

                                this.plugin.getTournamentManager().createTournament(commandSender, id, teamSize, size, kitName);
                            } catch (NumberFormatException var12) {
                                commandSender.sendMessage(ChatColor.RED + "Usage: /tournament start <id> <kit> <team size> <tournament size>");
                            }
                        } else {
                            commandSender.sendMessage(ChatColor.RED + "Usage: /tournament start <id> <kit> <team size> <tournament size>");
                        }
                        break;
                    case "stop":
                        if (args.length == 2) {
                            int id = Integer.parseInt(args[1]);
                            Tournament tournament = this.plugin.getTournamentManager().getTournament(id);
                            if (tournament != null) {
                                this.plugin.getTournamentManager().removeTournament(id, true);
                                commandSender.sendMessage(ChatColor.RED + "Successfully removed tournament " + id + ".");
                            }

                            commandSender.sendMessage(ChatColor.RED + "This tournament does not exist.");
                        } else {
                            commandSender.sendMessage(ChatColor.RED + "Usage: /tournament stop <id>");
                        }
                        break;
                    case "alert":
                        if (args.length == 2) {
                            int id = Integer.parseInt(args[1]);
                            Tournament tournament = this.plugin.getTournamentManager().getTournament(id);
                            if (tournament != null) {
                                String toSend = ChatColor.RED.toString()
                                    + ChatColor.BOLD
                                    + tournament.getKitName()
                                    + ChatColor.RED
                                    + " ["
                                    + tournament.getTeamSize()
                                    + "v"
                                    + tournament.getTeamSize()
                                    + "]"
                                    + ChatColor.WHITE
                                    + " is starting soon. "
                                    + ChatColor.GRAY
                                    + "[Click to Join]";
                                Clickable message = new Clickable(toSend, ChatColor.GRAY + "Click to join this tournament.", "/join " + id);
                                Bukkit.getServer().getOnlinePlayers().forEach(message::sendToPlayer);
                            }
                        } else {
                            commandSender.sendMessage(ChatColor.RED + "Usage: /tournament alert <id>");
                        }
                        break;
                    case "forcestart":
                        if (args.length == 2) {
                            int id = Integer.parseInt(args[1]);
                            Tournament tournament = this.plugin.getTournamentManager().getTournament(id);
                            if (tournament != null) {
                                if (tournament.getTournamentState() == TournamentState.FIGHTING) {
                                    commandSender.sendMessage(ChatColor.RED + "Tournament already started.");
                                    return true;
                                }

                                tournament.setTournamentState(TournamentState.STARTING);
                                tournament.setCountdown(5);
                            }
                        } else {
                            commandSender.sendMessage(ChatColor.RED + "Usage: /tournament forcestart <id>");
                        }
                        break;
                    default:
                        commandSender.sendMessage(player.isOp() ? HELP_ADMIN_MESSAGE : HELP_REGULAR_MESSAGE);
                }

                return false;
            }
        }
    }
}
