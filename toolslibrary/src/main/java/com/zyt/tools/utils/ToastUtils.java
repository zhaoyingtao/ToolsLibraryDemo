package com.zyt.tools.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zyt.tools.R;
import com.zyt.tools.ToolsConstant;

/**
 * Created by zyt on 2017/3/27.
 */

public class ToastUtils {
    private static ToastUtils mToastUtils;
    private Toast mToast;
    private int showPosition = Gravity.BOTTOM;

    public static ToastUtils getInstance() {
        if (mToastUtils == null) {
            synchronized (ToastUtils.class) {
                if (mToastUtils == null) {
                    mToastUtils = new ToastUtils();
                }
            }
        }
        return mToastUtils;
    }

    /**
     * 设置toast的位置
     *
     * @param showPosition Gravity.BOTTOM Gravity.CENTER
     */
    public void setToastPosition(int showPosition) {
        this.showPosition = showPosition;
    }

    /**
     * 显示Toast
     *
     * @param mess
     */
    public void showToast(String mess) {
        if (mToast == null) {
            mToast = Toast.makeText(ToolsConstant.applicationContext, mess, Toast.LENGTH_LONG);
            if (showPosition != Gravity.BOTTOM ){
                mToast.setGravity(showPosition, 0, 0);
            }
        } else {
            mToast.setText(mess);
        }
        mToast.show();
    }

    /**
     * 显示Toast
     *
     * @param messResID R.string.xxx
     */
    public void showToast(int messResID) {
        String messStr = ToolsConstant.applicationContext.getResources().getString(messResID);
        showToast(messStr);
    }

    /**
     * 显示Toast---在屏幕中间
     *
     * @param mess
     */
    public void showToastMiddle(String mess) {
        if (mToast == null) {
            mToast = Toast.makeText(ToolsConstant.applicationContext, mess, Toast.LENGTH_LONG);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.setText(mess);
        }
        mToast.show();
    }

    /**
     * 显示Toast
     *
     * @param context 可以为APPApplication的环境变量
     * @param mess
     * @param image   图片资源 R.mipmap.xxx 选用默认图片直接传-1
     */
    public void showToastUpImageViewDownText(Context context, String mess, int image) {
        Toast mToast = new Toast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
        ImageView imageView = view.findViewById(R.id.iv_toast);
        TextView textView = view.findViewById(R.id.tv_toast);
        if (image != -1) {
            imageView.setImageDrawable(context.getResources().getDrawable(image));
        }
        textView.setText(mess);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setView(view);
        mToast.show();
    }

}
