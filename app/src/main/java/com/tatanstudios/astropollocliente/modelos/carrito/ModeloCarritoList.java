package com.tatanstudios.astropollocliente.modelos.carrito;

import com.google.gson.annotations.SerializedName;

public class ModeloCarritoList {

    @SerializedName("id")
    public Integer id;
    @SerializedName("productoID")
    public Integer productoID;

    @SerializedName("nombre")
    public String nombre;

    @SerializedName("cantidad")
    public Integer cantidad;

    @SerializedName("imagen")
    public String imagen;

    @SerializedName("precio")
    public String precio;

    @SerializedName("precioformat")
    public String precioFormat;

    @SerializedName("activo")
    public Integer activo;

    @SerializedName("carritoid")
    public Integer carritoid;

    @SerializedName("utiliza_imagen")
    public Integer utilizaImagen;


    @SerializedName("id_subcategorias")
    public Integer idSubcategorias;

    @SerializedName("hora_abre")
    public String horaAbre;

    @SerializedName("hora_cierra")
    public String horaCierra;


    @SerializedName("estadoLocal")
    public Integer estadoLocal;


    @SerializedName("titulo")
    public String titulo;

    @SerializedName("mensaje")
    public String mensaje;

    public Integer getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Integer getProductoID() {
        return productoID;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public String getImagen() {
        return imagen;
    }

    public String getPrecio() {
        return precio;
    }

    public Integer getActivo() {
        return activo;
    }

    public Integer getCarritoid() {
        return carritoid;
    }

    public String getPrecioFormat() {
        return precioFormat;
    }

    public Integer getUtilizaImagen() {
        return utilizaImagen;
    }

    public Integer getIdSubcategorias() {
        return idSubcategorias;
    }

    public String getHoraAbre() {
        return horaAbre;
    }

    public String getHoraCierra() {
        return horaCierra;
    }

    public Integer getEstadoLocal() {
        return estadoLocal;
    }
}
