package me.devkevin.landcore.faction.commands.captain;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.faction.Faction;
import me.devkevin.landcore.faction.invite.FactionInvite;
import me.devkevin.landcore.faction.profile.FactionProfile;
import me.devkevin.landcore.utils.message.CC;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 21/03/2023 @ 13:53
 * FactionInviteCommand / me.devkevin.landcore.faction.commands.captain / LandCore
 */
public class FactionInviteCommand extends PlayerCommand {
    private final LandCore plugin;

    public FactionInviteCommand(LandCore plugin) {
        super("faction.invite");
        this.plugin = plugin;
        setAliases("f.invite");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(CC.RED + "Usage: /faction invite <player>");
            return;
        }

        FactionProfile factionProfile = this.plugin.getFactionManager().getProfile(player);
        if (factionProfile.getFaction() == null) {
            player.sendMessage(CC.RED + "You are not in a faction!");
            return;
        }

        Faction faction = factionProfile.getFaction();

        if (faction.getMembers().size() >= faction.getLimit()) {
            player.sendMessage(CC.RED + "Your faction has exceeded the max member limit, you are unable to invite no one currently!");
            return;
        }

        if (!faction.getCaptains().contains(player.getUniqueId()) && !faction.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are not a faction captain!");
            return;
        }

        Player target = this.plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(CC.RED + "Could not find player.");
            return;
        }

        if (target == player) {
            player.sendMessage(CC.RED + "Yoy may not invite yourself to a faction!");
            return;
        }

        if (faction.getMembers().contains(target.getUniqueId())) {
            player.sendMessage(target.getDisplayName() + CC.RED + " is already in your faction!");
            return;
        }

        FactionProfile targetProfile = this.plugin.getFactionManager().getProfile(target);
        if (targetProfile.getFaction() != null) {
            player.sendMessage(target.getDisplayName() + CC.RED + " already belongs to a faction. They must first leave their faction to be able to receive invites.");
            return;
        }

        FactionInvite factionInvite = targetProfile.getInviteList().stream().filter(ci -> ci.getFaction() == faction && System.currentTimeMillis() - ci.getTimestamp() <= 60000).findFirst().orElse(null);
        if (factionInvite != null) {
            player.sendMessage(target.getDisplayName() + CC.RED + " has already been invited to the faction within the last 60 seconds.");
            return;
        }
        targetProfile.getInviteList().add(new FactionInvite(faction));
        faction.broadcast(target.getDisplayName() + CC.YELLOW + " has been invited to the faction.");

        TextComponent textComponent = new TextComponent(CC.YELLOW + "Click ");
        TextComponent clickable = new TextComponent(CC.GOLD + "here");
        textComponent.addExtra(clickable);
        textComponent.addExtra(CC.YELLOW + " to accept the invite");
        clickable.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.GREEN + "Click to join " + target.getDisplayName() + CC.GREEN + "'s faction").create()));
        clickable.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faction join " + player.getName()));

        target.sendMessage(player.getDisplayName() + CC.YELLOW + " has invited you to join their faction " + CC.GRAY + "'" + CC.DARK_GREEN + faction.getName() + CC.GRAY + "'");
        target.spigot().sendMessage(textComponent);
    }
}
