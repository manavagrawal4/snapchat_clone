package com.example.snapchatclone;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MyListView extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> maintitle;
    private final ArrayList<String> subtitle;


    public MyListView(AppCompatActivity context, ArrayList<String> maintitle, ArrayList<String> subtitle) {
        super(context, R.layout.my_list,maintitle);
        this.context = context;
        this.maintitle = maintitle;
        this.subtitle = subtitle;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.my_list, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.nameTextView);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.emailTextView);

        try {


            titleText.setText(maintitle.get(position));
            subtitleText.setText(subtitle.get(position));

        }catch (Exception e){
            e.printStackTrace();
        }
        return rowView;

    };

}
