package com.tatanstudios.astropollocliente.modelos.mapa;

import com.google.gson.annotations.SerializedName;

public class ModeloDireccionMapa {

    @SerializedName("nombreZona")
    public String nombreZona;
    @SerializedName("zonaLatitud")
    public String zonaLatitud;
    @SerializedName("zonaLongitud")
    public String zonaLongitud;

    public String getNombreZona() {
        return nombreZona;
    }

    public void setNombreZona(String nombreZona) {
        this.nombreZona = nombreZona;
    }

    public String getZonaLatitud() {
        return zonaLatitud;
    }

    public void setZonaLatitud(String zonaLatitud) {
        this.zonaLatitud = zonaLatitud;
    }

    public String getZonaLongitud() {
        return zonaLongitud;
    }

    public void setZonaLongitud(String zonaLongitud) {
        this.zonaLongitud = zonaLongitud;
    }

}
