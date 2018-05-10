package com.example.demad.newsapp;

import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;
public class NewsAppActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsApp>> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_app);
    }

    @Override
    public Loader<List<NewsApp>> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<NewsApp>> loader, List<NewsApp> data) {
    }

    @Override
    public void onLoaderReset(Loader<List<NewsApp>> loader) {
    }
}
