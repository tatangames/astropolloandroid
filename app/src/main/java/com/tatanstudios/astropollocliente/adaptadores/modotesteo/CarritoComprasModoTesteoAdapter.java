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
import com.tatanstudios.astropollocliente.activitys.modotesteo.CarritoProductoTesteoActivity;
import com.tatanstudios.astropollocliente.itemclick.IOnRecyclerViewClickListener;
import com.tatanstudios.astropollocliente.modelos.carrito.ModeloCarritoList;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CarritoComprasModoTesteoAdapter extends RecyclerView.Adapter<CarritoComprasModoTesteoAdapter.MyViewHolder>  {

    // adaptador para carrito de compras

    Context context;
    public ArrayList<ModeloCarritoList> modeloTipo;
    CarritoProductoTesteoActivity fCarrito;
    LayoutInflater inflater;

    RequestOptions opcionesGlide = new RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.camaradefecto)
            .priority(Priority.NORMAL);

    public CarritoComprasModoTesteoAdapter(){}

    public CarritoComprasModoTesteoAdapter(Context context, ArrayList<ModeloCarritoList> modeloTipo, CarritoProductoTesteoActivity fCarrito){
        this.context = context;
        this.modeloTipo = modeloTipo;
        this.fCarrito = fCarrito;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CarritoComprasModoTesteoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.cardview_ver_carrito, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoComprasModoTesteoAdapter.MyViewHolder holder, int position) {

        // cargar imagen sino esta vacia

        // Evitar null
        if(modeloTipo.get(position).getImagen() != null){

            if(!modeloTipo.get(position).getImagen().isEmpty()){
                if(modeloTipo.get(position).getUtilizaImagen() == 1){
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
            }
        }


        // 25 caracteres cortados para nombre
        if(modeloTipo.get(position).getNombre().length() > 25){
            String cortado = modeloTipo.get(position).getNombre().substring(0,25);
            holder.txtNombre.setText(cortado+"...");
        }else{
            holder.txtNombre.setText(modeloTipo.get(position).getNombre());
        }



        holder.txtPrecio.setText(modeloTipo.get(position).getPrecioFormat());
        holder.txtCantidad.setText(modeloTipo.get(position).getCantidad()+"x");
    }


    @Override
    public int getItemCount() {
        if(modeloTipo != null){
            return modeloTipo.size();
        }else{
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView imgProducto;

        TextView txtNombre;
        TextView txtCantidad;
        TextView txtPrecio;

        CardView cartCarrito; // para cambiar color


        public MyViewHolder(View itemView){
            super(itemView);

            cartCarrito = itemView.findViewById(R.id.cardCarrito);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
            txtCantidad = itemView.findViewById(R.id.txtCantidad);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            imgProducto = itemView.findViewById(R.id.imgProducto);


        }
    }
}