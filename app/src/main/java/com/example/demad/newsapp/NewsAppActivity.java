package com.example.demad.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
public class NewsAppActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<NewsApp>>, SharedPreferences.OnSharedPreferenceChangeListener {
    /**
     * URL for earthquake data from the GUARDIAN data set
     */
    private static final String GUARDIAN_REQUEST_URL;

    static {
        GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search";
    }

    /**
     * API student key
     */
    private static final String API_STUDENT_KEY = "ee7fcfa8-a253-432e-9e44-80655700e71a";
    /**
     * Constant value for the newsApp loader ID.
     * Just in case multiple loaders are required at some stage
     */
    private static final int NEWSAPP_LOADER_ID = 1;
    public static final String JSON = "json";
    /**
     * Adapter for the list of news
     */
    public NewsAppAdapter mAdapter;
    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_app);
        // Find a reference to the {@link ListView} in the layout
        ListView newsAppListView = findViewById(R.id.list);
        mEmptyStateTextView = findViewById(R.id.empty_view);
        newsAppListView.setEmptyView(mEmptyStateTextView);
        // Create a new adapter that takes an empty list of newsApps as input
        mAdapter = new NewsAppAdapter(this, new ArrayList<NewsApp>());
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsAppListView.setAdapter(mAdapter);
        // Obtain a reference to the SharedPreferences file for this app
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // And register to be notified of preference changes
        // So we know when the user has adjusted the query settings
        prefs.registerOnSharedPreferenceChangeListener(this);
        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected article news.
        newsAppListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Find the current article news that was clicked on
                NewsApp currentNewsApp = mAdapter.getItem(position);
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsAppUri;
                assert currentNewsApp != null;
                newsAppUri = Uri.parse(currentNewsApp.getUrl());
                // Create a new intent to view the article news URI
                Intent webIntent = new Intent(Intent.ACTION_VIEW, newsAppUri);
                // Send the intent to launch a new activity
                startActivity(webIntent);
            }
        });
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWSAPP_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
            mEmptyStateTextView.setBackgroundResource(R.drawable.text_background);
            mEmptyStateTextView.setPadding(16, 16, 16, 16);
            mEmptyStateTextView.setTextSize(24);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.settings_item_per_page_key)) || key.equals(getString(R.string.settings_topic_category_key))) {
            // Clear the ListView as a new query will be kicked off
            mAdapter.clear();
            // Hide the empty state text view as the loading indicator will be displayed
            mEmptyStateTextView.setVisibility(View.GONE);
            // Show the loading indicator while new data is being fetched
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.VISIBLE);
            // Restart the loader to re-query the GUARDIAN as the query settings have been updated
            getLoaderManager().restartLoader(NEWSAPP_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<List<NewsApp>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String itemPerPage = sharedPreferences.getString(
                getString(R.string.settings_item_per_page_key),
                getString(R.string.settings_item_per_page_default));
        String topicCategory = sharedPreferences.getString(
                getString(R.string.settings_topic_category_key),
                getString(R.string.settings_topic_category_by_default));
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter(getString(R.string.format), JSON);
        //can be oldest,newest or relevance(default where q params is specified)
        uriBuilder.appendQueryParameter(getString(R.string.order_by), getString(R.string.newest));
        /*can be author,isbn,basic-prefix,...*/
        uriBuilder.appendQueryParameter(getString(R.string.show_reference), getString(R.string.author));
        /*can be all,contributor,keyword,newspaper-book,publication,series,tone,type,...*/
        uriBuilder.appendQueryParameter(getString(R.string.show_tags), getString(R.string.contributor));
        /*language parameter(ISO language code:fr,en)*/
        uriBuilder.appendQueryParameter(getString(R.string.language), getString(R.string.default_english));
        //default items per page is 10 but can get more(1-50)!!
        uriBuilder.appendQueryParameter(getString(R.string.page_size), itemPerPage);
        if (!topicCategory.equals(getString(R.string.settings_topic_category_by_default))) {
            uriBuilder.appendQueryParameter(getString(R.string.section), topicCategory);
        }
        //q parameter can be something like education,debate,economy,immigration,...
        //can combine debate AND economy as well(can use these operators :AND,OR,NOT)
        uriBuilder.appendQueryParameter(getString(R.string.key_q), "");
        //Free student key from the Guardian API website
        uriBuilder.appendQueryParameter(getString(R.string.api_key), API_STUDENT_KEY);
        return new NewsAppLoader(this, uriBuilder.toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onLoadFinished(Loader<List<NewsApp>> loader, List<NewsApp> newsApps) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        // Set empty state text to display "No news found."
        mEmptyStateTextView.setText(R.string.no_news);
        mEmptyStateTextView.setBackgroundResource(R.drawable.text_background);
        mEmptyStateTextView.setPadding(16, 16, 16, 16);
        mEmptyStateTextView.setTextSize(24);
        // If there is a valid list of {@link NewsApp}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (newsApps != null && !newsApps.isEmpty()) {
            //This will stop duplicate update of LisView after you click on item of ListView and come back to the NewsApp
            mAdapter.setNotifyOnChange(false);
            mAdapter.clear();
            mAdapter.setNotifyOnChange(true);
            mAdapter.addAll(newsApps);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsApp>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
