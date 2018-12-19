package com.fht.yuanzhuo.Adapter;

public class ContentModel {

    private int imageView;
    private String text;
    private int id;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public ContentModel(String text) {
        this.text = text;
    }

    public int getImageView() {
        return imageView;
    }

//    public void setImageView(int imageView) {
//        this.imageView = imageView;
//    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}