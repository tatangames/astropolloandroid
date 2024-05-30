package com.tatanstudios.astropollocliente.modelos.perfil;

import com.google.gson.annotations.SerializedName;

public class ModeloHorarioList {


    @SerializedName("id")
    public Integer id;

    @SerializedName("horario")
    public String horario;

    @SerializedName("dia")
    public Integer dia;

    @SerializedName("cerrado")
    public Integer cerrado;

    public Integer getId() {
        return id;
    }


    public String getHorario() {
        return horario;
    }

    public Integer getDia() {
        return dia;
    }


    public Integer getCerrado() {
        return cerrado;
    }

}
