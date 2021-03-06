package com.cartolino.chatall.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.cartolino.chatall.ChatActivity;
import com.cartolino.chatall.R;
import com.cartolino.chatall.adapter.ContatoAdapter;
import com.cartolino.chatall.config.ConfiguracaoFirebase;
import com.cartolino.chatall.helper.RecyclerItemClickListener;
import com.cartolino.chatall.helper.UsuarioFirebase;
import com.cartolino.chatall.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ContatosFragment extends Fragment {

        private RecyclerView recyclerViewContatos;
        private ContatoAdapter adapter;
        private ArrayList<Usuario> listaContatos = new ArrayList<>();
        private DatabaseReference usuariosRef;
        private ValueEventListener valueEventListenerContatos;
        private FirebaseUser usuarioAtual;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        usuariosRef = ConfiguracaoFirebase.getBaseDeDados().child("usuarios");

        usuarioAtual = UsuarioFirebase.getUsuarioAtual();

        recyclerViewContatos = view.findViewById(R.id.recyclerViewPaginaContatos);
        //configurar o Adpater
        adapter = new ContatoAdapter(getActivity(),listaContatos);

        // configurar o recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewContatos.setLayoutManager(layoutManager);
        recyclerViewContatos.setHasFixedSize(true);
        recyclerViewContatos.setAdapter(adapter);

        recyclerViewContatos.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerViewContatos,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }

                    @Override
                    public void onItemClick(View view, int position) {
                        Usuario usuarioSelecionado = listaContatos.get(position);
                        Intent intent  = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("chatContato", usuarioSelecionado);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }
        ));


        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContatos();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuariosRef.removeEventListener(valueEventListenerContatos);
    }

    public void recuperarContatos(){
        valueEventListenerContatos = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dados: snapshot.getChildren()) {
                    Usuario usuario = dados.getValue(Usuario.class);

                    if (!usuario.getEmail().equals(usuarioAtual.getEmail())) {
                        listaContatos.add(usuario);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}