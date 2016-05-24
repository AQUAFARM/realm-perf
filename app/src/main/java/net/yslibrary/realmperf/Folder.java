package net.yslibrary.realmperf;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by shimizu_yasuhiro on 2016/05/24.
 */
public class Folder extends RealmObject {

    @PrimaryKey
    public long id;

    public RealmList<Note> notes;

    public Folder() {

    }

    public Folder(long id, List<Note> notes) {
        this.id = id;
        this.notes = new RealmList<>();
        this.notes.addAll(notes);
    }
}
