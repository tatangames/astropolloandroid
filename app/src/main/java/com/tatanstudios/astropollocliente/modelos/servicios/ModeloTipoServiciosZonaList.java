package com.tatanstudios.astropollocliente.modelos.servicios;

import com.google.gson.annotations.SerializedName;

public class ModeloTipoServiciosZonaList {

    @SerializedName("id")
    public int id;
    @SerializedName("nombre")
    public String nombre;
    @SerializedName("telefono")
    public String telefono;
    @SerializedName("minimo")
    public String minimo;
    @SerializedName("utiliza_minimo")
    public int utilizaMinimo;
    @SerializedName("tiempo")
    public int tiempo;


    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getMinimo() {
        return minimo;
    }

    public int getUtilizaMinimo() {
        return utilizaMinimo;
    }

    public int getTiempo() {
        return tiempo;
    }
}
