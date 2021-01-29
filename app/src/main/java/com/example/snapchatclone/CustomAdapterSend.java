package com.example.snapchatclone;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class CustomAdapterSend extends RecyclerView.Adapter<CustomAdapterSend.CustomViewHolder> {
    private static ArrayList<CustomItemSend> mUserList;
    public CustomAdapterSend(ArrayList<CustomItemSend> UserList) {
        mUserList = UserList;
    }


    @NonNull
    @Override
    public CustomAdapterSend.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.test_card,parent,false);
        CustomAdapterSend.CustomViewHolder exampleViewHolder=new CustomAdapterSend.CustomViewHolder(view);
        return exampleViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomAdapterSend.CustomViewHolder holder, final int position) {

        final CustomItemSend customItemSend = mUserList.get(position);
        holder.textView.setText(customItemSend.getText());
        mUserList.get(position).setPosition(position);
        holder.cardView.setCardBackgroundColor(mUserList.get(position).isChecked() ? Color.parseColor("#fe485a"):Color.parseColor("#ADEFD1") );
        holder.imageView.setVisibility(mUserList.get(position).isChecked() ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserList.get(position).setChecked(!mUserList.get(position).isChecked());
                holder.cardView.setCardBackgroundColor(mUserList.get(position).isChecked() ? Color.parseColor("#fe485a"):Color.parseColor("#ADEFD1") );
                holder.imageView.setVisibility(mUserList.get(position).isChecked() ? View.VISIBLE : View.GONE);
            }
        });
    }



    @Override
    public int getItemCount() {
        return null!=mUserList?mUserList.size():0;
    }






    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        private ImageView imageView;
        private CardView cardView;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.userTextView2);
            imageView = itemView.findViewById(R.id.checkImageView2);
            cardView=itemView.findViewById(R.id.cardViewSend);

        }

    }

    public static ArrayList<CustomItemSend> getAll() {
        return mUserList;
    }



    public static ArrayList<CustomItemSend> getSelected() {

        ArrayList<CustomItemSend> selected = new ArrayList<>();
        for (int i = 0; i < mUserList.size(); i++) {
            if (mUserList.get(i).isChecked()) {
                selected.add(mUserList.get(i));
            }
        }
        return selected;
    }


}





