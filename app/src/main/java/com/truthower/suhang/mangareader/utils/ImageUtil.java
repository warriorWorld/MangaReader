package com.truthower.suhang.mangareader.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtil {
    /**
     * 旋转Bitmap
     *
     * @param b
     * @param rotateDegree
     * @return
     */
    public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) rotateDegree);
        Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
                b.getHeight(), matrix, false);
        return rotaBitmap;
    }

    /**
     * 读取图片旋转的角度
     *
     * @param filename
     * @return
     */
    public static void setPictureDegree(String filename, int degree) {
        try {
            if (degree == 0) {
                return;
            }

            int rotate = ExifInterface.ORIENTATION_UNDEFINED;
            switch (degree) {
                case 90:
                    rotate = ExifInterface.ORIENTATION_ROTATE_90;
                    break;
                case 180:
                    rotate = ExifInterface.ORIENTATION_ROTATE_180;
                    break;
                case 270:
                    rotate = ExifInterface.ORIENTATION_ROTATE_270;
                    break;
                default:
                    break;
            }

            ExifInterface exifInterface = new ExifInterface(filename);
            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION,
                    String.valueOf(rotate));
            exifInterface.saveAttributes();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载本地图片 http://bbs.3gstdy.com
     *
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        if (TextUtils.isEmpty(url))
            return null;
        try {
            // BitmapFactory.Options bitmapOptions = new
            // BitmapFactory.Options();
            // bitmapOptions.inSampleSize = 6;
            FileInputStream fis = new FileInputStream(url);
            // return BitmapFactory.decodeStream(fis, null, bitmapOptions);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 压缩图片到指定大小
     *
     * @param bitMap
     * @param maxSize 图片允许最大空间,单位:kb
     * @return
     */
    public static Bitmap imageZoom(Bitmap bitMap, double maxSize) {
        double bitmapSize = getBitmapSize(bitMap);
        // 判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
        while (bitmapSize > maxSize) {
            // 获取bitmap大小 是允许最大大小的多少倍
            double i = bitmapSize / maxSize;
            // 开始压缩 此处用到平方跟 将宽带和高度压缩掉对应的平方根倍
            // 保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小
            bitMap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i),
                    bitMap.getHeight() / Math.sqrt(i));
            bitmapSize = getBitmapSize(bitMap);
        }
        return bitMap;
    }

    /**
     * 获取图片大小
     *
     * @return 单位:kb.
     */
    private static double getBitmapSize(Bitmap bitMap) {
        // 将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitMap.compress(CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            // 将字节换成KB
            double mid = b.length / 1024;
            return mid;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 从SD卡中获取图片的方式,需要读取SD卡权限
     *
     * @param fileName
     * @return
     */
    public static Bitmap getImageFromSDFile(String fileName) {
        Bitmap image = null;
        File file = new File(fileName);
        if (file.exists()) {
            image = BitmapFactory.decodeFile(fileName);
        }
        return image;

    }

    /**
     * 从Assets中读取图片
     */
    public static Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 获取图片大小,更精确
     *
     * @param bitmap
     * @return
     */
    private static double getBitmapSize2(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount() / 1024;
        }
        return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
        // return bitmap.getWidth() * bitmap.getHeight() / 1024;
    }

    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    /**
     * 字符串转换成Bitmap
     *
     * @param string
     * @return
     */
    public static Bitmap stringtoBitmap(String string) {
        // 将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * Bitmap转换成字符串
     *
     * @param bitmap
     * @return
     */
    public static String bitmaptoString(Bitmap bitmap) {
        if (null == bitmap)
            return null;
        // 将Bitmap转换成字符串
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }

    public static String bitmaptoString(String path) {
        if (TextUtils.isEmpty(path))
            return null;
        return bitmaptoString(new File(path));
    }

    public static String bitmaptoString(File file) {
        if (null == file || !file.exists()) {
            return null;
        }
        FileInputStream in;
        String data = null;
        try {
            in = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length() + 100];
            int length = in.read(buffer);
            data = Base64.encodeToString(buffer, 0, length, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static Bitmap readBitmapFromFile(String filePath, int size) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        int inSampleSize = 1;

        if (srcHeight > size || srcWidth > size) {
            if (srcWidth < srcHeight) {
                inSampleSize = Math.round(srcHeight / size);
            } else {
                inSampleSize = Math.round(srcWidth / size);
            }
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(file.getAbsolutePath())));

    }
}
