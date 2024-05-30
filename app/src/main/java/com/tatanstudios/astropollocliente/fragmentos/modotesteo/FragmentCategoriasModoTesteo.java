package com.tatanstudios.astropollocliente.fragmentos.modotesteo;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.activitys.modotesteo.ElegirProductoModoTesteoActivity;
import com.tatanstudios.astropollocliente.activitys.principal.PrincipalActivity;
import com.tatanstudios.astropollocliente.adaptadores.direccion.DireccionAdapter;
import com.tatanstudios.astropollocliente.adaptadores.modotesteo.ListaProductosTesteoAdapter;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentCategoriasModoTesteo extends Fragment {


    TextView txtToolbar;
    RecyclerView recyclerView;

    RelativeLayout root;

    ApiService service;
    TokenManager tokenManager;
    ProgressBar progressBar;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ListaProductosTesteoAdapter adapter = new ListaProductosTesteoAdapter();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_categorias_modotesteo, container, false);


        root = vista.findViewById(R.id.root);
        recyclerView = vista.findViewById(R.id.recycler);
        txtToolbar = vista.findViewById(R.id.txtToolbar);


        txtToolbar.setText(getString(R.string.productos));

        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);
        progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


        peticionServidor();
        return vista;
    }


    void peticionServidor(){

        progressBar.setVisibility(View.VISIBLE);
        String id = tokenManager.getToken().getId();

        compositeDisposable.add(
                service.listadoProductosModoTesteo(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuestas -> {

                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuestas != null){

                                        if(apiRespuestas.getSuccess() == 1) {

                                            adapter = new ListaProductosTesteoAdapter(getContext(), apiRespuestas.getProducto(), this);
                                            recyclerView.setAdapter(adapter);

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


    // para elegir la cantidad de producto al listado
    public void elegirCantidadProducto(int idpro){

        Intent intent = new Intent(getContext(), ElegirProductoModoTesteoActivity.class);
        intent.putExtra("KEY_IDPRO", idpro);
        startActivity(intent);
    }



    void mensajeSinConexion(){
        progressBar.setVisibility(View.GONE);
        Toasty.info(getActivity(), getString(R.string.sin_conexion)).show();
    }

    @Override
    public void onStop() {
        if(compositeDisposable != null){
            compositeDisposable.clear();
        }
        super.onStop();
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
