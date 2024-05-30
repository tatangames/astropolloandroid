package com.tatanstudios.astropollocliente.activitys.perfil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.adaptadores.horario.HorarioRestauranteAdapter;
import com.tatanstudios.astropollocliente.modelos.horarios.ModeloHorarioRestaurante;
import com.tatanstudios.astropollocliente.modelos.perfil.ModeloHorarioList;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class HorariosActivity extends AppCompatActivity {


    // HORARIO DEL RESTAURANTE SEGUN DIRECCION


    TextView txtRestaurante;
    RecyclerView recyclerView;
    RelativeLayout root;
    LinearLayout linear;
    TextView txtToolbar;


    ProgressBar progressBar;
    ApiService service;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    TokenManager tokenManager;


    List<ModeloHorarioRestaurante> elementos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horarios);

        txtRestaurante = findViewById(R.id.txtRestaurante);
        recyclerView = findViewById(R.id.recyclerHorarios);
        root = findViewById(R.id.root);
        linear = findViewById(R.id.linear);
        txtToolbar = findViewById(R.id.txtToolbar);


        txtToolbar.setText(getString(R.string.horarios));
        elementos = new ArrayList<>();


        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);
        tokenManager = TokenManager.getInstance(this.getSharedPreferences("prefs", MODE_PRIVATE));
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);

        peticionServidor();
    }

    void peticionServidor(){
        progressBar.setVisibility(View.VISIBLE);

        String id = tokenManager.getToken().getId();

        compositeDisposable.add(
                service.informacionHorario(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuestas -> {

                                    progressBar.setVisibility(View.GONE);

                                    if(apiRespuestas != null) {

                                        if(apiRespuestas.getSuccess() == 1){

                                            txtRestaurante.setText(apiRespuestas.getRestaurante());


                                            for (ModeloHorarioList infoHora : apiRespuestas.getHorario()){

                                                switch (infoHora.getDia()){

                                                    case 1: // domingo


                                                        elementos.add(new ModeloHorarioRestaurante(getString(R.string.Domingo), infoHora.getHorario()));


                                                        break;

                                                    case 2: // lunes
                                                        elementos.add(new ModeloHorarioRestaurante(getString(R.string.Lunes), infoHora.getHorario()));

                                                        break;

                                                    case 3:
                                                        elementos.add(new ModeloHorarioRestaurante(getString(R.string.Martes), infoHora.getHorario()));

                                                        break;

                                                    case 4:
                                                        elementos.add(new ModeloHorarioRestaurante(getString(R.string.Miercoles), infoHora.getHorario()));

                                                        break;

                                                    case 5:
                                                        elementos.add(new ModeloHorarioRestaurante(getString(R.string.Jueves), infoHora.getHorario()));

                                                        break;

                                                    case 6:
                                                        elementos.add(new ModeloHorarioRestaurante(getString(R.string.Viernes), infoHora.getHorario()));

                                                        break;
                                                    case 7:
                                                        elementos.add(new ModeloHorarioRestaurante(getString(R.string.Sabado), infoHora.getHorario()));

                                                        break;
                                                    default:
                                                }
                                            }



                                            HorarioRestauranteAdapter adapter = new HorarioRestauranteAdapter(this, elementos, this);
                                            recyclerView.setHasFixedSize(true);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(this));
                                            recyclerView.setAdapter(adapter);


                                            linear.setVisibility(View.VISIBLE);


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
        Toasty.info(this, getResources().getString(R.string.sin_conexion)).show();
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