package com.tatanstudios.astropollocliente.fragmentos.splash;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.tatanstudios.astropollocliente.R;
import com.tatanstudios.astropollocliente.activitys.principal.PrincipalActivity;
import com.tatanstudios.astropollocliente.fragmentos.loginuser.FragmentLogin;

import com.tatanstudios.astropollocliente.network.TokenManager;

public class FragmentSplash extends Fragment {


    private final int SPLASH_DISPLAY_LENGTH = 3000;
    TokenManager tokenManager;

    LottieAnimationView lottieAnimationView;

    TextView txtApp;

    ImageView imgPizza;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_splash, container, false);

        lottieAnimationView = vista.findViewById(R.id.lottieAnim);
        txtApp = vista.findViewById(R.id.txtApp);
        imgPizza = vista.findViewById(R.id.pizzaIcono);


        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("prefs", MODE_PRIVATE));

        new Handler().postDelayed(() -> {

            // inicio automatico con token que iria en el SPLASH
            if(tokenManager.getToken().getId() != null){
                Intent intent = new Intent(getActivity(), PrincipalActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else {

                FragmentLogin fragmentLogin = new FragmentLogin();
                Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentContenedor);
                if(currentFragment.getClass().equals(fragmentLogin.getClass())) return;

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContenedor, fragmentLogin)
                        .addToBackStack(null)
                        .commit();
            }
        }, SPLASH_DISPLAY_LENGTH);


        return vista;
    }

}
