package com.tatanstudios.astropollocliente.activitys.carrito;

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
import com.tatanstudios.astropollocliente.activitys.principal.PrincipalActivity;
import com.tatanstudios.astropollocliente.activitys.procesar.ProcesarActivity;
import com.tatanstudios.astropollocliente.adaptadores.carrito.CarritoComprasAdapter;
import com.tatanstudios.astropollocliente.modelos.carrito.ModeloCarrito;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CarritoActivity extends AppCompatActivity {



    RecyclerView recyclerServicio;
    TextView txtToolbar;
    ImageView imgBorrar;
    RelativeLayout root;



    ApiService service;
    TokenManager tokenManager;
    ProgressBar progressBar;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    BottomNavigationView bottomBar;
    TextView txtVerCarrito;
    ArrayList<ModeloCarrito> modeloInfoProducto = new ArrayList<>();
    CarritoComprasAdapter adapter;

    boolean carritoBool = false; // para saver si hay productos en el carrito


    // SI ES 0: UNO O VARIOS PRODUCTOS NO ESTAN DISPONIBLES
    int estadoProductoGlobal = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        bottomBar = findViewById(R.id.bottomBar);
        recyclerServicio = findViewById(R.id.recyclerServicio);
        txtToolbar = findViewById(R.id.txtToolbar);
        imgBorrar = findViewById(R.id.imgBorrar);
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

        peticionServidor();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerServicio.setLayoutManager(layoutManager);
        recyclerServicio.setHasFixedSize(true);
        adapter = new CarritoComprasAdapter();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerServicio.addItemDecoration(dividerItemDecoration);
        recyclerServicio.setAdapter(adapter);

        imgBorrar.setOnClickListener(v -> {
            if(carritoBool) {
                mensajeBorrar();
            }
        });

        // ir a procesar la orden
        bottomBar.setOnClickListener(v -> {

            if(!carritoBool){
                Toasty.info(this, getString(R.string.carrito_de_compras_no_encontrado)).show();
                salir();
            }

            if(estadoProductoGlobal == 0){
                new KAlertDialog(this, KAlertDialog.WARNING_TYPE)
                        .setContentTextSize(20)
                        .setTitleText(getString(R.string.producto_no_disponible))
                        .setContentText(getString(R.string.el_producto_marcado_no))
                        .setConfirmText(getString(R.string.aceptar))
                        .setContentTextSize(16)
                        .confirmButtonColor(R.drawable.dialogo_theme_success)
                        .setConfirmClickListener(sDialog -> {
                            sDialog.dismissWithAnimation();
                        })
                        .show();
                return;
            }


            Intent i = new Intent(this, ProcesarActivity.class);
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

                int carritoid = adapter.modeloTipo.get(target.getBindingAdapterPosition()).getCarritoid();
                peticionBorrarProducto(carritoid);
            }
        }).attachToRecyclerView(recyclerServicio);
    }



    public void editarProducto(int carritoid, int estado, String titulo, String mensaje){

        // REGLAS::
        // 1- PRODUCTO NO ACTIVO
        // 2- SUB CATEGORTIA NO ACTIVA
        // 3- CATEGORIA NO ACTIVA
        // 4- CATEGORIA HORARIO NO ACTIVA


        // PRODUCTOS NO DISPONIBLE
        if(estado == 1){
            new KAlertDialog(this, KAlertDialog.WARNING_TYPE)
                    .setContentTextSize(20)
                    .setTitleText(titulo)
                    .setContentText(mensaje)
                    .setConfirmText(getString(R.string.aceptar))
                    .setContentTextSize(16)
                    .confirmButtonColor(R.drawable.dialogo_theme_success)
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                    })
                    .show();
            return;
        }

        Intent i = new Intent(this, CambiarCantidadCarritoActivity.class);
        i.putExtra("KEY_CARRITOID", carritoid);
        someActivityResultLauncher.launch(i);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    modeloInfoProducto.clear();
                    peticionServidor();
                }
            });

    // borrar un producto
    void peticionBorrarProducto(int carritoid){
        progressBar.setVisibility(View.VISIBLE);
        String id = tokenManager.getToken().getId();

        compositeDisposable.add(
                service.borrarProductoCarrito(id, carritoid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuesta != null) {

                                        if(apiRespuesta.getSuccess() == 1){

                                            // carrito de compras borrado completamente
                                            Toasty.info(this, getString(R.string.carrito_vacio)).show();

                                            salir();

                                        }else if(apiRespuesta.getSuccess() == 2) { // producto eliminado
                                            modeloInfoProducto.clear();
                                            Toasty.info(this, getString(R.string.producto_eliminado)).show();
                                            peticionServidor();

                                        }else if(apiRespuesta.getSuccess() == 3){ // producto no encontrado
                                            modeloInfoProducto.clear();
                                            Toasty.info(this, getString(R.string.producto_eliminado)).show();
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

    void salir(){
        Intent i = new Intent(this, PrincipalActivity.class);
        startActivity(i);
        finish();
    }

    // obtener el carrito de compras
    void peticionServidor(){

        progressBar.setVisibility(View.VISIBLE);
        String id = tokenManager.getToken().getId();
        compositeDisposable.add(
                service.verCarritoCompras(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuesta != null) {

                                        if(apiRespuesta.getSuccess() == 1){

                                            txtVerCarrito.setText(getString(R.string.orden_procesar, apiRespuesta.getSubtotal()));
                                            bottomBar.setVisibility(View.VISIBLE); // boton procesar visible
                                            carritoBool = true; // si hay productos para borrar

                                            //0: PRODUCTOS NO DISPONIBLE
                                            estadoProductoGlobal = apiRespuesta.getEstadoProductoGlobal();

                                            adapter = new CarritoComprasAdapter(this, apiRespuesta.getProducto(), this);
                                            recyclerServicio.setAdapter(adapter);

                                        }else if(apiRespuesta.getSuccess() == 2){
                                            // carrito vacio
                                            Toasty.info(this, getString(R.string.carrito_vacio)).show();
                                            bottomBar.setVisibility(View.INVISIBLE);
                                            carritoBool = false;
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

    // mensaje para borrar carrito
    void mensajeBorrar(){
        KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE);
        pDialog.setTitleText(getString(R.string.borrar_carrito));
        pDialog.setContentText(getString(R.string.se_eliminara));
        pDialog.setConfirmText(getString(R.string.si));
        pDialog.setContentTextSize(16);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.confirmButtonColor(R.drawable.dialogo_theme_success)
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    peticionBorrarCarrito();
                });
        pDialog.cancelButtonColor(R.drawable.dialogo_theme_cancel);
        pDialog.setContentTextSize(16);
        pDialog.setCancelText(getString(R.string.cancelar));
        pDialog.setCancelClickListener(kAlertDialog -> kAlertDialog.dismissWithAnimation());
        pDialog.show();
    }

    // borrar el carrito
    void peticionBorrarCarrito(){
        progressBar.setVisibility(View.VISIBLE);
        String id = tokenManager.getToken().getId();
        compositeDisposable.add(
                service.borrarCarritoCompras(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    progressBar.setVisibility(View.GONE);
                                    if(apiRespuesta != null) {

                                        if(apiRespuesta.getSuccess() == 1){ // carrito borrado
                                            Toasty.info(this, getString(R.string.carrito_borrado)).show();
                                            salir();
                                        }else if(apiRespuesta.getSuccess() == 2) { // carrito de compras no encontrado
                                            Toasty.info(this, getString(R.string.carrito_borrado)).show();
                                            salir();
                                        }else{
                                            mensajeSinConexion();
                                        }
                                    }else {
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