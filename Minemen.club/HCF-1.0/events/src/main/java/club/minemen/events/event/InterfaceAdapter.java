package club.minemen.events.event;

import club.minemen.events.util.JsonUtil;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public final class InterfaceAdapter implements JsonSerializer<Object>, JsonDeserializer<Object> {

	private static final String CLASSNAME = "CLASSNAME";
	private static final String EVENT = "EVENT";
	private static final String DATA = "DATA";

	@Override
	public JsonElement serialize(Object o, Type type, JsonSerializationContext jsonSerializationContext) {
		System.out.println("bye go power rangers " + o.getClass());

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(CLASSNAME, o.getClass().getName());

		if (!(o instanceof Event)) {
			jsonObject.add(DATA, jsonSerializationContext.serialize(o));
			jsonObject.addProperty(EVENT, false);
		} else {
			jsonObject.add(DATA, ((Event) o).toJson());
			jsonObject.addProperty(EVENT, true);
		}

		return jsonObject;
	}

	public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
			throws JsonParseException {

		System.out.println("go go power rangers");
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		if (jsonObject.get(CLASSNAME) == null) {
			return null;
		}

		JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
		String className = prim.getAsString();
		Class clazz = getObjectClass(className);

		boolean event = jsonObject.get(EVENT).getAsBoolean();

		if (event) {
			return JsonUtil.fromJson(jsonObject.get(DATA), clazz, jsonDeserializationContext);
		} else {
			return jsonDeserializationContext.deserialize(jsonObject.get(DATA), clazz);
		}
	}

	/****** Helper method to get the className of the object to be deserialized *****/
	public Class getObjectClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			//e.printStackTrace();
			throw new JsonParseException(e.getMessage());
		}
	}
}
