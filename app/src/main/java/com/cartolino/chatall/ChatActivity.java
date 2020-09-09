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
import com.cartolino.chatall.model.Mensagem;
import com.cartolino.chatall.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;

    private TextView toolbarNome;
    private CircleImageView toolbarFoto;
    private Usuario usuarioDestinatario;
    private EditText  chatEditMsgDigitada;
    private TextInputLayout endCustomIcon;

    private DatabaseReference databaseReference;
    private DatabaseReference mensagensRef;

    private StorageReference storageReference;
    private ChildEventListener childEventListenerMensagens;

    private String idUsuarioRemetente, idUsuarioDestinatario;


    private RecyclerView contentChat_RecyclerMsg;
    private MensagensAdapter adapter;

    private List<Mensagem> mensagemLista = new ArrayList<>();




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
        endCustomIcon            = findViewById(R.id.endCustomIcon);

        // Recuperar os dados do usuário
        Bundle b = getIntent().getExtras();
        if(b != null){
            usuarioDestinatario = (Usuario) b.getSerializable("chatContato");
            toolbarNome.setText(usuarioDestinatario.getNome());
            String foto = usuarioDestinatario.getFoto();
            new LoadFotoPerfil(this, foto, toolbarFoto);
        }

        idUsuarioRemetente = UsuarioFirebase.getIdUsuario();
        idUsuarioDestinatario = Base64Customizada.codificarParaBase64(usuarioDestinatario.getEmail());

        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        databaseReference = ConfiguracaoFirebase.getBaseDeDados();
        mensagensRef = databaseReference.child("mensagens").child(idUsuarioRemetente).child(idUsuarioDestinatario);



        adapter = new MensagensAdapter(getApplicationContext(), mensagemLista);
        contentChat_RecyclerMsg.setAdapter(adapter);
        contentChat_RecyclerMsg.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        contentChat_RecyclerMsg.setHasFixedSize(true);



        //Configuração de envio de mensagens
        //EVENTO: Clique no FAB
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensagemEscrita = chatEditMsgDigitada.getText().toString();
                if (!mensagemEscrita.isEmpty()){
                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario( idUsuarioRemetente);
                    mensagem.setMensagem(mensagemEscrita);
                    try {
                        //Remetente
                        salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);
                        //Destinatário
                        salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);
                        chatEditMsgDigitada.setText("");
                    }catch (Exception e ){
                        e.printStackTrace();
                    }

                }else{
                    Toast.makeText(ChatActivity.this, "Digite sua mensagem!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Evento de Clique na Camêra do texto
        endCustomIcon.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);;
                if (intent.resolveActivity(getPackageManager()) != null ){
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
                    case SELECAO_GALERIA :
                        assert data != null;
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        break;
                }

                if (imagem != null){
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    String nomeDaImagem = UUID.randomUUID().toString();

                    final StorageReference imagemRef = storageReference.child("imagens")
                            .child("fotos")
                            .child(idUsuarioRemetente)
                            .child( nomeDaImagem + ".jpeg");


                    UploadTask uploadTask = imagemRef.putBytes( dadosImagem);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity.this, " Erro ao fazer o upload da imagem", Toast.LENGTH_SHORT).show();
                            Log.d("ChatImagem:" , " Erro local 3");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ChatActivity.this, " Sucesso ao carregar a imagem", Toast.LENGTH_SHORT).show();

                            //Uri url = taskSnapshot.getUploadSessionUri();
                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    Mensagem mensagem = new Mensagem();
                                    if(url != null) {
                                        Log.d("ChatImagem:" , " Erro " + url.toString());
                                        mensagem.setIdUsuario(idUsuarioRemetente);
                                        mensagem.setMensagem("imagem.jpeg");
                                        mensagem.setImagem(url.toString());
                                        try {
                                            salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);
                                            salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                            Log.d("ChatImagem:" , " Erro local 5 " +e.getMessage());
                                        }

                                    }

                                    Toast.makeText(ChatActivity.this, "Sucesso ao enviar mensagem!", Toast.LENGTH_SHORT).show();
                                    Log.d("ChatImagem:" , " Erro local 4");
                                }
                            });
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
                Log.d("ChatImagem:" , " Erro local 2");
            }
        } else  {
            Toast.makeText(ChatActivity.this, " Erro ao processar a imagem", Toast.LENGTH_SHORT).show();
            Log.d("ChatImagem:" , " Erro local 1");
        }
    }

    private void salvarMensagem(String idRemetente, String idDestinatario, Mensagem msg){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getBaseDeDados();
        DatabaseReference mensagemRef = databaseReference.child("mensagens");
        mensagemRef.child(idRemetente)
                .child(idDestinatario)
                .push()
                .setValue(msg);
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

    private void recuperarMensagens(){
        childEventListenerMensagens =  mensagensRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Mensagem mensagem = snapshot.getValue(Mensagem.class);
                mensagemLista.add(mensagem);

                Log.d("Mudança Adapter:", "" +  mensagemLista.size());
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

}