package cn.robotpen.act.utils;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import static android.graphics.Color.*;

public class MyView extends View {
    private static final float STROKE_WIDTH = 5f;
    private static final float  RECT_WIDTH = 1f;
    private Paint paint = new Paint();
    private Path mPath = new Path();
    ArrayList<Path> mPaths = new ArrayList<Path>();
    ArrayList<Float> mStrokes = new ArrayList<Float>();

    ArrayList<Path> paths_ = new ArrayList<Path>();                                //修改区域 时path()
    ArrayList<Float> strokes_ = new ArrayList<Float>();        //修改区域

    //笔画表
    /*
               6
          5         7
     4                  0
           3       1
               2
*/
    /*
    数组中
             0     点      0,1,2,
             1     横      0,070,010,
             2     竖      2,212,232,
             3     撇      3,43,23,34,
             4     捺      1,01,21,10,
             5     横折    02,
             6     提      7,07,67,70,76,
             7     横折钩  025,035,034,024,014,015,
             8     竖钩    25,24,
             9     横撇     033,
             10    横钩     03,
             11    竖弯钩   206,
             12    撇折     30,37,
             13    竖提     67,
             14    竖折     60,
             15    撇折     31,
             16    竖折弯钩 2025,2035,
             17    斜钩      17,
             18    横撇弯钩  0315,
             19    横折提    027,
             20    弯钩
             21    横折弯钩
             22    竖弯
             23    横折弯
             24    横折折折钩
             25    横斜钩
             26    横折折撇
             27    竖折撇
             28    竖折折
             29    横折折
             30    横折折折
     */


    //实现智能稿纸
    short intervelX = 18;    //每个字的x间距参考值
    short intervelY = 18;    //每个字的y间距参考值

    int     nBHStartPos = 0;   //记录笔画在point里的开始
    ArrayList<Short> mAllPoint = new ArrayList<Short>(); //保存整个笔点
    ArrayList<Float> mAllPointPress = new ArrayList<Float>(); //保存每个点的压力值
    Float      mPrePress = 0.0F;  //保存前一个压力值给抬笔用

    int       mRegTxtShow_Type = 0;      //0 显示原笔迹      1 修改版面  2 显示识别
    int       mShopTypeWidth = 0;        //0,    0;    1,     紫的宽度;

    Point evrMinLineP = new Point(0, 0);
    Point evrMaxLineP = new Point(0, 0);

    Rect evrLineRect = new Rect();                           //保存每个笔画的Rect
    Rect evrWordRect = new Rect();                           //保存每个字的Rect

    ArrayList<Rect> allLineRect = new ArrayList<Rect> ();                           //保存所有笔画的Rect
    ArrayList<Rect> allWordRect = new ArrayList<Rect> ();                           //保存所有字的Rect
    ArrayList<Point> aAllWordCount = new ArrayList<Point>();                       //保存字的笔画序列,point.x  起始划， point.y 落笔划 划数
    Point            nWordLinePos = new Point(-1, -1);                             //记录书写过程中起始笔画数到结束笔画数
    boolean         bIsAddEndWriteToCount = false;                              //最后一笔是否已经添加到字的笔画序列中

    short  minZGX = -1;                                   //保存整张最小x
    short  maxZGX = -1;                                  //保存整张最大x
    short  minZGY = -1;                                   //保存整张最小y
    short  maxZGY = -1;                                  //保存整张最大y
    boolean bNormalwrite = false;                      //是否正常顺序书写
    short    nWriteLineCount = -1;                                 //书写的行数
    short    nWordRectCount = -1;                            //书写的rect个数
    short    nPathCount = -1;                                //书写笔画的个数

    //根据书写的习惯自动计算稿纸的字大小，字行间距等。
    Rect      autoWordRect  = new Rect(0, 0, 0, 0);   //自动计算字大小   48
    int       autoWordJiange = 0;   //自动计算字间隔
    int       autoLineJiange = 24;   //自动计算字的行间隔
    int       autoLineStartx = 20;   //自动计算行开始位置
    int       autoLineStarty = 20;   //自动计算行开始位置
    int       autoLineEndx = 1000;     //自动计算行结束位置
    Rect       leftWordRect = new Rect(0, 0, 0, 0);    //记录最左Rect
    Rect       rightWordRect = new Rect(0, 0, 0, 0);   //记录最右Rect
    Rect       maxWordRect = new Rect(0, 0, 0, 0);    //记录最大字大小
    int       xyOffset = 0;             //画稿纸的偏移量
    int       nGzStyle = 2;             //0 无稿纸    1   划线稿纸     2   方格稿纸
    ArrayList<Integer> nRowWordCount =  new ArrayList<Integer>();    //记录每行的Word第几个
    ArrayList<Integer> nRowWordCountOffset =  new ArrayList<Integer>();    //记录每行的Word是第几个

    String sPointString = "";

    private float lastTouchX;
    private float lastTouchY;
    private final Rect dirtyRect = new Rect();
    private float lastStroke = -1;
    float variableWidthDelta = 0;

    private static final float STROKE_DELTA = 0.0001f; // for float comparison
    private static final float STROKE_INCREMENT = 0.01f; // amount to interpolate
    private float currentStroke = STROKE_WIDTH;
    private float targetStroke = STROKE_WIDTH;

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 3;
    private static final float PEN_WIDTH = 3f;
    private int m_nInvalidateCount = 0;

    public void setGZStyle(int nStyle)
    {
        nGzStyle = nStyle;
    }

