package com.tatanstudios.astropollocliente.modelos.productos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModeloSubCategorias {

    @SerializedName("success")
    public Integer success;

    @SerializedName("mensaje")
    public String mensaje;


    @SerializedName("productos")
    public List<ModeloProductosArray> productos = null;


    public Integer getSuccess() {
        return success;
    }

    public String getMensaje() {
        return mensaje;
    }

    public List<ModeloProductosArray> getProductos() {
        return productos;
    }
}
