package net.badlion.uhc.commands.handlers;

import net.badlion.disguise.managers.DisguiseManager;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.tinyprotocol.TinyProtocolReferences;
import net.badlion.gpermissions.GPermissions;
import net.badlion.ministats.MiniStats;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.UHCTeam;
import net.badlion.uhc.commands.VanishCommand;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.practice.PracticeManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class ModCommandHandler {

    public static void handleModCommand(final Player p, final String[] args) {
        if (args.length == 1) {
            UUID uuid = BadlionUHC.getInstance().getUUID(args[0]);
            if (uuid == null) {
                // Offline lookup
                BadlionUHC.getInstance().getServer().getScheduler().runTaskAsynchronously(BadlionUHC.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        final UUID uuid = Gberry.getOfflineUUID(args[0]);
                        if (uuid == null) {
                            p.sendMessage(ChatColor.RED + "Could not find offline player to mod.");
                            return;
                        }

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                ModCommandHandler.handleInternalModCommand(p, args, uuid);
                                p.sendMessage(ChatColor.GREEN + "Added " + args[0] + " as a mod (offline).");
                            }
                        }.runTask(BadlionUHC.getInstance());
                    }
                });
            } else {
                UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(uuid);
                Bukkit.getLogger().info(uhcPlayer.toString());
                Player mod = Bukkit.getPlayer(uuid);

                if (!handleInternalModCommand(p, args, uuid)) {
                    return;
                }

                if (uhcPlayer.getTeamRequest() != null) {
                    if (mod != null) {
                        mod.sendMessage(ChatColor.RED + "You must deny your team invite before you can be made a mod.");
                        p.sendMessage(ChatColor.RED + "This player must not have a team request to be made a mod.");
                    }

                    return;
                } else if (uhcPlayer.getTeam().getSize() > 1) {
                    // Remove from old team and make a new one for this player
                    uhcPlayer.getTeam().removePlayer(uhcPlayer.getUUID());
                    uhcPlayer.setTeam(new UHCTeam(uhcPlayer.getUUID()));
                }

                // Remove their stats if they had any
                if (BadlionUHC.getInstance().getState().ordinal() >= BadlionUHC.BadlionUHCState.STARTED.ordinal()) {
                    if ((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.STATS.name()).getValue()) {
                        if (uhcPlayer.getState().ordinal() < UHCPlayer.State.DEAD.ordinal()) {
                            MiniStats.getInstance().getPlayerDataListener().getPlayerDataMap().remove(uuid);
                        }
                    }
                }

                // Annoying edge case of where a mod is mod'd after they join and they are the only one that has joined that needs a team
                if (StartCommandHandler.teamNeedingPlayers != null
                            && StartCommandHandler.teamNeedingPlayers.getUuids().contains(uuid)
                            && StartCommandHandler.teamNeedingPlayers.getSize() == 1) {
                    StartCommandHandler.teamNeedingPlayers = null;
                }

                if (mod != null) {
	                // If they are in practice, remove them
	                if (PracticeManager.isInPractice(uhcPlayer)) {
		                PracticeManager.removePlayer(uhcPlayer, false);
	                }

                    UHCPlayerManager.updateUHCPlayerState(uuid, UHCPlayer.State.MOD);

                    World w = Bukkit.getWorld(BadlionUHC.UHCWORLD_NAME);
                    mod.setGameMode(GameMode.CREATIVE);
                    mod.spigot().setCollidesWithEntities(false);
                    mod.teleport(new Location(w, 0, w.getHighestBlockYAt(0, 0) + 10, 0));
                    mod.sendMessage(ChatColor.AQUA + "You have been promoted to moderator by the host!");
                    p.sendMessage(ChatColor.GREEN + "You have successfully modded " + ChatColor.YELLOW + args[0] + ChatColor.GREEN + "!");

                    BadlionUHC.getInstance().addMuteBanPerms(mod);

                    // Vanish stuff
                    VanishCommand.vanishPlayer(mod);
                    mod.setIgnoreXray(true);

                    // Fix possible bug if they logged in unmodded
                    try {
                        String name = p.getName();
                        if (name.length() > 12) {
                            name = name.substring(0, 11);
                        }

                        Object packet = TinyProtocolReferences.tabPacketClass.newInstance();
                        TinyProtocolReferences.tabPacketName.set(packet, name);
                        TinyProtocolReferences.tabPacketAction.set(packet, 0);
                        Gberry.protocol.sendPacket(p, packet);
                    } catch (IllegalAccessException e) {
                        System.out.println("[TabMain] Error sending packet to client");
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        System.out.println("[TabMain] Error sending packet to client");
                        e.printStackTrace();
                    }

	                if (mod.isDisguised()) {
		                DisguiseManager.undisguisePlayer(mod, false);

		                mod.sendFormattedMessage(ChatColor.RED + "You cannot be disguised when you are a mod or host, you have been undisguised!");
	                }
                }


                p.sendMessage(ChatColor.GREEN + "Added " + args[0] + " as a mod.");
            }
        } else {
            p.sendMessage("Usage: /uhc mod <player>");
        }
    }

    private static boolean handleInternalModCommand(Player p, String[] args, UUID modUUID) {
        // See if they can even be modded
        if (!p.isOp() && !GPermissions.plugin.userHasPermission(modUUID.toString(), "badlion.uhctrial")) {
            p.sendMessage(ChatColor.RED + "This user cannot be modded");
            return false;
        }

        UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(modUUID);
        if (uhcPlayer != null && uhcPlayer.getState() == UHCPlayer.State.MOD) {
            return false;
        }

        if (uhcPlayer == null) {
            UHCPlayerManager.addNewUHCPlayer(modUUID, args[0], UHCPlayer.State.MOD);
        } else {
            UHCPlayerManager.updateUHCPlayerState(modUUID, UHCPlayer.State.MOD);
        }

        return true;
    }

    public static void handleDemodCommand(Player p, String[] args) {
        if (args.length == 1) {
            UUID uuid = BadlionUHC.getInstance().getUUID(args[0]);
            if (uuid != null) {
                p.sendMessage(ChatColor.GREEN + "You have successfully demodded " + ChatColor.YELLOW + args[0] + ChatColor.GREEN + "!");

                if (BadlionUHC.getInstance().getState().ordinal() <= BadlionUHC.BadlionUHCState.COUNTDOWN.ordinal()) {
                    UHCPlayerManager.removeUHCPlayer(uuid);
                    Player demod = Bukkit.getPlayerExact(args[0]);
                    if (demod != null) {
                        demod.kickPlayer("You have been demodded, please connect again!");
                    }
                } else {
                    // Otherwise they are now forced to spectate
                    UHCPlayerManager.updateUHCPlayerState(uuid, UHCPlayer.State.SPEC);
                }
            } else {
                p.sendMessage(ChatColor.RED + "That player is not a moderator!");
            }
        } else {
            p.sendMessage("Usage: /uhc demod <player>");
        }
    }

}
