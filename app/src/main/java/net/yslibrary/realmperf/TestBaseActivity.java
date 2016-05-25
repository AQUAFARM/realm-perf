package net.yslibrary.realmperf;

import net.yslibrary.realmperf.databinding.ActivityTestBaseBinding;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import timber.log.Timber;

/**
 * Created by shimizu_yasuhiro on 2016/05/25.
 */
public class TestBaseActivity extends AppCompatActivity {

    public static final String BUNDLE_NEXT_ID = "next_id";

    protected ActivityTestBaseBinding binding;

    protected long nextId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test_base);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.d("onStart - %s", this.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.d("onResume - %s", this.toString());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Timber.d("onPause - %s", this.toString());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Timber.d("onStop - %s", this.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.d("onDestroy - %s", this.toString());
    }
}
