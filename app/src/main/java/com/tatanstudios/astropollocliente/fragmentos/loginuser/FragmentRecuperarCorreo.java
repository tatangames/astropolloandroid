package com.tatanstudios.astropollocliente.fragmentos.loginuser;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.chaos.view.PinView;
import com.developer.kalert.KAlertDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentRecuperarCorreo extends Fragment {

    // SE COLOCA CORREO Y SE INGRESA EL CODIGO

    RelativeLayout root;

    Button btnEnviar;

    TextInputEditText edtCorreo;

    // CONTENEDORES
    LinearLayout layoutContenedor1;

    LinearLayout layoutContenedor2;


    PinView pinView;

    TextView txtReenviar;



    ProgressBar progressBar;
    ApiService service;
    TokenManager tokenManager;
    CompositeDisposable compositeDisposable = new CompositeDisposable();


    String correo = "";

    String codigoIngresado = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_solicitar_emailcode_login, container, false);

        root = vista.findViewById(R.id.root);
        txtReenviar = vista.findViewById(R.id.txtReenviarCorreo);
        pinView = vista.findViewById(R.id.pinView);
        layoutContenedor2 = vista.findViewById(R.id.layoutContenedor2);
        layoutContenedor1 = vista.findViewById(R.id.layoutContenedor1);
        edtCorreo = vista.findViewById(R.id.inputEditCorreo);
        btnEnviar = vista.findViewById(R.id.btnEnviar);



        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);
        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);
        progressBar.setVisibility(View.GONE);

        btnEnviar.setOnClickListener(view -> validarCorreo() );

        txtReenviar.setOnClickListener(view -> validarCorreo() );


        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // obtener numeros
                String code = pinView.getText().toString();

                // verificar hasta que tengamos 4 numeros
                if (code.length() == 4) {
                    codigoIngresado = code;
                    cerrarTeclado();
                    peticionVerificarCodigo();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return vista;
    }

    void peticionVerificarCodigo(){


        progressBar.setVisibility(View.VISIBLE);
        compositeDisposable.add(
                service.verificarCodigoResetPassword(codigoIngresado, correo)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuestas -> {
                                    progressBar.setVisibility(View.GONE);
                                    if(apiRespuestas != null){
                                        if (apiRespuestas.getSuccess() == 1){

                                           // PUEDE IR A CAMBIAR LA CONTRASENA
                                            String idcliente = apiRespuestas.getId();

                                            vistaCambiarContrasena(idcliente);

                                        }else if(apiRespuestas.getSuccess() == 2){

                                            Toasty.info(getActivity(), getString(R.string.codigo_incorrecto)).show();

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


    void validarCorreo(){

        correo = edtCorreo.getText().toString();

        if (TextUtils.isEmpty(correo)) {
            Toasty.info(getActivity(), getResources().getString(R.string.correo_electronico_es_requerido)).show();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            Toasty.info(getActivity(), getResources().getString(R.string.correo_es_invalido)).show();
            return;
        }

        verificar();
    }


    void vistaCambiarContrasena(String idCliente){

        FragmentCambiarContrasena fragmentCambiarContrasena = new FragmentCambiarContrasena();
        Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentContenedor);
        if(currentFragment.getClass().equals(fragmentCambiarContrasena.getClass())) return;

        Bundle bundle = new Bundle();
        bundle.putString("IDCLIENTE", idCliente);
        fragmentCambiarContrasena.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContenedor, fragmentCambiarContrasena)
                .addToBackStack(null)
                .commit();


    }



    void verificar(){


        KAlertDialog pDialog = new KAlertDialog(getActivity(), KAlertDialog.SUCCESS_TYPE);
        pDialog.setTitleText(getString(R.string.enviar_codigo));
        pDialog.setContentText("");
        pDialog.setConfirmText(getString(R.string.si));
        pDialog.setContentTextSize(16);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.confirmButtonColor(R.drawable.dialogo_theme_success)
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    peticionEnviarCodigo();
                });
        pDialog.cancelButtonColor(R.drawable.dialogo_theme_cancel)
                .setContentTextSize(16)
                .setCancelText(getString(R.string.cancelar))
                .setCancelClickListener(kAlertDialog -> kAlertDialog.dismissWithAnimation());
        pDialog.show();
    }

    void peticionEnviarCodigo() {

        cerrarTeclado();

        progressBar.setVisibility(View.VISIBLE);

        compositeDisposable.add(
                service.enviarCorreoRecuperarContrasena(correo)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(apiRespuestas -> {
                                    progressBar.setVisibility(View.GONE);
                                    if (apiRespuestas != null) {

                                        if (apiRespuestas.getSuccess() == 1) {
                                            // CODIGO ENVIADO
                                            layoutContenedor1.setVisibility(View.GONE);
                                            layoutContenedor2.setVisibility(View.VISIBLE);

                                            Toasty.info(getActivity(), getString(R.string.correo_enviado)).show();

                                        } else if (apiRespuestas.getSuccess() == 2) {

                                            // CORREO NO ENCONTRADO

                                            Toasty.info(getActivity(), getString(R.string.correo_no_registrado)).show();

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
