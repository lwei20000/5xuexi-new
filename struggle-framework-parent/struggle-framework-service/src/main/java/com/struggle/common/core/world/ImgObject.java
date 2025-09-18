package com.struggle.common.core.world;

import lombok.Data;

@Data
public class ImgObject {
    /**
     图片名称
     */
    private String imgName;
    /**
        图片地址
     */
    private String imgUrl;
    /**
     图片高徒
     */
    private int width;
    /**
     图片宽度
     */
    private int height;



    public ImgObject(){

    }
    public ImgObject(String imgUrl, int width, int height){
        this.imgUrl = imgUrl;
        this.width = width;
        this.height = height;
    }
    public ImgObject(String imgName, String imgUrl, int width, int height){
        this.imgName = imgName;
        this.imgUrl = imgUrl;
        this.width = width;
        this.height = height;
    }
}
