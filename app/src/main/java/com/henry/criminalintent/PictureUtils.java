package com.henry.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class PictureUtils {
    public static Bitmap getScaleBitmap(String path, Activity activity){
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay()
                .getSize(size);
        return getScaleBitmap(path,size.x,size.y);
    }
    public static Bitmap getScaleBitmap(String path,int destWidth,int destHeight){
        //Read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        //Figure out how much to scale down by
        int inSampleSize = 1;
        if(srcHeight > destHeight || srcWidth > destWidth){
            float heightScale = srcHeight / destHeight ;
            float widthScale = srcWidth / destWidth;
            //用Math.round 四捨五入
            inSampleSize = Math.round(heightScale > widthScale ? heightScale : widthScale);
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        //Read in and create final bitmap
        return BitmapFactory.decodeFile(path,options);
    }
}
