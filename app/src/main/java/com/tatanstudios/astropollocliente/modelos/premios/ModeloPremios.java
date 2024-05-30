package com.tatanstudios.astropollocliente.modelos.premios;

import com.google.gson.annotations.SerializedName;
import com.tatanstudios.astropollocliente.modelos.productoindividual.ModeloInformacionProductoList;

import java.util.ArrayList;
import java.util.List;

public class ModeloPremios {

    @SerializedName("success")
    public Integer success;

    @SerializedName("nota")
    public String nota;

    @SerializedName("puntos")
    public String puntos;


    @SerializedName("titulo")
    public String titulo;

    @SerializedName("mensaje")
    public String mensaje;


    @SerializedName("conteo")
    public Integer conteo;


    public Integer getConteo() {
        return conteo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getNota() {
        return nota;
    }


    public String getPuntos() {
        return puntos;
    }

    @SerializedName("listado")
    public ArrayList<ModeloPremiosList> listado = null;

    public Integer getSuccess() {
        return success;
    }


    public ArrayList<ModeloPremiosList> getListado() {
        return listado;
    }
}
