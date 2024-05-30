package com.tatanstudios.astropollocliente.activitys.historial;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.fragmentos.historial.FragmentFechaHistorial;

public class HistorialOrdenesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_ordenes);


        FragmentFechaHistorial fragmentFecha = new FragmentFechaHistorial();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragmentFecha)
                .commit();
    }
}