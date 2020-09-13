package com.cartolino.chatall.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.cartolino.chatall.ChatActivity;
import com.cartolino.chatall.R;
import com.cartolino.chatall.adapter.ConversasAdapter;
import com.cartolino.chatall.config.ConfiguracaoFirebase;
import com.cartolino.chatall.helper.RecyclerItemClickListener;
import com.cartolino.chatall.helper.UsuarioFirebase;
import com.cartolino.chatall.model.Conversa;
import com.cartolino.chatall.model.Usuario;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ConversasFragment extends Fragment {

    private RecyclerView conversaRecycler;
    private ConversasAdapter adapter;
    private List<Conversa> conversas = new ArrayList<>();
    private ChildEventListener valueEventListenerConversas;

    private DatabaseReference database;
    private DatabaseReference conversasRef;

    public ConversasFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        conversaRecycler = view.findViewById(R.id.fragmentConversasRecycler);

        String identificadoUsuario = UsuarioFirebase.getIdUsuario();
        database = ConfiguracaoFirebase.getBaseDeDados();
        conversasRef =  database.child("conversas").child(identificadoUsuario);

        LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        adapter = new ConversasAdapter(getActivity(), conversas);
        conversaRecycler.setLayoutManager(layoutManager);
        conversaRecycler.setHasFixedSize(true);
        conversaRecycler.setAdapter(adapter);

        conversaRecycler.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                conversaRecycler,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Conversa conversaSelecionada = conversas.get(position);

                        Intent intent  = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("chatContato", conversaSelecionada.getUsuarioExibicao());
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));

        return view;
    }

    public void recuperarConversas(){

        valueEventListenerConversas = conversasRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    Conversa conversa = snapshot.getValue(Conversa.class);
                    conversas.add(conversa);
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
    public void onStart() {
        super.onStart();
        recuperarConversas();
    }

    @Override
    public void onStop() {
        super.onStop();
        database.removeEventListener(valueEventListenerConversas);
    }
}