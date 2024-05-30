package com.tatanstudios.astropollocliente.modelos.ordenes;

import com.google.gson.annotations.SerializedName;

public class ModeloOrdenesActivasList {
    @SerializedName("id")
    public Integer id;

    @SerializedName("direccion")
    public String direccion;


    @SerializedName("estado")
    public String estado;


    @SerializedName("totalformat")
    public String totalFormat;


    @SerializedName("fecha_orden")
    public String fechaOrden;


    @SerializedName("haycupon")
    public Integer hayCupon;


    @SerializedName("estado_cancelada")
    public Integer estadoCancelada;

    @SerializedName("nota_cancelada")
    public String notaCancelada;

    @SerializedName("nota_orden")
    public String notaOrden;


    @SerializedName("mensaje_cupon")
    public String mensajeCupon;

    @SerializedName("estado_camino")
    public int estadoCamino;

    @SerializedName("estado_entregada")
    public int estadoEntrega;

    @SerializedName("fechaentregada")
    public String fechaEntrega;


    @SerializedName("fechacamino")
    public String fechaCamino;


    @SerializedName("fechaestimada")
    public String fechaEstimada;

    public String getFechaEstimada() {
        return fechaEstimada;
    }

    @SerializedName("textoiniciada")
    public String textoiniciada;



    @SerializedName("haypremio")
    public Integer haypremio;

    @SerializedName("textopremio")
    public String textoPremio;


    public Integer getHaypremio() {
        return haypremio;
    }

    public String getTextoPremio() {
        return textoPremio;
    }

    @SerializedName("textocamino")
    public String textoCamino;

    @SerializedName("textoentregada")
    public String textoEntregada;


    public String getTextoEntregada() {
        return textoEntregada;
    }

    public int getEstadoEntrega() {
        return estadoEntrega;
    }

    public String getFechaEntrega() {
        return fechaEntrega;
    }

    public String getTextoCamino() {
        return textoCamino;
    }

    public int getEstadoCamino() {
        return estadoCamino;
    }

    public String getFechaCamino() {
        return fechaCamino;
    }

    public String getTextoiniciada() {
        return textoiniciada;
    }

    @SerializedName("fechainiciada")
    public String fechainiciada;


    public String getFechainiciada() {
        return fechainiciada;
    }

    @SerializedName("estado_iniciada")
    public int estadoIniciada;

    public int getEstadoIniciada() {
        return estadoIniciada;
    }

    public String getMensajeCupon() {
        return mensajeCupon;
    }

    public String getNotaOrden() {
        return notaOrden;
    }

    public Integer getHayCupon() {
        return hayCupon;
    }

    public Integer getEstadoCancelada() {
        return estadoCancelada;
    }

    public String getNotaCancelada() {
        return notaCancelada;
    }

    public Integer getId() {
        return id;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getEstado() {
        return estado;
    }

    public String getTotalFormat() {
        return totalFormat;
    }

    public String getFechaOrden() {
        return fechaOrden;
    }
}
