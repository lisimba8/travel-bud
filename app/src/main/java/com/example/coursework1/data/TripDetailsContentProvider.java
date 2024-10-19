package com.example.coursework1.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;


public class TripDetailsContentProvider extends ContentProvider {
    public static final String LOG_TAG = "TripDetailsContentProvider";
    public static final int TRIPDETAILS = 100;
    public static final int TRIPDETAILS_WITH_ID = 101;
    private static final UriMatcher myUriMatcher = buildUriMatcher();

    public static TripDetailsDBHelper myDBHelper;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(TripDetailsContract.CONTENT_AUTHORITY,TripDetailsContract.PATH_TRIPDETAILS,TRIPDETAILS);

        matcher.addURI(TripDetailsContract.CONTENT_AUTHORITY,TripDetailsContract.PATH_TRIPDETAILS+"/#",TRIPDETAILS_WITH_ID);

        return matcher;
    }

    public TripDetailsContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        int match_code = myUriMatcher.match(uri);

        switch(match_code){
            case TRIPDETAILS:{
                myDBHelper.clearTable(TripDetailsContract.TripDetailsTable.TABLE_NAME);

                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        int match_code = myUriMatcher.match(uri);

        switch(match_code){
            case TRIPDETAILS:
                return TripDetailsContract.TripDetailsTable.CONTENT_TYPE_DIR;
            case TRIPDETAILS_WITH_ID:
                return TripDetailsContract.TripDetailsTable.CONTENT_TYPE_ITEM;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        int match_code = myUriMatcher.match(uri);
        Uri retUri = null;

        switch(match_code){
            case TRIPDETAILS:{
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                long _id = db.insert(TripDetailsContract.TripDetailsTable.TABLE_NAME,null,values);
                if (_id > 0) {
                    retUri = TripDetailsContract.TripDetailsTable.buildTripDetailsUriWithID(_id);
                }
                else
                    throw new SQLException("failed to insert");
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }





        Log.i(LOG_TAG, "insert success");
        return retUri;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        myDBHelper = new TripDetailsDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        int match_code = myUriMatcher.match(uri);
        Cursor myCursor;

        switch(match_code) {
            case TRIPDETAILS: {
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                myCursor = db.query(
                        TripDetailsContract.TripDetailsTable.TABLE_NAME, // Table to Query
                        projection,//Columns
                        null, // Columns for the "where" clause
                        null, // Values for the "where" clause
                        null, // columns to group by
                        null, // columns to filter by row groups
                        null // sort order
                );
                Log.i(LOG_TAG, "querying for Trip Details");
                Log.i(LOG_TAG, myCursor.getCount() + "");
                break;
            }
            case TRIPDETAILS_WITH_ID: {
                myCursor = myDBHelper.getReadableDatabase().query(
                        TripDetailsContract.TripDetailsTable.TABLE_NAME,
                        projection,
                        TripDetailsContract.TripDetailsTable._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        myCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return myCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        int match_code = myUriMatcher.match(uri);

        switch(match_code){
            case TRIPDETAILS_WITH_ID:{
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                int _id = db.update(TripDetailsContract.TripDetailsTable.TABLE_NAME,values,selection,selectionArgs);
                if (_id > 0) {
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                else
                    throw new SQLException("failed to update");
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        return 0;
    }



}



