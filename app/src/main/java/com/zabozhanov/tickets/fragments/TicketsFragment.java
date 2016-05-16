package com.zabozhanov.tickets.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zabozhanov.tickets.R;
import com.zabozhanov.tickets.adapters.MyTicketRecyclerViewAdapter;
import com.zabozhanov.tickets.models.Event;
import com.zabozhanov.tickets.models.TicketScanResult;
import com.zabozhanov.tickets.services.FakeScanResultsService;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class TicketsFragment extends BaseFragment {

    private static final String ARG_EVENTNAME = "ARG_EVENTNAME";
    private OnListFragmentInteractionListener mListener;

    private Event event;
    private String eventName;
    private RealmResults<TicketScanResult> tickets;
    private MyTicketRecyclerViewAdapter adapter;

    public TicketsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TicketsFragment newInstance(String eventName) {
        TicketsFragment fragment = new TicketsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENTNAME, eventName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            eventName = getArguments().getString(ARG_EVENTNAME);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_list, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new MyTicketRecyclerViewAdapter(new OnListFragmentInteractionListener() {
                @Override
                public void onListFragmentInteraction(TicketScanResult item) {

                }
            });
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.event = getMainActivity().getRealm().where(Event.class).equalTo("name", eventName).findFirst();
        tickets = getMainActivity().getRealm().where(TicketScanResult.class).equalTo("ticket.event.name", eventName).findAllAsync();
        reloadData(tickets);
        tickets.addChangeListener(new RealmChangeListener<RealmResults<TicketScanResult>>() {
            @Override
            public void onChange(RealmResults<TicketScanResult> element) {
                reloadData(element);
            }
        });

        Intent intent = new Intent(getMainActivity(), FakeScanResultsService.class);
        intent.setAction(FakeScanResultsService.ACTION_INSERT_FAKE_RESULT);
        intent.putExtra(FakeScanResultsService.EXTRA_EVENT, eventName);
        getMainActivity().startService(intent);
    }

    private void reloadData(RealmResults<TicketScanResult> items) {
        adapter.setmValues(items);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tickets.removeChangeListeners();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = new OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(TicketScanResult item) {

            }
        };
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(TicketScanResult item);
    }
}
