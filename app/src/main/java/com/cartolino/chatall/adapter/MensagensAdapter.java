package com.cartolino.chatall.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cartolino.chatall.R;
import com.cartolino.chatall.helper.LoadFotoPerfil;
import com.cartolino.chatall.helper.UsuarioFirebase;
import com.cartolino.chatall.model.Mensagem;

import java.util.List;

public class MensagensAdapter extends RecyclerView.Adapter<MensagensAdapter.MyViewHolder> {

    Context context;
    List<Mensagem> listMensagens;
    public static final int TIPO_REMETENTE = 0;
    public static final int TIPO_DESTINATARIO = 1;

    public MensagensAdapter(Context c, List<Mensagem> lista) {
        this.context = c;
        this.listMensagens = lista;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;
        if (viewType == TIPO_REMETENTE){
            view = inflater.inflate(R.layout.adapter_mensagem_remetente, parent, false);
        }else if( viewType == TIPO_DESTINATARIO){
            view = inflater.inflate(R.layout.adapter_mensagem_destinatario, parent, false);
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Mensagem mensagem    = listMensagens.get(position);
        String msg           = mensagem.getMensagem();
        String imagem        = mensagem.getImagem();

        if(imagem != null){
            new LoadFotoPerfil(context, imagem, holder.imagem);
            holder.mensagem.setVisibility(View.GONE);
        } else {
            holder.mensagem.setText(msg);
            holder.imagem.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listMensagens.size();
    }

    @Override
    public int getItemViewType(int position) {
        Mensagem mensagem = listMensagens.get(position);
        String idUsuario = UsuarioFirebase.getIdUsuario();

        if(idUsuario.equals(mensagem.getIdUsuario())){
            return TIPO_REMETENTE;
        } else {
            return TIPO_DESTINATARIO;
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mensagem;
        ImageView imagem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mensagem    = itemView.findViewById(R.id.textMsgTexto);
            imagem      = itemView.findViewById(R.id.imageMsgFoto);
        }
    }
}
