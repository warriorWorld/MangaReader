package com.truthower.suhang.mangareader.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ShareObjUtil {
    /**
     * @param context 上下文
     * @param name    文件名
     * @return
     * @description 获取object
     * @author Skyin_wd
     */
    public static Object getObject(Context context, String name) {
        Object obj = null;
        try {
            name=getLegalKey(name);
            FileInputStream fis = context.openFileInput(name);
            ObjectInputStream ois = new ObjectInputStream(fis);
            obj = ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * @param context 上下文
     * @param obj     对象
     * @param name    文件名
     * @description 保存 object
     * @author Skyin_wd
     */
    public static void saveObject(Context context, Object obj, String name) {
        try {
            name=getLegalKey(name);
            FileOutputStream fos = context.openFileOutput(name, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context 上下文
     * @param name    文件名
     * @return
     * @description 删除文件
     * @author Skyin_wd
     */
    public static void deleteFile(Context context, String name) {
        try {
            if (null == context || TextUtils.isEmpty(name)) {
                return;
            }
            name=getLegalKey(name);
            File f = new File(context.getFilesDir(), name);
            if (f.exists()) {
                f.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getLegalKey(String key) {
        return key.replaceAll(File.separator, "");
    }
}
