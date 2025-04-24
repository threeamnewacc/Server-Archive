package me.devkevin.landcore.server;

import com.google.common.collect.ImmutableMap;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.storage.flatfile.Config;
import me.devkevin.landcore.task.ShutdownTask;
import me.devkevin.landcore.utils.message.CC;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ServerSettings {
    private final Config coreConfig;
    private final String whitelistMessage;
    @Setter
    private WhitelistMode serverWhitelistMode;
    @Setter
    private ShutdownTask shutdownTask;
    @Setter
    private boolean globalChatMuted;
    @Setter
    private int slowChatTime = -1;

    public ServerSettings(LandCore plugin) {
        this.coreConfig = new Config(plugin, "LandCore");

        coreConfig.addDefaults(ImmutableMap.<String, Object>builder()
                .put("whitelist.mode", WhitelistMode.NONE.name())
                .put("whitelist.message", CC.RED + "The server is whitelisted. Come back later!")
                .build());
        coreConfig.copyDefaults();

        this.serverWhitelistMode = WhitelistMode.valueOf(coreConfig.getString("whitelist.mode"));
        this.whitelistMessage = coreConfig.getString("whitelist.message");
    }

    public void saveConfig() {
        coreConfig.save();
    }
}
