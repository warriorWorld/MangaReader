package com.truthower.suhang.mangareader.business.filter

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.truthower.suhang.mangareader.R
import com.truthower.suhang.mangareader.adapter.ReadMangaAdapter
import com.truthower.suhang.mangareader.base.BaseActivity
import com.truthower.suhang.mangareader.business.read.DepthPageTransformer
import com.truthower.suhang.mangareader.business.read.ReadMangaActivity
import com.truthower.suhang.mangareader.config.ShareKeys
import com.truthower.suhang.mangareader.eventbus.EventBusEvent
import com.truthower.suhang.mangareader.spider.FileSpider
import com.truthower.suhang.mangareader.utils.*
import com.truthower.suhang.mangareader.widget.dialog.*
import kotlinx.android.synthetic.main.activity_read_filter.*
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
        var filter = intent.getStringExtra("filter")
        if (TextUtils.isEmpty(filter)) {
            filter = SharedPreferencesUtils.getSharedPreferencesData(this, ShareKeys.FILTER_KEY)
        }
        loadBar?.show()
        Thread(Runnable {
            pathList = FileSpider.getInstance().getFilteredImages(this, filter)
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
        manga_viewpager.offscreenPageLimit = 1
        manga_viewpager.setPageTransformer(true, DepthPageTransformer())
        delete_iv!!.setOnClickListener(this)
        path_tv.setOnClickListener(this)
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
                    path_tv.text = pathList?.get(position)
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
            path_tv.visibility = View.GONE
        } else {
            delete_iv.visibility = View.VISIBLE
            path_tv.visibility = View.VISIBLE
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
            R.id.path_tv -> {
                val intent = Intent(this@ReadFilterActivity, ReadMangaActivity::class.java)
                intent.putExtra("currentMangaName", "RandomManga")
                val pngFile = File(path_tv.text.toString().replace("file://", "")).parentFile
                val files = pngFile.listFiles()
                try {
                    files.sortBy { Integer.valueOf(ReplaceUtil.onlyNumber(it.name)) }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                val storyImgPathList = ArrayList<String>(files.map { "file://" + it.path }.filter {
                    (it.endsWith(".png") || it.endsWith(".jpg") ||
                            it.endsWith(".jpeg") || it.endsWith(".gif") ||
                            it.endsWith(".PNG") || it.endsWith(".JPG") ||
                            it.endsWith(".JPEG") || it.endsWith(".GIF"))
                })
                val pathListBundle = Bundle()
                pathListBundle.putSerializable("pathList", storyImgPathList)
                intent.putExtras(pathListBundle)
                startActivity(intent)
            }
        }
    }
}