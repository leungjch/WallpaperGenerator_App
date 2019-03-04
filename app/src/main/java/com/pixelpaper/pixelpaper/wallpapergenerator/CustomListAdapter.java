package com.pixelpaper.justin.wallpapergenerator;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.pixelpaper.justin.wallpapergenerator.R;

import java.util.List;

public class CustomListAdapter extends ArrayAdapter {

    //to reference the Activity
    private final Activity context;


    public final List<Bitmap> imageList;

    public final List<String> nameList;


    public final List<String> infoList;

    public CustomListAdapter(Activity context, List<String> nameListParam, List<String> infoListParam, List<Bitmap> imageListParam){

        super(context, R.layout.listview_row , nameListParam);

        this.context=context;
        this.imageList = imageListParam;
        this.nameList = nameListParam;
        this.infoList = infoListParam;
    }

    public View getView(int position, View view, ViewGroup parent) {

        View row = view;
        ViewHolder holder = null;
        if (row==null)
        {
            LayoutInflater inflater=context.getLayoutInflater();
            row =inflater.inflate(R.layout.listview_row, null,true);
            holder  = new ViewHolder(row);
        row.setTag(holder);

        }
        else
        {
            holder = (ViewHolder)row.getTag();
        }
//        //this code gets references to objects in the listview_row.xml file
//        TextView nameTextField = (TextView) rowView.findViewById(R.id.nameTextViewID);
//        TextView infoTextField = (TextView) rowView.findViewById(R.id.infoTextViewID);
//        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView1ID);

//        //this code sets the values of the objects to values from the arrays
//        nameTextField.setText(nameList.get(position));
//        infoTextField.setText(infoList.get(position));
//        imageView.setImageResource(imageList.get(position));
//        Log.i("Inflating",Integer.toString(position));
//        holder.title.setText(nameList.get(position));
//        holder.desc.setText(infoList.get(position));
//        holder.img.setImageBitmap(imageList.get(position));
        if (position == (imageList.size()-1))
        {
            holder.title.setText("Wallpaper "+Integer.toString(position+1) + " (End of history)");
        }
        else
        {
            holder.title.setText("Wallpaper "+Integer.toString(position+1) + " (Scroll to view more)");
        }
        holder.desc.setText("");
        holder.img.setImageBitmap(imageList.get(position));

        return row;

    };

}
