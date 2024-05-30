package com.tatanstudios.astropollocliente.activitys.modotesteo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.activitys.principal.PrincipalActivity;
import com.tatanstudios.astropollocliente.fragmentos.loginuser.FragmentLogin;
import com.tatanstudios.astropollocliente.fragmentos.modotesteo.FragmentCategoriasModoTesteo;
import com.tatanstudios.astropollocliente.fragmentos.principal.FragmentZonaServicios;

public class ListadoProductoTesteoActivity extends AppCompatActivity {

    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_producto_testeo);

        fab = findViewById(R.id.botonfab);

        FragmentCategoriasModoTesteo fragmentCategoriasModoTesteo = new FragmentCategoriasModoTesteo();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContenedor, fragmentCategoriasModoTesteo)
                .addToBackStack(null)
                .commit();


        fab.setOnClickListener(view -> {

            // redireccionar a listado de productos carrito.

            Intent intent = new Intent(this, CarritoProductoTesteoActivity.class);
            startActivity(intent);
        });
    }


    @Override
    public void onBackPressed() {
      //  super.onBackPressed();

        Intent intent = new Intent(this, PrincipalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
}