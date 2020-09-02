package com.cartolino.chatall.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cartolino.chatall.R;
import com.cartolino.chatall.adapter.ContatoAdapter;
import com.cartolino.chatall.config.ConfiguracaoFirebase;
import com.cartolino.chatall.model.Usuario;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        usuariosRef = ConfiguracaoFirebase.getBaseDeDados().child("usuarios");

        recyclerViewContatos = view.findViewById(R.id.recyclerViewPaginaContatos);
        //configurar o Adpater
        adapter = new ContatoAdapter(getActivity(),listaContatos);

        // configurar o recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewContatos.setLayoutManager(layoutManager);
        recyclerViewContatos.setHasFixedSize(true);
        recyclerViewContatos.setAdapter(adapter);
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarCotnatos();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuariosRef.removeEventListener(valueEventListenerContatos);
    }

    public void recuperarCotnatos(){
        valueEventListenerContatos = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dados: snapshot.getChildren()){
                    Usuario usuario = dados.getValue(Usuario.class);
                    listaContatos.add(usuario);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}