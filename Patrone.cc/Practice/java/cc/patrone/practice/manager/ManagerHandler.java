package cc.patrone.practice.manager;

import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.manager.impl.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ManagerHandler {

    private final PracticePlugin plugin;
    private ProfileManager profileManager;
    private ScoreboardManager scoreboardManager;
    private InventoryManager inventoryManager;
    private PlayerManager playerManager;
    private SettingsManager settingsManager;
    private ArenaManager arenaManager;
    private KitManager kitManager;
    private ConfigurationManager configurationManager;

    public void registerManagers() {
        profileManager = new ProfileManager(this);
        scoreboardManager = new ScoreboardManager(this);
        inventoryManager = new InventoryManager(this);
        playerManager = new PlayerManager(this);
        settingsManager = new SettingsManager(this);
        arenaManager = new ArenaManager(this);
        kitManager = new KitManager(this);
        configurationManager = new ConfigurationManager(this);
    }

    public void save() {
        settingsManager.save();
        arenaManager.save();
        kitManager.save();
    }
}
