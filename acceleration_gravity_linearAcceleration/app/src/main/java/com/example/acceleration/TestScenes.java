package com.example.acceleration;

import android.widget.ArrayAdapter;

import junit.framework.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.WeakHashMap;

/**
 * Created by Mingchuan on 2015/4/20.
 */
public class TestScenes {
    ArrayList<String> wm = new ArrayList<String>();
    TestScenes(){
        wm.add("无");
        //基准场景
        wm.add("静止");
        wm.add("走路");
        wm.add("跑步");
        wm.add("上楼梯");

        //摔倒场景
        wm.add("绊倒");
        wm.add("滑倒");
        wm.add("摔倒");

        //排除场景
        wm.add("上扔手机");
        wm.add("摔手机");
        wm.add("问霸摔手机");
        wm.add("平抛手机");
        wm.add("扔手机");

        //以下场景仅为娱乐，诸君看完，注释之即可。 by DemoHn
        wm.add("桓神卖萌");
        wm.add("于老师小讲堂");
    }

    public ArrayList<String> getArray(){
        return wm;
    }

    public String getItem(int position){
        if(position < 0){
            return wm.get(0);
        }else if(position>wm.size()){
            return wm.get(0);
        }else{
            return wm.get(position);
        }
    }
}
