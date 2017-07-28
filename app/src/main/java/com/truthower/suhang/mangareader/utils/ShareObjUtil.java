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
	 * 
	 * @description 获取object
	 * @author Skyin_wd
	 * @param context
	 *            上下文
	 * @param name
	 *            文件名
	 * @return
	 */
	public static Object getObject(Context context, String name) {
		Object obj = null;
		try {
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
	 * 
	 * @description 保存 object
	 * @author Skyin_wd
	 * @param context
	 *            上下文
	 * @param obj
	 *            对象
	 * @param name
	 *            文件名
	 */
	public static void saveObject(Context context, Object obj, String name) {
		try {
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
	 * 
	 * @description 删除文件
	 * @author Skyin_wd
	 * @param context
	 *            上下文
	 * @param name
	 *            文件名
	 * @return
	 */
	public static void deleteFile(Context context, String name) {
		try {
			if (null == context || TextUtils.isEmpty(name)) {
				return;
			}
			File f = new File(context.getFilesDir(), name);
			if (f.exists()) {
				f.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
