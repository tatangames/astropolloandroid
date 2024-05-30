package com.tatanstudios.astropollocliente.adaptadores.perfil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.activitys.perfil.PerfilActivity;
import com.tatanstudios.astropollocliente.itemclick.IOnRecyclerViewClickListener;
import com.tatanstudios.astropollocliente.modelos.perfil.ModelosPerfil;

import java.util.List;


public class PerfilAdapter extends RecyclerView.Adapter<PerfilAdapter.MyViewHolder> {

    Context context;
    List<ModelosPerfil> modeloPerfil;
    PerfilActivity perfilActivity;


    public PerfilAdapter(Context context, List<ModelosPerfil> modeloPerfil, PerfilActivity perfilActivity) {
        this.context = context;
        this.modeloPerfil = modeloPerfil;
        this.perfilActivity = perfilActivity;
    }

    @NonNull
    @Override
    public PerfilAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.cardview_perfil, parent, false);
        return new PerfilAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PerfilAdapter.MyViewHolder holder, int position) {
        holder.txtPerfil.setText(modeloPerfil.get(position).getNombre());

        if(modeloPerfil.get(position).getPosicion() == 1){

            holder.imgPerfil.setImageResource(R.drawable.icono_casa);
        }

        else if(modeloPerfil.get(position).getPosicion() == 2){

            holder.imgPerfil.setImageResource(R.drawable.icono_candado_gris);
        }

        else if(modeloPerfil.get(position).getPosicion() == 3){

            holder.imgPerfil.setImageResource(R.drawable.icono_reloj);
        }

        else if(modeloPerfil.get(position).getPosicion() == 4){

            holder.imgPerfil.setImageResource(R.drawable.icono_baselinea);
        }

        else if(modeloPerfil.get(position).getPosicion() == 5){

            holder.imgPerfil.setImageResource(R.drawable.icono_canasta_gris);
        }

        else if(modeloPerfil.get(position).getPosicion() == 6){

            holder.imgPerfil.setImageResource(R.drawable.icono_pregunta);
        }

        else {

            holder.imgPerfil.setImageResource(R.drawable.icono_admiracion);
        }

        holder.setListener((view, po) -> {
            perfilActivity.verPosicion(modeloPerfil.get(position).getPosicion());
        });
    }

    @Override
    public int getItemCount() {
        if(modeloPerfil != null){
            return modeloPerfil.size();
        }else{
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtPerfil;
        ImageView imgPerfil;

        IOnRecyclerViewClickListener listener;

        public void setListener(IOnRecyclerViewClickListener listener) {
            this.listener = listener;
        }


        public MyViewHolder(View itemView){
            super(itemView);

            imgPerfil = itemView.findViewById(R.id.imgPerfil);
            txtPerfil = itemView.findViewById(R.id.txtPerfil);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getBindingAdapterPosition());
        }
    }

}