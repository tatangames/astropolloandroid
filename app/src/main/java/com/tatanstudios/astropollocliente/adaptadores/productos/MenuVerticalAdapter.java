package com.tatanstudios.astropollocliente.adaptadores.productos;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.adaptadores.holder.HeaderViewHolder;
import com.tatanstudios.astropollocliente.adaptadores.holder.ItemViewHolder;
import com.tatanstudios.astropollocliente.modelos.productos.ModeloMenuProductosList;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class MenuVerticalAdapter extends Section {

    // vista menu de productos tipo comida

    private final String title;
    private final List<ModeloMenuProductosList> list;
    private final ClickListener clickListener;
    Context c;

    RequestOptions opcionesGlide = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.camaradefecto)
            .priority(Priority.NORMAL);

    public MenuVerticalAdapter(@NonNull final String title, @NonNull final List<ModeloMenuProductosList> list,
                               @NonNull final ClickListener clickListener, Context c) {

        super(SectionParameters.builder()
                .itemResourceId(R.layout.seccion_menu_producto)
                .headerResourceId(R.layout.seccion_menu_cabecera)
                .build());

        this.title = title;
        this.list = list;
        this.clickListener = clickListener;
        this.c = c;
    }

    @Override
    public int getContentItemsTotal() {
        return list.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(final View view) {
        return new ItemViewHolder(view);
    }


    @Override
    public void onBindItemViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder itemHolder = (ItemViewHolder) holder;

        final ModeloMenuProductosList datos = list.get(position);

        itemHolder.txtNombre.setText(datos.getNombre());

        if(datos.getDescripcion() != null){
            if(datos.getDescripcion().length() > 80) {
                String cortado = datos.getDescripcion().substring(0,80);
                itemHolder.txtDescripcion.setText(cortado+"...");
            }else{
                itemHolder.txtDescripcion.setText(datos.getDescripcion());
            }
        }else{
            itemHolder.txtDescripcion.setText("");
        }

        itemHolder.txtPrecio.setText("$"+datos.getPrecio());

        if(datos.getUtiliza_imagen() == 1){
            if(datos.getImagen() != null){
                itemHolder.imgProducto.setVisibility(View.VISIBLE);
                Glide.with(c)
                        .load(RetrofitBuilder.urlImagenes + datos.getImagen())
                        .apply(opcionesGlide)
                        .into(itemHolder.imgProducto);
            }else{
                Glide.with(c)
                        .load(R.drawable.icono_usuario_gris)
                        .apply(opcionesGlide)
                        .into(itemHolder.imgProducto);
            }
        }else{
            Glide.with(c)
                    .load(R.drawable.icono_usuario_gris)
                    .apply(opcionesGlide)
                    .into(itemHolder.imgProducto);
        }

        itemHolder.rootView.setOnClickListener(v ->
                clickListener.onItemRootViewClicked(datos.getIdProducto())
        );
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(final View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
        final HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        headerHolder.txtTitulo.setText(title);
    }

    public interface ClickListener {
        void onItemRootViewClicked(@NonNull final int idproducto);
    }

}
