package com.tatanstudios.astropollocliente.adaptadores.ordenes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.fragmentos.ordenes.FragmentProductosOrdenes;
import com.tatanstudios.astropollocliente.itemclick.IOnRecyclerViewClickListener;
import com.tatanstudios.astropollocliente.modelos.ordenes.ModeloProductosOrdenesList;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductosOrdenesActivasAdapter extends RecyclerView.Adapter<ProductosOrdenesActivasAdapter.MyViewHolder>  {

    Context context;
    public ArrayList<ModeloProductosOrdenesList> modeloTipo;
    FragmentProductosOrdenes fragmentVerProductos;

    RequestOptions opcionesGlide = new RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.camaradefecto)
            .priority(Priority.NORMAL);

    public ProductosOrdenesActivasAdapter(){}

    public ProductosOrdenesActivasAdapter(Context context, ArrayList<ModeloProductosOrdenesList>  modeloTipo, FragmentProductosOrdenes fragmentVerProductos){
        this.context = context;
        this.modeloTipo = modeloTipo;
        this.fragmentVerProductos = fragmentVerProductos;
    }

    @NonNull
    @Override
    public ProductosOrdenesActivasAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.cardview_ver_carrito, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductosOrdenesActivasAdapter.MyViewHolder holder, int position) {

        if(modeloTipo.get(position).getUtilizaImagen() == 1){

            if(modeloTipo.get(position).getImagen() != null){
                Glide.with(context)
                        .load(RetrofitBuilder.urlImagenes + modeloTipo.get(position).getImagen())
                        .apply(opcionesGlide)
                        .into(holder.imgProducto);
            }else{
                Glide.with(context)
                        .load(R.drawable.camaradefecto)
                        .apply(opcionesGlide)
                        .into(holder.imgProducto);
            }

        }else{
            Glide.with(context)
                    .load(R.drawable.camaradefecto)
                    .apply(opcionesGlide)
                    .into(holder.imgProducto);
        }

        // 18 caracteres cortados para nombre
        if(modeloTipo.get(position).getNombreProducto().length() > 30){
            String cortado = modeloTipo.get(position).getNombreProducto().substring(0,30);
            holder.txtNombre.setText(cortado+"...");
        }else{
            holder.txtNombre.setText(modeloTipo.get(position).getNombreProducto());
        }

        holder.txtPrecio.setText(modeloTipo.get(position).getMultiplicado());
        holder.txtCantidad.setText(modeloTipo.get(position).getCantidad().toString() + "x");

        // buscar menu de este servicio
        holder.setListener((view, position1) -> {
            int id = modeloTipo.get(position).getIdordendescrip(); // id de fila orden descripcion

            fragmentVerProductos.verProductoIndividual(id);
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

        CircleImageView imgProducto;
        TextView txtNombre;
        TextView txtCantidad;
        TextView txtPrecio;

        IOnRecyclerViewClickListener listener;

        public void setListener(IOnRecyclerViewClickListener listener) {
            this.listener = listener;
        }


        public MyViewHolder(View itemView){
            super(itemView);

            imgProducto = itemView.findViewById(R.id.imgProducto);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtCantidad = itemView.findViewById(R.id.txtCantidad);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getBindingAdapterPosition());
        }
    }


}