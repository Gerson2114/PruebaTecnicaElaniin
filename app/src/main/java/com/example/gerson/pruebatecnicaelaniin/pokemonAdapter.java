package com.example.gerson.pruebatecnicaelaniin;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gerson.models.pokemon;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by gerson on 18/01/18.
 */

public class pokemonAdapter extends BaseAdapter {
    static class ViewHolder
    {
        TextView nombre;
        ImageView imageView;
    }

    private static final String TAG = "CustomAdapter";
    private static int convertViewCounter = 0;

    private ArrayList<pokemon> data;
    private LayoutInflater inflater = null;

    public pokemonAdapter(Context c, ArrayList<pokemon> d)
    {
        Log.v(TAG, "Constructing CustomAdapter");

        this.data = d;
        inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount()
    {
        Log.v(TAG, "in getCount()");
        return data.size();
    }

    @Override
    public Object getItem(int position)
    {
        Log.v(TAG, "in getItem() for position " + position);
        return data.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        Log.v(TAG, "in getItemId() for position " + position);
        return position;
    }

    @Override
    public int getViewTypeCount()
    {
        Log.v(TAG, "in getViewTypeCount()");
        return 1;
    }

    @Override
    public int getItemViewType(int position)
    {
        Log.v(TAG, "in getItemViewType() for position " + position);
        return 0;
    }

    @Override
    public void notifyDataSetChanged()
    {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        ViewHolder holder;

        Log.v(TAG, "in getView for position " + position + ", convertView is "
                + ((convertView == null) ? "null" : "being recycled"));

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.list_poke_deta_item, null);

            convertViewCounter++;
            Log.v(TAG, convertViewCounter + " convertViews have been created");

            holder = new ViewHolder();

            holder.nombre = (TextView) convertView
                    .findViewById(R.id.poke_name_deta);
            holder.imageView = (ImageView) convertView.findViewById(R.id.pokemon_imageView);
            convertView.setTag(holder);

        } else
            holder = (ViewHolder) convertView.getTag();

        // Para porde hacer click en el checkbox
        pokemon d = (pokemon) getItem(position);
        // Setting all values in listview
        holder.nombre.setText(data.get(position).getNombre());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            URL urlI = new URL(data.get(position).getImagen());
            holder.imageView.setImageBitmap(BitmapFactory.decodeStream((InputStream)urlI.getContent()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
