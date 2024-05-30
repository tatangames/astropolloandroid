package com.tatanstudios.astropollocliente.modelos.usuario;

import com.google.gson.annotations.SerializedName;

public class ApiRespuesta {

    @SerializedName("success")
    private Integer success;

    @SerializedName("id")
    private Integer id;

    @SerializedName("conteo")
    private Integer conteo;


    @SerializedName("titulo")
    private String titulo;

    @SerializedName("mensaje")
    private String mensaje;

    public Integer getConteo() {
        return conteo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Integer getId() {
        return id;
    }


    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }
}
