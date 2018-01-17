package com.example.gerson.pruebatecnicaelaniin;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
    EditText edt;
    // URL to get contacts JSON
    private static String url = "https://api.androidhive.info/contacts/";
    //ArrayList<HashMap<String, String>> regionList;
    ArrayList<HashMap<String, String>> equipoList;
    DatabaseReference usuarioRef;

    DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference("usuarios");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Equipos");

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
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            URL urlI = new URL(firebaseUser.getPhotoUrl().toString());
            imagen.setImageBitmap(BitmapFactory.decodeStream((InputStream)urlI.getContent()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        name.setText(firebaseUser.getDisplayName());
        email.setText(firebaseUser.getEmail());

        equipoList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list_equipos);
        ListAdapter adapter = new SimpleAdapter(
                MainActivity.this, equipoList,
                R.layout.list_equipos_item, new String[]{"name", "Region"}, new int[]{R.id.equipo_name, R.id.equipo_region});

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Se busca la referencia del TextView en la vista.
                int posicion = i;
                TextView nombre = (TextView) view.findViewById(R.id.equipo_name);
                TextView region = (TextView) view.findViewById(R.id.equipo_region);
                //Obtiene el texto dentro del TextView.
                String r  = region.getText().toString();
                String n  = nombre.getText().toString();
                showChangeLangDialog(posicion,r,n);
            }
        });
        lv.setOnLongClickListener(new AdapterView.OnLongClickListener(){

            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });
    }

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

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        usuarioRef = mRootReference.child("equips");
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

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showChangeLangDialog(int p,String r, String n) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.equipo_dialog, null);
        dialogBuilder.setView(dialogView);
        final int po = p;
        edt = (EditText) dialogView.findViewById(R.id.edit1);
        edt.setText(equipoList.get(po).get("name"));

        dialogBuilder.setTitle("Editar Equipo");
        dialogBuilder.setMessage("Ingrese un nuevo nombre");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseUser = firebaseAuth.getCurrentUser();
                if(edt.getText().toString() == "" || edt.getText().toString() ==null){
                    Toast.makeText(MainActivity.this, "Ingrese un nombre de equipo para guardar si lo desea",
                            Toast.LENGTH_SHORT).show();
                }else{
                    ArrayList<equipo> elist = new ArrayList<>();
                    equipo e = new equipo();
                    e.setId(equipoList.get(po).get("id"));
                    e.setNombre(edt.getText().toString());
                    e.setRegion(equipoList.get(po).get("Region"));
                    String pp = String.valueOf(po);
                    usuarioRef.child(firebaseUser.getUid()).child("equips").child(pp).setValue(e);
                    Intent intent = new Intent(getApplication(), PokemonActivity.class);
                    startActivityForResult(intent, 0);
                    finish();
                }
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
