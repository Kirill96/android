package com.example.kirill.lab_34;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kirill on 24.11.16.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "my_db";

    public static final String TABLE_CATEGORIES = "categories";
    public static final String KEY_ID = "id";
    public static final String KEY_CATEGORY_NAME = "category_name";

    public static final String TABLE_SONGS = "songs";
    public static final String KEY_SONG_NAME = "song_name";
    public static final String KEY_SONG_ARTIST = "song_artist";
    public static final String KEY_SONG_IMAGE = "song_image";
    public static final String KEY_CATEGORY_ID = "category_id";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_CATEGORIES + "(" + KEY_ID
                + " integer primary key autoincrement," + KEY_CATEGORY_NAME
                + " text not null" + ")");

        db.execSQL("create table " + TABLE_SONGS + "(" + KEY_SONG_NAME
                + " text not null," + KEY_SONG_ARTIST + " text not null,"
                + KEY_SONG_IMAGE + " integer not null," + KEY_CATEGORY_ID
                + " integer not null,"
                + "foreign key (" + KEY_CATEGORY_ID + ") references " + TABLE_CATEGORIES
                + "(" + KEY_ID + ")"
                + ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists categories");
        db.execSQL("drop table if exists songs");
        onCreate(db);
    }
}
