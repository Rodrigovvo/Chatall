package com.cartolino.chatall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cartolino.chatall.R;
import com.cartolino.chatall.model.Usuario;

import java.util.List;

public class ContatoAdapter extends RecyclerView.Adapter<ContatoAdapter.MViewHolder> {

    private List<Usuario> contatos;
    private Context context;
    public ContatoAdapter(List<Usuario> listaContatos, Context context) {
        this.contatos = listaContatos;
        this.context = context;
    }

    @NonNull
    @Override
    public MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext()).inflate(R.layout.contato_recycler_view, parent, false);
        return new MViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return contatos.size();
    }

    public class MViewHolder extends RecyclerView.ViewHolder{

        public MViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
