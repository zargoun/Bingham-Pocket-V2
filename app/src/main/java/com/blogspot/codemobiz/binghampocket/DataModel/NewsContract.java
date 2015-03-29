package com.blogspot.codemobiz.binghampocket.DataModel;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Barka on 3/23/2015.
 */
public class NewsContract {
    public  static  final String CONTENT_AUTH = "com.blogspot.codemobiz.binghampocket";
    public static  final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTH);
    public static  final String PATH_LOC = "bhu_table";

    public static final class NewsHub implements BaseColumns
    {
        public static  final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOC).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTH + "/" + PATH_LOC;
        public static final String CONTENT_ITEM = "vnd.android.cursor.item/" + CONTENT_AUTH + "/" + PATH_LOC;

        public static final String TABLE_NAME = "bhu_table";
        public static final String NEWS_ID = "news_id";
        public static final String TITLE = "bhu_title";
        public static final String TABLE_DATE = "bhu_date";
        public static final String TABLE_DESCRIPTION = "bhu_description";

        public static Uri buildNewsUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
