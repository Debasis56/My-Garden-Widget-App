package com.example.android.mygarden.provider;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.android.mygarden.provider.PlantContract.PlantEntry;


public class PlantContentProvider extends ContentProvider {

    public static final int PLANTS = 100;
    public static final int PLANT_WITH_ID = 101;

    // Declare a static variable for the Uri matcher that you construct
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String TAG = PlantContentProvider.class.getName();

    // Define a static buildUriMatcher method that associates URI's with their int match
    public static UriMatcher buildUriMatcher() {
        // Initialize a UriMatcher
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Add URI matches
        uriMatcher.addURI(PlantContract.AUTHORITY, PlantContract.PATH_PLANTS, PLANTS);
        uriMatcher.addURI(PlantContract.AUTHORITY, PlantContract.PATH_PLANTS + "/#", PLANT_WITH_ID);
        return uriMatcher;
    }

    // Member variable for a PlantDbHelper that's initialized in the onCreate() method
    private PlantDbHelper mPlantDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mPlantDbHelper = new PlantDbHelper(context);
        return true;
    }

    /***
     * Handles requests to insert a single new row of data
     *
     * @param uri
     * @param values
     * @return
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mPlantDbHelper.getWritableDatabase();

        // Write URI matching code to identify the match for the plants directory
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned
        switch (match) {
            case PLANTS:
                // Insert new values into the database
                long id = db.insert(PlantEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(PlantContract.PlantEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    /***
     * Handles requests for data by URI
     *
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db = mPlantDbHelper.getReadableDatabase();

        // Write URI match code and set a variable to return a Cursor
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            // Query for the plants directory
            case PLANTS:
                retCursor = db.query(PlantEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case PLANT_WITH_ID:
                String id = uri.getPathSegments().get(1);
                retCursor = db.query(PlantEntry.TABLE_NAME,
                        projection,
                        "_id=?",
                        new String[]{id},
                        null,
                        null,
                        sortOrder);
                break;
            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }

    /***
     * Deletes a single row of data
     *
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return number of rows affected
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // Get access to the database and write URI matching code to recognize a single item
        final SQLiteDatabase db = mPlantDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted plants
        int plantsDeleted; // starts as 0
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case PLANT_WITH_ID:
                // Get the plant ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                plantsDeleted = db.delete(PlantEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Notify the resolver of a change and return the number of items deleted
        if (plantsDeleted != 0) {
            // A plant (or more) was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of plant deleted
        return plantsDeleted;
    }

    /***
     * Updates a single row of data
     *
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return number of rows affected
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // Get access to underlying database
        final SQLiteDatabase db = mPlantDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        // Keep track of the number of updated plants
        int plantsUpdated;

        switch (match) {
            case PLANTS:
                plantsUpdated = db.update(PlantEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PLANT_WITH_ID:
                if (selection == null) selection = PlantEntry._ID + "=?";
                else selection += " AND " + PlantEntry._ID + "=?";
                // Get the place ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Append any existing selection options to the ID filter
                if (selectionArgs == null) selectionArgs = new String[]{id};
                else {
                    ArrayList<String> selectionArgsList = new ArrayList<String>();
                    selectionArgsList.addAll(Arrays.asList(selectionArgs));
                    selectionArgsList.add(id);
                    selectionArgs = selectionArgsList.toArray(new String[selectionArgsList.size()]);
                }
                plantsUpdated = db.update(PlantEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items updated
        if (plantsUpdated != 0) {
            // A place (or more) was updated, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of places deleted
        return plantsUpdated;
    }


    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
