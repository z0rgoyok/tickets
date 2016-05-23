package com.zabozhanov.tickets.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.zabozhanov.tickets.R;
import com.zabozhanov.tickets.fragments.EventsFragment;
import com.zabozhanov.tickets.models.Event;
import com.zabozhanov.tickets.models.Ticket;
import com.zabozhanov.tickets.services.ParseService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog parsingProgressDialog;

    //todo: для тестирования обновляемой выборки
    //@BindView(R.id.txt_tickets_count) TextView txtTicketsCount;

    private Realm realm;

    public Realm getRealm() {
        return realm;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.argb(255, 0, 0, 0));
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        realm = Realm.getDefaultInstance();
        RealmResults<Ticket> tickets = realm.where(Ticket.class).findAll();
        RealmResults<Event> events = realm.where(Event.class).findAll();
        //txtTicketsCount.setText("Items count: " + tickets.size() + ", events: " + events.size());

        EventsFragment fragment = EventsFragment.newInstance();
        changeFragment(fragment);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_events_list) {

        } else if (id == R.id.nav_import_data) {
            Intent intent = new Intent(this, ParseService.class);
            intent.setAction(ParseService.ACTION_PARSE);
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/test.csv";
            File f = new File(path);
            intent.putExtra(ParseService.EXTRA_PATH, path);
            startService(intent);
            return false;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(ParseService.ParseServiceEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        if (event.finished) {
            if (parsingProgressDialog != null) {
                parsingProgressDialog.dismiss();
                parsingProgressDialog = null;
            }
            if (event.error) {
                Toast.makeText(this, R.string.data_parsing_error, Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, R.string.data_parsing_finished, Toast.LENGTH_SHORT).show();


            RealmResults<Ticket> tickets = realm.where(Ticket.class).findAll();
            RealmResults<Event> events = realm.where(Event.class).findAll();
            //txtTicketsCount.setText("Items count: " + tickets.size() + ", events: " + events.size());

            return;
        }
        if (parsingProgressDialog == null) {
            createParsingDialog();
        }
        parsingProgressDialog.setMax(100);
        parsingProgressDialog.setProgress(event.progress);
    }

    private void createParsingDialog() {
        parsingProgressDialog = new ProgressDialog(this);
        parsingProgressDialog.setTitle(getString(R.string.parsing_data_title));
        parsingProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        parsingProgressDialog.show();
    }


    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
    }

    public void pushFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(fragmentManager.toString()).commit();
    }
}
