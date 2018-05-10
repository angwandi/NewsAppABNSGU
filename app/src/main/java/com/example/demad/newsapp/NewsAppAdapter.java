package com.example.demad.newsapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
/**
 * An {@link NewsAppAdapter} knows how to create a list item layout for each news
 * in the data source (a list of {@link NewsApp} objects).
 * <p>
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class NewsAppAdapter extends ArrayAdapter<NewsApp> {
    /**
     * Constructs a new {@link NewsAppAdapter}.
     *
     * @param context  of the app
     * @param newsApps is the list of article news, which is the data source of the adapter
     */
    public NewsAppAdapter(Context context, List<NewsApp> newsApps) {
        super(context, 0, newsApps);
    }

    /**
     * Returns a list item view that displays information about the article news at the given position
     * in the list of article news.
     */
    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView;
        listItemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_news_app_list_items, parent, false);
        // Find the article news at the given position in the list of newsApps.
        NewsApp currentNewsApp = getItem(position);
        //Find the TextView with ID article_title
        TextView articleTitleTextView = listItemView.findViewById(R.id.article_tile);
        articleTitleTextView.setText(currentNewsApp.getArticle_title());
        //Find the TextView with ID article_section
        TextView articleSectionTextView = listItemView.findViewById(R.id.article_section);
        articleSectionTextView.setText(currentNewsApp.getArticle_section());
        //Find the TextView with ID date
        TextView dateTextView = listItemView.findViewById(R.id.article_date);
        dateTextView.setText(currentNewsApp.getDate());
        return listItemView;
    }
}
