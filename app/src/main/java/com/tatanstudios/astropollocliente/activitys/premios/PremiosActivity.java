package com.tatanstudios.astropollocliente.activitys.premios;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.developer.kalert.KAlertDialog;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.activitys.loginuser.LoginActivity;
import com.tatanstudios.astropollocliente.adaptadores.categorias.CategoriasTodasAdapter;
import com.tatanstudios.astropollocliente.adaptadores.premios.PremiosAdapter;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class PremiosActivity extends AppCompatActivity {


    TextView txtNota;
    TextView txtPuntos;
    RelativeLayout root;
    RecyclerView recyclerView;

    CardView cardVista;
    TextView txtToolbar;

    ApiService service;
    TokenManager tokenManager;
    ProgressBar progressBar;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    PremiosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premios);

        txtToolbar = findViewById(R.id.txtToolbar);
        txtNota = findViewById(R.id.txtNota);
        txtPuntos = findViewById(R.id.txtPuntos);
        recyclerView = findViewById(R.id.recycler);
        cardVista = findViewById(R.id.cardViewVista);
        root = findViewById(R.id.root);

        txtToolbar.setText(getString(R.string.premios));

        tokenManager = TokenManager.getInstance(this.getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new PremiosAdapter();

        peticionBuscarPremios();
    }

    void peticionBuscarPremios(){

        progressBar.setVisibility(View.VISIBLE);

        String id = tokenManager.getToken().getId();

        compositeDisposable.add(
                service.listaDePremiosDisponibles(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuesta != null) {

                                        if(apiRespuesta.getSuccess() == 1){

                                            txtNota.setText(apiRespuesta.getNota());
                                            txtPuntos.setText(apiRespuesta.getPuntos());

                                            if(apiRespuesta.getConteo() == 0){
                                                Toasty.info(this, "No hay Premios").show();
                                            }

                                            adapter = new PremiosAdapter(this, apiRespuesta.getListado(), this);
                                            recyclerView.setAdapter(adapter);

                                            cardVista.setVisibility(View.VISIBLE);

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


    public void verTipoBoton(int idpremio, int seleccion){

        if(seleccion == 1){
            alertaBorrar();
        }
        else{
            alertaSeleccion( idpremio);
        }
    }

    void alertaSeleccion(int idpremio){

        KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE);
        pDialog.setTitleText("Seleccionar Premio");
        pDialog.setContentText("");
        pDialog.setConfirmText(getString(R.string.aceptar));
        pDialog.setContentTextSize(16);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.confirmButtonColor(R.drawable.dialogo_theme_success)
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();

                    peticionSeleccionOrden(idpremio);
                });
        pDialog.cancelButtonColor(R.drawable.dialogo_theme_cancel)
                .setContentTextSize(16)
                .setCancelText(getString(R.string.cancelar))
                .setCancelClickListener(kAlertDialog -> kAlertDialog.dismissWithAnimation());
        pDialog.show();
    }


    void peticionSeleccionOrden(int idpremio){

        progressBar.setVisibility(View.VISIBLE);

        String id = tokenManager.getToken().getId();

        compositeDisposable.add(
                service.seleccionarPremio(id, idpremio)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuesta != null) {

                                        if(apiRespuesta.getSuccess() == 1){

                                            // premio ya no esta disponible

                                          alertaReglas(apiRespuesta.getTitulo(), apiRespuesta.getMensaje());
                                        }
                                        else if(apiRespuesta.getSuccess() == 2) {

                                            // PUNTOS NO ALCANZAN
                                            alertaReglas(apiRespuesta.getTitulo(), apiRespuesta.getMensaje());

                                        }
                                        else if(apiRespuesta.getSuccess() == 3) {
                                            peticionBuscarPremios();
                                            alertaGeneral(apiRespuesta.getTitulo(), apiRespuesta.getMensaje());
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


    void alertaGeneral(String titulo, String mensaje){

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


                });

        pDialog.show();
    }


    void alertaBorrar(){

        KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE);
        pDialog.setTitleText("Borrar Selección");
        pDialog.setContentText("");
        pDialog.setConfirmText(getString(R.string.aceptar));
        pDialog.setContentTextSize(16);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.confirmButtonColor(R.drawable.dialogo_theme_success)
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();

                    peticionBorrarSeleccion();
                });
        pDialog.cancelButtonColor(R.drawable.dialogo_theme_cancel)
                .setContentTextSize(16)
                .setCancelText(getString(R.string.cancelar))
                .setCancelClickListener(kAlertDialog -> kAlertDialog.dismissWithAnimation());
        pDialog.show();
    }



    void peticionBorrarSeleccion(){

        progressBar.setVisibility(View.VISIBLE);

        String id = tokenManager.getToken().getId();

        compositeDisposable.add(
                service.deseleccionarPremio(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuesta != null) {

                                        if(apiRespuesta.getSuccess() == 1){

                                            Toasty.success(this, "Actualizado").show();
                                            peticionBuscarPremios();
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



    void alertaReglas(String titulo, String mensaje){


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

                });

        pDialog.show();

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