package me.devkevin.landcore.gson;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.utils.location.CustomLocation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 18/01/2023 @ 15:15
 * CustomLocationTypeAdapterFactory / land.pvp.core.gson / LandCore
 */
public class CustomLocationTypeAdapterFactory implements TypeAdapterFactory {

    public static LocationData serialize(CustomLocation customLocation) {
        Preconditions.checkNotNull(customLocation);

        return new LocationData(customLocation.getWorld(), customLocation.getX(), customLocation.getY(),
                customLocation.getZ(), customLocation.getYaw(), customLocation.getPitch());
    }

    public static CustomLocation deserialize(LocationData locationData) {
        Preconditions.checkNotNull(locationData);

        return new CustomLocation(locationData.getWorld(), locationData.getX(), locationData.getY(), locationData
                .getZ(), locationData.getYaw(), locationData.getPitch());
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        if (!CustomLocation.class.isAssignableFrom(typeToken.getRawType())) {
            return null;
        }

        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter jsonWriter, T location) throws IOException {
                if (location == null) {
                    jsonWriter.nullValue();
                } else {
                    LandCore.GSON.toJson(serialize((CustomLocation) location), LocationData.class, jsonWriter);
                }
            }

            @Override
            public T read(JsonReader jsonReader) throws IOException {
                if (jsonReader.peek() == null) {
                    jsonReader.nextNull();
                    return null;
                } else {
                    return (T) deserialize(LandCore.GSON.fromJson(jsonReader, LocationData.class));
                }
            }
        };
    }

    @Getter
    @RequiredArgsConstructor
    private static class LocationData {

        private final String world;
        private final double x;
        private final double y;
        private final double z;
        private final float yaw;
        private final float pitch;
    }
}
