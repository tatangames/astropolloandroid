package com.tatanstudios.astropollocliente.modelos.direccion;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModeloDireccion {

    @SerializedName("success")
    public Integer success;


    @SerializedName("titulo")
    public String titulo;

    @SerializedName("mensaje")
    public String mensaje;


    public String getTitulo() {
        return titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    @SerializedName("direcciones")
    public List<ModeloDireccionList> direcciones = null;

    public Integer getSuccess() {
        return success;
    }

    public List<ModeloDireccionList> getDirecciones() {
        return direcciones;
    }
}
