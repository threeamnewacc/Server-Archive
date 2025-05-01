package net.badlion.disguise;

import net.badlion.disguise.commands.DisguiseCommand;
import net.badlion.disguise.commands.RedisguiseCommand;
import net.badlion.disguise.commands.UndisguiseCommand;
import net.badlion.disguise.commands.ViewDisguisedPlayerCommand;
import net.badlion.disguise.listeners.PlayerListener;
import net.badlion.disguise.managers.DisguiseManager;
import net.badlion.gberry.Gberry;
import org.bukkit.plugin.java.JavaPlugin;

public class Disguise extends JavaPlugin {

    private static Disguise plugin;

	private boolean commandsEnabled;

    public Disguise() {
        Disguise.plugin = this;

        Gberry.enableProtocol = true;
    }

    @Override
    public void onEnable() {
	    // Write JSON files from JAR file
	    /*Gberry.writeJarFile(this, "names.json");
	    Gberry.writeJarFile(this, "skins.json");

	    Connection connection = null;
	    PreparedStatement ps = null;
	    PreparedStatement ps2 = null;

	    StringBuilder sb = new StringBuilder("INSERT INTO disguise_names (disguise_name, in_use) VALUES");
	    StringBuilder sb2 = new StringBuilder("INSERT INTO disguise_skins (texture, signature) VALUES");

	    // Get files
	    File namesFile = new File(this.getDataFolder(), "names.json");
	    File jsonFile = new File(this.getDataFolder(), "skins.json");

	    try {
		    // Load names
		    JSONObject jsonObject = (JSONObject) JSONValue.parse(new String(Files.readAllBytes(Paths.get(namesFile.getAbsolutePath()))));
		    List<String> names = (List<String>) jsonObject.get("names");

		    // Load skins
		    jsonObject = (JSONObject) JSONValue.parse(new String(Files.readAllBytes(Paths.get(jsonFile.getAbsolutePath()))));
		    List<String> textures = (List<String>) jsonObject.get("textures");
		    List<String> signatures = (List<String>) jsonObject.get("signatures");

		    for (int i = 0; i < names.size(); i++) {
			    String name = names.get(i);

			    // Fail-safe
			    if (name.length() > 16) continue;

			    sb.append(" (?, 'false'),");
		    }

		    for (int i = 0; i < textures.size(); i++) {
			    sb2.append(" (?, ?),");
		    }

		    connection = Gberry.getUnsafeConnection();

		    String s = sb.toString();
		    ps = connection.prepareStatement(s.substring(0, s.length() - 1) + ";");

		    s = sb2.toString();
		    ps2 = connection.prepareStatement(s.substring(0, s.length() - 1) + ";");

		    int j = 1;
		    for (int i = 0; i < names.size(); i++) {
			    String name = names.get(i);

			    // Fail-safe
			    if (name.length() > 16) continue;

			    ps.setString(j++, name);
		    }

		    j = 1;
		    for (int i = 0; i < textures.size(); i++) {
			    String texture = textures.get(i);
			    String signature = signatures.get(i);

			    ps2.setString(j++, texture);
			    ps2.setString(j++, signature);
		    }

		    Gberry.executeUpdate(connection, ps);
		    Gberry.executeUpdate(connection, ps2);

		    if (true) return;

	    } catch (Exception e) {
		    this.getLogger().info("Failed to insert names/skins");
		    e.printStackTrace();
	    } finally {
		    Gberry.closeComponents(ps2);
		    Gberry.closeComponents(ps, connection);
	    }*/

	    this.saveDefaultConfig();

	    this.commandsEnabled = this.getConfig().getBoolean("commands-enabled");

	    DisguiseManager.initialize();

	    this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        this.getCommand("disguise").setExecutor(new DisguiseCommand());
        this.getCommand("redisguise").setExecutor(new RedisguiseCommand());
        this.getCommand("undisguise").setExecutor(new UndisguiseCommand());
        this.getCommand("viewdisguise").setExecutor(new ViewDisguisedPlayerCommand());
    }

    @Override
    public void onDisable() {

    }

	public static Disguise getInstance() {
		return Disguise.plugin;
	}

	public boolean areCommandsEnabled() {
		return this.commandsEnabled;
	}

	public void setCommandsEnabled(boolean enabled) {
		this.commandsEnabled = enabled;
	}

}
