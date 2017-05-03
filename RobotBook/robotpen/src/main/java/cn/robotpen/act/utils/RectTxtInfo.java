package cn.robotpen.act.utils;

import android.graphics.Point;
import android.graphics.Rect;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/28 0028.
 */

public class RectTxtInfo {
    private Rect rt;                 //保存字区域
    private String val;              //保存文字
    private int    type;            //保存类型     0   识别      1   识别后不正确，修改的       2    添加的是前一个字的区域x+1

    public void RectTxtInfo(RectTxtInfo rInfo)
    {
        rt = rInfo.rt;
        val = rInfo.val;
        type = rInfo.type;
    }

    public void setRect(Rect rt_)
    {
        rt = rt_;
    }
    public Rect getRect()
    {
        return rt;
    }

    public void setVal(String val_)
    {
        val = val_;
    }
    public String getVal()
    {
        return val;
    }

    public void setType(int type_)
    {
        type = type_;
    }
    public int getType()
    {
        return type;
    }

    static public ArrayList<RectTxtInfo> AddRectInfo_sort(ArrayList<RectTxtInfo> muliteRectInfo, RectTxtInfo rectInfo)
    {
        boolean insertFlag = false;
        ArrayList<RectTxtInfo> retRectInfo = new ArrayList<RectTxtInfo>();
        for(int i = 0; i < muliteRectInfo.size(); i++)
        {
            RectTxtInfo info_  = muliteRectInfo.get(i);
            if(rectInfo.getRect().left < info_.getRect().left && !insertFlag) {
                retRectInfo.add(rectInfo);
                insertFlag = true;
            }
            retRectInfo.add(info_);
        }
        if(!insertFlag)
            retRectInfo.add(rectInfo);
        return retRectInfo;
    }

    /**
     * @brief 判断两个轴对齐的矩形是否重叠
     * @param rc1 第一个矩阵的位置
     * @param rc2 第二个矩阵的位置
     * @return 两个矩阵是否重叠（边沿重叠，也认为是重叠）
     */
    static public boolean rectToRect(Rect rc1, Rect rc2)
    {
        boolean ret = false;
        Point pt = new Point();
        if (rc1.left + rc1.width()  > rc2.left && rc2.left + rc2.width() > rc1.left)
            pt.x = -1;
        else
        {
            pt.x = Math.min(Math.abs(rc1.right - rc2.left), Math.abs(rc2.right - rc1.left));
        }
        if( rc1.top + rc1.height() > rc2.top && rc2.top + rc2.height() > rc1.top )
            pt.y = -1;
        else
        {
            pt.y = Math.min(Math.abs(rc1.bottom - rc2.top), Math.abs(rc2.bottom - rc1.top));
        }
        if(pt.x == -1 && pt.y == -1)
            ret = true;
        return ret;
    }
}
