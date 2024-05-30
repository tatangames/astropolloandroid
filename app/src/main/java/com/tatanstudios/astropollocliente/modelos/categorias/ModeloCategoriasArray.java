package com.tatanstudios.astropollocliente.modelos.categorias;

import com.google.gson.annotations.SerializedName;
import com.tatanstudios.astropollocliente.modelos.principal.ModeloCategorias;

import java.util.ArrayList;
import java.util.List;

public class ModeloCategoriasArray {

    @SerializedName("success")
    public Integer success;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("categorias")
    public ArrayList<ModeloCategorias> categorias;


    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public ArrayList<ModeloCategorias> getCategorias() {
        return categorias;
    }


}
