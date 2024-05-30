package com.tatanstudios.astropollocliente.modelos.servicios;

import com.google.gson.annotations.SerializedName;
import com.tatanstudios.astropollocliente.modelos.principal.ModeloCategorias;
import com.tatanstudios.astropollocliente.modelos.principal.ModeloProductos;

import java.util.List;

public class ModeloTipoServiciosZona {

    @SerializedName("success")
    public Integer success;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("mensaje")
    private String mensaje;



    @SerializedName("slider")
    public List<ModeloSlider> slider = null;

    @SerializedName("categorias")
    public List<ModeloCategorias> categorias;

    @SerializedName("populares")
    public List<ModeloProductos> productos;


    @SerializedName("activo")
    public int activo;



    @SerializedName("activo_slider")
    public int activo_slider;


    // CLIENTES MODO TESTEO BOTON

    @SerializedName("btntesteocliente")
    public int btnTesteoCliente;

    @SerializedName("btntesteoservicio")
    public int btnTesteoServicio;


    public int getBtnTesteoCliente() {
        return btnTesteoCliente;
    }

    public int getBtnTesteoServicio() {
        return btnTesteoServicio;
    }

    public int getActivo_slider() {
        return activo_slider;
    }


    public String getTitulo() {
        return titulo;
    }

    public String getMensaje() {
        return mensaje;
    }



    public Integer getSuccess() {
        return success;
    }


    public int getActivo() {
        return activo;
    }

    public List<ModeloCategorias> getCategorias() {
        return categorias;
    }

    public List<ModeloSlider> getSlider() {
        return slider;
    }


    public List<ModeloProductos> getProductos() {
        return productos;
    }
}
