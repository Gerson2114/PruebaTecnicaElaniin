package com.example.gerson.models;

import java.util.ArrayList;

/**
 * Created by gerson on 15/01/18.
 */

public class equipo {
    public String id;
    public String region;
    public String nombre;
    public ArrayList<pokemon> pokemonLis;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<pokemon> getPokemonLis() {
        return pokemonLis;
    }

    public void setPokemonLis(ArrayList<pokemon> pokemonLis) {
        this.pokemonLis = pokemonLis;
    }
}
