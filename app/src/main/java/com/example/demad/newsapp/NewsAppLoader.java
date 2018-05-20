package com.example.demad.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;
/**
 * Loads a list of news by using an AsyncTaskLoader to perform the
 * network request to the given URL.
 */
public class NewsAppLoader extends AsyncTaskLoader<List<NewsApp>> {
    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link NewsAppLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    NewsAppLoader(Context context, String url) {
        super(context);
        mUrl = url;
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
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of article news.
        return QueryUtils.fetchNewsAppData(mUrl);
    }
}
