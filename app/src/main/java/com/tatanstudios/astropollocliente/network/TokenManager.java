package com.tatanstudios.astropollocliente.network;

import android.content.SharedPreferences;

import com.tatanstudios.astropollocliente.modelos.usuario.AccessTokenUser;


public class TokenManager {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private static TokenManager INSTANCE = null;

    private TokenManager(SharedPreferences prefs) {
        this.prefs = prefs;
        this.editor = prefs.edit();
    }

    public static synchronized TokenManager getInstance(SharedPreferences prefs) {
        if (INSTANCE == null) {
            INSTANCE = new TokenManager(prefs);
        }
        return INSTANCE;
    }

    // GUARDAR CLIENTE ID DEL CLIENTE
    public void guardarClienteID(AccessTokenUser token) {
        editor.putString("ID", token.getId()).commit();
    }

    // INDICACIONES PARA COMO BORRAR EL CARRITO DE COMPRAS
    public void guardarPresentacionBorrarCarrito(String token) {
        editor.putString("DEDOS", token).commit();
    }

    // indicaciones para como elegir direccion
    public void guardarPresentacionMapa(String token) {
        editor.putString("MAPA", token).commit();
    }

    public void deletePreferences(){
        editor.remove("ID").commit();
    }

    public AccessTokenUser getToken(){
        AccessTokenUser token = new AccessTokenUser();

        token.setId(prefs.getString("ID", null));
        token.setStringPresenDireccionMapa(prefs.getString("MAPA", null));
        token.setStringPresenBorrarCarrito(prefs.getString("DEDOS", null));

        return token;
    }

}



