package com.tatanstudios.astropollocliente.modelos.perfil;

public class ModelosPerfil {

    public Integer posicion;


    public String nombre;


    public ModelosPerfil(Integer posicion, String nombre) {
        this.posicion = posicion;
        this.nombre = nombre;
    }

    public Integer getPosicion() {
        return posicion;
    }


    public String getNombre() {
        return nombre;
    }
}
