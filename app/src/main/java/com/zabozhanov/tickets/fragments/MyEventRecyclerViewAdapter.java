package com.zabozhanov.tickets.fragments;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zabozhanov.tickets.R;
import com.zabozhanov.tickets.models.Event;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

public class MyEventRecyclerViewAdapter extends RecyclerView.Adapter<MyEventRecyclerViewAdapter.ViewHolder> {

    private RealmResults<Event> mValues;

    public RealmResults<Event> getmValues() {
        return mValues;
    }

    public void setmValues(RealmResults<Event> mValues) {
        this.mValues = mValues;
    }

    private final EventFragment.OnListFragmentInteractionListener mListener;

    public MyEventRecyclerViewAdapter(EventFragment.OnListFragmentInteractionListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.txtEventName.setText(holder.mItem.getName());
        //holder.mContentView.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues != null ? mValues.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;

        @BindView(R.id.container)
        public View mContainer;

        @BindView(R.id.txtEventName)
        public TextView txtEventName;

        @BindView(R.id.txtEventTimePlace)
        public TextView txtEventTimePlace;

        @BindView(R.id.txtTicketsCount)
        public TextView txtTicketsCount;

        @BindView(R.id.txtBegins)
        public TextView txtBegins;

        public Event mItem;

        public ViewHolder(View view) {
            super(view);
            this.mView = view;
            ButterKnife.bind(this, view);
        }
    }
}
