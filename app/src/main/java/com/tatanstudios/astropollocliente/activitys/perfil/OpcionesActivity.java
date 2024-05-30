package com.tatanstudios.astropollocliente.activitys.perfil;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.angads25.toggle.widget.LabeledSwitch;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class OpcionesActivity extends AppCompatActivity {


    RelativeLayout root;
    Button btnGuardar;
    LabeledSwitch switch1;
    ScrollView scroll;
    TextView txtToolbar;
    TextView txtMensaje;
    ProgressBar progressBar;
    ApiService service;
    TokenManager tokenManager;
    CompositeDisposable compositeDisposable = new CompositeDisposable();


    int disponible = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones);


        txtMensaje = findViewById(R.id.txtMensaje);
        txtToolbar = findViewById(R.id.txtToolbar);
        root = findViewById(R.id.root);
        btnGuardar = findViewById(R.id.btnGuardar);
        switch1 = findViewById(R.id.switch1);
        scroll = findViewById(R.id.scroll);


        txtToolbar.setText(getString(R.string.opciones));

        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);

        peticionServidor();

        switch1.setLabelOff(getString(R.string.no_borrar));
        switch1.setLabelOn(getString(R.string.borrar));

        btnGuardar.setOnClickListener(v -> peticionGuardar());
    }


    void peticionServidor(){

        String id = tokenManager.getToken().getId();
        progressBar.setVisibility(View.VISIBLE);

        compositeDisposable.add(
                service.verOpcionCarrito(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuestas -> {

                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuestas != null) {

                                        if(apiRespuestas.getSuccess() == 1) {
                                            scroll.setVisibility(View.VISIBLE);

                                            txtMensaje.setText(apiRespuestas.getMensaje());

                                            if(apiRespuestas.getOpcion() == 1){
                                                switch1.setOn(true);
                                            }else{
                                                switch1.setOn(false);
                                            }

                                        }else {
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


    void peticionGuardar(){
        String id = tokenManager.getToken().getId();
        progressBar.setVisibility(View.VISIBLE);

        boolean sw1 = switch1.isOn();

        if(sw1){
            disponible = 1;
        }else{
            disponible = 0;
        }

        compositeDisposable.add(
                service.guardarOpcionCarrito(id, disponible)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuestas -> {

                                    progressBar.setVisibility(View.GONE);
                                    if(apiRespuestas != null) {

                                        if (apiRespuestas.getSuccess() == 1){
                                            Toasty.info(this,getString(R.string.actualizado)).show();
                                        }else {
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


    void mensajeSinConexion(){
        progressBar.setVisibility(View.GONE);
        Toasty.info(this, getResources().getString(R.string.sin_conexion)).show();
    }

    @Override
    public void onDestroy(){
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onPause(){
        compositeDisposable.clear();
        super.onPause();
    }



}