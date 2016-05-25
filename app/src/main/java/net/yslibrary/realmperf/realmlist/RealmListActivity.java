package net.yslibrary.realmperf.realmlist;

import net.yslibrary.realmperf.App;
import net.yslibrary.realmperf.Folder;
import net.yslibrary.realmperf.Note;
import net.yslibrary.realmperf.R;
import net.yslibrary.realmperf.TestBaseActivity;
import net.yslibrary.realmperf.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.internal.OutOfMemoryError;
import timber.log.Timber;

public class RealmListActivity extends TestBaseActivity {

    private Realm realm;

    private Adapter adapter;

    public static Intent getIntent(Context context, long nextId) {
        Intent intent = new Intent(context.getApplicationContext(), RealmListActivity.class);
        intent.putExtra(BUNDLE_NEXT_ID, nextId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();

        nextId = getIntent().getLongExtra(BUNDLE_NEXT_ID, 0L);
        if (nextId > 0) {
            long current = App.get(this).folderIdCounter.get();
            if (nextId == current) {
                nextId = 0;
            }
        }

        long start = System.currentTimeMillis();
        try {
            Folder folder = realm.where(Folder.class).equalTo("id", nextId).findFirst();
            if (folder == null) {
                while (realm.where(Folder.class).equalTo("id", nextId).count() == 0) {
                    nextId++;
                }
                folder = realm.where(Folder.class).equalTo("id", nextId).findFirst();
            }

            adapter = new Adapter(folder.notes, nextId);
            binding.list.setLayoutManager(new LinearLayoutManager(this));
            binding.list.setAdapter(adapter);
        } catch (Throwable t) {
            if (t instanceof OutOfMemoryError || t instanceof java.lang.OutOfMemoryError) {
                Timber.e("OutOfMemoryError occurred! - activity count: %d", nextId);
            } else {
                Timber.e(t, t.getMessage());
            }
            throw t;
        }
        long time = System.currentTimeMillis() - start;
        long count = App.get(this).realmListActivityCount.incrementAndGet();
        Timber.d("RealmListActivity in %d millis, count - %d", time, count);
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.getRoot().postDelayed(() -> {
            startActivity(getIntent(this, nextId + 1));
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private final RealmList<Note> dataSet;

        private final long nextId;

        public Adapter(RealmList<Note> dataSet, long nextId) {
            this.dataSet = dataSet;
            this.nextId = nextId;
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

            holder.binding.getRoot().setOnClickListener(view -> {
                view.getContext()
                        .startActivity(RealmListActivity.getIntent(view.getContext(), nextId));
            });
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }
    }
}
