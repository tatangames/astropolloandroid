package com.tatanstudios.astropollocliente.modelos.usuario;


import com.google.gson.annotations.SerializedName;

public class AccessTokenUser {

    @SerializedName("id")
    String id;

    @SerializedName("token")
    String token;

    @SerializedName("nombre")
    String nombre;

    @SerializedName("usuario")
    String usuario;

    @SerializedName("vehiculo")
    String vehiculo;


    @SerializedName("placa")
    String placa;


    @SerializedName("success")
    int success;

    @SerializedName("opcion")
    int opcion;

    @SerializedName("correo")
    String correo;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("imagen")
    private String imagen;

    @SerializedName("resta")
    private String resta;


    @SerializedName("foto")
    private String foto;


    @SerializedName("idorden")
    public int idorden;


    public int getIdorden() {
        return idorden;
    }

    public String getFoto() {
        return foto;
    }

    public String getVehiculo() {
        return vehiculo;
    }

    public String getPlaca() {
        return placa;
    }

    public int getOpcion() {
        return opcion;
    }

    public String getResta() {
        return resta;
    }

    public String getTitulo() {
        return titulo;
    }

    String stringPresenBorrarCarrito; // indicacion para carrito
    String stringPresenDireccionMapa; // indicacion para elegir direccion mapa


    public String getImagen() {
        return imagen;
    }



    public String getUsuario() {
        return usuario;
    }


    public String getMensaje() {
        return mensaje;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setSuccess(int success) {
        this.success = success;
    }


    public String getStringPresenDireccionMapa() {
        return stringPresenDireccionMapa;
    }

    public void setStringPresenDireccionMapa(String stringPresenDireccionMapa) {
        this.stringPresenDireccionMapa = stringPresenDireccionMapa;
    }

    public String getStringPresenBorrarCarrito() {
        return stringPresenBorrarCarrito;
    }

    public void setStringPresenBorrarCarrito(String stringPresenBorrarCarrito) {
        this.stringPresenBorrarCarrito = stringPresenBorrarCarrito;
    }

    public String getCorreo() {
        return correo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getSuccess() {
        return success;
    }

}

