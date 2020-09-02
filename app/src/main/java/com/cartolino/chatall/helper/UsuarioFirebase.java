package com.cartolino.chatall.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cartolino.chatall.config.ConfiguracaoFirebase;
import com.cartolino.chatall.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

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

    public static boolean atualizarFotoUsuario(Uri url){

        try{
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(url).build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if( !task.isSuccessful()){
                        Log.d("Perfil: ", " Erro ao atualizar a  foto de perfil.");
                    } else {
                        Log.d("Perfil: ", " Sucesso ao atualizar a  foto de perfil.");
                    }
                }
            });
            return true;
        } catch (Exception e ){
            Log.d("Perfil: ", " Erro ao atualizar a  foto de perfil." + e.getMessage());

            e.printStackTrace();

            return false;

        }


    }

    public static boolean atualizarNomeUsuario(String nome){

        try{
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if( !task.isSuccessful()){
                        Log.d("Perfil: ", " Erro ao atualizar o nome do perfil.");
                    } else {
                        Log.d("Perfil: ", " Sucesso ao atualizar o nome do perfil.");
                    }
                }
            });
            return true;
        } catch (Exception e ){
            Log.d("Perfil: ", " Erro ao atualizar o nome do perfil." + e.getMessage());

            e.printStackTrace();

            return false;

        }


    }

    public static Usuario getDadosUsuarioLogado(){
        FirebaseUser usuario = getUsuarioAtual();
        Usuario dadosUsuario = new Usuario();
        dadosUsuario.setEmail(usuario.getEmail());
        dadosUsuario.setNome(usuario.getDisplayName());
        if (usuario.getPhotoUrl() == null){
            dadosUsuario.setFoto("");
        }else{
            dadosUsuario.setFoto(usuario.getPhotoUrl().toString());
        }

        return dadosUsuario;

    }


}
