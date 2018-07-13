package com.zyt.tools.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zyt on 2018/1/25.
 * 关于BitMap的工具类
 */

public class BitMapUtils {
    private static BitMapUtils bitMapUtils;

    public static BitMapUtils init() {
        if (bitMapUtils == null) {
            synchronized (BitMapUtils.class) {
                if (bitMapUtils == null) {
                    bitMapUtils = new BitMapUtils();
                }
            }
        }
        return bitMapUtils;
    }

    /**
     * 将String 转为 Bitmap====方法一
     *
     * @param imagePath
     * @return Bimtap
     */
    public Bitmap getBitmapFromString(String imagePath) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
//获取资源图片
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//        Log.e("zyt","将String 转为 Bitmap ======"+bitmap);
        return bitmap;
    }

    /**
     * 获取View的Bitmap的方法
     * 自己在Canvas上画图，并得到Canvas上的Bitmap
     *
     * @param v
     * @return
     */
    public Bitmap loadBitmapFromView(View v) {
        if (v == null || v.getWidth() <= 0 || v.getHeight() <= 0) {
            return null;
        }
        Bitmap screenshot;
        screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(screenshot);
        canvas.translate(-v.getScrollX(), -v.getScrollY());//我们在用滑动View获得它的Bitmap时候，获得的是整个View的区域（包括隐藏的），如果想得到当前区域，需要重新定位到当前可显示的区域
        v.draw(canvas);// 将 view 画到画布上
        return screenshot;
    }

    /**
     * 拼接图片
     *
     * @param first  图片1
     * @param second 图片2
     */
    public Bitmap twoInOneBitmap(Bitmap first, Bitmap second) {
        float scale = ((float) first.getWidth()) / second.getWidth();
        second = scale(second, scale);
        int width = first.getWidth();
        int height = first.getHeight() + second.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(first, 0, 0, null);
        canvas.drawBitmap(second, 0, first.getHeight(), null);
        return result;
    }

    /**
     * 将资源文件mipmap／drawable转化为bitmap
     *
     * @param mContext
     * @param res      R.mipmap.XXX
     * @return
     */
    public Bitmap resToBitMap(Context mContext, int res) {
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), res);
        return bitmap;
    }

    /**
     * 图片bitmap转成string
     *
     * @param bitmap
     * @return
     */
    public String convertIconToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        String bitString = Base64.encodeToString(appicon, Base64.DEFAULT);
        return bitString;
    }

    /**
     * Bitmap放大的方法
     */
    private Bitmap scale(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    /**
     * 保存文件到指定路径
     *
     * @param context
     * @param bmp
     * @return
     */
    public boolean saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "customescreenshots";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();
            //把文件插入到系统图库
            //MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 读取图片资源
     */
    public Bitmap readBitmap(Resources res, int resID) {
        return readBitmap(res, resID, null);
    }

    /**
     * 读取图片资源
     *
     * @param res
     * @param resID
     * @param options
     * @return
     */
    public Bitmap readBitmap(Resources res, int resID, BitmapFactory.Options options) {
        if (res == null) {
            return null;
        }
        try {
            return BitmapFactory.decodeResource(res, resID, options);
        } catch (OutOfMemoryError e) {
            System.gc();
            return null;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * drawable转化为Bitmap
     * @param drawable
     * @return
     */
    public Bitmap drawableToBitmap(Drawable drawable) {

        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否则在View或者SurfaceView里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);

        return bitmap;
    }
    /**
     * 获得图片的后缀
     *
     * @param onlyBoundsOptions
     * @return
     */
    public String getPictureEnd(BitmapFactory.Options onlyBoundsOptions) {
        String pictureEnd = onlyBoundsOptions.outMimeType;//图片的后缀名
        String[] pictureEndArray = new String[2];
        if (!TextUtils.isEmpty(pictureEnd) && pictureEnd.contains("/")) {
            pictureEndArray = pictureEnd.split("/");
        }
        return pictureEndArray[1];
    }

}
