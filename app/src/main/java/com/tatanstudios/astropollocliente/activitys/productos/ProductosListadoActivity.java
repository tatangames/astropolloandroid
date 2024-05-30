package com.tatanstudios.astropollocliente.activitys.productos;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;


import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.fragmentos.productos.FragmentListadoProductos;

public class ProductosListadoActivity extends AppCompatActivity{

     int idCategoria = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos_listado);

        Intent intent = getIntent();
        if (intent != null) {
            idCategoria = intent.getIntExtra("KEY_CATEGORIA",0);
        }

        FragmentListadoProductos fragmentListadoProductos = new FragmentListadoProductos();

        Bundle bundle = new Bundle();
        bundle.putInt("KEY_CATEGORIA", idCategoria);
        fragmentListadoProductos.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContenedor, fragmentListadoProductos)
                .commit();
    }



}