package com.cartolino.chatall.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cartolino.chatall.R;
import com.cartolino.chatall.helper.Base64Customizada;
import com.cartolino.chatall.helper.LoadFotoPerfil;
import com.cartolino.chatall.model.Conversa;
import com.cartolino.chatall.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversasAdapter extends RecyclerView.Adapter<ConversasAdapter.ConversaViewHolder> {

    private Context context;
    private List<Conversa> conversaList;

    public ConversasAdapter(Context context, List<Conversa> conversaList) {
        this.context = context;
        this.conversaList = conversaList;
    }

    @NonNull
    @Override
    public ConversaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conversa_recycler_view, parent, false);
        return new ConversaViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversaViewHolder holder, int position) {

        Conversa conversa = conversaList.get(position);

            holder.lastMsg.setText(conversa.getUltimaMSG());

            Usuario usuario = conversa.getUsuarioExibicao();
            if(usuario != null) {
                holder.nome.setText(usuario.getNome());
                if(usuario.getFoto() != null) {
                    String url = usuario.getFoto();
                    Log.d("ConversasAdapter: ", " " + url);
                    new LoadFotoPerfil(context, url, holder.fotoPerfil);
                }else {
                    holder.fotoPerfil.setImageResource(R.drawable.padrao);
                }
            }

    }

    @Override
    public int getItemCount() {
        return conversaList.size();
    }

    public class ConversaViewHolder extends RecyclerView.ViewHolder {

         CircleImageView fotoPerfil;
         TextView nome, lastMsg;

        public ConversaViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoPerfil = (CircleImageView) itemView.findViewById(R.id.conversaCircleImage);
            nome = itemView.findViewById(R.id.conversaNome_do_Contato);
            lastMsg = itemView.findViewById(R.id.converaLastMsg);
        }
    }

}
