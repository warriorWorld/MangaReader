package com.truthower.suhang.mangareader.listener;


public interface DownloadCallBack {
    /**
     * 请求数据成功之后的回调
     * <p>
     * 返回对象
     */
    public abstract void loadSucceed(final int page);

    /**
     * 请求数据失败之后的回调
     *
     * @param error 错误信息
     */
    public abstract void loadFailed(final String error, final int page);
}
