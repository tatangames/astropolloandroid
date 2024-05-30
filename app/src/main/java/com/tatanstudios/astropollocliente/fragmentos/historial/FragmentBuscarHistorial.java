package com.tatanstudios.astropollocliente.fragmentos.historial;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.activitys.ordenes.ProductosOrdenesActivity;
import com.tatanstudios.astropollocliente.adaptadores.historial.HistorialAdapter;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentBuscarHistorial extends Fragment {


    // muestra lista de ordenes
    String fecha1 = "";
    String fecha2 = "";

    ApiService service;
    TokenManager tokenManager;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    HistorialAdapter historialAdapter;

    RecyclerView recyclerServicio;

    RelativeLayout root;

    LinearLayout vistaLinear;

    TextView txtTitulo;

    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_historial, container, false);

        txtTitulo = vista.findViewById(R.id.txtToolbar);
        vistaLinear = vista.findViewById(R.id.vista);
        root = vista.findViewById(R.id.root);
        recyclerServicio = vista.findViewById(R.id.recyclerServicio);


        Bundle bundle = getArguments();
        if (bundle != null) {
            fecha1 = bundle.getString("KEY_FECHA1");
            fecha2 = bundle.getString("KEY_FECHA2");
        }

        txtTitulo.setText(getString(R.string.historial));

        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);

        progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerServicio.setLayoutManager(layoutManager);
        recyclerServicio.setHasFixedSize(true);
        recyclerServicio.setAdapter(historialAdapter);
        peticionServidor();

        return vista;
    }

    void peticionServidor(){

        progressBar.setVisibility(View.VISIBLE);

        String id = tokenManager.getToken().getId();
        compositeDisposable.add(
                service.verHistorial(id, fecha1, fecha2)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    progressBar.setVisibility(View.GONE);
                                    vistaLinear.setVisibility(View.VISIBLE);

                                    if(apiRespuesta != null){
                                        if(apiRespuesta.getSuccess() == 1){


                                            if(apiRespuesta.getConteo() == 0){
                                                Toasty.info(getActivity(), getString(R.string.no_hay_ordenes)).show();
                                            }

                                            historialAdapter = new HistorialAdapter(getContext(), apiRespuesta.getOrdenes(), this);
                                            recyclerServicio.setAdapter(historialAdapter);
                                            root.setVisibility(View.VISIBLE);
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

    public void verProductos(int ordenid){
        Intent res = new Intent(getContext(), ProductosOrdenesActivity.class);
        res.putExtra("KEY_ORDEN", ordenid);
        startActivity(res);
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
