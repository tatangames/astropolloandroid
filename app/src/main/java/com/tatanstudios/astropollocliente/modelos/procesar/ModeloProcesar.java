package com.tatanstudios.astropollocliente.modelos.procesar;

import com.google.gson.annotations.SerializedName;

public class ModeloProcesar {

    @SerializedName("success")
    public Integer success;
    @SerializedName("total")
    public String total;
    @SerializedName("subtotal")
    public String subtotal;
    @SerializedName("envio")
    public String envio;
    @SerializedName("direccion")
    public String direccion;
    @SerializedName("minimo")
    public Integer minimo;
    @SerializedName("mensaje")
    public String mensaje;

    @SerializedName("titulo")
    public String titulo;

    @SerializedName("cliente")
    public String cliente;


    @SerializedName("usacupon")
    public Integer usacupon;



    @SerializedName("usapremio")
    public Integer usaPremio;

    @SerializedName("textopremio")
    public String textoPremio;


    public Integer getUsaPremio() {
        return usaPremio;
    }

    public String getTextoPremio() {
        return textoPremio;
    }

    public Integer getUsacupon() {
        return usacupon;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getCliente() {
        return cliente;
    }

    public Integer getSuccess() {
        return success;
    }

    public String getTotal() {
        return total;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public String getEnvio() {
        return envio;
    }

    public String getDireccion() {
        return direccion;
    }

    public Integer getMinimo() {
        return minimo;
    }

    public String getMensaje() {
        return mensaje;
    }
}
