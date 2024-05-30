package com.tatanstudios.astropollocliente.activitys.ordenes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.developer.kalert.KAlertDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.modelos.ordenes.ModeloOrdenesActivasList;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class OrdenesEstadoActivasActivity extends AppCompatActivity {


    TextView txtOrden;

    TextView btnProductos;

    Button btnCancelar;

    //*****************

    ImageView circuloIniciada;

    TextView txtIniciada;

    TextView txtFechaIniciada;


    View lineaPendiente;


    //*****************



    ImageView circuloEncamino;

    TextView txtEncamino;

    TextView txtFechaEncamino;

    Button btnMotorista;


    View lineaMotorista;


    //*****************


    ImageView circuloEntregada;

    TextView txtEntregada;

    TextView txtFechaEntregada;

    Button btnCompletar;


    //*****************

    ImageView circuloCancelada;
    View lineaCancelada;
    TextView txtCancelada;
    TextView txtNotaCancelada;



    SwipeRefreshLayout refresh;

    TextView txtToolbar;

    RelativeLayout root;


    Dialog miDialogoCalificar;
    Button btnEnviar;
    float estrellas = 5;
    RatingBar ratingBar;



    RequestOptions opcionesGlide = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .priority(Priority.NORMAL);

    boolean boolOrdenCancelada = false;


    ApiService service;
    TokenManager tokenManager;
    ProgressBar progressBar;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    TextInputEditText edtNotaCalificatoria;


    int ordenid = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordenes_estado_activas);


        refresh = findViewById(R.id.swipe);
        txtToolbar = findViewById(R.id.txtToolbar);
        root = findViewById(R.id.root);

        circuloCancelada = findViewById(R.id.img6);
        lineaCancelada = findViewById(R.id.view12);
        txtCancelada = findViewById(R.id.txtEstado6);
        txtNotaCancelada = findViewById(R.id.txtFecha6);

        circuloEntregada = findViewById(R.id.img2);
        txtEntregada = findViewById(R.id.txtEstado4);
        txtFechaEntregada = findViewById(R.id.txtEstado5);
        btnCompletar = findViewById(R.id.btnMotorista2);


        circuloEncamino = findViewById(R.id.img);
        txtEncamino = findViewById(R.id.txtEstado2);
        txtFechaEncamino = findViewById(R.id.txtEstado3);
        btnMotorista = findViewById(R.id.btnMotorista);
        lineaMotorista = findViewById(R.id.view2);


        txtOrden = findViewById(R.id.txtOrden);
        btnProductos = findViewById(R.id.txtProducto);
        btnCancelar = findViewById(R.id.btnCancelar);

        circuloIniciada = findViewById(R.id.img1);
        txtIniciada = findViewById(R.id.txtEstado1);
        txtFechaIniciada = findViewById(R.id.txtFecha1);
        lineaPendiente = findViewById(R.id.view8);



        //*****************


        Intent intent = getIntent();
        if (intent != null) {
            ordenid = intent.getIntExtra("KEY_ORDEN", 0);
        }

        txtToolbar.setText(getString(R.string.estados));

        tokenManager = TokenManager.getInstance(this.getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);

        btnProductos.setOnClickListener(v -> verProductos());
        btnCompletar.setOnClickListener(v ->calificar());
        btnCancelar.setOnClickListener(v ->cancelarOrden());

        btnMotorista.setOnClickListener(v -> verMotorista());

        refresh.setEnabled(false);

        ocultarControles();
        peticionServidor();

        refresh.setOnRefreshListener(() -> {
            refresh.setEnabled(false);
            peticionServidor();
        });
    }

    void ocultarControles(){

        txtNotaCancelada.setVisibility(View.GONE);
        txtCancelada.setVisibility(View.GONE);
        circuloCancelada.setVisibility(View.GONE);
        lineaCancelada.setVisibility(View.GONE);

        btnCompletar.setVisibility(View.GONE);
        txtFechaEntregada.setVisibility(View.GONE);
        txtEntregada.setVisibility(View.GONE);
        circuloEntregada.setVisibility(View.GONE);
        lineaMotorista.setVisibility(View.GONE);

        btnMotorista.setVisibility(View.GONE);
        txtFechaEncamino.setVisibility(View.GONE);
        txtEncamino.setVisibility(View.GONE);
        circuloEncamino.setVisibility(View.GONE);

        lineaPendiente.setVisibility(View.GONE);

        txtIniciada.setText("");
        txtFechaIniciada.setText("");
    }


    // informacion de la orden
    void peticionServidor(){


        refresh.setRefreshing(true);

        progressBar.setVisibility(View.VISIBLE);

        compositeDisposable.add(
                service.verEstadosDeOrden(ordenid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    refresh.setRefreshing(false);
                                    refresh.setEnabled(true);

                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuesta != null) {
                                        if(apiRespuesta.getSuccess() == 1) {

                                            refresh.setVisibility(View.VISIBLE);


                                            for (ModeloOrdenesActivasList m : apiRespuesta.getOrdenes()) {
                                                txtOrden.setText("Orden #:" + m.getId());

                                                txtIniciada.setText(m.getTextoiniciada());
                                                txtFechaIniciada.setText(m.getFechaEstimada());

                                                if(m.getEstadoIniciada() == 1){
                                                    Glide.with(this)
                                                            .load(R.drawable.marcador_azul)
                                                            .apply(opcionesGlide)
                                                            .into(circuloIniciada);

                                                    btnCancelar.setVisibility(View.GONE);

                                                    lineaPendiente.setVisibility(View.VISIBLE);
                                                }else{
                                                    Glide.with(this)
                                                            .load(R.drawable.marcador_gris)
                                                            .apply(opcionesGlide)
                                                            .into(circuloEncamino);

                                                    btnCancelar.setVisibility(View.VISIBLE);

                                                    lineaPendiente.setVisibility(View.GONE);
                                                }



                                                // MOTORISTA VIENE EN CAMINO


                                                if(m.getEstadoCamino() == 1){

                                                    txtEncamino.setText(m.getTextoCamino());
                                                    txtFechaEncamino.setText(m.getFechaCamino());

                                                    Glide.with(this)
                                                            .load(R.drawable.marcador_azul)
                                                            .apply(opcionesGlide)
                                                            .into(circuloEncamino);

                                                    circuloEncamino.setVisibility(View.VISIBLE);
                                                    txtEncamino.setVisibility(View.VISIBLE);
                                                    txtFechaEncamino.setVisibility(View.VISIBLE);

                                                    btnMotorista.setVisibility(View.VISIBLE);


                                                }



                                                // ORDEN ENTREGADA


                                                if(m.getEstadoEntrega() == 1){

                                                    Glide.with(this)
                                                            .load(R.drawable.marcador_azul)
                                                            .apply(opcionesGlide)
                                                            .into(circuloEntregada);


                                                    txtEntregada.setText(m.getTextoEntregada());
                                                    txtFechaEntregada.setText(m.getFechaEntrega());

                                                    lineaMotorista.setVisibility(View.VISIBLE);

                                                    circuloEntregada.setVisibility(View.VISIBLE);
                                                    txtEntregada.setVisibility(View.VISIBLE);
                                                    txtFechaEntregada.setVisibility(View.VISIBLE);

                                                    btnCompletar.setVisibility(View.VISIBLE);
                                                }

                                                if(m.getEstadoCancelada() == 1){

                                                    txtIniciada.setText("");
                                                    txtFechaIniciada.setText("");

                                                    boolOrdenCancelada = true;

                                                    txtCancelada.setText(getString(R.string.orden_cancelada));
                                                    txtNotaCancelada.setText(m.getNotaCancelada());


                                                    btnCancelar.setText(getString(R.string.borrar));
                                                    btnCancelar.setVisibility(View.VISIBLE);


                                                    Glide.with(this)
                                                            .load(R.drawable.marcador_rojo)
                                                            .apply(opcionesGlide)
                                                            .into(circuloCancelada);

                                                    lineaCancelada.setVisibility(View.VISIBLE);
                                                    circuloCancelada.setVisibility(View.VISIBLE);
                                                    txtCancelada.setVisibility(View.VISIBLE);
                                                    txtNotaCancelada.setVisibility(View.VISIBLE);


                                                }
                                            }

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

    // mensaje para cancelar orden
    void cancelarOrden(){

        if(boolOrdenCancelada){
            peticionOcultarMiOrden();
        }else{
            new KAlertDialog(this, KAlertDialog.WARNING_TYPE)
                    .setContentTextSize(20)
                    .setTitleText(getString(R.string.cancelar_orden))
                    .setConfirmText(getString(R.string.si))
                    .setContentTextSize(16)
                    .confirmButtonColor(R.drawable.dialogo_theme_success)
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        peticionCancelar();
                    })
                    .cancelButtonColor(R.drawable.dialogo_theme_cancel)
                    .setContentTextSize(16)
                    .setCancelText(getString(R.string.cancelar))
                    .setCancelClickListener(kAlertDialog -> kAlertDialog.dismissWithAnimation())
                    .show();
        }
    }


    // cancelar la orden
    void peticionCancelar(){


        progressBar.setVisibility(View.VISIBLE);
        compositeDisposable.add(
                service.cancelarOrden(ordenid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {
                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuesta != null) {
                                        if(apiRespuesta.getSuccess() == 1){

                                            // ORDEN FUE INICIADA
                                            alertaCancelaNo(apiRespuesta.getTitulo(), apiRespuesta.getMensaje());

                                        }
                                        else if(apiRespuesta.getSuccess() == 2){

                                            // ORDEN CANCELADA CORRECTAMENTE
                                            alertaCanceladaSuccess(apiRespuesta.getTitulo(), apiRespuesta.getMensaje());
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


    void alertaCanceladaSuccess(String titulo, String mensaje){

        KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE);
        pDialog.setTitleText(titulo);
        pDialog.setContentText(mensaje);
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

    void alertaCancelaNo(String titulo, String mensaje){

        KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.WARNING_TYPE);
        pDialog.setTitleText(titulo);
        pDialog.setContentText(mensaje);
        pDialog.setConfirmText(getString(R.string.aceptar));
        pDialog.setContentTextSize(16);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.confirmButtonColor(R.drawable.dialogo_theme_success)
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    peticionServidor();
                });
        pDialog.show();
    }


    void verMotorista(){

        progressBar.setVisibility(View.VISIBLE);
        compositeDisposable.add(
                service.verMotorista(ordenid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuesta != null) {
                                        if(apiRespuesta.getSuccess() == 1) { // completada

                                            String foto = apiRespuesta.getFoto();
                                            String nombre = apiRespuesta.getNombre();
                                            String vehiculo = apiRespuesta.getVehiculo();
                                            String placa = apiRespuesta.getPlaca();
                                            mostrarMotorista(foto, nombre, vehiculo, placa);

                                        }else if(apiRespuesta.getSuccess() == 2){ // motorista no encontrado
                                            Toasty.info(this, getString(R.string.motorista_no_encontrado)).show();
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


    Dialog miDialogo;

    boolean ocupadoVistaMotorista = false;




    void mostrarMotorista(String foto, String nombre, String vehiculo, String placa){

        if(ocupadoVistaMotorista){
            return;
        }

        ocupadoVistaMotorista = true;

        miDialogo = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        miDialogo.setContentView(R.layout.activity_ver_motorista);

        ImageView imgMotorista = miDialogo.findViewById(R.id.imgMotorista);
        TextView txtNombre = miDialogo.findViewById(R.id.txtNombre);
        TextView txtVehiculo = miDialogo.findViewById(R.id.txtVehiculo);
        TextView txtPlaca = miDialogo.findViewById(R.id.txtPlaca);

        TextView txtToolbar = miDialogo.findViewById(R.id.txtToolbar);

        txtToolbar.setText(getString(R.string.motorista));

        if(foto != null) {
            Glide.with(this)
                    .load(RetrofitBuilder.urlImagenes + foto)
                    .apply(opcionesGlide)
                    .into(imgMotorista);
        }
        txtNombre.setText(nombre);
        txtVehiculo.setText(vehiculo);
        txtPlaca.setText(placa);

        miDialogo.show();

        miDialogo.setOnDismissListener(dialogInterface -> {
            miDialogo.dismiss();
            ocupadoVistaMotorista = false;
        });
    }


    // esto para evitar abrir 2 ventanas al mismo tiempo
    boolean ocupadoVistaCalificar = false;

    // calificaciones de motorista
    void calificar(){


        if(ocupadoVistaCalificar){
            return;
        }

        ocupadoVistaCalificar = true;

        miDialogoCalificar = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        miDialogoCalificar.setContentView(R.layout.vista_calificacion_motorista);

        edtNotaCalificatoria = miDialogoCalificar.findViewById(R.id.edtNombre);
        btnEnviar = miDialogoCalificar.findViewById(R.id.btnEnviar);
        ratingBar = miDialogoCalificar.findViewById(R.id.ratingBar);

        TextView toolbar = miDialogoCalificar.findViewById(R.id.txtToolbar);
        toolbar.setText(getString(R.string.calificar_orden));

        btnEnviar.setOnClickListener(v ->mensajeCalificacion());

        ratingBar.setOnRatingBarChangeListener((ratingBar, valor, b) -> estrellas = valor);

        miDialogoCalificar.show();

        miDialogoCalificar.setOnDismissListener(dialogInterface -> {
            miDialogoCalificar.dismiss();
            ocupadoVistaCalificar = false;
        });
    }

    // verificar
    void mensajeCalificacion(){

        if(estrellas == 0){
            estrellas = 5;
        }

        KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE);
        pDialog.setTitleText(getString(R.string.enviar_calificacion));
        pDialog.setContentText("");
        pDialog.setConfirmText(getString(R.string.si));
        pDialog.setContentTextSize(16);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.confirmButtonColor(R.drawable.dialogo_theme_success)
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();

                    String notas = edtNotaCalificatoria.getText().toString();

                    peticionEnviarCalificacion(notas);
                });

        pDialog.cancelButtonColor(R.drawable.dialogo_theme_cancel)
                .setContentTextSize(16)
                .setCancelText(getString(R.string.cancelar))
                .setCancelClickListener(kAlertDialog -> kAlertDialog.dismissWithAnimation());

        pDialog.show();
    }

    // enviar calificacion de motorista
    void peticionEnviarCalificacion(String notas){

        cerrarTeclado();

        progressBar.setVisibility(View.VISIBLE);
        compositeDisposable.add(
                service.calificarOrden(ordenid, Math.round(estrellas), notas)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuesta != null) {
                                        if(apiRespuesta.getSuccess() == 1){
                                           // CALIFICADA CORRECTAMENTE

                                            Toasty.info(this, getString(R.string.muchas_gracias)).show();
                                            onBackPressed();

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

    void peticionOcultarMiOrden(){

        progressBar.setVisibility(View.VISIBLE);
        compositeDisposable.add(
                service.ocultarMiOrden(ordenid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuesta != null) {
                                        if(apiRespuesta.getSuccess() == 1){
                                            onBackPressed();
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

    void cerrarTeclado() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    void verProductos(){
        Intent res = new Intent(this, ProductosOrdenesActivity.class);
        res.putExtra("KEY_ORDEN", ordenid);
        startActivity(res);
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

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        super.onBackPressed();
    }

}