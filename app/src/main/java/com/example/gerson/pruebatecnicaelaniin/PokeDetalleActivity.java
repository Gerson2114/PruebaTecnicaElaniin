package com.example.gerson.pruebatecnicaelaniin;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.gerson.models.equipo;
import com.example.gerson.models.pokemon;
import com.example.gerson.models.usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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

public class PokeDetalleActivity extends AppCompatActivity {
    int idE;
    String urlP;
    private String TAG = PokeDetalleActivity.class.getSimpleName();
    ArrayList<pokemon> pokemonList;
    private ListView lv;
    TextView poke_deta_nombre;
    TextView poke_deta_abilidades;
    ImageView imageView;
    Button guardar;
    DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference("usuarios");
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    pokemon p;
    usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poke_detalle);
        setTitle("Pokemon detalle");
        Bundle extras = getIntent().getExtras();
        p = new pokemon();
        pokemonList = new ArrayList<pokemon>();
        idE = extras.getInt("equipoID");
        urlP = extras.getString("url");
        poke_deta_abilidades = (TextView) findViewById(R.id.poke_deta_abilidades);
        poke_deta_nombre = (TextView) findViewById(R.id.poke_deta_nombre);
        imageView = (ImageView) findViewById(R.id.pokemon_imageView);
        guardar = (Button) findViewById(R.id.poke_deta_button);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseUser = firebaseAuth.getCurrentUser();
                String id = firebaseAuth.getCurrentUser().getUid();
                mRootReference.child(id).child("equips").child(String.valueOf(idE)).child("pokemons").setValue(pokemonList);
                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivityForResult(intent, 0);
                finish();
            }
        });
        new PokeData().execute();
    }
    //----------------------Proceso de fondo------------------------------------//
    private class PokeData extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String response = null;
            try {
                URL url = new URL(urlP);
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
                String nombre = jsonObj.getString("name");
                JSONObject sprites = jsonObj.getJSONObject("sprites");
                JSONArray abilities = jsonObj.getJSONArray("abilities");
                String n ="";
                String imagen= sprites.getString("front_default");
                for (int i = 0; i < abilities.length(); i++) {
                    JSONObject c = abilities.getJSONObject(i);
                    JSONObject a = c.getJSONObject("ability");
                    String name = a.getString("name");
                    n = n+" | "+name;
                    // tmp hash map for single contact
                }

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                try {
                    URL urlI = new URL(imagen);
                    imageView.setImageBitmap(BitmapFactory.decodeStream((InputStream)urlI.getContent()));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                poke_deta_nombre.setText("Nombre: "+nombre);
                poke_deta_abilidades.setText("Habilidades: "+ n);
                p.setEquipo(String.valueOf(idE));
                p.setImagen(imagen);
                p.setNombre(nombre);
                pokemonList.add(p);
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

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String id = firebaseAuth.getCurrentUser().getUid();
        mRootReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = new usuario();
                user = dataSnapshot.getValue(usuario.class);
                if(user.equips != null) {
                    for (equipo e : user.equips) {
                        if (e.pokemons != null) {
                            for (pokemon p : e.pokemons) {
                                pokemon po = new pokemon();
                                po.setNombre(p.getNombre());
                                po.setImagen(p.getImagen());
                                pokemonList.add(po);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
