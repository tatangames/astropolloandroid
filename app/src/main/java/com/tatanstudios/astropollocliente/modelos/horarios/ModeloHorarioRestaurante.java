package com.tatanstudios.astropollocliente.modelos.horarios;

public class ModeloHorarioRestaurante {


    public String dia;
    public String hora;


    public ModeloHorarioRestaurante(String dia, String hora) {
        this.dia = dia;
        this.hora = hora;
    }


    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
