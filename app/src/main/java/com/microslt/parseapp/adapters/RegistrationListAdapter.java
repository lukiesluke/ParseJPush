package com.microslt.parseapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.microslt.parseapp.R;
import com.microslt.parseapp.model.ListData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MicroSLTAdmin on 10/6/2017.
 */

public class RegistrationListAdapter extends RecyclerView.Adapter<RegistrationListAdapter.MyViewHolder> {
    private LayoutInflater layoutInflater;
    List<ListData> listData = new ArrayList<>();
    public static Context context;

    public RegistrationListAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        layoutInflater = LayoutInflater.from(context);
    }

    public void setListRegistration(List<ListData> listData) {
        this.listData = listData;
        notifyItemRangeChanged(0, listData.size());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.custom_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ListData current = listData.get(position);
        holder.title.setText(current.title);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.listText);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
