package com.tatanstudios.astropollocliente.activitys.mapa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.developer.kalert.KAlertDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.activitys.principal.PrincipalActivity;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class DireccionNuevoMapaActivity extends AppCompatActivity {


    TextInputEditText edtNombre;
    TextInputEditText edtDireccion;

    TextInputEditText edtPunto;
    TextInputEditText edtTelefono;
    Button btnGuardar;

    RelativeLayout root;

    TokenManager tokenManager;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiService service;

    String nombre = "";
    String direccion = "";
    String puntoref = "";
    String telefono = "";

    String idzona = "";
    String latitud = "";
    String longitud = "";
    String latitudreal = "";
    String longitudreal = "";

    ProgressBar progressBar;
    KAlertDialog dialogoProcesar;

    private boolean seguroGuardar = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direccion_nuevo_mapa);

        root = findViewById(R.id.root);
        btnGuardar = findViewById(R.id.btnRegistro);
        edtTelefono = findViewById(R.id.inputEditTelefono);
        edtPunto = findViewById(R.id.inputEditReferencia);
        edtDireccion = findViewById(R.id.inputEditDireccion);
        edtNombre = findViewById(R.id.inputEditNombre);





        Intent intent = getIntent();
        if (intent != null) {
            idzona = intent.getStringExtra("KEY_ZONA");
            latitud = intent.getStringExtra("KEY_LATITUD");
            longitud = intent.getStringExtra("KEY_LONGITUD");
            latitudreal = intent.getStringExtra("KEY_LATITUDREAL");
            longitudreal = intent.getStringExtra("KEY_LONGITUDREAL");
        }

        dialogoProcesar = new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);
        progressBar.setVisibility(View.GONE);

        btnGuardar.setOnClickListener(v -> {
            campos();
        });
    }

    void campos(){
        nombre = edtNombre.getText().toString();
        direccion = edtDireccion.getText().toString();
        puntoref = edtPunto.getText().toString();
        telefono = edtTelefono.getText().toString();

        cerrarTeclado();

        if (TextUtils.isEmpty(nombre)) {
            Toasty.info(this, getResources().getString(R.string.nombre_es_requerido)).show();
            return;
        }

        if (TextUtils.isEmpty(telefono)) {
            Toasty.info(this, getResources().getString(R.string.telefono_es_requerido)).show();
            return;
        }

        // 7 digitos para telefono
        if (telefono.length() < 8) {
            Toasty.info(this, "Teléfono requiere 8 dígitos").show();
            return;
        }

        if (TextUtils.isEmpty(direccion)) {
            Toasty.info(this, getResources().getString(R.string.direccion_requerido)).show();
            return;
        }

        // punto de referencia es requerida siempre
        if (TextUtils.isEmpty(puntoref)) {
            Toasty.info(this, getResources().getString(R.string.punto_de_referencia_requerido)).show();
            return;
        }

        guardarDireccion();
    }

    void guardarDireccion(){

        KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE);
        pDialog.setTitleText(getString(R.string.guardar_direccion));
        pDialog.setContentText("");
        pDialog.setConfirmText(getString(R.string.si));
        pDialog.setContentTextSize(16);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.confirmButtonColor(R.drawable.dialogo_theme_success)
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    peticionGuardar();
                });
        pDialog.cancelButtonColor(R.drawable.dialogo_theme_cancel)
                .setContentTextSize(16)
                .setCancelText(getString(R.string.cancelar))
                .setCancelClickListener(kAlertDialog -> kAlertDialog.dismissWithAnimation());
        pDialog.show();
    }

    void peticionGuardar(){
        if(seguroGuardar){
            seguroGuardar = false;

            progressBar.setVisibility(View.VISIBLE);
            String idcliente = tokenManager.getToken().getId();
            compositeDisposable.add(
                    service.registrarDireccion(idcliente, nombre, direccion, puntoref, idzona,
                                    latitud, longitud, latitudreal, longitudreal, telefono)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .retry()
                            .subscribe(apiRespuestas -> {

                                        progressBar.setVisibility(View.GONE);
                                        seguroGuardar = true;

                                        if(apiRespuestas != null){
                                            if(apiRespuestas.getSuccess() == 1){
                                                direccionGuardadada();
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
    }

    void direccionGuardadada(){

        KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE);
        pDialog.setTitleText(getString(R.string.guardado));
        pDialog.setContentText(getString(R.string.la_nueva_direccion));
        pDialog.setConfirmText(getString(R.string.aceptar));
        pDialog.setContentTextSize(16);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.confirmButtonColor(R.drawable.dialogo_theme_success)
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();

                    // volver a pantalla principal
                    Intent intent = new Intent(this, PrincipalActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
        pDialog.show();
    }

    // cierra el teclado
    void cerrarTeclado() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    void mensajeSinConexion(){
        progressBar.setVisibility(View.GONE);
        Toasty.info(DireccionNuevoMapaActivity.this, getString(R.string.sin_conexion)).show();
    }
}
