package net.badlion.potpvp;

import net.badlion.cmdsigns.CmdSigns;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.tinyprotocol.TinyProtocolReferences;
import net.badlion.gspigot.ProtocolOutHook;
import net.badlion.gspigot.ProtocolScheduler;
import net.badlion.gguard.GGuard;
import net.badlion.gpermissions.GPermissions;
import net.badlion.potpvp.commands.AddArenaCommand;
import net.badlion.potpvp.commands.AddWarpCommand;
import net.badlion.potpvp.commands.DuelCommand;
import net.badlion.potpvp.commands.FakeVoteCommand;
import net.badlion.potpvp.commands.FollowCommand;
import net.badlion.potpvp.commands.ForceCommand;
import net.badlion.potpvp.commands.GiftMatchesCommand;
import net.badlion.potpvp.commands.InviteCommand;
import net.badlion.potpvp.commands.JoinEventCommand;
import net.badlion.potpvp.commands.LeaveCommand;
import net.badlion.potpvp.commands.PartyCommand;
import net.badlion.potpvp.commands.PromoteCommand;
import net.badlion.potpvp.commands.QuotaCommand;
import net.badlion.potpvp.commands.RankedCommand;
import net.badlion.potpvp.commands.RankedTeamCommand;
import net.badlion.potpvp.commands.RatingCommand;
import net.badlion.potpvp.commands.ResetEloCommand;
import net.badlion.potpvp.commands.ScanArenasCommand;
import net.badlion.potpvp.commands.SpawnCommand;
import net.badlion.potpvp.commands.SpectatorCommand;
import net.badlion.potpvp.commands.StateCommand;
import net.badlion.potpvp.commands.StatsCommand;
import net.badlion.potpvp.commands.TPWarpCommand;
import net.badlion.potpvp.commands.UnfollowCommand;
import net.badlion.potpvp.commands.UnrankedCommand;
import net.badlion.potpvp.commands.ViewOpponentInventoryCommand;
import net.badlion.potpvp.ffaworlds.FFAWorld;
import net.badlion.potpvp.helpers.DuelHelper;
import net.badlion.potpvp.helpers.ItemStackHelper;
import net.badlion.potpvp.helpers.KitCreationHelper;
import net.badlion.potpvp.helpers.KitHelper;
import net.badlion.potpvp.helpers.LobbyItemHelper;
import net.badlion.potpvp.helpers.MatchmakingHelper;
import net.badlion.potpvp.helpers.PartyHelper;
import net.badlion.potpvp.helpers.PlayerHelper;
import net.badlion.potpvp.helpers.RatingSignsHelper;
import net.badlion.potpvp.helpers.SpectatorHelper;
import net.badlion.potpvp.inventories.duel.DuelRequestChooseCustomKitInventory;
import net.badlion.potpvp.inventories.duel.DuelRequestInventory;
import net.badlion.potpvp.inventories.duel.RedRoverChooseFighterInventory;
import net.badlion.potpvp.inventories.kitcreation.CustomKitCreationInventories;
import net.badlion.potpvp.inventories.lobby.EventsInventory;
import net.badlion.potpvp.inventories.lobby.FFAInventory;
import net.badlion.potpvp.inventories.lobby.Ranked1v1Inventory;
import net.badlion.potpvp.inventories.lobby.SettingsInventory;
import net.badlion.potpvp.inventories.lobby.TDMInventory;
import net.badlion.potpvp.inventories.party.PartyEventsInventory;
import net.badlion.potpvp.inventories.party.PartyFFAChooseKitInventory;
import net.badlion.potpvp.inventories.party.PartyFightChooseKitInventory;
import net.badlion.potpvp.inventories.party.PartyListInventory;
import net.badlion.potpvp.inventories.party.PartyRequestInventory;
import net.badlion.potpvp.inventories.party.Ranked2v2Inventory;
import net.badlion.potpvp.inventories.party.Ranked3v3Inventory;
import net.badlion.potpvp.inventories.party.Ranked5v5Inventory;
import net.badlion.potpvp.inventories.party.RedRoverChooseKitInventory;
import net.badlion.potpvp.inventories.spectator.SpectateEventInventory;
import net.badlion.potpvp.inventories.spectator.SpectateFFAInventory;
import net.badlion.potpvp.inventories.spectator.SpectateTDMInventory;
import net.badlion.potpvp.inventories.tdm.TDMVoteInventory;
import net.badlion.potpvp.listeners.GlobalListener;
import net.badlion.potpvp.listeners.PartyDeadListener;
import net.badlion.potpvp.listeners.SpectatorListener;
import net.badlion.potpvp.listeners.StateListener;
import net.badlion.potpvp.listeners.VoteListener;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.managers.DonatorManager;
import net.badlion.potpvp.managers.EnderPearlManager;
import net.badlion.potpvp.managers.EventManager;
import net.badlion.potpvp.managers.FFAManager;
import net.badlion.potpvp.managers.MatchMakingManager;
import net.badlion.potpvp.managers.MessageManager;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.potpvp.managers.RankedLeftManager;
import net.badlion.potpvp.managers.RatingManager;
import net.badlion.potpvp.managers.RespawnManager;
import net.badlion.potpvp.managers.StasisManager;
import net.badlion.potpvp.managers.TDMManager;
import net.badlion.potpvp.managers.VoteManager;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.potpvp.states.matchmaking.GameState;
import net.badlion.potpvp.tdm.TDMGame;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.smellymapvotes.SmellyMapVotes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PotPvP extends JavaPlugin {

    private static PotPvP plugin;
    private static CmdSigns cmdSignsPlugin;
    private static GGuard gGuardPlugin;

    public static GroupStateMachine stateMachine = new GroupStateMachine(); // NEED TO ASSIGN TO STATIC VARIABLE B/C GroupStateMachine.getInstance()
    private static Map<Player, Group> playerToGroupMap = new ConcurrentHashMap<>();
    private static Map<UUID, String> uuidToUsername = new HashMap<>();

    private Location spawnLocation;
    private Location kitCreationLocation;
    private Location defaultRespawnLocation;
    private static boolean allowRankedMatches = true;
    public static boolean restarting = false;

    private boolean tournamentMode;

	private Map<Integer, ChatColor> customArmorPlayers = new HashMap<>();

    private static String unlimitedRankedPermission = "PVPServer.unlimitedRanked";

    private String dbExtra = "";

	// Tiny Protocol stuff
	private Map<Player, ConcurrentLinkedQueue<Location>> blockedBlockChangeLocations = new ConcurrentHashMap<>();
	private Map<Player, ConcurrentLinkedQueue<Location>> blockedPlayerSignUpdates = new ConcurrentHashMap<>();
	private Map<Player, Map<Location, String[]>> queuedPlayerSignUpdates = new ConcurrentHashMap<>();

    public static Set<Object> validPackets = new HashSet<>();

    public PotPvP() {
        PotPvP.plugin = this;
    }

	@Override
    public void onEnable() {
        if (this.getServer().getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
            this.dbExtra = "_v19";
        }

        Gberry.enableAsyncDelayedLoginEvent = true;

        this.saveDefaultConfig();

        this.tournamentMode = this.getConfig().getBoolean("potpvp.tournament-mode", false);

        this.getServer().getWorld("world").setAutoSave(this.getConfig().getBoolean("potpvp.save-world"));

	    // For debugging
		//Gberry.loggingTags.add("RATINGSIGNS");
		//Gberry.loggingTags.add("PACKET");
		//Gberry.loggingTags.add("GROUP");
		//Gberry.loggingTags.add("DUEL");
	    //Gberry.loggingTags.add("RATING");
	    //Gberry.loggingTags.add("SM");
        //Gberry.loggingTags.add("ARENAS");
        //Gberry.loggingTags.add("ARENA");
        //Gberry.loggingTags.add("KIT2");
        //Gberry.loggingTags.add("EVENT");
        //Gberry.loggingTags.add("EVENT2");
        //Gberry.loggingTags.add("MATCH");
        //Gberry.loggingTags.add("MATCH2");
        //Gberry.loggingTags.add("LMS");
        //Gberry.loggingTags.add("PARTY");
        //Gberry.loggingTags.add("KIT");
        //Gberry.loggingTags.add("SLAUGHTER");
        //Gberry.loggingTags.add("INTERACT");
        //Gberry.loggingTags.add("INV");
        //Gberry.loggingTags.add("FFA");
        //Gberry.loggingTags.add("TDM");
        //Gberry.loggingTags.add("SPEC");
        //Gberry.loggingTags.add("LAG");
        Gberry.loggingTags.add("BUG");

		// Setup map voting
		SmellyMapVotes.getInstance().setServerType(net.badlion.smellymapvotes.VoteManager.ServerType.ARENAPVP);

		PotPvP.cmdSignsPlugin = (CmdSigns) this.getServer().getPluginManager().getPlugin("CmdSigns");
        PotPvP.gGuardPlugin = (GGuard) this.getServer().getPluginManager().getPlugin("GGuard");

	    // Initialize locations
	    this.spawnLocation = new Location(Bukkit.getWorld("world"), -3.5, 75.5, -6.5, 90, 0);
	    this.kitCreationLocation = new Location(Bukkit.getWorld("world"), 489.5, 77, -5.5, 0, 0);
        this.defaultRespawnLocation = this.spawnLocation;

        // Initialize command executors
        this.getCommand("addarena").setExecutor(new AddArenaCommand());
        this.getCommand("addwarp").setExecutor(new AddWarpCommand());
	    this.getCommand("duel").setExecutor(new DuelCommand());
	    this.getCommand("fakevote").setExecutor(new FakeVoteCommand());
        this.getCommand("follow").setExecutor(new FollowCommand());
        this.getCommand("force").setExecutor(new ForceCommand());
        this.getCommand("giftmatches").setExecutor(new GiftMatchesCommand());
		this.getCommand("invite").setExecutor(new InviteCommand());
	    this.getCommand("joinevent").setExecutor(new JoinEventCommand());
	    this.getCommand("leave").setExecutor(new LeaveCommand());
	    this.getCommand("openoppinv").setExecutor(new ViewOpponentInventoryCommand());
	    this.getCommand("party").setExecutor(new PartyCommand());
	    this.getCommand("promote").setExecutor(new PromoteCommand());
        this.getCommand("quota").setExecutor(new QuotaCommand());
	    this.getCommand("ranked").setExecutor(new RankedCommand());
	    this.getCommand("rankedteam").setExecutor(new RankedTeamCommand());
        this.getCommand("resetelo").setExecutor(new ResetEloCommand());
        this.getCommand("rating").setExecutor(new RatingCommand());
        this.getCommand("scanarenas").setExecutor(new ScanArenasCommand());
	    this.getCommand("spawn").setExecutor(new SpawnCommand());
	    this.getCommand("spectate").setExecutor(new SpectatorCommand());
        this.getCommand("state").setExecutor(new StateCommand());
        this.getCommand("stats").setExecutor(new StatsCommand());
		this.getCommand("tpwarp").setExecutor(new TPWarpCommand());
        this.getCommand("unfollow").setExecutor(new UnfollowCommand());
	    this.getCommand("unranked").setExecutor(new UnrankedCommand());

		class RRDuelCommand implements CommandExecutor {
			@Override
			public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
				sender.sendMessage(ChatColor.RED + "Please use /duel and click on the Emerald block!");
				return true;
			}
		}
		this.getCommand("rrduel").setExecutor(new RRDuelCommand());

	    // Initialize helpers
        LobbyItemHelper.initialize();
        DuelHelper.initialize();
        ItemStackHelper.initialize();
        KitCreationHelper.initialize();
        new KitHelper(); // Has to be after LobbyItemHelper
        MatchmakingHelper.initialize();
        PartyHelper.initialize();
		PartyRequestInventory.initialize();
        new RatingSignsHelper();
        RatingSignsHelper.initialize();
        SpectatorHelper.initialize();

		// Initialize inventories
		SmellyInventory.initialize(this, true); // Always initialize first
		CustomKitCreationInventories.initialize();
		//DuelChooseKitInventory.initialize(); // Initialized from KitHelper
		DuelRequestInventory.initialize();
		DuelRequestChooseCustomKitInventory.initialize();
		EventsInventory.initialize();
		FFAInventory.initialize();
		//KitCreationKitSelectionInventory.initialize(); // Initialized from KitHelper
        PartyEventsInventory.initialize();
        PartyFightChooseKitInventory.initialize();
        PartyFFAChooseKitInventory.initialize();
        PartyListInventory.initialize();
		//PartyPlayerInventoriesInventory.initialize();
		PartyRequestInventory.initialize();
		Ranked1v1Inventory.initialize();
		Ranked2v2Inventory.initialize();
        Ranked3v3Inventory.initialize();
        Ranked5v5Inventory.initialize();
		RedRoverChooseKitInventory.initialize();
		RedRoverChooseFighterInventory.initialize();
		SettingsInventory.initialize();
		SpectateEventInventory.initialize();
        SpectateFFAInventory.initialize();
        SpectateTDMInventory.initialize();
		TDMInventory.initialize();
		TDMVoteInventory.initialize();
		//Unranked1v1Inventory.initialize(); // Initialized from KitHelper

	    // Initialize managers
	    ArenaManager.initialize();
        new DonatorManager();
	    new EventManager();
        new FFAManager();
	    new MatchMakingManager();
        new PotPvPPlayerManager();
        new RankedLeftManager();
        new RatingManager();
        new RespawnManager();
        new TDMManager();
        StasisManager.initialize();
        VoteManager.initialize();

	    // Initialize listeners
	    new GlobalListener();
	    new MessageManager();
        new PartyDeadListener();
	    new SpectatorListener();
	    new StateListener();
        new VoteListener();
		this.getServer().getPluginManager().registerEvents(new ResetEloCommand(), this); // Had to implement Bukkit Listener

	    // Register state listeners
        this.getServer().getPluginManager().registerEvents(GroupStateMachine.duelRequestState, this);
        this.getServer().getPluginManager().registerEvents(GroupStateMachine.ffaState, this);
        this.getServer().getPluginManager().registerEvents(GroupStateMachine.followState, this);
        this.getServer().getPluginManager().registerEvents(GroupStateMachine.kitCreationState, this);
        this.getServer().getPluginManager().registerEvents(GroupStateMachine.lmsState, this);
        this.getServer().getPluginManager().registerEvents(GroupStateMachine.slaughterState, this);
        this.getServer().getPluginManager().registerEvents(GroupStateMachine.kothState, this);
        this.getServer().getPluginManager().registerEvents(GroupStateMachine.lobbyState, this);
	    this.getServer().getPluginManager().registerEvents(GroupStateMachine.matchMakingState, this);
        this.getServer().getPluginManager().registerEvents(GroupStateMachine.partyState, this);
        this.getServer().getPluginManager().registerEvents(GroupStateMachine.partyRequestState, this);
	    //this.getServer().getPluginManager().registerEvents(GroupStateMachine.rankedMatchState, this);
	    this.getServer().getPluginManager().registerEvents(GroupStateMachine.regularMatchState, this);
        this.getServer().getPluginManager().registerEvents(GroupStateMachine.spectatorState, this);
        this.getServer().getPluginManager().registerEvents(GroupStateMachine.tdmState, this);
        this.getServer().getPluginManager().registerEvents(GroupStateMachine.uhcMeetupState, this);

        // Initialize FFA Worlds
        for (FFAWorld ffaWorld : FFAWorld.getFfaWorlds().values()) {
            ffaWorld.startGame();
        }

		// Initialize TDMs
        if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_7) {
            new TDMGame(2, 30, KitRuleSet.tdmRuleSet);

            // Force update on TDM inventory
            TDMInventory.updateTDMInventory(true);
        }

        // Perma day
		World world = PotPvP.getInstance().getServer().getWorld("world");
		world.setTime(6000L);
		world.setGameRuleValue("doDaylightCycle", "false");

		// Disable cosmetics
		Cosmetics.getInstance().disallowCosmetics();
		Cosmetics.getInstance().setCosmeticEnabled(Cosmetics.CosmeticType.ARROW_TRAIL, true); // Just ArrowTrails for now

		// Register the Tiny Protocol packet listener
        ProtocolScheduler.addHook(new ProtocolOutHook() {
            @Override
            public Object handlePacket(Player receiver, Object packet) {
                if (PotPvP.this.tournamentMode && TinyProtocolReferences.packetEntityEquipmentClass.isInstance(packet)) {
                    // Is this player a spectator?
                    Group group = PotPvP.getInstance().getPlayerGroup(receiver);
                    if (GroupStateMachine.spectatorState.contains(group)) {
                        int entityId = TinyProtocolReferences.packetEntityEquipmentEntityID.get(packet);

                        // Is this player in a tournament match?
                        ChatColor color = PotPvP.this.customArmorPlayers.get(entityId);
                        if (color != null) {
	                        ItemStack item = new ItemStack(Material.LEATHER_HELMET);//null;

	                        int slot = TinyProtocolReferences.getPacketEntityEquipmentSlot(packet);

	                        // Check to make sure this is a helmet item
                            if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
                                if (slot != 5) return packet;
                            } else {
                                if (slot != 4) return packet;
                            }

                            /*switch (slot) {
                                case 4:
                                    item = new ItemStack(Material.LEATHER_HELMET);
                                    break;
                                case 3:
                                    item = new ItemStack(Material.LEATHER_CHESTPLATE);
                                    break;
                                case 2:
                                    item = new ItemStack(Material.LEATHER_LEGGINGS);
                                    break;
                                case 1:
                                    item = new ItemStack(Material.LEATHER_BOOTS);
                                    break;
                            }

                            // Was this an armor item?
                            if (item == null) return packet;*/

                            // Color item
                            LeatherArmorMeta itemMeta = ((LeatherArmorMeta) item.getItemMeta());
                            itemMeta.setColor(Gberry.getColorFromChatColor(color));
                            item.setItemMeta(itemMeta);

	                        // Change item in the packet
	                        try {
		                        // Clone packet because MC sends literally the same packet to literally everyone like literally
		                        Object newPacket = TinyProtocolReferences.packetEntityEquipmentClass.newInstance();

		                        TinyProtocolReferences.packetEntityEquipmentEntityID.set(newPacket, entityId);

		                        // 1.9 slot is offset by 1
                                TinyProtocolReferences.setPacketEntityEquipmentSlot(newPacket, slot);

		                        TinyProtocolReferences.packetEntityEquipmentItem.set(newPacket, TinyProtocolReferences.getItemStackNMSCopy.invoke(null, item));

		                        return newPacket;
	                        } catch (Exception e) {
		                        e.printStackTrace();
	                        }
                        }
                    }
                } else if (TinyProtocolReferences.packetBlockChangeClass.isInstance(packet)) { // TODO: WE MIGHT NEED TO UPDATE BLOCK CHANGES TO THE NEW SYSTEM TOO?
                    ConcurrentLinkedQueue<Location> locations = PotPvP.this.blockedBlockChangeLocations.get(receiver);
                    if (locations != null) {
                        // Check if locations match
                        for (Location loc : locations) {
                            if (loc.getBlockX() == TinyProtocolReferences.getPacketBlockChangeCoord(packet, 'x')
                                        && loc.getBlockY() == TinyProtocolReferences.getPacketBlockChangeCoord(packet, 'y')
                                        && loc.getBlockZ() == TinyProtocolReferences.getPacketBlockChangeCoord(packet, 'z')) {
                                return null;
                            }
                        }
                    }
                } else if (TinyProtocolReferences.packetUpdateSignClass.isInstance(packet) && TinyProtocolReferences.isSignUpdate(packet)) {
                    // Check to see if sign update is blocked
                    ConcurrentLinkedQueue<Location> blockedSignUpdates = PotPvP.this.blockedPlayerSignUpdates.get(receiver);
                    Map<Location, String[]> queuedSignUpdates = PotPvP.getInstance().getQueuedPlayerSignUpdates().get(receiver);
                    if (blockedSignUpdates != null && queuedSignUpdates != null) {
                        // Check if locations match
                        for (Location loc : blockedSignUpdates) {
                            if (loc.getX() == TinyProtocolReferences.getPacketUpdateSignCoord(packet, 'x')
                                        && loc.getY() == TinyProtocolReferences.getPacketUpdateSignCoord(packet, 'y')
                                        && loc.getZ() == TinyProtocolReferences.getPacketUpdateSignCoord(packet, 'z')) {
                                if (queuedSignUpdates.containsKey(loc)) {
                                    String[] newLines = queuedSignUpdates.remove(loc);

                                    // Sanitize
                                    int i = 0;
                                    for (String s : newLines) {
                                        if (s == null) {
	                                        s = "";
                                            newLines[i] = "";
                                        }

                                        if (s.length() > 15) {
                                            newLines[i] = s.substring(0, 15);
                                        }

                                        i++;
                                    }

                                    TinyProtocolReferences.setPacketUpdateSignLines(packet, newLines);

                                    // Stop
                                    return packet;
                                }
                            }
                        }
                    }
                } else if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9 && TinyProtocolReferences.packetMapChunk.isInstance(packet)) {
                    // Check to see if sign update is blocked
                    ConcurrentLinkedQueue<Location> blockedSignUpdates = PotPvP.this.blockedPlayerSignUpdates.get(receiver);
                    Map<Location, String[]> queuedSignUpdates = PotPvP.getInstance().getQueuedPlayerSignUpdates().get(receiver);
                    if (blockedSignUpdates != null && queuedSignUpdates != null) {
                        Iterator<Location> it = blockedSignUpdates.iterator();

                        // Check if locations match
                        tag: while (it.hasNext()) {
                            Location loc = it.next();
                            List<Object> tileEntityNBTTagCompounds = TinyProtocolReferences.packetMapChunkTileEntities.get(packet);
                            for (Object nbtTagCompound : tileEntityNBTTagCompounds) {
                                String id = (String) TinyProtocolReferences.getNbtTagCompoundGetString.invoke(nbtTagCompound, "id");
                                if (id.equals("Sign")) {
                                    int x = (int) TinyProtocolReferences.getNbtTagCompoundGetInt.invoke(nbtTagCompound, "x");
                                    int y = (int) TinyProtocolReferences.getNbtTagCompoundGetInt.invoke(nbtTagCompound, "y");
                                    int z = (int) TinyProtocolReferences.getNbtTagCompoundGetInt.invoke(nbtTagCompound, "z");
                                    if (loc.getX() == x && loc.getY() == y && loc.getZ() == z) {
                                        if (queuedSignUpdates.containsKey(loc)) {
                                            String[] newLines = queuedSignUpdates.remove(loc);
                                            it.remove();

                                            // Sanitize
                                            int i = 0;
                                            for (String s : newLines) {
                                                if (s == null) {
                                                    s = "";
                                                    newLines[i] = "";
                                                }

                                                if (s.length() > 15) {
                                                    newLines[i] = s.substring(0, 15);
                                                }

                                                i++;
                                            }

                                            // Sanitize the String's into IChatBaseComponent's
                                            Object[] chatComponents = (Object []) TinyProtocolReferences.sanitizeLines.invoke(null, (Object) newLines);

                                            // Emulate the TileEntity.save() method
                                            for (i = 0; i < 4; ++i) {
                                                Object s = TinyProtocolReferences.iChatComponentBaseChatSerializerToJson.invoke(null, chatComponents[i]);
                                                TinyProtocolReferences.nbtTagCompoundSetString.invoke(nbtTagCompound, "Text" + (i + 1), s);
                                            }

                                            // Next tag
                                            continue tag;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                return packet;
            }

            @Override
            public ProtocolPriority getPriority() {
                return ProtocolPriority.MEDIUM;
            }
        });
    }

    @Override
    public void onDisable() {

    }

	public void givePlayerDuelStateItems(Player player) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);

		player.updateInventory();
	}

	public void givePlayerPartyDeadStateItems(Player player) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);

		player.getInventory().setItem(8, PartyHelper.getLeavePartyItem());

		player.getInventory().setHeldItemSlot(0);

		player.updateInventory();
	}

    public Location getDefaultRespawnLocation() {
        return defaultRespawnLocation;
    }

    public static PotPvP getInstance() {
        return PotPvP.plugin;
    }

    public CmdSigns getCmdSignsPlugin() { return PotPvP.cmdSignsPlugin; }

    public GGuard getgGuardPlugin() {
        return gGuardPlugin;
    }

    public boolean isAllowRankedMatches() { return PotPvP.allowRankedMatches; }

    public static String getUnlimitedRankedPermission() {
        return PotPvP.unlimitedRankedPermission;
    }

    public void setAllowRankedMatches(boolean allowRankedMatches) {
        PotPvP.allowRankedMatches = allowRankedMatches;
    }

    public Group getPlayerGroup(Entity entity) {
        if (!(entity instanceof Player)) {
            throw new RuntimeException("Non-Player object passed as entity to get group");
        }

        return this.getPlayerGroup((Player) entity);
    }

    public Group getPlayerGroup(Player player) {
        Group group = PotPvP.playerToGroupMap.get(player);
        if (group == null) {
            GameState.debugGameMappings(PotPvP.playerToGroupMap);

            player.sendMessage("You have glitched out of the PotPvP system. You have been removed to prevent corruption.");
            player.kickPlayer("Report to an admin if you can reproduce how to end up glitched.");
	        PotPvPPlayerManager.getPotPvPPlayer(player.getUniqueId()).printDebug();
	        throw new RuntimeException("Null group requested for " + player.getName());
        }

        return group;
    }

    public void debugGames() {
        GameState.debugGameMappings(PotPvP.playerToGroupMap);
    }

    public void handlePlayerLeaveGroup(Player player, Group group) {
        if (group.isParty()) {
            group.getParty().removePlayer(player);

            // Last person just left
            if (group.players().size() == 0) {
	            // Remove party listing item
	            PartyListInventory.removePartyListing(group);

                Gberry.log("GROUP", "Cleaning party group up with player " + player.getName());
                GroupStateMachine.getInstance().cleanupElement(group);
            } else {
	            // Update party listing item for the new set of players
	            PartyListInventory.updatePartyListing(group);
            }
        } else {
            Gberry.log("GROUP", "Cleaning group up with player " + player.getName());
            GroupStateMachine.getInstance().cleanupElement(group);
        }
    }

    public void updatePlayerGroup(Player player, Group group) {
        if (group == null) {
            throw new IllegalArgumentException("group cannot be null");
        }

        Group oldGroup = PotPvP.playerToGroupMap.get(player);
        if (oldGroup != null) {
	        // Debug stuff
	        if (Gberry.loggingTags.contains("SM")) {
		        List<String> lines = GroupStateMachine.getInstance().debugTransitionsForElement(oldGroup);
		        for (String line : lines) {
			        Gberry.log("SM", line);
		        }
	        }

	        // Cleanup group stuff if needed
	        //try {
		        this.handlePlayerLeaveGroup(player, oldGroup);
	        //} catch (RuntimeException e) {
		        // Another player was in the same group and this method has already
		        // been called and the old group has already been cleaned up
	        //}
        }

        PotPvP.playerToGroupMap.put(player, group);
    }

    public Group removePlayerGroup(Player player) {
        return PotPvP.playerToGroupMap.remove(player);
    }

    // PLEASE NOTE THIS IS INTENDED AS A MEMORY LEAK
    public String getUsernameFromUUID(UUID uuid) {
        return PotPvP.uuidToUsername.get(uuid);
    }

    public void addUUIDToUsername(UUID uuid, String username) {
        PotPvP.uuidToUsername.put(uuid, username);
    }

    public void sendMessageToAllGroups(String msg, Group... groups) {
        for (Group group : groups) {
            for (Player p : group.players()) {
                p.sendMessage(msg);
            }
        }
    }

	public void somethingBroke(CommandSender sender, Group ...groups) {
        sender.sendMessage(ChatColor.RED + "Something broke, contact an administrator.");

		for (Group group : groups) {
			for (String line : GroupStateMachine.getInstance().debugTransitionsForElement(group)) {
                Bukkit.getLogger().info(line);
            }

            for (Player pl : group.players()) {
                pl.kickPlayer("Something broke, contact an administrator if you can reproduce this error");
            }
		}
	}

    public void healAndTeleportToSpawn(Player player) {
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.teleport(this.spawnLocation);
        player.setFlying(false);
        player.spigot().setCollidesWithEntities(true);
        player.setGameMode(GameMode.SURVIVAL);

	    // Douse the player
	    player.setFireTicks(0);

	    // Remove arrows
	    PlayerHelper.removeArrows(player);

        // Clear all buffs on them too...not sure when we wouldn't want to do this
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        // No pearl glitches
        EnderPearlManager.remove(player);
    }

    public void addMuteBanPerms(Player player) {
	    if (player.hasPermission("badlion.kitmod")) {
		    GPermissions.giveModPermissions(player);
	    } else if (player.hasPermission("badlion.kittrial")) {
		    GPermissions.giveTrialPermissions(player);
	    }
    }

	public static void sendBlockChange(Player player, Location location, Material material) {
		PotPvP.sendBlockChange(player, location, material, (byte) 0);
	}

	public static void sendBlockChange(final Player player, final Location location, Material material, byte data) {
		final ConcurrentLinkedQueue<Location> locations = PotPvP.getInstance().getBlockedBlockChangeLocations().get(player);

		// Remove location if we're currently blocking packets
		if (locations != null) {
			locations.remove(location);
		}

		player.sendBlockChange(location, material, data);

		// Do this in one tick to let the packet above go through
		BukkitUtil.runTaskNextTick(new Runnable() {
            @Override
            public void run() {
                if (Gberry.isPlayerOnline(player)) {
                    if (locations == null) {
                        ConcurrentLinkedQueue<Location> locations2 = new ConcurrentLinkedQueue<>();

                        locations2.add(location);

                        PotPvP.getInstance().getBlockedBlockChangeLocations().put(player, locations2);
                    } else {
                        locations.add(location);
                    }
                }
            }
        });
	}

	public static void sendSignChange(final Player player, final Location location, final String[] lines) {
		ConcurrentLinkedQueue<Location> locations = PotPvP.getInstance().getBlockedPlayerSignUpdates().get(player);

		if (locations == null) {
			locations = new ConcurrentLinkedQueue<>();
			PotPvP.getInstance().getBlockedPlayerSignUpdates().put(player, locations);
		}

		// Add location if not already in the list
		if (!locations.contains(location)) {
			locations.add(location);
			//Gberry.log("PACKET", "Adding location " + location.toString());
		}

		//for (String s : lines) {
		//    Gberry.log("PACKET", "Adding line " + s);
		//}

		Map<Location, String[]> queuedSignUpdates = PotPvP.getInstance().getQueuedPlayerSignUpdates().get(player);
		if (queuedSignUpdates == null) {
			queuedSignUpdates = new ConcurrentHashMap<>();
			PotPvP.getInstance().getQueuedPlayerSignUpdates().put(player, queuedSignUpdates);
		}

		queuedSignUpdates.put(location, lines);

		//for (Map.Entry<Location, String[]> keyValue : queuedSignUpdates.entrySet()) {
		//    Gberry.log("PACKET", "Location " + keyValue.getKey());
		//    for (String s : keyValue.getValue()) {
		//        Gberry.log("PACKET", "LINE2 " + s);
		//    }
		//}
	}

    public static void printLagDebug(String msg) {
        Gberry.log("LAG", GroupStateMachine.spectatorState.getElements().size() + " - " + msg);
    }

	public Location getSpawnLocation() {
		return spawnLocation;
	}

	public Location getKitCreationLocation() {
		return kitCreationLocation;
	}

	public Map<Player, ConcurrentLinkedQueue<Location>> getBlockedBlockChangeLocations() {
		return blockedBlockChangeLocations;
	}

	public Map<Player, ConcurrentLinkedQueue<Location>> getBlockedPlayerSignUpdates() {
		return blockedPlayerSignUpdates;
	}

	public Map<Player, Map<Location, String[]>> getQueuedPlayerSignUpdates() {
		return queuedPlayerSignUpdates;
	}

    public boolean isTournamentMode() {
        return tournamentMode;
    }

	public Map<Integer, ChatColor> getCustomArmorPlayers() {
		return customArmorPlayers;
	}

    public String getDBExtra() {
        return dbExtra;
    }
}
