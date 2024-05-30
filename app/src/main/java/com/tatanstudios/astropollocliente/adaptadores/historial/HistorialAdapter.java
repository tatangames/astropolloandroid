package com.tatanstudios.astropollocliente.adaptadores.historial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.fragmentos.historial.FragmentBuscarHistorial;
import com.tatanstudios.astropollocliente.itemclick.IOnRecyclerViewClickListener;
import com.tatanstudios.astropollocliente.modelos.ordenes.ModeloOrdenesActivasList;

import java.util.List;


public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.MyViewHolder> {

    // historial ordenes que pidio el cliente

    Context context;
    List<ModeloOrdenesActivasList> modeloTipo;
    FragmentBuscarHistorial fTipoServicio;

    public HistorialAdapter(Context context, List<ModeloOrdenesActivasList> modeloTipo, FragmentBuscarHistorial fTipoServicio){
        this.context = context;
        this.modeloTipo = modeloTipo;
        this.fTipoServicio = fTipoServicio;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.cardview_historial, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        // id de la orden
        holder.txtOrdenNum.setText(modeloTipo.get(position).getId().toString());

        holder.txtDireccion.setText(modeloTipo.get(position).getDireccion());


        // total a pagar
        holder.txtTotal.setText(modeloTipo.get(position).getTotalFormat());

        // fecha de la orden
        holder.txtFecha.setText(modeloTipo.get(position).getFechaOrden());

        // estado de la orden
        holder.txtEstado.setText(modeloTipo.get(position).getEstado());

        if(modeloTipo.get(position).getEstadoCancelada() == 1){

            if(modeloTipo.get(position).getNotaCancelada() != null){
                holder.txtMensajeCancelada.setText(modeloTipo.get(position).getNotaCancelada().toString());
            }

            holder.txtNotaCancelada.setVisibility(View.VISIBLE);
            holder.txtMensajeCancelada.setVisibility(View.VISIBLE);
        }else{
            holder.txtNotaCancelada.setVisibility(View.GONE);
            holder.txtMensajeCancelada.setVisibility(View.GONE);
        }

        // CUPONES

        if(modeloTipo.get(position).getHayCupon() == 1){

            if(modeloTipo.get(position).getMensajeCupon() != null){
                holder.txtCupon.setText(modeloTipo.get(position).getMensajeCupon());
                holder.txtCupon.setVisibility(View.VISIBLE);
                holder.textoCupon.setVisibility(View.VISIBLE);
            }else{
                holder.txtCupon.setText("");
                holder.txtCupon.setVisibility(View.GONE);
                holder.textoCupon.setVisibility(View.GONE);
            }

        }else{

            holder.txtCupon.setText("");
            holder.txtCupon.setVisibility(View.GONE);
            holder.textoCupon.setVisibility(View.GONE);
        }


        if(modeloTipo.get(position).getHaypremio() == 1){

            holder.txtPremio.setText(modeloTipo.get(position).getTextoPremio());

            holder.textoPremio.setVisibility(View.VISIBLE);
            holder.txtPremio.setVisibility(View.VISIBLE);
        }else{

            holder.textoPremio.setVisibility(View.GONE);
            holder.txtPremio.setVisibility(View.GONE);
        }


        if(modeloTipo.get(position).getNotaOrden() != null) {
            holder.txtNota.setText(modeloTipo.get(position).getNotaOrden());
        }else{
            holder.txtNota.setText("");
        }



        holder.setListener((view, position1) -> {
            int pos = modeloTipo.get(position).getId();
            fTipoServicio.verProductos(pos);
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

    public class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{

        TextView txtOrdenNum;

        TextView txtTotal;
        TextView txtFecha;
        TextView txtNota;
        TextView textoDireccion;

        TextView txtDireccion;

        TextView textoCupon;

        TextView txtCupon;

        TextView txtEstado; // estado de la orden


        // aparece si orden fue cancelada
        TextView txtNotaCancelada;

        TextView txtMensajeCancelada;

        TextView textoPremio;
        TextView txtPremio;


        IOnRecyclerViewClickListener listener;

        public void setListener(IOnRecyclerViewClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(View itemView){
            super(itemView);

            txtOrdenNum = itemView.findViewById(R.id.txtOrdenNum);
            txtTotal = itemView.findViewById(R.id.txtTotal);

            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtNota = itemView.findViewById(R.id.txtNota);
            textoDireccion = itemView.findViewById(R.id.txtS3);
            txtDireccion = itemView.findViewById(R.id.txtDireccion);

            txtMensajeCancelada = itemView.findViewById(R.id.txtEntrega3);
            txtNotaCancelada = itemView.findViewById(R.id.txtS4);
            txtEstado = itemView.findViewById(R.id.txtEntrega);
            txtCupon = itemView.findViewById(R.id.txtCupon);
            textoCupon = itemView.findViewById(R.id.txtt3);

            textoPremio = itemView.findViewById(R.id.txtt6);
            txtPremio = itemView.findViewById(R.id.txtCupon3);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getBindingAdapterPosition());
        }
    }
}