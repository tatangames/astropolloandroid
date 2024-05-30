package com.tatanstudios.astropollocliente.modelos.mapa;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModeloPoligono {

    @SerializedName("id")
    public Integer id;
    @SerializedName("nombreZona")
    public String nombreZona;
    @SerializedName("poligonos")
    public List<ModeloZonas> poligonos = null;


    public Integer getId() {
        return id;
    }

    public String getNombreZona() {
        return nombreZona;
    }

    public void setNombreZona(String nombreZona) {
        this.nombreZona = nombreZona;
    }

    public List<ModeloZonas> getPoligonos() {
        return poligonos;
    }

    public void setPoligonos(List<ModeloZonas> poligonos) {
        this.poligonos = poligonos;
    }
}
