package com.tatanstudios.astropollocliente.fragmentos.loginuser;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.activitys.loginuser.LoginActivity;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentCambiarContrasena extends Fragment {

    RelativeLayout root;

    TextInputEditText edtContrasena;

    Button btnGuardar;


    ProgressBar progressBar;
    ApiService service;
    TokenManager tokenManager;
    CompositeDisposable compositeDisposable = new CompositeDisposable();


    String idCliente = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_cambiar_pass_login, container, false);

        root = vista.findViewById(R.id.root);
        btnGuardar = vista.findViewById(R.id.btnGuardar);
        edtContrasena = vista.findViewById(R.id.inputEditPassword);

        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);
        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);
        progressBar.setVisibility(View.GONE);


        Bundle bundle = getArguments();
        if(bundle != null) {
            idCliente = bundle.getString("IDCLIENTE");
        }


        btnGuardar.setOnClickListener(view -> validarPassword() );


        return vista;
    }


    void validarPassword(){

        cerrarTeclado();

        String password = edtContrasena.getText().toString();

        if (TextUtils.isEmpty(password)) {
            Toasty.info(getActivity(), getResources().getString(R.string.contrasena_es_requerido)).show();
            return;
        }

        if (password.length() < 4) {
            Toasty.info(getActivity(), getResources().getString(R.string.minimo_4_caracter_para_contrasena)).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        compositeDisposable.add(
                service.cambiarPasswordPerdida(idCliente, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuestas -> {
                                    progressBar.setVisibility(View.GONE);
                                    if(apiRespuestas != null){


                                        if (apiRespuestas.getSuccess() == 1){

                                            //CONTRASENA ACTUALIZADA


                                            volver();
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


    void volver(){

        Toasty.success(getActivity(), getResources().getString(R.string.contrasena_actualizada)).show();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
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
