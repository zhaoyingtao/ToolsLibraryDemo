package com.zyt.tools.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by zyt on 2017/11/20.
 */

public class ImageUtil {

    /**
     * 使用Glide加载图片
     *
     * @param mContext
     * @param url       图片地址
     * @param imageView 显示的ImageView
     */
    public static void imageLoad(Context mContext, String url, ImageView imageView) {
        imageLoad(mContext, url, imageView, false, -1);
    }

    /**
     * 使用Glide加载图片
     *
     * @param mContext
     * @param url        图片地址
     * @param imageView  显示的ImageView
     * @param isUseCache 是否使用缓存
     */
    public static void imageLoad(Context mContext, String url, ImageView imageView, boolean isUseCache) {
        imageLoad(mContext, url, imageView, isUseCache, -1);
    }

    /**
     * 使用Glide加载图片
     *
     * @param mContext
     * @param url        图片地址
     * @param imageView  显示的ImageView
     * @param defaultPic 默认图片
     */
    public static void imageLoad(Context mContext, String url, ImageView imageView, int defaultPic) {
        imageLoad(mContext, url, imageView, false, defaultPic);
    }

    /**
     * 使用Glide加载图片
     *
     * @param mContext
     * @param url        图片地址
     * @param imageView  显示的ImageView
     * @param isUseCache 是否使用缓存
     * @param defaultPic 默认图片
     */
    public static void imageLoad(Context mContext, String url, ImageView imageView, boolean isUseCache, int defaultPic) {
        if (imageView == null) {
            return;
        }
        RequestOptions options = new RequestOptions();
        options.placeholder(defaultPic);
        options.error(defaultPic);
        options.diskCacheStrategy(DiskCacheStrategy.DATA);
        if (isUseCache) {
            Glide.with(mContext).load(url).apply(options).into(imageView);
        } else {
            Glide.with(mContext).load(url).apply(options).into(imageView);
        }
    }

    /**
     * 加载本地图片
     *
     * @param mContext
     * @param url
     * @param imageView
     */
    public static void imageLoadLoaction(Context mContext, String url, ImageView imageView) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        RequestOptions options = new RequestOptions();
        options.diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(mContext).load(new File(url)).apply(options).into(imageView);
    }


    /**
     * 将String 转为 Bitmap
     *
     * @param imgStr
     * @return Bimtap
     */
    public static Bitmap getBitmapFromString(String imgStr) {
        Bitmap bitmap = null;
        if (TextUtils.isEmpty(imgStr)) {
            return bitmap;
        }
        byte[] bytes = Base64.decode(imgStr, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        if (bitmap == null) {
            Log.e("TAG", "------->" + (bitmap == null) + "----->" + imgStr);
        }
        return bitmap;
    }

    /**
     * 设置View的背景图------根据后台返回的图片url
     *
     * @param url
     * @param view
     */
    public static void setViewBackGroundWithUrl(String url, View view) {
        if (TextUtils.isEmpty(url) || view == null) {
            return;
        }
        new DownloadImageTask(view).execute(url);
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Drawable> {
        private View view;

        public DownloadImageTask(View view) {
            this.view = view;
        }

        protected Drawable doInBackground(String... urls) {
            return ImageUtil.changeImageUrlToDrawable(urls[0]);
        }

        protected void onPostExecute(Drawable result) {
            if (view != null) {
                view.setBackgroundDrawable(result);
            }
        }
    }

    /**
     * 获得视频的拍摄方向
     *
     * @param filePath 文件路径
     * @return
     */
    public static String getVideoRotation(String filePath) {
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        retr.setDataSource(filePath);
        String rotation = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        //rotation 0 横屏  90 竖屏
        return rotation;
    }

    /**
     * 将图片url转化为Drawable
     * 注意----------不能再主线程中使用此方法
     *
     * @param imageUrl
     * @return
     */
    public static Drawable changeImageUrlToDrawable(String imageUrl) {
        Drawable drawable = null;
        try {
            // 可以在这里通过文件名来判断，是否本地有此图片
            drawable = Drawable.createFromStream(new URL(imageUrl).openStream(), "image.jpg");
        } catch (IOException e) {
            Log.d("test", e.getMessage());
        }
        if (drawable == null) {
            Log.d("test", "null drawable");
        } else {
            Log.d("test", "not null drawable");
        }

        return drawable;
    }

    /**
     * 旋转图片方向
     *
     * @param bitmap
     * @param imgPath
     * @return
     */
    private static Bitmap rotatingImage(Bitmap bitmap, String imgPath) {
        ExifInterface exifInterface = null;
        Matrix matrix = null;
        try {
            exifInterface = new ExifInterface(imgPath);
            if (exifInterface == null) return bitmap;
            matrix = new Matrix();
            int angle = 0;
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    angle = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    angle = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    angle = 270;
                    break;
            }
            matrix.postRotate(angle);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

}
