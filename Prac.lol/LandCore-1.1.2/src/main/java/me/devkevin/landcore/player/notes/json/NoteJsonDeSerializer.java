package me.devkevin.landcore.player.notes.json;

import com.google.gson.JsonObject;
import me.devkevin.landcore.player.notes.Note;
import me.devkevin.landcore.utils.json.JsonDeserializer;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 23/01/2023 @ 2:40
 * NoteJsonDeSerializer / me.devkevin.landcore.player.notes.json / LandCore
 */
public class NoteJsonDeSerializer implements JsonDeserializer<Note> {
    @Override
    public Note deserialize(JsonObject object) {
        Note note = new Note();

        note.setId(object.get("id").getAsInt());
        note.setCreateAt(object.get("createAt").getAsInt());
        note.setCreateBy(object.get("createBy").getAsString());
        note.setNote(object.get("note").getAsString());

        if (object.get("UpdateBy") != null) {
            note.setUpdateBy(object.get("createBy").getAsString());
            note.setUpdateAt(object.get("createAt").getAsInt());
        }

        return note;
    }
}
