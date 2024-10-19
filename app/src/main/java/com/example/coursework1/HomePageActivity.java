package com.example.coursework1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import android.content.Loader;
import android.content.CursorLoader;
import android.widget.SimpleCursorAdapter;

import com.example.coursework1.data.TripDetailsContract;
import com.example.coursework1.data.TripDetailsDBHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class HomePageActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {
    private static final int ADD_TRIP_REQUEST_CODE = 3;
    private ShareActionProvider myShareActionProvider;
    SimpleCursorAdapter adapter;

    ListView tripListView;

    private static final int TRIP_DETAILS_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //set up tool bar and floating action buttons
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton addTripFAB = (FloatingActionButton) findViewById(R.id.addTripButton);
        addTripFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePageActivity.this, AddTripActivity.class);
                startActivityForResult(intent, ADD_TRIP_REQUEST_CODE);

            }
        });

        ///initiate the loader
        getLoaderManager().initLoader(TRIP_DETAILS_LOADER, null, this);

        //create simple cursor adapter
        adapter = new SimpleCursorAdapter(
                getApplicationContext(),
                R.layout.list_item,
                null,
                new String[]{TripDetailsContract.TripDetailsTable.COLUMN_TRIP_DESTINATION,
                        TripDetailsContract.TripDetailsTable.COLUMN_TRIP_ARRIVAL_DATE,
                        TripDetailsContract.TripDetailsTable._ID,
                        TripDetailsContract.TripDetailsTable.COLUMN_TRIP_DEPARTURE_DATE,
                        TripDetailsContract.TripDetailsTable.COLUMN_TRIP_BUDGET,
                        TripDetailsContract.TripDetailsTable.COLUMN_TRIP_ACCOMODATION,
                        TripDetailsContract.TripDetailsTable.COLUMN_TRIP_TRANSPORT,
                        TripDetailsContract.TripDetailsTable.COLUMN_TRIP_IMAGE},
                new int[]{R.id.trip_destination,
                        R.id.trip_start_date,
                        R.id.trip_id,
                        R.id.trip_end_date,
                        R.id.trip_budget,
                        R.id.trip_accommodation,
                        R.id.trip_transport,
                        R.id.trip_image},
                0
        );
        tripListView = (ListView) findViewById(R.id.tripListView);
        tripListView.setAdapter(adapter);

        // create notifications channel
        createNotificationsChannel();

        Log.i("CREATE", "onCreate: ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Retrieve the share menu item
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);

        //Create a share action provider
        myShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        //set the ShareIntent
        if (myShareActionProvider != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, R.string.share_text);
            shareIntent.setType("text/plain");
            myShareActionProvider.setShareIntent(shareIntent);
        }

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivityForResult(intent, 2);// Activity is started with requestCode 2
        }

        if(id == R.id.action_open_user_guide) {
            Intent intent = new Intent(this, UserGuideActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    // handle lifecycle events
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Home", "ON START");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Home", "ON RESUME");

        //get shared preferences
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_key), MODE_PRIVATE);
        String name = prefs.getString("user_name", "");

        //set name in displayName text view////////////////////////////////////////
        TextView displayNameTextView = findViewById(R.id.displayNameTextView);
        displayNameTextView.setText("Welcome " + name + "!");

        // set visibility of no trips text view to gone if there are trips
        TextView noTripsTextView = findViewById(R.id.noTripsTextView);
        TextView here_are_planned_trips = findViewById(R.id.here_are_planned_trips);
        // if no trips message is visible and the shared prefs trips flag is set to true, set no trips message to gone
        String anyTrips = getSharedPreferences(getString(R.string.preferences_key), MODE_PRIVATE).getString("anyTrips", "");

        if (noTripsTextView.getVisibility() == View.VISIBLE && anyTrips.equals("true")){
            noTripsTextView.setVisibility(View.GONE);
            here_are_planned_trips.setVisibility(View.VISIBLE);
            tripListView.setVisibility(View.VISIBLE);
        }

        if(anyTrips.equals("false")){
            noTripsTextView.setVisibility(View.VISIBLE);
            here_are_planned_trips.setVisibility(View.GONE);
            tripListView.setVisibility(View.GONE);
        }
        // handles when listview needs to be refreshed
        if(anyTrips.equals("true")){
            adapter.notifyDataSetChanged();

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Home", "ON PAUSE");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Home", "ON STOP");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Home", "ON DESTROY");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("Home", "ON RESTART");
        TripDetailsDBHelper dbHelper = new TripDetailsDBHelper(this);
        Integer numOfRecords = dbHelper.getNumberofRecords();
        if(numOfRecords == 0){
            //set anytrips to false
            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.preferences_key), MODE_PRIVATE).edit();
            editor.putString("anyTrips", "false");
            editor.apply();
        }
        finish(); // to refresh activity when user has deleted a trip
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                TripDetailsContract.TripDetailsTable._ID,
                TripDetailsContract.TripDetailsTable.COLUMN_TRIP_DESTINATION,
                TripDetailsContract.TripDetailsTable.COLUMN_TRIP_ARRIVAL_DATE,
                TripDetailsContract.TripDetailsTable.COLUMN_TRIP_DEPARTURE_DATE,
                TripDetailsContract.TripDetailsTable.COLUMN_TRIP_BUDGET,
                TripDetailsContract.TripDetailsTable.COLUMN_TRIP_ACCOMODATION,
                TripDetailsContract.TripDetailsTable.COLUMN_TRIP_TRANSPORT,
                TripDetailsContract.TripDetailsTable.COLUMN_TRIP_IMAGE
        };
        CursorLoader loader = new CursorLoader(this, TripDetailsContract.TripDetailsTable.CONTENT_URI, projection, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
        Log.i("LoadFinished: ", "LoadFinshed");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
        Log.i("onLoaderReset: ", "onLoaderReset");
    }

    public void createNotificationsChannel(){ // to allow notifications to be sent on android 8.0 and above
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "TravelBudNotification";
            String description = "Channel for TravelBud notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("TravelBudNotification", name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("onActivityResult: ", Integer.toString(requestCode));
        if (requestCode == ADD_TRIP_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                finish(); // refreshes the activity when a trip is added
            }
        }
    }
}

