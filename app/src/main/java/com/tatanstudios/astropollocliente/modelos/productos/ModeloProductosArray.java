package com.tatanstudios.astropollocliente.modelos.productos;

import com.google.gson.annotations.SerializedName;
import com.tatanstudios.astropollocliente.modelos.principal.ModeloCategorias;
import com.tatanstudios.astropollocliente.modelos.principal.ModeloProductos;

import java.util.ArrayList;
import java.util.List;

public class ModeloProductosArray {

    @SerializedName("id")
    public Integer id;

    @SerializedName("nombre")
    public String nombre;

    @SerializedName("productos")
    public List<ModeloMenuProductosList> productos = null;

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<ModeloMenuProductosList> getProductos() {
        return productos;
    }

    public void setProductos(List<ModeloMenuProductosList> productos) {
        this.productos = productos;
    }


}
