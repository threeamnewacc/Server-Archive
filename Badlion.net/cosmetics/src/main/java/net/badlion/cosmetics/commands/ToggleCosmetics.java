package net.badlion.cosmetics.commands;

import net.badlion.common.libraries.StringCommon;
import net.badlion.cosmetics.Cosmetics;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ToggleCosmetics implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 1) {
            Cosmetics.CosmeticType type = null;
            boolean toChange = true;

            switch (args[0]) {
                case "pets":
                    type = Cosmetics.CosmeticType.PET;
                    toChange = Cosmetics.getInstance().isPetsEnabled();
                    break;
                case "morphs":
                    type = Cosmetics.CosmeticType.MORPH;
                    toChange = Cosmetics.getInstance().isMorphsEnabled();
                    break;
                case "arrow_trails":
                    type = Cosmetics.CosmeticType.ARROW_TRAIL;
                    toChange = Cosmetics.getInstance().isArrowTrailsEnabled();
                    break;
                case "particles":
                    type = Cosmetics.CosmeticType.PARTICLE;
                    toChange = Cosmetics.getInstance().isParticlesEnabled();
                    break;
                case "gadgets":
                    type = Cosmetics.CosmeticType.GADGET;
                    toChange = Cosmetics.getInstance().isGadgetsEnabled();
                    break;
                case "enable":
                    Cosmetics.getInstance().allowCosmetics();
                    sender.sendMessage(ChatColor.GREEN + "Enabled all cosmetics");
                    return true;
                case "disable":
                    Cosmetics.getInstance().disallowCosmetics();
                    sender.sendMessage(ChatColor.GREEN + "Disabled all cosmetics");
                    return true;
                default:
                    sender.sendMessage(ChatColor.RED + "Unknown cosmetics type");
                    return true;
            }

            Cosmetics.getInstance().setCosmeticEnabled(type, !toChange);
            sender.sendMessage(ChatColor.GREEN + StringCommon.niceUpperCase(args[0]) + " have been " + (toChange ? "disabled" : "enabled"));
        }

        return false;
    }

}
