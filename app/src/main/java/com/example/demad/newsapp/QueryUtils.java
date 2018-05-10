package com.example.demad.newsapp;

import android.text.TextUtils;
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
import java.util.ArrayList;
import java.util.List;
/**
 * Helper methods related to requesting and receiving news data from the GUARDIAN.
 */
public class QueryUtils {
    /**
     * Tag for log message
     */
    private static final String LOD_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the GUARDIAN dataset and return a list of {@link NewsApp} objects.
     */
    public static List<NewsApp> fetchNewsAppData(String requestUrl) {
        //Create a URL object
        URL url = createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOD_TAG, "Problem making HTTP request.", e);
        }
        //Extract relevant fields from the JSON response and create a list of {@link NewsApp}s
        //Return the list of {@link NewsApp}s
        return extractFeatureFromJson(jsonResponse);
    }
    /**
     * Return new URL object from the given string URL
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOD_TAG, "Problem building the URL", e);
        }
        return url;
    }

    /**
     * Make an HTTP request vto the given URL and return a String as the response.
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
            urlConnection.setReadTimeout(10000/* milliseconds*/);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOD_TAG, "Error response code:" + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOD_TAG, "Problem retrieving the Article News Json result.", e);
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
        //If teh JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsAppJson)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding newsApps to
        List<NewsApp> newsApps = new ArrayList<>();
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            //Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsAppJson);
            // Extract the JSONArray associated with the key called "results",
            // which represents a list of results (or newsApps).
            JSONArray newsAppArray = baseJsonResponse.getJSONArray("results");
            // For each article news in the newsAppArray, create an {@link NewsApp} object
            for (int i = 0; i < newsAppArray.length(); i++) {
                //Get a single article news at position i within the list of newsApps
                JSONObject currentNewsApp = newsAppArray.getJSONObject(i);
                // For a given article news, extract the JSONObject associated with the
                // key called "results", which represents a list of all results
                // for that article news.
                JSONObject results = currentNewsApp.getJSONObject("results");
                // Extract the value for the key called "webTitle"
                String artTitle = results.getString("webTitle");
                // Extract the value for the key called "sectionName"
                String secName = results.getString("sectionName");
                //Extract the value for the key called "webPublicationDate".
                String artDate = results.getString("webPublicationDate");
                //Extract the value for the key called"webUrl"
                String url = results.getString("webUrl");
                // Create a new {@link NewsApp} object with the artTitle, secName,artDate
                // and url from the JSON response.
                NewsApp newsApp = new NewsApp(artTitle, secName, artDate, url);
                //Add the new {@link NewsApp} to the list of newsApps.
                newsApps.add(newsApp);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the newsApp JSON results", e);
        }
        //Return the list of newsApps
        return newsApps;
    }
}

