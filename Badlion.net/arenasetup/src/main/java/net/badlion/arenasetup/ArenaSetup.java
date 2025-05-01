package net.badlion.arenasetup;

import net.badlion.arenasetup.command.ArenaStatusCommand;
import net.badlion.arenasetup.command.DeleteArenaCommand;
import net.badlion.arenasetup.command.FinishSetupCommand;
import net.badlion.arenasetup.command.ListArenasCommand;
import net.badlion.arenasetup.command.SetArenaTypesCommand;
import net.badlion.arenasetup.command.SetupCommand;
import net.badlion.arenasetup.command.UpdateArenaSelection;
import net.badlion.arenasetup.command.Warp1Command;
import net.badlion.arenasetup.command.Warp2Command;
import net.badlion.arenasetup.inventory.ArenaTypeInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArenaSetup extends JavaPlugin {

	private static ArenaSetup plugin;

	public ArenaSetup() {
		this.plugin = this;
	}

	Map<UUID, SetupSession> setupSessionMap = new HashMap<>();

	@Override
	public void onEnable() {
		new ArenaTypeInventory();

		this.getCommand("arenasetup").setExecutor(new SetupCommand());
		this.getCommand("setwarp1").setExecutor(new Warp1Command());
		this.getCommand("setwarp2").setExecutor(new Warp2Command());
		this.getCommand("setarenatypes").setExecutor(new SetArenaTypesCommand());
		this.getCommand("finishsetup").setExecutor(new FinishSetupCommand());
		this.getCommand("delarena").setExecutor(new DeleteArenaCommand());
		this.getCommand("arenastatus").setExecutor(new ArenaStatusCommand());
		this.getCommand("setarenaselection").setExecutor(new UpdateArenaSelection());
		this.getCommand("listarenas").setExecutor(new ListArenasCommand());

	}

	public static ArenaSetup getInstance() {
		return plugin;
	}

	public Map<UUID, SetupSession> getSetupSessionMap() {
		return setupSessionMap;
	}
}
