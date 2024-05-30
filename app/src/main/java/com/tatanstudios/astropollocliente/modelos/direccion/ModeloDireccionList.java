package com.tatanstudios.astropollocliente.modelos.direccion;

import com.google.gson.annotations.SerializedName;

public class ModeloDireccionList {

    @SerializedName("id")
    private int idDireccion;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("direccion")
    private String direccion;

    @SerializedName("numero_casa")
    private String numeroCasa;

    @SerializedName("punto_referencia")
    private String puntoReferencia;

    @SerializedName("telefono")
    private String telefono;

    @SerializedName("seleccionado")
    private int seleccionado;

    @SerializedName("minimocompra")
    private String minimoCompra;


    public String getMinimoCompra() {
        return minimoCompra;
    }

    public int getIdDireccion() {
        return idDireccion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getNumeroCasa() {
        return numeroCasa;
    }

    public String getPuntoReferencia() {
        return puntoReferencia;
    }

    public String getTelefono() {
        return telefono;
    }

    public int getSeleccionado() {
        return seleccionado;
    }

}
