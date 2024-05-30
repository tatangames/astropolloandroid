package com.tatanstudios.astropollocliente.activitys.procesar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.developer.kalert.KAlertDialog;
import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.activitys.principal.PrincipalActivity;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ProcesarModoTesteoActivity extends AppCompatActivity {



    TextView txtTotal;
    TextView txtDireccion;
    TextView txtCliente;
    TextView txtToolbar;
    RelativeLayout root;
    Button btnConfirmar;
    ApiService service;
    TokenManager tokenManager;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressBar progressBar;

    ConstraintLayout vista;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procesar_modo_testeo);


        vista = findViewById(R.id.vista);


        txtDireccion = findViewById(R.id.txtDireccion);
        txtCliente = findViewById(R.id.txtCliente);
        txtTotal = findViewById(R.id.txtTotal);
        txtToolbar = findViewById(R.id.txtToolbar);
        root = findViewById(R.id.root);
        btnConfirmar = findViewById(R.id.btnConfirmar);

        txtToolbar.setText(getString(R.string.procesar_orden));

        tokenManager = TokenManager.getInstance(this.getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);

        btnConfirmar.setOnClickListener(v -> {
            cerrarTeclado();
            confirmar();
        });

        peticionServidor();
    }




    void confirmar(){

        KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE);
        pDialog.setTitleText(getString(R.string.confirmar_orden));
        pDialog.setContentText("");
        pDialog.setConfirmText(getString(R.string.si));
        pDialog.setContentTextSize(16);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.confirmButtonColor(R.drawable.dialogo_theme_success)
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();


                     peticionEnviarOrden();
                });
        pDialog.cancelButtonColor(R.drawable.dialogo_theme_cancel);
        pDialog.setContentTextSize(16);
        pDialog.setCancelText(getString(R.string.cancelar));
        pDialog.setCancelClickListener(kAlertDialog -> kAlertDialog.dismissWithAnimation());
        pDialog.show();
    }


    // solicitar informacion
    void peticionServidor(){

        progressBar.setVisibility(View.VISIBLE);
        String id = tokenManager.getToken().getId();

        compositeDisposable.add(
                service.verProcesarOrdenModoTesteo(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuesta != null) {

                                        if(apiRespuesta.getSuccess() == 1){


                                            txtTotal.setText(apiRespuesta.getTotal());
                                            txtCliente.setText(apiRespuesta.getCliente());
                                            txtDireccion.setText(apiRespuesta.getDireccion());

                                            vista.setVisibility(View.VISIBLE);

                                        }
                                        else{
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

    //------------------------------------




    void peticionEnviarOrden(){


        progressBar.setVisibility(View.VISIBLE);
        String id = tokenManager.getToken().getId();

        compositeDisposable.add(
                service.finalizarModoTesteo(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuesta != null) {

                                        if(apiRespuesta.getSuccess() == 1){


                                            // mostrar pantalla de motoristas
                                            // finalizar ahi.

                                            salir();


                                        }
                                        else{
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

    void salir(){


        KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE);
        pDialog.setTitleText("Orden Enviada");
        pDialog.setContentText("Esperar confirmación del Restaurante");
        pDialog.setConfirmText("Aceptar");
        pDialog.setContentTextSize(16);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.confirmButtonColor(R.drawable.dialogo_theme_success)
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();

                    Intent intent = new Intent(this, EsperandoOrdenModoTesteoMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                });
        pDialog.show();
    }

    void mensajeSinConexion(){
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
    public void onStop() {
        if(compositeDisposable != null){
            compositeDisposable.clear();
        }
        super.onStop();
    }


}