/**
  * Copyright 2017 bejson.com 
  */
package cn.robotpen.act.model;
import java.util.ArrayList;
import java.util.List;
/**
 * Auto-generated: 2017-05-02 19:45:33
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class ArticalResult {

    public String result;
    public String yqzishu;
    public String yqtitle;
    public String yqwenti;
    public String name;
    public String grade;
    public String number;
    public String title;
    public String content;
    public String wenti;
    public String score;
    public String bfscore;
    public String comments;
    public String level;
    public String gtype;
    public String dlshu;
    public String zishu;
    public String jvshu;
    public ArrayList<MeiList> MeiList;
    public ArrayList<Errorlist> ErrorList;

    public class Errorlist {
        public String EType;
        public String Elocation;
        public String Esentence;
        public String Edescription;

    }

    public class MeiList {
        public String Mxb;
        public String Mcontent;
    }
}