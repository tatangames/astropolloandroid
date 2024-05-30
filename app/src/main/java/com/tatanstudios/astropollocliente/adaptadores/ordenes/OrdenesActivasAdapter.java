package com.tatanstudios.astropollocliente.adaptadores.ordenes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.fragmentos.principal.FragmentOrdenesActivas;
import com.tatanstudios.astropollocliente.itemclick.IOnRecyclerViewClickListener;
import com.tatanstudios.astropollocliente.modelos.ordenes.ModeloOrdenesActivasList;

import java.util.List;

public class OrdenesActivasAdapter extends RecyclerView.Adapter<OrdenesActivasAdapter.MyViewHolder> {

    Context context;
    List<ModeloOrdenesActivasList> modeloTipo;
    FragmentOrdenesActivas fTipoServicio;

    public OrdenesActivasAdapter(){}

    public OrdenesActivasAdapter(Context context, List<ModeloOrdenesActivasList> modeloTipo, FragmentOrdenesActivas fTipoServicio){
        this.context = context;
        this.modeloTipo = modeloTipo;
        this.fTipoServicio = fTipoServicio;
    }

    @NonNull
    @Override
    public OrdenesActivasAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.cardview_ordenes_activas, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdenesActivasAdapter.MyViewHolder holder, int position) {

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
                holder.txtMensajeCancelada.setText(modeloTipo.get(position).getNotaCancelada());
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


        // PREMIOS


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
            fTipoServicio.abrirFragmentOrdenes(pos);
        });

        holder.btnBuscar.setOnClickListener(v -> {
            int pos = modeloTipo.get(position).getId();
            fTipoServicio.abrirFragmentOrdenes(pos);
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

        TextView txtOrdenNum;

        TextView txtTotal;
        TextView txtFecha;
        TextView txtNota;

        TextView btnBuscar;

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

            txtMensajeCancelada = itemView.findViewById(R.id.txtEntrega3);
            txtNotaCancelada = itemView.findViewById(R.id.txtS4);
            txtEstado = itemView.findViewById(R.id.txtEntrega);
            txtCupon = itemView.findViewById(R.id.txtCupon);
            textoCupon = itemView.findViewById(R.id.txtt3);
            txtDireccion = itemView.findViewById(R.id.txtDireccion);
            btnBuscar = itemView.findViewById(R.id.btnBuscar);
            txtNota = itemView.findViewById(R.id.txtNota);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            txtOrdenNum = itemView.findViewById(R.id.txtOrdenNum);

            textoPremio = itemView.findViewById(R.id.txtt5);
            txtPremio = itemView.findViewById(R.id.txtCupon2);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getBindingAdapterPosition());
        }
    }
}

