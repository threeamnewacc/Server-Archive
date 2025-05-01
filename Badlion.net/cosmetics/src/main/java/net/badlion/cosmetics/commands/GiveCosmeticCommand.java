package net.badlion.cosmetics.commands;

import net.badlion.common.libraries.StringCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.managers.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GiveCosmeticCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, final String[] args) {
        if (args.length == 3) {
            Player player = Cosmetics.getInstance().getServer().getPlayerExact(args[0]);

            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Player is not online!");
                return true;
            }

            Cosmetics.CosmeticType cosmeticType = Cosmetics.CosmeticType.valueOf(args[1].toUpperCase());

            if (cosmeticType == Cosmetics.CosmeticType.PET) {
                PetManager.addPet(sender, player, args[2].toLowerCase());
            } else if (cosmeticType == Cosmetics.CosmeticType.MORPH) {
                MorphManager.addMorph(sender, player, args[2].toLowerCase());
            } else if (cosmeticType == Cosmetics.CosmeticType.PARTICLE) {
                ParticleManager.addParticle(sender, player, args[2].toLowerCase());
            } else if (cosmeticType == Cosmetics.CosmeticType.GADGET) {
                GadgetManager.addGadget(sender, player, args[2].toLowerCase());
            } else if (cosmeticType == Cosmetics.CosmeticType.ARROW_TRAIL) {
                ArrowTrailManager.addArrowTrail(sender, player, args[2].toLowerCase());
            } else if (cosmeticType == Cosmetics.CosmeticType.ROD_TRAIL) {
                RodTrailManager.addRodTrail(sender, player, args[2].toLowerCase());
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid cosmetic.");
                return true;
            }

            // Send message to command sender
            sender.sendMessage(ChatColor.GREEN + "Gave cosmetic " + args[2].toLowerCase() + " (" + args[1].toUpperCase() + ") to " + args[0]);
            return true;
        } else if (args.length == 4 && args[0].equals("uuid")) {
            final UUID uuid = StringCommon.uuidFromStringWithoutDashes(args[1]);
            Player player = Cosmetics.getInstance().getServer().getPlayer(uuid);

            Cosmetics.CosmeticType cosmeticType = Cosmetics.CosmeticType.valueOf(args[2].toUpperCase());

            if (player != null) {
                // Player happens to be online, do it normally
                if (cosmeticType == Cosmetics.CosmeticType.PET) {
                    PetManager.addPet(sender, player, args[3].toLowerCase());
                } else if (cosmeticType == Cosmetics.CosmeticType.MORPH) {
                    MorphManager.addMorph(sender, player, args[3].toLowerCase());
                } else if (cosmeticType == Cosmetics.CosmeticType.PARTICLE) {
                    ParticleManager.addParticle(sender, player, args[3].toLowerCase());
                } else if (cosmeticType == Cosmetics.CosmeticType.GADGET) {
                    GadgetManager.addGadget(sender, player, args[3].toLowerCase());
                } else if (cosmeticType == Cosmetics.CosmeticType.ARROW_TRAIL) {
                    ArrowTrailManager.addArrowTrail(sender, player, args[3].toLowerCase());
                } else if (cosmeticType == Cosmetics.CosmeticType.ROD_TRAIL) {
                    RodTrailManager.addRodTrail(sender, player, args[3].toLowerCase());
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid cosmetic.");
                }
            } else {
                if (cosmeticType == Cosmetics.CosmeticType.PET) {
                    CosmeticsManager.getCosmeticsSettings(uuid).addPet(args[3].toLowerCase());
                } else if (cosmeticType == Cosmetics.CosmeticType.MORPH) {
                    CosmeticsManager.getCosmeticsSettings(uuid).addMorph(MorphManager.getMorph(args[3].toLowerCase()));
                } else if (cosmeticType == Cosmetics.CosmeticType.PARTICLE) {
                    CosmeticsManager.getCosmeticsSettings(uuid).addParticle(ParticleManager.getParticle(args[3].toLowerCase()));
                } else if (cosmeticType == Cosmetics.CosmeticType.GADGET) {
                    CosmeticsManager.getCosmeticsSettings(uuid).addGadget(GadgetManager.getGadget(args[3].toLowerCase()));
                } else if (cosmeticType == Cosmetics.CosmeticType.ARROW_TRAIL) {
                    CosmeticsManager.getCosmeticsSettings(uuid).addArrowTrail(ArrowTrailManager.getArrowTrail(args[3].toLowerCase()));
                } else if (cosmeticType == Cosmetics.CosmeticType.ROD_TRAIL) {
                    CosmeticsManager.getCosmeticsSettings(uuid).addRodTrail(RodTrailManager.getRodTrail(args[3].toLowerCase()));
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid cosmetic.");
                }

                // Send message to command sender
                sender.sendMessage(ChatColor.GREEN + "Gave cosmetic " + args[3].toLowerCase() + " (" + args[2].toUpperCase() + ") to " + args[0]);
            }
        } else if (args.length == 4 && args[0].equals("remove")) {
            final UUID uuid = StringCommon.uuidFromStringWithoutDashes(args[1]);

            Cosmetics.CosmeticType cosmeticType = Cosmetics.CosmeticType.valueOf(args[2].toUpperCase());

            CosmeticsManager.CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(uuid);
            if (cosmeticType == Cosmetics.CosmeticType.PET) {
                cosmeticsSettings.removePet(args[3].toLowerCase());
            } else if (cosmeticType == Cosmetics.CosmeticType.MORPH) {
                cosmeticsSettings.removeMorph(MorphManager.getMorph(args[3].toLowerCase()));
            } else if (cosmeticType == Cosmetics.CosmeticType.PARTICLE) {
                cosmeticsSettings.removeParticle(ParticleManager.getParticle(args[3].toLowerCase()));
            } else if (cosmeticType == Cosmetics.CosmeticType.GADGET) {
                cosmeticsSettings.removeGadget(GadgetManager.getGadget(args[3].toLowerCase()));
            } else if (cosmeticType == Cosmetics.CosmeticType.ARROW_TRAIL) {
                cosmeticsSettings.removeArrowTrail(ArrowTrailManager.getArrowTrail(args[3].toLowerCase()));
            } else if (cosmeticType == Cosmetics.CosmeticType.ROD_TRAIL) {
                cosmeticsSettings.removeRodTrail(RodTrailManager.getRodTrail(args[3].toLowerCase()));
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid cosmetic.");
            }

            // Send message to command sender
            sender.sendMessage(ChatColor.GREEN + "Removed cosmetic " + args[3].toLowerCase() + " (" + args[2].toUpperCase() + ") to " + args[0]);
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid arguments.");
        }

        return true;
    }

}
