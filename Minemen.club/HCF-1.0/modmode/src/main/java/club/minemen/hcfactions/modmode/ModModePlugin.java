package club.minemen.hcfactions.modmode;

import club.minemen.core.CorePlugin;
import club.minemen.hcfactions.modmode.command.InvSeeCommand;
import club.minemen.hcfactions.modmode.command.ModModeCommand;
import club.minemen.hcfactions.modmode.profile.Profile;
import club.minemen.hcfactions.modmode.profile.ProfileListeners;
import java.util.Arrays;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ModModePlugin extends JavaPlugin {

	@Getter private static ModModePlugin instance;

	@Override
	public void onEnable() {
		instance = this;

		// Register Listeners
		this.getServer().getPluginManager().registerEvents(new ProfileListeners(), this);

		// Register Commands using Core
		CorePlugin.getInstance().getCommandManager().registerAllClasses(Arrays.asList(
				new ModModeCommand(),
				new InvSeeCommand()
		));
	}

	@Override
	public void onDisable() {
		for (Profile profile : Profile.getProfiles().values()) {
			Player player = profile.getPlayer();

			if (player != null) {
				profile.setVanished(false);
				player.getInventory().setContents(profile.getContents());
				player.getInventory().setArmorContents(profile.getArmor());
				player.setGameMode(profile.getGamemode());
			}
		}
	}

}
