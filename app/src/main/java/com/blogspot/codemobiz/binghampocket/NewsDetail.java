package com.blogspot.codemobiz.binghampocket;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ShareActionProvider;
import android.widget.TextView;


public class NewsDetail extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private static String SHARE_TAG = "BHU_POCKET";
        private String myNewsStr;
        public PlaceholderFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.detail_menu, menu);

            MenuItem menuItem = menu.findItem(R.id.action_share);
            android.support.v7.widget.ShareActionProvider shareActionProvider = (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            if(shareActionProvider != null)
            {
                shareActionProvider.setShareIntent(shareNewsDetail());
            }
        }

        private Intent shareNewsDetail()
        {
            Intent shareNewsIntent = new Intent(Intent.ACTION_SEND);
            shareNewsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareNewsIntent.setType("text/plain");
            shareNewsIntent.putExtra(Intent.EXTRA_TEXT, myNewsStr+"\n\nShare via Bingham Pocket");
            return shareNewsIntent;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Intent intent = getActivity().getIntent();
            View rootView = inflater.inflate(R.layout.fragment_news_detail, container, false);
            if(intent != null && intent.hasExtra(intent.EXTRA_TEXT)) {
                myNewsStr = intent.getStringExtra(intent.EXTRA_TEXT);
                TextView feedFrmLst = (TextView)rootView.findViewById(R.id.display_frm_list);
                feedFrmLst.setText(myNewsStr);
            }
            return rootView;
        }
    }
}
