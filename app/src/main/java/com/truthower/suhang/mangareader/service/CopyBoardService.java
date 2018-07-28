package com.truthower.suhang.mangareader.service;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.truthower.suhang.mangareader.eventbus.DownLoadEvent;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
import com.truthower.suhang.mangareader.utils.Logger;
import com.truthower.suhang.mangareader.utils.MatchStringUtil;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
import com.truthower.suhang.mangareader.widget.toast.EasyToast;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2018/7/28.
 */

public class CopyBoardService extends Service implements ClipboardManager.OnPrimaryClipChangedListener {
    ClipboardManager clipboard;
    private String lastOne="";

    @Override
    public void onCreate() {
        super.onCreate();
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        clipboard.addPrimaryClipChangedListener(this);
    }

    @Override
    public void onPrimaryClipChanged() {
        if (clipboard.hasPrimaryClip() && clipboard.getPrimaryClip().getItemCount() > 0) {

            CharSequence addedText = clipboard.getPrimaryClip().getItemAt(0).getText();

            if (addedText != null && MatchStringUtil.isURL(addedText.toString()) && !lastOne.equals(addedText.toString())) {
                try {
                    lastOne = addedText.toString();
                    EventBus.getDefault().post(new EventBusEvent(addedText.toString(), EventBusEvent.COPY_BOARD_EVENT));
                } catch (Exception e) {
                    Logger.d(e + "");
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clipboard.removePrimaryClipChangedListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
