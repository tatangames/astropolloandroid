package com.tatanstudios.astropollocliente.activitys.mapa;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.maps.android.PolyUtil;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.extras.LoadingDialog;
import com.tatanstudios.astropollocliente.modelos.mapa.ModeloPoligono;
import com.tatanstudios.astropollocliente.modelos.mapa.ModeloZonas;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import java.util.ArrayList;


import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback {


    View mapView;
    GoogleMap mMap;
    FusedLocationProviderClient mFusedLocationProviderClient;
    Location mLastKnownLocation;
    LocationCallback locationCallback;
    final float DEFAULT_ZOOM = 15;

    Button btnSeleccionar;
    TextView txtToolbar;

    TokenManager tokenManager;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiService service;
    String latitud = "";
    String longitud = "";
    String latitudreal = "";
    String longitudreal = "";

    private ArrayList<Polygon> polygonList = new ArrayList<>();

    PolygonOptions options;
    Polygon polygon;

    LoadingDialog loadingDialog;

    boolean seguroCarga = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        btnSeleccionar = findViewById(R.id.btnSeleccionar);
        txtToolbar = findViewById(R.id.txtToolbar);


        txtToolbar.setText("MAPA");

        loadingDialog = new LoadingDialog(MapaActivity.this);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        if(tokenManager.getToken().getStringPresenDireccionMapa() == null){
            new KAlertDialog(this, KAlertDialog.WARNING_TYPE)
                    .setContentTextSize(20)
                    .setContentText("El Servicio sera brindado solo en las partes marcadas en el mapa.")
                    .setConfirmText(getString(R.string.aceptar))
                    .setContentTextSize(16)
                    .confirmButtonColor(R.drawable.dialogo_theme_success)
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                    })
                    .show();

            tokenManager.guardarPresentacionMapa("1");
        }

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapaActivity.this);
        Places.initialize(MapaActivity.this, getString(R.string.google_maps_api));

        btnSeleccionar.setOnClickListener(v -> posicionPoligono());
    }

    // poligonos
    void obtenerPoligonos(){

        loadingDialog.startLoadingDialog();

        compositeDisposable.add(
                service.listadoMapaZonaPoligonos()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(apiRespuestas -> {

                                    loadingDialog.dismissDialog();

                                    if(apiRespuestas != null){

                                        if(apiRespuestas.getSuccess() == 1) {

                                            // obtener id ciudad del poligono
                                            for (ModeloPoligono mpoligono : apiRespuestas.getPoligono()) {

                                                options = new PolygonOptions();
                                                options.fillColor(Color.argb(20, 0, 255, 0));
                                                options.strokeWidth(2);
                                                options.strokeColor(Color.rgb(25, 118, 210));

                                                // obtener latitud y longitud de poligonos
                                                for (ModeloZonas poli : mpoligono.getPoligonos()) {
                                                    options.add(new LatLng(Float.valueOf(poli.getLatitudPoligono()), Float.valueOf(poli.getLongitudPoligono())));
                                                }

                                                // crear poligono
                                                polygon = mMap.addPolygon(options);
                                                polygon.setTag(mpoligono.getId());
                                                polygonList.add(polygon);
                                            }

                                            // mostrar toolbar
                                            seguroCarga = true;
                                        }else{
                                            mensajeSinConexion();
                                        }
                                    }
                                },
                                throwable -> {
                                    mensajeSinConexion();
                                })
        );
    }

    void mensajeSinConexion(){

        loadingDialog.dismissDialog();
        Toasty.info(MapaActivity.this, getString(R.string.sin_conexion)).show();
    }

    // ver cual poligono esta seleccionando
    void posicionPoligono(){

        if(seguroCarga){
            if(mMap == null){
                Toasty.info(MapaActivity.this, getString(R.string.google_maps_no_instalado)).show();
                return;
            }

            LatLng currentMarkerLocation = mMap.getCameraPosition().target;
            boolean valor = false;
            String idzona = "";
            for (Polygon p : polygonList) {
                valor = PolyUtil.containsLocation(currentMarkerLocation, p.getPoints(), false);
                if (valor){
                    idzona = p.getTag().toString();
                    latitud = String.valueOf(currentMarkerLocation.latitude);
                    longitud = String.valueOf(currentMarkerLocation.longitude);
                    abrirDialogoDireccion(idzona);

                    break;
                }
            }

            // mostrar que aun no hay lugar de entrega para aqui
            if(!valor){
                dialogSinServicio();
            }
        }else{
            Toasty.info(MapaActivity.this, "Cargando Cobertura").show();
        }
    }

    // mensaje zona sin servicio
    void dialogSinServicio(){
        new KAlertDialog(this, KAlertDialog.WARNING_TYPE)
                .setContentTextSize(20)
                .setTitleText("Sin Servicio")
                .setContentText(getString(R.string.lo_sentimos_no_ofrecemos_nuestro))
                .setConfirmText(getString(R.string.aceptar))
                .setContentTextSize(16)
                .confirmButtonColor(R.drawable.dialogo_theme_success)
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                })
                .show();
    }

    // google maps
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if(mapView != null && mapView.findViewById(Integer.parseInt("1")) != null){
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.CENTER_VERTICAL);
            layoutParams.setMargins(0, 0, 10, 250);
        }

        //check if gps is enabled or not and then request user to enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(MapaActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        // habilitar el gps
        task.addOnSuccessListener(MapaActivity.this, locationSettingsResponse -> {
            // si esta habilitado el gps
            getDeviceLocation();
        });

        task.addOnFailureListener(MapaActivity.this, e -> {
            if(e instanceof ResolvableApiException){
                ResolvableApiException resolvable = (ResolvableApiException) e;
                try {
                    resolvable.startResolutionForResult(MapaActivity.this, 51);
                } catch (IntentSender.SendIntentException e1) {
                    e1.printStackTrace();
                }
            }
        });

        obtenerPoligonos();
    }

    // habilitar el gps
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 51) {
            if(resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        super.onBackPressed();
    }

    // localizacion
    @SuppressLint("MissingPermission")
    private void getDeviceLocation(){

        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(Task<Location> task) {
                        if(task.isSuccessful()){
                            mLastKnownLocation = task.getResult();
                            if(mLastKnownLocation != null){
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                                locationCallback = new LocationCallback(){
                                    @Override
                                    public void onLocationResult(LocationResult locationResult){
                                        super.onLocationResult(locationResult);
                                        if(locationResult == null) {
                                            return;
                                        }
                                        mLastKnownLocation = locationResult.getLastLocation();


                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        } else {
                            Toasty.info(MapaActivity.this, getString(R.string.ubicación_desconocida)).show();
                        }
                    }
                });
    }

    // abrir Dialogo detalle para completar los datos e guardarlo en la base
    public void abrirDialogoDireccion(String idzona){

        try {
            if(mLastKnownLocation != null) {
                latitudreal = String.valueOf(mLastKnownLocation.getLatitude());
                longitudreal = String.valueOf(mLastKnownLocation.getLongitude());
            }
        } catch(Exception e) {

        }

        Intent i  = new Intent(this, DireccionNuevoMapaActivity.class);
        i.putExtra("KEY_ZONA", idzona);
        i.putExtra("KEY_LATITUD", latitud);
        i.putExtra("KEY_LONGITUD", longitud);
        i.putExtra("KEY_LATITUDREAL", latitudreal);
        i.putExtra("KEY_LONGITUDREAL", longitudreal);
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        compositeDisposable.clear();
        super.onPause();
    }

}