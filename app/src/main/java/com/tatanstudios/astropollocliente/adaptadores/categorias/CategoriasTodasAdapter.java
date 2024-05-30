package com.tatanstudios.astropollocliente.adaptadores.categorias;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.activitys.categorias.TodasCategoriasActivity;
import com.tatanstudios.astropollocliente.itemclick.IOnRecyclerViewClickListener;
import com.tatanstudios.astropollocliente.modelos.principal.ModeloCategorias;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;

import java.util.ArrayList;

public class CategoriasTodasAdapter extends RecyclerView.Adapter<CategoriasTodasAdapter.MyViewHolder>  {

    // adaptador para carrito de compras

    Context context;
    ArrayList<ModeloCategorias> modeloTipo;

    TodasCategoriasActivity todasCategoriasActivity;

    RequestOptions opcionesGlide = new RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.camaradefecto)
            .priority(Priority.NORMAL);

    public CategoriasTodasAdapter(){}

    public CategoriasTodasAdapter(Context context, ArrayList<ModeloCategorias> modeloTipo, TodasCategoriasActivity todasCategoriasActivity){
        this.context = context;
        this.modeloTipo = modeloTipo;
        this.todasCategoriasActivity = todasCategoriasActivity;
    }

    @NonNull
    @Override
    public CategoriasTodasAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.cardview_todas_las_categorias, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriasTodasAdapter.MyViewHolder holder, int position) {

        // cargar imagen sino esta vacia
        if(!modeloTipo.get(position).getImagen().isEmpty()){
            Glide.with(context)
                    .load(RetrofitBuilder.urlImagenes + modeloTipo.get(position).getImagen())
                    .apply(opcionesGlide)
                    .into(holder.imgCategoria);
        }

        holder.txtNombre.setText(modeloTipo.get(position).getNombre());

        // buscar todos los productos de la categoria
        holder.setListener((view, position1) -> {

            todasCategoriasActivity.verListaDeProductos(modeloTipo.get(position).getId());
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

        ImageView imgCategoria;
        TextView txtNombre;
        IOnRecyclerViewClickListener listener;

        public void setListener(IOnRecyclerViewClickListener listener) {
            this.listener = listener;
        }


        public MyViewHolder(View itemView){
            super(itemView);

            imgCategoria = itemView.findViewById(R.id.imgCategoria);
            txtNombre = itemView.findViewById(R.id.txtCategoria);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getBindingAdapterPosition());
        }
    }
}