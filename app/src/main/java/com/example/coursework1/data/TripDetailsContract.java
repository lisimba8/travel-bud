package com.example.coursework1.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class TripDetailsContract {


        public static final String CONTENT_AUTHORITY = "com.example.coursework1";
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        public static final String PATH_TRIPDETAILS = "tripdetails";

        public static final class TripDetailsTable implements BaseColumns{
            public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRIPDETAILS).build();
            public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_TRIPDETAILS;
            public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_TRIPDETAILS;

            // table and column names for tripdetails table
            public static final String TABLE_NAME = "tripdetails";

            public static final String COLUMN_TRIP_DESTINATION = "tripdestination";

            public static final String COLUMN_TRIP_ARRIVAL_DATE = "triparrivaldate";

            public static final String COLUMN_TRIP_DEPARTURE_DATE = "tripdeparturedate";

            public static final String COLUMN_TRIP_TRANSPORT = "triptransport";

            public static final String COLUMN_TRIP_ACCOMODATION = "tripaccomodation";

            public static final String COLUMN_TRIP_BUDGET = "tripbudget";

            public static final String COLUMN_TRIP_IMAGE = "tripimage";

            public static Uri buildTripDetailsUriWithID(long id){
                return ContentUris.withAppendedId(CONTENT_URI,id);
            }

        }
}
