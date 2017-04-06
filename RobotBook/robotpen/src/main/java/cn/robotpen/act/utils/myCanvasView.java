package cn.robotpen.act.utils;

/**
 * Created by Administrator on 2017/3/22/022.
 */
    import android.annotation.SuppressLint;
    import android.content.Context;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.graphics.Canvas;
    import android.graphics.Color;
    import android.graphics.Paint;
    import android.graphics.Paint.Style;
    import android.graphics.Path;
    import android.graphics.Point;
    import android.util.AttributeSet;
    import android.view.MotionEvent;
    import android.view.View;

    import com.zhiyuweilai.tiger.robotpen.R;

    import java.util.ArrayList;
    import java.util.List;



    /**
     * @ClassName: GameView
     * @Description: TODO(这里用一句话描述这个类的作用)
     * @author Winton winton_by@126.com
     * @date 2015年9月26日 上午8:54:37
     *
     */

    public class myCanvasView extends View{

        private Paint paint = null; //

        private Bitmap originalBitmap = null;//原始图

        private Bitmap new1Bitmap = null;

        private Bitmap new2Bitmap = null;

        private float clickX =0;

        private float clickY=0;

        private float startX=0;

        private float startY=0;

        private boolean isMove = true;

        private boolean isClear = false;

        private int color =Color.BLACK;//默认画笔颜色

        private float strokeWidth = 3f;//默认画笔宽度

        private List<String> m_RegText;
        private ArrayList<Point> mPoint = new ArrayList<Point>();
        private ArrayList<Point> mPtSave = new ArrayList<Point>();
        ArrayList<Path> mPaths = new ArrayList<Path>();
        ArrayList<Float> mStrokes = new ArrayList<Float>();

        Path mPath;
        private Float mStroke = 0f;

        //清楚
       public void AddRegText(String sText) {
           m_RegText.add(sText);
       }

        public myCanvasView(Context context) {
            this(context,null);
            // TODO Auto-generated constructor stub
        }
        public myCanvasView(Context context,AttributeSet atts) {
            this(context,atts,0);
            // TODO Auto-generated constructor stub
        }
        @SuppressWarnings("static-access")
        public myCanvasView(Context context,AttributeSet atts,int defStyle) {
            super(context,atts,defStyle);
            // TODO Auto-generated constructor stub

            originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_pic).copy(Bitmap.Config.ARGB_8888, true);//白色的画板
            new1Bitmap=originalBitmap.createBitmap(originalBitmap);
            mPath=new Path();
            m_RegText = new ArrayList<String>();
        }

        //清楚
        @SuppressWarnings("static-access")
        public void clear(){
            isClear =true;
            m_RegText.clear();
            new2Bitmap=originalBitmap.createBitmap(originalBitmap);
            invalidate();//重置
        }

        public void function_Catmull_Rom(ArrayList<Point> point, int cha, ArrayList<Point> save, Path path) {
            if (point.size() < 4) {
                path.moveTo(point.get(0).x, point.get(0).y);
                save.add(point.get(0));
                for(int j = 1; j < point.size(); j++) {
                    path.lineTo(point.get(j).x, point.get(j).y);
                    save.add(point.get(j));
                }
                return;
            }
            path.moveTo(point.get(0).x, point.get(0).y);
            save.add(point.get(0));

            int startX = point.get(0).x;
            int startY = point.get(0).y;
            int clickX;
            int clickY;
            for (int index = 1; index < point.size() - 2; index++) {
                Point p0 = point.get(index - 1);
                Point p1 = point.get(index);
                Point p2 = point.get(index + 1);
                Point p3 = point.get(index + 2);

                for (int i = 1; i <= cha; i++) {
                    float t = i * (1.0f / cha);
                    float tt = t * t;
                    float ttt = tt * t;

                    Point pi = new Point(); // intermediate point
                    pi.x = (int)(0.5 * (2 * p1.x + (p2.x - p0.x) * t + (2 * p0.x - 5 * p1.x + 4 * p2.x - p3.x) * tt + (3 * p1.x - p0.x - 3 * p2.x + p3.x)
                            * ttt));
                    pi.y = (int) (0.5 * (2 * p1.y + (p2.y - p0.y) * t + (2 * p0.y - 5 * p1.y + 4 * p2.y - p3.y) * tt + (3 * p1.y - p0.y - 3 * p2.y + p3.y)
                            * ttt));

                    clickX=pi.x;
                    clickY=pi.y;
                    float dx=Math.abs(clickX-startX);
                    float dy=Math.abs(clickY-startY);

                    if( dx>=3 || dy>=3) {
                        //设置贝塞尔曲线的操作点为起点和终点的一半
                        float cX = (clickX + startX) / 2;
                        float cY = (clickY + startY) / 2;
                        mPath.quadTo(startX, startY, cX, cY);

                        startX = clickX;
                        startY = clickY;
                        save.add(pi);
                    }

                    else if( dx>=2 || dy>=2)
                    {
                        path.lineTo(pi.x, pi.y);
                        startX = clickX;
                        startY = clickY;
                        save.add(pi);
                    }


                    /*
                    if( dx>=1 || dy>=1) {
                        path.lineTo(pi.x, pi.y);
                        save.add(pi);
                        startX = clickX;
                        startY = clickY;
                    }
                    */

                }
            }
           // path.lineTo(point.get(point.size() - 1).x, point.get(point.size() - 1).y);
            save.add(point.get(point.size() - 1));
        }


        public void setStrokeWidth(float width){
            this.strokeWidth=width;
            initPaint();
        }
        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            super.onDraw(canvas);
            canvas.drawBitmap(writer(new1Bitmap),0,0, null);
        }
        public void dowithWriteMove(float x, float y, float w) {

            clickX=x;
            clickY=y;
            float dx=Math.abs(clickX-startX);
            float dy=Math.abs(clickY-startY);

           if( dx>=3 || dy>=3) {
               //设置贝塞尔曲线的操作点为起点和终点的一半
               float cX = (clickX + startX) / 2;
               float cY = (clickY + startY) / 2;

               mPath.quadTo(startX, startY, cX, cY);

               startX = clickX;
               startY = clickY;

             //  invalidate();
           }
            else if(dx >= 2 || dy >= 2)
           {
               /*
               mPath.lineTo(clickX, clickY);
               startX = clickX;
               startY = clickY;
               */
           }

            /*
            Point pt = new Point();
            pt.x = (int) x;
            pt.y = (int) y;
            mPoint.add(pt);
            */
        }
        public void dowithWriteUp()
        {
         //   function_Catmull_Rom(mPoint, 100, mPtSave, mPath);
            invalidate();
            mPoint.clear();
        }
        public void dowithWriteDown(float x, float y, float w)
        {
            clickX = x;
            clickY= y;
            startX=clickX;
            startY=clickY;
            mPath.reset();
            setPenWidth(w);
            mPath.moveTo(clickX, clickY);
           // invalidate();

            /*
            Point pt = new Point();
            pt.x = (int)x;
            pt.y = (int)y;
            mPoint.add(pt);
            */
        }

        public void dowithWriteText(String sText)
        {
            AddRegText(sText);
            invalidate();
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            // TODO Auto-generated method stub


            clickX =event.getX();

            clickY=event.getY();

            if(event.getAction()==MotionEvent.ACTION_DOWN){
                //手指点下屏幕时触发
                startX=clickX;
                startY=clickY;
                mPath.reset();
                mPath.moveTo(clickX, clickY);

//   isMove =false;
//   invalidate();
//   return true;
            }
            else if(event.getAction()==MotionEvent.ACTION_MOVE){
                //手指移动时触发
                float dx=Math.abs(clickX-startX);
                float dy=Math.abs(clickY-startY);
//   if(dx>=3||dy>=3){
                //设置贝塞尔曲线的操作点为起点和终点的一半
                float cX = (clickX + startX) / 2;
                float cY = (clickY + startY) / 2;
                mPath.quadTo(startX,startY, cX, cY);

                startX=clickX;
                startY=clickY;
//   }
//   isMove =true;
//   invalidate();
//   return true;
            }
            invalidate();
            return true;
        }


        /**
         * @Title: writer
         * @Description: TODO(这里用一句话描述这个方法的作用)
         * @param @param pic
         * @param @return 设定文件
         * @return Bitmap 返回类型
         * @throws
         */
        public Bitmap writer(Bitmap pic){
            initPaint();

            Canvas canvas =null;
            if(isClear){
                canvas=new Canvas(new2Bitmap);
            }else{
                canvas=new Canvas(pic);
            }

            //canvas.drawLine(startX, startY, clickX, clickY, paint);//画线
            for(int x_ = 0; x_ < mPaths.size(); x_++) {
                Path path_ = mPaths.get(x_);
                setPenWidth(mStrokes.get(x_));
                canvas.drawPath(path_, paint);
            }

            int xPos_ = 0;
            int yPos_ = 0;
            for(int i = 0; i < m_RegText.size(); i++)
            {
                String sReg = m_RegText.get(i);
                String sVarray[] = sReg.split(";");
                for(int j = 0; j < sVarray.length; j++) {
                    String sItem[] = sVarray[j].split(":");
                    if(sItem.length > 0)
                    {
                        String sPos[] = sItem[1].split(",");
                        String sText = sItem[0];

                        xPos_ = Integer.parseInt(sPos[0]);
                        yPos_ = Integer.parseInt(sPos[3])/80*80;  //一行80像素

                        if(Integer.parseInt(sPos[1]) < yPos_)
                        {
                            if(Integer.parseInt(sPos[3])%80 < 30)
                                yPos_ -= 80;
                        }

                        Paint paint_ = new Paint();
                        paint_.setColor(Color.BLUE);
                   //   point_.setTypeface();
                        paint_.setAntiAlias(true);//去除锯齿
                        paint_.setFilterBitmap(true);//对位图进行滤波处理
                        paint_.setTextSize(20);
                        canvas.drawText(sText, xPos_, yPos_, paint_);
                    }

                }
            }
            if(isClear){
                return new2Bitmap;
            }
            return pic;
        }

        private void initPaint(){

            paint = new Paint(Paint.ANTI_ALIAS_FLAG);//初始化画笔

            paint.setStyle(Style.STROKE);//设置为画线

            paint.setAntiAlias(true);//设置画笔抗锯齿

            paint.setColor(color);//设置画笔颜色

            paint.setStrokeWidth(strokeWidth);//设置画笔宽度
        }

        private void setPenWidth(float nWidth)
        {
            paint.setStrokeWidth(nWidth);
        }

        private void setPenColor(int nColor)
        {
            paint.setColor(nColor);
        }

        /**
         * @Title: setColor
         * @Description: TODO(设置画笔颜色)
         * @param @param color 设定文件
         * @return void 返回类型
         * @throws
         */
        public void setColor(int color){

            this.color=color;
            initPaint();
        }

        public Bitmap getPaint(){
            return new1Bitmap;
        }
    }
