package com.truthower.suhang.mangareader.business.detail;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.LocalMangaListAdapter;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.business.read.ReadMangaActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.listener.OnSevenFourteenListDialogListener;
import com.truthower.suhang.mangareader.sort.FileComparator;
import com.truthower.suhang.mangareader.sort.FileComparatorAllNum;
import com.truthower.suhang.mangareader.sort.FileComparatorDirectory;
import com.truthower.suhang.mangareader.sort.FileComparatorWithBracket;
import com.truthower.suhang.mangareader.spider.FileSpider;
import com.truthower.suhang.mangareader.utils.ReplaceUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.dialog.ListDialog;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshBase;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshGridView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class LocalMangaDetailsActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        PullToRefreshBase.OnRefreshListener<GridView>, AdapterView.OnItemLongClickListener,
        EasyPermissions.PermissionCallbacks {
    private PullToRefreshGridView pullToRefreshGridView;
    private View emptyView;
    private ImageView emptyIV;
    private TextView emptyTV;
    private GridView mangaGV;
    private ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
    private LocalMangaListAdapter adapter;
    private TopBar topBar;
    private String filePath;
    private ArrayList<String> pathList;
    private String currentMangaName;
    private String[] selectOptions = {"设为封面", "删除该图片"};
    private String[] deleteOptions = {"选择删除", "区间删除"};
    private boolean isInEditMode = false;
    private boolean isInSectionDeleteMode = false;
    private boolean firstChoose = true;
    private int deleteStartPoint = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        filePath = intent.getStringExtra("filePath");
        if (TextUtils.isEmpty(filePath)) {
            this.finish();
        }

        initUI();
        initPullGridView();
        initGridView();

        initFile();

        currentMangaName = intent.getStringExtra("currentMangaName");
        baseTopBar.setTitle(currentMangaName);
        SharedPreferencesUtils.setSharedPreferencesData(this, ShareKeys.LAST_READ_MANGA_NAME,
                currentMangaName);
        SharedPreferencesUtils.setSharedPreferencesData(this, ShareKeys.LAST_READ_MANGA_PATH,
                filePath);
    }

    @AfterPermissionGranted(Configure.PERMISSION_FILE_REQUST_CODE)
    private void initFile() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            try {
                mangaList.clear();
                mangaList = FileSpider.getInstance().getMangaList(filePath);
                sortFiles();
                initGridView();
            } catch (Exception e) {

            }
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "我们需要写入/读取权限",
                    Configure.PERMISSION_FILE_REQUST_CODE, perms);
        }
    }

    private void sortFiles() {
        pathList = new ArrayList<>();
        for (int i = 0; i < mangaList.size(); i++) {
            pathList.add(mangaList.get(i).getLocalThumbnailUrl());
        }

        if (!isNextDirectory(mangaList.get(0).getUrl())) {
            //阅读页的前一页
            //获取第一张图片的路径
            String firstImgName = pathList.get(0);
            if (firstImgName.contains(".jpg") || firstImgName.contains(".png") || firstImgName.contains(".bmp")) {
                firstImgName = firstImgName.substring(0, firstImgName.length() - 1 - 3);
                Log.d("s", "裁剪后的字符串" + firstImgName);
            } else if (firstImgName.contains(".jpeg")) {
                firstImgName = firstImgName.substring(0, firstImgName.length() - 1 - 4);
                Log.d("s", "裁剪后的字符串" + firstImgName);
            }
            String[] arr = firstImgName.split("_");
            if (arr.length == 0) {
                arr = firstImgName.split("-");
            }

            if (pathList.get(0).contains("_") ||
                    pathList.get(0).contains("-")) {
                //正常的漫画
                if (arr.length != 3) {
                    return;
                }
                FileComparator comparator = new FileComparator();
                Collections.sort(pathList, comparator);
            } else if (pathList.get(0).contains("(")) {
                FileComparatorWithBracket comparator1 = new FileComparatorWithBracket();
                Collections.sort(pathList, comparator1);
            } else {
                String[] arri = firstImgName.split("/");
                //最终获得图片名字
                firstImgName = arri[arri.length - 1];
                try {
                    //用于判断是否位数字的异教徒写法
                    int isInt = Integer.valueOf(firstImgName);
                    //没抛出异常 所以是纯数字
                    FileComparatorAllNum comparator2 = new FileComparatorAllNum();
                    Collections.sort(pathList, comparator2);
                } catch (NumberFormatException e) {

                }
            }
            //将得到的排序结果给mangaList
            for (int i = 0; i < pathList.size(); i++) {
                mangaList.get(i).setLocalThumbnailUrl(pathList.get(i));
                mangaList.get(i).setName((i + 1) + "");
            }
        } else {
            //有很多话的漫画的文件夹层
            try {
                String firstDirectoryName = pathList.get(0);
                String[] arri = firstDirectoryName.split("/");

                firstDirectoryName = arri[arri.length - 2];
                String[] arri1 = firstDirectoryName.split("-");
                firstDirectoryName = arri1[0];
                firstDirectoryName = ReplaceUtil.onlyNumber(firstDirectoryName);

                //用于判断是否位数字的异教徒写法
                int isInt = Integer.valueOf(firstDirectoryName);
                //没抛出异常 所以是纯数字
                FileComparatorDirectory comparator4 = new FileComparatorDirectory();
                Collections.sort(mangaList, comparator4);
            } catch (Exception e) {
                //假设有异常就不是有很多话的漫画的文件夹层
            }
        }
    }

    private void initGridView() {
        if (null == adapter) {
            adapter = new LocalMangaListAdapter(this, mangaList);
            mangaGV.setAdapter(adapter);
            mangaGV.setOnItemClickListener(this);
            mangaGV.setOnItemLongClickListener(this);
            mangaGV.setEmptyView(emptyView);
//            mangaGV.setColumnWidth(100);
            mangaGV.setNumColumns(2);
            mangaGV.setVerticalSpacing(12);
            mangaGV.setGravity(Gravity.CENTER);
            mangaGV.setHorizontalSpacing(15);
        } else {
            adapter.setMangaList(mangaList);
            adapter.notifyDataSetChanged();
        }
        pullToRefreshGridView.onPullDownRefreshComplete();// 动画结束方法
        pullToRefreshGridView.onPullUpRefreshComplete();
    }


    private void initUI() {
        pullToRefreshGridView = (PullToRefreshGridView) findViewById(R.id.ptf_local_grid_view);
        topBar = (TopBar) findViewById(R.id.gradient_bar);
        topBar.setVisibility(View.GONE);
        mangaGV = (GridView) pullToRefreshGridView.getRefreshableView();
        emptyView = findViewById(R.id.empty_view);
        emptyIV = (ImageView) findViewById(R.id.empty_image);
        emptyIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initFile();
            }
        });
        emptyTV = (TextView) findViewById(R.id.empty_text);
        emptyTV.setText("没有内容~");
        baseTopBar.setRightText("更多");
        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {
                if (isInEditMode) {
                    isInEditMode = false;
                    resetEditMode();
                } else {
                    finish();
                }
            }

            @Override
            public void onRightClick() {
                if (isInEditMode) {
                    showDeleteAllThisDialog();
                } else {
                    showDeleteOptionsDialog();
                }
            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    private void resetEditMode() {
        adapter.setInEditMode(isInEditMode);
        adapter.notifyDataSetChanged();
        if (isInEditMode) {
            baseTopBar.setLeftText("取消");
            baseTopBar.setRightText("删除");
        } else {
            baseTopBar.setLeftText("");
            baseTopBar.setLeftBackground(R.drawable.back);
            baseTopBar.setRightText("更多");
        }
    }

    private void initPullGridView() {
        // 上拉加载更多
        pullToRefreshGridView.setPullLoadEnabled(false);
        // 滚到底部自动加载
        pullToRefreshGridView.setScrollLoadEnabled(false);
        pullToRefreshGridView.setOnRefreshListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        baseToast.showToast(mangaList.get(position).getUrl());
        if (isInEditMode) {
            if (isInSectionDeleteMode) {
                if (firstChoose) {
                    baseToast.showToast("请点击删除终点!");
                    deleteStartPoint = position;
                    firstChoose = false;
                    mangaList.get(deleteStartPoint).setChecked(true);
                    adapter.notifyDataSetChanged();
                } else {
                    for (int i = 0; i < position - deleteStartPoint + 1; i++) {
                        mangaList.get(deleteStartPoint + i).setChecked(true);
                    }
                    adapter.notifyDataSetChanged();
                }
            } else {
                mangaList.get(position).setChecked(!mangaList.get(position).isChecked());
                adapter.notifyDataSetChanged();
            }
        } else {
            Intent intent = null;
            if (isNextDirectory(mangaList.get(position).getUrl())) {
                intent = new Intent(LocalMangaDetailsActivity.this, LocalMangaDetailsActivity.class);
                intent.putExtra("filePath", mangaList.get(position).getUrl());
            } else {
//            baseToast.showToast("接下来就要进入看漫画页了" + mangaList.get(position).getLocalThumbnailUrl());
                intent = new Intent(LocalMangaDetailsActivity.this, ReadMangaActivity.class);
                Bundle pathListBundle = new Bundle();
                pathListBundle.putSerializable("pathList", pathList);
                intent.putExtras(pathListBundle);
                intent.putExtra("img_position", position);
            }
            intent.putExtra("currentMangaName", currentMangaName);
            if (null != intent) {
                startActivity(intent);
            }
        }
    }

    private boolean isNextDirectory(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return false;
        }
        if (f.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (!isNextDirectory(mangaList.get(0).getUrl())) {
            //阅读页的前一页
            showOptionsDialog(i);
        } else {
            showDeleteDialog(i);
        }
        return true;
    }

    private void showOptionsDialog(final int selectedPosition) {
        ListDialog listDialog = new ListDialog(this);
        listDialog.setOnSevenFourteenListDialogListener(new OnSevenFourteenListDialogListener() {
            @Override
            public void onItemClick(String selectedRes, String selectedCodeRes) {

            }

            @Override
            public void onItemClick(String selectedRes) {

            }

            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0:
                        File thumbnailFile = new File(Configure.thumnailPath);
                        if (!thumbnailFile.exists()) {
                            thumbnailFile.mkdirs();
                        }

                        String thumbnailFilePath = mangaList.get(selectedPosition).getLocalThumbnailUrl().substring(0,
                                mangaList.get(selectedPosition).getLocalThumbnailUrl().lastIndexOf(File.separator));
                        thumbnailFilePath = thumbnailFilePath.replaceAll("file://", "");
                        thumbnailFilePath = thumbnailFilePath.replaceAll(Configure.storagePath, "");
                        thumbnailFilePath = thumbnailFilePath.replaceAll(File.separator, "_");
                        thumbnailFilePath = thumbnailFilePath.substring(1, thumbnailFilePath.length()) + ".png";
                        //如果有就删
                        FileSpider.getInstance().deleteFile(Configure.thumnailPath + File.separator + thumbnailFilePath);
                        FileSpider.getInstance().copyFile
                                (mangaList.get(selectedPosition).getLocalThumbnailUrl().replaceAll("file://", ""),
                                        Configure.thumnailPath + File.separator + thumbnailFilePath);
                        ImageLoader.getInstance().clearDiskCache();
                        ImageLoader.getInstance().clearMemoryCache();
                        baseToast.showToast("设置成功");
                        break;
                    case 1:
                        showDeleteDialog(selectedPosition);
                        break;
                }
            }
        });
        listDialog.show();
        listDialog.setOptionsList(selectOptions);
    }

    private void showDeleteOptionsDialog() {
        ListDialog listDialog = new ListDialog(this);
        listDialog.setOnSevenFourteenListDialogListener(new OnSevenFourteenListDialogListener() {
            @Override
            public void onItemClick(String selectedRes, String selectedCodeRes) {

            }

            @Override
            public void onItemClick(String selectedRes) {

            }

            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0:
                        isInEditMode = true;
                        resetEditMode();
                        break;
                    case 1:
                        baseToast.showToast("请点击删除起点!");
                        isInSectionDeleteMode = true;
                        firstChoose = true;
                        isInEditMode = true;
                        resetEditMode();
                        break;
                }
            }
        });
        listDialog.show();
        listDialog.setOptionsList(deleteOptions);
    }

    private void showDeleteAllThisDialog() {
        MangaDialog deleteDialog = new MangaDialog(this);
        deleteDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                for (int i = 0; i < mangaList.size(); i++) {
                    if (mangaList.get(i).isChecked()) {
                        if (isNextDirectory(mangaList.get(i).getUrl())) {
                            //是文件夹就删这个路径的
                            FileSpider.getInstance().deleteFile(mangaList.get(i).getUrl());
                        } else {
                            FileSpider.getInstance().deleteFile(mangaList.get(i).getLocalThumbnailUrl());
                        }
                    }
                }
                isInEditMode = false;
                isInSectionDeleteMode = false;
                resetEditMode();
                initFile();
            }

            @Override
            public void onCancelClick() {

            }
        });
        deleteDialog.show();

        deleteDialog.setTitle("确定删除选中的所有文件吗?");
        deleteDialog.setOkText("删除");
        deleteDialog.setCancelText("算了");
        deleteDialog.setCancelable(true);
    }

    private void showDeleteDialog(final int i) {
        MangaDialog deleteDialog = new MangaDialog(this);
        deleteDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                if (isNextDirectory(mangaList.get(i).getUrl())) {
                    //是文件夹就删这个路径的
                    FileSpider.getInstance().deleteFile(mangaList.get(i).getUrl());
                } else {
                    FileSpider.getInstance().deleteFile(mangaList.get(i).getLocalThumbnailUrl());
                }
                initFile();
            }

            @Override
            public void onCancelClick() {

            }
        });
        deleteDialog.show();

        deleteDialog.setTitle("确定删除?");
        deleteDialog.setOkText("删除");
        deleteDialog.setCancelText("算了");
        deleteDialog.setCancelable(true);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
        initFile();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
        initFile();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_local;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        baseToast.showToast("已获得授权,请继续!");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        baseToast.showToast("没文件读取/写入授权,你让我怎么读取本地漫画?", true);
    }
}
