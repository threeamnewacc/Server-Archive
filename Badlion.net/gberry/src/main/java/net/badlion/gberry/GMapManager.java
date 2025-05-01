package net.badlion.gberry;

import java.util.*;

public class GMapManager {

    private static GMapManager instance;
    private static Map<String, Map<UUID, Object>> maps = new HashMap<>();

    public static GMapManager getInstance() {
        return GMapManager.instance;
    }

    public GMapManager() {
        GMapManager.instance = this;
    }

    public void register(GMap intf) {
        GMapManager.maps.put(intf.getName(), intf.getMap());
    }

    public String getValue(String mapName, UUID uuid) {
        Map<UUID, Object> map = GMapManager.maps.get(mapName);
        if (map == null) {
            return "No map";
        }

        Object o = map.get(uuid);
        if (o == null) {
            return "No uuid";
        }

        return o.toString();
    }

    public List<String> getAllKeyValues(String mapName) {
        Map<UUID, Object> map = GMapManager.maps.get(mapName);
        if (map == null) {
            return null;
        }

        List<String> keyValues = new ArrayList<>();
        for (Map.Entry<UUID, Object> entry : map.entrySet()) {
            keyValues.add(entry.getKey() + ": " + entry.getValue());
        }

        return keyValues;
    }

    public List<String> getAllKeys(String mapName) {
        Map<UUID, Object> map = GMapManager.maps.get(mapName);
        if (map == null) {
            return null;
        }

        List<String> keyValues = new ArrayList<>();
        for (UUID uuid : map.keySet()) {
            keyValues.add(uuid.toString());
        }

        return keyValues;
    }

    public List<String> getAllValues(String mapName) {
        Map<UUID, Object> map = GMapManager.maps.get(mapName);
        if (map == null) {
            return null;
        }

        List<String> keyValues = new ArrayList<>();
        for (Object o : map.values()) {
            keyValues.add(o.toString());
        }

        return keyValues;
    }

}
