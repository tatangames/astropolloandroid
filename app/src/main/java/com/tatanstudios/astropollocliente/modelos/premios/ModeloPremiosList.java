package com.tatanstudios.astropollocliente.modelos.premios;

import com.google.gson.annotations.SerializedName;

public class ModeloPremiosList {

    @SerializedName("id")
    public Integer id;

    @SerializedName("id_servicio")
    public Integer idServicio;

    @SerializedName("nombre")
    public String nombre;

    @SerializedName("puntos")
    public String puntos;

    @SerializedName("activo")
    public Integer activo;

    @SerializedName("seleccionado")
    public Integer seleccionado;


    public Integer getSeleccionado() {
        return seleccionado;
    }

    public Integer getId() {
        return id;
    }

    public Integer getIdServicio() {
        return idServicio;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPuntos() {
        return puntos;
    }

    public Integer getActivo() {
        return activo;
    }
}
