package com.cartolino.chatall.model;

import android.hardware.usb.UsbDevice;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Usuario {
    private String idUsuario;
    private String nome;
    private String email;
    private String senha;

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
        referenciaDoFirebase.child("usuarios").child(idUsuario).setValue(this);
    }

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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
