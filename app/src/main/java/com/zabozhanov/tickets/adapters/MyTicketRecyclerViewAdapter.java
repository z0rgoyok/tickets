package com.zabozhanov.tickets.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zabozhanov.tickets.R;
import com.zabozhanov.tickets.fragments.TicketsFragment;
import com.zabozhanov.tickets.models.TicketScanResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

public class MyTicketRecyclerViewAdapter extends RecyclerView.Adapter<MyTicketRecyclerViewAdapter.ViewHolder> {

    private RealmResults<TicketScanResult> mValues;

    public RealmResults<TicketScanResult> getmValues() {
        return mValues;
    }

    public void setmValues(RealmResults<TicketScanResult> mValues) {
        this.mValues = mValues;
    }

    private TicketsFragment.OnListFragmentInteractionListener mListener;

    public MyTicketRecyclerViewAdapter(RealmResults<TicketScanResult> items, TicketsFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    public MyTicketRecyclerViewAdapter(TicketsFragment.OnListFragmentInteractionListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ticket_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = mValues.get(position);

        holder.txtTicketNumber.setText(String.valueOf(holder.item.getTicket().getId()));

        /*holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);*/

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues == null ? 0 : mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TicketScanResult item;
        public View mView;
        @BindView(R.id.container)
        public View mContainer;
        @BindView(R.id.txtTicketNumber)
        public TextView txtTicketNumber;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }
}
