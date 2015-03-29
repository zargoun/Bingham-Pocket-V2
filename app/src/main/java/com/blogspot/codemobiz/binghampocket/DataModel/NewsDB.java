package com.blogspot.codemobiz.binghampocket.DataModel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Barka on 3/23/2015.
 */
public class NewsDB extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    public static final String DB_NAME = "bhu_db.db";

    public NewsDB(Context context)
    {
        super(context, DB_NAME, null,DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_NEWS_TABLE = "CREATE TABLE " + NewsContract.NewsHub.TABLE_NAME+"("+
                NewsContract.NewsHub._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                NewsContract.NewsHub.TITLE + " TEXT NOT NULL, "+
                NewsContract.NewsHub.TABLE_DATE + " TEXT NOT NULL, "+
                NewsContract.NewsHub.TABLE_DESCRIPTION + " TEXT NOT NULL"+");";

        sqLiteDatabase.execSQL(SQL_CREATE_NEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ NewsContract.NewsHub.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
