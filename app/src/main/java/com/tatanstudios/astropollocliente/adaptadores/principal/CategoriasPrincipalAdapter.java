package com.tatanstudios.astropollocliente.adaptadores.principal;

import android.content.Context;
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
import com.tatanstudios.astropollocliente.fragmentos.principal.FragmentZonaServicios;
import com.tatanstudios.astropollocliente.itemclick.IOnRecyclerViewClickListener;
import com.tatanstudios.astropollocliente.modelos.principal.ModeloCategorias;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;

import java.util.List;

public class CategoriasPrincipalAdapter extends RecyclerView.Adapter<CategoriasPrincipalAdapter.MyViewHolder> {

    Context context;
    List<ModeloCategorias> modeloTipo;
    FragmentZonaServicios fragmentoPrincipal;

    RequestOptions opcionesGlide = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .priority(Priority.NORMAL);

    public CategoriasPrincipalAdapter(){}

    public CategoriasPrincipalAdapter(Context context, List<ModeloCategorias> modeloTipo, FragmentZonaServicios fragmentoPrincipal){
        this.context = context;
        this.modeloTipo = modeloTipo;
        this.fragmentoPrincipal = fragmentoPrincipal;
    }

    @NonNull
    @Override
    public CategoriasPrincipalAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.viewholder_category, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.txtCategoria.setText(modeloTipo.get(position).getNombre());

        if(!modeloTipo.get(position).getImagen().isEmpty()) {

                Glide.with(context)
                        .load(RetrofitBuilder.urlImagenes + modeloTipo.get(position).getImagen())
                        .apply(opcionesGlide)
                        .into(holder.imgCategoria);

        }else{
            Glide.with(context)
                    .load(R.drawable.camaradefecto)
                    .apply(opcionesGlide)
                    .into(holder.imgCategoria);
        }

        // Redireccionamiento al fragmento correspondiente segun el servicio
        holder.setListener((view, p) -> {
            fragmentoPrincipal.elegirCategoria(modeloTipo.get(position).getId());
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

        TextView txtCategoria;

        ImageView imgCategoria;

        IOnRecyclerViewClickListener listener;

        public void setListener(IOnRecyclerViewClickListener listener) {
            this.listener = listener;
        }


        public MyViewHolder(View itemView){
            super(itemView);

            imgCategoria = itemView.findViewById(R.id.imgCategoria);
            txtCategoria = itemView.findViewById(R.id.txtCategoria);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getBindingAdapterPosition());
        }
    }


}
