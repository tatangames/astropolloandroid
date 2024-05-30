package com.tatanstudios.astropollocliente.fragmentos.direcciones;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.activitys.mapa.MapaActivity;
import com.tatanstudios.astropollocliente.adaptadores.direccion.DireccionAdapter;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentListaDirecciones extends Fragment {


    TextView txtToolbar;
    RecyclerView recyclerView;
    FloatingActionButton fab;
    RelativeLayout root;

    ApiService service;
    TokenManager tokenManager;
    ProgressBar progressBar;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    DireccionAdapter adapter = new DireccionAdapter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_lista_direcciones, container, false);


        root = vista.findViewById(R.id.root);
        fab = vista.findViewById(R.id.fab);
        recyclerView = vista.findViewById(R.id.recyclerDireccion);
        txtToolbar = vista.findViewById(R.id.txtToolbar);


        txtToolbar.setText(getString(R.string.direcciones));

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

        fab.setOnClickListener(v -> abrirMapa());

        peticionServidor();

        return vista;
    }

    // recargar activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 10) {
            if (resultCode == Activity.RESULT_OK) {
                peticionServidor();
            }
        }
    }

    // ver si tengo permiso para GPS
    void abrirMapa(){

        Dexter.withContext(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        Intent intent = new Intent(getActivity(), MapaActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if(response.isPermanentlyDenied()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle(getString(R.string.permiso_denegado))
                                    .setMessage(getString(R.string.permiso_localizacion_denegado))
                                    .setNegativeButton(getString(R.string.cancelar), null)
                                    .setPositiveButton(getString(R.string.aceptar), (dialog, which) -> {
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));
                                    })
                                    .show();
                        }else{
                            Toasty.info(getActivity(), getResources().getString(R.string.permiso_denegado)).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    // obtener lista de direcciones
    void peticionServidor(){

        progressBar.setVisibility(View.VISIBLE);
        String id = tokenManager.getToken().getId();

        compositeDisposable.add(
                service.listadoDeDirecciones(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuestas -> {

                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuestas != null){

                                        // no tengo una direccion registrada
                                        if(apiRespuestas.getSuccess() == 1) {
                                            Toasty.info(getActivity(), apiRespuestas.getMensaje()).show();
                                        }

                                        // si tengo direcciones registradas
                                        else if(apiRespuestas.getSuccess() == 2) {
                                            adapter = new DireccionAdapter(getContext(), apiRespuestas.getDirecciones(), this);
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

    // obtiene los datos de una direccion
    public void dialogoDescripcion(int iddireccion, String nombre, String direccion, String puntoReferencia, String telefono){

        FragmentInfoDireccionCliente fragmentInfo = new FragmentInfoDireccionCliente();

        Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentContenedor);
        if (currentFragment.getClass().equals(fragmentInfo.getClass())) return;

        Bundle bundle = new Bundle();
        bundle.putInt("KEY_ID", iddireccion);
        bundle.putString("KEY_NOMBRE", nombre);
        bundle.putString("KEY_DIRECCION", direccion);
        bundle.putString("KEY_REFERENCIA", puntoReferencia);
        bundle.putString("KEY_TELEFONO", telefono);
        fragmentInfo.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContenedor, fragmentInfo)
                .addToBackStack(null)
                .commit();
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
