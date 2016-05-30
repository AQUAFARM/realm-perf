package net.yslibrary.realmperf;

import net.yslibrary.realmperf.databinding.ActivityTestBaseBinding;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import timber.log.Timber;

/**
 * Created by shimizu_yasuhiro on 2016/05/25.
 */
public class TestBaseActivity extends AppCompatActivity {

    public static final String BUNDLE_NEXT_ID = "next_id";

    protected Realm realm;

    protected ActivityTestBaseBinding binding;

    protected Adapter adapter;

    protected long nextId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test_base);
        realm = Realm.getDefaultInstance();
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
        realm.close();
    }


    public static class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private final List<Note> dataSet;

        public Adapter() {
            this(new ArrayList<>());
        }

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

        public void addAll(List<Note> dataSet) {
            this.dataSet.addAll(dataSet);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }
    }
}
