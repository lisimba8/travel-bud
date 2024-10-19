package com.example.coursework1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coursework1.data.TripDetailsDBHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TripViewActivity extends AppCompatActivity {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance(); // using firebase firestore to store trip to do list information

    //define the collection name
    String collectionName = "tripToDoLists";

    String documentName;

    InternetReceiver internetBroadcastReceiver = new InternetReceiver();
    String tripId;
    String tripDestination;
    String startTripDate;
    String endTripDate;
    String tripBudget;
    String accommodation;
    String transport;

    String tripImage;

    boolean book_transport;
    boolean book_accommodation;
    boolean pack_clothes;

    //grab check boxes

    CheckBox bookTransportCheckBox;
    CheckBox bookAccommodationCheckBox;
    CheckBox packClothesCheckBox;

    Map<String, Object> tripToDoList = new HashMap<>();

    ArrayList<Boolean> tripTodos = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_view);
        //receive the intent
        Intent intent = getIntent();
        tripId = intent.getStringExtra("tripId");
        tripDestination = intent.getStringExtra("tripDestination");
        startTripDate = intent.getStringExtra("startTripDate");
        endTripDate = intent.getStringExtra("endTripDate");
        tripBudget = intent.getStringExtra("tripBudget");
        accommodation = intent.getStringExtra("accommodation");
        transport = intent.getStringExtra("transport");
        tripImage = intent.getStringExtra("tripImage");


        // set the title of the activity
        setTitle("Your trip to "+tripDestination);

        // get user id from shared preferences to define the collection name
        String user_id = getSharedPreferences(getString(R.string.preferences_key), MODE_PRIVATE).getString("user_id", "");

        // add user id to the collection name
        collectionName = collectionName+user_id;

        //define the document name for firebase
        documentName = "tripToDoList"+tripId;

        // add values to their text views and add the image as well
        TextView tripDestinationTextView = (TextView) findViewById(R.id.tripDestinationTextView);
        tripDestinationTextView.setText(tripDestination);

        TextView startTripDateTextView = (TextView) findViewById(R.id.startTripDateTextView);
        startTripDateTextView.setText(startTripDate);

        TextView endTripDateTextView = (TextView) findViewById(R.id.endTripDateTextView);
        endTripDateTextView.setText(endTripDate);

        TextView tripBudgetTextView = (TextView) findViewById(R.id.tripBudgetTextView);
        tripBudgetTextView.setText(tripBudget);

        TextView accommodationTextView = (TextView) findViewById(R.id.accommodationTextView);
        accommodationTextView.setText(accommodation);

        TextView transportTextView = (TextView) findViewById(R.id.transportTextView);
        transportTextView.setText(transport);

        ImageView myImageView = findViewById(R.id.tripImageView);
        Picasso.get().load(tripImage)
                .error(R.drawable.travelbud_icon)
                .placeholder(R.drawable.image_loading)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        myImageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        myImageView.setImageDrawable(errorDrawable);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        myImageView.setImageDrawable(placeHolderDrawable);
                    }
                });

        // set up floating action buttons
        FloatingActionButton shareFAB = (FloatingActionButton) findViewById(R.id.shareButton);
        shareFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "I will be leaving on a trip to "+tripDestination+" on "+startTripDate+". I will be using the TravelBud app to plan my trip. You too can soon download the app from the Google Play Store.";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        // get check box values from firebase
        getFromFirebase();

        // set up check boxes
        bookTransportCheckBox = (CheckBox) findViewById(R.id.book_transport_checkbox);
        bookAccommodationCheckBox = (CheckBox) findViewById(R.id.book_accommodation_checkbox);
        packClothesCheckBox = (CheckBox) findViewById(R.id.pack_clothes_checkbox);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register internet broadcast receiver when activity is in foreground
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internetBroadcastReceiver, filter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregister internet broadcast receiver when activity is paused
        unregisterReceiver(internetBroadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void viewTripPlans(View view) {

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        Intent intent = new Intent(this, TripPlanActivity.class);
        intent.putExtra("tripId", tripId);
        intent.putExtra("tripDestination", tripDestination);
        startActivity(intent);
        if (networkInfo != null && networkInfo.isConnected()){
            //connected to internet
        }else{
            //not connected to internet
            Toast.makeText(this, "No Internet Connection. This page may be limited in functionality.", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendToMaps(View view) {
        String location = tripDestination;
        String encodedLocation = Uri.encode(location);
        Uri geoLocation = Uri.parse("geo:0,0?q=" + encodedLocation);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        mapIntent.setData(geoLocation);
//        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }


    public void updateCheckedValues(View view) {
        book_transport = bookTransportCheckBox.isChecked();
        book_accommodation = bookAccommodationCheckBox.isChecked();
        pack_clothes = packClothesCheckBox.isChecked();

        tripTodos = new ArrayList<>();

        tripTodos.add(book_transport);
        tripTodos.add(book_accommodation);
        tripTodos.add(pack_clothes);

        tripToDoList.put(tripId, tripTodos);

        //add to firebase
        addtoFirebase();

    }

    public void addtoFirebase(){
        firestore.collection(collectionName)
                .document(documentName)
                .set(tripToDoList)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(TripViewActivity.this, "Progress saved!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TripViewActivity.this, "Sorry. We failed to save your progress...", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getFromFirebase(){
        String TAG = "FIREBASE";
        DocumentReference docRef = firestore.collection(collectionName).document(documentName);

        // Source can be CACHE, SERVER, or DEFAULT.
        Source source = Source.DEFAULT;

        // Get the document, forcing the SDK to use the offline cache
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Document found in the offline cache
                    DocumentSnapshot document = task.getResult();
                    Log.i(TAG, "onComplete: "+document.getData());
                    if(document.getData() != null) { // if we have saved data before, then update the check boxes. Otherwise, leave them unchecked
                        bookTransportCheckBox.setChecked((Boolean) ((ArrayList) document.getData().get(tripId)).get(0));
                        bookAccommodationCheckBox.setChecked((Boolean) ((ArrayList) document.getData().get(tripId)).get(1));
                        packClothesCheckBox.setChecked((Boolean) ((ArrayList) document.getData().get(tripId)).get(2));
                    }
                } else {
                    Log.d(TAG, "get failed: ", task.getException());
                }
            }
        });
    }

    public void deleteTrip(View view) {
        Log.i("deleteTrip", tripId);

        TripDetailsDBHelper dbHelper = new TripDetailsDBHelper(this);
        dbHelper.deleteTripDetailsById(tripId);

        Toast.makeText(this, "Deleting Trip...", Toast.LENGTH_SHORT).show();

        // delay finish to allow for database to be updated
        new Thread(() -> {
            try {
                Thread.sleep(200);
                finish();
                Log.i("DTAG", "deleteTrip");
            }
            catch (Exception e){
                Log.e("DELETE_ERROR", "deleteTrip: ", e);            }
        }).start();

    };
}