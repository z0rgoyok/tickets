package com.zabozhanov.tickets.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.zabozhanov.tickets.models.Ticket;

import io.realm.RealmResults;

/**
 * Created by z0rgoyok on 15.05.16.
 */
public class TicketsAdapter extends RecyclerView.Adapter<TicketsAdapter.ViewHolder> {

    private RealmResults<Ticket> data;

    public RealmResults<Ticket> getData() {
        return data;
    }

    public void setData(RealmResults<Ticket> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {




        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
