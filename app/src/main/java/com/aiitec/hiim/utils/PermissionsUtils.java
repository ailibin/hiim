package com.aiitec.hiim.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.aiitec.hiim.im.location.util.ToastUtil;


/**
 * Android 6.0权限管理
 * @author Anthony
 * @version 1.0
 * createTime 2017/5/18.
 */
public class PermissionsUtils {


    private Activity context;

    public PermissionsUtils(Activity context){
        this.context = context;
    }

    public void requestPermissions(int permissionsRequestCode, String... permissions){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            if(onPermissionsListener != null){
                onPermissionsListener.onPermissionsSuccess(permissionsRequestCode);
            }
            return;
        }
        if (ContextCompat.checkSelfPermission(context, permissions[0]) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(context,permissions, permissionsRequestCode);
        }
        else
        {
            if(onPermissionsListener != null){
                onPermissionsListener.onPermissionsSuccess(permissionsRequestCode);
            }
        }
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            if(onPermissionsListener != null){
                onPermissionsListener.onPermissionsSuccess(requestCode);
            }
        } else
        {
            // Permission Denied
            ToastUtil.show(context, "权限申请失败");
            if(onPermissionsListener != null){
                onPermissionsListener.onPermissionsFailure(requestCode);
            }
        }
    }

    private OnPermissionsListener onPermissionsListener;

    public void setOnPermissionsListener(OnPermissionsListener onPermissionsListener) {
        this.onPermissionsListener = onPermissionsListener;
    }

    public interface OnPermissionsListener{
        void onPermissionsSuccess(int requestCode);
        void onPermissionsFailure(int requestCode);
    }
}
