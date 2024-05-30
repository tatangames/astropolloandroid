package com.tatanstudios.astropollocliente.activitys.modotesteo;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.developer.kalert.KAlertDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.activitys.carrito.CambiarCantidadCarritoActivity;
import com.tatanstudios.astropollocliente.activitys.principal.PrincipalActivity;
import com.tatanstudios.astropollocliente.activitys.procesar.ProcesarActivity;
import com.tatanstudios.astropollocliente.activitys.procesar.ProcesarModoTesteoActivity;
import com.tatanstudios.astropollocliente.adaptadores.carrito.CarritoComprasAdapter;
import com.tatanstudios.astropollocliente.adaptadores.modotesteo.CarritoComprasModoTesteoAdapter;
import com.tatanstudios.astropollocliente.modelos.carrito.ModeloCarrito;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CarritoProductoTesteoActivity extends AppCompatActivity {



    RecyclerView recyclerServicio;
    TextView txtToolbar;

    RelativeLayout root;


    ApiService service;
    TokenManager tokenManager;
    ProgressBar progressBar;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    BottomNavigationView bottomBar;
    TextView txtVerCarrito;
    ArrayList<ModeloCarrito> modeloInfoProducto = new ArrayList<>();
    CarritoComprasModoTesteoAdapter adapter;

    boolean carritoBool = false; // para saver si hay productos en el carrito



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito_producto_testeo);

        bottomBar = findViewById(R.id.bottomBar);
        recyclerServicio = findViewById(R.id.recyclerServicio);
        txtToolbar = findViewById(R.id.txtToolbar);
        root = findViewById(R.id.root);


        txtVerCarrito = bottomBar.findViewById(R.id.txtVerCarrito);
        txtToolbar.setText(getString(R.string.carrito));

        tokenManager = TokenManager.getInstance(this.getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);

        if(tokenManager.getToken().getStringPresenBorrarCarrito() == null){
            new KAlertDialog(this, KAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setContentText(getString(R.string.para_eliminar_un_producto_deslice))
                    .setConfirmText(getString(R.string.aceptar))
                    .setCustomImage(R.drawable.fingerswipe)
                    .confirmButtonColor(R.drawable.dialogo_theme_success)
                    .show();
            tokenManager.guardarPresentacionBorrarCarrito("1");
        }



        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerServicio.setLayoutManager(layoutManager);
        recyclerServicio.setHasFixedSize(true);
        adapter = new CarritoComprasModoTesteoAdapter();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerServicio.addItemDecoration(dividerItemDecoration);
        recyclerServicio.setAdapter(adapter);


        peticionServidor();



        // ir a procesar la orden
        bottomBar.setOnClickListener(v -> {

            if(!carritoBool){
                Toasty.info(this, getString(R.string.carrito_vacio)).show();
            }

            Intent i = new Intent(this, ProcesarModoTesteoActivity.class);
            startActivity(i);
        });

        // borrar un producto deslizando a los lados
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder target, int i) {

                int carritoid = adapter.modeloTipo.get(target.getBindingAdapterPosition()).getId();
                peticionBorrarProducto(carritoid);
            }
        }).attachToRecyclerView(recyclerServicio);
    }


    // borrar un producto
    void peticionBorrarProducto(int carritoid){
        progressBar.setVisibility(View.VISIBLE);
        String id = tokenManager.getToken().getId();

        compositeDisposable.add(
                service.borrarProductoCarritoModoTesteo(id, carritoid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuesta != null) {

                                        if(apiRespuesta.getSuccess() == 1){

                                            modeloInfoProducto.clear();
                                            Toasty.info(this, getString(R.string.producto_eliminado)).show();
                                            peticionServidor();

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




    // obtener el carrito de compras
    void peticionServidor(){

        progressBar.setVisibility(View.VISIBLE);
        String id = tokenManager.getToken().getId();
        compositeDisposable.add(
                service.verCarritoComprasModoTesteo(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuesta != null) {

                                        if(apiRespuesta.getSuccess() == 1){

                                            txtVerCarrito.setText(getString(R.string.orden_procesar, apiRespuesta.getSubtotal()));
                                            bottomBar.setVisibility(View.VISIBLE); // boton procesar visible

                                            if(apiRespuesta.getConteo() > 0){
                                                carritoBool = true;
                                            }else{
                                                carritoBool = false;
                                            }


                                            adapter = new CarritoComprasModoTesteoAdapter(this, apiRespuesta.getProducto(), this);
                                            recyclerServicio.setAdapter(adapter);

                                        }
                                        else if(apiRespuesta.getSuccess() == 2) {
                                            carritoBool = false;
                                            Toasty.info(this, getString(R.string.carrito_vacio)).show();
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