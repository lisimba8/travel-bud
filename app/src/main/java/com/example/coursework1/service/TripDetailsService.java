package com.example.coursework1.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.coursework1.HomePageActivity;
import com.example.coursework1.R;
import com.example.coursework1.data.TripDetailsContract;
import com.example.coursework1.Trip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TripDetailsService extends IntentService {
    private static final String TAG = "TripDetailsService";

    public static final String ACTION_GET_TRIP_DETAILS = "com.example.coursework1.service.action.GET_TRIP_DETAILS";


    // URI for SERVICE TO BROADCAST INTENT
    public static String TRIP_DETAILS_RESULT = TripDetailsContract.CONTENT_AUTHORITY + ".TRIP_DETAILS_RESULT";
    public static String TRIP_DETAILS_MESSAGE = TripDetailsContract.CONTENT_AUTHORITY + ".TRIP_DETAILS_MESSAGE";

    // Broadcaster for SERVICE TO BROADCAST INTENT
    private LocalBroadcastManager broadcaster;


    public TripDetailsService() {
        super("TripDetailsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "TripDetailsService started");

        //get country from intent
        String country = intent.getStringExtra("country");

        String jsondata = "";

        String LOG_TAG = "AddTripActivity";
        String tripImage = "";

        //check if there is internet connection
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){

            jsondata = GET(country); // we need to get an image of the country
            Log.i("YOURDATA", jsondata);
            tripImage = getInfoFromJSON(jsondata);
            Log.i("YOURIMAGE", tripImage);
        }
        else {
            //display error
            tripImage = "None";
            Log.d(LOG_TAG,"No Internet Connection!");
        }

        // onpost execute

        Trip trip = new Trip();// using for testing purposes

        //used trip class to store the data
        trip.tripDestination = intent.getStringExtra("destination");
        trip.tripTransport = intent.getStringExtra("transport");
        trip.tripStartDate = intent.getStringExtra("startDate");
        trip.tripEndDate = intent.getStringExtra("endDate");
        trip.tripAccommodation = intent.getStringExtra("accommodation");
        trip.tripBudget = intent.getStringExtra("budget");
        trip.tripImage = tripImage;


        //add to the database
        ContentValues cv = new ContentValues();
        cv.put(TripDetailsContract.TripDetailsTable.COLUMN_TRIP_DESTINATION, trip.tripDestination);
        cv.put(TripDetailsContract.TripDetailsTable.COLUMN_TRIP_TRANSPORT, trip.tripTransport);
        cv.put(TripDetailsContract.TripDetailsTable.COLUMN_TRIP_ARRIVAL_DATE, trip.tripStartDate);
        cv.put(TripDetailsContract.TripDetailsTable.COLUMN_TRIP_DEPARTURE_DATE, trip.tripEndDate);
        cv.put(TripDetailsContract.TripDetailsTable.COLUMN_TRIP_ACCOMODATION, trip.tripAccommodation);
        cv.put(TripDetailsContract.TripDetailsTable.COLUMN_TRIP_BUDGET, trip.tripBudget);
        cv.put(TripDetailsContract.TripDetailsTable.COLUMN_TRIP_IMAGE, trip.tripImage);

        getContentResolver().insert(TripDetailsContract.TripDetailsTable.CONTENT_URI, cv);

        //set the anyTrips variable to true for the main activity
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preferences_key), MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("anyTrips", "true");
        editor.commit();

        sendResult("saving trip details");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    public void sendResult(String message) {
        Intent intent = new Intent(TRIP_DETAILS_RESULT);
        if (message != null)
            intent.putExtra(TRIP_DETAILS_MESSAGE, message);
        broadcaster.sendBroadcast(intent);
    }

    private String getInfoFromJSON(String imageObject) {
        String toReturn = "None"; //default value if no image is found
        try {
            // Parse the JSON string into a JSONObject
            JSONObject jsondata = new JSONObject(imageObject);

            // Access the landscape property of the src object in the first element of the photos array
            JSONArray photos = jsondata.getJSONArray("photos");
            JSONObject firstPhoto = photos.getJSONObject(0);
            JSONObject src = firstPhoto.getJSONObject("src");
            String imageSource = src.getString("landscape"); //gets the landscape image from the API
            return imageSource;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return toReturn;
    }

    //The helper function that makes an HTTP GET request using the url passed in as the parameter
    public String GET(String destination){
        String url ="https://api.pexels.com/v1/search?query="+destination+"&size=small&per_page=1"; //query to get image of destination
        InputStream is = null;
        String result="";
        URL request = null;
        try {
            request = new URL(url);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) request.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty ("Authorization", "add_auth_token");

            conn.connect();
            is = conn.getInputStream();
            if (is!=null)
                result = convertInputStreamToString(is);
            else
                result = "Did not work!";
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            //Disconnect
            conn.disconnect();
        }
        return result;
    }
    //The helper function that converts the input stream to String
    private String convertInputStreamToString(InputStream is) throws IOException{
        //initialise a BufferedReader
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        String line = "";
        String result = "";
        while((line=bufferedReader.readLine())!=null)
            result += line;
        //close the input stream and return
        is.close();
        return result;
    }
}
