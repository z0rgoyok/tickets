package com.zabozhanov.tickets.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.zabozhanov.tickets.models.Event;
import com.zabozhanov.tickets.models.Ticket;
import com.zabozhanov.tickets.models.TicketScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Handler;

import io.realm.Realm;

public class FakeScanResultsService extends Service {

    private Realm realm;
    private int insertedCount;

    public FakeScanResultsService() {
    }

    public static final String ACTION_INSERT_FAKE_RESULT = "com.zabozhanov.tickets.action.INSERT_FAKE_RESULT";
    public static final String EXTRA_EVENT = "com.zabozhanov.tickets.extra.EVENT";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        realm = Realm.getDefaultInstance();
        final String eventName = intent.getStringExtra(EXTRA_EVENT);
        Event event = realm.where(Event.class).equalTo("name", eventName).findFirst();
        final List<Ticket> tickets = new ArrayList<>();

        int size = event.getTickets().size();

        for (int i = 0; i < 10; i++) {
            int randomIndex = Math.abs(new Random().nextInt()) % size;
            Ticket ticket = event.getTickets().get(randomIndex);
            if (ticket != null) {
                tickets.add(ticket);
            }
        }

        final android.os.Handler handler = new android.os.Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                realm.beginTransaction();
                TicketScanResult result = realm.createObject(TicketScanResult.class);
                result.setTicket(tickets.get(0));
                tickets.remove(0);
                result.setTicketScanResult(Ticket.STATE_IN);
                realm.commitTransaction();
                if (tickets.size() > 0) {
                    handler.postDelayed(this, 1500);
                } else {
                    realm.close();
                }
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }
}
