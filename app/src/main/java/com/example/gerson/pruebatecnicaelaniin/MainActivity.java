package com.example.gerson.pruebatecnicaelaniin;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Activity activity;

    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    // URL to get contacts JSON
    private static String url = "https://api.androidhive.info/contacts/";
    //ArrayList<HashMap<String, String>> regionList;
    ArrayList<HashMap<String, String>> equipoList;

    DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference("usuarios");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        activity = this;
        //--------------------Verificando si esta logeado---------------------//
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        //Now check if this user is null

        if (firebaseUser == null) {
            //send user to the login page
            try {
                Intent intent = new Intent(getApplication(), AuthUIActivity.class);
                startActivityForResult(intent, 0);
                return;
            } catch (Exception e) {
                e.getMessage();
            }

        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), EquiposActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //-------------setiando el nombre de usuario y correo-----------------------------
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        TextView name = (TextView) header.findViewById(R.id.username);
        TextView email = (TextView)header.findViewById(R.id.email);
        ImageView imagen = (ImageView)header.findViewById(R.id.nav_imageView);
        imagen.setImageURI(null);
        imagen.setImageURI(firebaseUser.getPhotoUrl());
        name.setText(firebaseUser.getDisplayName());
        email.setText(firebaseUser.getEmail());
        //--------Verificando que el usuari tenga equipos------------
        String id = firebaseAuth.getCurrentUser().getUid();
        mRootReference.child(id).addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuario user = dataSnapshot.getValue(usuario.class);
                if(user.equips != null){
                    for (equipo e: user.equips) {
                        HashMap<String, String> equipos = new HashMap<>();
                        equipos.put("id", e.getId());
                        equipos.put("name", e.getNombre());
                        equipos.put("Region",e.getRegion());
                        equipoList.add(equipos);
                    }
                    ListAdapter adapter = new SimpleAdapter(
                            MainActivity.this, equipoList,
                            R.layout.list_equipos_item, new String[]{"name", "Region"}, new int[]{R.id.equipo_name, R.id.equipo_region});
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //regionList = new ArrayList<>();
        equipoList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);
        //new PokeData().execute();
    }

    //----------------------Proceso de fondo------------------------------------//
/*    private class PokeData extends AsyncTask<Void, Void, String> {

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
                        MainActivity.this, regionList,
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
    //-----------------------Fin proceso de fondo----------------------------------//*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_send) {
            firebaseAuth.signOut();
            startActivity(new Intent(this, AuthUIActivity.class));
            finish();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
