package com.cartolino.chatall.model;



import android.drm.DrmStore;
import android.util.Log;

import com.cartolino.chatall.config.ConfiguracaoFirebase;
import com.cartolino.chatall.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Usuario {
    private String idUsuario;
    private String nome;
    private String email;
    private String senha;
    private String foto;

    //construtor
    public Usuario() {
    }

    //construtor com referências
    public Usuario(String idUsuario, String nome, String email, String senha) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public void salvarUsuario() {
        DatabaseReference referenciaDoFirebase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usuario  = referenciaDoFirebase.child("usuarios").child(this.idUsuario);
        usuario.setValue(this);

    }

    public void atualizar(){
        String identificadorUsuario = UsuarioFirebase.getIdUsuario();
        DatabaseReference firebaseDatabase = ConfiguracaoFirebase.getBaseDeDados();

        DatabaseReference usuariosRef = firebaseDatabase.child("usuarios")
                .child(identificadorUsuario);

        Map<String, Object> valoresUsuario = converterParaMap();

        usuariosRef.updateChildren(valoresUsuario);
    }

    @Exclude
    public Map<String, Object> converterParaMap(){
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("email", getEmail());
        usuarioMap.put("nome", getNome());
        usuarioMap.put("foto", getFoto());

        return  usuarioMap;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Exclude
    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
