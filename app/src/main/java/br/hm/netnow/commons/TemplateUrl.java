package br.hm.netnow.commons;

import android.util.Log;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class TemplateUrl {

    private static final String LOG_TAG = TemplateUrl.class.getSimpleName();

    public static  void query(String urlString, JSONCallback callback)
            throws IOException, JSONException {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        InputStream is = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));

            StringBuilder buffer = new StringBuilder();
            String row = null;
            while ((row = reader.readLine()) != null) {
                buffer.append(row).append('\n');
            }

            JSONObject jsonObject = new JSONObject(buffer.toString());
            JSONObject response = (JSONObject) jsonObject.get("response");
            if (response != null)

            {
                JSONArray docs = (JSONArray) response.get("docs");
                if (docs.length() > 0) {
                    //List<T> result = new ArrayList<T>();
                    for (int rowNum = 0; rowNum < docs.length(); rowNum++) {
                        Object jsonRow = docs.get(rowNum);
                        callback.processRow(jsonRow, rowNum);
                        //result.add(callback.processRow(jsonRow, rowNum));
                    }
                    //callback.postProcess(result);
                }
            }
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

    }

    public static byte[] urlToByteArray(String urlString) {
        HttpURLConnection urlConnection = null;
        BufferedInputStream bis = null;
        try {

            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();


            InputStream is = urlConnection.getInputStream();
            bis = new BufferedInputStream(is);

            ByteArrayBuffer baf = new ByteArrayBuffer(500);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            return baf.toByteArray();
        } catch (Exception e) {
            Log.d(LOG_TAG, "Error: " + e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }
}
