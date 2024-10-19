package com.example.coursework1;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;



public class TripView extends LinearLayout { //this class is used to create a custom view for the trip list
    private TextView destinationTextView;
    private TextView startDateTextView;

    private TextView tripBudgetTextView;

    private TextView accommodationTextView;

    private TextView transportTextView;

    private TextView endDateTextView;

    private TextView tripIdTextView;

    private TextView tripImageTextView;
    private Button viewDetailsButton;

    private String tripDestination;
    private String startTripDate;

    private String tripBudget;

    private String accommodation;

    private String transport;

    private String endTripDate;

    private String tripId;

    private String tripImage;


    public TripView(Context context) {
        super(context);
        initializeViews(context);
    }

    public TripView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);

        //get attributes set in the layout xml file
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.tripView,0,0);

        try {
            tripDestination = a.getString(R.styleable.tripView_tripDestination);
            startTripDate = a.getString(R.styleable.tripView_tripDates);
            tripId = a.getString(R.styleable.tripView_tripId);
            endTripDate = a.getString(R.styleable.tripView_tripDates);
        }
        catch (Exception e){
            Log.i("Load tripView: ", e.toString());
        }finally {
            a.recycle();
        }
    }

    public TripView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //get the layout inflater service
        inflater.inflate(R.layout.custom_trip_view, this); //inflate the layout

        destinationTextView = findViewById(R.id.trip_destination);
        startDateTextView = findViewById(R.id.trip_start_date);
        tripIdTextView = findViewById(R.id.trip_id);
        endDateTextView = findViewById(R.id.trip_end_date);
        tripBudgetTextView = findViewById(R.id.trip_budget);
        accommodationTextView = findViewById(R.id.trip_accommodation);
        transportTextView = findViewById(R.id.trip_transport);
        tripImageTextView = findViewById(R.id.trip_image);

        viewDetailsButton = findViewById(R.id.view_details_button);



        setViewDetailsClickListener();
    }

    public void setViewDetailsClickListener() {
        viewDetailsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //get the trip details from the text views
                tripDestination = destinationTextView.getText().toString();
                startTripDate = startDateTextView.getText().toString();
                tripId = tripIdTextView.getText().toString();
                endTripDate = endDateTextView.getText().toString();
                tripBudget = tripBudgetTextView.getText().toString();
                accommodation = accommodationTextView.getText().toString();
                transport = transportTextView.getText().toString();
                tripImage = tripImageTextView.getText().toString();

                //get context from the view and start the trip view activity
                Intent intent = new Intent(getContext(), TripViewActivity.class);

                // Add the FLAG_ACTIVITY_NEW_TASK flag to the intent for the activity to work
                // because intent is called outside of an activity
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.putExtra("tripId", tripId);
                intent.putExtra("tripDestination", tripDestination);
                intent.putExtra("startTripDate", startTripDate);
                intent.putExtra("endTripDate", endTripDate);
                intent.putExtra("tripBudget", tripBudget);
                intent.putExtra("accommodation", accommodation);
                intent.putExtra("transport", transport);
                intent.putExtra("tripImage", tripImage);

                getContext().startActivity(intent);
            }
            });
    }
}
