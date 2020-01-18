package com.truthower.suhang.mangareader.base;

public interface BaseView<T extends BasePresenter> {
    void setPresenter(T presenter);
}
