package com.tatanstudios.astropollocliente.activitys.direccion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.fragmentos.direcciones.FragmentListaDirecciones;

public class ListaDireccionesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_direcciones);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContenedor, new FragmentListaDirecciones())
                .commit();
    }
}