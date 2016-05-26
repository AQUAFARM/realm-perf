package net.yslibrary.realmperf.standalonelist;

import net.yslibrary.realmperf.App;
import net.yslibrary.realmperf.Note;
import net.yslibrary.realmperf.TestBaseActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.internal.OutOfMemoryError;
import timber.log.Timber;

public class StandaloneListActivity extends TestBaseActivity {

    public static Intent getIntent(Context context, long nextId) {
        Intent intent = new Intent(context.getApplicationContext(), StandaloneListActivity.class);
        intent.putExtra(BUNDLE_NEXT_ID, nextId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nextId = getIntent().getLongExtra(BUNDLE_NEXT_ID, 0L);
        if (nextId > 0) {
            long current = App.get(this).noteIdCounter.get();
            if (nextId == current) {
                nextId = 0;
            }
        }

        long start = System.currentTimeMillis();
        try {
            List<Note> notes = new ArrayList<>();
            long max = App.get(this).noteIdCounter.get();
            long current = nextId;
            long i = 0;
            for (; i < 200; ) {
                Note note = realm.where(Note.class).equalTo("id", current).findFirst();
                if (note == null) {
                    if (current >= max) {
                        current = 0;
                    } else {
                        current++;
                    }
                } else {
                    // Managed realm object to standalone realm object
                    notes.add(realm.copyFromRealm(note));
                    i++;
                    current++;
                }
            }
            adapter = new Adapter(notes);
            binding.list.setLayoutManager(
                    new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            binding.list.setAdapter(adapter);
            nextId = current;

        } catch (Throwable t) {
            if (t instanceof OutOfMemoryError || t instanceof java.lang.OutOfMemoryError) {
                Timber.e("OutOfMemoryError occurred! - activity count: %d", nextId);
            } else {
                Timber.e(t, t.getMessage());
            }
            throw t;
        }
        long time = System.currentTimeMillis() - start;
        long count = App.get(this).listActivityCount.incrementAndGet();
        Timber.d("ListActivity in %d millis count - %d", time, count);
    }


    @Override
    protected void onResume() {
        super.onResume();
        binding.getRoot().postDelayed(() -> {
            startActivity(getIntent(this, nextId));
        }, 500);
    }
}
