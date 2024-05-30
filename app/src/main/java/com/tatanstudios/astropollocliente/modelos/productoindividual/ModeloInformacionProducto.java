package com.tatanstudios.astropollocliente.modelos.productoindividual;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModeloInformacionProducto {
    @SerializedName("success")
    public Integer success;

    @SerializedName("producto")
    public List<ModeloInformacionProductoList> producto = null;

    public Integer getSuccess() {
        return success;
    }

    public List<ModeloInformacionProductoList> getProducto() {
        return producto;
    }

}
