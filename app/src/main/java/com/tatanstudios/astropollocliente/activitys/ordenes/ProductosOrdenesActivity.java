package com.tatanstudios.astropollocliente.activitys.ordenes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.fragmentos.ordenes.FragmentProductosOrdenes;

public class ProductosOrdenesActivity extends AppCompatActivity {

    int ordenid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos_ordenes);

        Intent intent = getIntent();
        if (intent != null) {
            ordenid = intent.getIntExtra("KEY_ORDEN", 0);
        }

        FragmentProductosOrdenes fragmentOrden = new FragmentProductosOrdenes();

        Bundle bundle = new Bundle();
        bundle.putInt("KEY_ORDEN", ordenid);
        fragmentOrden.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContenedor, fragmentOrden)
                .commit();

    }
}