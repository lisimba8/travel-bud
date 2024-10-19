package com.example.coursework1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.coursework1.data.TripDetailsContract;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import com.example.coursework1.Trip;
import com.example.coursework1.service.TripDetailsService;

public class AddTripActivity extends AppCompatActivity {

    private static final int ADD_TRIP_REQUEST_CODE = 3;

    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        //change the title of the action bar
        getSupportActionBar().setTitle("Enter Trip details");

        // create listener for broadcast receiver
        receiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent ){
                String s = intent.getStringExtra(TripDetailsService.TRIP_DETAILS_MESSAGE);
                if(s.compareTo("saving trip details")==0){
                    Log.i("Service Received", "onReceive: ");
                    Toast.makeText(AddTripActivity.this, "Trip details saved", Toast.LENGTH_SHORT).show();

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("response", "trip details saved");
                    AddTripActivity.this.setResult(Activity.RESULT_OK, returnIntent);
                    finish();

                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        // register the local broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter(TripDetailsService.TRIP_DETAILS_RESULT));
    }

    @Override
    protected void onStop() {
        super.onStop();
        // unregister the local broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    //create a method to handle the click event of the button
    public void addTrip(View view) {
        Trip trip = new Trip();// using for testing purposes


        //get the values from the edit text fields
        //create a new trip object
        //add the trip to the database
        //display a toast message
        DatePicker startdate = (DatePicker) findViewById(R.id.startDatePicker);
        int year = startdate.getYear();
        int month = startdate.getMonth();
        int day = startdate.getDayOfMonth();

        Date date1 = new Date(year, month, day); //used for alarm manager

        String string_date = day + "/" + month + "/" + year;

        DatePicker enddate = (DatePicker) findViewById(R.id.endDatePicker);
        int year2 = enddate.getYear();
        int month2 = enddate.getMonth();
        int day2 = enddate.getDayOfMonth();

        Date date2 = new Date(year2, month2, day2);

        String string_date2 = day2 + "/" + month2 + "/" + year2;

        Boolean allValuesFilledIn = allValuesFilledIn();

        //validate the values before sending to the service to save to the database
        if(date1.after(date2)){
            Toast.makeText(this, "Start date cannot be after end date", Toast.LENGTH_SHORT).show();
        } else if (!allValuesFilledIn) {
            Toast.makeText(this, "Please Fill in all values", Toast.LENGTH_SHORT).show();
            
        } else{
            // add to database
            // create a new async task
//            addTripAsyncTask task = new addTripAsyncTask();
//            // execute the task
//            task.execute();

            Spinner countrySpinner = (Spinner) findViewById(R.id.destination_country);
            String country = countrySpinner.getSelectedItem().toString();

            EditText destinationEditText = (EditText) findViewById(R.id.destination_city);
            trip.tripDestination = destinationEditText.getText().toString()+", "+country;

            Spinner transportSpinner = (Spinner) findViewById(R.id.transport);
            String transport = transportSpinner.getSelectedItem().toString();
            trip.tripTransport = transport;

            trip.tripStartDate = day + "/" + month + "/" + year;

            trip.tripEndDate = day2 + "/" + month2 + "/" + year2;

            EditText tripAccommodationEditText = (EditText) findViewById(R.id.accomodation);
            trip.tripAccommodation = tripAccommodationEditText.getText().toString();

            EditText tripBudgetEditText = (EditText) findViewById(R.id.budget);
            trip.tripBudget = tripBudgetEditText.getText().toString();

            Intent serviceIntent = new Intent(getApplicationContext(), TripDetailsService.class);
            serviceIntent.putExtra("destination", trip.tripDestination);
            serviceIntent.putExtra("transport", trip.tripTransport);
            serviceIntent.putExtra("startDate", trip.tripStartDate);
            serviceIntent.putExtra("endDate", trip.tripEndDate);
            serviceIntent.putExtra("accommodation", trip.tripAccommodation);
            serviceIntent.putExtra("budget", trip.tripBudget);
            serviceIntent.putExtra("country", country); //used to get the trip image from image API call
            startService(serviceIntent);

            Toast.makeText(this, "Saving Trip ", Toast.LENGTH_SHORT).show();

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            //setting the time to receive notification
            Calendar cal = Calendar.getInstance(); // creates calendar

            // sets calendar time/date
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.DAY_OF_MONTH, day);
            Log.i("date", cal.getTime().toString());
            cal.add(Calendar.MINUTE, 1);      // adds 1 minutes. If date set for today, notification will be received in 1 minute
            Log.i("date 2", cal.getTime().toString());

            alarmManager.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(), pendingIntent);

        }

    }

    private boolean allValuesFilledIn(){
        //function to check if the values are valid
        //if not return false
        //if yes, return true

        Trip trip = new Trip();// using for testing purposes

        EditText destinationEditText = findViewById(R.id.destination_city);
        trip.tripDestination = destinationEditText.getText().toString();

        EditText accommodationEditText = findViewById(R.id.accomodation);
        trip.tripAccommodation = accommodationEditText.getText().toString();

        EditText budgetEditText = findViewById(R.id.budget);
        trip.tripBudget = budgetEditText.getText().toString();

        if(trip.tripDestination.equals("") || trip.tripAccommodation.equals("") || trip.tripBudget.equals("")){
            return false;
        }
        else{
            return true;
        }
    }

}