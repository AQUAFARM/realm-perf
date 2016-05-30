package net.yslibrary.realmperf.pojo;

import net.yslibrary.realmperf.Note;

/**
 * Created by shimizu_yasuhiro on 2016/05/26.
 */
public class NoteDto {

    public long id;

    public String note;

    public String note2;

    public long createdAt;

    public NoteDto(Note note) {
        id = note.id;
        this.note = note.note;
        note2 = note.note2;
        createdAt = note.createdAt;
    }
}
