package com.cartolino.chatall.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cartolino.chatall.R;
import com.cartolino.chatall.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContatoAdapter extends RecyclerView.Adapter<ContatoAdapter.MViewHolder> {

    private List<Usuario> contatos;
    private Context context;
    public ContatoAdapter(Context context, List<Usuario> listaContatos) {
        this.contatos = listaContatos;
        this.context = context;
    }

    @NonNull
    @Override
    public MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contato_recycler_view, parent, false);
        return new MViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull MViewHolder holder, int position) {

        Usuario usuario = contatos.get(position);

        holder.nome.setText(usuario.getNome());
        holder.email.setText(usuario.getEmail());
        if(usuario.getFoto() != null){
            Uri uri = Uri.parse(usuario.getFoto());
            Glide.with(context).load(uri).into(holder.foto);
        }else{
            holder.foto.setImageResource(R.drawable.padrao);
        }

    }

    @Override
    public int getItemCount() {
        return contatos.size();
    }

    public class MViewHolder extends RecyclerView.ViewHolder{

        CircleImageView foto;
        TextView nome, email;
        public MViewHolder(@NonNull View itemView) {
            super(itemView);
            foto = (CircleImageView) itemView.findViewById(R.id.circleFotoContatos);
            nome = itemView.findViewById(R.id.textNome_do_Contato);
            email = itemView.findViewById(R.id.textEmail_do_Contato);
        }

    }
}
