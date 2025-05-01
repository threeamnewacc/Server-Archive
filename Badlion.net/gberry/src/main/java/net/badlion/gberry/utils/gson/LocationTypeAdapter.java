package net.badlion.gberry.utils.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;

public class LocationTypeAdapter extends TypeAdapter<Location> {

	@Override
	public void write(JsonWriter writer, Location location) throws IOException {
		if (location == null) {
			writer.nullValue();
			return;
		}
		writer.beginObject();
		writer.name("world").value(location.getWorld().getName());
		writer.name("x").value(location.getX());
		writer.name("y").value(location.getY());
		writer.name("z").value(location.getZ());
		writer.name("yaw").value(location.getYaw());
		writer.name("pitch").value(location.getPitch());
		writer.endObject();
	}

	@Override
	public Location read(JsonReader reader) throws IOException {
		String worldName = null;
		double x = 0, y = 0, z = 0;
		float yaw = 0, pitch = 0;
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			switch (name) {
				case "world":
					worldName = reader.nextString();
					break;
				case "x":
					x = reader.nextDouble();
					break;
				case "y":
					y = reader.nextDouble();
					break;
				case "z":
					z = reader.nextDouble();
					break;
				case "yaw":
					yaw = (float) reader.nextDouble();
					break;
				case "pitch":
					pitch = (float) reader.nextDouble();
					break;
			}
		}
		reader.endObject();
		return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
	}

}