    //对笔画区域处理公共函数
    //设置每个笔画的最小点
    public void SetMinPoint(Point pt)
    {
        if(evrMinLineP.x == 0 && evrMinLineP.y == 0)
        {
            evrMinLineP = pt;
        }
        else
        {
            if(pt.x < evrMinLineP.x)
                evrMinLineP.x = pt.x;
            if(pt.y < evrMinLineP.y)
                evrMinLineP.y = pt.y;
        }
    }

    //设置每个笔画的最大点
    public void SetMaxPoint(Point pt)
    {
        if(evrMaxLineP.x == 0 && evrMaxLineP.y == 0)
        {
            evrMaxLineP = pt;
        }
        else
        {
            if(pt.x > evrMaxLineP.x)
                evrMaxLineP.x = pt.x;
            if(pt.y > evrMaxLineP.y)
                evrMaxLineP.y = pt.y;
        }
    }

    //设置每个笔画的Rect
    public void SetEvrLineRect()
    {
        evrLineRect = new Rect(evrMinLineP.x, evrMinLineP.y, evrMaxLineP.x, evrMaxLineP.y);
    }

    //判断点靠近Rect的靠近度，-1，点落在Rect内
    public short GetPtToRect(Point pt, Rect rt)
    {
        short nRet = -1;
        if(pt.x >= rt.left &&  pt.x <= rt.right && pt.y >= rt.top &&  pt.y <= rt.bottom)
        {
            nRet = -1;
        }
        else
        {
            int x_ = Math.min( Math.abs(pt.x - rt.left),  Math.abs(pt.x - rt.right));
            int y_ = Math.min( Math.abs(pt.y - rt.top),  Math.abs(pt.y - rt.bottom));
            nRet = (short)Math.min(x_, y_);
        }
        return nRet;
    }

    /**
     * @brief 判断两个轴对齐的矩形是否重叠
     * @param rc1 第一个矩阵的位置
     * @param rc2 第二个矩阵的位置
     * @return 两个矩阵是否重叠（边沿重叠，也认为是重叠）
     */
    Point isOverlap(Rect rc1, Rect rc2)
    {
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
        return pt;
    }

    //判断两个笔画是否靠近的度，x,y -1，有交叉,x -1 x 有重叠   y -1  y 有重叠  x,y 距离
    public Point GetRectToRect(Rect rt1, Rect rt2)
    {
        Point pt = isOverlap(rt1, rt2);
        return pt;
    }

    //获取区域（笔画开始于结束的区域）
    public Rect GetRectWord()
    {
        int iStart = nWordLinePos.x;
        int iEnd = nWordLinePos.y;
        Rect rt = allLineRect.get(iStart);
        iStart+=1;
        for (; iStart <= iEnd; iStart++) {
            rt.union(allLineRect.get(iStart));
        }
        return rt;
    }

    //获取没有写完的最后一个字的区域
    public Rect GetEndRectWord()
    {
        Rect rt = new Rect(0, 0,0, 0);
        int iStart = nWordLinePos.x;
        int iEnd = nWordLinePos.y;
        if(iStart >=0 ) {
            rt = allLineRect.get(iStart);
            iStart += 1;
            for (; iStart <= iEnd; iStart++) {
                rt.union(allLineRect.get(iStart));
            }
        }
        return rt;
    }

    //判断一个新的笔画，是否落到当前字中
    public short GetRectToWord(Rect rt1)
    {
       // bIsAddEndWriteToCount = false;
        if(nPathCount == 0)
        {
            nWordLinePos.x = 0;
            nWordLinePos.y = 0;
        }
        else {
            int iStart = nWordLinePos.x;
            int iEnd = nWordLinePos.y;

            Point intevelXY = new Point(-1, -1);

            for (; iStart <= iEnd; iStart++)
            {
                Rect rt = allLineRect.get(iStart);
                Point pt = GetRectToRect(rt, rt1);
                if(pt.x == -1 && pt.y == -1)
                {
                    nWordLinePos.y = nPathCount;   //是一个word里
                    return 0;
                }
                else if(pt.x == -1 && pt.y < intervelY)
                {
                    nWordLinePos.y = nPathCount;   //是一个word里
                    return 0;
                }
                else if(pt.y == -1 && pt.x < intervelX)
                {
                    nWordLinePos.y = nPathCount;   //是一个word里
                    return 0;
                }
                else
                {
                    if(intevelXY.x == -1)
                    {
                        intevelXY = pt;
                    }
                    else
                    {
                        intevelXY.x = Math.min(intevelXY.x, pt.x);
                        intevelXY.y = Math.min(intevelXY.y, pt.y);
                    }
                }
            }

            if (intevelXY.x < intervelX && intevelXY.y < intervelY ) {
                nWordLinePos.y = nPathCount;   //是一个word里
            }
            else
            {
                //生产一个wordRect
                Rect rt_ = GetRectWord();
                //添加字Rect
                allWordRect.add(rt_);

                //自动计算稿纸
                SetAutoWordParam(rt_);

                //添加字的起始结束笔画
                if(!bIsAddEndWriteToCount) {
                    Point pt = new Point(nWordLinePos);
                    aAllWordCount.add(pt);
                }
                else
                {
                    Point pt = new Point(nWordLinePos);
                    aAllWordCount.remove(aAllWordCount.size()-1);
                    aAllWordCount.add(pt);
                }
                bIsAddEndWriteToCount = false;

                //设置新的一个字的位置
                nWordLinePos.x = nPathCount;
                nWordLinePos.y = nPathCount;
            }
        }
        return 0;
    }

