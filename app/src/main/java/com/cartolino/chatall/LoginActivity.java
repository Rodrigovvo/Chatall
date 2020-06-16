package com.cartolino.chatall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cartolino.chatall.config.ConfiguracaoFirebase;
import com.cartolino.chatall.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {

    TextView cadastro;

    EditText editTextEmailLogin, editTextSenhaLogin;
    Button buttonEntrar;
    FirebaseAuth autenticacao;
    Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cadastro = findViewById(R.id.textCadastro);
        editTextEmailLogin = findViewById(R.id.editText_EmailLogin);
        editTextSenhaLogin = findViewById(R.id.editText_SenhaLogin);
        buttonEntrar = findViewById(R.id.buttonEntrar);


        cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fazerCadastro(v);
            }
        });


        buttonEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoEmail = editTextEmailLogin.getText().toString();
                String textoSenha = editTextSenhaLogin.getText().toString();

                if (!textoEmail.isEmpty()) {
                    if (!textoSenha.isEmpty()) {

                        usuario = new Usuario();
                        usuario.setSenha(textoSenha);
                        usuario.setEmail(textoEmail);

                        validarEmailSenha();

                    } else {
                        Toast.makeText(LoginActivity.this, "Preencha sua senha", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Preencha o campo de email", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario != null){
            abrirTelaPrincipal();
        }

    }

    public void validarEmailSenha() {
        Log.d("Email: ", "" + usuario.getEmail());
        Log.d("Senha: ", "" + usuario.getSenha());

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()

        ).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            abrirTelaPrincipal();
                        } else {
                            String excecao = " ";
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                excecao = "Usuário não cadastrado";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                excecao = "Senha inválida";
                            } catch (Exception e) {
                                excecao = "Erro ao entrar no sistema: " + e.getMessage();
                                e.printStackTrace();
                            }

                            Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

    }



    public void abrirTelaPrincipal(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    public void fazerCadastro(View view) {
        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(intent);

    }


}