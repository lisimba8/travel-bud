package com.example.coursework1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TripPlanActivity extends AppCompatActivity {
    private String tripId;
    private String tripDestination;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance(); // using firebase firestore to store trip plans

    //define the collection name
    String collectionName = "tripPlans";

    String documentName;

    // the trip plans will be stored as map
    // the key will have a string variable called tripId which will be the id of the trip
    // and the value will be a String array showing trip plans

    Map<String, Object> tripPlan = new HashMap<>();

    ArrayList<String> tripPlanDescriptions = new ArrayList<>();

    ArrayAdapter<String> adapter;
    ListView lv;

    // issue with updating the list view therefore I am using flag to fix this
    // adding plans to trips that had no plans causes them to add twice
    boolean hasPlans = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_plan);

        Log.i("MTAG", tripPlanDescriptions.toString());

        tripId = getIntent().getStringExtra("tripId");
        tripDestination = getIntent().getStringExtra("tripDestination");

        // get user id from shared preferences to define the collection name
        String user_id = getSharedPreferences(getString(R.string.preferences_key), MODE_PRIVATE).getString("user_id", "");

        // add user id to the collection name
        collectionName = collectionName+user_id;

        //define the document name
        documentName = "tripPlan"+tripId;

        getFromFirebase();

//        Log.i("TAG", String.valueOf(tripPlanDescriptions));

        setTitle(tripDestination + " Trip Plan");



        // instantiate the adapter
        adapter = new ArrayAdapter<>(this, R.layout.trip_plan, tripPlanDescriptions);
        // get the list view
        lv = findViewById(R.id.tripPlanListView);
        // bind the adapter to the list view
        lv.setAdapter(adapter);
        // add on touch listener to each item in the list view
        lv.setOnItemLongClickListener((parent, view, position, id) -> {
            // remove the item from the list view
            adapter.remove(adapter.getItem(position));
            // remove the item from the array list
            tripPlanDescriptions.remove(position);
            // update the firebase
            saveToFirebaseAfterDeletion();
            return true;
        });


    }

    public void saveToFirebase(View view){

        // get data from newTripPlanEditText
        EditText newTripPlanEditText = (EditText) findViewById(R.id.newTripPlanEditText);
        String newTripPlanDescription = newTripPlanEditText.getText().toString();

        if(!hasPlans){
            // using flag to fix issue with updating the list view
            // this prevents the list view from adding the same trip plan twice
            // for trips that did not have plans previously
            tripPlanDescriptions = new ArrayList<>();
        }

        if(newTripPlanDescription.trim().equals("")){//do not save empty trip plans
            return;
        }else {
            tripPlanDescriptions.add(newTripPlanDescription);
            Log.i("saveToFirebase: ", tripId + " " + tripPlanDescriptions.toString());

            tripPlan.put(tripId, tripPlanDescriptions);

            Toast.makeText(this, "Saving your plan", Toast.LENGTH_SHORT).show();

            firestore.collection(collectionName)
                    .document(documentName)
                    .set(tripPlan)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(TripPlanActivity.this, "Trip Plan Added", Toast.LENGTH_SHORT).show();
                            adapter.add(newTripPlanDescription);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TripPlanActivity.this, "Failed to save trip", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void saveToFirebaseAfterDeletion(){
        firestore.collection(collectionName)
                .document(documentName)
                .set(tripPlan)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(TripPlanActivity.this, "Trip Plan Deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TripPlanActivity.this, "Failed to delete trip", Toast.LENGTH_SHORT).show();
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
                    if((ArrayList<String>) document.get(tripId)!=null) {
                        tripPlanDescriptions = (ArrayList<String>) document.get(tripId);
                        Log.d(TAG, "document data: " + tripPlanDescriptions);
                        for (String tripPlanDescription : tripPlanDescriptions) {
                            adapter.add(tripPlanDescription);
                        }
                        hasPlans = true;
                    }

                } else {
                    Log.d(TAG, "get failed: ", task.getException());
                }
            }
        });
    }
}