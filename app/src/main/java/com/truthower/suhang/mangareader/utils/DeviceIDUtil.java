package com.truthower.suhang.mangareader.utils;/**
 * Created by Administrator on 2016/10/18.
 */

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.UUID;

/**
 * 作者：苏航 on 2016/10/18 14:21
 * 邮箱：772192594@qq.com
 */
public class DeviceIDUtil {
    public static String getDeviceID(Activity context) {
        String deviceID = SharedPreferencesUtils.getSharedPreferencesData(context, "deviceID");
        if (TextUtils.isEmpty(deviceID)) {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(context.TELEPHONY_SERVICE);
            String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
            String IMEI = tm.getDeviceId();
            String tmSerial = "" + tm.getSimSerialNumber();
            UUID uuid = new UUID(android_id.hashCode(), ((long) IMEI.hashCode() << 32) | tmSerial.hashCode());
            deviceID = MD5Util.getMD5String(IMEI + android_id + uuid.toString());
            Logger.d("IMEI:" + IMEI + "\n长度:" + IMEI.length() +
                    "\n" + "android_id:" + android_id + "\n长度:" + android_id.length() +
                    "\n" + "UUID:" + uuid.toString() + "\n长度:" + uuid.toString().length() +
                    "\n" + "deviceID:" + deviceID + "\n长度:" + deviceID.length());
            SharedPreferencesUtils.setSharedPreferencesData(context, "deviceID", deviceID);
        }
        return deviceID;
    }
}
