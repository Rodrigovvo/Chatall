package com.cartolino.chatall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cartolino.chatall.config.ConfiguracaoFirebase;
import com.cartolino.chatall.helper.Permissao;
import com.cartolino.chatall.helper.UsuarioFirebase;
import com.cartolino.chatall.model.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesActivity extends AppCompatActivity {

    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;

    private ImageButton imageButtonCamera, imageButtonGaleria;
    private ImageView imageViewCorrecaoNome;
    private CircleImageView circleImageView;
    private EditText editTextNome;
    private String idUsuario = UsuarioFirebase.getIdUsuario();
    private StorageReference referenciaImagem = ConfiguracaoFirebase.getFirebaseStorage();

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    } ;

    private Usuario usuarioLogado;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        imageButtonCamera   = findViewById(R.id.imageCamera);
        imageButtonGaleria  = findViewById(R.id.imageGaleria);
        circleImageView     = findViewById(R.id.circleImageViewFotoPerfil);
        editTextNome        = findViewById(R.id.editTextNomePessoal);
        imageViewCorrecaoNome= findViewById(R.id.imageViewCorrecaoNome);

        //Validar Permissoes
        Permissao.validarPermissoes( permissoesNecessarias, this, 1);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final FirebaseUser usuario = UsuarioFirebase.getUsuarioAtual();
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

        editTextNome.setText(usuario.getDisplayName());


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

        imageViewCorrecaoNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String novoNome = editTextNome.getText().toString();
                atualizarNomeUsuario(novoNome);
                usuarioLogado.setNome(novoNome);
                usuarioLogado.atualizar();
                Toast.makeText(ConfiguracoesActivity.this, "Alteração do nome do Usuário \nRealizada com Sucesso!", Toast.LENGTH_SHORT).show();

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
            usuarioLogado.setFoto(url.toString());
            usuarioLogado.atualizar();
            Log.i("atualização da foto", " OK - caminho da foto" + url);

        }else {
            Log.i(" Erro atualizarFoto", "caminho da foto"  + url);
        }

    }

    public void atualizarNomeUsuario(String nome){
        if(UsuarioFirebase.atualizarNomeUsuario(nome)){
            Log.i("Atualização de nome", " novo nome: " + nome);
        } else{
            Log.i("Erro na atualização", " novo nome: " + nome);
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