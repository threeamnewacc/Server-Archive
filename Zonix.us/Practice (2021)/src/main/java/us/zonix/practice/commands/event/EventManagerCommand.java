package us.zonix.practice.commands.event;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import us.zonix.practice.Practice;
import us.zonix.practice.events.EventState;
import us.zonix.practice.events.PracticeEvent;
import us.zonix.practice.events.woolmixup.WoolMixUpEvent;
import us.zonix.practice.util.ItemUtil;
import us.zonix.practice.util.inventory.InventoryUI;

public class EventManagerCommand extends Command {
    private final Practice plugin = Practice.getInstance();

    public EventManagerCommand() {
        super("eventmanager");
        this.setDescription("Manage an event.");
        this.setUsage(ChatColor.RED + "Usage: /eventmanager <start/end/status/cooldown> <event>");
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {
            Player player = (Player)sender;
            if (!player.hasPermission("practice.eventextra")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
                return true;
            } else if (args.length < 2) {
                player.openInventory(this.buildInventory().getCurrentPage());
                return true;
            } else {
                String action = args[0];
                String eventName = args[1];
                if (this.plugin.getEventManager().getByName(eventName) == null) {
                    player.sendMessage(ChatColor.RED + "That event doesn't exist.");
                    return true;
                } else {
                    PracticeEvent event = this.plugin.getEventManager().getByName(eventName);
                    if (action.toUpperCase().equalsIgnoreCase("START") && event.getState() == EventState.WAITING) {
                        event.getCountdownTask().setTimeUntilStart(5);
                        player.sendMessage(ChatColor.RED + "Event was force started.");
                    } else if (action.toUpperCase().equalsIgnoreCase("END") && event.getState() == EventState.STARTED) {
                        event.end();
                        player.sendMessage(ChatColor.RED + "Event was cancelled.");
                    } else if (!action.toUpperCase().equalsIgnoreCase("STATUS")) {
                        if (action.toUpperCase().equalsIgnoreCase("COOLDOWN")) {
                            this.plugin.getEventManager().setCooldown(0L);
                            player.sendMessage(ChatColor.RED + "Event cooldown was cancelled.");
                        } else if (action.toUpperCase().equalsIgnoreCase("SETUP")) {
                            if (eventName.toUpperCase().contains("WOOLMIXUP")) {
                                WoolMixUpEvent woolMixUpEvent = (WoolMixUpEvent)event;
                                woolMixUpEvent.generateArena(player.getLocation());
                                player.sendMessage(ChatColor.GREEN + "Generating the arena.");
                            }
                        } else {
                            player.sendMessage(this.usageMessage);
                        }
                    }

                    return true;
                }
            }
        }
    }

    private InventoryUI buildInventory() {
        InventoryUI inventory = new InventoryUI("Event Manager", true, 1);
        inventory.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.NETHER_STAR, ChatColor.GREEN + "Force Start")) {
            @Override
            public void onClick(InventoryClickEvent e) {
                Player player = (Player)e.getWhoClicked();
                PracticeEvent event = EventManagerCommand.this.getEventByState(EventState.WAITING);
                if (event == null) {
                    player.sendMessage(ChatColor.RED + "There is no active event.");
                    player.closeInventory();
                } else {
                    event.getCountdownTask().setTimeUntilStart(5);
                    player.sendMessage(ChatColor.RED + "Event was force started.");
                    player.closeInventory();
                }
            }
        });
        inventory.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.EYE_OF_ENDER, ChatColor.GREEN + "Force End")) {
            @Override
            public void onClick(InventoryClickEvent e) {
                Player player = (Player)e.getWhoClicked();
                PracticeEvent event = EventManagerCommand.this.getEventByState(EventState.STARTED);
                if (event == null) {
                    player.sendMessage(ChatColor.RED + "There is no active event.");
                    player.closeInventory();
                } else {
                    event.end();
                    player.sendMessage(ChatColor.RED + "Event was cancelled.");
                    player.closeInventory();
                }
            }
        });
        inventory.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.WATCH, ChatColor.GREEN + "Remove Cooldown")) {
            @Override
            public void onClick(InventoryClickEvent e) {
                Player player = (Player)e.getWhoClicked();
                boolean cooldown = System.currentTimeMillis() < EventManagerCommand.this.plugin.getEventManager().getCooldown();
                if (!cooldown) {
                    player.sendMessage(ChatColor.RED + "There is no active cooldown.");
                    player.closeInventory();
                } else {
                    Practice.getInstance().getEventManager().setCooldown(0L);
                    player.sendMessage(ChatColor.RED + "Event cooldown was cancelled.");
                    player.closeInventory();
                }
            }
        });
        inventory.addItem(
            new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.PAPER, ChatColor.GREEN + "Event Status")) {
                @Override
                public void onClick(InventoryClickEvent e) {
                    Player player = (Player)e.getWhoClicked();
                    PracticeEvent event = EventManagerCommand.this.getEventByState(EventState.STARTED);
                    if (event == null) {
                        player.sendMessage(ChatColor.RED + "There is no active event.");
                        player.closeInventory();
                    } else {
                        String[] message = new String[]{
                            ChatColor.YELLOW + "Event: " + ChatColor.WHITE + event.getName(),
                            ChatColor.YELLOW + "Host: " + ChatColor.WHITE + (event.getHost() == null ? "Player Left" : event.getHost().getName()),
                            ChatColor.YELLOW + "Players: " + ChatColor.WHITE + event.getPlayers().size() + "/" + event.getLimit(),
                            ChatColor.YELLOW + "State: " + ChatColor.WHITE + event.getState().name()
                        };
                        player.sendMessage(message);
                        player.closeInventory();
                    }
                }
            }
        );
        return inventory;
    }

    private PracticeEvent getEventByState(EventState state) {
        for (PracticeEvent event : Practice.getInstance().getEventManager().getEvents().values()) {
            if (event.getState() == state && event.getHost() != null) {
                return event;
            }
        }

        return null;
    }
}
