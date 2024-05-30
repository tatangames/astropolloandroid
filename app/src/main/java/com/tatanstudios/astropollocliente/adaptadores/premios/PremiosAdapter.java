package com.tatanstudios.astropollocliente.adaptadores.premios;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.activitys.premios.PremiosActivity;
import com.tatanstudios.astropollocliente.itemclick.IOnRecyclerViewClickListener;
import com.tatanstudios.astropollocliente.modelos.premios.ModeloPremiosList;


import java.util.ArrayList;

public class PremiosAdapter extends RecyclerView.Adapter<PremiosAdapter.MyViewHolder>  {

    // adaptador para carrito de compras

    Context context;
    ArrayList<ModeloPremiosList> modeloTipo;

    PremiosActivity premiosActivity;


    public PremiosAdapter(){}

    public PremiosAdapter(Context context, ArrayList<ModeloPremiosList> modeloTipo, PremiosActivity premiosActivity){
        this.context = context;
        this.modeloTipo = modeloTipo;
        this.premiosActivity = premiosActivity;
    }

    @NonNull
    @Override
    public PremiosAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.cardview_premios, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PremiosAdapter.MyViewHolder holder, int position) {

        // cargar imagen sino esta vacia
        if(modeloTipo.get(position).getSeleccionado() == 1){
            holder.imgSeleccionado.setVisibility(View.VISIBLE);
            holder.cardViewVista.setBackgroundColor(Color.parseColor("#f2ffdb"));

            holder.btnSeleccion.setBackground(ContextCompat.getDrawable(context, R.drawable.button_corner_rojo));
            holder.btnSeleccion.setText("BORRAR SELECCION");
        }
        else{
            holder.cardViewVista.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.imgSeleccionado.setVisibility(View.GONE);

            holder.btnSeleccion.setBackground(ContextCompat.getDrawable(context, R.drawable.button_corner_verde));
            holder.btnSeleccion.setText("SELECCIONAR");
        }

        holder.txtTitulo.setText(modeloTipo.get(position).getNombre());
        holder.txtPuntos.setText("Puntos: " + modeloTipo.get(position).getPuntos());

        holder.btnSeleccion.setOnClickListener(v -> {

            int seleccion = modeloTipo.get(position).getSeleccionado();
            int idpremio = modeloTipo.get(position).getId();

            premiosActivity.verTipoBoton(idpremio, seleccion);
        });

        holder.setListener((view, po) -> {

        });

    }

    @Override
    public int getItemCount() {
        if(modeloTipo != null){
            return modeloTipo.size();
        }else{
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imgSeleccionado;
        TextView txtTitulo;
        TextView txtPuntos;

        CardView cardViewVista;

        IOnRecyclerViewClickListener listener;

        Button btnSeleccion;


        public void setListener(IOnRecyclerViewClickListener listener) {
            this.listener = listener;
        }


        public MyViewHolder(View itemView){
            super(itemView);

            imgSeleccionado = itemView.findViewById(R.id.imgCheck);
            txtTitulo = itemView.findViewById(R.id.txtNombre);
            txtPuntos = itemView.findViewById(R.id.txtDireccion);
            cardViewVista = itemView.findViewById(R.id.cardViewVista);
            btnSeleccion = itemView.findViewById(R.id.btnSeleccion);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getBindingAdapterPosition());
        }
    }
}