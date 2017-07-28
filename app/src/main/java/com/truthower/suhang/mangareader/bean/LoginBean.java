package com.truthower.suhang.mangareader.bean;/**
 * Created by Administrator on 2016/11/2.
 */

import android.content.Context;

import com.avos.avoscloud.AVUser;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.utils.ShareObjUtil;


/**
 * 作者：苏航 on 2016/11/2 15:52
 * 邮箱：772192594@qq.com
 */
public class LoginBean extends AVUser {

    private LoginBean() {
    }

    private static volatile LoginBean instance = null;

    public static LoginBean getInstance() {
        if (instance == null) {
            //线程锁定
            synchronized (LoginBean.class) {
                //双重锁定
                if (instance == null) {
                    instance = new LoginBean();
                }
            }
        }
        return instance;
    }

    public void setLoginInfo(Context context, LoginBean loginInfo) {
        instance = loginInfo;
        saveLoginInfo(context, loginInfo);
    }

    //这个最好只有在application类里调用一次(即刚进入应用时调用一次),其他情况直接用单例就好,调用这个效率太低了
    public static LoginBean getLoginInfo(Context context) {
        LoginBean res = (LoginBean) ShareObjUtil.getObject(context, ShareKeys.LOGIN_INFO_KEY);
        if (null != res) {
            return res;
        } else {
            return instance;
        }
    }

    private void saveLoginInfo(Context context, LoginBean userInfo) {
        ShareObjUtil.saveObject(context, userInfo, ShareKeys.LOGIN_INFO_KEY);
    }

    public void clean(Context context) {
        instance = null;
        ShareObjUtil.deleteFile(context, ShareKeys.LOGIN_INFO_KEY);
    }

}
