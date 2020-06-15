package com.cartolino.chatall.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {
    private static FirebaseAuth autenticacao;
    private static DatabaseReference baseDeDados;

    public static FirebaseAuth getFirebaseAutenticacao() {
        if (autenticacao == null) {
            autenticacao = FirebaseAuth.getInstance();
        }
        return autenticacao;
    }

    public static DatabaseReference getBaseDeDados() {
        if (baseDeDados == null) {
            baseDeDados = FirebaseDatabase.getInstance().getReference();
        }
        return baseDeDados;
    }


}
