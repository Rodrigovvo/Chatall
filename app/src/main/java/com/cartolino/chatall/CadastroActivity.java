package com.cartolino.chatall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cartolino.chatall.config.ConfiguracaoFirebase;
import com.cartolino.chatall.helper.Base64Customizada;
import com.cartolino.chatall.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private EditText nomeCadastro, emailCadastro, senhaCadastro;
    private Button btnCadastrar;
    private FirebaseAuth autenticador;
    private Usuario novoUsuario = new Usuario();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        nomeCadastro = findViewById(R.id.EditText_NomeCadastro);
        emailCadastro = findViewById(R.id.EditText_EmailCadastro);
        senhaCadastro = findViewById(R.id.EditText_SenhaCadastro);
        btnCadastrar = findViewById(R.id.buttonEntrar);


        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nomeDoUserCadastrante = nomeCadastro.getText().toString();
                String emailDoUserCadastrante = emailCadastro.getText().toString();
                String senhaDoUserCadastrante = senhaCadastro.getText().toString();

                if (!nomeDoUserCadastrante.isEmpty()) {
                    if (!emailDoUserCadastrante.isEmpty()) {
                        if (!senhaDoUserCadastrante.isEmpty()) {


                            novoUsuario.setEmail(emailDoUserCadastrante);
                            novoUsuario.setNome(nomeDoUserCadastrante);
                            novoUsuario.setSenha(senhaDoUserCadastrante);

                            String idDoUsuario = Base64Customizada.codificarParaBase64(emailDoUserCadastrante);
                            novoUsuario.setIdUsuario(idDoUsuario);


                            cadastarNovoUsuario();

                        } else {
                            Toast.makeText(CadastroActivity.this, "O campo da senha do usuário está vazio!", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(CadastroActivity.this, "O campo email do usuário está vazio!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(CadastroActivity.this, "O campo nome do usuário está vazio!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public void cadastarNovoUsuario() {
        autenticador = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticador.createUserWithEmailAndPassword(
                novoUsuario.getEmail(), novoUsuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    novoUsuario.salvarUsuario();
                    finish();
                    try {




                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(CadastroActivity.this, "Erro ao cadastrar usuário" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    String excecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        excecao = "Digite uma senha mais forte! ";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        excecao = "Digite email válido! ";
                    } catch (FirebaseAuthUserCollisionException e) {
                        excecao = "O email informado já está cadastrado!";
                    } catch (Exception e) {
                        excecao = "Erro ao cadastrar usuário" + e.getMessage();
                        e.printStackTrace();

                    }
                    Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}