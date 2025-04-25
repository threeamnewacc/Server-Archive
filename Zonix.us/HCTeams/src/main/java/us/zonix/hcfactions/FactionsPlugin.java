package us.zonix.hcfactions;

import club.minemen.spigot.ClubSpigot;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.zonix.core.CorePlugin;
import us.zonix.core.board.BoardManager;
import us.zonix.hcfactions.blockoperation.BlockOperationModifier;
import us.zonix.hcfactions.blockoperation.BlockOperationModifierListeners;
import us.zonix.hcfactions.claimwall.ClaimWallListeners;
import us.zonix.hcfactions.combatlogger.CombatLogger;
import us.zonix.hcfactions.combatlogger.CombatLoggerListeners;
import us.zonix.hcfactions.combatlogger.commands.CombatLoggerCommand;
import us.zonix.hcfactions.crate.Crate;
import us.zonix.hcfactions.crate.CrateListeners;
import us.zonix.hcfactions.crate.command.CrateCommand;
import us.zonix.hcfactions.crowbar.CrowbarListeners;
import us.zonix.hcfactions.deathlookup.DeathLookupCommand;
import us.zonix.hcfactions.deathlookup.DeathLookupListeners;
import us.zonix.hcfactions.deathsign.DeathSignListeners;
import us.zonix.hcfactions.economysign.EconomySignListeners;
import us.zonix.hcfactions.elevator.ElevatorListeners;
import us.zonix.hcfactions.enchantmentlimiter.EnchantmentLimiterListeners;
import us.zonix.hcfactions.event.Event;
import us.zonix.hcfactions.event.EventManager;
import us.zonix.hcfactions.event.glowstone.GlowstoneEvent;
import us.zonix.hcfactions.event.glowstone.GlowstoneEventListeners;
import us.zonix.hcfactions.event.glowstone.command.GlowstoneForceCommand;
import us.zonix.hcfactions.event.glowstone.procedure.GlowstoneCreateProcedureListeners;
import us.zonix.hcfactions.event.glowstone.procedure.command.GlowstoneProcedureCommand;
import us.zonix.hcfactions.event.glowstone.procedure.command.GlowstoneRemoveCommand;
import us.zonix.hcfactions.event.koth.KothEvent;
import us.zonix.hcfactions.event.koth.KothEventListeners;
import us.zonix.hcfactions.event.koth.command.KothCommand;
import us.zonix.hcfactions.event.koth.command.KothScheduleCommand;
import us.zonix.hcfactions.event.koth.command.KothStartCommand;
import us.zonix.hcfactions.event.koth.command.KothStopCommand;
import us.zonix.hcfactions.event.koth.procedure.KothCreateProcedureListeners;
import us.zonix.hcfactions.event.koth.procedure.command.KothCreateProcedureCommand;
import us.zonix.hcfactions.event.koth.procedure.command.KothRemoveCommand;
import us.zonix.hcfactions.factions.Faction;
import us.zonix.hcfactions.factions.claims.ClaimListeners;
import us.zonix.hcfactions.factions.claims.ClaimPillar;
import us.zonix.hcfactions.factions.claims.CustomMovementHandler;
import us.zonix.hcfactions.factions.commands.*;
import us.zonix.hcfactions.factions.commands.admin.*;
import us.zonix.hcfactions.factions.commands.leader.FactionDemoteCommand;
import us.zonix.hcfactions.factions.commands.leader.FactionDisbandCommand;
import us.zonix.hcfactions.factions.commands.leader.FactionLeaderCommand;
import us.zonix.hcfactions.factions.commands.leader.FactionPromoteCommand;
import us.zonix.hcfactions.factions.commands.officer.*;
import us.zonix.hcfactions.factions.commands.system.FactionColorCommand;
import us.zonix.hcfactions.factions.commands.system.FactionCreateSystemCommand;
import us.zonix.hcfactions.factions.commands.system.FactionToggleDeathbanCommand;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.files.ConfigFile;
import us.zonix.hcfactions.inventory.command.CloneInventoryCommand;
import us.zonix.hcfactions.inventory.command.GiveInventoryCommand;
import us.zonix.hcfactions.inventory.command.LastInventoryCommand;
import us.zonix.hcfactions.itemdye.ItemDye;
import us.zonix.hcfactions.itemdye.ItemDyeListeners;
import us.zonix.hcfactions.kits.Kit;
import us.zonix.hcfactions.kits.KitListeners;
import us.zonix.hcfactions.kits.command.KitCommand;
import us.zonix.hcfactions.misc.commands.*;
import us.zonix.hcfactions.misc.commands.economy.AddBalanceCommand;
import us.zonix.hcfactions.misc.commands.economy.BalanceCommand;
import us.zonix.hcfactions.misc.commands.economy.PayCommand;
import us.zonix.hcfactions.misc.commands.economy.SetBalanceCommand;
import us.zonix.hcfactions.misc.listeners.*;
import us.zonix.hcfactions.mobstack.MobStack;
import us.zonix.hcfactions.mobstack.MobStackListeners;
import us.zonix.hcfactions.mode.Mode;
import us.zonix.hcfactions.mode.ModeListeners;
import us.zonix.hcfactions.mode.command.ModeCommand;
import us.zonix.hcfactions.potionlimiter.PotionLimiterListeners;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.ProfileAutoSaver;
import us.zonix.hcfactions.profile.ProfileListeners;
import us.zonix.hcfactions.profile.cooldown.ProfileCooldownListeners;
import us.zonix.hcfactions.profile.fight.command.KillStreakCommand;
import us.zonix.hcfactions.profile.kit.ProfileKitActionListeners;
import us.zonix.hcfactions.profile.kit.command.ProfileKitCommand;
import us.zonix.hcfactions.profile.options.command.ProfileOptionsCommand;
import us.zonix.hcfactions.profile.ore.ProfileOreCommand;
import us.zonix.hcfactions.profile.protection.ProfileProtection;
import us.zonix.hcfactions.profile.protection.command.ProfileProtectionCommand;
import us.zonix.hcfactions.statracker.StatTrackerListeners;
import us.zonix.hcfactions.subclaim.SubclaimListeners;
import us.zonix.hcfactions.util.FactionsBoardAdapter;
import us.zonix.hcfactions.util.command.CommandFramework;
import us.zonix.hcfactions.util.database.FactionsDatabase;
import us.zonix.hcfactions.util.player.PlayerUtility;
import us.zonix.hcfactions.util.player.SimpleOfflinePlayer;

