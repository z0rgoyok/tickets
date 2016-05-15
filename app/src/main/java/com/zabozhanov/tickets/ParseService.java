package com.zabozhanov.tickets;

import android.app.IntentService;
import android.content.Intent;

import com.zabozhanov.tickets.models.TicketCSVParser;

import org.greenrobot.eventbus.EventBus;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class ParseService extends IntentService {

    public class ParseServiceEvent {
        public String path;
        public int progress;
        public boolean error;
        public boolean finished;
        public long size;
        public int parsed;

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

        public ParseServiceEvent(int progress, String path, long size, int parsed) {
            this.progress = progress;
            this.path = path;
            this.size = size;
            this.parsed = parsed;
        }
    }

    public static final String ACTION_PARSE = "com.zabozhanov.tickets.action.PARSE";
    public static final String EXTRA_PATH = "com.zabozhanov.tickets.extra.PATH";

    public ParseService() {
        super("ParseService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PARSE.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PATH);
                handleParse(param1);
            }
        }
    }

    private void handleParse(final String path) {
        TicketCSVParser parser = new TicketCSVParser();
        parser.parseFile(path, new TicketCSVParser.ITickerListener() {

            @Override
            public void progress(int progress, long total, int count) {
                postSticky(new ParseServiceEvent(progress, path, total, count));
            }

            @Override
            public void finish() {
                postSticky(new ParseServiceEvent(false, true));
            }

            @Override
            public void error(Throwable error) {
                postSticky(new ParseServiceEvent(true, true));
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

}
