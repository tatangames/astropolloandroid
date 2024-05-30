package com.tatanstudios.astropollocliente.activitys.principal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.activitys.carrito.CarritoActivity;
import com.tatanstudios.astropollocliente.fragmentos.principal.FragmentOrdenesActivas;
import com.tatanstudios.astropollocliente.fragmentos.principal.FragmentZonaServicios;


public class PrincipalActivity extends AppCompatActivity {


    final Fragment fragment1 = new FragmentZonaServicios();
    final Fragment fragment2 = new FragmentOrdenesActivas();

    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    public BottomNavigationView navigation;

    FloatingActionButton fab;

    int vista = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);


        fab = findViewById(R.id.botonfab);


        navigation = findViewById(R.id.bottomNavigationView);
        navigation.setBackground(null);


        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            vista = bundle.getInt("VISTA");
        }

        // cuando se ordena, cambiar la vista a otra pantalla
        if(vista == 0){
            fm.beginTransaction().add(R.id.main_container, fragment2).hide(fragment2).commit();
            fm.beginTransaction().add(R.id.main_container,fragment1).commit();
        }else if(vista == 1){
            // vista a historial

            navigation.getMenu().findItem(R.id.nav_historial).setChecked(true);

            fm.beginTransaction().add(R.id.main_container, fragment2).hide(fragment2).commit();
            fm.beginTransaction().add(R.id.main_container,fragment1).commit();

            fm.beginTransaction().hide(active).show(fragment2).commit();
            active = fragment2;
        }else{
            fm.beginTransaction().add(R.id.main_container, fragment2).hide(fragment2).commit();
            fm.beginTransaction().add(R.id.main_container,fragment1).commit();
        }


        fab.setOnClickListener(v -> {
            Intent i = new Intent(this, CarritoActivity.class);
            startActivity(i);
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.nav_inicio:
                fm.beginTransaction().hide(active).show(fragment1).commit();
                active = fragment1;
                return true;

            case R.id.nav_historial:
                fm.beginTransaction().hide(active).show(fragment2).commit();
                ((FragmentOrdenesActivas) fragment2).peticionServidor();
                active = fragment2;
                return true;
        }
        return false;
    };


}