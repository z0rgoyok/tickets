package com.zabozhanov.tickets.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;

import io.realm.Realm;
import io.realm.RealmAsyncTask;

/**
 * Created by z0rgoyok on 14.05.16.
 */
public class TicketCSVParser {

    public interface ITickerListener {
        void progress(int progress, long total, int count);

        void finish();

        void error(Throwable error);
    }

    private RealmAsyncTask transaction;

    public void parseFile(final String path, final ITickerListener listener) {
        final Realm realm = Realm.getDefaultInstance();
        transaction = realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                File file = new File(path);
                realm.beginTransaction();
                try {
                    LineNumberReader lnr = new LineNumberReader(new FileReader(file));
                    lnr.skip(Long.MAX_VALUE);
                    long linesCount = lnr.getLineNumber() + 1;
                    lnr.close();
                    int lineIndex = 0;
                    BufferedReader br = new BufferedReader(new FileReader(new File(path)));
                    for (String line; (line = br.readLine()) != null; ) {
                        int percent = (int) ((lineIndex * 100.0f) / linesCount);
                        String[] fields = line.split(";");

                        Ticket ticket = new Ticket();
                        ticket.setId(Long.parseLong(fields[0]));
                        ticket.setCost(Integer.parseInt(fields[1]));
                        ticket.setType(fields[2]);
                        ticket.setEvent(fields[3]);
                        realm.copyToRealmOrUpdate(ticket);
                        lineIndex++;
                        listener.progress(percent, linesCount, lineIndex);
                    }
                    listener.finish();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    listener.error(ex);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                realm.close();
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
