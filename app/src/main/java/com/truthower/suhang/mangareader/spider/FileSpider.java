package com.truthower.suhang.mangareader.spider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Message;
import android.text.TextUtils;

import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.eventbus.DownLoadEvent;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
import com.truthower.suhang.mangareader.listener.DownloadCallBack;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.utils.ImageUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * http://www.mangareader.net/
 */
public class FileSpider {
    private String webUrl = "file://";

    private FileSpider() {
    }

    private static volatile FileSpider instance = null;

    public static FileSpider getInstance() {
        if (instance == null) {
            //线程锁定
            synchronized (FileSpider.class) {
                //双重锁定
                if (instance == null) {
                    instance = new FileSpider();
                }
            }
        }
        return instance;
    }

    public ArrayList<MangaBean> getMangaList(final String path) {
        File f = new File(path);//第一级目录 reptile
        if (!f.exists()) {
            f.mkdirs();
        }
        int firstFileLength = f.toString().length() + 1;
        File[] files = f.listFiles();//第二级目录 具体漫画们
        if (null != files && files.length > 0) {
            ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
            for (int i = 0; i < files.length; i++) {
                Bitmap bp = null;
                MangaBean item = new MangaBean();

                String title = files[i].toString();
                title = title.substring(firstFileLength, title.length());

                item.setName(title);
                item.setUrl(files[i].toString());

                File[] files1 = files[i].listFiles();//第三级目录 某具体漫画内部
                if (null != files1 && files1.length > 0 && !files1[0].isDirectory()) {
                    //如果下一级目录就直接是图片文件 则显示第一张图片
                    item.setLocalThumbnailUrl(files1[0].toString());

                } else if (null != files1 && files1.length > 0 && files1[0].isDirectory()) {
                    //如果下一级目录不是图片而是文件夹们  二级文件夹
                    File[] files2 = files1[0].listFiles();//第四级目录 某具体漫画内部的内部
                    item.setLocalThumbnailUrl(files2[0].toString());
                } else if (null == files1 && !files[i].isDirectory()) {
                    //如果files1这一级就已经是单张图片了
                    item.setLocalThumbnailUrl(files[i].toString());
                } else {
                    //空文件夹
                }
                mangaList.add(item);
            }
            return mangaList;
        } else {
            return null;
        }
    }


    public static void deleteFile(String file) {
        if (file.contains("file://")) {
            file = file.substring(7, file.length());
        }
        File f = new File(file);
        deleteFile(f);
    }

    /**
     * 递归删除文件和文件夹 因为file.delete();只能删除空文件夹或文件 所以需要这么递归循环删除
     *
     * @param file 要删除的根目�?
     */
    public static void deleteFile(File file) {
        if (file.isFile() && file.exists()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                deleteFile(f);
            }
            file.delete();
        }
    }


    /**
     * 存图片 TODO
     *
     * @param b
     * @param bmpName
     * @param mangaName
     * @return
     * @childFolder 子文件夹名 因为漫画图片数量太大 所以在多一层子文件夹 自动建立
     */
    public static String saveBitmap(Bitmap b, String bmpName,
                                    String childFolder, String mangaName) {
        b = ImageUtil.imageZoom(b, 480);
        String path = Configure.storagePath + "/" + mangaName;
        String jpegName = path + "/" + childFolder + "/" + bmpName;
        String folderName = path + "/" + childFolder;
        File f = new File(folderName);
        if (!f.exists()) {
            // 如果不存在 就创建
            f.mkdirs();
        }
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jpegName;
    }

    public String getChildFolderName(int episode, int folderSize) {
        String res;
        int start = ((int) (episode / folderSize)) * folderSize;
        int end = start + folderSize - 1;
        res = start + "-" + end;
        return res;
    }

}
