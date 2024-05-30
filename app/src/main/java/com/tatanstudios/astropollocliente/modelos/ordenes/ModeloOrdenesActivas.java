package com.tatanstudios.astropollocliente.modelos.ordenes;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModeloOrdenesActivas {


    @SerializedName("success")
    public Integer success;

    @SerializedName("ordenes")
    public List<ModeloOrdenesActivasList> ordenes = null;

    @SerializedName("mensaje")
    public String mensaje;

    @SerializedName("hayordenes")
    public int conteo;


    public int getConteo() {
        return conteo;
    }

    public String getMensaje() {
        return mensaje;
    }
    public Integer getSuccess() {
        return success;
    }

    public List<ModeloOrdenesActivasList> getOrdenes() {
        return ordenes;
    }
}
