package com.example.gerson.pruebatecnicaelaniin;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class EquiposActivity extends AppCompatActivity {
    ArrayList<HashMap<String, String>> regionList;
    private ListView lv;
    private String TAG = EquiposActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipos);
        regionList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list_equipo_item);
        new PokeData().execute();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Se busca la referencia del TextView en la vista.
                TextView textView = (TextView) view.findViewById(R.id.equipo_region);
                //Obtiene el texto dentro del TextView.

                String textItemList  = textView.getText().toString();
            }
        });
    }



    //----------------------Proceso de fondo------------------------------------//
    private class PokeData extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String response = null;
            try {
                URL url = new URL("https://pokeapi.co/api/v2/region/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                // read the response
                InputStream in = new BufferedInputStream(conn.getInputStream());
                response = convertStreamToString(in);
            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e(TAG, "ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObj = new JSONObject(s);
                // Getting JSON Array node
                JSONArray regions = jsonObj.getJSONArray("results");
                // looping through All Contacts
                for (int i = 0; i < regions.length(); i++) {
                    JSONObject c = regions.getJSONObject(i);
                    String url = c.getString("url");
                    String name = c.getString("name");

                    // tmp hash map for single contact
                    HashMap<String, String> region = new HashMap<>();
                    region.put("url", url);
                    region.put("name", name);
                    // adding contact to contact list
                    regionList.add(region);
                }
                ListAdapter adapter = new SimpleAdapter(
                        EquiposActivity.this, regionList,
                        R.layout.list_item, new String[]{"name", "url"}, new int[]{R.id.name, R.id.url});

                lv.setAdapter(adapter);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    //------------------------Conventir a cadena--------------------------------//
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    //-----------------------Fin proceso de fondo----------------------------------//


}
