package cc.patrone.practice.commands;

import zone.potion.commands.PlayerCommand;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.inventory.menu.impl.SettingsMenu;
import org.bukkit.entity.Player;

public class SettingsCommand extends PlayerCommand {
	private final PracticePlugin plugin;

	public SettingsCommand(PracticePlugin plugin) {
		super("settings");
		this.plugin = plugin;
		setAliases("options");
	}

	@Override
	public void execute(Player player, String[] args) {
		plugin.getMenuManager().getMenu(SettingsMenu.class).open(player);
	}
}
