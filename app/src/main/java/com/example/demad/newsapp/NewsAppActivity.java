package com.example.demad.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
public class NewsAppActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<NewsApp>> {
    /**
     * Constant value for the newsApp loader ID.
     * Just in case multiple loaders are required at some stage
     */
    private static final int NEWSAPP_LOADER_ID = 1;
    /**
     * Adapter for the list of news
     */
    private NewsAppAdapter mAdapter;
    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_app);
        // Find a reference to the {@link ListView} in the layout
        ListView newsAppListView = findViewById(R.id.list);
        /*Add header and footer views for the listView*/
        View paddingTopAndBottom = new View(this);
        paddingTopAndBottom.setMinimumHeight(10);
        newsAppListView.addFooterView(paddingTopAndBottom);
        newsAppListView.addHeaderView(paddingTopAndBottom);
        mEmptyStateTextView = findViewById(R.id.empty_view);
        newsAppListView.setEmptyView(mEmptyStateTextView);
        // Create a new adapter that takes an empty list of newsApps as input
        mAdapter = new NewsAppAdapter(this, new ArrayList<NewsApp>());
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsAppListView.setAdapter(mAdapter);
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
        }
    }

    @Override
    public Loader<List<NewsApp>> onCreateLoader(int id, Bundle args) {
        // Create a new loader for the given URL
        return new NewsAppLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsApp>> loader, List<NewsApp> newsApps) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        // Set empty state text to display "No news found."
        mEmptyStateTextView.setText(R.string.no_news);
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
}
