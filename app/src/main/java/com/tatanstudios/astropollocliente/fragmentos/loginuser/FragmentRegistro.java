package com.tatanstudios.astropollocliente.fragmentos.loginuser;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.developer.kalert.KAlertDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.activitys.direccion.ListaDireccionesActivity;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentRegistro extends Fragment {


    String idFCM = "";

    RelativeLayout root;

    ProgressBar progressBar;
    ApiService service;
    TokenManager tokenManager;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    Button btnRegistro;
    TextInputEditText edtUsuario;
    TextInputEditText edtCorreo;
    TextInputEditText edtPassword;

    String usuario = "";
    String password = "";
    String correo = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_registro, container, false);

        root = vista.findViewById(R.id.root);
        edtPassword = vista.findViewById(R.id.inputEditPassword);
        edtCorreo = vista.findViewById(R.id.inputEditCorreo);
        edtUsuario = vista.findViewById(R.id.inputEditUsuario);
        btnRegistro = vista.findViewById(R.id.btnRegistro);


        OSDeviceState device = OneSignal.getDeviceState();

        if(device != null){
            idFCM = device.getUserId();
        }

        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);
        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);
        progressBar.setVisibility(View.GONE);

        btnRegistro.setOnClickListener(view -> validarRegistro() );

        return vista;
    }

    void validarRegistro(){

        usuario = edtUsuario.getText().toString();
        password = edtPassword.getText().toString();
        correo = edtCorreo.getText().toString();

        boolean a = validacion(usuario, password, correo);

        if (a){
            cerrarTeclado();
            completarRegistro();
        }
    }

    boolean validacion(String nombre, String password, String correo) {

        if (TextUtils.isEmpty(nombre)) {
            Toasty.info(getActivity(), getResources().getString(R.string.usuario_es_requerido)).show();
            return false;
        }

        if (!TextUtils.isEmpty(correo)) {
            if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
                Toasty.info(getActivity(), getResources().getString(R.string.correo_es_invalido)).show();
                return false;
            }
        }

        if (TextUtils.isEmpty(password)) {
            Toasty.info(getActivity(), getResources().getString(R.string.contrasena_es_requerido)).show();
            return false;
        }

        if (password.length() < 4) {
            Toasty.info(getActivity(), getResources().getString(R.string.minimo_4_caracter_para_contrasena)).show();
            return false;
        }

        return true;
    }


    void completarRegistro(){

            KAlertDialog pDialog = new KAlertDialog(getActivity(), KAlertDialog.SUCCESS_TYPE);
            pDialog.setTitleText(getString(R.string.registrarse));
            pDialog.setContentText(getString(R.string.completar_registro));
            pDialog.setConfirmText(getString(R.string.si));
            pDialog.setContentTextSize(16);
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.confirmButtonColor(R.drawable.dialogo_theme_success)
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        peticionServidor();
                    });
            pDialog.cancelButtonColor(R.drawable.dialogo_theme_cancel)
                    .setContentTextSize(16)
                    .setCancelText(getString(R.string.cancelar))
                    .setCancelClickListener(kAlertDialog -> kAlertDialog.dismissWithAnimation());
            pDialog.show();
    }


    void peticionServidor(){
        progressBar.setVisibility(View.VISIBLE);

        String version = getString(R.string.app_registro);

        compositeDisposable.add(
                service.registroUsuario(usuario, password, correo, idFCM, version)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuestas -> {
                                    progressBar.setVisibility(View.GONE);
                                    if(apiRespuestas != null) {

                                        if (apiRespuestas.getSuccess() == 1){

                                            String titulo = apiRespuestas.getTitulo();
                                            String mensaje = apiRespuestas.getMensaje();
                                            // usuario registrado
                                            alertaTexto(titulo, mensaje);
                                        }
                                        else if (apiRespuestas.getSuccess() == 2){
                                            // correo registrado
                                            String titulo = apiRespuestas.getTitulo();
                                            String mensaje = apiRespuestas.getMensaje();
                                            alertaTexto(titulo, mensaje);
                                        }
                                        else if(apiRespuestas.getSuccess() == 3){
                                            // registrado
                                            tokenManager.guardarClienteID(apiRespuestas);
                                            siguienteActivity();
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

    void siguienteActivity(){

        Intent intent = new Intent(getActivity(), ListaDireccionesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
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







    void mensajeSinConexion(){
        progressBar.setVisibility(View.GONE);
        Toasty.info(getActivity(), getResources().getString(R.string.sin_conexion)).show();
    }

    // cerrar teclado
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
