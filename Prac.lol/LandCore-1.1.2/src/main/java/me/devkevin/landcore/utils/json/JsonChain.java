package me.devkevin.landcore.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 23/01/2023 @ 2:41
 * JsonChain / me.devkevin.landcore.utils.json / LandCore
 */
public class JsonChain {
    private JsonObject json = new JsonObject();

    public JsonChain addProperty(String property, String value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonChain addProperty(String property, Number value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonChain addProperty(String property, Boolean value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonChain addProperty(String property, Character value) {
        this.json.addProperty(property, value);
        return this;
    }

    public JsonChain add(String property, JsonElement element) {
        this.json.add(property, element);
        return this;
    }

    public JsonObject get() {
        return this.json;
    }
}
