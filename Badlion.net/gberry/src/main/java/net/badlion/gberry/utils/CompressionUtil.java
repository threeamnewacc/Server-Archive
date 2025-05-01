package net.badlion.gberry.utils;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionUtil {

    public static byte[] compress(String str) throws Exception {
        if (str == null || str.length() == 0) {
            return null;
        }

        ByteArrayOutputStream obj = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(obj);
        gzip.write(str.getBytes("UTF-8"));
        gzip.close();
        return obj.toByteArray();
    }

    public static String decompress(byte[] bytes) throws Exception {
        if (bytes == null || bytes.length == 0) {
            return "";
        }

        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        String outStr = "";
        String line;

        while ((line=bf.readLine()) != null) {
            outStr += line;
        }

        return outStr;
    }

	public static Map<String, Object> recreateMap(Map<String, Object> original) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Map.Entry<String, Object> entry : original.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}

	public static Map<String, Object> serialize(ConfigurationSerializable cs) {
		Map<String, Object> serialized = recreateMap(cs.serialize());
		for (Map.Entry<String, Object> entry : serialized.entrySet()) {
			if (entry.getValue() instanceof ConfigurationSerializable) {
				entry.setValue(CompressionUtil.serialize((ConfigurationSerializable) entry.getValue()));
			}
		}
		serialized.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(cs.getClass()));
		return serialized;
	}

	public static List<Map<String, Object>> serializeItemList(List<ConfigurationSerializable> list) {
		List<Map<String, Object>> returnVal = new ArrayList<Map<String, Object>>();
		for (ConfigurationSerializable cs : list) {
			if (cs == null)
				returnVal.add(null);
			else
				returnVal.add(CompressionUtil.serialize(cs));
		}
		return returnVal;
	}

	public static ConfigurationSerializable deserialize(Map<String, Object> map) {
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			// Check if any of its sub-maps are ConfigurationSerializable. They need to be done first.
			if (entry.getValue() instanceof Map && ((Map)entry.getValue()).containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
				entry.setValue(deserialize((Map)entry.getValue()));
			}
		}
		return ConfigurationSerialization.deserializeObject(map);
	}

	public static List<ConfigurationSerializable> deserializeItemList(List<Map<String, Object>> itemList) {
		List<ConfigurationSerializable> returnVal = new ArrayList<ConfigurationSerializable>();
		for (Map<String, Object> map : itemList) {
			if (map == null)
				returnVal.add(null);
			else
				returnVal.add(CompressionUtil.deserialize(map));
		}
		return returnVal;
	}

}
