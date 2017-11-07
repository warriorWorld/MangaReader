package com.truthower.suhang.mangareader.business.main;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.LocalMangaListAdapter;
import com.truthower.suhang.mangareader.base.BaseFragment;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.business.detail.LocalMangaDetailsActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.listener.OnEditResultListener;
import com.truthower.suhang.mangareader.sort.FileComparatorByTime;
import com.truthower.suhang.mangareader.spider.FileSpider;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
import com.truthower.suhang.mangareader.widget.dialog.MangaEditDialog;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshBase;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshGridView;
import com.truthower.suhang.mangareader.widget.wheelview.wheelselector.WheelSelectorDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class LocalMangaFragment extends BaseFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener,
        PullToRefreshBase.OnRefreshListener<GridView>,
        EasyPermissions.PermissionCallbacks {
    private View mainView;
    private PullToRefreshGridView pullToRefreshGridView;
    private View emptyView;
    private ImageView emptyIV;
    private TextView emptyTV;
    private GridView mangaGV;
    private ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
    private LocalMangaListAdapter adapter;
    private TopBar topBar;
    private String storagePath;
    private String[] fileNameOptions = {"independent", "story", "other(edit)"};

    private enum FileTypeEnum {
        Independent,
        Story
    }

    private WheelSelectorDialog optionsSelector;
    private final String INDEPENDENT_PATH = "Independent", STORY_PATH = "Story";
    private Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {
            switch (Integer.valueOf(msg.obj.toString())) {
                case 0:
                    MangaDialog dialog = new MangaDialog(getActivity());
                    dialog.show();
                    dialog.setTitle("文件移动完成!");
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.activity_local, null);
        initUI(mainView);
        initPullGridView();
        initGridView();

        initFilePath();
        initFile();
        return mainView;
    }

    private void initFilePath() {
        File parentPath = Environment
                .getExternalStorageDirectory();
        storagePath = parentPath.getAbsolutePath() + "/" + Configure.DST_FOLDER_NAME;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (Configure.isTest) {
            baseToast.showToast("local onHiddenChanged");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Configure.isTest) {
            baseToast.showToast("local onResume");
        }
    }

    @AfterPermissionGranted(Configure.PERMISSION_FILE_REQUST_CODE)
    public void initFile() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            // Already have permission, do the thing
            // ...
            mangaList.clear();
            mangaList = FileSpider.getInstance().getMangaList(storagePath);
            initGridView();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "我们需要写入/读取权限",
                    Configure.PERMISSION_FILE_REQUST_CODE, perms);
        }
    }

    private void initGridView() {
        if (null == adapter) {
            adapter = new LocalMangaListAdapter(getActivity(), mangaList);
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


    private void initUI(View v) {
        pullToRefreshGridView = (PullToRefreshGridView) v.findViewById(R.id.ptf_local_grid_view);
        topBar = (TopBar) v.findViewById(R.id.gradient_bar);
        topBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {

            }

            @Override
            public void onRightClick() {

            }

            @Override
            public void onTitleClick() {
                showOptionsSelector();
            }
        });
        mangaGV = (GridView) pullToRefreshGridView.getRefreshableView();
        emptyView = v.findViewById(R.id.empty_view);
        emptyIV = (ImageView) v.findViewById(R.id.empty_image);
        emptyIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initFile();
            }
        });
        emptyTV = (TextView) v.findViewById(R.id.empty_text);
        emptyTV.setText("还没有本地漫画哦~");
    }

    private void initPullGridView() {
        // 上拉加载更多
        pullToRefreshGridView.setPullLoadEnabled(true);
        // 滚到底部自动加载
        pullToRefreshGridView.setScrollLoadEnabled(false);
        pullToRefreshGridView.setOnRefreshListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        baseToast.showToast(mangaList.get(position).getUrl());
        String currentMangaName = mangaList.get(position).getName();
        Intent intent = new Intent(getActivity(), LocalMangaDetailsActivity.class);
        intent.putExtra("filePath", mangaList.get(position).getUrl());
        intent.putExtra("currentMangaName", currentMangaName);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        showDeleteDialog(i);
        return true;
    }

    private void sortAndRenameFile(String manganame) {
        String oldPath = storagePath + "/" + "download";
        String newPath = storagePath + "/" + manganame;
        String gifPath = storagePath + "/" + "GIFs";

        File f = new File(oldPath);
        File[] files = f.listFiles();
        ArrayList<File> fileArrayList = new ArrayList<File>();
        for (int i = 0; i < files.length; i++) {
            fileArrayList.add(files[i]);
        }
        Collections.sort(fileArrayList, new FileComparatorByTime());//通过重写Comparator的实现类

        //如果子目录不存在 建立一个子目录
        File folder = new File(newPath);
        if (!folder.exists()) {
            folder.mkdirs();
        } else {
            baseToast.showToast("该文件夹已存在,请重新命名!");
            return;
        }
        File gifFolder = new File(gifPath);
        if (!gifFolder.exists()) {
            gifFolder.mkdirs();
        }
        for (int i = 0; i < fileArrayList.size(); i++) {
            if (!fileArrayList.get(i).toString().contains("gif")) {
                File to = new File(newPath, manganame + "(" + i + ")" + ".jpg");

                fileArrayList.get(i).renameTo(to);
            } else {
                Long time = new Date().getTime();
                String timeString = time + "";
                timeString = timeString.substring(5);
                File to = new File(gifPath, "gif" + timeString + ".gif");

                fileArrayList.get(i).renameTo(to);
            }
        }
        baseToast.showToast("完成");
    }

    private void showOptionsSelector() {
        if (null == fileNameOptions || fileNameOptions.length == 0) {
            baseToast.showToast("没有筛选条件");
            return;
        }
        if (null == optionsSelector) {
            optionsSelector = new WheelSelectorDialog(getActivity());
            optionsSelector.setCancelable(true);
        }
        optionsSelector.setOnSingleSelectedListener(new WheelSelectorDialog.OnSingleSelectedListener() {

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes) {
            }

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes, String selectedTypeRes) {
            }

            @Override
            public void onOkBtnClick(int position) {
                String fileName;
                switch (position) {
                    case 0:
                        fileName = getFileName(FileTypeEnum.Independent);
                        sortAndRenameFile(fileName);
                        moveFolder(INDEPENDENT_PATH, fileName);
                        break;
                    case 1:
                        fileName = getFileName(FileTypeEnum.Story);
                        sortAndRenameFile(fileName);
                        moveFolder(STORY_PATH, fileName);
                        break;
                    case 2:
                        showSortAndRenameFilesDialog();
                        break;
                }
            }
        });
        optionsSelector.show();

        optionsSelector.initOptionsData(fileNameOptions);
    }

    private void moveFolder(final String folderName, final String fileName) {
        new Thread() {
            @Override
            public void run() {
                FileSpider.getInstance().moveFile(storagePath + "/" + fileName + "/",
                        storagePath + "/" + folderName + "/" + fileName + "/");

                Message msg = handler2.obtainMessage();
                msg.obj = 0;
                msg.sendToTarget();
            }
        }.start();
    }

    private String getFileName(FileTypeEnum fileType) {
        try {
            String filePath = "";
            switch (fileType) {
                case Independent:
                    filePath = storagePath + "/" + INDEPENDENT_PATH;
                    break;
                case Story:
                    filePath = storagePath + "/" + STORY_PATH;
                    break;
                default:
                    break;
            }
            File f = new File(filePath);
            if (!f.exists()) {
                f.mkdirs();
            }
            File[] files = f.listFiles();
            if (null == files || files.length == 0) {
                switch (fileType) {
                    case Independent:
                        return INDEPENDENT_PATH + "0";
                    case Story:
                        return STORY_PATH + "0";
                    default:
                        return "";
                }
            } else {
                int[] fileNums = new int[files.length];
                String replaceString = "";
                switch (fileType) {
                    case Independent:
                        replaceString = INDEPENDENT_PATH;
                        break;
                    case Story:
                        replaceString = STORY_PATH;
                        break;
                }
                for (int i = 0; i < files.length; i++) {
                    String numString = files[i].getName();
                    numString = numString.replaceAll(replaceString, "");
                    fileNums[i] = Integer.valueOf(numString);
                }
                int fileNum = getMaxNum(fileNums) + 1;
                return replaceString + fileNum;
            }
        } catch (NumberFormatException e) {
            return "";
        }
    }

    private int getMaxNum(int[] nums) {
        int maxNum = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] > maxNum) {
                maxNum = nums[i];
            }
        }
        return maxNum;
    }

    private void showSortAndRenameFilesDialog() {
        MangaEditDialog mangaEditDialog = new MangaEditDialog(getActivity());
        mangaEditDialog.setOnEditResultListener(new OnEditResultListener() {
            @Override
            public void onResult(String text) {
                sortAndRenameFile(text);
            }

            @Override
            public void onCancelClick() {

            }
        });
        mangaEditDialog.show();
        mangaEditDialog.setTitle("是否按修改时间重新排序?");
        mangaEditDialog.setOkText("是的");
        mangaEditDialog.setCancelText("算了");
    }

    private void showDeleteDialog(final int i) {
        MangaDialog deleteDialog = new MangaDialog(getActivity());
        deleteDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                FileSpider.getInstance().deleteFile(mangaList.get(i).getUrl());
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
        pullToRefreshGridView.onPullDownRefreshComplete();// 动画结束方法
        pullToRefreshGridView.onPullUpRefreshComplete();
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
