package com.tatanstudios.astropollocliente.activitys.carrito;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.developer.kalert.KAlertDialog;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.modelos.carrito.ModeloCarritoProductoEditarList;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CambiarCantidadCarritoActivity extends AppCompatActivity {



    ImageView imgNumeroMenos;

    ImageView imgNumeroMas;

    TextView txtNumeroCantidad;

    int contadorProducto = 1;
    int limiteProducto = 50;



    ImageView imgProducto;

    TextView txtNombre;

    TextView txtDescripcion;

    TextView txtPrecio;

    EditText edtNota;

    TextView txtToolbar;

    Button btnAgregar;

    RelativeLayout root;

    ScrollView scroll;

    TextView txtDinero;

    RequestOptions opcionesGlide = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .priority(Priority.NORMAL);


    Float precioUnidad = 0.0f; // precio de unidad del producto
    int carritoid = 0; //id del carrito

    int utiliza_nota = 0;
    String notaproducto = "";
    boolean seguro = true;

    ApiService service;
    TokenManager tokenManager;
    ProgressBar progressBar;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    DecimalFormat df = new DecimalFormat("#####0.00");
    DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_cantidad_carrito);



        imgNumeroMenos = findViewById(R.id.imgNumeroMenos);
        imgNumeroMas = findViewById(R.id.imgNumeroMas);
        txtNumeroCantidad = findViewById(R.id.txtNumeroCantidad);
        imgProducto = findViewById(R.id.imgProducto);
        txtNombre = findViewById(R.id.txtNombre);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtPrecio = findViewById(R.id.txtPrecio);
        edtNota = findViewById(R.id.edtNota);
        txtToolbar = findViewById(R.id.txtToolbar);
        btnAgregar = findViewById(R.id.btnAgregar);
        root = findViewById(R.id.root);
        scroll = findViewById(R.id.scroll);
        txtDinero = findViewById(R.id.txtDinero);




        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            carritoid = bundle.getInt("KEY_CARRITOID");
        }

        txtToolbar.setText(getString(R.string.elegir_cantidad));

        tokenManager = TokenManager.getInstance(this.getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);


        txtNumeroCantidad.setText("1");

        imgNumeroMenos.setOnClickListener(view -> {
            restarCantidad();
        });

        imgNumeroMas.setOnClickListener(view -> {
            sumarCantidad();
        });


        peticionServidor();



        btnAgregar.setOnClickListener(v -> {
            peticionCambiarCantidad();
        });
    }


    void sumarCantidad(){

        if(contadorProducto >= limiteProducto){
            // no hacer nada
        }else{
            contadorProducto ++;
            txtNumeroCantidad.setText(String.valueOf(contadorProducto));

            float precioFinal = precioUnidad * contadorProducto;
            txtDinero.setText("$"+df.format(precioFinal));
        }
    }

    void restarCantidad(){
        if(contadorProducto <= 1){
            // no hacer nada
        }else{
            contadorProducto --;
            txtNumeroCantidad.setText(String.valueOf(contadorProducto));

            float precioFinal = precioUnidad * contadorProducto;
            txtDinero.setText("$"+df.format(precioFinal));
        }
    }






    void peticionServidor(){
        progressBar.setVisibility(View.VISIBLE);
        String userid = tokenManager.getToken().getId();

        compositeDisposable.add(
                service.infoProductoIndividualCarrito(userid, carritoid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuesta != null){

                                        if(apiRespuesta.getSuccess() == 1){
                                            // informacion del producto
                                            ModeloCarritoProductoEditarList p = apiRespuesta.getProducto();
                                            setearUI(p);

                                        }else if(apiRespuesta.getSuccess() == 2){ // producto no encontrado

                                            Toasty.info(this, getString(R.string.producto_no_encontrado)).show();

                                        }else if(apiRespuesta.getSuccess() == 3){ // carrito de compras vacio

                                            Toasty.info(this, getString(R.string.carrito_vacio)).show();
                                        }
                                    }
                                    else{
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

    // modificar vista
    void setearUI(ModeloCarritoProductoEditarList p){
        if(p.getUtiliza_imagen() == 1){
            if(p.getImagen() != null){
                imgProducto.setVisibility(View.VISIBLE);

                Glide.with(this)
                        .load(RetrofitBuilder.urlImagenes + p.getImagen())
                        .apply(opcionesGlide)
                        .into(imgProducto);
            }
        }

        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);

        txtNombre.setText(p.getNombre());

        if(p.getDescripcion() != null){
            if(!TextUtils.isEmpty(p.getDescripcion())){
                txtDescripcion.setText(p.getDescripcion());
            }else{
                txtDescripcion.setVisibility(View.GONE);
            }
        }else{
            txtDescripcion.setVisibility(View.GONE);
        }

        contadorProducto = p.getCantidad(); // cantidad que tengo agregada

        utiliza_nota = p.getUtiliza_nota();

        if(p.getNota() != null){
            notaproducto = p.getNota();
        }

        txtPrecio.setText("Precio: $"+p.getPrecio()); // precio del producto
        txtNumeroCantidad.setText(p.getCantidad().toString());


        // multiplicar
        precioUnidad = Float.valueOf(p.getPrecio());
        float precioFinal = precioUnidad * p.getCantidad();
        txtDinero.setText("$" + df.format(precioFinal));
        edtNota.setText(p.getNota_producto());

        new Handler().postDelayed(() -> {
            scroll.setVisibility(View.VISIBLE);
            scroll.fullScroll(ScrollView.FOCUS_UP);
        }, 1);
    }

    // cambiar la cantidad de producto y su nota


    void peticionCambiarCantidad(){
        String nota = edtNota.getText().toString();

        if(utiliza_nota == 1){
            if(TextUtils.isEmpty(nota)){

                KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.CUSTOM_IMAGE_TYPE);
                pDialog.setTitleText("Nota requerida");
                pDialog.setContentText(notaproducto);
                pDialog.setCustomImage(R.drawable.icono_lista);
                pDialog.setConfirmText("Aceptar");
                pDialog.setContentTextSize(16);
                pDialog.setCancelable(false);
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.confirmButtonColor(R.drawable.dialogo_theme_success)
                        .setConfirmClickListener(sDialog -> {
                            sDialog.dismissWithAnimation();

                        });
                pDialog.show();

                return;
            }
        }

        if(seguro){
            seguro = false;
            progressBar.setVisibility(View.VISIBLE);
            String userid = tokenManager.getToken().getId();
            compositeDisposable.add(
                    service.cambiarCantidadProducto(userid, contadorProducto, carritoid, nota)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .retry()
                            .subscribe(apiRespuesta -> {

                                        progressBar.setVisibility(View.GONE);
                                        seguro = true;

                                        if(apiRespuesta != null){
                                            if(apiRespuesta.getSuccess() == 1){
                                                Toasty.success(this, getString(R.string.actualizado)).show();
                                                regresar();
                                            }else if(apiRespuesta.getSuccess() == 2){
                                                Toasty.info(this, getString(R.string.producto_no_encontrado)).show();
                                                regresar();
                                            }
                                            else{
                                                mensajeSinConexion();
                                            }
                                        }
                                        else{
                                            mensajeSinConexion();
                                        }
                                    },
                                    throwable -> {
                                        mensajeSinConexion();
                                    })
            );
        }
    }






    // regresar atras
    void regresar(){
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
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