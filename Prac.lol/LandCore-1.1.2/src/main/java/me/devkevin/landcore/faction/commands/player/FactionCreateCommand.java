package me.devkevin.landcore.faction.commands.player;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.LandCoreAPI;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.faction.Faction;
import me.devkevin.landcore.faction.profile.FactionProfile;
import me.devkevin.landcore.utils.TaskUtil;
import me.devkevin.landcore.utils.message.CC;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 21/03/2023 @ 2:28
 * FactionCreateCommand / me.devkevin.landcore.faction.commands.player / LandCore
 */
public class FactionCreateCommand extends PlayerCommand {
    private final LandCore plugin;

    public FactionCreateCommand(LandCore plugin) {
        super("faction.create");
        this.plugin = plugin;
        setAliases("f create");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(CC.RED + "Usage: /faction create <name>");
            return;
        }

        FactionProfile factionProfile = this.plugin.getFactionManager().getProfile(player);

        if (factionProfile.getFaction() != null) {
            player.sendMessage(CC.RED + "You are already in a faction.");
            return;
        }

        String name = args[0];

        if (name.length() < 2) {
            player.sendMessage(CC.RED + "Faction names must be greater than or equal to 2 characters long.");
            return;
        }

        if (!StringUtils.isAlpha(name)) {
            player.sendMessage(CC.RED + "Faction names must only contain alpha characters (letters only).");
            return;
        }

        if (name.length() > 8) {
            player.sendMessage(CC.RED + "Faction names must be less than or equal to 8 characters long.");
            return;
        }

        Faction faction = this.plugin.getFactionManager().getFaction(name);

        if (faction != null) {
            player.sendMessage(CC.RED + "A faction with that name already exists!");
            return;
        }

        Faction createdFaction = new Faction(name, player.getUniqueId());
        createdFaction.getMembers().add(player.getUniqueId());
        factionProfile.setFaction(createdFaction);
        createdFaction.setDescription("This is the default description...");
        createdFaction.setDateCreated(LandCoreAPI.getTodayDate() + " " + LandCoreAPI.getCurrentTime());
        this.plugin.getFactionManager().getFactions().add(createdFaction);

        player.sendMessage(CC.GREEN + "You have successfully created a new faction!");

        TaskUtil.runAsync(plugin.getFactionManager()::save);
        this.plugin.getFactionManager().savePlayerFaction(player);
    }
}
