package com.zabozhanov.tickets;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.zabozhanov.tickets.models.TicketCSVParser;

import org.greenrobot.eventbus.EventBus;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class ParseService extends Service {

    public class ParseServiceEvent {
        public String path;
        public int progress;
        public boolean error;
        public boolean finished;

        public ParseServiceEvent(String path, int progress, boolean error) {
            this.path = path;
            this.progress = progress;
            this.error = error;
        }

        public ParseServiceEvent(int progress, String path) {
            this.progress = progress;
            this.path = path;
        }

        public ParseServiceEvent(boolean error, boolean finished) {
            this.error = error;
            this.finished = finished;
        }

    }

    public static final String ACTION_PARSE = "com.zabozhanov.tickets.action.PARSE";
    public static final String EXTRA_PATH = "com.zabozhanov.tickets.extra.PATH";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PARSE.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PATH);
                handleParse(param1);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleParse(final String path) {
        TicketCSVParser parser = new TicketCSVParser();
        parser.parseFile(path, new TicketCSVParser.ITickerListener() {

            @Override
            public void progress(int progress) {
                postSticky(new ParseServiceEvent(progress, path));
            }

            @Override
            public void finish() {
                postSticky(new ParseServiceEvent(false, true));
                ParseService.this.stopSelf();
            }

            @Override
            public void error(Throwable error) {
                postSticky(new ParseServiceEvent(true, true));
                ParseService.this.stopSelf();
            }

            private void postSticky(ParseServiceEvent event) {
                ParseServiceEvent lastEvent = EventBus.getDefault().getStickyEvent(ParseServiceEvent.class);
                if (lastEvent != null) {
                    EventBus.getDefault().removeStickyEvent(ParseServiceEvent.class);
                }
                EventBus.getDefault().postSticky(event);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
