package com.tatanstudios.astropollocliente.modelos.carrito;

import com.google.gson.annotations.SerializedName;

public class ModeloCarritoTemporal {


    @SerializedName("success")
    public Integer success;

    @SerializedName("nombre")
    public String nombre;

    @SerializedName("mensaje")
    public String mensaje;


    @SerializedName("titulo")
    public String titulo;


    public Integer getSuccess() {
        return success;
    }

    public String getNombre() {
        return nombre;
    }


    public String getMensaje() {
        return mensaje;
    }

    public String getTitulo() {
        return titulo;
    }
}
