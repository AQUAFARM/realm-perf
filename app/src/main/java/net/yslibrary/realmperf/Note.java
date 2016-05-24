package net.yslibrary.realmperf;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by shimizu_yasuhiro on 2016/05/24.
 */
public class Note extends RealmObject {

    @PrimaryKey
    public long id;

    public String note;

    public String note2;

    public long createdAt;

    public Note() {
    }

    public Note(long id, String note, String note2, long createdAt) {
        this.id = id;
        this.note = note;
        this.note2 = note2;
        this.createdAt = createdAt;
    }
}
