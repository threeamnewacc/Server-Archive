package net.badlion.cosmetics.commands;

import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.managers.MorphManager;
import net.badlion.cosmetics.managers.ParticleManager;
import net.badlion.cosmetics.managers.PetManager;
import net.badlion.cosmetics.pets.Pet;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EquipCosmeticCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player && args.length == 2) {
            if (args[0].equalsIgnoreCase("pet")) {


                Pet pet = PetManager.getPet(args[1].toLowerCase());

                if (pet != null) {
                    PetManager.spawnPet((Player) sender, CosmeticsManager.getCosmeticsSettings(((Player) sender).getUniqueId()), pet);
                } else {
                    sender.sendMessage(ChatColor.RED + "Pet " + args[1].toLowerCase() + " not found");
                }
            } else if (args[0].equalsIgnoreCase("morph")) {
                MorphManager.equipMorph((Player) sender, args[1].toLowerCase());
            } else if (args[0].equalsIgnoreCase("particle")) {
                ParticleManager.equipParticle((Player) sender, args[0].toLowerCase());
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid cosmetic.");
            }
            return true;
        } else if (args.length == 3) {
            if (sender.isOp()) {
                Player player = Cosmetics.getInstance().getServer().getPlayerExact(args[0]);
                if (player != null) {
                    if (args[1].equalsIgnoreCase("pet")) {
                        Pet pet = PetManager.getPet(args[2].toLowerCase());

                        if (pet != null) {
                            PetManager.spawnPet(player, CosmeticsManager.getCosmeticsSettings(player.getUniqueId()), pet);
                        } else {
                            sender.sendMessage(ChatColor.RED + "Pet " + args[2].toLowerCase() + " not found");
                        }
                    } else if (args[1].equalsIgnoreCase("morph")) {
                        MorphManager.equipMorph(player, args[2].toLowerCase());
                    } else if (args[1].equalsIgnoreCase("particle")) {
                        ParticleManager.equipParticle(player, args[2].toLowerCase());
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid cosmetic.");

                        return false;
                    }
                    sender.sendMessage(ChatColor.YELLOW + "Attempting to equip " + args[2].toLowerCase() + " for " + player.getName());
                } else {
                    sender.sendMessage(ChatColor.RED + "Player not found.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }

            return true;
        }

        return false;
    }

}
