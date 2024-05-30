package com.tatanstudios.astropollocliente.modelos.carrito;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ModeloCarrito {

    @SerializedName("success")
    public Integer success;

    @SerializedName("subtotal")
    public String subtotal;

    @SerializedName("conteo")
    public int conteo;

    @SerializedName("estadoProductoGlobal")
    public Integer estadoProductoGlobal;


    public int getConteo() {
        return conteo;
    }

    @SerializedName("producto")
    public ArrayList<ModeloCarritoList> producto = null;


    public Integer getSuccess() {
        return success;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public Integer getEstadoProductoGlobal() {
        return estadoProductoGlobal;
    }

    public ArrayList<ModeloCarritoList> getProducto() {
        return producto;
    }
}
