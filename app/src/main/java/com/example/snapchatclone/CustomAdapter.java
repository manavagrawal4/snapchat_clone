package com.example.snapchatclone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
    private ArrayList<CustomItem> customItemArrayList=null;
    private OnItemClickListener mListener;
    public interface OnItemClickListener
    {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mListener = listener;
    }





    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item,parent,false);
        CustomViewHolder exampleViewHolder=new CustomViewHolder(view,mListener);
        return exampleViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        CustomItem currentItem=customItemArrayList.get(position);
        holder.textView1.setText(currentItem.getText1());
        holder.textView2.setText(currentItem.getText2());

    }

    @Override
    public int getItemCount() {
        return null!=customItemArrayList?customItemArrayList.size():0;
    }



    //Constructor
    public CustomAdapter(ArrayList<CustomItem> customList){
        customItemArrayList=customList;

    }




    public static class CustomViewHolder extends RecyclerView.ViewHolder{
        public TextView textView1;
        public TextView textView2;

        public CustomViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            textView1=itemView.findViewById(R.id.textView1);
            textView2=itemView.findViewById(R.id.textView2);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(listener!=null)
                    {
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION)
                        {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }
}
