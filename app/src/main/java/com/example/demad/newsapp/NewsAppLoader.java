package com.example.demad.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;
/**
 * Loads a list of news by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class NewsAppLoader extends AsyncTaskLoader<List<NewsApp>> {
    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = NewsAppLoader.class.getSimpleName();
    /**
     * Query URL
     */
    private String mUrl;

    public NewsAppLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is a background thread
     */
    @Override
    public List<NewsApp> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        //Perform the network request, and extract a list of article news.
    List<NewsApp> newsApps = QueryUtils.fetchNewsAppData(mUrl);
        return newsApps;
    }
}
