package net.yslibrary.realmperf;


import net.yslibrary.realmperf.databinding.ActivityMainBinding;
import net.yslibrary.realmperf.event.DataPrepared;
import net.yslibrary.realmperf.list.ListActivity;
import net.yslibrary.realmperf.realmlist.RealmListActivity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.realm.Realm;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    private Realm realm;

    private ActivityMainBinding binding;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        realm = Realm.getDefaultInstance();

        binding.noteCount.setText(String.valueOf(realm.where(Note.class).count()));

        binding.toRealmList.setEnabled(false);
        binding.toList.setEnabled(false);

        binding.toRealmList.setOnClickListener(view -> {
            startActivity(RealmListActivity.getIntent(this, 0));
        });

        binding.toList.setOnClickListener(view -> {
            startActivity(ListActivity.getIntent(this, 0));
        });

        Subscription subscription = App.get(this).bus.on(DataPrepared.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    binding.requiredTime.setText(
                            String.format("%d milli seconds required to insert new data",
                                    event.millis));
                    binding.noteCount.setText(String.valueOf(realm.where(Note.class).count()));
                    binding.toRealmList.setEnabled(true);
                    binding.toList.setEnabled(true);
                });

        subscriptions.add(subscription);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscriptions.clear();
        realm.close();
    }
}
