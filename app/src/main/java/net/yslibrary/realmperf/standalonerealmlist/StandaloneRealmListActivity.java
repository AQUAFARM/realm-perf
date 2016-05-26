package net.yslibrary.realmperf.standalonerealmlist;

import net.yslibrary.realmperf.App;
import net.yslibrary.realmperf.Folder;
import net.yslibrary.realmperf.TestBaseActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;

import io.realm.internal.OutOfMemoryError;
import timber.log.Timber;

public class StandaloneRealmListActivity extends TestBaseActivity {

    public static Intent getIntent(Context context, long nextId) {
        Intent intent = new Intent(context.getApplicationContext(),
                StandaloneRealmListActivity.class);
        intent.putExtra(BUNDLE_NEXT_ID, nextId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

            // Managed realm object to standalone realm object
            adapter = new Adapter(realm.copyFromRealm(folder.notes));
            binding.list.setLayoutManager(
                    new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
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
        Timber.d("StandaloneRealmListActivity in %d millis, count - %d", time, count);
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.getRoot().postDelayed(() -> {
            startActivity(getIntent(this, nextId + 1));
        }, 500);
    }
}
