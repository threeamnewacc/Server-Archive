package net.badlion.ministats;

import net.badlion.ministats.listeners.PlayerDataListener;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MiniStats extends JavaPlugin {

	public static boolean DISABLE_PLAYER_LISTENER_DEATHS = false;

	public static int SEASON = 1;

	public static String TAG = "MPG";
	public static String TYPE = "???";
    public static String TABLE_NAME = "mpg";
    public static String MATCH_ID = null;

	private static MiniStats plugin;

	private PlayerDataListener playerDataListener = new PlayerDataListener();

	private MiniStatsPlayerCreator miniStatsPlayerCreator;

    public MiniStats() {
        MiniStats.plugin = this;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public void startListening() {
        this.playerDataListener.setTrackStats(true);
        MiniStats.plugin.getServer().getPluginManager().registerEvents(this.playerDataListener, MiniStats.plugin);
    }

    public void stopListening() {
        HandlerList.unregisterAll(this.playerDataListener);
    }

    public PlayerDataListener getPlayerDataListener() {
        return playerDataListener;
    }

    public static long getLong(Object obj) {
        if (obj instanceof Integer) {
            return (long) (int) obj;
        } else if (obj instanceof Long) {
            return (long) obj;
        } else if (obj instanceof Double) {
            return (long) (double) obj;
        } else if (obj instanceof Float) {
            return (long) (float) obj;
        } else if (obj instanceof Short) {
            return (long) (short) obj;
        }

        return 0;
    }

    public static JSONObject mergeJSON(JSONObject obj1, JSONObject obj2) {
        for (Object key : obj2.keySet()) {
            String k = (String) key;
            // Prevent really strange shit
            if (k.equalsIgnoreCase("id") || k.equalsIgnoreCase("key") || k.equalsIgnoreCase("value") || k.equalsIgnoreCase("version") || k.equalsIgnoreCase("_id") || k.equalsIgnoreCase("_rev")) {
                continue;
            }

            if (!obj1.containsKey(key)) {
                obj1.put(key, obj2.get(key));
            } else if (obj1.get(key) instanceof String) {
                continue;
            } else if (k.equalsIgnoreCase("highestKillStreak")) {
                long ks1 = (long) obj1.get("highestKillStreak");
                long ks2 = (long) obj2.get("highestKillStreak");

                if (ks2 > ks1) {
                    obj1.put("highestKillStreak", ks2);
                }
            } else if (k.equalsIgnoreCase("kdr")) {
                double kdr;
                long kills = (long) obj1.get("kills") + (long) obj2.get("kills");
                long deaths = (long) obj1.get("deaths") + (long) obj2.get("deaths");
                if (deaths == 0L) {
                    kdr = kills;
                } else if (kills == 0L) {
                    kdr = 0;
                } else {
                    kdr = kills / (double) deaths;
                }

                obj1.put("kdr", kdr);
            } else if (obj1.get(key) instanceof JSONObject) {
                MiniStats.mergeJSON((JSONObject) obj1.get(key), (JSONObject) obj2.get(key));
            } else if (obj1.get(key) instanceof JSONArray) {
                JSONArray jsonArray1 = (JSONArray) obj1.get(key);
                JSONArray jsonArray2 = (JSONArray) obj2.get(key);

                for (Object o : jsonArray2) {
                    if (!jsonArray1.contains(o)) {
                        jsonArray1.add(o);
                    }
                }
            } else if (obj1.get(key) instanceof Boolean) {
                obj1.put(key, obj2.get(key));
            } else {
                obj1.put(key, MiniStats.mergeNumbers(obj1.get(key), obj2.get(key)));
            }
        }

        return obj1;
    }

    public static Object mergeNumbers(Object one, Object two) {
        if (two instanceof Integer) {
            return mergeNumbers(one, ((Integer) two).intValue());
        } else if (two instanceof Long) {
            return mergeNumbers(one, ((Long) two).longValue());
        } else if (two instanceof Double) {
            return mergeNumbers(one, ((Double) two).doubleValue());
        } else if (two instanceof Short) {
            return mergeNumbers(one, ((Short) two).shortValue());
        } else if (two instanceof Float) {
            return mergeNumbers(one, ((Float) two).floatValue());
        }

        return one;
    }

    public static Object mergeNumbers(Object object, int toAdd) {
        if (object instanceof Integer) {
            return (Integer) object + toAdd;
        } else if (object instanceof Long) {
            return (Long) object + toAdd;
        } else if (object instanceof Double) {
            return (Double) object + toAdd;
        } else if (object instanceof Short) {
            return (Short) object + toAdd;
        } else if (object instanceof Float) {
            return (Float) object + toAdd;
        }

        return toAdd;
    }

    public static Object mergeNumbers(Object object, long toAdd) {
        if (object instanceof Integer) {
            return (Integer) object + toAdd;
        } else if (object instanceof Long) {
            return (Long) object + toAdd;
        } else if (object instanceof Double) {
            return (Double) object + toAdd;
        } else if (object instanceof Short) {
            return (Short) object + toAdd;
        } else if (object instanceof Float) {
            return (Float) object + toAdd;
        }

        return toAdd;
    }

    public static Object mergeNumbers(Object object, double toAdd) {
        if (object instanceof Integer) {
            return (Integer) object + toAdd;
        } else if (object instanceof Long) {
            return (Long) object + toAdd;
        } else if (object instanceof Double) {
            return (Double) object + toAdd;
        } else if (object instanceof Short) {
            return (Short) object + toAdd;
        } else if (object instanceof Float) {
            return (Float) object + toAdd;
        }

        return toAdd;
    }

    public static Object mergeNumbers(Object object, float toAdd) {
        if (object instanceof Integer) {
            return (Integer) object + toAdd;
        } else if (object instanceof Long) {
            return (Long) object + toAdd;
        } else if (object instanceof Double) {
            return (Double) object + toAdd;
        } else if (object instanceof Short) {
            return (Short) object + toAdd;
        } else if (object instanceof Float) {
            return (Float) object + toAdd;
        }

        return toAdd;
    }

    public static Object mergeNumbers(Object object, short toAdd) {
        if (object instanceof Integer) {
            return (Integer) object + toAdd;
        } else if (object instanceof Long) {
            return (Long) object + toAdd;
        } else if (object instanceof Double) {
            return (Double) object + toAdd;
        } else if (object instanceof Short) {
            return (Short) object + toAdd;
        } else if (object instanceof Float) {
            return (Float) object + toAdd;
        }

        return toAdd;
    }

	public static MiniStats getInstance() {
		return MiniStats.plugin;
	}

	public MiniStatsPlayerCreator getMiniStatsPlayerCreator() {
		return this.miniStatsPlayerCreator;
	}

	public void setMiniStatsPlayerCreator(MiniStatsPlayerCreator miniStatsPlayerCreator) {
		this.miniStatsPlayerCreator = miniStatsPlayerCreator;
	}

}
