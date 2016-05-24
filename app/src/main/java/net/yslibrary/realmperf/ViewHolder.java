package net.yslibrary.realmperf;

import net.yslibrary.realmperf.databinding.ItemListBinding;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by shimizu_yasuhiro on 2016/05/24.
 */
public class ViewHolder extends RecyclerView.ViewHolder {

    public final ItemListBinding binding;

    public ViewHolder(View itemView) {
        super(itemView);

        binding = DataBindingUtil.bind(itemView);
    }
}
