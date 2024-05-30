package com.tatanstudios.astropollocliente.activitys.perfil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.developer.kalert.KAlertDialog;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.activitys.direccion.ListaDireccionesActivity;
import com.tatanstudios.astropollocliente.activitys.historial.HistorialOrdenesActivity;
import com.tatanstudios.astropollocliente.activitys.loginuser.LoginActivity;
import com.tatanstudios.astropollocliente.activitys.premios.PremiosActivity;
import com.tatanstudios.astropollocliente.activitys.reporteproblema.ReporteProblemaActivity;
import com.tatanstudios.astropollocliente.adaptadores.perfil.PerfilAdapter;
import com.tatanstudios.astropollocliente.modelos.perfil.ModelosPerfil;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class PerfilActivity extends AppCompatActivity {


    TextView txtToolbar;

    RelativeLayout root;

    TextView txtUsuario;

    RecyclerView recyclerPerfil;


    LinearLayout linear;


    List<ModelosPerfil> elementos;


    ApiService service;
    TokenManager tokenManager;
    ProgressBar progressBar;
    CompositeDisposable compositeDisposable = new CompositeDisposable();


    // nota de reporte app

    String txtReporte = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);


        txtToolbar = findViewById(R.id.txtToolbar);
        root = findViewById(R.id.root);
        txtUsuario = findViewById(R.id.txtUsuario);
        recyclerPerfil = findViewById(R.id.recyclerPerfil);
        linear = findViewById(R.id.linear);


        txtToolbar.setText(getString(R.string.perfil));


        elementos = new ArrayList<>();
        elementos.add(new ModelosPerfil(1, getString(R.string.direcciones)));
        elementos.add(new ModelosPerfil(2,  getString(R.string.cambio_de_contrasena)));
        elementos.add(new ModelosPerfil(3,  getString(R.string.horarios)));
        elementos.add(new ModelosPerfil(4,  getString(R.string.historial_de_compras)));
        elementos.add(new ModelosPerfil(5,  getString(R.string.premios)));
        elementos.add(new ModelosPerfil(6,  getString(R.string.reportar_problema)));
        elementos.add(new ModelosPerfil(7, getString(R.string.cerrar_sesion)));

        tokenManager = TokenManager.getInstance(this.getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);


        peticionServidor();
    }

    // IINFORMACION PARA OBTENER EL USUARIO DEL CLIENTE
    void peticionServidor(){

        progressBar.setVisibility(View.VISIBLE);

        // esta peticion el usuario ya tiene una direccion registrada
        String id = tokenManager.getToken().getId();
        compositeDisposable.add(
                service.informacionCliente(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuesta != null) {

                                        if(apiRespuesta.getSuccess() == 1){

                                           txtUsuario.setText(apiRespuesta.getUsuario());

                                            PerfilAdapter adapter = new PerfilAdapter(this, elementos, this);
                                            recyclerPerfil.setHasFixedSize(true);
                                            recyclerPerfil.setLayoutManager(new LinearLayoutManager(this));
                                            recyclerPerfil.setAdapter(adapter);

                                            linear.setVisibility(View.VISIBLE);

                                        }else{
                                            mensajeSinConexion();
                                        }

                                    }else{
                                        mensajeSinConexion();
                                    }
                                },
                                throwable -> {
                                    mensajeSinConexion();
                                })
        );
    }


    public void verPosicion(int pos){

        if(pos == 1){
            Intent i = new Intent(this, ListaDireccionesActivity.class);
            startActivity(i);
        }
        else if(pos == 2){

            Intent i = new Intent(this, CambiarPasswordActivity.class);
            startActivity(i);
        }
        else if(pos == 3){

            Intent i = new Intent(this, HorariosActivity.class);
            startActivity(i);
        }

        else if(pos == 4){

            Intent i = new Intent(this, HistorialOrdenesActivity.class);
            startActivity(i);
        }
        else if(pos == 5){

            Intent i = new Intent(this, PremiosActivity.class);
            startActivity(i);
        }
        else if(pos == 6){

            // REPORTAR PROBLEMAS DE APP


            Intent i = new Intent(this, ReporteProblemaActivity.class);
            startActivity(i);

        }
        else{
            cerrarSesion();
        }
    }


    boolean seguroCerrarSesion = true;

    void cerrarSesion(){

        if(seguroCerrarSesion) {
            seguroCerrarSesion = false;
            KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.WARNING_TYPE);
            pDialog.setTitleText(getString(R.string.cerrar_sesion));
            pDialog.setContentText("");
            pDialog.setConfirmText(getString(R.string.si));
            pDialog.setContentTextSize(16);
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.confirmButtonColor(R.drawable.dialogo_theme_success)
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        salir();
                    });
            pDialog.cancelButtonColor(R.drawable.dialogo_theme_cancel)
                    .setContentTextSize(16)
                    .setCancelText(getString(R.string.no))
                    .setCancelClickListener(kAlertDialog -> {
                        kAlertDialog.dismissWithAnimation();
                        seguroCerrarSesion = true;
                    });
            pDialog.show();
        }

    }


    void salir(){
        tokenManager.deletePreferences();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    void mensajeSinConexion(){
        progressBar.setVisibility(View.GONE);
        Toasty.info(this, getString(R.string.sin_conexion)).show();
    }

    @Override
    public void onDestroy(){
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        if(compositeDisposable != null){
            compositeDisposable.clear();
        }
        super.onStop();
    }

}