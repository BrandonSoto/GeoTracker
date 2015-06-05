/*
 * Copyright (c) 2015. Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */

/**
 * Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */
package edu.uw.ProjectMayhem.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**MovementDBHandler is a database handler for the location tracking info. */
public class MovementDBHandler extends SQLiteOpenHelper {

    /** database version number. */
    private static int DATABASE_VERSION = 1;
    /** database name. */
    private static final String DATABASE_NAME = "PeopleTracker";
    /**Movement string. */
    private static final String MOVEMENT = "movement";
    /**Latitude string. */
    private static final String LATITUDE = "latitude";
    /**Longitude string. */
    private static final String LONGITUDE = "longitude";
    /**Speed string. */
    private static final String SPEED = "speed";
    /**Heading string. */
    private static final String HEADING = "heading";
    /**User_ID string. */
    private static final String USER_ID = "user_id";
    /**Timestamp string. */
    private static final String TIMESTAMP = "timestamp";

    /**MovementDBHandler constructor. */
    public MovementDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**addMovement() add new location data to database. */
    public void addMovement(MovementData aMove) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(LATITUDE, aMove.getLatitude());
        values.put(LONGITUDE, aMove.getLongitude());
        values.put(SPEED, aMove.getSpeed());
        values.put(HEADING, aMove.getHeading());
        values.put(USER_ID, aMove.getUserId());
        values.put(TIMESTAMP, aMove.getTimeStamp());

        db.insert(MOVEMENT, null, values);

        db.close();
    }

//    public MovementData getMovement(int id) {
//
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = db.query(MOVEMENT, new String[] {LATITUDE,
//                LONGITUDE, SPEED, HEADING, TIMESTAMP} + "=?",
//                new String[] { String.valueOf(id) }, null, null, null, null);
//
//        if (cursor != null)
//            cursor.moveToFirst();
//
//        MovementData move = new MovementData(Double.parseDouble(cursor.getString(0)),
//                Double.parseDouble(cursor.getString(1)), Double.parseDouble(cursor.getString(2)),
//                Double.parseDouble(cursor.getString(3)),Integer.parseInt(cursor.getString(4)), Long.parseLong(cursor.getString(5)));
//
//        return move;
//    }

    /**Retieves all movement data. */
    public List<MovementData> getAllMovement() {

        List<MovementData> movementList = new ArrayList<MovementData>();

        String selectQuery = "SELECT  * FROM " + MOVEMENT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MovementData move = new MovementData();

                move.setLatitude(Double.parseDouble(cursor.getString(0)));
                move.setLongitude(Double.parseDouble(cursor.getString(1)));
                move.setSpeed(Double.parseDouble(cursor.getString(2)));
                move.setHeading(Double.parseDouble(cursor.getString(3)));
                move.setUserId(cursor.getString(4));
                move.setTimeStamp(Long.parseLong(cursor.getString(5)));

                movementList.add(move);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return movementList;
    }

    /**retrieves Movement count. */
    public int getMovementCount() {

        String countQuery = "SELECT  * FROM " + MOVEMENT;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

//    public int updateMovement(MovementData update) {
//
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//
//        values.put(LATITUDE, update.getLatitude());
//        values.put(LONGITUDE, update.getLongitude());
//        values.put(SPEED, update.getSpeed());
//        values.put(HEADING, update.getHeading());
////        values.put(PK_SOURCE_ID, update.getSourceID());
//        values.put(TIMESTAMP, update.getTimeStamp());
//
//        return db.update(MOVEMENT, values, " = ?",
//                new String[] { String.valueOf(update.getSourceID())});
//    }
//
    /**deletes movement data from database. */
    public void deleteAllMovement() {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(MOVEMENT, null, null);

        db.close();
    }

    /**onCreate() instantiates movement table. */
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_MOVEMENT_TABLE = "create table " + MOVEMENT + " (" + LATITUDE + " DOUBLE,"
                + LONGITUDE + " DOUBLE," + SPEED + " DOUBLE," + HEADING + " TEXT,"
                + USER_ID + " TEXT," + TIMESTAMP + " TEXT" + ")";

        db.execSQL(CREATE_MOVEMENT_TABLE);
    }

    /**onUpgrade() upgrades table. */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldDB, int newDB) {

        db.execSQL("DROP TABLE IF EXISTS " + MOVEMENT);

//        DATABASE_VERSION++;

        onCreate(db);
    }
}