    //获取所有字的笔画起始、结束位置
    public ArrayList<Point> GetAllWordRectLine()
    {
        if(!bIsAddEndWriteToCount) {
            aAllWordCount.add(nWordLinePos);
            bIsAddEndWriteToCount = true;
        }
        return aAllWordCount;
    }

    public ArrayList<Short> GetWordRectLine(int nIndex)
    {
        Point pt = aAllWordCount.get(nIndex);
        ArrayList<Short> nPointLine = new ArrayList<Short>();
        int nLineCount = 0;
        boolean isread = false;
        for(int i = 0; i < mAllPoint.size(); i++ )
        {
            if(nLineCount >=  pt.x && nLineCount <= pt.y)
            {
                if(!isread)
                {
                    if(pt.x > 0) //跳出  0
                    {
                        i++;
                    }
                    isread = true;
                }
                nPointLine.add(mAllPoint.get(i));
            }
            if(mAllPoint.get(i) == -1) {
                nLineCount++;
            }
            if(nLineCount > pt.y)
            {
                nPointLine.add((short)0);
                break;
            }
        }
        return nPointLine;
    }

    //保存笔画
    public void SaveAllLine()
    {
        //保存每笔
        SetEvrLineRect();
        allLineRect.add(evrLineRect);
        nPathCount++;

        //分析每个字并保存
        GetRectToWord(evrLineRect);

        evrMaxLineP = new Point(0, 0);
        evrMinLineP = new Point(0, 0);
    }

    //int       mRegTxtShow_Type = 0;      //0 显示原笔迹      1 显示原笔迹+识别   2 显示识别+原笔迹  3  显示识别
    //设置显示的方式
    public void SetRegTxtShowType(int type)
    {
        mRegTxtShow_Type = type;
        if(type == 1)
            mShopTypeWidth = GetWordRectWidth();
        else
            mShopTypeWidth = 0;

        invalidate();
    }

    //判断是否需要再次识别
    public boolean GetIsMustRegText()
    {
        if(paths_.size() > 0)
            return false;
        return true;
    }
    //获取显示的方式
    public int GetRegTxtShowType()
    {
        return mRegTxtShow_Type;
    }

    public MyView(Context context) {
        super(context);

        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(STROKE_WIDTH);
    }

    @SuppressWarnings("static-access")
    public MyView(Context context, AttributeSet atts) {
        super(context, atts);
        // TODO Auto-generated constructor stub
        setFocusable(true);
        paint.setColor(BLACK);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(STROKE_WIDTH);
    }

    public void clear() {
        mPath.reset();
        // Repaints the entire view.
        invalidate();
    }

    //找到一行字的平均高度
    public int FindRowWordPJHigh(int nStart, int nEnd)
    {
        int ret = 0;
        int iCount = 0;
        int nCount = 0;

        for(int i = nStart; i <= nEnd; i++)
        {
            Rect rt = allWordRect.get(i);
            if(rt.height() > 10) {
                iCount++;
                nCount += rt.top + rt.height()/2;
            }
        }
        if(iCount > 0)
            ret = nCount/iCount;
        else
        {
            Rect rt = allWordRect.get(nStart);
            ret = rt.top + rt.height()/2;
        }

        return ret;
    }

    //找到一行中最近的Rect
    public Rect FindRowMinRect(int nStart, int nEnd)
    {

        Rect minRect = allWordRect.get(nStart);

        for(int i = nStart+1; i <= nEnd; i++)
        {
            Rect rt = allWordRect.get(i);
            if(rt.left < minRect.left)
                minRect = rt;

        }

        return minRect;
    }

    //找到一行中最远的Rect
    public Rect FindRowMaxRect(int nStart, int nEnd)
    {

        Rect maxRect = allWordRect.get(nStart);

        for(int i = nStart+1; i <= nEnd; i++)
        {
            Rect rt = allWordRect.get(i);
            if(rt.left > maxRect.left)
                maxRect = rt;
        }
        return maxRect;
    }

    //找出一行中的最大字与最小字，计算出均匀分布的Rect w
    public int CalucRowWidth(int nStart, int nEnd)
    {
        int ret = 0;
        Rect minRect = allWordRect.get(nStart);
        Rect maxRect = allWordRect.get(nStart);
        for(int i = nStart+1; i <= nEnd; i++)
        {
            Rect rt = allWordRect.get(i);
            if(rt.left < minRect.left)
                minRect = rt;
            if(rt.left > maxRect.left)
                maxRect = rt;
        }
        int x1 = minRect.left + minRect.width() / 2;
        int x2 = maxRect.left + maxRect.width() / 2;
        ret = (x2 - x1) / (nEnd - nStart);
        return ret;
    }

