package com.cartolino.chatall.helper;

import android.util.Base64;

public class Base64Customizada {

    public static String codificarParaBase64(String codificando) {
        return Base64.encodeToString(codificando.getBytes(), Base64.DEFAULT).replaceAll("([\\n|\\r])", "");

    }

    public static String decodificarDaBase64(String textoCodificado) {
        return new String(Base64.decode(textoCodificado, Base64.DEFAULT));
    }
}
