package com.tatanstudios.astropollocliente.fragmentos.principal;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.activitys.ordenes.OrdenesEstadoActivasActivity;
import com.tatanstudios.astropollocliente.adaptadores.ordenes.OrdenesActivasAdapter;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentOrdenesActivas extends Fragment {


    RecyclerView recyclerServicio;
    TextView txtToolbar;
    SwipeRefreshLayout refresh;
    RelativeLayout root;


    ApiService service;
    TokenManager tokenManager;
    ProgressBar progressBar;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    OrdenesActivasAdapter adapter;

    boolean unaVez = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_ordenes_activas, container, false);

        root = vista.findViewById(R.id.root);
        refresh = vista.findViewById(R.id.refresh);
        txtToolbar = vista.findViewById(R.id.txtToolbar);
        recyclerServicio = vista.findViewById(R.id.recyclerServicio);

        String texto = getString(R.string.ordenes_activas);
        txtToolbar.setText(texto);

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
        adapter = new OrdenesActivasAdapter();
        recyclerServicio.setAdapter(adapter);

        peticionServidor();
        refresh.setOnRefreshListener(() -> peticionServidor());

        return vista;
    }


    public void peticionServidor(){
        refresh.setRefreshing(true);
        progressBar.setVisibility(View.VISIBLE);
        String id = tokenManager.getToken().getId();

        compositeDisposable.add(
                service.verOrdenesActivas(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {

                                    refresh.setRefreshing(false);
                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuesta != null) {
                                        if(apiRespuesta.getSuccess() == 1) {

                                            if(apiRespuesta.getOrdenes().size() == 0) {
                                                if(unaVez) {
                                                    Toasty.info(getActivity(), "No hay ordenes").show();
                                                }
                                            }

                                            adapter = new OrdenesActivasAdapter(getContext(), apiRespuesta.getOrdenes(), this);
                                            recyclerServicio.setAdapter(adapter);

                                            unaVez = true;
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

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    peticionServidor();
                }
            });

    public void abrirFragmentOrdenes(int ordenid){

        Intent res = new Intent(getActivity(), OrdenesEstadoActivasActivity.class);
        res.putExtra("KEY_ORDEN", ordenid);
        someActivityResultLauncher.launch(res);
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
