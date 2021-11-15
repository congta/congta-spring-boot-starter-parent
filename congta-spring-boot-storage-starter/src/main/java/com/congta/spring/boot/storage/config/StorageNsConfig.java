package com.congta.spring.boot.storage.config;


/**
 * Created by zhangfucheng on 2021/7/1.
 */
public class StorageNsConfig {

    private String bucket;

    private int width;

    private int height;

    private String style;

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
