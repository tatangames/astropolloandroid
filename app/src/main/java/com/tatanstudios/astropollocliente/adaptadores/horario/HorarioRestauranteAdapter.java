package com.tatanstudios.astropollocliente.adaptadores.horario;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.activitys.perfil.HorariosActivity;
import com.tatanstudios.astropollocliente.modelos.horarios.ModeloHorarioRestaurante;

import java.util.List;


public class HorarioRestauranteAdapter extends RecyclerView.Adapter<HorarioRestauranteAdapter.MyViewHolder> {

    Context context;
    List<ModeloHorarioRestaurante> modeloHorarioRestaurantes;
    HorariosActivity horariosActivity;


    public HorarioRestauranteAdapter(Context context, List<ModeloHorarioRestaurante> modeloHorarioRestaurantes, HorariosActivity horariosActivity) {
        this.context = context;
        this.modeloHorarioRestaurantes = modeloHorarioRestaurantes;
        this.horariosActivity = horariosActivity;
    }

    @NonNull
    @Override
    public HorarioRestauranteAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.cardview_horario_restaurante, parent, false);
        return new HorarioRestauranteAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HorarioRestauranteAdapter.MyViewHolder holder, int position) {

        holder.txtDia.setText(modeloHorarioRestaurantes.get(position).getDia());

        holder.txtHora.setText(modeloHorarioRestaurantes.get(position).getHora());

    }

    @Override
    public int getItemCount() {
        if(modeloHorarioRestaurantes != null){
            return modeloHorarioRestaurantes.size();
        }else{
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txtDia;
        TextView txtHora;


        public MyViewHolder(View itemView){
            super(itemView);

            txtDia = itemView.findViewById(R.id.txtDia);
            txtHora = itemView.findViewById(R.id.txtHora);


        }
    }

}