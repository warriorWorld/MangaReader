package com.truthower.suhang.mangareader.bean;/**
 * Created by Administrator on 2016/11/2.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.avos.avoscloud.AVUser;
import com.truthower.suhang.mangareader.business.user.LoginActivity;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.utils.ShareObjUtil;


/**
 * 作者：苏航 on 2016/11/2 15:52
 * 邮箱：772192594@qq.com
 */
public class LoginBean extends BaseBean {
    private String userName;
    private String email;
    private boolean master;
    private boolean creator;

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

    public void setUserName(Context context, String userName) {
        this.userName = userName;
        saveLoginInfo(context, instance);
    }

    public void setEmail(Context context, String email) {
        this.email = email;
        saveLoginInfo(context, instance);
    }

    public String getUserName() {
        return userName;
    }

    public String getUserName(Activity context) {
        if (TextUtils.isEmpty(userName)) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            return "";
        }
        return userName;
    }


    public String getEmail() {
        return email;
    }

    public boolean isMaster() {
        return master;
    }

    public void setMaster(Context context, boolean master) {
        this.master = master;
        saveLoginInfo(context, instance);
    }

    public boolean isCreator() {
        return creator;
    }

    public void setCreator(Context context, boolean creator) {
        this.creator = creator;
        saveLoginInfo(context, instance);
    }
}
