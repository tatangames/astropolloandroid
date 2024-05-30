package com.tatanstudios.astropollocliente.modelos.principal;

import com.google.gson.annotations.SerializedName;

public class ModeloProductos {

    @SerializedName("id")
    public int id;
    @SerializedName("id_productos")
    public int idProductos;
    @SerializedName("id_servicios")
    public int idServicios;
    @SerializedName("id_categorias")
    public int idCategorias;
    @SerializedName("nombre")
    public String nombre;
    @SerializedName("imagen")
    public String imagen;
    @SerializedName("descripcion")
    public String descripcion;
    @SerializedName("precio")
    public String precio;
    @SerializedName("activo")
    public int activo;
    @SerializedName("posicion")
    public int posicion;
    @SerializedName("utiliza_nota")
    public int utilizaNota;
    @SerializedName("nota")
    public Object nota;
    @SerializedName("utiliza_imagen")
    public int utilizaImagen;


    public int getId() {
        return id;
    }

    public int getIdProductos() {
        return idProductos;
    }

    public int getIdServicios() {
        return idServicios;
    }

    public int getIdCategorias() {
        return idCategorias;
    }

    public String getNombre() {
        return nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getPrecio() {
        return precio;
    }

    public int getActivo() {
        return activo;
    }

    public int getPosicion() {
        return posicion;
    }

    public int getUtilizaNota() {
        return utilizaNota;
    }

    public Object getNota() {
        return nota;
    }

    public int getUtilizaImagen() {
        return utilizaImagen;
    }
}
