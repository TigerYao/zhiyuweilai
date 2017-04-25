package com.zhiyuweilai.tiger.robotbook.model;

import java.util.Random;
import java.util.UUID;

/**
 * Created by yaohu on 2017/4/21.
 */

public class TestRquest extends BaseModel{

    /**
     * "stitle": "作文标题", "number":"作文编号",
     * "sanswer":"作文文体", "sgrade":"年级",
     * "smiyao":"秘钥", "sname":"姓名",
     * "snum":"学生编号", "swenti":"文体"
     */

    public String stitle;
    public String number = new Random(1000L).nextInt()+"";
    public String sanswer;
    public String sgrade = "三年级";
    public String smiyao = getMiyao();
    public String sname = "测试";
    public String snum = "000001";
    public String swenti = "记叙文";
}
