package us.zonix.api.util;

import com.google.gson.JsonObject;

public class Constants {

	public static String SUCCESS;
	public static String FAILED;
	public static String PLAYER_NOT_FOUND;
	public static String MALFORMED_REQUEST;

	static {
		JsonObject object = new JsonObject();
		object.addProperty("response", "SUCCESS");
		SUCCESS = object.toString();

		object = new JsonObject();
		object.addProperty("response", "FAILED");
		FAILED = object.toString();

		object = new JsonObject();
		object.addProperty("response", "PLAYER_NOT_FOUND");
		PLAYER_NOT_FOUND = object.toString();

		object = new JsonObject();
		object.addProperty("response", "MALFORMED_REQUEST");
		MALFORMED_REQUEST = object.toString();
	}

	public static String getSuccessPair(String key, Object value) {
		JsonObject object = new JsonObject();
		object.addProperty("response", "success");
		object.addProperty(key, value.toString());

		return object.toString();
	}

	public static boolean isValidKey(String key) {
		return key != null && key.equals("faggot");
	}

}
