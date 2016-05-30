package net.yslibrary.realmperf.standalonelist;

import net.yslibrary.realmperf.App;
import net.yslibrary.realmperf.Note;
import net.yslibrary.realmperf.TestBaseActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.internal.OutOfMemoryError;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

public class StandaloneListActivity extends TestBaseActivity {

    Subscription subscription = Subscriptions.empty();

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
        subscription = Observable.fromCallable(() -> {
            Realm _realm = Realm.getDefaultInstance();
            List<Note> notes = new ArrayList<>();
            long max = App.get(this).noteIdCounter.get();
            long current = nextId;
            long i = 0;
            for (; i < 200; ) {
                Note note = _realm.where(Note.class).equalTo("id", current).findFirst();
                if (note == null) {
                    if (current >= max) {
                        current = 0;
                    } else {
                        current++;
                    }
                } else {
                    // Managed realm object to standalone realm object
                    notes.add(_realm.copyFromRealm(note));
                    i++;
                    current++;
                }
            }
            _realm.close();
            return new Pair<List<Note>, Long>(notes, current);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(t -> {
                    if (t instanceof OutOfMemoryError || t instanceof java.lang.OutOfMemoryError) {
                        Timber.e("OutOfMemoryError occurred! - activity count: %d",
                                App.get(this).standaloneListActivityCount.get());
                    } else {
                        Timber.e(t, t.getMessage());
                    }
                })
                .subscribe(info -> {
                    adapter.addAll(info.first);
                    nextId = info.second;

                    long time = System.currentTimeMillis() - start;
                    long count = App.get(this).standaloneListActivityCount.incrementAndGet();
                    Timber.d("ListActivity in %d millis count - %d", time, count);
                });

        adapter = new Adapter();
        binding.list.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.list.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        binding.getRoot().postDelayed(() -> {
            startActivity(getIntent(this, nextId));
        }, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
    }
}
