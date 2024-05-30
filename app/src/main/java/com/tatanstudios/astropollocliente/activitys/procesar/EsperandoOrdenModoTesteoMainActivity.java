package com.tatanstudios.astropollocliente.activitys.procesar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.activitys.principal.PrincipalActivity;

public class EsperandoOrdenModoTesteoMainActivity extends AppCompatActivity {


    Button btnSalir;
    TextView txtToolbar;

    private CountDownTimer countDownTimer;

    private int tiempo = 20;


    ImageView img1, img2, img3;
    TextView estado1, estado2, estado3;
    TextView fecha1, fecha2, fecha3;

    boolean bloque1, bloque2, bloque3;


    RequestOptions opcionesGlide = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .priority(Priority.NORMAL);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esperando_orden_modo_testeo_main);

        btnSalir = findViewById(R.id.btnSalir);
        txtToolbar = findViewById(R.id.txtToolbar);

        bloque1 = true;
        bloque2 = true;
        bloque3 = true;

        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img);
        img3 = findViewById(R.id.img2);

        estado1 = findViewById(R.id.txtEstado1);
        estado2 = findViewById(R.id.txtEstado2);
        estado3 = findViewById(R.id.txtEstado4);

        fecha1 = findViewById(R.id.txtFecha1);
        fecha2 = findViewById(R.id.txtEstado3);
        fecha3 = findViewById(R.id.txtEstado5);

        txtToolbar.setText(getString(R.string.procesar_orden));


        btnSalir.setOnClickListener(view -> {
            salir();
        });

        starConteo();

    }


    private void starConteo(){
        countDownTimer = new CountDownTimer(tiempo * 1000, 1000) {
            @Override
            public void onTick(long milisegundos) {
                long secondUntilFinish = milisegundos / 1000;

                if(secondUntilFinish < 15){
                    if(bloque1){
                        bloque1 = false;

                        Glide.with(getApplicationContext())
                                .load(R.drawable.marcador_verde)
                                .apply(opcionesGlide)
                                .into(img1);

                        estado1.setText("Orden Preparandose");
                        fecha1.setText("Fecha");
                    }
                }

                if(secondUntilFinish < 10){
                    if(bloque2){
                        bloque2 = false;
                        Glide.with(getApplicationContext())
                                .load(R.drawable.marcador_verde)
                                .apply(opcionesGlide)
                                .into(img2);

                        estado2.setText("Orden En Camino");
                        fecha2.setText("Fecha");
                    }
                }

                if(secondUntilFinish < 5){
                    if(bloque3){
                        bloque3 = false;
                        Glide.with(getApplicationContext())
                                .load(R.drawable.marcador_verde)
                                .apply(opcionesGlide)
                                .into(img3);

                        estado3.setText("Orden Completada");
                        fecha3.setText("Fecha");
                    }
                }

            }

            @Override
            public void onFinish() {
                // finalizado



            }
        }.start();
    }


    void salir(){

        Intent intent = new Intent(this, PrincipalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}