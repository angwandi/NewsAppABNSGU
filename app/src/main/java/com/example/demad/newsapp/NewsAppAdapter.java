package com.example.demad.newsapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.chip.Chip;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
/**
 * Create By 2ms on 10/05/2018
 * An {@link NewsAppAdapter} knows how to create a list item layout for each news
 * in the data source (a list of {@link NewsApp} objects).
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
    NewsAppAdapter(Context context, List<NewsApp> newsApps) {
        super(context, 0, newsApps);
    }

    /**
     * Returns a list item view that displays information about the article news at the given position
     * in the list of article news.
     */
    @NonNull
    @SuppressLint("ViewHolder")
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItemView;
        listItemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_news_app_list_items, parent, false);
        // Find the article news at the given position in the list of newsApps.
        NewsApp currentNewsApp;
        currentNewsApp = getItem(position);
        //Find the TextView with ID article_title
        TextView articleTitleTextView = listItemView.findViewById(R.id.article_tile);
        assert currentNewsApp != null;
        articleTitleTextView.setText(currentNewsApp.getArticle_title());
        //Find the TextView with ID article_section
        TextView articleSectionChip = listItemView.findViewById(R.id.article_section);
        articleSectionChip.setText(currentNewsApp.getArticle_section());
        //Find the TextView with ID date
        TextView dateTextView = listItemView.findViewById(R.id.article_date);
        dateTextView.setText(currentNewsApp.getDate());
//        Find the TextView with the ID author
        TextView authorTextView = listItemView.findViewById(R.id.author);
        authorTextView.setText(currentNewsApp.getAuthor());
        return listItemView;
    }
}
