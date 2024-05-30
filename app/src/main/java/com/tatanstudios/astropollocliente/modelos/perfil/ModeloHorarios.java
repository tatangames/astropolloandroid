package com.tatanstudios.astropollocliente.modelos.perfil;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModeloHorarios {

    @SerializedName("success")
    public int success;

    @SerializedName("mensaje")
    public String mensaje;

    @SerializedName("restaurante")
    public String restaurante;

    public String getRestaurante() {
        return restaurante;
    }

    public String getMensaje() {
        return mensaje;
    }

    @SerializedName("horario")
    public List<ModeloHorarioList> horario = null;


    public List<ModeloHorarioList> getHorario() {
        return horario;
    }

    public int getSuccess() {
        return success;
    }


}
