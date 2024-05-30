package com.tatanstudios.astropollocliente.modelos.modotesteo;

import com.google.gson.annotations.SerializedName;
import com.tatanstudios.astropollocliente.modelos.carrito.ModeloCarritoList;

import java.util.ArrayList;

public class ModeloProductosTesteoList {




    @SerializedName("id")
    public Integer id;

    @SerializedName("nombrepro")
    public String nombrepro;

    @SerializedName("precio")
    public String precio;

    @SerializedName("imagen")
    public String imagen;

    @SerializedName("utiliza_imagen")
    public Integer utilizaImagen;


    @SerializedName("descripcion")
    public String descripcion;


    @SerializedName("idpro")
    public Integer idpro;


    public Integer getIdpro() {
        return idpro;
    }

    public Integer getId() {
        return id;
    }

    public String getNombrepro() {
        return nombrepro;
    }

    public String getPrecio() {
        return precio;
    }

    public String getImagen() {
        return imagen;
    }

    public Integer getUtilizaImagen() {
        return utilizaImagen;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
