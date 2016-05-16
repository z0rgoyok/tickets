package com.zabozhanov.tickets.models;

import android.util.Log;

import com.zabozhanov.tickets.TicketApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmResults;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

/**
 * Created by z0rgoyok on 14.05.16.
 */
public class TicketCSVParser {

    public interface ITickerListener {
        void progress(int progress);

        void finish();

        void error(Throwable error);
    }

    private RealmAsyncTask transaction;

    public void parseFile(final String path, final ITickerListener listener) {
        final Realm realm = Realm.getDefaultInstance();
        listener.progress(0);

        RealmResults<Event> events = realm.where(Event.class).findAll();
        final Map<String, Event> eventsMap = new HashMap<>();
        for (int i = 0; i < events.size(); i++) {
            eventsMap.put(events.get(i).getName(), events.get(i));
        }

        final long start = System.nanoTime();

        transaction = realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                File file = new File(path);
                long fileSize = file.length();
                try {
                    long readBytes = 0;
                    BufferedReader br = new BufferedReader(new FileReader(new File(path)));
                    for (String line; (line = br.readLine()) != null; ) {

                        readBytes += line.getBytes().length;

                        int percent = (int) ((readBytes * 100.0f) / fileSize);
                        //Log.d("READ", "Read: " + readBytes + ", total: " + file + ", percentage: " + percent);
                        String[] fields = line.split(";");

                        Ticket ticket = null;
                        try {
                            ticket = realm.createObject(Ticket.class);
                            ticket.setId(Long.parseLong(fields[0]));
                        } catch (RealmPrimaryKeyConstraintException exception) {
                            continue;
                        }
                        ticket.setCost(Integer.parseInt(fields[1]));
                        ticket.setDirection(fields[2]);
                        ticket.setType(fields[3]);

                        String eventName = fields[4];
                        Event event = eventsMap.get(eventName);
                        if (event == null) {
                            event = realm.createObject(Event.class);
                            event.setName(eventName);
                            eventsMap.put(eventName, event);
                        } else {
                            //Log.d("READ", "Event exists");
                        }
                        ticket.setEvent(event);
                        event.getTickets().add(ticket);
                        listener.progress(percent);
                    }
                    //listener.finish();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    listener.error(ex);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                realm.close();
                long elapsedTime = System.nanoTime() - start;

                Log.d("READ", "Pasing time: " + elapsedTime);
                listener.finish();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                realm.close();
                listener.error(error);
            }
        });


    }

    //todo: проверить вызывается ли слушатель реалмасинктаска
    public void cancel() {
        if (transaction != null && !transaction.isCancelled()) {
            transaction.cancel();
        }
    }
}
