package com.moinapp.wuliao.modules.cosplay.model;

import android.support.annotation.NonNull;

import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.utils.StringUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by liujiancheng on 15/6/29.
 * 定义一个大咖秀资源的组成内容，包括手 脚 头 等
 */
public class CosplayThemes implements Serializable {
    private List<CosplayThemeInfo> head;
    private List<CosplayThemeInfo> body;
    private List<CosplayThemeInfo> hand;
    private List<CosplayThemeInfo> arm;
    private List<CosplayThemeInfo> foot;
    private List<CosplayThemeInfo> leg;
    private List<CosplayThemeInfo> feature;
    private List<CosplayThemeInfo> tool;
    private List<CosplayThemeInfo> other;
    private List<CosplayThemeInfo> other1;
    private List<CosplayThemeInfo> other2;
    private List<CosplayThemeInfo> other3;



    //得到所有存在的类型//
    public Map<String,List<CosplayThemeInfo>>  getAllExistInfo()
    {
        //List<CosplayThemeInfo> tlist[] = {}
        //List<List<CosplayThemeInfo>> list = new List<List<CosplayThemeInfo>>();
        Map<String,List<CosplayThemeInfo>> map = new LinkedHashMap<String,List<CosplayThemeInfo>>();

        if(head != null&&head.size()>0)
        {
            map.put("head",head);
        }

        if(body != null&&body.size()>0)
        {
            map.put("body",body);
        }

        if(hand != null&&hand.size()>0)
        {
            map.put("hand",hand);
        }

        if(arm != null&&arm.size()>0)
        {
            map.put("arm",arm);
        }
        if(foot != null&&foot.size()>0)
        {
            map.put("foot",foot);
        }
        if(leg != null&&leg.size()>0)
        {
            map.put("leg",leg);
        }
        if(feature!= null&& feature.size()>0)
        {
            map.put("feature",feature);
        }

        ILogger MyLog = LoggerFactory.getLogger("CosplayEditorActivity");

        return map;
    }

    public List<CosplayThemeInfo> getHead() {
        return head;
    }

    public void setHead(List<CosplayThemeInfo> head) {
        this.head = head;
    }

    public List<CosplayThemeInfo> getBody() {
        return body;
    }

    public void setBody(List<CosplayThemeInfo> body) {
        this.body = body;
    }

    public List<CosplayThemeInfo> getHand() {
        return hand;
    }

    public void setHand(List<CosplayThemeInfo> hand) {
        this.hand = hand;
    }

    public List<CosplayThemeInfo> getArm() {
        return arm;
    }

    public void setArm(List<CosplayThemeInfo> arm) {
        this.arm = arm;
    }

    public List<CosplayThemeInfo> getFoot() {
        return foot;
    }

    public void setFoot(List<CosplayThemeInfo> foot) {
        this.foot = foot;
    }

    public List<CosplayThemeInfo> getLeg() {
        return leg;
    }

    public void setLeg(List<CosplayThemeInfo> leg) {
        this.leg = leg;
    }

    public List<CosplayThemeInfo> getFeature() {
        return feature;
    }

    public void setFeature(List<CosplayThemeInfo> feature) {
        this.feature = feature;
    }

    public List<CosplayThemeInfo> getTool() {
        return tool;
    }

    public void setTool(List<CosplayThemeInfo> tool) {
        this.tool = tool;
    }

    public List<CosplayThemeInfo> getOther() {
        return other;
    }

    public void setOther(List<CosplayThemeInfo> other) {
        this.other = other;
    }

    public List<CosplayThemeInfo> getOther1() {
        return other1;
    }

    public void setOther1(List<CosplayThemeInfo> other1) {
        this.other1 = other1;
    }

    public List<CosplayThemeInfo> getOther2() {
        return other2;
    }

    public void setOther2(List<CosplayThemeInfo> other2) {
        this.other2 = other2;
    }

    public List<CosplayThemeInfo> getOther3() {
        return other3;
    }

    public void setOther3(List<CosplayThemeInfo> other3) {
        this.other3 = other3;
    }
}
