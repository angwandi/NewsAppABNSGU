package com.example.demad.newsapp;

/**
 * An {@link NewsApp} object contains information related to a single News.
 */
public class NewsApp {
    /**
     * Article title for news
     */
    private String mArticle_title;
    /**
     * Article section for the news
     */
    private String mArticle_section;
    /**
     * Date of the article news
     */
    private String mDate;
    /**
     * Author of the article news
     */
    private String mAuthor;
    /**
     * Website URL of the article news
     */
    private String mUrl;

    /**
     * Constructs a new {@link NewsApp} object
     *
     * @param articleTitle   is the title of the article
     * @param articleSection is the Section of the article news
     * @param date           is the date of publication of the news on the GUARDIAN website
     * @param author         is the author of the article
     * @param url            is the url of the article news
     */
    NewsApp(String articleTitle, String articleSection, String date, String author, String url) {
        mArticle_title = articleTitle;
        mArticle_section = articleSection;
        mDate = date;
        mAuthor = author;
        mUrl = url;
    }

    /**
     * Returns the article title of the article news
     */
    public String getArticle_title() {
        return mArticle_title;
    }

    /**
     * Returns the article section of the article news
     */
    public String getArticle_section() {
        return mArticle_section;
    }

    /**
     * Returns the date of the article news
     */
    public String getDate() {
        return mDate;
    }

    /**
     * Returns the author of the article
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * Returns the website url of the article news
     */
    public String getUrl() {
        return mUrl;
    }
}
