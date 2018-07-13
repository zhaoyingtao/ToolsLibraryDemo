package com.zyt.tools.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * Created by zyt on 2018/7/4.
 * 权限公共类
 */

public class PermissionsUtils {
    public static final int REQUEST_CODE = 10001;
    private static String[] PERMISSIONS_ARRAY = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE};
    private static PermissionsUtils mPermissionsUtils;

    public static PermissionsUtils getInstance() {
        if (mPermissionsUtils == null) {
            synchronized (PermissionsUtils.class) {
                if (mPermissionsUtils == null) {
                    mPermissionsUtils = new PermissionsUtils();
                }
            }
        }
        return mPermissionsUtils;
    }

    /**
     * 验证相机权限
     *
     * @param activity
     */
    public boolean verifyCameraPermissions(Activity activity) {
        /*******below android 6.0*******/
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        // Check if we have write permission
        int camera = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (camera != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 验证读取sd卡的权限
     *
     * @param activity
     */
    public boolean verifyMustHasPermissions(Activity activity) {
        /*******below android 6.0*******/
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int readPhoneState = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);
//        int recordPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
//        int locationPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED || readPhoneState != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_ARRAY, REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }

    // 显示缺失权限提示
    public void showMissingPermissionDialog(final Context mContext, final MissingPermissionCallBack callBack) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("帮助");
        builder.setMessage("当前应用缺少必要权限。\n请点击“设置”-“权限”-打开所需权限");
        builder.setCancelable(false);
        // 拒绝, 退出应用
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callBack.finishCurrentActivity();
                dialog.dismiss();
//                setResult(PERMISSIONS_DENIED);
//                finish();
            }
        });

        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings(mContext);
                dialog.dismiss();
            }
        });

        builder.show();
    }

    public interface MissingPermissionCallBack {
        void finishCurrentActivity();
    }

    // 启动应用的设置
    private void startAppSettings(Context mContext) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
        intent.setData(uri);
        mContext.startActivity(intent);
    }

    /**
     * 检测应用通知是否打开
     *
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean isNotificationEnabled(Context context) {

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
  /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 跳转至应用的权限设置界面
     *
     * @param mContext
     */
    public void goToAPPPermissionSetting(Context mContext) { //设置小米悬浮窗
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", mContext.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", mContext.getPackageName());
        }
        mContext.startActivity(localIntent);
    }

    /**
     * 通过包名 在应用商店打开应用
     *
     * @param mContext
     */
    public void openApplicationMarket(Context mContext) {
        try {
            String str = "market://details?id=" + "com.changdao.ttsing";
            Intent localIntent = new Intent(Intent.ACTION_VIEW);
            localIntent.setData(Uri.parse(str));
            mContext.startActivity(localIntent);
        } catch (Exception e) {
            // 打开应用商店失败 可能是没有手机没有安装应用市场
            e.printStackTrace();
            ToastUtils.getInstance().showToast("打开应用商店失败");
            // 调用系统浏览器进入商城
            String url = "http://app.mi.com/detail/163525?ref=search";
            openLinkBySystem(mContext, url);
        }
    }

    /**
     * 调用系统浏览器打开网页
     *
     * @param url 地址
     */
    private void openLinkBySystem(Context mContext, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        mContext.startActivity(intent);
    }

}
