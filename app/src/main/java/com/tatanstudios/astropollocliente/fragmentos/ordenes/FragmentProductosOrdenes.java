package com.tatanstudios.astropollocliente.fragmentos.ordenes;

import static android.content.Context.MODE_PRIVATE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.adaptadores.ordenes.ProductosOrdenesActivasAdapter;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentProductosOrdenes extends Fragment {


    int ordenid = 0;

    RecyclerView recyclerProductos;
    TextView txtToolbar;
    RelativeLayout root;

    ApiService service;
    TokenManager tokenManager;
    ProgressBar progressBar;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProductosOrdenesActivasAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_productos_ordenes, container, false);

        root = vista.findViewById(R.id.root);
        txtToolbar = vista.findViewById(R.id.txtToolbar);
        recyclerProductos = vista.findViewById(R.id.recyclerProductos);

        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);
        progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);

        txtToolbar.setText(getString(R.string.productos));

        Bundle bundle = getArguments();
        if(bundle != null) {
            ordenid = bundle.getInt("KEY_ORDEN");
        }

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerProductos.setLayoutManager(layoutManager);
        recyclerProductos.setHasFixedSize(true);
        adapter = new ProductosOrdenesActivasAdapter();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerProductos.addItemDecoration(dividerItemDecoration);
        recyclerProductos.setAdapter(adapter);

        peticionServidor();

        return vista;
    }

    // solciitar lista de productos
    void peticionServidor(){
        progressBar.setVisibility(View.VISIBLE);
        compositeDisposable.add(
                service.verProductosOrdenes(ordenid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuesta -> {
                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuesta != null) {

                                        if(apiRespuesta.getSuccess() == 1){

                                            adapter = new ProductosOrdenesActivasAdapter(getContext(), apiRespuesta.getProductos(), this);
                                            recyclerProductos.setAdapter(adapter);

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

    // informacion del producto individual
    public void verProductoIndividual(int idproducto){
        FragmentProductoOrdenIndividual fragmentInfo = new FragmentProductoOrdenIndividual();

        Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentContenedor);
        if (currentFragment.getClass().equals(fragmentInfo.getClass())) return;

        Bundle bundle = new Bundle();
        bundle.putInt("KEY_ORDEN", idproducto);

        fragmentInfo.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContenedor, fragmentInfo)
                .addToBackStack(null)
                .commit();
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
