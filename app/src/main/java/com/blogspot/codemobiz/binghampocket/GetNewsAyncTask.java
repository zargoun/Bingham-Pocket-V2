package com.blogspot.codemobiz.binghampocket;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.blogspot.codemobiz.binghampocket.DataModel.NewsContract;
import com.blogspot.codemobiz.binghampocket.DataModel.NewsDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

/**
 * Created by Barka on 3/23/2015.
 */
public class GetNewsAyncTask extends AsyncTask<String, Void, String[]> {

    private final Context myContext;
    private ArrayAdapter<String> myNewsAdapter;
    private boolean DEBUG = true;

    GetNewsAyncTask(Context context, ArrayAdapter<String> newsArray)
    {
        myContext = context;
        myNewsAdapter = newsArray;
    }

    String[] convertContentValuesToUXFormat(Vector<ContentValues> cvv) {
        // return strings to keep UI functional for now
        String[] resultStrs = new String[cvv.size()];
        for ( int i = 0; i < cvv.size(); i++ ) {
            ContentValues newsValues = cvv.elementAt(i);
            resultStrs[i] =
                    " - " + newsValues.getAsString(NewsContract.NewsHub.TITLE) +
                    " - " + newsValues.getAsString(NewsContract.NewsHub.TABLE_DATE) +
                    " - " + newsValues.getAsString(NewsContract.NewsHub.TABLE_DESCRIPTION) ;
        }
        return resultStrs;
    }


    private String[] getNewsDataFromJson(String bhuJsonStr, int numPost)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String BHU_TITLE = "title";
        final String BHU_DATE = "Date";
        final String BHU_DESCRIPTION = "description";
    try{
        JSONArray jFeedArray = new JSONArray(bhuJsonStr);
        Log.i("Array length", String.valueOf(jFeedArray.length()));
        Vector<ContentValues> cVVector = new Vector<ContentValues>(jFeedArray.length());


        // OWM returns daily forecasts based upon the local time of the city that is being
        // asked for, which means that we need to know the GMT offset to translate this data
        // properly.
        // Since this data is also sent in-order and the first day is always the
        // current day, we're going to take advantage of that to get a nice
        // normalized UTC date for all of our weather.
        String[] resultStrs = new String[numPost];
        for (int i = 0; i < jFeedArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String title;
            String description;
            String date;

            JSONObject jFeedObj = jFeedArray.getJSONObject(i);
            title = jFeedObj.getString(BHU_TITLE);
            description = jFeedObj.getString(BHU_DESCRIPTION);
            date = jFeedObj.getString(BHU_DATE);

            Log.i("TITLE", title);

            ContentValues contentValues = new ContentValues();

            contentValues.put(NewsContract.NewsHub.TITLE, title);
            contentValues.put(NewsContract.NewsHub.TABLE_DATE, date);
            contentValues.put(NewsContract.NewsHub.TABLE_DESCRIPTION, description);

            cVVector.add(contentValues);
            resultStrs[i] = title + " - " + date ;
        }
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            int rowsInserted = myContext.getContentResolver()
                    .bulkInsert(NewsContract.NewsHub.CONTENT_URI, cvArray);
            Log.v("Inserting Data", "inserted " + rowsInserted + " rows of weather data");
            // Use a DEBUG variable to gate whether or not you do this, so you can easily
            // turn it on and off, and so that it's easy to see what you can rip out if
            // you ever want to remove it.
            if (DEBUG) {
                Cursor newsCursor = myContext.getContentResolver().query(
                        NewsContract.NewsHub.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );

                if (newsCursor.moveToFirst()) {
                    ContentValues resultValues = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(newsCursor, resultValues);
                    Log.i("Querying...", "Query succeeded! **********");
                    for (String key : resultValues.keySet()) {
                        Log.i("Key insert", key + ": " + resultValues.getAsString(key));
                    }
                } else {
                    Log.v("Querying...", "Query failed! :( **********");
                }
            }
        }
        return resultStrs;
        }
    catch (JSONException e)
    {
        Log.e("JSONEX", "Json string Exception");
    }
        return null;
    }
/*
        for (String s : resultStrs) {
            Log.v("Christ", "Json entry: " + s);
        }
        return resultStrs;

    }*/


    @Override
    protected String[] doInBackground(String... params) {
        if(params.length == 0)
        {
            return null;
        }

//        String newsQuery = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;


        String bhuJsonStr = null;
        int numPost = 5;

        try {
            //URL url = new URL("http://10.0.2.2/buggieman/feeds.php");
            final String NEWS_URL = "http://10.0.2.2/buggieman/feeds.php";

            URL url = new URL(NEWS_URL);
            Log.i("Logged URL", url.toString());
            // Create the request to News API, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                Log.i("Inputstream", "Null returned");
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            bhuJsonStr = buffer.toString();
            Log.v("Verbose json", bhuJsonStr);
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
        try {
            return getNewsDataFromJson(bhuJsonStr, numPost);
        } catch (JSONException e) {
            Log.e("Oh no", e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }


     @Override
    protected void onPostExecute(String[] result) {
        if (result != null && myNewsAdapter != null) {
            myNewsAdapter.clear();
            for(String bhuJsonStr : result) {
                myNewsAdapter.add(bhuJsonStr);
            }
            // New data is back from the server.  Hooray!
        }
    }
}
