package com.cartolino.chatall.helper;

import com.cartolino.chatall.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UsuarioFirebase {

    public static String getIdUsuario(){

        FirebaseAuth usu = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String email = usu.getCurrentUser().getEmail();
        assert email != null;

        return Base64Customizada.codificarParaBase64(email);
    }

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usu = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usu.getCurrentUser();
    }


}
