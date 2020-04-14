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
import com.truthower.suhang.mangareader.listener.OnResultListener;
import com.truthower.suhang.mangareader.utils.ImageUtil;
import com.truthower.suhang.mangareader.utils.Logger;
import com.truthower.suhang.mangareader.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.DecimalFormat;
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
        try {
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
                        String thumbnailFilePath = files1[0].toString().substring(0,
                                files1[0].toString().lastIndexOf(File.separator));
                        thumbnailFilePath = thumbnailFilePath.replaceAll("file://", "");
                        thumbnailFilePath = thumbnailFilePath.replaceAll(Configure.storagePath, "");
                        thumbnailFilePath = thumbnailFilePath.replaceAll(File.separator, "_");
                        thumbnailFilePath = thumbnailFilePath.substring(1, thumbnailFilePath.length()) + ".png";
                        thumbnailFilePath = Configure.thumnailPath + File.separator + thumbnailFilePath;

                        File thumbnailFile = new File(thumbnailFilePath);
                        if (thumbnailFile.exists()) {
                            item.setUserThumbnailUrl(thumbnailFilePath);
                        } else {
                            item.setUserThumbnailUrl("");
                        }
                        //如果下一级目录就直接是图片文件 则显示第一张图片
                        item.setLocalThumbnailUrl(files1[0].toString());
                    } else if (null != files1 && files1.length > 0 && files1[0].isDirectory()) {
                        //如果下一级目录不是图片而是文件夹们  二级文件夹
                        File[] files2 = files1[0].listFiles();//第四级目录 某具体漫画内部的内部
                        try {
                            item.setLocalThumbnailUrl(files2[0].toString());
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
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
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void deleteFile(String file) {
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

    public String saveBitmap(Bitmap b, String bmpName,
                             String childFolder, String mangaName) {
        return saveBitmap(b, bmpName, childFolder, mangaName, null);
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
    public String saveBitmap(Bitmap b, String bmpName,
                             String childFolder, String mangaName, OnResultListener listener) {
//        b = ImageUtil.imageZoom(b, 600);
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
            if (null != listener) {
                listener.onFailed();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            if (null != listener) {
                listener.onFailed();
            }
        }
        if (null != listener) {
            listener.onFinish();
        }
        return jpegName;
    }

    public static void saveBitmap(Bitmap b, String folderName, String bmpName) {
        b = ImageUtil.imageZoom(b, 600);
        folderName = Configure.storagePath + "/" + folderName;
        String path = folderName + bmpName;
        File f = new File(folderName);
        if (!f.exists()) {
            // 如果不存在 就创建
            f.mkdirs();
        }
        try {
            FileOutputStream fout = new FileOutputStream(path);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getChildFolderName(int episode, int folderSize) {
        String res;
        int start = ((int) (episode / folderSize)) * folderSize;
        int end = start + folderSize - 1;
        res = start + "-" + end;
        return res;
    }

    /**
     * 网络获取图片
     *
     * @param imageUrl
     * @return
     */
    public Bitmap loadImageFromNetwork(String imageUrl, String folderName, String fileName) {
        Bitmap bitmap = null;
        try {
            InputStream is = new URL(imageUrl).openStream();
            bitmap = BitmapFactory.decodeStream(is);
            saveBitmap(bitmap, folderName, fileName);
        } catch (IOException e) {
        }
        return bitmap;
    }

    public byte[] File2byte(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public File byte2File(byte[] buf, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buf);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return file;
        }
    }

    /**
     * 复制文件目录
     *
     * @param srcDir  要复制的源目录 eg:/mnt/sdcard/DB
     * @param destDir 复制到的目标目录 eg:/mnt/sdcard/db/
     * @return
     */
    public boolean copyDir(String srcDir, String destDir) {
        File sourceDir = new File(srcDir);
        //判断文件目录是否存在
        if (!sourceDir.exists()) {
            return false;
        }
        //判断是否是目录
        if (sourceDir.isDirectory()) {
            File[] fileList = sourceDir.listFiles();
            File targetDir = new File(destDir);
            //创建目标目录
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            //遍历要复制该目录下的全部文件
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {//如果如果是子目录进行递归
                    copyDir(fileList[i].getPath() + "/",
                            destDir + fileList[i].getName() + "/");
                } else {//如果是文件则进行文件拷贝
                    copyFile(fileList[i].getPath(), destDir + fileList[i].getName());
                }
            }
            return true;
        } else {
            copyFileToDir(srcDir, destDir);
            return true;
        }
    }


    /**
     * 复制文件（非目录）
     *
     * @param srcFile  要复制的源文件
     * @param destFile 复制到的目标文件
     * @return
     */
    public boolean copyFile(String srcFile, String destFile) {
        try {
            InputStream streamFrom = new FileInputStream(srcFile);
            OutputStream streamTo = new FileOutputStream(destFile);
            byte buffer[] = new byte[1024];
            int len;
            while ((len = streamFrom.read(buffer)) > 0) {
                streamTo.write(buffer, 0, len);
            }
            streamFrom.close();
            streamTo.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    /**
     * 把文件拷贝到某一目录下
     *
     * @param srcFile
     * @param destDir
     * @return
     */
    public boolean copyFileToDir(String srcFile, String destDir) {
        File fileDir = new File(destDir);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        String destFile = destDir + "/" + new File(srcFile).getName();
        try {
            InputStream streamFrom = new FileInputStream(srcFile);
            OutputStream streamTo = new FileOutputStream(destFile);
            byte buffer[] = new byte[1024];
            int len;
            while ((len = streamFrom.read(buffer)) > 0) {
                streamTo.write(buffer, 0, len);
            }
            streamFrom.close();
            streamTo.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    /**
     * 移动文件目录到某一路径下
     *
     * @param srcFile
     * @param destDir
     * @return
     */
    public boolean moveFile(String srcFile, String destDir) {
        //复制后删除原目录
        if (copyDir(srcFile, destDir)) {
            deleteFile(new File(srcFile));
            return true;
        }
        return false;
    }

    /**
     * 获取指定文件大小
     */
    public long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            Logger.d("文件不存在!");
        }
        return size;
    }

    /**
     * 获取指定文件夹
     */
    public long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     */
    public String toFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }
}
