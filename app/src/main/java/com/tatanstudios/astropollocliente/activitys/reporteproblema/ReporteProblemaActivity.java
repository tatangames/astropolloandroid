package com.tatanstudios.astropollocliente.activitys.reporteproblema;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.developer.kalert.KAlertDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.jaredrummler.android.device.DeviceName;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.Field;

public class ReporteProblemaActivity extends AppCompatActivity {


    TextInputEditText edtNota;
    Button btnEnviar;
    RelativeLayout root;


    ApiService service;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressBar progressBar;
    TokenManager tokenManager;

    String textoNotaProblema = "";


    String manufacturer = "";  // "Samsung"
    String name = "";             // "Galaxy S8+"
    String model = "";              // "SM-G955W"
    String codename = "";         // "dream2qltecan"
    String deviceName = "";       // "Galaxy S8+"


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_problema);

        DeviceName.init(this);

        DeviceName.with(this).request((info, error) -> {
            manufacturer = info.manufacturer;  // "Samsung"
            name = info.marketName;            // "Galaxy S8+"
            model = info.model;                // "SM-G955W"
            codename = info.codename;          // "dream2qltecan"
            deviceName = info.getName();       // "Galaxy S8+"
        });


        root = findViewById(R.id.root);
        btnEnviar = findViewById(R.id.btnGuardar);
        edtNota = findViewById(R.id.inputEditProblema);

        tokenManager = TokenManager.getInstance(this.getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);


        btnEnviar.setOnClickListener(view -> verificarDatos() );
        progressBar.setVisibility(View.GONE);
    }


    void verificarDatos(){

        cerrarTeclado();

        textoNotaProblema = edtNota.getText().toString();

        if (TextUtils.isEmpty(textoNotaProblema)) {
            Toasty.info(this, "Descripción es requerida").show();
            return;
        }

        enviarNota();
    }


    void enviarNota(){

        progressBar.setVisibility(View.VISIBLE);

        String id = tokenManager.getToken().getId();

        compositeDisposable.add(
                service.enviarProblema(id, manufacturer, name, model, codename, deviceName, textoNotaProblema)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuestas -> {

                                    progressBar.setVisibility(View.GONE);
                                    if (apiRespuestas != null) {

                                        if (apiRespuestas.getSuccess() == 1) {

                                            notaEnviada();

                                        } else {
                                            mensajeSinConexion();
                                        }
                                    } else {
                                        mensajeSinConexion();
                                    }
                                },
                                throwable -> {
                                    mensajeSinConexion();
                                })
        );
    }


    void notaEnviada(){

        edtNota.setText("");

        KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE);
        pDialog.setTitleText("Nota Recibida");
        pDialog.setContentText("Muchas gracias por compartir experiencias de la aplicación");
        pDialog.setConfirmText(getString(R.string.aceptar));
        pDialog.setContentTextSize(16);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.confirmButtonColor(R.drawable.dialogo_theme_success)
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();

                   onBackPressed();
                });
        pDialog.show();
    }



    void mensajeSinConexion() {
        progressBar.setVisibility(View.GONE);
        Toasty.info(this, getString(R.string.sin_conexion)).show();
    }

    void cerrarTeclado() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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