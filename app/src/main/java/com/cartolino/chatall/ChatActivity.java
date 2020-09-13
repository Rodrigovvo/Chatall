package com.cartolino.chatall;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.cartolino.chatall.adapter.MensagensAdapter;
import com.cartolino.chatall.config.ConfiguracaoFirebase;
import com.cartolino.chatall.helper.Base64Customizada;
import com.cartolino.chatall.helper.LoadFotoPerfil;
import com.cartolino.chatall.helper.UsuarioFirebase;
import com.cartolino.chatall.model.Conversa;
import com.cartolino.chatall.model.Mensagem;
import com.cartolino.chatall.model.Usuario;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    public static final int SELECAO_GALERIA = 100;

    private TextView toolbarNome;
    private CircleImageView toolbarFoto;
    private Usuario usuarioDestinatario;

    private EditText  chatEditMsgDigitada;
    private TextInputLayout textInputLayout;

    private RecyclerView contentChat_RecyclerMsg;
    private MensagensAdapter adapter;
    private List<Mensagem> mensagemList = new ArrayList<>();
    private ChildEventListener childEventListenerMensagens;

    private StorageReference referenciaImagem = ConfiguracaoFirebase.getFirebaseStorage();
    private DatabaseReference databaseReference;
    private DatabaseReference mensagensRef;

    private String idUsuarioRemetente, idUsuarioDestinatario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }
        FloatingActionButton fab = findViewById(R.id.fab);

        toolbarNome              = findViewById(R.id.chat_toolbar_textNomeUsuario);
        toolbarFoto              = findViewById(R.id.chat_toolbar_circleImageView);
        chatEditMsgDigitada      = findViewById(R.id.chatEditMsgDigitada);
        contentChat_RecyclerMsg  = findViewById(R.id.contentChat_RecyclerMsg);
        textInputLayout          = findViewById(R.id.textInputLayout);


        // Recuperar os dados do usuário
        Bundle b = getIntent().getExtras();
        if (b != null) {
            usuarioDestinatario = (Usuario) b.getSerializable("chatContato");
            toolbarNome.setText(usuarioDestinatario.getNome());
            String foto = usuarioDestinatario.getFoto();
            new LoadFotoPerfil(this, foto, toolbarFoto);
        }


        adapter = new MensagensAdapter(getApplicationContext(), mensagemList);
        contentChat_RecyclerMsg.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        contentChat_RecyclerMsg.setHasFixedSize(true);
        contentChat_RecyclerMsg.setAdapter(adapter);

        idUsuarioRemetente = UsuarioFirebase.getIdUsuario();
        idUsuarioDestinatario = Base64Customizada.codificarParaBase64(usuarioDestinatario.getEmail());


        databaseReference = ConfiguracaoFirebase.getBaseDeDados();
        mensagensRef = databaseReference.child("mensagens").child(idUsuarioRemetente).child(idUsuarioDestinatario);


        //Configuração de envio de mensagens
        //EVENTO: Clique no FAB
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensagemEscrita = chatEditMsgDigitada.getText().toString();
                if (!mensagemEscrita.isEmpty()) {
                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuarioRemetente);
                    mensagem.setMensagem(mensagemEscrita);
                    try {
                        salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);
                        salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);
                        salvarConversa(mensagem);
                        chatEditMsgDigitada.setText("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(ChatActivity.this, "Digite sua mensagem!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);;
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, SELECAO_GALERIA);
                }

            }
        });

    }

    private void salvarMensagem(String idRemetente, String idDestinatario, Mensagem msg) {
        DatabaseReference databaseReference = ConfiguracaoFirebase.getBaseDeDados();
        DatabaseReference mensagemRef = databaseReference.child("mensagens");
        mensagemRef.child(idRemetente)
                .child(idDestinatario)
                .push()
                .setValue(msg);
    }


    private void salvarConversa(Mensagem mensagem){
        Conversa conversaRemetente = new Conversa();
        conversaRemetente.setIdDestinatario(idUsuarioDestinatario);
        conversaRemetente.setIdRemetente(idUsuarioRemetente);
        conversaRemetente.setUltimaMSG(mensagem.getMensagem());
        conversaRemetente.setUsuarioExibicao(usuarioDestinatario);

        conversaRemetente.salvar();
    }

    private void recuperarMensagens(){
        childEventListenerMensagens =  mensagensRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Mensagem mensagem = snapshot.getValue(Mensagem.class);
                mensagemList.add(mensagem);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                    case SELECAO_GALERIA:
                        assert data != null;
                        Uri localImagemSelecionada = data.getData();
                        try {
                            imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                            Log.d("Imagem", "Url " + localImagemSelecionada.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 45, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    String nomeImagem = UUID.randomUUID().toString();

                    final StorageReference imagemRef = referenciaImagem.child("fotos")
                            .child(idUsuarioRemetente)
                            .child(idUsuarioDestinatario)
                            .child(nomeImagem);


                    UploadTask uploadTask = imagemRef.putBytes( dadosImagem);

                Task<Uri> urlTask;
                urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return imagemRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            String dowloadUrl = downloadUri.toString();
                            if (dowloadUrl != null) {

                                Mensagem msgComImagem = new Mensagem();
                                msgComImagem.setImagem(dowloadUrl);
                                msgComImagem.setIdUsuario(idUsuarioRemetente);
                                msgComImagem.setMensagem("imagem.jpeg");
                                salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, msgComImagem);
                                salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, msgComImagem);

                                salvarConversa(msgComImagem);
                            }
                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensagens();
    }


    @Override
    protected void onStop() {
        super.onStop();
        mensagensRef.removeEventListener(childEventListenerMensagens);
    }
}