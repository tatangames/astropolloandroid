package com.tatanstudios.astropollocliente.activitys.perfil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CambiarPasswordActivity extends AppCompatActivity {


    TextInputEditText edtPassword;
    Button btnGuardar;
    RelativeLayout root;


    ApiService service;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressBar progressBar;
    TokenManager tokenManager;

    String password = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_password);

        root = findViewById(R.id.root);
        btnGuardar = findViewById(R.id.btnGuardar);
        edtPassword = findViewById(R.id.inputEditPassword);


        tokenManager = TokenManager.getInstance(this.getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);


        btnGuardar.setOnClickListener(view -> verificarDatos() );
        progressBar.setVisibility(View.GONE);
    }


     void verificarDatos(){

         cerrarTeclado();

         password = edtPassword.getText().toString();

         if (TextUtils.isEmpty(password)) {
             Toasty.info(this, getResources().getString(R.string.password_requerido)).show();
             return;
         } else if (password.length() < 4) {
             Toasty.info(this, getResources().getString(R.string.minimo_4_caracter_para_contrasena)).show();
             return;
         }

         nuevaPassword();
     }


    void nuevaPassword() {

        cerrarTeclado();
        progressBar.setVisibility(View.VISIBLE);
        String id = tokenManager.getToken().getId();
        compositeDisposable.add(
                service.nuevaPasswordPerfil(id, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuestas -> {

                                    progressBar.setVisibility(View.GONE);
                                    if (apiRespuestas != null) {

                                        if (apiRespuestas.getSuccess() == 1) {
                                            Toasty.success(this, getString(R.string.contrasena_actualizada)).show();
                                            onBackPressed();
                                        } else {
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

    void mensajeSinConexion() {
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
    public void onPause(){
        compositeDisposable.clear();
        super.onPause();
    }


}