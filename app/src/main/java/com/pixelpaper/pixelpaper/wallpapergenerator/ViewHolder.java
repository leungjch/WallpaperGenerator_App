package com.pixelpaper.justin.wallpapergenerator;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pixelpaper.justin.wallpapergenerator.R;

public class ViewHolder {
    ImageView img;
    TextView desc;
    TextView title;

    public ViewHolder(View v)
    {
        img = (ImageView)v.findViewById(R.id.imageView1ID);
        title = (TextView)v.findViewById(R.id.nameTextViewID);
        desc = (TextView)v.findViewById(R.id.infoTextViewID);
    }

}
