package com.example.gerson.pruebatecnicaelaniin;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.gerson.models.equipo;
import com.example.gerson.models.usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference usuarioRef;

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
                TextView textView = (TextView) view.findViewById(R.id.name);
                //Obtiene el texto dentro del TextView.
                String region  = textView.getText().toString();
                showChangeLangDialog(region);
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
    public void showChangeLangDialog(final String region) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.equipo_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setTitle("Custom dialog");
        dialogBuilder.setMessage("Enter text below");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseUser = firebaseAuth.getCurrentUser();

                usuarioRef = mRootReference.child("usuarios");
                usuarioRef.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener(){

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        usuario user = new usuario();
                        user = dataSnapshot.getValue(usuario.class);
                        ArrayList<equipo> elist = new ArrayList<>();
                        equipo e = new equipo();
                        e.setId(firebaseUser.getUid());
                        e.setNombre(edt.getText().toString());
                        e.setRegion(region);
                        elist.add(e);
                        user.equips = elist;
                        usuarioRef.child(firebaseUser.getUid()).setValue(user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

}
