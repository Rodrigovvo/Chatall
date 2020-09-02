package com.cartolino.chatall.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cartolino.chatall.R;
import com.cartolino.chatall.adapter.ContatoAdapter;
import com.cartolino.chatall.model.Usuario;

import java.util.ArrayList;


public class ContatosFragment extends Fragment {
        public RecyclerView recyclerViewContatos;
        public ContatoAdapter adapter;
        private ArrayList<Usuario> listaContatos = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        recyclerViewContatos = view.findViewById(R.id.recyclerViewPaginaContatos);
        //configurar o Adpater
        adapter = new ContatoAdapter(listaContatos, getActivity());

        // configurar o recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewContatos.setLayoutManager(layoutManager);
        recyclerViewContatos.setHasFixedSize(true);
        //recyclerViewContatos.setAdapter();
        return view;

    }
}