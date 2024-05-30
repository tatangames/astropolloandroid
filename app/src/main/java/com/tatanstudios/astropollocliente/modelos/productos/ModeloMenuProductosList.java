package com.tatanstudios.astropollocliente.modelos.productos;

import com.google.gson.annotations.SerializedName;

public class ModeloMenuProductosList {

    @SerializedName("id")
    public int idProducto;

    @SerializedName("nombre")
    public String nombre;

    @SerializedName("descripcion")
    public String descripcion;

    @SerializedName("imagen")
    public Object imagen;

    @SerializedName("precio")
    public String precio;

    @SerializedName("utiliza_imagen")
    public int utiliza_imagen;

    public ModeloMenuProductosList(int idProducto, String nombre, String descripcion, Object imagen, String precio, int utiliza_imagen) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.precio = precio;
        this.utiliza_imagen = utiliza_imagen;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Object getImagen() {
        return imagen;
    }

    public String getPrecio() {
        return precio;
    }

    public int getUtiliza_imagen() {
        return utiliza_imagen;
    }
}
