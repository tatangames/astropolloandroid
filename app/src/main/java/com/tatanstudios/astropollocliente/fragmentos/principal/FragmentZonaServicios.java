package com.tatanstudios.astropollocliente.fragmentos.principal;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.kalert.KAlertDialog;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.activitys.categorias.TodasCategoriasActivity;
import com.tatanstudios.astropollocliente.activitys.direccion.ListaDireccionesActivity;
import com.tatanstudios.astropollocliente.activitys.loginuser.LoginActivity;
import com.tatanstudios.astropollocliente.activitys.modotesteo.ListadoProductoTesteoActivity;
import com.tatanstudios.astropollocliente.activitys.perfil.PerfilActivity;
import com.tatanstudios.astropollocliente.activitys.productos.ProductosListadoActivity;
import com.tatanstudios.astropollocliente.adaptadores.principal.CategoriasPrincipalAdapter;
import com.tatanstudios.astropollocliente.adaptadores.principal.PopularesPrincipalAdapter;
import com.tatanstudios.astropollocliente.adaptadores.principal.SliderBannerPrincipalAdapter;
import com.tatanstudios.astropollocliente.fragmentos.productos.FragmentElegirCantidadProducto;
import com.tatanstudios.astropollocliente.modelos.principal.SliderItem;
import com.tatanstudios.astropollocliente.modelos.servicios.ModeloSlider;
import com.tatanstudios.astropollocliente.network.ApiService;
import com.tatanstudios.astropollocliente.network.RetrofitBuilder;
import com.tatanstudios.astropollocliente.network.TokenManager;

