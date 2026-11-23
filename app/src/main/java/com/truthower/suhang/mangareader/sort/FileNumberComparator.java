package com.truthower.suhang.mangareader.sort;

import com.truthower.suhang.mangareader.bean.MangaBean;

import java.util.Comparator;

/**
 * 按文件名里的数字排序
 * <p>
 * 规则见 {@link FileNameNumberUtil}：把文件名里非数字的字符都去掉再比较数字大小，
 * 没有数字时不排序（返回0）。
 * <p>
 * 图片列表按图片路径排序（sortByThumbnail=true），
 * 章节文件夹列表按文件夹路径排序（sortByThumbnail=false）。
 */
public class FileNumberComparator implements Comparator<MangaBean> {
    private final boolean sortByThumbnail;

    public FileNumberComparator() {
        this(false);
    }

    public FileNumberComparator(boolean sortByThumbnail) {
        this.sortByThumbnail = sortByThumbnail;
    }

    @Override
    public int compare(MangaBean lhs, MangaBean rhs) {
        if (null == lhs || null == rhs) {
            return 0;
        }
        String l = sortByThumbnail ? lhs.getLocalThumbnailUrl() : lhs.getUrl();
        String r = sortByThumbnail ? rhs.getLocalThumbnailUrl() : rhs.getUrl();
        return FileNameNumberUtil.compareNumbers(l, r);
    }
}
