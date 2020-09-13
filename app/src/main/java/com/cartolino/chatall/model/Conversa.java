package com.cartolino.chatall.model;

import com.cartolino.chatall.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Conversa {
    private String idRemetente, idDestinatario, ultimaMSG;
    private Usuario usuarioExibicao;

    public Conversa() {
    }
    public void salvar(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getBaseDeDados();
        DatabaseReference conversaRef = databaseReference.child("conversas");

        conversaRef.child(this.getIdRemetente()).child(this.getIdDestinatario())
                .setValue(this);


    }

    public String getIdRemetente() {
        return idRemetente;
    }

    public void setIdRemetente(String idRemetente) {
        this.idRemetente = idRemetente;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getUltimaMSG() {
        return ultimaMSG;
    }

    public void setUltimaMSG(String ultimaMSG) {
        this.ultimaMSG = ultimaMSG;
    }

    public Usuario getUsuarioExibicao() {
        return usuarioExibicao;
    }

    public void setUsuarioExibicao(Usuario usuarioExibicao) {
        this.usuarioExibicao = usuarioExibicao;
    }
}
