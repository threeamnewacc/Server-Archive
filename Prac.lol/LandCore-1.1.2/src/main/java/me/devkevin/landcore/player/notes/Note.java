package me.devkevin.landcore.player.notes;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.devkevin.landcore.player.notes.json.NoteJsonDeSerializer;
import me.devkevin.landcore.player.notes.json.NoteJsonSerializer;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 23/01/2023 @ 2:37
 * Note / me.devkevin.landcore.player.notes / LandCore
 */
@Getter
@Setter
@NoArgsConstructor
public class Note {
    public static NoteJsonSerializer SERIALIZER = new NoteJsonSerializer();
    public static NoteJsonDeSerializer DESERIALIZER = new NoteJsonDeSerializer();

    private int id;
    private long createAt;
    private String createBy;
    private String updateBy;
    private long updateAt;
    private String note;

    public Note(int id, String note, Player staff) {
        this.id = id;
        this.note = note;
        this.createAt = System.currentTimeMillis();
        this.createBy = staff.getName();
    }
}
