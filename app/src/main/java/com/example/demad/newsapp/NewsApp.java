package com.example.demad.newsapp;

/**
 * An {@link NewsApp} object contains information related to a single News.
 */
public class NewsApp {
    /*Article title for news*/
    private String article_title;
    /*Article section for the news*/
    private String article_section;
    /*Date of the article news*/
    private String mDate;
    /*Time of the article news */
//    private long mTime;
    /*Website URL of the article news*/
    private String mUrl;

    public NewsApp(String articleTitle, String articleSection, String date, String url) {
        article_title = articleTitle;
        article_section = articleSection;
        mDate = date;
        mUrl = url;
    }
    /*Returns the article title of the article news*/

    public String getArticle_title() {
        return article_title;
    }
    /*Returns the article section of the article news*/

    public String getArticle_section() {
        return article_section;
    }
    /*Returns the date of the article news */

    public String getDate() {
        return mDate;
    }
    /*Returns the website url of the article news*/

    public String getUrl() {
        return mUrl;
    }
}