import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentZonaServicios extends Fragment {

    // VISTA PRINCIPAL DEL NEGOCIO

    TextView txtToolbar;
    ImageView imgPerfil;

    RelativeLayout root;

    ScrollView scrollView;

    TextView txtVerMas;

    ApiService service;
    TokenManager tokenManager;
    ProgressBar progressBar;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    // Para banner
    SliderView sliderView;
    private SliderBannerPrincipalAdapter adapterSliderBanner;
    boolean shouldExecuteOnResume;
    boolean seguro;


    // EVITAR QUE SE RECARGUE SLIDER
    boolean boolSlider = true;


    // adaptadores para vista horizontal
    private RecyclerView recyclerViewCategoryList, recyclerViewPopularList;

    CategoriasPrincipalAdapter adaptadorCategorias;

    PopularesPrincipalAdapter adaptadorPopular;



    // para ordenes de testeo

    Button btnPrimera;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_zona_servicios, container, false);

        txtVerMas = vista.findViewById(R.id.txtVerMas);
        scrollView = vista.findViewById(R.id.scrollView);
        root = vista.findViewById(R.id.root);
        imgPerfil = vista.findViewById(R.id.imgPerfil);
        txtToolbar = vista.findViewById(R.id.txtToolbar);
        btnPrimera = vista.findViewById(R.id.btnPrimera);


        shouldExecuteOnResume = false;

        seguro = true;

        txtToolbar.setText(getString(R.string.astro_pollo));
        sliderView = vista.findViewById(R.id.imageSlider);

        adapterSliderBanner = new SliderBannerPrincipalAdapter(getContext(), this);
        sliderView.setSliderAdapter(adapterSliderBanner);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();



        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));

        service = RetrofitBuilder.createServiceNoAuth(ApiService.class);

        progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(progressBar, params);

        adaptadorCategorias = new CategoriasPrincipalAdapter();
        adaptadorPopular = new PopularesPrincipalAdapter();

        recyclerViewCategoryList = vista.findViewById(R.id.recyclerCategorias);



        // set a GridLayoutManager with 3 number of columns , horizontal gravity and false value for reverseLayout to show the items from start to end
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2, LinearLayoutManager.HORIZONTAL,false);
        recyclerViewCategoryList.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView


        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPopularList = vista.findViewById(R.id.recyclerPopulares);
        recyclerViewPopularList.setLayoutManager(linearLayoutManager2);

        imgPerfil.setOnClickListener(v -> abrirPerfil());
        txtVerMas.setOnClickListener(v -> verTodasCategorias());


        btnPrimera.setOnClickListener(v -> abrirListaProductosTesteo());


        peticionServidor();

        return vista;
    }

    public void redireccionarProducto(int id){

        if(id != 0){

            FragmentElegirCantidadProducto fragmentInfoProducto = new FragmentElegirCantidadProducto();

            Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.main_container);
            if(currentFragment.getClass().equals(fragmentInfoProducto.getClass())) return;

            Bundle bundle = new Bundle();
            bundle.putInt("KEY_PRODUCTO", id);
            fragmentInfoProducto.setArguments(bundle);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_container, fragmentInfoProducto)
                    .addToBackStack(null)
                    .commit();
        }
    }

    // regresa id de categoria seleccionada
    public void elegirCategoria(int id){

        Intent i = new Intent(getActivity(), ProductosListadoActivity.class);
        i.putExtra("KEY_CATEGORIA", id);
        startActivity(i);
    }

    // ver todas las categorias
    public void verTodasCategorias(){
         Intent i = new Intent(getActivity(), TodasCategoriasActivity.class);
         startActivity(i);
    }


    public void abrirListaProductosTesteo(){

        Intent i = new Intent(getActivity(), ListadoProductoTesteoActivity.class);
        startActivity(i);

    }



    // abrir perfil del usuario
    void abrirPerfil() {
        Intent i = new Intent(getActivity(), PerfilActivity.class);
        startActivity(i);
    }

    // solicitar servicios por zona, y nos devuelve el token
    void peticionServidor(){

        if(seguro) {
            seguro = false;

            progressBar.setVisibility(View.VISIBLE);
            String id = tokenManager.getToken().getId();
            compositeDisposable.add(
                    service.listaDeTipoServicio(id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .retry()
                            .subscribe(apiRespuesta -> {

                                        progressBar.setVisibility(View.GONE);
                                        seguro = true;
                                        if (apiRespuesta != null) {

                                            // CLIENTE BLOQUEADO
                                            if (apiRespuesta.getSuccess() == 1) {
                                                clienteBloqueado(apiRespuesta.getTitulo(), apiRespuesta.getMensaje());
                                            }

                                            // NO HAY DIRECCION DE ENTREGA
                                            else if (apiRespuesta.getSuccess() == 2) {
                                                enviarRegistrarDireccion();
                                            }

                                            // MENU DE PRODUCTOS
                                            else if (apiRespuesta.getSuccess() == 3) {

                                                if(boolSlider){
                                                    boolSlider = false;
                                                    for (ModeloSlider p : apiRespuesta.getSlider()) {
                                                        SliderItem sliderItem = new SliderItem();
                                                        sliderItem.setIdproducto(p.getIdProducto());
                                                        sliderItem.setImageUrl(RetrofitBuilder.urlImagenes + p.getImagen());
                                                        adapterSliderBanner.addItem(sliderItem);
                                                    }
                                                }


                                                // PARA MOSTRAR EL BOTON PARA MODO TESTEO DE CLIENTE
                                                if(apiRespuesta.getBtnTesteoServicio() == 1){

                                                    if(apiRespuesta.getBtnTesteoCliente() == 1){

                                                        btnPrimera.setVisibility(View.VISIBLE);

                                                    }else{
                                                        btnPrimera.setVisibility(View.GONE);
                                                    }

                                                }else{
                                                    btnPrimera.setVisibility(View.GONE);
                                                }





                                                adaptadorCategorias = new CategoriasPrincipalAdapter(getContext(), apiRespuesta.getCategorias(), this);
                                                recyclerViewCategoryList.setAdapter(adaptadorCategorias);

                                                adaptadorPopular = new PopularesPrincipalAdapter(getContext(), apiRespuesta.getProductos(), this);
                                                recyclerViewPopularList.setAdapter(adaptadorPopular);


                                                scrollView.setVisibility(View.VISIBLE);
                                            }

                                            // NO HAY UN SERVICIO ASOCIADO A LA ZONA
                                            else if (apiRespuesta.getSuccess() == 4) {
                                                Toasty.info(getActivity(), apiRespuesta.getMensaje()).show();
                                            }

                                            // NO HAY DIRECCION DE ENTREGA SELECCIONADA
                                            else if (apiRespuesta.getSuccess() == 5) {
                                                enviarRegistrarDireccion();
                                            }
                                            else {
                                                mensajeSinConexion();
                                            }
                                        } else {
                                            mensajeSinConexion();
                                        }
                                    },
                                    throwable -> {
                                        mensajeSinConexion();
                                    })
            );
        }
    }

    void enviarRegistrarDireccion(){
        Intent intent = new Intent(getActivity(), ListaDireccionesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    void clienteBloqueado(String titulo, String mensaje){
        KAlertDialog pDialog = new KAlertDialog(getActivity(), KAlertDialog.WARNING_TYPE);
        pDialog.setTitleText(titulo);
        pDialog.setContentText(mensaje);
        pDialog.setConfirmText(getString(R.string.aceptar));
        pDialog.setContentTextSize(16);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.confirmButtonColor(R.drawable.dialogo_theme_success)
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();

                    tokenManager.deletePreferences();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
        pDialog.show();
    }

    void mensajeSinConexion(){
        seguro = true;
        progressBar.setVisibility(View.GONE);
        Toasty.info(getActivity(), getString(R.string.sin_conexion)).show();
    }



    // recarga fragmento
    @Override
    public void onResume() {
        super.onResume();

        if(shouldExecuteOnResume){
            // Your onResume Code Here
            peticionServidor();
        } else{
            shouldExecuteOnResume = true;
        }
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
