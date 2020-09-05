package com.cartolino.chatall.helper;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class LoadFotoPerfil {

    public LoadFotoPerfil(Context context, String url, ImageView view) {
        if(url != null){
            Uri uri = Uri.parse(url);
            Glide.with(context).load(uri).into(view);
        }

    }
}
