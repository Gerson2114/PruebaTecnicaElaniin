package com.example.gerson.pruebatecnicaelaniin;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.gerson.models.equipo;
import com.example.gerson.models.pokemon;
import com.example.gerson.models.usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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

public class PokemonActivity extends AppCompatActivity {
    //private String TAG = PokemonActivity.class.getSimpleName();
    ArrayList<pokemon> pokemonList;
    //private ListView lv;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    DatabaseReference usuarioRef;
    private Activity activity;

    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    EditText edt;
    int idE;
    usuario user;

    DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference("usuarios");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);
        setTitle("Pokemos");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pokemonList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list_pokemonss);
/*
        ListAdapter adapter = new SimpleAdapter(
                PokemonActivity.this, pokemonList,
                R.layout.list_poke_deta_item, new String[]{"nombre", "imagen"}, new int[]{R.id.poke_name_deta, R.id.pokemon_imageView});
*/
        pokemonAdapter adapter = new pokemonAdapter(PokemonActivity.this,pokemonList);
        lv.setAdapter(adapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), PokemonListactivityActivity.class);
                intent.putExtra("equipoID",idE);
                startActivityForResult(intent, 0);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String id = firebaseAuth.getCurrentUser().getUid();
        Bundle extras = getIntent().getExtras();
        idE = extras.getInt("equipoID");
/*        Query myTopPostsQuery = mRootReference.child(id).child("equips").child(String.valueOf(idE)).child("pokemon");
        myTopPostsQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getChildren() != null) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        // TODO: handle the post
                        pokemon p = new pokemon();
                        p = postSnapshot.getValue(pokemon.class);
                        p.setNombre(p.getNombre());
                        p.setImagen(p.getImagen());
                        pokemonList.add(p);
                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
            // TODO: implement the ChildEventListener methods as documented above
            // ...
        });*/


        //---------------------------------------------------------------------------//
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
