package com.truthower.suhang.mangareader.business.detail;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.LocalRecyclerAdapter;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.business.read.ReadMangaActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.listener.OnRecycleItemLongClickListener;
import com.truthower.suhang.mangareader.listener.OnSevenFourteenListDialogListener;
import com.truthower.suhang.mangareader.sort.FileNameNumberUtil;
import com.truthower.suhang.mangareader.sort.FileNumberComparator;
import com.truthower.suhang.mangareader.spider.FileSpider;
import com.truthower.suhang.mangareader.utils.DisplayUtil;
import com.truthower.suhang.mangareader.utils.PermissionUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.dialog.ListDialog;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
import com.truthower.suhang.mangareader.widget.recyclerview.RecyclerGridDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class LocalMangaDetailsActivity extends BaseActivity implements
        EasyPermissions.PermissionCallbacks {
    private View emptyView;
    private ImageView emptyIV;
    private TextView emptyTV;
    private RecyclerView mangaRcv;
    private SwipeRefreshLayout mangaSrl;
    private ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
    private String filePath;
    private ArrayList<String> pathList;
    private String currentMangaName;
    private String[] selectOptions = {"设为封面", "删除该图片"};
    private String[] deleteOptions = {"选择删除", "区间删除"};
    private boolean isInEditMode = false;
    private boolean isInSectionDeleteMode = false;
    private boolean firstChoose = true;
    private int deleteStartPoint = 0;
    private LocalRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        filePath = intent.getStringExtra("filePath");
        if (TextUtils.isEmpty(filePath)) {
            this.finish();
        }

        initUI();
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
        if (PermissionUtil.hasStorageAccess(this)) {
            // Already have permission, do the thing
            // ...
            try {
                mangaList.clear();
                mangaList = FileSpider.getInstance().getMangaList(filePath);
                if (null == mangaList) {
                    mangaList = new ArrayList<MangaBean>();
                }
                sortFiles();
                initGridView();
            } catch (Exception e) {
                e.printStackTrace();
                initGridView();
            }
        } else {
            // Do not have permissions, request them now
            PermissionUtil.requestStorageAccess(this, Configure.PERMISSION_FILE_REQUST_CODE);
        }
    }

    private void sortFiles() {
        //阅读页要用的图片路径列表，先按原来的顺序填好，排序失败也不会让阅读页没东西可看
        pathList = new ArrayList<>();
        if (null == mangaList || mangaList.isEmpty()) {
            return;
        }
        for (int i = 0; i < mangaList.size(); i++) {
            pathList.add(mangaList.get(i).getLocalThumbnailUrl());
        }

        //排序规则：把文件名里的非数字字符都去掉，再比较剩下的数字（见FileNameNumberUtil）
        //整个列表里都没有数字的话就不排序（个别没数字的文件会被当成相等，留在原位置）
        if (isNextDirectory(mangaList.get(0).getUrl())) {
            //有很多话的漫画的文件夹层  按文件夹名里的数字排序
            if (!hasAnyNumber(false)) {
                return;
            }
            Collections.sort(mangaList, new FileNumberComparator());
        } else {
            //阅读页的前一页  按图片名里的数字排序
            if (!hasAnyNumber(true)) {
                return;
            }
            Collections.sort(mangaList, new FileNumberComparator(true));
            //图片页的标题就是页码
            for (int i = 0; i < mangaList.size(); i++) {
                mangaList.get(i).setName((i + 1) + "");
            }
        }

        //排序结果排的是整个mangaList，重新生成和它一一对应的pathList
        pathList.clear();
        for (int i = 0; i < mangaList.size(); i++) {
            pathList.add(mangaList.get(i).getLocalThumbnailUrl());
        }
    }

    /**
     * 列表里有没有带数字的文件名，没有的话就不用排序了
     */
    private boolean hasAnyNumber(boolean byThumbnail) {
        for (int i = 0; i < mangaList.size(); i++) {
            String path = byThumbnail ? mangaList.get(i).getLocalThumbnailUrl()
                    : mangaList.get(i).getUrl();
            if (FileNameNumberUtil.hasNumber(path)) {
                return true;
            }
        }
        return false;
    }

    private void initGridView() {
        try {
            if (null == mangaList || mangaList.size() <= 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
            if (null == adapter) {
                adapter = new LocalRecyclerAdapter(this);
                adapter.setList(mangaList);
                adapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
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
                                    int count = position - deleteStartPoint;
                                    if (count > 0) {
                                        for (int i = 0; i <= count; i++) {
                                            mangaList.get(deleteStartPoint + i).setChecked(true);
                                        }
                                    } else {
                                        for (int i = 0; i <= Math.abs(count); i++) {
                                            mangaList.get(deleteStartPoint - i).setChecked(true);
                                        }
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
                });
                adapter.setOnRecycleItemLongClickListener(new OnRecycleItemLongClickListener() {
                    @Override
                    public void onItemLongClick(int position) {
                        if (!isNextDirectory(mangaList.get(0).getUrl())) {
                            //阅读页的前一页
                            showOptionsDialog(position);
                        } else {
                            showDeleteDialog(position);
                        }
                    }
                });
                mangaRcv.setAdapter(adapter);
                ColorDrawable dividerDrawable = new ColorDrawable(0x00000000) {
                    @Override
                    public int getIntrinsicHeight() {
                        return DisplayUtil.dip2px(LocalMangaDetailsActivity.this, 8);
                    }

                    @Override
                    public int getIntrinsicWidth() {
                        return DisplayUtil.dip2px(LocalMangaDetailsActivity.this, 8);
                    }
                };
                RecyclerGridDecoration itemDecoration = new RecyclerGridDecoration(LocalMangaDetailsActivity.this,
                        dividerDrawable, true);
                mangaRcv.addItemDecoration(itemDecoration);
            } else {
                adapter.setList(mangaList);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mangaSrl.setRefreshing(false);
    }


    private void initUI() {
        mangaRcv = findViewById(R.id.manga_rcv);
        mangaRcv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mangaRcv.setFocusableInTouchMode(false);
        mangaRcv.setFocusable(false);
        mangaRcv.setHasFixedSize(true);
        mangaSrl = findViewById(R.id.manga_srl);
        mangaSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initFile();
            }
        });
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

    private boolean isNextDirectory(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
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
