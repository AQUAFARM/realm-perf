package net.yslibrary.realmperf.list;

import net.yslibrary.realmperf.App;
import net.yslibrary.realmperf.Note;
import net.yslibrary.realmperf.R;
import net.yslibrary.realmperf.TestBaseActivity;
import net.yslibrary.realmperf.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.internal.OutOfMemoryError;
import timber.log.Timber;

public class ListActivity extends TestBaseActivity {

    private Realm realm;

    private Adapter adapter;

    public static Intent getIntent(Context context, long nextId) {
        Intent intent = new Intent(context.getApplicationContext(), ListActivity.class);
        intent.putExtra(BUNDLE_NEXT_ID, nextId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();

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
                    notes.add(note);
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private final List<Note> dataSet;

        public Adapter(List<Note> dataSet) {
            this.dataSet = dataSet;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Note note = dataSet.get(position);
            holder.binding.id.setText(String.valueOf(note.id));
            holder.binding.note1.setText(note.note);
            holder.binding.note2.setText(note.note2);

            int res = position % 2 > 0 ? R.drawable.lorem_ipsum_1 : R.drawable.lorem_ipsum_2;
            holder.binding.header.setImageResource(res);
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }
    }
}
