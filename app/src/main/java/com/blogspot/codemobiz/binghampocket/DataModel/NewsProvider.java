package com.blogspot.codemobiz.binghampocket.DataModel;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Barka on 3/23/2015.
 */
public class NewsProvider extends ContentProvider {

    public static final int NEWS = 0;
    public static final int NEWS_ID = 102;
    private static final UriMatcher sUriMatcher = buildMatcher();
    private NewsDB mNewsDB;
    private static UriMatcher buildMatcher()
    {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String auth = NewsContract.CONTENT_AUTH;

        uriMatcher.addURI(auth, NewsContract.PATH_LOC, NEWS);
        uriMatcher.addURI(auth, NewsContract.PATH_LOC+"/#", NEWS_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate()
    {
        Log.i("News Case check: ", ((Integer) NEWS).toString());
        mNewsDB = new NewsDB(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri)
    {
        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case NEWS:
                return NewsContract.NewsHub.CONTENT_TYPE;
            case NEWS_ID:
                return NewsContract.NewsHub.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Lost are we?" + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor returnCur;
        switch (sUriMatcher.match(uri))
        {
            case NEWS: {
                returnCur = mNewsDB.getReadableDatabase().query(NewsContract.NewsHub.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
                case NEWS_ID:
                {
                returnCur = mNewsDB.getReadableDatabase().query(NewsContract.NewsHub.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
              throw new UnsupportedOperationException("URI exception " + uri);
        }
        returnCur.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCur;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final SQLiteDatabase my_db = mNewsDB.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match)
        {
            case NEWS:
            {
                long _id = my_db.insert(NewsContract.NewsHub.TABLE_NAME, null,contentValues);
                if(_id > 0)
                {
                    returnUri = NewsContract.NewsHub.buildNewsUri(_id);
                    Log.i("INSERTED", "Content inserted");
                }
                else
                    throw new SQLException("Insertion Failed: " +uri);
                break;
            }
            case NEWS_ID:
            {
                long _id = my_db.insert(NewsContract.NewsHub.TABLE_NAME, null,contentValues);
                if(_id > 0)
                {
                    returnUri = NewsContract.NewsHub.buildNewsUri(_id);
                    Log.i("INSERTED", "Content inserted");
                }
                else
                    throw new SQLException("Insertion Failed: " +uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Lost are we?" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mNewsDB.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NEWS:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(NewsContract.NewsHub.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase my_db = mNewsDB.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if(null == selection) selection = "1";
        switch (match)
        {
            case NEWS:
                rowsDeleted = my_db.delete(NewsContract.NewsHub.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Lost are we?" + uri);
        }
        if (rowsDeleted != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase my_db = mNewsDB.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpadted;

        switch (match)
        {
            case NEWS:
                rowsUpadted = my_db.update(NewsContract.NewsHub.TABLE_NAME, contentValues,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Lost are we?" + uri);
        }
        if (rowsUpadted != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpadted;
    }
}
