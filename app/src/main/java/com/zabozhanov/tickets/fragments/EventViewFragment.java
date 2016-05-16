package com.zabozhanov.tickets.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zabozhanov.tickets.R;
import com.zabozhanov.tickets.models.Event;
import com.zabozhanov.tickets.models.Ticket;
import com.zabozhanov.tickets.models.TicketScanResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class EventViewFragment extends BaseFragment {

    private static final String ARG_EVENTNAME = "ARG_EVENTNAME";

    private Event event;

    @BindView(R.id.txtEventName)
    public TextView txtEventName;

    @BindView(R.id.txtEventTimePlace)
    public TextView txtEventTimePlace;

    @BindView(R.id.txtTicketsCount)
    public TextView txtTicketsCount;

    @BindView(R.id.txtAddress)
    public TextView txtAddress;

    private OnFragmentInteractionListener mListener;
    private String eventName;

    private RealmResults<TicketScanResult> scanResults;

    public EventViewFragment() {
    }

    public static EventViewFragment newInstance(String eventName) {
        EventViewFragment fragment = new EventViewFragment();
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
        View view = inflater.inflate(R.layout.fragment_event_view, container, false);
        view.findViewById(R.id.btnStartChecking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMainActivity().pushFragment(TicketsFragment.newInstance(eventName));
            }
        });
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.event = getMainActivity().getRealm().where(Event.class).equalTo("name", eventName).findFirst();

        scanResults = getMainActivity().getRealm().where(TicketScanResult.class).
                equalTo("ticket.event.name", eventName).equalTo("ticketScanResult", Ticket.STATE_IN).findAllAsync();
        scanResults.addChangeListener(new RealmChangeListener<RealmResults<TicketScanResult>>() {
            @Override
            public void onChange(RealmResults<TicketScanResult> element) {
                txtTicketsCount.setText(String.format(getMainActivity().getString(R.string.sold_number_tickets), element.size()));
            }
        });

        Log.d("tag", event.getName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scanResults.removeChangeListeners();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = new OnFragmentInteractionListener() {
            @Override
            public void onFragmentInteraction(Uri uri) {

            }
        };
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
