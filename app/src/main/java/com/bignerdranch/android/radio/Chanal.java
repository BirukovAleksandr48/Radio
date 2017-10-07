package com.bignerdranch.android.radio;

public class Chanal {
    public String url;
    public String imgName;
    public String name;

    public Chanal(String url, String imgName, String name) {
        this.url = url;
        this.imgName = imgName;
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgURI) {
        this.imgName = imgURI;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
