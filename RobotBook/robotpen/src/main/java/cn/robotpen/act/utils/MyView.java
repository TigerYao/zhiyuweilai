package cn.robotpen.act.utils;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MyView extends View {

    private static final float STROKE_WIDTH = 5f;
    private Paint paint = new Paint();
    private Path mPath = new Path();
    ArrayList<Path> mPaths = new ArrayList<Path>();
    ArrayList<Float> mStrokes = new ArrayList<Float>();

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
        paint.setColor(Color.BLACK);
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

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mPaths.size(); i++) {
            paint.setStrokeWidth(mStrokes.get(i) * PEN_WIDTH);
            canvas.drawPath(mPaths.get(i), paint);
        }
    }

    public void dowithWriteMove(float x, float y, float w) {
        float eventX = x;
        float eventY = y;

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
        //if (m_nInvalidateCount++ % 5 == 0)
            invalidate();
    }

    public void dowithWriteUp() {
        mPath.lineTo(mX, mY);
        mPaths.add(mPath);

        String sPathPoint = "{\"x\":" + mX + ",\"y\":" + mY + "},{\"x\":" + -1 + ",\"y\":" + 0 + "}]},";
        sPointString = sPointString.concat(sPathPoint);
        Log.i("lxz", sPointString);
        invalidate();
    }

    public void dowithWriteDown(float x, float y, float w) {
        float eventX = x;
        float eventY = y;

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
}
