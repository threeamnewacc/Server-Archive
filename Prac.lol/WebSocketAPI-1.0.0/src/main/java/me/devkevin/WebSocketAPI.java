package me.devkevin;

import club.udrop.chatcolor.CC;
import lombok.Getter;
import me.devkevin.file.ConfigFile;
import me.devkevin.profile.listener.ProfileListener;
import me.devkevin.profile.manager.ProfileManager;
import me.devkevin.socket.WebSocketAPIProcessor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getPluginManager;

/**
 * Created by DevKevin
 * Project: WebSocketAPI
 * Date: 23/02/2022 @ 4:27
 */
@Getter
public class WebSocketAPI extends JavaPlugin {
    @Getter private static WebSocketAPI instance;

    private ConfigFile configFile;

    private ProfileManager profileManager;
    private WebSocketAPIProcessor processor;

    private String apiUrl, apiKey;

    @Override
    public void onEnable() {
        instance = this;

        if (!WebSocketAPI.getInstance().getDescription().getAuthors().contains("DevKevin") || !WebSocketAPI.getInstance().getDescription().getName().equals("WebSocketAPI")) {
            getPluginManager().disablePlugin(this);
        }

        Bukkit.getConsoleSender().sendMessage(CC.STRIKETHROUGH);
        Bukkit.getConsoleSender().sendMessage(CC.translate("&7[WebSocketAPI]: &aHas been enabled correctly."));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&7Made by &6DevKevin"));
        Bukkit.getConsoleSender().sendMessage(CC.STRIKETHROUGH);

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        this.configFile = new ConfigFile(this, "config");

        this.apiUrl = this.configFile.getString("api.url");
        this.apiKey = this.configFile.getString("api.key");

        profileManager = new ProfileManager();
        processor = new WebSocketAPIProcessor(this, this.apiUrl, this.apiKey);

        this.getServer().getPluginManager().registerEvents(new ProfileListener(), this);
    }

    @Override
    public void onDisable() {
        instance = null;
    }
}
