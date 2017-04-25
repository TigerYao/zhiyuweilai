package com.zhiyuweilai.tiger.robotbook.model;


import com.zhiyuweilai.tiger.robotbook.utils.MD5Util;

import cn.robotpen.utils.TimeUtil;

/**
 * Created by yaohu on 2017/4/20.
 */

public class BaseModel {

    public String getMiyao(){
        String time = TimeUtil.TimeMillisecond2Date(System.currentTimeMillis(),"YYYY/MM/dd");
        String miyao = "kfj3jfk" + time + "KJU893";
        try {
            return MD5Util.Bit32(miyao);
        } catch (Exception e) {
            e.printStackTrace();
            return cn.robotpen.utils.MD5Util.getMD5String(miyao);
        }
    }
}
