package com.tatanstudios.astropollocliente.fragmentos.direcciones;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.developer.kalert.KAlertDialog;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.activitys.principal.PrincipalActivity;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentInfoDireccionCliente extends Fragment {

    Button btnSeleccionar;

    ImageView imgBorrar;
    TextView txtNombre;
    TextView txtDireccion;

    TextView txtPuntoReferencia;
    TextView txtTelefono;

    TextView txtToolbar;

    RelativeLayout root;

    int iddireccion = 0;
    String nombre = "";
    String direccion = "";
    String puntoReferencia = "";
    String telefono = "";

    ApiService service;
    TokenManager tokenManager;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressBar progressBar;

    private boolean seguroBorrar = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_info_direccion_cliente, container, false);

        root = vista.findViewById(R.id.root);
        txtToolbar = vista.findViewById(R.id.txtToolbar);
        txtTelefono = vista.findViewById(R.id.txtTelefono);
        txtPuntoReferencia = vista.findViewById(R.id.txtReferencia);
        txtDireccion = vista.findViewById(R.id.txtDireccion);
        txtNombre = vista.findViewById(R.id.txtNombre);
        imgBorrar = vista.findViewById(R.id.imgBorrar);
        btnSeleccionar = vista.findViewById(R.id.btnSeleccionar);


        txtToolbar.setText(getString(R.string.direccion));

        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);

        progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);
        progressBar.setVisibility(View.GONE);

        Bundle bundle = getArguments();
        if(bundle != null) {
            iddireccion = bundle.getInt("KEY_ID");
            nombre = bundle.getString("KEY_NOMBRE");
            txtNombre.setText(nombre);
            direccion = bundle.getString("KEY_DIRECCION");
            txtDireccion.setText(direccion);
            puntoReferencia = bundle.getString("KEY_REFERENCIA");
            txtPuntoReferencia.setText(puntoReferencia);

            telefono = bundle.getString("KEY_TELEFONO");
            txtTelefono.setText(telefono);
        }

        btnSeleccionar.setOnClickListener(v -> seleccionarDireccion());
        imgBorrar.setOnClickListener(v -> preguntarBorrar());

        return vista;
    }

    void seleccionarDireccion(){
        progressBar.setVisibility(View.VISIBLE);
        String iduser = tokenManager.getToken().getId();

        compositeDisposable.add(
                service.elegirDireccion(iduser, iddireccion)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuestas -> {
                                    progressBar.setVisibility(View.GONE);
                                    if(apiRespuestas != null){

                                        if(apiRespuestas.getSuccess() == 1){

                                            volverPantallaPrincipal(getString(R.string.direccion_seleccionada));
                                        }else{
                                            mensajeSinConexion();
                                        }
                                    }else{
                                        mensajeSinConexion();
                                    }
                                },
                                throwable -> mensajeSinConexion())
        );
    }

    // preguntar si borrar la direccion
    void preguntarBorrar(){

        if(seguroBorrar){
            seguroBorrar = false;
            KAlertDialog pDialog = new KAlertDialog(getActivity(), KAlertDialog.WARNING_TYPE);
            pDialog.setTitleText(getString(R.string.borrar_esta_direccion));
            pDialog.setContentText("");
            pDialog.setConfirmText(getString(R.string.borrar));
            pDialog.setContentTextSize(16);
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.confirmButtonColor(R.drawable.dialogo_theme_success)
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        borrarDireccion();
                    });
            pDialog.cancelButtonColor(R.drawable.dialogo_theme_cancel)
                    .setContentTextSize(16)
                    .setCancelText(getString(R.string.cancelar))
                    .setCancelClickListener(kAlertDialog -> {
                        kAlertDialog.dismissWithAnimation();
                        seguroBorrar = true;
                    });
            pDialog.show();
        }
    }

    // preguntar si quiere borrar
    void borrarDireccion(){

        String id = tokenManager.getToken().getId();

        progressBar.setVisibility(View.VISIBLE);
        compositeDisposable.add(
                service.eliminarDireccion(id, iddireccion)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuestas -> {

                                    seguroBorrar = true;
                                    progressBar.setVisibility(View.GONE);
                                    if (apiRespuestas != null) {

                                        if (apiRespuestas.getSuccess() == 1) {

                                            volverPantallaPrincipal(getString(R.string.direccion_borrada));
                                        } else if (apiRespuestas.getSuccess() == 2) {
                                            // no puede borrar, ya que minimo 1 direccion puede tener
                                            alerta();
                                        } else {
                                            mensajeSinConexion();
                                        }
                                    } else {
                                        mensajeSinConexion();
                                    }
                                },
                                throwable -> mensajeSinConexion())
        );
    }

    void alerta(){

        KAlertDialog pDialog = new KAlertDialog(getActivity(), KAlertDialog.WARNING_TYPE);
        pDialog.setTitleText(getString(R.string.nota));
        pDialog.setContentText(getString(R.string.no_se_puede_eliminar_direccion_ultima));
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

    void volverPantallaPrincipal(String mensaje){

        KAlertDialog pDialog = new KAlertDialog(getActivity(), KAlertDialog.SUCCESS_TYPE);
        pDialog.setTitleText(getString(R.string.guardado));
        pDialog.setContentText(mensaje);
        pDialog.setConfirmText(getString(R.string.aceptar));
        pDialog.setContentTextSize(16);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.confirmButtonColor(R.drawable.dialogo_theme_success)
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();

                    // volver a pantalla principal
                    Intent intent = new Intent(getActivity(), PrincipalActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
        pDialog.show();
    }

    void mensajeSinConexion(){
        progressBar.setVisibility(View.GONE);
        Toasty.info(getActivity(), getString(R.string.sin_conexion)).show();
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
