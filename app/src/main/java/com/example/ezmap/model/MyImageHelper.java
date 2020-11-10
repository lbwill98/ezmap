package com.example.ezmap.model;

import android.graphics.Bitmap;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class MyImageHelper {

    private Bitmap sourceImage, myImage;
    private ImageView imageView;
    private int   sourceWidth, sourceHeight, ivWidth, ivHeight, myX, myY, myWidth, myHeight;
    double ivWHRatio, zoomRatio;

    public MyImageHelper(Bitmap imageSource, ImageView imageView, int ivWidth, int ivHeight ) {
        this.sourceImage = imageSource;
        this.imageView = imageView;
        this.sourceWidth = sourceImage.getWidth();
        this.sourceHeight = sourceImage.getHeight();
        this.ivWidth = ivWidth;
        this.ivHeight = ivHeight;
        this.myX = 0;
        this.myY = 0;
        this.ivWHRatio = (double)this.ivWidth/(double)this.ivHeight;

        if(ivWHRatio >1.){
            this.myWidth = sourceImage.getWidth();
            this.myHeight = (int)((double)sourceImage.getHeight()/ivWHRatio);
        }else{
            this.myWidth = (int)(sourceImage.getWidth()*ivWHRatio);
            this.myHeight = sourceImage.getHeight();
        }
        this.myWidth = sourceImage.getWidth();
        this.myHeight = (int)((double)sourceImage.getHeight()/ivWHRatio);
        this.myImage = Bitmap.createBitmap(this.sourceImage,myX,myY,myWidth,myHeight);
        this.zoomRatio = 1.; //=sourceImage/myImage
    }

    public Bitmap getSourceImage() {
        return sourceImage;
    }

    public void setSourceImage(Bitmap sourceImage) {
        this.sourceImage = sourceImage;
    }

    public Bitmap getMyImage() {
        return myImage;
    }

    public void setMyImage(Bitmap myImage) {
        this.myImage = myImage;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public int getSourceWidth() {
        return sourceWidth;
    }

    public void setSourceWidth(int sourceWidth) {
        this.sourceWidth = sourceWidth;
    }

    public int getSourceHeight() {
        return sourceHeight;
    }

    public void setSourceHeight(int sourceHeight) {
        this.sourceHeight = sourceHeight;
    }

    public int getIvWidth() {
        return ivWidth;
    }

    public void setIvWidth(int ivWidth) {
        this.ivWidth = ivWidth;
    }

    public int getIvHeight() {
        return ivHeight;
    }

    public void setIvHeight(int ivHeight) {
        this.ivHeight = ivHeight;
    }

    public int getMyX() {
        return myX;
    }

    public void setMyX(int myX) {
        if(myX>=0 && myX<this.sourceWidth-this.myWidth){
            this.myX = myX;
        }
    }

    public int getMyY() {
        return myY;
    }

    public void setMyY(int myY) {
        if(myY>=0 && myY<this.sourceHeight-this.myHeight) {
            this.myY = myY;
        }
    }

    public int getMyWidth() {
        return myWidth;
    }

    public void setMyWidth(int myWidth) {
        this.myWidth = myWidth;
    }

    public int getMyHeight() {
        return myHeight;
    }

    public void setMyHeight(int myHeight) {
        this.myHeight = myHeight;
    }

    public double getIvWHRatio() {
        return ivWHRatio;
    }

    public void setIvWHRatio(float ivWHRatio) {
        this.ivWHRatio = ivWHRatio;
    }

    public double getZoomRatio() {
        return zoomRatio;
    }

    public void setZoomRatio(float zoomRatio) {
        this.zoomRatio = zoomRatio;
    }






    public void update(){
        this.myImage.recycle();
        this.myImage = Bitmap.createBitmap(this.sourceImage, myX, myY, myWidth, myHeight);
    }

    @Override
    public String toString() {
        return "sourceWidth=" + sourceWidth +
                ", sourceHeight=" + sourceHeight +
                ", ivWidth=" + ivWidth +
                ", ivHeight=" + ivHeight +
                ", myX=" + myX +
                ", myY=" + myY +
                ", myWidth=" + myWidth +
                ", myHeight=" + myHeight +
                ", ivWHRatio=" + ivWHRatio +
                ", zoomRatio=" + zoomRatio;
    }

    public void focus(int centerX, int centerY, float zoomRatio){
        /*boolean paramOK = zoomRatio>=1. &&
                zoomRatio<=4 &&
                ivToSourceImageX(centerX)*/
        this.zoomRatio = zoomRatio;
        if(ivWHRatio >1.){
            this.myWidth = (int)(sourceImage.getWidth()/zoomRatio);
            this.myHeight = (int)((double)sourceImage.getHeight()/ivWHRatio/zoomRatio);

        }else{
            this.myWidth = (int)(sourceImage.getWidth()*ivWHRatio/zoomRatio);
            this.myHeight = (int)(sourceImage.getHeight()/zoomRatio);

        }
        this.myX = centerX-myWidth/2;
        this.myY = centerY-myHeight/2;
        this.myImage = Bitmap.createBitmap(this.sourceImage, myX, myY, myWidth, myHeight);


        /*boolean paramOK = centerX-size/2>=0 && centerX+size/2<=sourceWidth && centerY-size/2>=0 && centerY+size/2<=sourceHeight;
        if(paramOK) {
            this.myX = centerX - size / 2;
            this.myY = centerY - size / 2;
            this.myWidth = size;
            this.myHeight = size;
            this.zoomRatio = (float)this.sourceHeight/this.myHeight;
            this.myImage = Bitmap.createBitmap(this.sourceImage, myX, myY, myWidth, myHeight);*/
    }




    public int ivToSourceImageX(int ivX){
        return((int)(ivX*this.myWidth/this.imageView.getWidth()));
    }

    public int ivToSourceImageY(int ivY){
        return((int)(ivY*this.myHeight/this.imageView.getHeight()));
    }


}






/*

    private Bitmap sourceImage, myImage;
    private int sourceWidth, sourceHeight, myX, myY, myWidth, myHeight;

    public MyImageHelper(Bitmap imageSource) {
        this.sourceImage = imageSource;
        this.myImage = imageSource;
        this.sourceWidth = sourceImage.getWidth();
        this.sourceHeight = sourceImage.getHeight();
        this.myX = 0;
        this.myY = 0;
        this.myWidth = sourceImage.getWidth();
        this.myHeight = sourceImage.getHeight();
    }

    public Bitmap getSourceImage() {
        return sourceImage;
    }

    public void setSourceImage(Bitmap sourceImage) {
        this.sourceImage = sourceImage;
    }

    public void setMyImage(Bitmap myImage) {
        this.myImage = myImage;
    }

    public int getSourceWidth() {
        return sourceWidth;
    }

    public void setSourceWidth(int sourceWidth) {
        this.sourceWidth = sourceWidth;
    }

    public int getSourceHeight() {
        return sourceHeight;
    }

    public void setSourceHeight(int sourceHeight) {
        this.sourceHeight = sourceHeight;
    }

    public int getMyX() {
        return myX;
    }

    public void setMyX(int myX) {
        this.myX = myX;
    }

    public int getMyY() {
        return myY;
    }

    public void setMyY(int myY) {
        this.myY = myY;
    }

    public int getMyWidth() {
        return myWidth;
    }

    public void setMyWidth(int myWidth) {
        this.myWidth = myWidth;
    }

    public int getMyHeight() {
        return myHeight;
    }

    public void setMyHeight(int myHeight) {
        this.myHeight = myHeight;
    }

    public Bitmap getMyImage() {
        return myImage;
    }

    public void focus(int centerX, int centerY, int size) {
        boolean paramOK = centerX - size / 2 >= 0 && centerX + size / 2 <= sourceWidth && centerY - size / 2 >= 0 && centerY + size / 2 <= sourceHeight;
        if (paramOK) {
            this.myX = centerX - size / 2;
            this.myY = centerY - size / 2;
            this.myWidth = size;
            this.myHeight = size;
            this.myImage = Bitmap.createBitmap(this.sourceImage, myX, myY, myWidth, myHeight);
        }
    }

    public void update(){
        this.myImage = Bitmap.createBitmap(this.sourceImage, myX, myY, myWidth, myHeight);
    }
*/

/*int[]coordinates = new int[2];
        imageView.getLocationOnScreen(coordinates);
        this.ivX = coordinates[0];
        this.ivY = coordinates[1];*/

 /*if(whRatio>=1){
            this.myWidth = sourceImage.getWidth();
            this.myHeight = (int)(sourceImage.getHeight()/whRatio);
        }else {
            this.myWidth = (int)(sourceImage.getWidth()*whRatio);
            this.myHeight = sourceImage.getHeight();
        }*/