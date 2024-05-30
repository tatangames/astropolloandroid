package com.tatanstudios.astropollocliente.modelos.mapa;

import com.google.gson.annotations.SerializedName;

public class ModeloZonas {
    @SerializedName("latitudPoligono")
    public String latitudPoligono;
    @SerializedName("longitudPoligono")
    public String longitudPoligono;

    public String getLatitudPoligono() {
        return latitudPoligono;
    }

    public void setLatitudPoligono(String latitudPoligono) {
        this.latitudPoligono = latitudPoligono;
    }

    public String getLongitudPoligono() {
        return longitudPoligono;
    }

    public void setLongitudPoligono(String longitudPoligono) {
        this.longitudPoligono = longitudPoligono;
    }
}
