package com.example.coursework1.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.coursework1.Trip;

public class TripDetailsDBHelper extends SQLiteOpenHelper {

        public static final String LOG_TAG = TripDetailsDBHelper.class.getSimpleName();

        public static final String DATABASE_NAME = "tripdetails.db";

        public static final int DATABASE_VERSION = 2;

        public static SQLiteDatabase myDB;

        public TripDetailsDBHelper(Context context) {
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
            myDB = getWritableDatabase();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String SQL_CREATE_TRIPDETAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + TripDetailsContract.TripDetailsTable.TABLE_NAME + " ("
                    + TripDetailsContract.TripDetailsTable._ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TripDetailsContract.TripDetailsTable.COLUMN_TRIP_DESTINATION + " TEXT NOT NULL, "
                    + TripDetailsContract.TripDetailsTable.COLUMN_TRIP_ARRIVAL_DATE + " TEXT NOT NULL, "
                    + TripDetailsContract.TripDetailsTable.COLUMN_TRIP_DEPARTURE_DATE + " TEXT NOT NULL, "
                    + TripDetailsContract.TripDetailsTable.COLUMN_TRIP_TRANSPORT + " TEXT NOT NULL, "
                    + TripDetailsContract.TripDetailsTable.COLUMN_TRIP_ACCOMODATION + " TEXT NOT NULL, "
                    + TripDetailsContract.TripDetailsTable.COLUMN_TRIP_BUDGET + " TEXT NOT NULL, "
                    + TripDetailsContract.TripDetailsTable.COLUMN_TRIP_IMAGE + " TEXT NOT NULL);";

            db.execSQL(SQL_CREATE_TRIPDETAILS_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TripDetailsContract.TripDetailsTable.TABLE_NAME);
            onCreate(db);
        }

        //CLEAR TABLE
        public void clearTable(String tableName){
            myDB.execSQL("DELETE FROM " + tableName);
        }

        //ADD TRIP DETAILS
        public void addTripDetails(String tripDestination, String tripArrivalDate, String tripDepartureDate, String tripTransport, String tripAccomodation, String tripImage){
            String SQL_INSERT_TRIPDETAILS = "INSERT INTO " + TripDetailsContract.TripDetailsTable.TABLE_NAME + " ("
                    + TripDetailsContract.TripDetailsTable.COLUMN_TRIP_DESTINATION + ", "
                    + TripDetailsContract.TripDetailsTable.COLUMN_TRIP_ARRIVAL_DATE + ", "
                    + TripDetailsContract.TripDetailsTable.COLUMN_TRIP_DEPARTURE_DATE + ", "
                    + TripDetailsContract.TripDetailsTable.COLUMN_TRIP_TRANSPORT + ", "
                    + TripDetailsContract.TripDetailsTable.COLUMN_TRIP_ACCOMODATION +", "
                    + TripDetailsContract.TripDetailsTable.COLUMN_TRIP_BUDGET +", "
                    + TripDetailsContract.TripDetailsTable.COLUMN_TRIP_IMAGE +") VALUES ('"
                    + tripDestination + "', '"
                    + tripArrivalDate + "', '"
                    + tripDepartureDate + "', '"
                    + tripTransport + "', '"
                    + tripAccomodation + "', '"
                    + tripImage +"');";
            myDB.execSQL(SQL_INSERT_TRIPDETAILS);
        }

        //GET TRIP DETAILS BY ID
        public Trip getTripDetailsById(String id){
            Trip trip = new Trip();

            myDB = getReadableDatabase();

            // SQL query statement
            String SQL_GET_TRIPDETAILS_BY_ID = "SELECT * FROM " + TripDetailsContract.TripDetailsTable.TABLE_NAME + " WHERE "
                    + TripDetailsContract.TripDetailsTable._ID + " = " + id + ";";

            Cursor cursor = myDB.rawQuery(SQL_GET_TRIPDETAILS_BY_ID, null);
            try {
                if (cursor.moveToFirst()) {
                    do {
                        trip.tripDestination = cursor.getString(cursor.getColumnIndex(TripDetailsContract.TripDetailsTable.COLUMN_TRIP_DESTINATION));
                        trip.tripStartDate = cursor.getString(cursor.getColumnIndex(TripDetailsContract.TripDetailsTable.COLUMN_TRIP_ARRIVAL_DATE));
                        trip.tripEndDate = cursor.getString(cursor.getColumnIndex(TripDetailsContract.TripDetailsTable.COLUMN_TRIP_DEPARTURE_DATE));
                        trip.tripTransport = cursor.getString(cursor.getColumnIndex(TripDetailsContract.TripDetailsTable.COLUMN_TRIP_TRANSPORT));
                        trip.tripAccommodation = cursor.getString(cursor.getColumnIndex(TripDetailsContract.TripDetailsTable.COLUMN_TRIP_ACCOMODATION));
                        trip.tripBudget = cursor.getString(cursor.getColumnIndex(TripDetailsContract.TripDetailsTable.COLUMN_TRIP_BUDGET));
                        trip.tripImage = cursor.getString(cursor.getColumnIndex(TripDetailsContract.TripDetailsTable.COLUMN_TRIP_IMAGE));
                    } while (cursor.moveToNext());
                }
            }catch (Exception e){
                Log.i("DATABASEISSUE", "DID NOT PULL DATA ");
            }finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }

            return trip;
        }

        //DELETE TRIP DETAILS BY ID
        public void deleteTripDetailsById(String id){
            String SQL_DELETE_TRIPDETAILS_BY_ID = "DELETE FROM " + TripDetailsContract.TripDetailsTable.TABLE_NAME + " WHERE "
                    + TripDetailsContract.TripDetailsTable._ID + " = " + id + ";";
            myDB.execSQL(SQL_DELETE_TRIPDETAILS_BY_ID);
        }

        //GET NUMBER OF RECORDS IN TABLE
        public int getNumberofRecords(){
            // This function is used to update the homepage to show when there are no trips
            myDB = getReadableDatabase();
            String SQL_GET_ALL_TRIPDETAILS = "SELECT * FROM " + TripDetailsContract.TripDetailsTable.TABLE_NAME + ";";
            Cursor cursor = myDB.rawQuery(SQL_GET_ALL_TRIPDETAILS, null);
            Integer numOfColumns = 0;
            while(cursor.moveToNext()) {
                numOfColumns++;
            }
            return numOfColumns;
        }

}
