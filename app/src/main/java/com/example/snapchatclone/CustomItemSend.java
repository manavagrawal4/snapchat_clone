package com.example.snapchatclone;

import android.widget.ImageView;

public class CustomItemSend {

    private String text;
    private Integer position;
    private boolean isChecked = false;

    public CustomItemSend(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer pos){
        position=pos;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

}
