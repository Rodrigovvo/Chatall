package com.cartolino.chatall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.cartolino.chatall.config.ConfiguracaoFirebase;
import com.cartolino.chatall.helper.Base64Customizada;
import com.cartolino.chatall.helper.Permissao;
import com.cartolino.chatall.helper.UsuarioFirebase;
import com.cartolino.chatall.model.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesActivity extends AppCompatActivity {

    private ImageButton imageButtonCamera, imageButtonGaleria;
    private CircleImageView circleImageView;
    private String idUsuario = UsuarioFirebase.getIdUsuario();
    private StorageReference referenciaImagem = ConfiguracaoFirebase.getFirebaseStorage();

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    } ;

    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        imageButtonCamera   = findViewById(R.id.imageCamera);
        imageButtonGaleria  = findViewById(R.id.imageGaleria);
        circleImageView     = findViewById(R.id.circleImageViewFotoPerfil);

        //Validar Permissoes
        Permissao.validarPermissoes( permissoesNecessarias, this, 1);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseUser usuario = UsuarioFirebase.getUsuarioAtual();
        Uri url = usuario.getPhotoUrl();

        Log.i(" Url perfil: ", " Endereço da foto do perfil " + url);
        if (url != null){

            Glide.with(ConfiguracoesActivity.this)
                    .load(url)
                    .into(circleImageView);

            Log.i(" Url perfil: ", " Endereço da foto do perfil glide " + url);

        }else{
            circleImageView.setImageResource(R.drawable.padrao);
        }


        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null ){
                    startActivityForResult(intent, SELECAO_CAMERA);
                }
            }
        });


        imageButtonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);;
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, SELECAO_GALERIA);
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {
                switch (requestCode){
                    case SELECAO_CAMERA :
                        assert data != null;
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA:
                        assert data != null;
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        break;
                }

                if (imagem != null){
                    circleImageView.setImageBitmap(imagem);


                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();


                    // TODO : Salvar  a imagem no Firebase
                   StorageReference imagemRef = referenciaImagem.child("imagens").child("perfil").child(idUsuario).child("perfil.jpeg");


                    UploadTask uploadTask = imagemRef.putBytes( dadosImagem);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesActivity.this, " Erro ao fazer o upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ConfiguracoesActivity.this, " Sucesso ao carregar a imagem", Toast.LENGTH_SHORT).show();

                            //Uri url = taskSnapshot.getUploadSessionUri();
                            Task<Uri> url = taskSnapshot.getStorage().getDownloadUrl();
                            url.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    atualizaFotoUsuario(uri);

                                    Log.d(" AtualizaFotoUsuario: ", " Url: " + uri);

                                }
                            });



                        }
                    });

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        } else  {
            Toast.makeText(ConfiguracoesActivity.this, " Erro ao processar a imagem", Toast.LENGTH_SHORT).show();
        }
    }

    public void atualizaFotoUsuario(Uri url){
        if(UsuarioFirebase.atualizarFotoUsuario(url)) {
            Log.i(" atualização da foto", " caminho da foto" + url);

        }else {
            Log.i(" Erro atualizarFoto", "caminho da foto"  + url);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoREsultado : grantResults){
            if (permissaoREsultado == PackageManager.PERMISSION_DENIED ){
                alertaValidacao();
            }
        }
    }

    private void alertaValidacao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

}