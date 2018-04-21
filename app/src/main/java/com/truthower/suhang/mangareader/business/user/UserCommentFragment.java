package com.truthower.suhang.mangareader.business.user;

import android.text.TextUtils;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.truthower.suhang.mangareader.bean.CommentBean;
import com.truthower.suhang.mangareader.bean.LoginBean;
import com.truthower.suhang.mangareader.utils.LeanCloundUtil;
import com.truthower.suhang.mangareader.widget.dialog.SingleLoadBarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人信息页
 */
public class UserCommentFragment extends BaseCommentFragment implements View.OnClickListener {
    @Override
    protected void doGetData() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName(getActivity()))) {
            getActivity().finish();
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(getActivity());
        AVQuery<AVObject> query = new AVQuery<>("Comment");
        query.whereEqualTo("owner", LoginBean.getInstance().getUserName());
        query.limit(999);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(getActivity(), e)) {
                    commentList = new ArrayList<>();
                    if (null != list && list.size() > 0) {
                        CommentBean item;
                        for (int i = 0; i < list.size(); i++) {
                            item = new CommentBean();
                            item.setCreate_at(list.get(i).getCreatedAt());
                            item.setMangaName(list.get(i).getString("mangaName"));
                            item.setMangaUrl(list.get(i).getString("mangaUrl"));
                            item.setOo_number(list.get(i).getInt("oo_number"));
                            item.setXx_number(list.get(i).getInt("xx_number"));
                            item.setReply_user(list.get(i).getString("reply_user"));
                            item.setOwner(list.get(i).getString("owner"));
                            item.setComment_content(list.get(i).getString("comment_content"));
                            item.setObjectId(list.get(i).getObjectId());
                            commentList.add(item);
                        }
                    }
                    initListView();
                }
            }
        });
    }
}