    //动态计算字的Rect
    public void SetAutoWordParam(Rect rt) {
        /*
        //找最大、最小的位置
        if (autoLineStartx > rt.left)
            autoLineStartx = rt.left - xyOffset;
        if (autoLineStarty > rt.top)
            autoLineStarty = rt.top - xyOffset;
        if (autoLineEndx < rt.right)
            autoLineEndx = rt.right + xyOffset;
            */
        //找换行
        if (allWordRect.size() > 0) {
            if (allWordRect.size() >= 2) {
                Rect rt_ = allWordRect.get(allWordRect.size() - 2);
                int nOffsetPos = (rt.top - rt_.bottom);
                if (nOffsetPos > 20) {
                    nRowWordCount.add(new Integer(allWordRect.size() - 2));
                    nWriteLineCount++;

                    //前三行计算列
                    //if(nWriteLineCount < 3)
                    {
                        //int y1 = rt_.top + rt_.height() / 2;
                        int nStart = 0;
                        int nEnd = 0;
                        if(nWriteLineCount == 2)
                            nStart = 0;
                        else {
                            nStart = nRowWordCount.get(nWriteLineCount - 3)+1;
                        }
                        nEnd = nRowWordCount.get(nWriteLineCount - 2);

                        int y1 = FindRowWordPJHigh(nStart, nEnd);
                        int y2 = rt.top + rt.height() / 2;
                        nOffsetPos = y2 - y1 - autoWordRect.width();

                        int nTotal = 0;
                        int nPJPos = 0;

                        if (nRowWordCountOffset.size() > 0) {
                            for (int i = 0; i < nRowWordCountOffset.size(); i++) {
                                nTotal += nRowWordCountOffset.get(i);
                            }
                            nPJPos = nTotal / nRowWordCountOffset.size();
                        }

                        nTotal = 0;
                        if (nOffsetPos > autoWordRect.width() / 2)
                            nOffsetPos = nPJPos;
                        else if (nOffsetPos < 0)
                            nOffsetPos = 0;

                        nRowWordCountOffset.add(nOffsetPos);

                        for (int i = 0; i < nRowWordCountOffset.size(); i++) {
                            nTotal += nRowWordCountOffset.get(i);
                        }
                        nOffsetPos = nTotal / nRowWordCountOffset.size();

                        if (Math.abs(autoLineJiange - nOffsetPos) > 0 /* && autoLineJiange > nOffsetPos */) {
                            if (nOffsetPos < 0)
                                autoLineJiange = 0;
                            else
                                autoLineJiange = nOffsetPos;
                        }
                    }
                }
            }
        }
        //第一次书写
        if (maxWordRect.width() == 0 || maxWordRect.height() == 0) {
            maxWordRect = rt;
            int w = Math.max(rt.width(), rt.height());

            autoWordRect = new Rect(0, 0, maxWordRect.width() + 16, maxWordRect.width() + 16);
            autoLineJiange = autoWordRect.width() / 4;

            xyOffset = 8;
            leftWordRect = rt;

            autoLineStartx = leftWordRect.left - xyOffset;
            autoLineStarty = leftWordRect.top - xyOffset;

            autoLineEndx = autoLineStartx + autoWordRect.width() * 4;
            rightWordRect = new Rect(autoLineEndx - autoWordRect.width(), rt.top, autoLineEndx, rt.bottom);
            nWriteLineCount = 1;
        } else {
            /*int nPosStart = 0;
            int nPrePosStart = 0;
            int nSize = allWordRect.size();
            if(nWriteLineCount > 1) {
                nPosStart = nLineCount.get(nWriteLineCount - 2) + 1;
            }
            if(nWriteLineCount > 2)
            {
                nPrePosStart = nLineCount.get(nWriteLineCount - 3) + 1;
            }
            if((nSize - nPosStart) >= 2 && (nSize - nPosStart) > (nPosStart-nPrePosStart))
            {
                Rect rt1 = allWordRect.get(nPosStart);
                Rect rt2 = allWordRect.get(allWordRect.size() - 1);
                int x1 = rt1.left + rt1.width() / 2;
                int x2 = rt2.left + rt2.width() / 2;

                int w1 = (x2 - x1) / (nSize - nPosStart - 1);
                int offset = (w1 - rt1.width()) / 2;

                if (w1 != autoWordRect.width()) {
                    autoWordRect = new Rect(0, 0, w1, w1);
                    autoLineJiange = autoWordRect.width() / 2;
                    xyOffset = offset;
                    autoLineStartx = allWordRect.get(0).left - xyOffset*11/10;
                    autoLineStarty = allWordRect.get(0).top - xyOffset*5/8;
                }
            }
            */
            int w1 = 0;
            int offset = 0;
            if (nWriteLineCount == 1 && allWordRect.size() >= 2) {
                /*
                Rect rt1 = allWordRect.get(0);
                Rect rt2 = allWordRect.get(allWordRect.size() - 1);
                int x1 = rt1.left + rt1.width() / 2;
                int x2 = rt2.left + rt2.width() / 2;
                w1 = (x2 - x1) / (allWordRect.size() - 1);
                offset = (w1 - rt1.width()) / 2;
                */
                Rect rt1 = FindRowMinRect(0, allWordRect.size() - 1);
                w1 = CalucRowWidth(0, allWordRect.size() - 1);
                offset = (w1 - rt1.width()) / 2;
            }
            else if(nWriteLineCount == 2 && (allWordRect.size() - nRowWordCount.get(0) - 1) >=2 && (allWordRect.size() - nRowWordCount.get(0) - 1) >= (nRowWordCount.get(0)+1) )
            {
                int nPos = nRowWordCount.get(0) +1;
                /*
                Rect rt1 = allWordRect.get(nPos);
                Rect rt2 = allWordRect.get(allWordRect.size() - 1);
                int x1 = rt1.left + rt1.width() / 2;
                int x2 = rt2.left + rt2.width() / 2;

                w1 = (x2 - x1) / (allWordRect.size()  - nPos - 1);
                //w1 =  w1*19/20;
                offset = (w1 - rt1.width()) / 2;
                */
                Rect rt1 = FindRowMinRect(nPos, allWordRect.size() - 1);
                w1 = CalucRowWidth(nPos, allWordRect.size() - 1);
                offset = (w1 - rt1.width()) / 2;
            }
            if(w1 != 0)
            {
                if (w1 != autoWordRect.width()) {
                    autoWordRect = new Rect(0, 0, w1, w1);
                    //autoLineJiange = autoWordRect.width() / 2;
                    if(xyOffset != offset) {
                        xyOffset = offset;
                        autoLineStartx = leftWordRect.left - xyOffset;
                        autoLineStarty = leftWordRect.top - xyOffset;
                    }
                }
            }
        }
        //找最大、最小的位置
        if (leftWordRect.left > rt.left) {
            leftWordRect = new Rect(rt.left, leftWordRect.top, rt.left+autoWordRect.width(),leftWordRect.right);
            autoLineStartx = leftWordRect.left - xyOffset;
        }

        if (leftWordRect.top > rt.top) {
            leftWordRect = new Rect(leftWordRect.left, rt.top, leftWordRect.right, rt.top + autoWordRect.height());
            autoLineStarty = leftWordRect.top - xyOffset;
        }

        if (rightWordRect.right < rt.right) {
            rightWordRect = rt;
            autoLineEndx = rt.right;
        }
    }

