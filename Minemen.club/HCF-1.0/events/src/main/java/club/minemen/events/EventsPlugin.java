package club.minemen.events;

import club.minemen.clublibrary.scoreboard.ClubScoreboardHandler;
import club.minemen.core.CorePlugin;
import club.minemen.events.command.EventCommand;
import club.minemen.events.event.Event;
import club.minemen.events.event.EventManager;
import club.minemen.events.event.InterfaceAdapter;
import club.minemen.events.event.impl.KothEvent;
import club.minemen.events.event.impl.conquest.ConquestEvent;
import club.minemen.events.handler.EventMovementHandler;
import club.minemen.spigot.ClubSpigot;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @since 12/17/2017
 */
@Getter
public final class EventsPlugin extends JavaPlugin {

	@Getter private static EventsPlugin instance;

	private EventManager eventManager;
	private WorldEditPlugin worldEditPlugin;

	@Override
	public void onEnable() {
		instance = this;

		if (this.getServer().getPluginManager().getPlugin("WorldEdit") != null) {
			this.worldEditPlugin = (WorldEditPlugin) this.getServer().getPluginManager().getPlugin("WorldEdit");
		}

		this.eventManager = new EventManager();

		CorePlugin.getInstance().getCommandManager().registerAllClasses(Collections.singletonList(
				new EventCommand(this)
		));

		ClubSpigot.INSTANCE.addMovementHandler(new EventMovementHandler(this));

		ClubScoreboardHandler.getScoreboardConfiguration().getScoreGetters().add(player -> {
			List<String> scores = new ArrayList<>();

			this.getEventManager().getActiveEvents().forEach(event -> {
				// Consider this 'caching' the scoreboard display so we don't double call the shit
				String display = event.getScoreboardDisplay();
				if (display != null) {
					scores.add(display);
				}
			});

			return scores;
		});


		/*CorePlugin.GSONBUILDER.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(Event.class, "type")
		                                                                           .registerSubtype(KothEvent.class, "koth")
		);*/
		CorePlugin.GSONBUILDER.registerTypeAdapter(KothEvent.class, new InterfaceAdapter());
		CorePlugin.GSONBUILDER.registerTypeAdapter(ConquestEvent.class, new InterfaceAdapter());
		CorePlugin.GSONBUILDER.registerTypeAdapter(Event.class, new InterfaceAdapter());

		CorePlugin.GSON = CorePlugin.GSONBUILDER.create();
		this.loadConfig();
	}

	private void loadConfig() {
		File eventsFile = new File(this.getDataFolder() + "/events.json");

		try (FileReader reader = new FileReader(eventsFile)) {
			Type eventsType = new TypeToken<List<Event>>() {
			}.getType();
			List<Event> events = CorePlugin.GSON.fromJson(reader, eventsType);
			events.forEach(event -> this.getEventManager().register(event));
		} catch (FileNotFoundException ex) {
			// ignore
		} catch (Exception ex) {
			this.getLogger().warning("Failed to load events.json");
			ex.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		if (!this.getDataFolder().exists()) {
			this.getDataFolder().mkdirs();
		}

		File feast = new File(this.getDataFolder() + "/events.json");
		try (FileWriter writer = new FileWriter(feast)) {
			CorePlugin.GSON.toJson(this.getEventManager().getEventMap().values(), writer);
		} catch (Exception ex) {
			this.getLogger().warning("Failed to save events.json");
			ex.printStackTrace();
		}
	}

}
