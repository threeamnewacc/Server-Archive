package me.devkevin.landcore.player.notes.json;

import com.google.gson.JsonObject;
import me.devkevin.landcore.player.notes.Note;
import me.devkevin.landcore.utils.json.JsonSerializer;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 23/01/2023 @ 2:38
 * NoteJsonSerializer / me.devkevin.landcore.player.notes.json / LandCore
 */
public class NoteJsonSerializer implements JsonSerializer<Note> {
    @Override
    public JsonObject serialize(Note note) {
        JsonObject object = new JsonObject();

        object.addProperty("id", note.getId());
        object.addProperty("createAt", note.getCreateAt());
        object.addProperty("createBy", note.getCreateBy());
        object.addProperty("note", note.getNote());

        if (note.getCreateBy() != null) {
            object.addProperty("UpdateBy", note.getUpdateBy());
            object.addProperty("UpdateAt", note.getUpdateAt());
        }

        return object;
    }
}