    //获取多行的每行Rect
    public int GetWordRectWidth() {
        return autoWordRect.width();
    }
    //获取输入了多少行数
    public int GetWordRowCount() {
        return nWriteLineCount;
    }

    //获取多行的每行Rect
    public ArrayList<Rect> GetMuliteRect()
    {
        ArrayList<Rect> mMuliteRect = new ArrayList<Rect>();
        int left = autoLineStartx;

        int nCol = (autoLineEndx - autoLineStartx) / autoWordRect.width();
        if((autoLineEndx - autoLineStartx) % autoWordRect.width() != 0)
            nCol+=1;

        int right = autoLineEndx = left + (nCol) * autoWordRect.width();
        for(int i = 0; i < nWriteLineCount; i++)
        {

            Rect rt = new Rect(left, autoLineStarty + (autoWordRect.width()+autoLineJiange) *(i), right, autoLineStarty + (autoWordRect.width()+autoLineJiange) *(i) + autoWordRect.width());
            mMuliteRect.add(rt);
        }
        return mMuliteRect;
    }

    public void RefreshCavans()
    {
        invalidate();
    }

    public void DrawDraft(Canvas canvas)
    {
        /*
        Rect      autoWordRect  = new Rect(0, 0, 48, 48);   //自动计算字大小
        int       autoWordJiange = 0;   //自动计算字间隔
        int       autoLineJiange = 24;   //自动计算字的行间隔
        int       autoLineStartx = 20;   //自动计算行开始位置
        int       autoLineStarty = 20;
        int       autoLineEndx = 1000;     //自动计算行结束位置
        */
        if(maxWordRect.width() <= 0 || autoWordRect.width() <= 0)
            return;
        if(nGzStyle == 0)
            return;
        else if(nGzStyle == 1) //划线
        {
            int nDrafWidth = 1024*2;
            int nDraftHigh = 1280*2;
        }
        else {    //划方格
            int nDrafWidth = 1024*2;
            int nDraftHigh = 1280*2;
            int nCol = (autoLineEndx - autoLineStartx) / autoWordRect.width();
            if((autoLineEndx - autoLineStartx) % autoWordRect.width() != 0)
                nCol+=1;
            int nRow = (nDraftHigh - autoLineStarty) / (autoWordRect.height() + autoLineJiange+mShopTypeWidth);
            /*
            if(mRegTxtShow_Type == 0 || mRegTxtShow_Type == 1)
                paint.setColor(GREEN);
            else if(mRegTxtShow_Type == 2)
                paint.setColor(LTGRAY);
            */

            paint.setColor(GREEN);
            for (int j = 0; j < nRow; j++) {
                int x_ = autoLineStartx;
                int y_ = autoLineStarty + j * (autoWordRect.height() + autoLineJiange + mShopTypeWidth);
                Rect fr = new Rect(x_, y_, x_ + (nCol) * autoWordRect.width(), y_ + autoWordRect.height());
                canvas.drawRect(fr, paint);
                for (int i = 0; i < nCol; i++) {
                    Rect fr1 = new Rect(x_ + i * autoWordRect.width(), y_, x_ + (i + 1) * autoWordRect.width(), y_ + autoWordRect.height());
                    canvas.drawRect(fr1, paint);
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //canvas.drawColor(WHITE, PorterDuff.Mode.CLEAR);
        /*
        if(mRegTxtShow_Type != 3) {
            DrawDraft(canvas);
            if(mRegTxtShow_Type == 0 || mRegTxtShow_Type == 1)
                paint.setColor(DKGRAY);
            else if(mRegTxtShow_Type == 2)
                paint.setColor(LTGRAY);
            for (int i = 0; i < mPaths.size(); i++) {
                if (i < mStrokes.size())
                    paint.setStrokeWidth(mStrokes.get(i) * PEN_WIDTH);
                canvas.drawPath(mPaths.get(i), paint);
            }
        }
        */

        if(mRegTxtShow_Type == 0) {
            DrawDraft(canvas);
            paint.setColor(BLACK);
            for (int i = 0; i < mPaths.size(); i++) {
                if (i < mStrokes.size())
                    paint.setStrokeWidth(mStrokes.get(i) * PEN_WIDTH);
                canvas.drawPath(mPaths.get(i), paint);
            }
        }
        else if(mRegTxtShow_Type == 1)
        {
            DrawDraft(canvas);
            paint.setColor(BLACK);
            if(paths_.size() <= 0) {
                //点坐标的移动
                Path path_ = new Path();

                float oldPress_ = 0;

                //点在第几行
                int nRowCount_ = 0;
                ArrayList<Rect> muliteRowRect_ = GetMuliteRect();

                boolean isPendown = true;
                short x0 = 0;
                short y0 = 0;

                int nPreLineCount = 0;

                for (int i = 0; i < mAllPoint.size() / 2; i++) {
                    short x1 = mAllPoint.get(2 * i);
                    short y1 = mAllPoint.get(2 * i + 1);
                    Float press0 = mAllPointPress.get(i);

                    if (x1 != -1) {
                        if (isPendown) {
                            isPendown = false;

                            //落笔计算偏移了多少行
                            for (int n = 0; n < muliteRowRect_.size(); n++) {
                                Rect rt_ = muliteRowRect_.get(n);
                                if (y1 >= rt_.top && y1 <= rt_.bottom ) {
                                    //补丁 如果在Rect的末端，落笔，落笔的
                                    nRowCount_ = n;

                                    if(nPreLineCount - nRowCount_ == 1)   //突然跑到上一行时，可能有问题
                                    {
                                        if(y1 < (rt_.bottom - rt_.height()/4))
                                        {
                                            nRowCount_ = nPreLineCount;
                                        }
                                    }
                                    //记忆前一笔在那个行中
                                    nPreLineCount = nRowCount_;

                                    break;
                                }
                            }

                            //加上偏移量
                            y1 = (short) (y1 + nRowCount_ * mShopTypeWidth);

                            path_.moveTo(x1, y1);
                            path_.lineTo(x1, y1 + 1);
                            mStrokes.add(press0);
                            oldPress_ = press0;
                            x0 = x1;
                            y0 = y1;
                        } else {  //移动
                            //加上偏移量
                            y1 = (short) (y1 + nRowCount_ * mShopTypeWidth);

                            float dx = Math.abs(x1 - x0);
                            float dy = Math.abs(y1 - y0);

                            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                                if (oldPress_ != press0) {
                                    path_.quadTo(x0, y0, (x1 + x0) / 2, (y1 + y0) / 2);
                                    paths_.add(path_);

                                    path_ = new Path();
                                    path_.moveTo(x0, y0);

                                    strokes_.add(press0);
                                    oldPress_ = press0;
                                } else {
                                    path_.quadTo(x0, y0, (x1 + x0) / 2, (y1 + y0) / 2);
                                }
                                x0 = x1;
                                y0 = y1;
                            } else {
                                if (oldPress_ != press0) {
                                    path_.lineTo(x1, y1);
                                    paths_.add(path_);

                                    path_ = new Path();
                                    path_.moveTo(x1, y1);

                                    strokes_.add(press0);
                                    oldPress_ = press0;
                                } else {
                                    path_.lineTo(x1, y1);
                                }

                                x0 = x1;
                                y0 = y1;
                            }
                        }
                    } else   //抬笔path_
                    {
                        isPendown = true;
                        path_.lineTo(x0, y0);
                        paths_.add(path_);
                    }
                }
            }

            for (int i = 0; i < paths_.size(); i++) {
                if (i < strokes_.size())
                    paint.setStrokeWidth(strokes_.get(i) * PEN_WIDTH);
                canvas.drawPath(paths_.get(i), paint);
            }
        }

        //智能稿纸
        /*
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(RECT_WIDTH);

        //划笔画
        for(int j = 0; j < allLineRect.size(); j++)
        {
            Rect rt = allLineRect.get(j);
            canvas.drawRect(rt, paint);
        }
        */


        /*
        paint.setColor(Color.RED);
        paint.setStrokeWidth(RECT_WIDTH);
        //划字框
        for(int j = 0; j < allWordRect.size(); j++)
        {
            Rect rt = allWordRect.get(j);
            canvas.drawRect(rt, paint);
        }
        */


        //划最后没有落入字的框
        //Rect rt_ = GetEndRectWord();
       // canvas.drawRect(rt_, paint);

    }

    public void dowithWriteMove(float x, float y, float w) {
        float eventX = x;
        float eventY = y;

        //智能稿纸
        mAllPoint.add((short)x);
        mAllPoint.add((short)y);
        mAllPointPress.add(w);
        mPrePress = w;
        SetMaxPoint(new Point((short)x, (short)y));
        SetMinPoint(new Point((short)x, (short)y));

        variableWidthDelta = w;

        // mStrokes.add(variableWidthDelta);
        // targetStroke = variableWidthDelta;

        float dx = Math.abs(eventX - mX);
        float dy = Math.abs(eventY - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            if (lastStroke != variableWidthDelta) {
                mPath.quadTo(mX, mY, (eventX + mX) / 2, (eventY + mY) / 2);
                mPaths.add(mPath);

                String sPathPoint = "{\"x\":" + mX + ",\"y\":" + mY + "}]},";
                sPointString = sPointString.concat(sPathPoint);

                mPath = new Path();
                mPath.moveTo(mX, mY);

                sPathPoint = "{\"press\":" + w + ",\"xy\":[{\"x\":" + mX + ",\"y\":" + mY + "},";
                sPointString = sPointString.concat(sPathPoint);

                mStrokes.add(variableWidthDelta);
                lastStroke = variableWidthDelta;
            } else {
                mPath.quadTo(mX, mY, (eventX + mX) / 2, (eventY + mY) / 2);

                String sPathPoint = "{\"x\":" + mX + ",\"y\":" + mY + "},";
                sPointString = sPointString.concat(sPathPoint);
            }

            mX = eventX;
            mY = eventY;
        } else {

            if (lastStroke != variableWidthDelta) {
                mPath.lineTo(eventX, eventY);
                mPaths.add(mPath);

                String sPathPoint = "{\"x\":" + eventX + ",\"y\":" + eventY + "}]},";
                sPointString = sPointString.concat(sPathPoint);

                mPath = new Path();
                mPath.moveTo(eventX, eventY);

                sPathPoint = "{\"press\":" + w + ",\"xy\":[{\"x\":" + eventX + ",\"y\":" + eventY + "},";
                sPointString = sPointString.concat(sPathPoint);

                mStrokes.add(variableWidthDelta);
                lastStroke = variableWidthDelta;
            } else {
                mPath.lineTo(eventX, eventY);

                String sPathPoint = "{\"x\":" + eventX + ",\"y\":" + eventY + "},";
                sPointString = sPointString.concat(sPathPoint);
            }

            mX = eventX;
            mY = eventY;
        }
        // lastStroke = variableWidthDelta;
        if (m_nInvalidateCount++ % 3 == 0)
            invalidate();
    }

    public void dowithWriteUp() {

        //清空修改区域的笔迹
        if(paths_.size() > 0)   //清空笔迹
        {
            paths_.clear();
            strokes_.clear();
        }

        //识别笔画
        RegBihua();

        //智能稿纸
        mAllPoint.add((short)-1);
        mAllPoint.add((short)0);
        mAllPointPress.add(mPrePress);

        SaveAllLine();

        mPath.lineTo(mX, mY);
        mPaths.add(mPath);

        String sPathPoint = "{\"x\":" + mX + ",\"y\":" + mY + "},{\"x\":" + -1 + ",\"y\":" + 0 + "}]},";
        sPointString = sPointString.concat(sPathPoint);
        Log.i("lxz", sPointString);
        invalidate();
    }

    public ArrayList<Short> GetMyWrite()
    {
        return mAllPoint;
    }

    public void dowithWriteDown(float x, float y, float w) {
        float eventX = x;
        float eventY = y;

        nBHStartPos = mAllPoint.size();

        //智能稿纸
        mAllPoint.add((short)x);
        mAllPoint.add((short)y);
        mAllPointPress.add(mPrePress);
        mPrePress = w;

        SetMaxPoint(new Point((short)x, (short)y));
        SetMinPoint(new Point((short)x, (short)y));

        variableWidthDelta = w;
        mStrokes.add(variableWidthDelta);
        lastStroke = variableWidthDelta;

        String sPathPoint = "{\"press\":" + w + ",\"xy\":[{\"x\":" + x + ",\"y\":" + y + "},";
        sPointString = sPointString.concat(sPathPoint);

        resetDirtyRect(eventX, eventY);
        mPath.moveTo(eventX, eventY);
        mPath.lineTo(eventX, eventY + 1);
        mX = eventX;
        mY = eventY;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        int historySize = event.getHistorySize();
        return true;
        // lxz
        /*
         * switch (event.getAction()) {
         * case MotionEvent.ACTION_DOWN: {
         * resetDirtyRect(eventX, eventY);
         * // mPath.reset();
         * mPath.moveTo(eventX, eventY);
         * mX = eventX;
         * mY = eventY;
         * break;
         * }
         * case MotionEvent.ACTION_MOVE: {
         * if (event.getPressure()>=0.00 && event.getPressure()<0.05) {
         * variableWidthDelta = -2;
         * } else if (event.getPressure()>=0.05 && event.getPressure()<0.10) {
         * variableWidthDelta = -2;
         * } else if (event.getPressure()>=0.10 && event.getPressure()<0.15) {
         * variableWidthDelta = -2;
         * } else if (event.getPressure()>=0.15 && event.getPressure()<0.20) {
         * variableWidthDelta = -2;
         * } else if (event.getPressure()>=0.20 && event.getPressure()<0.25) {
         * variableWidthDelta = -2;
         * } else if (event.getPressure() >= 0.25 && event.getPressure()<0.30) {
         * variableWidthDelta = 1;
         * } else if (event.getPressure() >= 0.30 && event.getPressure()<0.35) {
         * variableWidthDelta = 2;
         * } else if (event.getPressure() >= 0.35 && event.getPressure()<0.40) {
         * variableWidthDelta = 3;
         * } else if (event.getPressure() >= 0.40 && event.getPressure()<0.45) {
         * variableWidthDelta = 4;
         * } else if (event.getPressure() >= 0.45 && event.getPressure()<0.60) {
         * variableWidthDelta = 5;
         * }
         * 
         * // if current not roughly equal to target
         * if( Math.abs(targetStroke - currentStroke) > STROKE_DELTA )
         * {
         * // move towards target by the increment
         * if( targetStroke > currentStroke)
         * {
         * currentStroke = Math.min(targetStroke, currentStroke + STROKE_INCREMENT);
         * }
         * else
         * {
         * currentStroke = Math.max(targetStroke, currentStroke - STROKE_INCREMENT);
         * }
         * 
         * }
         * mStrokes.add(currentStroke);
         * 
         * targetStroke = variableWidthDelta;
         * 
         * float dx = Math.abs(eventX - mX);
         * float dy = Math.abs(eventY - mY);
         * 
         * if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
         * if(lastStroke != variableWidthDelta)
         * {
         * mPath.lineTo(mX, mY);
         * 
         * mPath = new Path();
         * mPath.moveTo(mX,mY);
         * mPaths.add(mPath);
         * }
         * 
         * mPath.quadTo(mX, mY, (eventX + mX)/2, (eventY + mY)/2);
         * mX = eventX;
         * mY = eventY;
         * }
         * 
         * for (int i = 0; i < historySize; i++) {
         * float historicalX = event.getHistoricalX(i);
         * float historicalY = event.getHistoricalY(i);
         * expandDirtyRect(historicalX, historicalY);
         * }
         * break;
         * }
         * case MotionEvent.ACTION_UP: {
         * for (int i = 0; i < historySize; i++) {
         * float historicalX = event.getHistoricalX(i);
         * float historicalY = event.getHistoricalY(i);
         * expandDirtyRect(historicalX, historicalY);
         * }
         * mPath.lineTo(mX, mY);
         * break;
         * }
         * }
         * 
         * // Include half the stroke width to avoid clipping.
         * invalidate();
         * 
         * lastTouchX = eventX;
         * lastTouchY = eventY;
         * lastStroke = variableWidthDelta;
         */
        // return true;
    }

    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < dirtyRect.left) {
            dirtyRect.left = (int)historicalX;
        } else if (historicalX > dirtyRect.right) {
            dirtyRect.right = (int)historicalX;
        }
        if (historicalY < dirtyRect.top) {
            dirtyRect.top = (int)historicalY;
        } else if (historicalY > dirtyRect.bottom) {
            dirtyRect.bottom = (int)historicalY;
        }
    }

