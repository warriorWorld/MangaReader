package com.truthower.suhang.mangareader.business.filter

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.text.ClipboardManager
import android.text.SpannableString
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.ViewPager.GONE
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.android.volley.Request
import com.android.volley.VolleyError
import com.insightsurfface.stylelibrary.keyboard.English26KeyBoardView.KeyBorad26Listener
import com.insightsurfface.stylelibrary.keyboard.KeyBoardDialog
import com.insightsurfface.stylelibrary.keyboard.LandscapeKeyBoardDialog
import com.insightsurfface.stylelibrary.listener.OnKeyboardChangeListener
import com.insightsurfface.stylelibrary.listener.OnKeyboardListener
import com.nostra13.universalimageloader.core.ImageLoader
import com.truthower.suhang.mangareader.R
import com.truthower.suhang.mangareader.adapter.ReadMangaAdapter
import com.truthower.suhang.mangareader.base.BaseActivity
import com.truthower.suhang.mangareader.base.TTSActivity
import com.truthower.suhang.mangareader.bean.RxDownloadChapterBean
import com.truthower.suhang.mangareader.bean.YoudaoResponse
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity
import com.truthower.suhang.mangareader.business.other.AboutActivity
import com.truthower.suhang.mangareader.business.other.KeyboardSettingActivity
import com.truthower.suhang.mangareader.business.read.DepthPageTransformer
import com.truthower.suhang.mangareader.business.read.HackyViewPager
import com.truthower.suhang.mangareader.business.threadpooldownload.ManageDownloadActivity
import com.truthower.suhang.mangareader.config.Configure
import com.truthower.suhang.mangareader.config.ShareKeys
import com.truthower.suhang.mangareader.db.DbAdapter
import com.truthower.suhang.mangareader.eventbus.EventBusEvent
import com.truthower.suhang.mangareader.listener.JsoupCallBack
import com.truthower.suhang.mangareader.listener.OnEditResultListener
import com.truthower.suhang.mangareader.listener.OnSpeakClickListener
import com.truthower.suhang.mangareader.spider.FileSpider
import com.truthower.suhang.mangareader.spider.SpiderBase
import com.truthower.suhang.mangareader.utils.*
import com.truthower.suhang.mangareader.volley.VolleyCallBack
import com.truthower.suhang.mangareader.volley.VolleyTool
import com.truthower.suhang.mangareader.widget.bar.TopBar
import com.truthower.suhang.mangareader.widget.bar.TopBar.OnTopBarClickListener
import com.truthower.suhang.mangareader.widget.bar.TopBar.OnTopBarLongClickListener
import com.truthower.suhang.mangareader.widget.dialog.*
import com.truthower.suhang.mangareader.widget.dragview.DragView
import com.truthower.suhang.mangareader.widget.shotview.ScreenShot
import com.truthower.suhang.mangareader.widget.shotview.ShotView
import com.youdao.sdk.app.LanguageUtils
import com.youdao.sdk.ydonlinetranslate.Translator
import com.youdao.sdk.ydtranslate.Translate
import com.youdao.sdk.ydtranslate.TranslateErrorCode
import com.youdao.sdk.ydtranslate.TranslateListener
import com.youdao.sdk.ydtranslate.TranslateParameters
import kotlinx.android.synthetic.main.activity_read_filter.*
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar.OnProgressChangeListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.util.*

/**
 * /storage/sdcard0/reptile/one-piece
 *
 *
 * Created by Administrator on 2016/4/4.
 */
class ReadFilterActivity : BaseActivity(), View.OnClickListener {
    private var adapter: ReadMangaAdapter? = null
    private var pathList: ArrayList<String>? = ArrayList()
    private var loadBar: ProgressDialog? = null
    private var currentPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
        initProgressBar()
        doGetPaths();
    }

    private fun doGetPaths() {
        loadBar?.show()
        Thread(Runnable {
            pathList = FileSpider.getInstance().getFilteredImages(this, SharedPreferencesUtils.getSharedPreferencesData(this, ShareKeys.FILTER_KEY))
            runOnUiThread(Runnable {
                loadBar?.dismiss()
                refresh()
            })
        }).start()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_read_filter
    }

    private fun initProgressBar() {
        loadBar = ProgressDialog(this@ReadFilterActivity)
        loadBar!!.setCancelable(false)
        loadBar!!.setMessage("稍等...")
    }

    /**
     * 在主线程中执行,eventbus遍历所有方法,就为了找到该方法并执行.传值自己随意写
     *
     * @param event
     */
    @Subscribe
    override fun onEventMainThread(event: EventBusEvent) {
        when (event.eventType) {
            EventBusEvent.ON_TAP_EVENT -> {
                val touchX = event.floatsMsg[0]
                val touchY = event.floatsMsg[1]
                Logger.d("photo tap :$touchX,$touchY")
                if (touchX > 0.4 && touchX < 0.6 && touchY > 0.4 && touchY < 0.6) {
                    VibratorUtil.Vibrate(this, 20)
                    toggleControlUI()
                }
            }
        }
    }

    private fun refresh() {
        initViewPager()
        read_progress_tv.text = "${currentPos + 1}/${pathList?.size}"
        showImgSize(currentPos)
    }

    private fun initUI() {
        manga_viewpager.offscreenPageLimit = 3
        manga_viewpager.setPageTransformer(true, DepthPageTransformer())
        delete_iv!!.setOnClickListener(this)
        hideBaseTopBar()
    }

    private fun showDeleteDialog(fileName: String) {
        val deleteDialog = MangaDialog(this)
        deleteDialog.setOnPeanutDialogClickListener(object : MangaDialog.OnPeanutDialogClickListener {
            override fun onOkClick() {
                FileSpider.getInstance().deleteFile(fileName)
                pathList!!.removeAt(currentPos)
                baseToast.showToast("已删除")
                initViewPager()
            }

            override fun onCancelClick() {}
        })
        deleteDialog.show()
        deleteDialog.setTitle("确定删除?")
        deleteDialog.setOkText("删除")
        deleteDialog.setCancelText("算了")
        deleteDialog.setCancelable(true)
    }

    private fun initViewPager() {
        if (null == adapter) {
            adapter = ReadMangaAdapter(this@ReadFilterActivity, pathList)
            manga_viewpager.adapter = adapter
            manga_viewpager.setOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                override fun onPageSelected(position: Int) {
                    currentPos = position
                    read_progress_tv.text = "${currentPos + 1}/${pathList?.size}"
                    path_tv.text=pathList?.get(position)
                    showImgSize(position)
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
        } else {
            adapter?.setPathList(pathList)
            adapter?.notifyDataSetChanged()
        }
    }

    private fun showImgSize(position: Int) {
        try {
            img_size_tv.text = FileSpider.getInstance().toFileSize(FileSpider.getInstance().getFileSize(File(pathList?.get(position)?.replace("file://".toRegex(), ""))))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun toggleControlUI() {
        if (delete_iv.isShown) {
            delete_iv.visibility = View.GONE
            path_tv.visibility= View.GONE
        } else {
            delete_iv.visibility = View.VISIBLE
            path_tv.visibility= View.VISIBLE
        }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.delete_iv -> {
                var file = pathList?.get(currentPos)
                if (file != null) {
                    if (file.contains("file://")) {
                        file = file.substring(7, file.length)
                    }
                    showDeleteDialog(file)
                }
            }
        }
    }
}