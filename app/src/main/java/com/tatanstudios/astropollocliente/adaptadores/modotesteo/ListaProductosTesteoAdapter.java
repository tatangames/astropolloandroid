package com.tatanstudios.astropollocliente.adaptadores.modotesteo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.activitys.carrito.CarritoActivity;
import com.tatanstudios.astropollocliente.activitys.modotesteo.ListadoProductoTesteoActivity;
import com.tatanstudios.astropollocliente.fragmentos.modotesteo.FragmentCategoriasModoTesteo;
import com.tatanstudios.astropollocliente.itemclick.IOnRecyclerViewClickListener;
import com.tatanstudios.astropollocliente.modelos.carrito.ModeloCarritoList;
import com.tatanstudios.astropollocliente.modelos.modotesteo.ModeloProductosTesteo;
import com.tatanstudios.astropollocliente.modelos.modotesteo.ModeloProductosTesteoList;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListaProductosTesteoAdapter extends RecyclerView.Adapter<ListaProductosTesteoAdapter.MyViewHolder>  {

    // adaptador para carrito de compras

    Context context;
    public ArrayList<ModeloProductosTesteoList> modeloTipo;
    FragmentCategoriasModoTesteo listadoProductoTesteoActivity;
    LayoutInflater inflater;

    RequestOptions opcionesGlide = new RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.camaradefecto)
            .priority(Priority.NORMAL);

    public ListaProductosTesteoAdapter(){}

    public ListaProductosTesteoAdapter(Context context, ArrayList<ModeloProductosTesteoList> modeloTipo, FragmentCategoriasModoTesteo listadoProductoTesteoActivity){
        this.context = context;
        this.modeloTipo = modeloTipo;
        this.listadoProductoTesteoActivity = listadoProductoTesteoActivity;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ListaProductosTesteoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.cardview_listaproducto_testeo, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaProductosTesteoAdapter.MyViewHolder holder, int position) {

        // cargar imagen sino esta vacia

        // Evitar null
        if(modeloTipo.get(position).getImagen() != null){

            if(!modeloTipo.get(position).getImagen().isEmpty()){
                if(modeloTipo.get(position).getUtilizaImagen() == 1){
                    Glide.with(context)
                            .load(RetrofitBuilder.urlImagenes + modeloTipo.get(position).getImagen())
                            .apply(opcionesGlide)
                            .into(holder.imgProducto);

                    holder.imgProducto.setVisibility(View.VISIBLE);
                }else{
                    Glide.with(context)
                            .load(R.drawable.camaradefecto)
                            .apply(opcionesGlide)
                            .into(holder.imgProducto);

                    holder.imgProducto.setVisibility(View.GONE);
                }
            }
        }


        // 25 caracteres cortados para nombre
        holder.txtNombre.setText(modeloTipo.get(position).getNombrepro());

        if(modeloTipo.get(position).getDescripcion() != null){
            if(modeloTipo.get(position).getDescripcion().length() > 50){
                String cortado = modeloTipo.get(position).getDescripcion().substring(0,50);
                holder.txtDescripcion.setText(cortado+"...");
            }else{
                holder.txtDescripcion.setText(modeloTipo.get(position).getDescripcion());
            }
        }

        holder.txtPrecio.setText(modeloTipo.get(position).getPrecio());


        holder.setListener((view, position1) -> {

            int idpro = modeloTipo.get(position).getIdpro();
            listadoProductoTesteoActivity.elegirCantidadProducto(idpro);

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
        TextView txtPrecio;

        TextView txtDescripcion;


        IOnRecyclerViewClickListener listener;

        public void setListener(IOnRecyclerViewClickListener listener) {
            this.listener = listener;
        }


        public MyViewHolder(View itemView){
            super(itemView);

            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            txtPrecio = itemView.findViewById(R.id.txtSubtotalPrecio);//
            txtNombre = itemView.findViewById(R.id.txtNombre);//
            imgProducto = itemView.findViewById(R.id.imgProducto);//


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getBindingAdapterPosition());
        }
    }
}