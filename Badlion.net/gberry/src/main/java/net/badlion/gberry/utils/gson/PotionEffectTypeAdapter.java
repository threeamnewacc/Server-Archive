package net.badlion.gberry.utils.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;

public class PotionEffectTypeAdapter extends TypeAdapter<PotionEffect> {

    @Override
    public void write(JsonWriter writer, PotionEffect potionEffect) throws IOException {
        if (potionEffect == null) {
            writer.nullValue();
            return;
        }
        writer.beginObject();
        writer.name("a").value(potionEffect.getAmplifier());
        writer.name("d").value(potionEffect.getDuration());
        writer.name("type").value(potionEffect.getType().getName());
        writer.endObject();
    }

    @Override
    public PotionEffect read(JsonReader reader) throws IOException {
        int amplifier = 0;
        int duration = 0;
        PotionEffectType potionEffectType = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "a":
                    amplifier = reader.nextInt();
                    break;
                case "d":
                    duration = reader.nextInt();
                    break;
                case "type":
                    potionEffectType = PotionEffectType.getByName(reader.nextString());
                    break;
            }
        }
        reader.endObject();
        return new PotionEffect(potionEffectType, duration, amplifier);
    }

}
