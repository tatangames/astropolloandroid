package com.tatanstudios.astropollocliente.adaptadores.carrito;

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
import com.tatanstudios.astropollocliente.itemclick.IOnRecyclerViewClickListener;
import com.tatanstudios.astropollocliente.modelos.carrito.ModeloCarritoList;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CarritoComprasAdapter extends RecyclerView.Adapter<CarritoComprasAdapter.MyViewHolder>  {

    // adaptador para carrito de compras

    Context context;
    public ArrayList<ModeloCarritoList> modeloTipo;
    CarritoActivity fCarrito;
    LayoutInflater inflater;

    RequestOptions opcionesGlide = new RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.camaradefecto)
            .priority(Priority.NORMAL);

    public CarritoComprasAdapter(){}

    public CarritoComprasAdapter(Context context, ArrayList<ModeloCarritoList> modeloTipo, CarritoActivity fCarrito){
        this.context = context;
        this.modeloTipo = modeloTipo;
        this.fCarrito = fCarrito;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CarritoComprasAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.cardview_ver_carrito, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoComprasAdapter.MyViewHolder holder, int position) {

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


        // SI PRODUCTO TIENE PROBLEMAS
        if(modeloTipo.get(position).getEstadoLocal() == 1){
            holder.cartCarrito.setBackgroundColor(context.getResources().getColor(R.color.colorRojoCarrito));
            holder.txtNombre.setTextColor(Color.parseColor("#FFFFFF"));
            holder.txtPrecio.setTextColor(Color.parseColor("#FFFFFF"));
        }else{
            holder.cartCarrito.setBackgroundColor(context.getResources().getColor(R.color.colorBlanco));
            holder.txtNombre.setTextColor(Color.parseColor("#000000"));
            holder.txtPrecio.setTextColor(Color.parseColor("#000000"));
        }

        holder.txtPrecio.setText(modeloTipo.get(position).getPrecioFormat());
        holder.txtCantidad.setText(modeloTipo.get(position).getCantidad()+"x");

        // buscar menu de este servicio
        holder.setListener((view, position1) -> {

            int carritoid = modeloTipo.get(position).getCarritoid();

            // REGLAS::
            // 1- PRODUCTO NO ACTIVO
            // 2- SUB CATEGORTIA NO ACTIVA
            // 3- CATEGORIA NO ACTIVA
            // 4- CATEGORIA HORARIO NO ACTIVA

            fCarrito.editarProducto(carritoid, modeloTipo.get(position).getEstadoLocal(), modeloTipo.get(position).getTitulo(),
                    modeloTipo.get(position).getMensaje());
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

        CardView cartCarrito; // para cambiar color

        IOnRecyclerViewClickListener listener;

        public void setListener(IOnRecyclerViewClickListener listener) {
            this.listener = listener;
        }


        public MyViewHolder(View itemView){
            super(itemView);

            cartCarrito = itemView.findViewById(R.id.cardCarrito);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
            txtCantidad = itemView.findViewById(R.id.txtCantidad);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            imgProducto = itemView.findViewById(R.id.imgProducto);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }
}