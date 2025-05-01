package net.badlion.gberry;

import java.util.Map;
import java.util.UUID;

public interface GMap<T> {

    public String getName();

    public Map<UUID, T> getMap();

}
