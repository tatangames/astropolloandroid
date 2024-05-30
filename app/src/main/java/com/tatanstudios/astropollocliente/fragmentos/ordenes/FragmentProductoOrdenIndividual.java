package com.tatanstudios.astropollocliente.fragmentos.ordenes;

import static android.content.Context.MODE_PRIVATE;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.modelos.ordenes.ModeloProductosOrdenesList;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentProductoOrdenIndividual extends Fragment {


    RelativeLayout root;
    ImageView imgProducto;
    TextView txtNombre;
    TextView txtDescripcion;
    TextView txtPrecio;
    TextView txtUnidades;
    TextView txtNotas;
    TextView txtTotal;

    ScrollView scroll;

    TextView txtToolbar;

    ApiService service;
    TokenManager tokenManager;
    ProgressBar progressBar;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    int idOrdenDescrip = 0;

    RequestOptions opcionesGlide = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.camaradefecto)
            .priority(Priority.NORMAL);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_producto_orden_individual, container, false);

        txtToolbar = vista.findViewById(R.id.txtToolbar);
        scroll = vista.findViewById(R.id.scroll);
        txtTotal = vista.findViewById(R.id.txtEstadoOrden);
        txtNotas = vista.findViewById(R.id.txtNotas);
        txtUnidades = vista.findViewById(R.id.txtUnidades);
        txtPrecio = vista.findViewById(R.id.txtPrecio);
        txtDescripcion = vista.findViewById(R.id.txtDescripcion);
        txtNombre = vista.findViewById(R.id.txtNombre);
        imgProducto = vista.findViewById(R.id.imgProducto);
        root = vista.findViewById(R.id.root);


        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);
        progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);

        txtToolbar.setText(getString(R.string.producto));

        Bundle bundle = getArguments();
        if (bundle != null) {
            idOrdenDescrip = bundle.getInt("KEY_ORDEN");
        }

        peticionServidor();

        return vista;
    }

    void peticionServidor(){
        progressBar.setVisibility(View.VISIBLE);

        compositeDisposable.add(
                service.verProductosOrdenesIndividual(idOrdenDescrip)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {
                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuesta != null) {
                                        if(apiRespuesta.getSuccess() == 1) {

                                            for (ModeloProductosOrdenesList m : apiRespuesta.getProductos()) {

                                                if(m.getUtilizaImagen() == 1){
                                                    if(m.getImagen() != null){
                                                        Glide.with(this)
                                                                .load(RetrofitBuilder.urlImagenes + m.getImagen())
                                                                .apply(opcionesGlide)
                                                                .into(imgProducto);

                                                        imgProducto.setVisibility(View.VISIBLE);
                                                    }
                                                }

                                                txtNombre.setText(m.getNombreProducto());

                                                if(m.getDescripcion() != null){

                                                    if(!TextUtils.isEmpty(m.getDescripcion())){
                                                        txtDescripcion.setText(m.getDescripcion());
                                                    }else{
                                                        txtDescripcion.setVisibility(View.GONE);
                                                    }

                                                }else{
                                                    txtDescripcion.setVisibility(View.GONE);
                                                }

                                                txtUnidades.setText(String.valueOf(m.getCantidad()));
                                                txtPrecio.setText(m.getPrecio());
                                                txtTotal.setText(m.getMultiplicado());

                                                if(m.getNota() != null){
                                                    txtNotas.setText(m.getNota());
                                                }

                                            }

                                            new Handler().postDelayed(() -> {
                                                scroll.setVisibility(View.VISIBLE);
                                                scroll.fullScroll(ScrollView.FOCUS_UP);
                                            }, 1);



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
        Toasty.info(getActivity(), getString(R.string.sin_conexion)).show();
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
