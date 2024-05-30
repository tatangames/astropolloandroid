package com.tatanstudios.astropollocliente.fragmentos.productos;

import static android.content.Context.MODE_PRIVATE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.adaptadores.productos.MenuVerticalAdapter;
import com.tatanstudios.astropollocliente.modelos.productos.ModeloMenuProductosList;
import com.tatanstudios.astropollocliente.modelos.productos.ModeloProductosArray;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentListadoProductos extends Fragment implements MenuVerticalAdapter.ClickListener{

    // MUESTRA LISTADO DE PRODUCTOS

    TextView txtToolbar;
    RelativeLayout root;
    RecyclerView recyclerProductos;

    ApiService service;
    TokenManager tokenManager;
    ProgressBar progressBar;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    int idCategoria = 0;


    private SectionedRecyclerViewAdapter sectionAdapter;
    ArrayList<ModeloMenuProductosList> modeloInfoProducto;
    GridLayoutManager layoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_productos_listado, container, false);

        txtToolbar = vista.findViewById(R.id.txtToolbar);
        root = vista.findViewById(R.id.root);
        recyclerProductos = vista.findViewById(R.id.recyclerProductos);


        txtToolbar.setText(getString(R.string.productos));

        sectionAdapter = new SectionedRecyclerViewAdapter();

        Bundle bundle = getArguments();
        if (bundle != null) {
            idCategoria = bundle.getInt("KEY_CATEGORIA", 0);
        }

        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);
        progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);

        layoutManager = new GridLayoutManager(getContext(), 1);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerProductos.setLayoutManager(layoutManager);
        recyclerProductos.setAdapter(sectionAdapter);

        peticionServidor();



        return vista;
    }


    @Override
    public void onItemRootViewClicked(@NonNull int idproducto) {

       FragmentElegirCantidadProducto fragmentInfoProducto = new FragmentElegirCantidadProducto();

        Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentContenedor);
        if(currentFragment.getClass().equals(fragmentInfoProducto.getClass())) return;

        Bundle bundle = new Bundle();
        bundle.putInt("KEY_PRODUCTO", idproducto);
        fragmentInfoProducto.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContenedor, fragmentInfoProducto)
                .addToBackStack(null)
                .commit();
    }


    void peticionServidor(){

        progressBar.setVisibility(View.VISIBLE);

        // esta peticion el usuario ya tiene una direccion registrada

        compositeDisposable.add(
                service.listadoDeProductosServicio(idCategoria)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuesta != null) {

                                        if(apiRespuesta.getSuccess() == 1){

                                            for (ModeloProductosArray pl : apiRespuesta.getProductos()) {
                                                modeloInfoProducto = new ArrayList<>();

                                                for (ModeloMenuProductosList p : pl.getProductos()) {

                                                    modeloInfoProducto.add(new ModeloMenuProductosList(p.getIdProducto(), p.getNombre(), p.getDescripcion(), p.getImagen(), p.getPrecio(), p.utiliza_imagen));
                                                }
                                                sectionAdapter.addSection(new MenuVerticalAdapter(pl.getNombre(), modeloInfoProducto, this, getContext()));
                                            }

                                            recyclerProductos.setLayoutManager(new LinearLayoutManager(getContext()));
                                            recyclerProductos.setAdapter(sectionAdapter);

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
        Toasty.info(getContext(), getString(R.string.sin_conexion)).show();
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
