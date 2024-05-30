package com.tatanstudios.astropollocliente.adaptadores.direccion;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.fragmentos.direcciones.FragmentListaDirecciones;
import com.tatanstudios.astropollocliente.itemclick.IOnRecyclerViewClickListener;
import com.tatanstudios.astropollocliente.modelos.direccion.ModeloDireccionList;

import java.util.List;


public class DireccionAdapter extends RecyclerView.Adapter<DireccionAdapter.MyViewHolder> {

    // para agregar lista de direcciones

    Context context;
    List<ModeloDireccionList> modeloDirecciones;
    FragmentListaDirecciones fragmentDirecciones;

    public DireccionAdapter(){}

    public DireccionAdapter(Context context, List<ModeloDireccionList> direccionList, FragmentListaDirecciones fragmentDirecciones) {
        this.context = context;
        this.modeloDirecciones = direccionList;
        this.fragmentDirecciones = fragmentDirecciones;
    }

    @NonNull
    @Override
    public DireccionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.cardview_lista_direcciones, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DireccionAdapter.MyViewHolder holder, int position) {
        holder.txtNombre.setText(modeloDirecciones.get(position).getNombre());

        holder.txtDireccion.setText(modeloDirecciones.get(position).getDireccion());

        holder.txtMinimo.setText(modeloDirecciones.get(position).getMinimoCompra());

        // agregar check, que esta seleccionado esta direccion
        if(modeloDirecciones.get(position).getSeleccionado() == 1){
            holder.imgCheck.setVisibility(View.VISIBLE);
            holder.cardDireccion.setBackgroundColor(Color.parseColor("#f2ffdb"));
        }else{
            holder.cardDireccion.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.imgCheck.setVisibility(View.GONE);
        }

        holder.setListener((view, po) -> {
            int iddireccion = modeloDirecciones.get(position).getIdDireccion();
            String nombre = modeloDirecciones.get(position).getNombre();
            String direccion = modeloDirecciones.get(position).getDireccion();
            String puntoReferencia = modeloDirecciones.get(position).getPuntoReferencia();
            String telefono = modeloDirecciones.get(position).getTelefono();
            fragmentDirecciones.dialogoDescripcion(iddireccion, nombre, direccion, puntoReferencia, telefono);
        });
    }

    @Override
    public int getItemCount() {
        if(modeloDirecciones != null){
            return modeloDirecciones.size();
        }else{
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtNombre;
        TextView txtDireccion;
        ImageView imgCheck;
        CardView cardDireccion;

        TextView txtMinimo;

        IOnRecyclerViewClickListener listener;

        public void setListener(IOnRecyclerViewClickListener listener) {
            this.listener = listener;
        }


        public MyViewHolder(View itemView){
            super(itemView);


            cardDireccion = itemView.findViewById(R.id.cardDireccion);
            imgCheck = itemView.findViewById(R.id.imgCheck);
            txtDireccion = itemView.findViewById(R.id.txtDireccion);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtMinimo = itemView.findViewById(R.id.txtMinimo);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getBindingAdapterPosition());
        }
    }

}