package com.tatanstudios.astropollocliente.modelos.principal;

import com.google.gson.annotations.SerializedName;

public class ModeloCategorias {

    @SerializedName("id")
    public int id;
    @SerializedName("id_servicios")
    public int idServicios;
    @SerializedName("nombre")
    public String nombre;
    @SerializedName("posicion")
    public int posicion;
    @SerializedName("activo")
    public int activo;
    @SerializedName("usa_horario")
    public int usaHorario;
    @SerializedName("hora_abre")
    public String horaAbre;
    @SerializedName("hora_cierra")
    public String horaCierra;
    @SerializedName("imagen")
    public String imagen;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdServicios() {
        return idServicios;
    }

    public void setIdServicios(int idServicios) {
        this.idServicios = idServicios;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public int getActivo() {
        return activo;
    }

    public void setActivo(int activo) {
        this.activo = activo;
    }

    public int getUsaHorario() {
        return usaHorario;
    }

    public void setUsaHorario(int usaHorario) {
        this.usaHorario = usaHorario;
    }

    public String getHoraAbre() {
        return horaAbre;
    }

    public void setHoraAbre(String horaAbre) {
        this.horaAbre = horaAbre;
    }

    public String getHoraCierra() {
        return horaCierra;
    }

    public void setHoraCierra(String horaCierra) {
        this.horaCierra = horaCierra;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