    /**
     * Resets the dirty region when the motion event occurs.
     */
    private void resetDirtyRect(float eventX, float eventY) {
        // The lastTouchX and lastTouchY were set when the ACTION_DOWN
        // motion event occurred.
        dirtyRect.left = (int)Math.min(lastTouchX, eventX);
        dirtyRect.right = (int)Math.max(lastTouchX, eventX);
        dirtyRect.top = (int)Math.min(lastTouchY, eventY);
        dirtyRect.bottom = (int)Math.max(lastTouchY, eventY);
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        if (bitmap == null)
            return null;
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        canvas.save();
        return bitmap;
    }

    /**
     *获取两条线的夹角
     * @param centerX
     * @param centerY
     * @param xInView
     * @param yInView
     * @return
     */
    public static int getRotationBetweenLines(float centerX, float centerY, float xInView, float yInView) {
        double rotation = 0;

        double k1 = (double) (centerY - centerY) / (centerX * 2 - centerX);
        double k2 = (double) (yInView - centerY) / (xInView - centerX);
        double tmpDegree = Math.atan((Math.abs(k1 - k2)) / (1 + k1 * k2)) / Math.PI * 180;

        if (xInView > centerX && yInView < centerY) {  //第一象限
            rotation = 90 - tmpDegree;
        } else if (xInView > centerX && yInView > centerY) //第二象限
        {
            rotation = 90 + tmpDegree;
        } else if (xInView < centerX && yInView > centerY) { //第三象限
            rotation = 270 - tmpDegree;
        } else if (xInView < centerX && yInView < centerY) { //第四象限
            rotation = 270 + tmpDegree;
        } else if (xInView == centerX && yInView < centerY) {
            rotation = 0;
        } else if (xInView == centerX && yInView > centerY) {
            rotation = 180;
        }
        return (int) rotation;
    }

