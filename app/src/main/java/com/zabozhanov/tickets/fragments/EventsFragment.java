package com.zabozhanov.tickets.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zabozhanov.tickets.R;
import com.zabozhanov.tickets.adapters.MyEventRecyclerViewAdapter;
import com.zabozhanov.tickets.models.Event;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class EventsFragment extends BaseFragment {
    private OnListFragmentInteractionListener mListener;
    private MyEventRecyclerViewAdapter adapter;

    public EventsFragment() {
    }

    private Realm realm;

    @SuppressWarnings("unused")
    public static EventsFragment newInstance() {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new MyEventRecyclerViewAdapter(mListener);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        realm = Realm.getDefaultInstance();

        RealmResults<Event> events = realm.where(Event.class).findAllAsync();
        reloadData(events);
        events.addChangeListener(new RealmChangeListener<RealmResults<Event>>() {
            @Override
            public void onChange(RealmResults<Event> element) {
                reloadData(element);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mListener = new OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(Event item) {
                getMainActivity().pushFragment(EventViewFragment.newInstance(item.getName()));
            }
        };
    }

    private void reloadData(RealmResults<Event> items) {
        adapter.setmValues(items);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        realm.removeAllChangeListeners();
        realm.close();
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Event item);
    }
}
