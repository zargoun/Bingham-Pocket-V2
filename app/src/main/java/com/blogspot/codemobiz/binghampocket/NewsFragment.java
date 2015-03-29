package com.blogspot.codemobiz.binghampocket;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.blogspot.codemobiz.binghampocket.DataModel.NewsContract;
import com.blogspot.codemobiz.binghampocket.NewsFragment;
import com.blogspot.codemobiz.binghampocket.Parser.NewsParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Barka on 3/23/2015.
 */
public class NewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOADER_ID = 0;
    private static String[] NEWS_COLNS =
    {
            NewsContract.NewsHub._ID,
            NewsContract.NewsHub.TITLE,
            NewsContract.NewsHub.TABLE_DATE,
            NewsContract.NewsHub.TABLE_DESCRIPTION
    };

    public static final int COL_NEWS_ID = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_TABLE_DATE = 2;
    public static final int COL_TABLE_DESCRIPTION = 3;

    private  ArrayAdapter<String> myNews;
    //private SimpleCursorAdapter myNews;
    public NewsFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        /*myNews = new SimpleCursorAdapter(
                getActivity(),
                R.layout.news_list,
                null,
                new String[]{
                        NewsContract.NewsHub.TITLE
                },
                new int[]{
                 R.id.lv_tv
                },
                0
        );*/
        myNews = new ArrayAdapter<String>(
                getActivity(),
                R.layout.news_list,
                R.id.lv_tv,
                new ArrayList<String>()
        );

        ListView newsListView = (ListView)rootView.findViewById(R.id.lv_news);
        newsListView.setAdapter(myNews);
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String feed = myNews.getItem(position).toString();
                Intent goDetailIntent = new Intent(getActivity(),NewsDetail.class);
                goDetailIntent.putExtra(Intent.EXTRA_TEXT,feed);
                startActivity(goDetailIntent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        upDateNews();
    }

    public void upDateNews()
    {
        GetNewsAyncTask getNewsAyncTask = new GetNewsAyncTask(getActivity(), myNews);
        getNewsAyncTask.execute(NewsContract.NewsHub.NEWS_ID);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.refresh_settings)
        {
            upDateNews();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortOrder = NewsContract.NewsHub._ID +" DESC";
        Log.i("Carrots", sortOrder +" "+ NewsContract.NewsHub._ID.toString());
        Uri newsUri = NewsContract.NewsHub.buildNewsUri(i);
        return new CursorLoader(
                getActivity(),
                newsUri,
                NEWS_COLNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
