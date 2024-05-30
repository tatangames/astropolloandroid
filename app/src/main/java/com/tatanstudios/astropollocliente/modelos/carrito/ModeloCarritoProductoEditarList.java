package com.tatanstudios.astropollocliente.modelos.carrito;

import com.google.gson.annotations.SerializedName;

public class ModeloCarritoProductoEditarList {

    @SerializedName("productoID")
    public Integer productoID;

    @SerializedName("nombre")
    public String nombre;

    @SerializedName("descripcion")
    public String descripcion;

    @SerializedName("cantidad")
    public Integer cantidad;

    @SerializedName("imagen")
    public String imagen;

    @SerializedName("precio")
    public String precio;

    @SerializedName("nota_producto")
    public String nota_producto;

    @SerializedName("utiliza_imagen")
    public Integer utiliza_imagen;

    @SerializedName("utiliza_nota")
    public Integer utiliza_nota;

    @SerializedName("nota")
    public String nota;

    public Integer getProductoID() {
        return productoID;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public String getImagen() {
        return imagen;
    }

    public String getPrecio() {
        return precio;
    }

    public String getNota_producto() {
        return nota_producto;
    }

    public Integer getUtiliza_imagen() {
        return utiliza_imagen;
    }

    public Integer getUtiliza_nota() {
        return utiliza_nota;
    }

    public String getNota() {
        return nota;
    }


}
