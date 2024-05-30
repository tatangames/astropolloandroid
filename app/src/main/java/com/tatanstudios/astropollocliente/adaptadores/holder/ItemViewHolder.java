package com.tatanstudios.astropollocliente.adaptadores.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.tatanstudios.astropollocliente.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    public View rootView;
    public TextView txtNombre;
    public TextView txtDescripcion;
    public TextView txtPrecio;
    public CircleImageView imgProducto;
    public ConstraintLayout constraintView;

    public ItemViewHolder(@NonNull View view) {
        super(view);

        rootView = view;
        txtNombre = view.findViewById(R.id.txtNombre);
        txtDescripcion = view.findViewById(R.id.txtDescripcion);
        txtPrecio = view.findViewById(R.id.txtSubtotalPrecio);
        imgProducto = view.findViewById(R.id.imgProducto);
        constraintView = view.findViewById(R.id.constraintView);
    }
}