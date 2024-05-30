package com.tatanstudios.astropollocliente.modelos.modotesteo;

import com.google.gson.annotations.SerializedName;
import com.tatanstudios.astropollocliente.modelos.carrito.ModeloCarritoList;

import java.util.ArrayList;

public class ModeloProductosTesteo {


    @SerializedName("success")
    public Integer success;


    @SerializedName("listado")
    public ArrayList<ModeloProductosTesteoList> producto = null;

    public Integer getSuccess() {
        return success;
    }

    public ArrayList<ModeloProductosTesteoList> getProducto() {
        return producto;
    }
}
