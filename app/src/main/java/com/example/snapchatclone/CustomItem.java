package com.example.snapchatclone;

public class CustomItem {
    private String mText1;
    private String mText2;
    private boolean isSelected = false;

    public CustomItem(String text1, String text2) {
        mText1 = text1;
        mText2 = text2;
    }

    public String getText1() {
        return mText1;
    }

    public String getText2() {
        return mText2;
    }

}
