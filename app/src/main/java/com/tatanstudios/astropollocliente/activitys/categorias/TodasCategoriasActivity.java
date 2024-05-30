package com.tatanstudios.astropollocliente.activitys.categorias;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.activitys.productos.ProductosListadoActivity;
import com.tatanstudios.astropollocliente.adaptadores.categorias.CategoriasTodasAdapter;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TodasCategoriasActivity extends AppCompatActivity {

    // MUESTRA LISTA DE CATEGORIAS


    TextView txtToolbar;
    RelativeLayout root;

    RecyclerView recyclerCategorias;


    ApiService service;
    TokenManager tokenManager;
    ProgressBar progressBar;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    CategoriasTodasAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todas_categorias);

        txtToolbar = findViewById(R.id.txtToolbar);
        root = findViewById(R.id.root);
        recyclerCategorias = findViewById(R.id.recyclerCategorias);


        txtToolbar.setText(getString(R.string.categorias));

        tokenManager = TokenManager.getInstance(this.getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerCategorias.setLayoutManager(layoutManager);
        recyclerCategorias.setHasFixedSize(true);
        adapter = new CategoriasTodasAdapter();


        peticionServidor();
    }


    void peticionServidor(){

        progressBar.setVisibility(View.VISIBLE);

        // esta peticion el usuario ya tiene una direccion registrada
        String id = tokenManager.getToken().getId();
        compositeDisposable.add(
                service.listaTodasLasCategorias(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuesta != null) {

                                        if(apiRespuesta.getSuccess() == 1){

                                            adapter = new CategoriasTodasAdapter(this, apiRespuesta.getCategorias(), this);
                                            recyclerCategorias.setAdapter(adapter);

                                        }else if(apiRespuesta.getSuccess() == 2){

                                            // el usuario no tiene direccion registrada, pero aqui no deberia llegar nunca
                                            Toasty.info(this, apiRespuesta.getMensaje()).show();

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


    void mensajeSinConexion(){
        progressBar.setVisibility(View.GONE);
        Toasty.info(this, getString(R.string.sin_conexion)).show();
    }


    public void verListaDeProductos(int id){
        Intent i = new Intent(this, ProductosListadoActivity.class);
        i.putExtra("KEY_CATEGORIA", id);
        startActivity(i);
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