import java.io.IOException;

@Getter
public class FactionsPlugin extends JavaPlugin {

    @Getter private static FactionsPlugin instance;

    private CommandFramework framework;
    private FactionsDatabase factionsDatabase;
    private ConfigFile mainConfig, scoreboardConfig, languageConfig, kothScheduleConfig;
    @Setter private boolean loaded;
    @Setter private boolean kitmapMode;

    public void onEnable() {
        instance = this;

        this.mainConfig = new ConfigFile(this, "config");
        this.languageConfig = new ConfigFile(this, "lang");
        this.scoreboardConfig = new ConfigFile(this, "scoreboard");
        this.kothScheduleConfig = new ConfigFile(this, "koth-schedule");
        this.factionsDatabase = new FactionsDatabase(this);
        this.kitmapMode = this.mainConfig.getBoolean("KITMAP_MODE");

        for (Player player : PlayerUtility.getOnlinePlayers() ) {
            new Profile(player.getUniqueId());
        }

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getType() == CombatLogger.ENTITY_TYPE) {
                    if (entity instanceof LivingEntity) {
                        if (entity.getCustomName() != null) {
                            entity.remove();
                        }
                    }
                }
            }
        }

        this.framework = new CommandFramework(this);

        SimpleOfflinePlayer.load(this);
        Faction.load();
        Mode.load();
        MobStack.stack();
        KothEvent.load();
        GlowstoneEvent.load();
        Crate.load();
        Kit.load();
        BlockOperationModifier.run();
        ProfileProtection.run(this);

        registerRecipes();
        registerListeners();
        registerCommands();

        PlayerFaction.runTasks();

        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new ProfileAutoSaver(this), 5000L, 5000L);

        ClubSpigot.INSTANCE.addMovementHandler(new CustomMovementHandler());

        //CorePlugin.getInstance().useTabList();
        //CorePlugin.getInstance().getTabListManager().getTabList().setHead(UUID.fromString("6b22037d-c043-4271-94f2-adb00368bf16"));

        CorePlugin.getInstance().setBoardManager(new BoardManager(new FactionsBoardAdapter(this)));
       // this.getServer().getScheduler().runTaskTimerAsynchronously(this, new TabListRunnable(), 20L, 20L);
    }

    public void onDisable() {
        Faction.save();

        for (Player player : PlayerUtility.getOnlinePlayers() ) {
            Profile profile = Profile.getByPlayer(player);

            if (profile.getClaimProfile() != null) {
                profile.getClaimProfile().removePillars();
            }

            for (ClaimPillar claimPillar : profile.getMapPillars()) {
                claimPillar.remove();
            }

            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }

        try {
            SimpleOfflinePlayer.save(this);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        for (Profile profile : Profile.getProfiles()) {
            profile.save();
        }

        for (Mode mode : Mode.getModes()) {
            mode.save();
        }

        for (CombatLogger logger : CombatLogger.getLoggers()) {
            logger.getEntity().remove();
        }

        for (Event event : EventManager.getInstance().getEvents()) {
            if (event instanceof KothEvent) {
                ((KothEvent) event).save();
            }
            else if (event instanceof GlowstoneEvent) {
                ((GlowstoneEvent) event).save();
            }
        }

        for (Crate crate : Crate.getCrates()) {
            crate.save();
        }

        for (Kit kit : Kit.getKits()) {
            kit.save();
        }

        MobStack.unStack();

        factionsDatabase.getClient().close();
    }

    private void registerRecipes() {
        for (Material material : Material.values()) {
            if (material.name().contains("CHESTPLATE") || material.name().contains("SWORD") || material.name().contains("LEGGINGS") || material.name().contains("BOOTS") || material.name().contains("HELMET") || material.name().contains("AXE") || material.name().contains("SPADE")) {
                for (ItemDye dye : ItemDye.values()) {
                    Bukkit.addRecipe(ItemDye.getRecipe(material, dye));
                }
            }
        }

        Bukkit.addRecipe(new ShapelessRecipe(new ItemStack(Material.EXP_BOTTLE)).addIngredient(1, Material.GLASS_BOTTLE));
    }

    private void registerCommands() {
        new ProfileProtectionCommand();
        new ProfileOreCommand();
        new CloneInventoryCommand();
        new LastInventoryCommand();
        new GiveInventoryCommand();
        new DeathLookupCommand();
        new ProfileKitCommand();
        new KillStreakCommand();
        new CombatLoggerCommand();

        new ModeCommand();

        new KothCommand();
        new KothScheduleCommand();
        new KothCreateProcedureCommand();
        new KothRemoveCommand();
        new KothStartCommand();
        new KothStopCommand();

        new GlowstoneProcedureCommand();
        new GlowstoneRemoveCommand();
        new GlowstoneForceCommand();

        new BalanceCommand();
        new ReclaimCommand();
        new ReclaimRemoveCommand();
        new StackCommand();
        new CobbleCommand();
        new PayCommand();
        new SetBalanceCommand();
        new AddBalanceCommand();
        new HelpCommand();
        new SpawnCommand();
        new ProfileOptionsCommand();
        new CrateCommand();
        new KitCommand();
        new MapKitCommand();
        new RenameCommand();
        new PlayTimeCommand();
        new TellLocationCommand();
        new FocusCommand();
        new SetGKitCommand();
        new MedicReviveCommand();

        new FactionHelpCommand();
        new FactionDisbandCommand();
        new FactionCreateCommand();
        new FactionDisbandAllCommand();
        new FactionInviteCommand();
        new FactionJoinCommand();
        new FactionRenameCommand();
        new FactionPromoteCommand();
        new FactionDemoteCommand();
        new FactionLeaderCommand();
        new FactionUninviteCommand();
        new FactionChatCommand();
        new FactionSetHomeCommand();
        new FactionMessageCommand();
        new FactionAnnouncementCommand();
        new FactionLeaveCommand();
        new FactionShowCommand();
        new FactionKickCommand();
        new FactionInvitesCommand();
        new FactionDepositCommand();
        new FactionWithdrawCommand();
        new FactionClaimCommand();
        new FactionMapCommand();
        new FactionUnclaimCommand();
        new FactionListCommand();
        new FactionHomeCommand();
        new FactionStuckCommand();
        new FactionCreateSystemCommand();
        new FactionToggleDeathbanCommand();
        new FactionColorCommand();
        new FactionFreezeCommand();
        new FactionThawCommand();
        new FactionSetDtrCommand();
        new FactionAdminCommand();

        if (this.mainConfig.getBoolean("FACTION_GENERAL.ALLIES.ENABLED")) {
            new FactionAllyCommand();
        }

        if (mainConfig.getBoolean("FACTION_GENERAL.ALLIES.ENABLED")) {
            new FactionEnemyCommand();
        }
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ProfileListeners(this), this);
        pluginManager.registerEvents(new MobStackListeners(), this);
        pluginManager.registerEvents(new CrowbarListeners(), this);
        pluginManager.registerEvents(new EconomySignListeners(), this);
        pluginManager.registerEvents(new DeathSignListeners(), this);
        pluginManager.registerEvents(new StatTrackerListeners(), this);
        pluginManager.registerEvents(new ProfileCooldownListeners(), this);
        pluginManager.registerEvents(new ProfileKitActionListeners(), this);
        pluginManager.registerEvents(new ClaimWallListeners(this), this);
        pluginManager.registerEvents(new EnchantmentLimiterListeners(), this);
        pluginManager.registerEvents(new PotionLimiterListeners(), this);
        pluginManager.registerEvents(new DeathLookupListeners(), this);
        pluginManager.registerEvents(new CombatLoggerListeners(this), this);
        pluginManager.registerEvents(new BlockOperationModifierListeners(), this);
        pluginManager.registerEvents(new KothCreateProcedureListeners(), this);
        pluginManager.registerEvents(new GlowstoneCreateProcedureListeners(), this);
        pluginManager.registerEvents(new KothEventListeners(), this);
        pluginManager.registerEvents(new GlowstoneEventListeners(), this);
        pluginManager.registerEvents(new ElevatorListeners(), this);
        pluginManager.registerEvents(new SubclaimListeners(), this);
        pluginManager.registerEvents(new CrateListeners(), this);
        pluginManager.registerEvents(new ItemDyeListeners(), this);
        pluginManager.registerEvents(new GlitchListeners(), this);
        pluginManager.registerEvents(new ModeListeners(), this);
        pluginManager.registerEvents(new ScoreboardListeners(), this);
        pluginManager.registerEvents(new ChatListeners(), this);
        pluginManager.registerEvents(new ClaimListeners(), this);
        pluginManager.registerEvents(new KitListeners(), this);
        pluginManager.registerEvents(new BorderListener(), this);
        pluginManager.registerEvents(new EnderpearlListener(this), this);
    }

}
