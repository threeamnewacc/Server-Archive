package club.minemen.events.util;

import club.minemen.core.CorePlugin;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

public final class JsonUtil {

	private static <T> JsonArray toJsonArray(Iterable<T> collection) {
		if (collection == null) {
			return new JsonArray();
		}

		JsonArray array = new JsonArray();

		for (T t : collection) {
			array.add(toJson(t));
		}

		return array;
	}

	private static <T> void putAll(JsonObject object, T instance) {
		Class<?> parent = instance.getClass();

		while (parent != Object.class) {
			try {
				for (Field field : parent.getDeclaredFields()) {
					if (Modifier.isTransient(field.getModifiers())) {
						continue;
					}

					field.setAccessible(true);

					Object value = field.get(instance);

					if (value instanceof Number) {
						object.addProperty(field.getName(), (Number) value);
					} else if (value instanceof Boolean) {
						object.addProperty(field.getName(), (Boolean) value);
					} else if (value instanceof String) {
						object.addProperty(field.getName(), (String) value);
					} else if (value instanceof Character) {
						object.addProperty(field.getName(), (Character) value);
					} else {
						object.add(field.getName(), toJson(value));
					}
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			parent = parent.getSuperclass();
		}
	}

	private static <T> JsonObject toJsonObject(T t) {
		if (t == null) {
			return new JsonObject();
		}

		JsonObject object = new JsonObject();

		putAll(object, t);

		return object;
	}

	public static <T> JsonElement toJson(T t) {
		if (t == null) {
			return JsonNull.INSTANCE;
		}

		if (t instanceof Iterable) {
			//noinspection unchecked
			return toJsonArray((Iterable<T>) t);
		}

		return toJsonObject(t);
	}

	public static <T> T fromJsonObject(JsonObject object, Class<T> clazz, T t, JsonDeserializationContext jsonDeserializationContext) throws IllegalAccessException, InstantiationException {
		for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
			Class<?> parent = t.getClass();

			Field field = null;
			while (parent != Object.class) {
				try {
					field = parent.getDeclaredField(entry.getKey());
					break;
				} catch (NoSuchFieldException ignore) {

				}

				parent = parent.getSuperclass();
			}

			if (field == null) {
				continue;
			}

			field.setAccessible(true);

			System.out.println(entry.getKey() + " -> " + entry.getValue());

			JsonElement value = entry.getValue();
			if (value.isJsonPrimitive()) {
				JsonPrimitive primitive = value.getAsJsonPrimitive();

				if (primitive.isString()) {
					field.set(t, primitive.getAsString());
				} else if (primitive.isBoolean()) {
					field.set(t, primitive.getAsBoolean());
				} else if (primitive.isNumber()) {
					if (field.getType() == long.class) {
						field.set(t, primitive.getAsNumber().longValue());
					} else if (field.getType() == double.class) {
						field.set(t, primitive.getAsNumber().doubleValue());
					} else if (field.getType() == int.class) {
						field.set(t, primitive.getAsNumber().intValue());
					}
				}
			} else {
				//t = fromJsonObject((JsonObject) value, t, jsonDeserializationContext);

				//jsonDeserializationContext.deserialize(value, t.getClass().getComponentType());
				//System.out.println("xdxdxdxd");
				return CorePlugin.GSON.fromJson(value, clazz);
			}
		}

		return t;
	}

	public static <T> T fromJson(JsonElement element, Class<T> clazz, JsonDeserializationContext jsonDeserializationContext) {
		if (!element.isJsonObject()) {
			throw new IllegalArgumentException("Not a JsonObject");
		}

		try {
			T t = clazz.newInstance();

			JsonObject object = element.getAsJsonObject();

			t = fromJsonObject(object, clazz, t, jsonDeserializationContext);

			return t;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
