package com.tatanstudios.astropollocliente.activitys.procesar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Constraints;

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
import android.widget.ScrollView;
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

public class ProcesarActivity extends AppCompatActivity {

    TextView txtDireccion;
    TextView txtCliente;
    EditText edtNota;
    TextView txtToolbar;
    RelativeLayout root;
    Button btnConfirmar;
    Button btnCupon;
    TextView txtCupon;
    ApiService service;
    TokenManager tokenManager;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressBar progressBar;

    boolean seguroEnviar = true;
    int minimo = 0;
    String minimoConsumo = "";


    // CUPONES

    int tengoCupon = 0;
    int estadoBoton = 1;  // 1: para agregar cupon, 2: borrar cupon

    String cupon = "";


    // LOS TEXTOS QUE APARECERAN SI SE APLICA UN CUPON

    TextView textoTotalNormal;

    TextView txtTotalNormal;


    TextView textoTotalCupon;
    TextView txtTotalCupon;
    String idfirebase = "";

    TextView txtPremio;


    ConstraintLayout vista;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procesar);


        txtTotalCupon = findViewById(R.id.txtTotal2);
        textoTotalCupon = findViewById(R.id.textoTotal2);
        txtTotalNormal = findViewById(R.id.txtTotal);
        textoTotalNormal = findViewById(R.id.textoTotal);

        vista = findViewById(R.id.vista);

        txtPremio = findViewById(R.id.txtPremio);

        txtDireccion = findViewById(R.id.txtDireccion);
        txtCliente = findViewById(R.id.txtCliente);
        edtNota = findViewById(R.id.edtNota);
        txtToolbar = findViewById(R.id.txtToolbar);
        root = findViewById(R.id.root);
        btnConfirmar = findViewById(R.id.btnConfirmar);
        btnCupon = findViewById(R.id.btnCupon);
        txtCupon = findViewById(R.id.txtCupon);


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


        // ocultar
        textoTotalCupon.setVisibility(View.GONE);
        txtTotalCupon.setVisibility(View.GONE);


        OSDeviceState device = OneSignal.getDeviceState();

        if(device != null){
            idfirebase = device.getUserId();
        }


        peticionServidor();

        // ******  CUPONES  ****


        btnCupon.setOnClickListener(v -> {
            if(estadoBoton == 1){
                modalCupon();
            }else{
                // borrar cupon
                modalBorrarCupon();
            }
        });
    }


    void confirmar(){

        if(minimo == 0){ // minimo de consumo
            KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.WARNING_TYPE);
            pDialog.setTitleText(getString(R.string.nota));
            pDialog.setContentText(minimoConsumo);
            pDialog.setConfirmText(getString(R.string.aceptar));
            pDialog.setContentTextSize(16);
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.confirmButtonColor(R.drawable.dialogo_theme_success)
                    .setConfirmClickListener(KAlertDialog::dismissWithAnimation);
            pDialog.show();
        }else{
            confirmarPregunta();
        }
    }

    void confirmarPregunta(){
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
                service.verProcesarOrden(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuesta != null) {

                                        if(apiRespuesta.getSuccess() == 1) {

                                            // SIN DIRECCION EL CLIENTE
                                            salir(0);

                                        }else if(apiRespuesta.getSuccess() == 2){

                                            // 1: si puede ordenes
                                            // 0: el minimo de consumo no alcanza
                                            minimo = apiRespuesta.getMinimo();
                                            minimoConsumo = apiRespuesta.getMensaje();

                                            textoTotalNormal.setText(getString(R.string.total));
                                            txtTotalNormal.setText(apiRespuesta.getTotal());

                                            txtCliente.setText(apiRespuesta.getCliente());
                                            txtDireccion.setText(apiRespuesta.getDireccion());

                                            // BOTON CUPON
                                            if(apiRespuesta.getUsacupon() == 1){
                                                btnCupon.setVisibility(View.VISIBLE);

                                                // porque aun no ha colocado cupon sera gone
                                                txtCupon.setVisibility(View.GONE);
                                            }else{
                                                btnCupon.setVisibility(View.GONE);
                                                txtCupon.setVisibility(View.GONE);
                                            }


                                            if(apiRespuesta.getUsaPremio() == 1){

                                                txtPremio.setText(apiRespuesta.getTextoPremio());
                                                txtPremio.setVisibility(View.VISIBLE);
                                            }else{
                                                txtPremio.setText("");
                                                txtPremio.setVisibility(View.GONE);
                                            }

                                            vista.setVisibility(View.VISIBLE);

                                        }
                                        else if(apiRespuesta.getSuccess() == 3){
                                            // carrito no encontrado
                                            Toasty.info(this, getString(R.string.carrito_no_encontrado)).show();
                                            salir(0);
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

    //------------------------------------

    // enviar orden
    void peticionEnviarOrden(){

        if(seguroEnviar){
            seguroEnviar = false;
            progressBar.setVisibility(View.VISIBLE);
            String id = tokenManager.getToken().getId();

            String nota = edtNota.getText().toString();

            String version = getString(R.string.app_registro);

            compositeDisposable.add(
                    service.enviarOrden(id, nota, cupon, tengoCupon, version, idfirebase)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(apiRespuesta -> {

                                        progressBar.setVisibility(View.GONE);
                                        seguroEnviar = true;

                                        if(apiRespuesta != null) {

                                            // REGLAS
                                            if(apiRespuesta.getSuccess() == 1) {
                                                alertaMensaje(apiRespuesta.getTitulo(), apiRespuesta.getMensaje());
                                            }

                                            // ORDEN ENVIADA
                                            else if(apiRespuesta.getSuccess() == 10){

                                                // ENVIAR NOTIFICACION A RESTAURANTE
                                                peticionNotiRestaurante(apiRespuesta.getIdorden());

                                                ordenEnviada(apiRespuesta.getTitulo(), apiRespuesta.getMensaje());
                                            }
                                            else{
                                                // errores
                                                Toasty.info(this, getString(R.string.error_al_enviar_orden)).show();
                                            }

                                        }else{
                                            mensajeSinConexion();
                                            seguroEnviar = true;
                                        }
                                    },
                                    throwable -> {
                                        seguroEnviar = true;
                                        mensajeSinConexion();
                                    })
            );
        }
    }

    void peticionNotiRestaurante(int id){

        compositeDisposable.add(
                service.enviarNotiRestauranteOrdenar(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(apiRespuesta -> {


                                },
                                throwable -> {

                                })
        );
    }


    void ordenEnviada(String titulo, String mensaje){
        KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE);
        pDialog.setTitleText(titulo);
        pDialog.setContentText(mensaje);
        pDialog.setConfirmText("Aceptar");
        pDialog.setContentTextSize(16);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.confirmButtonColor(R.drawable.dialogo_theme_success)
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    salir(1);
                });
        pDialog.show();
    }

    void alertaMensaje(String titulo, String mensaje){
        KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.WARNING_TYPE);
        pDialog.setTitleText(titulo);
        pDialog.setContentText(mensaje);
        pDialog.setConfirmText("Aceptar");
        pDialog.setContentTextSize(16);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.confirmButtonColor(R.drawable.dialogo_theme_success)
                .setConfirmClickListener(KAlertDialog::dismissWithAnimation);
        pDialog.show();
    }

    void salir(int vista){

        Intent intent = new Intent(this, PrincipalActivity.class);
        intent.putExtra("VISTA", vista); // 1: vista historial
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    void mensajeSinConexion(){
        progressBar.setVisibility(View.GONE);
        Toasty.info(this, getString(R.string.sin_conexion)).show();
    }



    //**************  CUPONES  ***********************

    void modalBorrarCupon(){

        new KAlertDialog(this, KAlertDialog.WARNING_TYPE)
                .setContentTextSize(20)
                .setTitleText("Nota")
                .setConfirmText(getString(R.string.borrar_cupon))
                .setContentTextSize(16)
                .confirmButtonColor(R.drawable.dialogo_theme_success)
                .setConfirmClickListener(sDialog ->{
                    sDialog.dismissWithAnimation();

                    estadoBoton = 1;
                    tengoCupon = 0;

                    txtCupon.setText("");
                    txtCupon.setVisibility(View.GONE);
                    btnCupon.setText(getString(R.string.cupon));

                    textoTotalCupon.setText("");
                    txtTotalCupon.setText("");
                    textoTotalCupon.setVisibility(View.GONE);
                    txtTotalCupon.setVisibility(View.GONE);


                    // RECARGAR
                    peticionServidor();
                })
                .cancelButtonColor(R.drawable.dialogo_theme_cancel)
                .setContentTextSize(16)
                .setCancelText(getString(R.string.cancelar))
                .setCancelClickListener(kAlertDialog -> kAlertDialog.dismissWithAnimation())
                .show();
    }



    void modalCupon(){
        final EditText edtMensaje = new EditText(this);
        edtMensaje.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.cupon))
                .setMessage(getString(R.string.agregar))
                .setView(edtMensaje)
                .setPositiveButton(getString(R.string.verificar), (dialog1, which) -> {
                    cupon = (edtMensaje.getText().toString());
                    if(cupon.length() > 50) {
                        Toasty.info(this, "Máximo 50 caracteres, agrego(" + cupon.length() + ")").show();
                        return;
                    }

                    if(cupon.isEmpty()){
                        Toasty.info(this, getString(R.string.cupon_es_requerido)).show();
                        return;
                    }

                    peticionVerificarCupon();
                })
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.show();
    }


    void peticionVerificarCupon(){

        // RETORNOS
        // 1: carrito de compras no encontrado
        // 1: cupon no valido (estado igual)

        // 2: cupon producto gratis
        // 3: cupon descuento de dinero
        // 4: cupon descuento de porcentaje


        progressBar.setVisibility(View.VISIBLE);
        String id = tokenManager.getToken().getId();
        compositeDisposable.add(
                service.verificarCupon(id, cupon)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuesta != null) {

                                        if(apiRespuesta.getSuccess() == 1) {

                                            Toasty.info(this, getString(R.string.cupon_no_valido)).show();

                                        }else if(apiRespuesta.getSuccess() == 2){

                                            // CUPON PARA PRODUCTO GRATIS
                                            alertaMensajeCupones(apiRespuesta.getTitulo(), apiRespuesta.getMensaje());
                                        }

                                        else if(apiRespuesta.getSuccess() == 3){


                                            // CUPON PARA DESCUENTO DE DINERO

                                            textoTotalNormal.setText(getString(R.string.sub_total));
                                            textoTotalCupon.setText(getString(R.string.total));
                                            txtTotalCupon.setText(apiRespuesta.getResta());

                                            textoTotalCupon.setVisibility(View.VISIBLE);
                                            txtTotalCupon.setVisibility(View.VISIBLE);

                                            alertaMensajeCupones(apiRespuesta.getTitulo(), apiRespuesta.getMensaje());

                                        }else if(apiRespuesta.getSuccess() == 4){


                                            // CUPON PARA DESCUENTO DE PORCENTAJE

                                            textoTotalNormal.setText(getString(R.string.sub_total));
                                            textoTotalCupon.setText(getString(R.string.total));
                                            txtTotalCupon.setText(apiRespuesta.getResta());

                                            textoTotalCupon.setVisibility(View.VISIBLE);
                                            txtTotalCupon.setVisibility(View.VISIBLE);

                                            alertaMensajeCupones(apiRespuesta.getTitulo(), apiRespuesta.getMensaje());

                                        }
                                        else{

                                            Toasty.info(this, getString(R.string.cupon_no_valido)).show();
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

    void alertaMensajeCupones(String titulo, String mensaje){

        txtCupon.setText(mensaje);
        txtCupon.setVisibility(View.VISIBLE);

        tengoCupon = 1;

        btnCupon.setText(getString(R.string.borrar));
        estadoBoton = 2;

        new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE)
                .setContentTextSize(20)
                .setTitleText(titulo)
                .setContentText(mensaje)
                .setConfirmText(getString(R.string.aceptar))
                .setContentTextSize(16)
                .confirmButtonColor(R.drawable.dialogo_theme_success)
                .setConfirmClickListener(sDialog ->{
                    sDialog.dismissWithAnimation();
                })
                .show();
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