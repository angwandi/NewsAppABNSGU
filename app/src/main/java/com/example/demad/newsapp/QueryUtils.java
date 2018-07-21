package com.example.demad.newsapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
/**
 * Helper methods related to requesting and receiving news data from the GUARDIAN.
 */
public class QueryUtils {
    /**
     * Tag for log message
     */
    private static final String LOD_TAG = QueryUtils.class.getSimpleName();
    /**
     * Time for time out in milliseconds
     */
    private static final int READ_TIMEOUT = 10000;
    /**
     * Time for connection time out in milliseconds
     */
    private static final int CONNECTION_TIMEOUT = 15000;
    /**
     * Successful response code
     */
    private static final int SUCCESSFUL_RESPONSE_CODE = 200;
    /**
     * keys for the json responses
     */
    private static final String KEY_SECTION_NAME = "sectionName";
    private static final String KEY_JSON_OBJECT_RESPONSE = "response";
    private static final String KEY_JSON_ARRAY_RESULTS = "results";
    private static final String KEY_JSON_ARRAY_TAGS = "tags";
    private static final String KEY_DATE = "webPublicationDate";
    private static final String KEY_TITLE = "webTitle";
    private static final String KEY_URL = "webUrl";

    /**
     * building and manipulating my uri url requests
     */
    public static List<NewsApp> fetchNewsAppData(String requestUrl) {
        //Create URL object
        URL url = createUrl(requestUrl);
        //Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException ignored) {
        }
        // Extract relevant fields from the JSON response and create a list of {@link NewsApp}s
        // Return the list of {@link NewsApp}s
        return extractFeatureFromJson(jsonResponse);
    }

    /**
     * Return new URL object from the given string URL
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException ignored) {
        }
        return url;
    }

    /**
     * Date Helper
     */
    private static String formatDate(String dateData) {
        String guardianJsonDateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat jsonDateFormatter = new SimpleDateFormat(guardianJsonDateFormat, Locale.UK);
        try {
            Date jsonDateToParse = jsonDateFormatter.parse(dateData);
            String resultDate = "MMM d, yyy";
            SimpleDateFormat resultDateFormatter = new SimpleDateFormat(resultDate, Locale.UK);
            return resultDateFormatter.format(jsonDateToParse);
        } catch (ParseException e) {
            return "";
        }
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        //If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == SUCCESSFUL_RESPONSE_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } catch (IOException ignored) {
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * <p>
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link NewsApp} objects that has been built up from
     * <p>
     * parsing the given JSON response.
     */
    private static List<NewsApp> extractFeatureFromJson(String newsAppJson) {
        // Create an empty ArrayList that we can start adding newsApps to
        List<NewsApp> newsApps = new ArrayList<>();
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            //Create a JSONObject from the JSON response string
            JSONObject jsonResponse = new JSONObject(newsAppJson);
            JSONObject jsonResults = jsonResponse.getJSONObject(KEY_JSON_OBJECT_RESPONSE);
            // Extract the JSONArray associated with the key called "results",
            JSONArray resultsArray = jsonResults.getJSONArray(KEY_JSON_ARRAY_RESULTS);
            // For each article news in the newsAppArray, create an {@link NewsApp} object
            for (int i = 0; i < resultsArray.length(); i++) {
                //Get a single article news at position i within the list of newsApps
                JSONObject currentNewsApp = resultsArray.getJSONObject(i);
                // Extract the value for the key called "sectionName"
                String secName = currentNewsApp.getString(KEY_SECTION_NAME);
                //Extract the value for the key called "webPublicationDate".
                String artDate = currentNewsApp.getString(KEY_DATE);
                artDate = formatDate(artDate);
                // Extract the value for the key called "webTitle"
                String artTitle = currentNewsApp.getString(KEY_TITLE);
                //Extract the value for the key called"webUrl"
                String url = currentNewsApp.getString(KEY_URL);
                //Extract the value of the author
                JSONArray tagsArray = currentNewsApp.getJSONArray(KEY_JSON_ARRAY_TAGS);
                String artAuthor;
                if (tagsArray.length() == 0) {
                    artAuthor = null;
                } else {
                    StringBuilder artAuthorBuilder = new StringBuilder();
                    for (int j = 0; j < tagsArray.length(); j++) {
                        JSONObject objectPositionOne = tagsArray.getJSONObject(j);
                        artAuthorBuilder.append(objectPositionOne.getString(KEY_TITLE)).append(". ");
                    }
                    artAuthor = artAuthorBuilder.toString();
                }
                // Create a new {@link NewsApp} object with the artTitle, secName,artDate,artAuthor and url.
                NewsApp newsApp = new NewsApp(artTitle, secName, artDate, artAuthor, url);
                // and url from the JSON response.
                newsApps.add(newsApp);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
        }
        //Return the list of newsApps
        return newsApps;
    }
}

