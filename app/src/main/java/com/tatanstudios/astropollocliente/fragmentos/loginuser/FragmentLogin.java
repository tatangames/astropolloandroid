package com.tatanstudios.astropollocliente.fragmentos.loginuser;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.developer.kalert.KAlertDialog;
import com.google.android.material.textfield.TextInputEditText;
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

public class FragmentLogin extends Fragment {


    TextInputEditText edtUsuario;
    TextInputEditText edtPassword;

    TextView txtRegistro;

    Button btnAcceder;

    RelativeLayout root;

    ImageView logo;

    TextView txtPassOlvidada;

    String usuario = "";
    String password = "";

    ApiService service;
    ProgressBar progressBar;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    TokenManager tokenManager;



    String idOneSignal = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_loginuser, container, false);


        txtPassOlvidada = vista.findViewById(R.id.txtPassOlvidada);
        logo = vista.findViewById(R.id.logoapp);
        root = vista.findViewById(R.id.root);
        btnAcceder = vista.findViewById(R.id.btnIniciarSesion);
        txtRegistro = vista.findViewById(R.id.txtRegistro);
        edtPassword = vista.findViewById(R.id.inputEditPassword);
        edtUsuario = vista.findViewById(R.id.inputEditUsuario);


        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);
        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);

        progressBar.setVisibility(View.GONE);


        OSDeviceState device = OneSignal.getDeviceState();

        if(device != null){
            idOneSignal = device.getUserId();
        }


        logo.setAdjustViewBounds(true);

        progressBar.setVisibility(View.GONE);

        txtRegistro.setOnClickListener(v -> verRegistro());
        btnAcceder.setOnClickListener(v ->verificarDatos());
        txtPassOlvidada.setOnClickListener(v ->verCambioPassword());


        return vista;
    }

    void verRegistro(){
        FragmentRegistro fragmentRegistro = new FragmentRegistro();
        Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentContenedor);
        if(currentFragment.getClass().equals(fragmentRegistro.getClass())) return;

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContenedor, fragmentRegistro)
                .addToBackStack(null)
                .commit();
    }


    void verCambioPassword(){
        FragmentRecuperarCorreo fragmentRecuperarCorreo = new FragmentRecuperarCorreo();
        Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentContenedor);
        if(currentFragment.getClass().equals(fragmentRecuperarCorreo.getClass())) return;

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContenedor, fragmentRecuperarCorreo)
                .addToBackStack(null)
                .commit();
    }


    void verificarDatos(){

        usuario = edtUsuario.getText().toString();
        password = edtPassword.getText().toString();
        cerrarTeclado();
        if(TextUtils.isEmpty(usuario)){
            Toasty.info(getActivity(), getResources().getString(R.string.usuario_requerido)).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toasty.info(getActivity(), getResources().getString(R.string.password_requerido)).show();
            return;
        }


        peticionServidor();
    }


    void peticionServidor(){
        progressBar.setVisibility(View.VISIBLE);
        compositeDisposable.add(
                service.inicioSesion(usuario, password, idOneSignal)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuestas -> {
                                    progressBar.setVisibility(View.GONE);
                                    if (apiRespuestas != null) {

                                        if (apiRespuestas.getSuccess() == 1) {
                                            // USUARIO BLOQUEADO
                                            alertaTexto(apiRespuestas.getTitulo(), apiRespuestas.getMensaje());
                                        }
                                        else if (apiRespuestas.getSuccess() == 2) {
                                            // INICIO DE SESION
                                            tokenManager.guardarClienteID(apiRespuestas);
                                            siguienteActivity();
                                        } else if (apiRespuestas.getSuccess() == 3) {
                                            // PASSWORD INCORRECTA
                                            Toasty.info(getActivity(), getResources().getString(R.string.datos_incorrectos)).show();
                                        }
                                        else if (apiRespuestas.getSuccess() == 4) {
                                            // USUARIO NO ENCONTRADO
                                            Toasty.info(getActivity(), getResources().getString(R.string.datos_incorrectos)).show();
                                        }
                                        else{
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

    void alertaTexto(String titulo, String mensaje){

        KAlertDialog pDialog = new KAlertDialog(getActivity(), KAlertDialog.WARNING_TYPE);
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

    // pasar a pantalla servicios
    void siguienteActivity(){
        Intent intent = new Intent(getActivity(), PrincipalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    void mensajeSinConexion(){
        progressBar.setVisibility(View.GONE);
        Toasty.info(getActivity(), getResources().getString(R.string.sin_conexion)).show();
    }

    // cierra el teclado
    void cerrarTeclado() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
