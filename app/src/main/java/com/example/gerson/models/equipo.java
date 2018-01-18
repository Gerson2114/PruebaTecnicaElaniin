package com.example.gerson.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by gerson on 15/01/18.
 */

public class equipo implements Serializable {
    public String id;
    public String region;
    public String nombre;
    public ArrayList<pokemon> pokemons;

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

    public ArrayList<pokemon> getPokemons() {
        return pokemons;
    }

    public void setPokemons(ArrayList<pokemon> pokemons) {
        this.pokemons = pokemons;
    }
}
