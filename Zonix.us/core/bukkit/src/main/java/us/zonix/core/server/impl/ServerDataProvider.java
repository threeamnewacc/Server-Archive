package us.zonix.core.server.impl;

import us.zonix.core.server.ServerState;

public interface ServerDataProvider {

    ServerState getState();

    int getPlayerCount();

}
