package net.badlion.smellychat.managers;

import net.badlion.smellychat.Channel;
import net.badlion.smellychat.SmellyChat;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ChannelManager {

	private static final File directory = new File("./plugins/SmellyChat/channels/");

	private static Map<String, Channel> channels = new HashMap<>();

	public static void initialize() {
		if (!ChannelManager.directory.exists()) {
			ChannelManager.directory.mkdirs();
		}

		ChannelManager.loadChannels();
	}

	public static Channel getChannel(String identifier) {
		return ChannelManager.channels.get(identifier);
	}

	public static Collection<Channel> getChannels() {
		return ChannelManager.channels.values();
	}

	private static void loadChannels() {
		for (File channelConfig : ChannelManager.directory.listFiles()) {
			FileConfiguration config = new YamlConfiguration();
			try {
				config.load(channelConfig);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}

			Channel channel = new Channel(config.getString("Name"), config.getString("Identifier"), config.getString("Color"));

			ChannelManager.channels.put(channel.getIdentifier(), channel);
			SmellyChat.getInstance().getLogger().info("Channel " + channel.getName() + " loaded");
		}

		// Load global if the config file for it doesn't exist
		if (!new File(directory, "Global.yml").exists()) {
			Channel global = new Channel("Global", "G", "f");
			ChannelManager.saveChannel(global);
		}
	}

	private static void saveChannel(Channel ch) {
		FileConfiguration config = new YamlConfiguration();

		String name = ch.getName();

		config.set("Name", name);
		config.set("Color", ch.getColor().charAt(1));
		config.set("Identifier", ch.getIdentifier());

		File file = new File(directory, name + ".yml");

		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
