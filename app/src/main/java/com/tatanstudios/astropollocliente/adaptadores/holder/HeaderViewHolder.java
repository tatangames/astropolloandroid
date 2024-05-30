package com.tatanstudios.astropollocliente.adaptadores.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tatanstudios.astropollocliente.R;


public class HeaderViewHolder extends RecyclerView.ViewHolder {

    // cabecera para vista tipo comida

    public TextView txtTitulo;
    public HeaderViewHolder(@NonNull View view) {
        super(view);

        txtTitulo = view.findViewById(R.id.txtTitulo);
    }


}