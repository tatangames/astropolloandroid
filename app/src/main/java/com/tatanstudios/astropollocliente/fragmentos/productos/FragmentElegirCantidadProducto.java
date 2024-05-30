package com.tatanstudios.astropollocliente.fragmentos.productos;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.developer.kalert.KAlertDialog;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.modelos.productoindividual.ModeloInformacionProductoList;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentElegirCantidadProducto extends Fragment {

    int idproducto = 0;

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

    ScrollView scrollView;

    TextView txtDinero;

    float precio = 0;

    String notaProducto = "";

    int utilizaNota = 0;
    String usaNota = "";

    RequestOptions opcionesGlide = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.camaradefecto)
            .priority(Priority.NORMAL);

    ApiService service;
    TokenManager tokenManager;
    ProgressBar progressBar;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    // utilizado para multiplicar cantidad, y setear texto en el boton "agregar"
    DecimalFormat df = new DecimalFormat("#####0.00");
    DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_elegir_cantidad_producto, container, false);


        txtDinero = vista.findViewById(R.id.txtDinero);
        scrollView = vista.findViewById(R.id.scroll);
        root = vista.findViewById(R.id.root);
        btnAgregar = vista.findViewById(R.id.btnAgregar);
        txtToolbar = vista.findViewById(R.id.txtToolbar);
        edtNota = vista.findViewById(R.id.edtNota);
        txtPrecio = vista.findViewById(R.id.txtPrecio);
        txtDescripcion = vista.findViewById(R.id.txtDescripcion);
        txtNombre = vista.findViewById(R.id.txtNombre);
        imgProducto = vista.findViewById(R.id.imgProducto);
        txtNumeroCantidad = vista.findViewById(R.id.txtNumeroCantidad);
        imgNumeroMas = vista.findViewById(R.id.imgNumeroMas);
        imgNumeroMenos = vista.findViewById(R.id.imgNumeroMenos);


        Bundle bundle = getArguments();
        if(bundle != null) {
            idproducto = bundle.getInt("KEY_PRODUCTO", 0);
        }

        txtNumeroCantidad.setText("1");

        imgNumeroMenos.setOnClickListener(view -> {
            restarCantidad();
        });

        imgNumeroMas.setOnClickListener(view -> {
            sumarCantidad();
        });

        txtToolbar.setText(getString(R.string.elegir_cantidad));

        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);
        progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);

        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);

        btnAgregar.setOnClickListener(v -> {
            agregarProducto();
        });


        peticionServidor();

        return vista;
    }


    void sumarCantidad(){

        if(contadorProducto >= limiteProducto){
            // no hacer nada
        }else{
            contadorProducto ++;
            txtNumeroCantidad.setText(String.valueOf(contadorProducto));

            float precioFinal = precio * contadorProducto;
            txtDinero.setText("$"+df.format(precioFinal));
        }
    }

    void restarCantidad(){
        if(contadorProducto <= 1){
            // no hacer nada
        }else{
            contadorProducto --;
            txtNumeroCantidad.setText(String.valueOf(contadorProducto));

            float precioFinal = precio * contadorProducto;
            txtDinero.setText("$"+df.format(precioFinal));
        }
    }

    void peticionServidor(){

        progressBar.setVisibility(View.VISIBLE);
        compositeDisposable.add(
                service.infoProductoIndividual(idproducto)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    progressBar.setVisibility(View.GONE);
                                    if(apiRespuesta != null) {
                                        if(apiRespuesta.getSuccess() == 1) {
                                            for (ModeloInformacionProductoList pi : apiRespuesta.getProducto()) {

                                                if(pi.getUtiliza_imagen() == 1){
                                                    if(pi.getImagen() != null){

                                                        Glide.with(this)
                                                                .load(RetrofitBuilder.urlImagenes + pi.getImagen())
                                                                .apply(opcionesGlide)
                                                                .into(imgProducto);
                                                        imgProducto.setVisibility(View.VISIBLE);

                                                    }
                                                }

                                                txtNombre.setText(pi.getNombre());

                                                if(pi.getDescripcion() != null){
                                                    if(!TextUtils.isEmpty(pi.getDescripcion())){
                                                        txtDescripcion.setText(pi.getDescripcion());
                                                    }else{
                                                        txtDescripcion.setVisibility(View.GONE);
                                                    }
                                                }else{
                                                    txtDescripcion.setVisibility(View.GONE);
                                                }

                                                txtPrecio.setText("Precio: $"+ pi.getPrecio());

                                                precio = Float.valueOf(pi.getPrecio());

                                                txtDinero.setText("$"+df.format(precio));
                                                btnAgregar.setText(getString(R.string.agregar_a_la_orden));

                                                new Handler().postDelayed(() -> {
                                                    scrollView.setVisibility(View.VISIBLE);
                                                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                                                }, 1);

                                                utilizaNota = pi.getUtiliza_nota();

                                                if(pi.getNota() != null){
                                                    usaNota = pi.getNota();
                                                }

                                            }

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


    // agregar producto al carrito de compras
    void agregarProducto(){

        cerrarTeclado();

        notaProducto = edtNota.getText().toString();

        if(notaProducto.length() > 400){
            Toasty.info(getActivity(), "400 caracteres máximo para nota").show();
            return;
        }

        if(utilizaNota== 1){
            if(TextUtils.isEmpty(notaProducto)) {


   KAlertDialog pDialog = new KAlertDialog(getActivity(), KAlertDialog.CUSTOM_IMAGE_TYPE);
                pDialog.setTitleText("Nota requerida");
                pDialog.setContentText(usaNota);
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

        progressBar.setVisibility(View.VISIBLE);
        String id = tokenManager.getToken().getId();
        compositeDisposable.add(
                service.agregarCarritoTemporal(id, idproducto, contadorProducto, notaProducto)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    progressBar.setVisibility(View.GONE);
                                    if(apiRespuesta != null) {


                                        // REGLA 1: cliente no tiene direccion
                                        // REGLA 2: producto debe estar activo
                                        // REGLA 3: sub categoria del producto debe estar activo
                                        // REGLA 4: la categoria del producto debe estar activo
                                        // REGLA 5: la categoria si utiliza horario debe estar disponible


                                        if(apiRespuesta.getSuccess() == 1){
                                            alertaMensaje(apiRespuesta.getTitulo(), apiRespuesta.getMensaje());

                                        }else if(apiRespuesta.getSuccess() == 2){
                                            alertaMensaje(apiRespuesta.getTitulo(), apiRespuesta.getMensaje());

                                        }else if(apiRespuesta.getSuccess() == 3){
                                            alertaMensaje(apiRespuesta.getTitulo(), apiRespuesta.getMensaje());

                                        }else if(apiRespuesta.getSuccess() == 4){
                                            alertaMensaje(apiRespuesta.getTitulo(), apiRespuesta.getMensaje());
                                        }
                                        else if(apiRespuesta.getSuccess() == 5){
                                            //servicio no activo globalmente
                                            alertaMensaje(apiRespuesta.getTitulo(), apiRespuesta.getMensaje());
                                        }
                                        else if(apiRespuesta.getSuccess() == 6){
                                            // producto guardado
                                            productoGuardado();
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


    void alertaMensaje(String titulo, String nota){
        KAlertDialog pDialog = new KAlertDialog(getActivity(), KAlertDialog.WARNING_TYPE);
        pDialog.setTitleText(titulo);
        pDialog.setContentText(nota);
        pDialog.setConfirmText("Aceptar");
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
        Toasty.info(getActivity(), getString(R.string.sin_conexion)).show();
    }

    void productoGuardado(){
        Toasty.success(getActivity(), "Agregado al Carrito").show();
        getActivity().onBackPressed();
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

    // cierra el teclado
    void cerrarTeclado() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
