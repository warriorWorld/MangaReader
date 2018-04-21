package com.truthower.suhang.mangareader.sort;

import com.truthower.suhang.mangareader.bean.CommentBean;

import java.io.File;
import java.util.Comparator;

/**
 * 这个是给文件排序并重命名的
 */
public class CommentComparatorByOO implements Comparator<CommentBean> {

    @Override
    public int compare(CommentBean item1, CommentBean item2) {
        if (item1.getOo_number() < item2.getOo_number()) {
            return 1;
        } else if (item1.getOo_number() > item2.getOo_number()) {
            return -1;
        } else {
            return 0;
        }
    }
}
