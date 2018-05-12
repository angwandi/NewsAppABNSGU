package com.example.demad.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.List;
/**
 * Loads a list of news by using an AsyncTaskLoader to perform the
 * network request to the given URL.
 */
public class NewsAppLoader extends AsyncTaskLoader<List<NewsApp>> {
    /**
     * Tag for log messages
     */
    private static final String LOD_TAG = NewsAppLoader.class.getSimpleName();

    /**
     * Constructs a new {@link NewsAppLoader}.
     * @param context of the activity
     */
    NewsAppLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    /**
     * This is a background thread
     */
    @Override
    public List<NewsApp> loadInBackground() {
        List<NewsApp> newsApps = null;
        try {
            URL url = QueryUtils.createUrl();
            //Perform the network request, and extract a list of article news.
            String jsonResponse = QueryUtils.makeHttpRequest(url);
            newsApps = QueryUtils.extractFeatureFromJson(jsonResponse);
        } catch (IOException e) {
            Log.e(LOD_TAG, "Problem Making HTTP request");
        }
        return newsApps;
    }
}
