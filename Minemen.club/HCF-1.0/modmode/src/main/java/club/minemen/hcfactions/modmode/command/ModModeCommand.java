package club.minemen.hcfactions.modmode.command;

import club.minemen.core.rank.Rank;
import club.minemen.core.util.cmd.CommandHandler;
import club.minemen.core.util.cmd.annotation.commandTypes.Command;
import club.minemen.hcfactions.modmode.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ModModeCommand implements CommandHandler {

	@Command(name = "modmode", rank = Rank.TRIAL_MOD, description = "Toggle your mod mode")
	public void modmodeCommand(Player sender) {
		Profile profile = Profile.getByUuid(sender.getUniqueId());

		if (profile == null) {
			sender.sendMessage(ChatColor.GREEN + "You have been put into Mod Mode.");

			new Profile(sender.getUniqueId());
		}
		else {
			sender.sendMessage(ChatColor.RED + "You have been put out of Mod Mode.");

			profile.setVanished(false);
			sender.getInventory().setContents(profile.getContents());
			sender.getInventory().setArmorContents(profile.getArmor());
			sender.setGameMode(profile.getGamemode());

			Profile.getProfiles().remove(sender.getUniqueId());
		}
	}

}
