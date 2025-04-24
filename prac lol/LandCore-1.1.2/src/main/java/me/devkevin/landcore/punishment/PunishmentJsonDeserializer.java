package me.devkevin.landcore.punishment;

import com.google.gson.JsonObject;
import me.devkevin.landcore.utils.json.JsonDeserializer;

import java.util.UUID;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 09/02/2023 @ 3:31
 * PunishmentJsonDeserializer / me.devkevin.landcore.punishment / LandCore
 */
public class PunishmentJsonDeserializer implements JsonDeserializer<Punishment> {

    @Override
    public Punishment deserialize(JsonObject object) {
        Punishment punishment = new Punishment(
                UUID.fromString(object.get("uuid").getAsString()),
                PunishmentType.valueOf(object.get("type").getAsString()),
                object.get("addedAt").getAsLong(),
                object.get("addedReason").getAsString(),
                object.get("duration").getAsLong()
        );

        if (!object.get("addedBy").isJsonNull()) {
            punishment.setAddedBy(UUID.fromString(object.get("addedBy").getAsString()));
        }

        if (!object.get("pardonedBy").isJsonNull()) {
            punishment.setPardonedBy(UUID.fromString(object.get("pardonedBy").getAsString()));
        }

        if (!object.get("pardonedAt").isJsonNull()) {
            punishment.setPardonedAt(object.get("pardonedAt").getAsLong());
        }

        if (!object.get("pardonedReason").isJsonNull()) {
            punishment.setPardonedReason(object.get("pardonedReason").getAsString());
        }

        if (!object.get("pardoned").isJsonNull()) {
            punishment.setPardoned(object.get("pardoned").getAsBoolean());
        }

        return punishment;
    }
}
