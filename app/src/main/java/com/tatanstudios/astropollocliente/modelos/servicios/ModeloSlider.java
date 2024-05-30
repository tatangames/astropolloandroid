package com.tatanstudios.astropollocliente.modelos.servicios;

import com.google.gson.annotations.SerializedName;

public class ModeloSlider {

    @SerializedName("id")
    public int id;
    @SerializedName("id_producto")
    public int idProducto;
    @SerializedName("id_servicios")
    public int idServicios;
    @SerializedName("nombre")
    public String nombre;
    @SerializedName("imagen")
    public String imagen;
    @SerializedName("posicion")
    public int posicion;
    @SerializedName("redireccionamiento")
    public int redireccionamiento;
    @SerializedName("usa_horario")
    public int usaHorario;
    @SerializedName("hora_abre")
    public String horaAbre;
    @SerializedName("hora_cierra")
    public String horaCierra;
    @SerializedName("activo")
    public int activo;


    public int getId() {
        return id;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public int getIdServicios() {
        return idServicios;
    }

    public String getNombre() {
        return nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public int getPosicion() {
        return posicion;
    }

    public int getRedireccionamiento() {
        return redireccionamiento;
    }

    public int getUsaHorario() {
        return usaHorario;
    }

    public String getHoraAbre() {
        return horaAbre;
    }

    public String getHoraCierra() {
        return horaCierra;
    }

    public int getActivo() {
        return activo;
    }
}
