package us.zonix.core.server.impl;

import us.zonix.core.CorePlugin;
import us.zonix.core.server.ServerState;

public class DefaultServerDataProvider implements ServerDataProvider {

    private CorePlugin plugin = CorePlugin.getInstance();

    @Override
    public ServerState getState() {
        return this.plugin.isSetupMode() ? ServerState.SETUP : ServerState.ACTIVE;
    }

    @Override
    public int getPlayerCount() {
        return this.plugin.getServer().getOnlinePlayers().size();
    }

}
