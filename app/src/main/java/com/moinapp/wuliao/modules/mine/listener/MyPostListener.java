package com.moinapp.wuliao.modules.mine.listener;

import com.moinapp.wuliao.commons.net.NetworkingListener;
import com.moinapp.wuliao.modules.wowo.model.PostInfo;

import java.util.List;

/**
 * Created by liujiancheng on 15/6/23.
 */
public interface MyPostListener extends NetworkingListener {
    public void getMyPostSucc(List<PostInfo> postInfoList);
    public void delMyPostSucc(int result);
}
