package com.example.gerson.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by gerson on 15/01/18.
 */

public class usuario implements Serializable {
    public String nombre;
    public String correo;
    public ArrayList<equipo> equips;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public ArrayList<equipo> getEquips() {
        return equips;
    }

    public void setEquips(ArrayList<equipo> equips) {
        this.equips = equips;
    }
}
