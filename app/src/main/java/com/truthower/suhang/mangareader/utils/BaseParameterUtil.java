package com.truthower.suhang.mangareader.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;


/**
 * Created by Administrator on 2017/11/11.
 */

public class BaseParameterUtil {
    private PackageManager manager;
    private PackageInfo info;
    public String EMPTY = "empty";

    private BaseParameterUtil() {
    }

    private static volatile BaseParameterUtil instance = null;

    public static BaseParameterUtil getInstance() {
        if (instance == null) {
            //线程锁定
            synchronized (BaseParameterUtil.class) {
                //双重锁定
                if (instance == null) {
                    instance = new BaseParameterUtil();
                }
            }
        }
        return instance;
    }

    private void init(Context context) {
        try {
            manager = context.getPackageManager();
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getAppVersionName(Context context) {
        if (null == manager || null == info) {
            init(context);
        }
        if (TextUtils.isEmpty(info.versionName)) {
            return EMPTY;
        }
        return info.versionName;
    }

    public int getAppVersionCode(Context context) {
        if (null == manager || null == info) {
            init(context);
        }
        return info.versionCode;
    }

    public String getDevice(Context context) {
        String device = SharedPreferencesUtils.getSharedPreferencesData(context,
                ShareKeys.DEVICE_KEY);
        if (TextUtils.isEmpty(device)) {
            device = DeviceIDUtil.getDeviceID(context);
            if (TextUtils.isEmpty(device)) {
                return EMPTY;
            }
            SharedPreferencesUtils.setSharedPreferencesData(context, ShareKeys.DEVICE_KEY,
                    device);
        }
        return device;
    }

    public String getCurrentWebSite(Context context) {
        String website = SharedPreferencesUtils.getSharedPreferencesData(context,
                ShareKeys.CURRENT_WEBSITE);
        if (TextUtils.isEmpty(website)) {
            website = DeviceIDUtil.getDeviceID(context);
            if (TextUtils.isEmpty(website)) {
                return Configure.websList[0];
            }
            SharedPreferencesUtils.setSharedPreferencesData(context, ShareKeys.CURRENT_WEBSITE,
                    website);
        }
        return website;
    }

    public void saveCurrentWebSite(Context context, String website) {
        SharedPreferencesUtils.setSharedPreferencesData(context, ShareKeys.CURRENT_WEBSITE,
                website);
    }
//    public String getDeviceToken() {
//        String deviceToken = SharedPreferencesUtils.getSharedPreferencesData(context,
//                ShareKeys.DEVICE_TOKEN_KEY);
//        if (TextUtils.isEmpty(deviceToken)) {
//            deviceToken = PushAgent.getInstance(context).getRegistrationId();
//            if (TextUtils.isEmpty(deviceToken)) {
//                return EMPTY;
//            }
//            SharedPreferencesUtils.setSharedPreferencesData(context, ShareKeys.DEVICE_TOKEN_KEY,
//                    deviceToken);
//        }
//        return deviceToken;
//    }

    public void saveDeviceToken(Context context, String token) {
        SharedPreferencesUtils.setSharedPreferencesData(context, ShareKeys.DEVICE_TOKEN_KEY,
                token);
    }

    public String getSystemVersion() {
        if (TextUtils.isEmpty(android.os.Build.VERSION.RELEASE)) {
            return EMPTY;
        }
        return android.os.Build.VERSION.RELEASE;
    }

    public String getPhoneModel() {
        if (TextUtils.isEmpty(android.os.Build.MODEL)) {
            return EMPTY;
        }
        return android.os.Build.MODEL;
    }

    public String getSource() {
        return "ANDROID";
    }
}