    public static int getRotationBetweenLines_(int centerX, int centerY, int xInView, int yInView) {
        int ret = -1;
        if (centerX == xInView) {    //  x 相等
            if(yInView > centerY)
                ret = 2;
            else
                ret = 6;
        }
        else if (centerY == yInView) //y 相等
        {
            if(xInView > centerX )
                ret = 0;
            else
                ret = 4;
        }
        else if(xInView > centerX)
        {
            if(yInView > centerY)
                ret = 1;
            else
                ret = 7;
        }
        else if(xInView < centerX)
        {
            if(yInView > centerY)
                ret = 3;
            else
                ret = 5;
        }
        /*
        if(xInView > centerX )
        {

        }*/
        return ret;
    }

    /*
    连续一样才认为是正确的笔趋势，抬笔最后一点保留


     */
    //识别笔画
    public void RegBihua()
    {
        int x0 = 0;
        int y0 = 0;
        int x1 = 0;
        int y1 = 0;

        ArrayList<Short> nBHXL = new ArrayList<Short>(); //保存笔画序列
        int preBH = -1;

        int rt = -1;

        for(int i = nBHStartPos; i < mAllPoint.size() - 4; i+=2)
        {
            x0 = mAllPoint.get(i);
            y0 = mAllPoint.get(i+1);
            x1 = mAllPoint.get(i+2);
            y1 = mAllPoint.get(i+3);
            if(x0 != x1 || y0 != y1) {
                rt = getRotationBetweenLines_(x0, y0, x1, y1);
                if(preBH == -1)
                    preBH = rt;
                else if(preBH == rt)
                {
                    nBHXL.add((short)preBH);
                }
                else
                    preBH = rt;

                Log.i("lxz2", "bh:(" + x0 + "," + y0 + "," + x1 + "," + y1 + "," + rt + ")");
            }
        }
        //保留最后一个点
        if(rt != -1)
            nBHXL.add((short)rt);

        String bxl = "";
        String bxl1 = "";
        String bxl2 = "";
        preBH = -1;
        int nCount = 0;
        for(int i = 0; i < nBHXL.size(); i++)
        {
            bxl = bxl.concat(nBHXL.get(i).toString());
            if(nBHXL.get(i) != preBH) {
                nCount = 0;
                bxl1 = bxl1.concat(nBHXL.get(i).toString());
                bxl2 = bxl2.concat(nBHXL.get(i).toString());
                preBH = nBHXL.get(i);
            }
            else
                nCount++;
            if(nCount == 3)
            {
                nCount = 0;
                bxl2 = bxl2.concat(nBHXL.get(i).toString());
            }
        }
        Log.i("lxz3", "bxl:" + bxl);
        Log.i("lxz3", "bxl1:" + bxl1);
        Log.i("lxz3", "bxl2:" + bxl2);
    }
}
