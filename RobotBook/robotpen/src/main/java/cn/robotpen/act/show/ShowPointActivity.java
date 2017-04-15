package cn.robotpen.act.show;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hanwang.hwdoclib.HWDoc;
import com.zhiyuweilai.tiger.robotpen.R;
import com.zhiyuweilai.tiger.robotpen.databinding.ActivityShowPointBinding;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.robotpen.act.connect.DeviceActivity;
import cn.robotpen.core.PenManage;
import cn.robotpen.core.services.PenService;
import cn.robotpen.core.services.SmartPenService;
import cn.robotpen.model.PointObject;
import cn.robotpen.model.entity.DeviceEntity;
import cn.robotpen.model.symbol.ConnectState;
import cn.robotpen.model.symbol.Keys;
import cn.robotpen.model.symbol.SceneType;
import cn.robotpen.utils.StringUtil;

public class ShowPointActivity extends Activity {

    String TAG = getClass().getSimpleName();
    PenManage mPenManage;
    ProgressDialog mProgressDialog;
    Handler mHandler;
    Handler mRegHandler;
    Handler mWriteHandler;

    public static final int WR_REG_TEXT = 0x10;
    private ActivityShowPointBinding showPointBinding;

    public static final int WR_PEN_MOVE = 0x10;
    public static final int WR_PEN_DOWN = 0x11;
    public static final int WR_PEN_UP = 0x12;
    public static final int WR_RANGE_STRING = 0x13;

    private HWDoc mHwrDoc = new HWDoc();
    public static final Rect oldRect = new Rect();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        showPointBinding = DataBindingUtil.setContentView(this, R.layout.activity_show_point);
        mHandler = new Handler();
        // 获取关键服务
        mPenManage = new PenManage(this);
        // 设置状态监听
        mPenManage.setOnConnectStateListener(onConnectStateListener);
        // mHwrDoc = new HWDoc();

        showPointBinding.myTextView.setText(showPointBinding.myTextView.getText(), TextView.BufferType.EDITABLE);

        oldRect.left = 0;
        oldRect.top = 0;
        oldRect.right = 0;
        oldRect.bottom = 0;

        HandlerThread handlerThread = new HandlerThread("handler_thread");
        // 在使用HandlerThread的getLooper()方法之前，必须先调用该类的start();
        handlerThread.start();

        HandlerThread handlerThread1 = new HandlerThread("handler_thread1");
        // 在使用HandlerThread的getLooper()方法之前，必须先调用该类的start();
        handlerThread1.start();

        mRegHandler = new Handler(handlerThread.getLooper()) {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WR_REG_TEXT:   // move
                    {
                        short vv[] = (short[])msg.obj;
                        mHwrDoc.setRange(mHwrDoc.HW_RANGE_GB18030 | mHwrDoc.HW_RANGE_DIGIT | mHwrDoc.HW_RANGE_ALPHA | mHwrDoc.HW_RANGE_PUNCT);
                        // mHwrDoc.setRange(mHwrDoc.HW_RANGE_GB18030);
                        mHwrDoc.setChsSentenceOverlap(false);
                        mHwrDoc.segmentInputTrace(vv);
                        String sReg = mHwrDoc.getSegmentText();
                        Log.i("lxz:", "hw:" + sReg);
                        if (sReg != null) {
                            int nIndex[] = mHwrDoc.getSegmentIndexes();
                            String sIndex = "";
                            for (int x = 0; (nIndex != null) && x < nIndex.length; x++) {
                                String strTmp = String.valueOf(nIndex[x]);
                                sIndex = sIndex.concat(strTmp).concat(";");
                            }
                            Log.i("lxz:", "Index:" + sIndex);
                            ArrayList<Rect> sRectList = mHwrDoc.getSegmentRects();
                            String sRect_ = "";
                            for (int j = 0; (sRectList != null) && j < sRectList.size(); j++) {
                                String strTmp = sReg.substring(j, j + 1) + ":" + sRectList.get(j).left + "," + sRectList.get(j).top + "," + sRectList.get(j).right + "," + sRectList.get(j).bottom + ","
                                        + String.valueOf(nIndex[j]);
                                sRect_ = sRect_.concat(strTmp).concat(";");
                            }
                            Log.i("lxz:", "Rect:" + sRect_);

                            Message msg_ = mWriteHandler.obtainMessage();
                            msg_.what = WR_RANGE_STRING;
                            msg_.obj = sRect_;
                            mWriteHandler.sendMessage(msg_);
                        }
                        break;
                    }

                    default:
                        break;
                }
            }
        };
        mWriteHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case WR_PEN_MOVE:   // move
                    {
                        Bundle bundle = msg.getData();
                        showPointBinding.myView.dowithWriteMove((float)msg.arg1, (float)msg.arg2, bundle.getFloat("w"));
                        break;
                    }
                    case WR_PEN_DOWN:   // down
                    {
                        Bundle bundle = msg.getData();
                        showPointBinding.myView.dowithWriteDown((float)msg.arg1, (float)msg.arg2, bundle.getFloat("w"));
                        break;
                    }
                    case WR_PEN_UP:   // up
                        showPointBinding.myView.dowithWriteUp();
                        break;
                    case WR_RANGE_STRING:   // 识别结果
                    // showPointBinding.myView.dowithWriteText(msg.obj.toString());
                    {

                        String sReg = GetRegStext(msg.obj.toString());
                        Editable text = (Editable)showPointBinding.myTextView.getText();
                        text.append(sReg);
                        showPointBinding.myTextView.setText(text);;// myEdit.setText();
                    }
                        break;
                    default:
                        break;
                }
            };
        };
    }

    public String GetRegStext(String sRegText) {
        String sRet = "";
        if (TextUtils.isEmpty(sRegText))
            return sRet;
        String sVarray[] = sRegText.split(";");
        Rect preRect = new Rect(0, 0, 0, 0);
        for (int j = 0; j < sVarray.length; j++) {
            String sItem[] = sVarray[j].split(":");
            if (sItem.length > 1) {
                // String sPos[] = sItem[1].split(",");
                String sRect[] = sItem[1].split(",");
                int leftPos = Integer.parseInt(sRect[0]);
                int topPos = Integer.parseInt(sRect[1]);
                int rightPos = Integer.parseInt(sRect[2]);
                int bottomPos = Integer.parseInt(sRect[3]);
                boolean isFlag = false;
                if (j == 0) {
                    if (oldRect.top == 0 && oldRect.top == 0 && oldRect.right == 0 && oldRect.bottom == 0) {
                        sRet = sRet.concat("    ");
                    } else if ((leftPos - oldRect.left) < -100) {
                        sRet = sRet.concat("\r\n");
                        if (leftPos > 100)
                            sRet = sRet.concat("    ");
                    }
                    isFlag = true;
                } else {
                    isFlag = false;
                }
                if (j == sVarray.length - 1) {
                    oldRect.left = leftPos;
                    oldRect.top = topPos;
                    oldRect.right = rightPos;
                    oldRect.bottom = bottomPos;
                }
                if (!isFlag) {
                    if (bottomPos - preRect.bottom > 80) {
                        sRet = sRet.concat("\r\n");
                        if (leftPos > 100)
                            sRet = sRet.concat("    ");
                    }
                }

                preRect = new Rect(leftPos, topPos, rightPos, bottomPos);
                String sText = sItem[0];
                sRet = sRet.concat(sText);
            }
        }

        return sRet;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkDeviceConnStatus(); // 检查设备连接状态
    }

    @Override
    protected void onPause() {
        if (mPenManage != null)
            mPenManage.disconnectDevice(); // 退出Activity时将服务释放，方便其他地方继续使用
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPenManage != null)
            mPenManage.shutdown(); // 退出Activity时将服务释放，方便其他地方继续使用
    }

    /*
     * 检测设备连接
     */
    public void checkDeviceConnStatus() {
        DeviceEntity deviceEntity = mPenManage.getConnectDevice();
        if (null == deviceEntity) {
            // 判断蓝牙还是USB服务
            if (SmartPenService.TAG.equals(mPenManage.getSvrTag())) {
                // 检查以前是否有连接过设备
                DeviceEntity lastDevice = PenManage.getLastDevice(ShowPointActivity.this);
                if (lastDevice == null || TextUtils.isEmpty(lastDevice.getAddress())) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(ShowPointActivity.this);
                    alert.setTitle("提示");
                    alert.setMessage("暂未连接设备，请先连接设备！");
                    alert.setPositiveButton(R.string.canceled, null);
                    alert.setNegativeButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(ShowPointActivity.this, DeviceActivity.class);
                            ShowPointActivity.this.startActivity(intent);
                            ShowPointActivity.this.finish();
                        }
                    });
                    alert.show();
                } else {
                    mPenManage.scanDevice(onScanDeviceListener);
                }
            } else {
                mPenManage.scanDevice(null);
            }
        } else { // 已成功连接设备
            Toast.makeText(ShowPointActivity.this, "设备连接成功", Toast.LENGTH_LONG).show();
        }
    }

    /*
     * 扫描监听
     */
    PenService.OnScanDeviceListener onScanDeviceListener = new PenService.OnScanDeviceListener() {
        @Override
        public void find(DeviceEntity deviceObject) {
            DeviceEntity lastDevice = mPenManage.getLastDevice(ShowPointActivity.this);
            if (!StringUtil.isEmpty(lastDevice.getAddress())) {
                if (deviceObject.getAddress().equals(lastDevice.getAddress())) {
                    mPenManage.stopScanDevice();
                    mPenManage.connectDevice(onConnectStateListener, lastDevice.getAddress());
                }
            }
        }

        @Override
        public void complete(HashMap<String, DeviceEntity> hashMap) {
            if (!mPenManage.getIsStartConnect()) {
                Toast.makeText(ShowPointActivity.this, "暂未发现设备", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void status(int i) {
            switch (i) {
                case Keys.REQUEST_ENABLE_BT:
                    Toast.makeText(ShowPointActivity.this, "蓝牙未打开", Toast.LENGTH_SHORT).show();
                    Intent req_ble = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(req_ble, Keys.REQUEST_ENABLE_BT);
                    break;
                case Keys.BT_ENABLE_ERROR:
                    Toast.makeText(ShowPointActivity.this, "设备不支持BLE协议", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };
    /*
     * 此处监听是为了弹出授权
     */
    private PenService.OnConnectStateListener onConnectStateListener = new PenService.OnConnectStateListener() {
        @Override
        public void stateChange(String arg0, ConnectState arg1) {
            if (arg1 == ConnectState.CONNECTED) {
                Toast.makeText(ShowPointActivity.this, "设备已连接且连接成功！", Toast.LENGTH_SHORT).show();
                mPenManage.setSceneObject(SceneType.getSceneType(false, mPenManage.getConnectDeviceType()));
                // 刷新当前临时笔记
                mPenManage.setOnPointChangeListener(onPointChangeListener);
            } else if (arg1 == ConnectState.DISCONNECTED) {
                Toast.makeText(ShowPointActivity.this, "设备已断开", Toast.LENGTH_SHORT).show();
            }
        }
    };

    static boolean isPenDown = false;
    static List<Short> original_xy = new ArrayList<>();
    static short noriginalCount = 0;
    private MyThread thread = null;
    static short xReversevVal = 8191;
    static short yReversevVal = 14335;//24335;      // 14335
    /*
     * static short xDCx0 = 105;
     * static short xDCy0 = 1787;
     * static short xDCx1 = 7257;
     * static short xDCy1 = 12268; //13268
     */
    static short xGZx0 = 7;
    static short xGZy0 = 86;
    static short xGZx1 = 1069;
    static short xGZy1 = 1318;
    static int xyCount = 0;

    class MyHandler extends Handler {
        public MyHandler() {

        }

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            int age = b.getInt("age");
            String name = b.getString("name");
            System.out.println("age is " + age + ", name is" + name);
            System.out.println("Handler--->" + Thread.currentThread().getId());
            System.out.println("handlerMessage");
        }
    }

    private class MyThread extends Thread {

        public boolean stop = false;
        public boolean regflag = false;

        public void run() {
            int sSleepCount = 0;
            while (!stop) {
                // 处理功能

                // 通过睡眠线程来设置定时时间
                try {
                    Thread.sleep(10);
                    regflag = false;
                    if (sSleepCount > 150 && original_xy.size() > 0) {
                        short[] vv = new short[original_xy.size()];
                        int index = 0;
                        String sxy = "";
                        for (Short vItem : original_xy) {
                            vv[index] = vItem;
                            sxy = sxy.concat(String.valueOf(vv[index])).concat(",");
                            ++index;
                        }
                        Log.i("lxz:", sxy);

                        Message msg_ = mRegHandler.obtainMessage();
                        msg_.what = WR_REG_TEXT;
                        msg_.obj = vv;
                        mRegHandler.sendMessage(msg_);

                        /*
                         * public static final int HW_RANGE_GB18030 = 0xF;
                         * public static final int HW_RANGE_DIGIT = 0x100;
                         * public static final int HW_RANGE_ALPHA = 0x600;
                         * public static final int HW_RANGE_PUNCT = 0x7800;
                         */

                        /*
                         * mHwrDoc.setRange(mHwrDoc.HW_RANGE_GB18030|mHwrDoc.HW_RANGE_DIGIT|mHwrDoc.HW_RANGE_ALPHA|mHwrDoc.HW_RANGE_PUNCT);
                         * //mHwrDoc.setRange(mHwrDoc.HW_RANGE_GB18030);
                         * mHwrDoc.setChsSentenceOverlap(false);
                         * mHwrDoc.segmentInputTrace(vv);
                         * String sReg = mHwrDoc.getSegmentText();
                         * Log.i("lxz:", "hw:"+sReg);
                         * if(sReg != null) {
                         * int nIndex[] = mHwrDoc.getSegmentIndexes();
                         * String sIndex = "";
                         * for (int x = 0; (nIndex != null) && x < nIndex.length; x++) {
                         * String strTmp = String.valueOf(nIndex[x]);
                         * sIndex = sIndex.concat(strTmp).concat(";");
                         * }
                         * Log.i("lxz:", "Index:" + sIndex);
                         * ArrayList<Rect> sRectList = mHwrDoc.getSegmentRects();
                         * String sRect_ = "";
                         * for (int j = 0; (sRectList != null) && j < sRectList.size(); j++) {
                         * String strTmp = sReg.substring(j, j + 1) + ":" + sRectList.get(j).left + "," + sRectList.get(j).top + "," + sRectList.get(j).right + "," + sRectList.get(j).bottom + "," +
                         * String.valueOf(nIndex[j]);
                         * sRect_ = sRect_.concat(strTmp).concat(";");
                         * }
                         * Log.i("lxz:", "Rect:" + sRect_);
                         * 
                         * Message msg = mWriteHandler.obtainMessage();
                         * msg.what = WR_RANGE_STRING;
                         * msg.obj = sRect_;
                         * regflag = true;
                         * mWriteHandler.sendMessage(msg);
                         * regflag = false;
                         * }
                         */
                        regflag = false;

                        // HciCloudFuncHelper.Func_reg(mAccountInfo.getCapKey(), vv);
                        noriginalCount = 0;
                        original_xy.clear();
                        stop = true;
                        sSleepCount = 0;
                        regflag = false;
                    } else {
                        sSleepCount++;
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            thread = null;
        }
    }

    /**
     * 启动线程
     */
    private void start_regxy() {
        if (thread == null) {
            thread = new MyThread();
            thread.start();
        }
    }

    /**
     * 停止线程
     */
    private void stop_regxy() {
        if (thread != null) {
            thread.stop = true;
        }
    }

    private boolean is_regxy() {
        if (thread != null)
            return (!thread.stop);
        else
            return false;
    }

    private PenService.OnPointChangeListener onPointChangeListener = new PenService.OnPointChangeListener() {

        @Override
        public void change(final PointObject point) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // 设置看坐标中的各个字段
                    /*
                     * connectDeviceType.setText(point.getDeviceType().name());
                     * connectSenceType.setText(point.getSceneType().name());
                     * connectDeviceSize.setText(point.getWidth()+"/"+point.getHeight());
                     * penBattery.setText(String.valueOf(point.battery.getValue()));
                     * penIsRoute.setText(String.valueOf(point.isRoute));
                     * penWeight.setText(String.valueOf(point.weight));
                     * penColor.setText(String.valueOf(point.color));
                     * penPress.setText(point.pressure+"/"+point.pressureValue);
                     * penOriginal.setText(point.originalX+"/"+point.originalY);
                     * connectOffest.setText(point.getOffsetX()+"/"+point.getOffsetY());
                     */
                    if (point.isRoute && !isPenDown) {
                        isPenDown = true;
                        if (is_regxy()) {
                            stop_regxy();
                        }

                        Message msg = mWriteHandler.obtainMessage();
                        msg.what = WR_PEN_DOWN;

                        short nX;
                        short nY;
                        short xDCx0;
                        short xDCy0;
                        short xDCx1;
                        short xDCy1;   // 13268
                        if (point.getDeviceType().name() == "ELITE") {
                            xDCx0 = 502;
                            xDCy0 = 2246;
                            xDCx1 = 7556;
                            xDCy1 = 13229;   // 13268
                            nX = (short)(xReversevVal - point.originalY);
                            nY = (short)(point.originalX);
                        } else if (point.getDeviceType().name() == "P7") {
                            xDCx0 = 105;
                            xDCy0 = 1787;
                            xDCx1 = 7257;
                            xDCy1 = 12268;   // 13268
                            nX = (short)(point.originalY);
                            nY = (short)((yReversevVal - (short)point.originalX));
                        } else {
                            xDCx0 = 105;
                            xDCy0 = 1787;
                            xDCx1 = 7257;
                            xDCy1 = 12268;   // 13268
                            nX = (short)point.originalY;
                            nY = (short)(yReversevVal - (short)point.originalX);
                        }

                        // short nX_ = (short) ((nX - xDCx0) / ((xDCx1 - xDCx0) / (xGZx1 - xGZx0)) + xGZx0);
                        // short nY_ = (short) ((nY - xDCy0) / ((xDCy1 - xDCy0) / (xGZy1 - xGZy0)) + xGZy0);
                        short nX_ = (short)((nX - xDCx0) * (xGZx1 - xGZx0) / (xDCx1 - xDCx0) + xGZx0);
                        short nY_ = (short)((nY - xDCy0) * (xGZy1 - xGZy0) / (xDCy1 - xDCy0) + xGZy0);

                        original_xy.add(nX_);
                        original_xy.add(nY_);
                        msg.arg1 = nX_;
                        msg.arg2 = nY_;

                        float nW_ = point.pressure;

                        Bundle bundle = new Bundle();
                        bundle.putInt("x", nX_);
                        bundle.putInt("y", nY_);
                        bundle.putFloat("w", nW_);
                        msg.setData(bundle);// mes利用Bundle传递数据

                        mWriteHandler.sendMessage(msg);

                    } else if (!point.isRoute && isPenDown) {
                        original_xy.add((short)-1);
                        original_xy.add((short)0);
                        isPenDown = false;
                        if (thread != null) {
                            while (thread != null && thread.regflag) {
                                ;
                            }
                        }
                        Message msg = mWriteHandler.obtainMessage();
                        msg.what = WR_PEN_UP;
                        msg.arg1 = 0;
                        msg.arg1 = 0;
                        mWriteHandler.sendMessage(msg);

                        /*
                         * Message msg = mWriteHandler.obtainMessage();
                         * msg.what = WR_PEN_UP;
                         * msg.arg1 = 0;
                         * msg.arg1 = 0;
                         * mWriteHandler.sendMessage(msg);
                         * original_xy.add((short)-1);
                         * original_xy.add((short)0);
                         * isPenDown = false;
                         */
                        if (is_regxy()) {
                            stop_regxy();
                        }
                        start_regxy();
                    }
                    if (isPenDown) {
                        // short nX = (short) point.originalY;
                        // short nY = (short) (yReversevVal - (short) point.originalX);
                        short nX;
                        short nY;
                        short xDCx0;
                        short xDCy0;
                        short xDCx1;
                        short xDCy1;   // 13268
                        if (point.getDeviceType().name() == "ELITE") {
                            xDCx0 = 502;
                            xDCy0 = 2246;
                            xDCx1 = 7556;
                            xDCy1 = 13229;   // 13268
                            nX = (short)(xReversevVal - point.originalY);
                            nY = (short)(point.originalX);
                        } else if (point.getDeviceType().name() == "P7") {
                            xDCx0 = 105;
                            xDCy0 = 1787;
                            xDCx1 = 7257;
                            xDCy1 = 12268;   // 13268
                            nX = (short)(point.originalY);
                            nY = (short)((yReversevVal - (short)point.originalX));
                        } else {
                            xDCx0 = 105;
                            xDCy0 = 1787;
                            xDCx1 = 7257;
                            xDCy1 = 12268;   // 13268
                            nX = (short)point.originalY;
                            nY = (short)(yReversevVal - (short)point.originalX);
                        }

                        short nX_ = (short)((nX - xDCx0) * (xGZx1 - xGZx0) / (xDCx1 - xDCx0) + xGZx0);
                        short nY_ = (short)((nY - xDCy0) * (xGZy1 - xGZy0) / (xDCy1 - xDCy0) + xGZy0);

                        original_xy.add(nX_);
                        original_xy.add(nY_);

                        Message msg = mWriteHandler.obtainMessage();
                        msg.what = WR_PEN_MOVE;
                        msg.arg1 = nX_;
                        msg.arg2 = nY_;

                        float nW_ = point.pressure;

                        Bundle bundle = new Bundle();
                        bundle.putInt("x", nX_);
                        bundle.putInt("y", nY_);
                        bundle.putFloat("w", nW_);
                        msg.setData(bundle);// mes利用Bundle传递数据
                        Log.i("lxz:", "p:" + nW_);
                        mWriteHandler.sendMessage(msg);
                    }
                }
            });
        }

        @Override
        public void onButClick(int i) {

        }
    };

    /**
     * 释放progressDialog
     **/
    private void dismissProgressDialog() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void save(View view){
        Bitmap b = showPointBinding.myView.getBitmap();
        FileOutputStream fos = null;
        try {
            Log.i(TAG,"start savePic");

         String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/zhiyuweilai";
            File f = new File(sdpath ,System.currentTimeMillis() + ".png");
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }

            fos = new FileOutputStream(f);
            Log.i(TAG,"strFileName 1= " + f.getPath());
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
                Log.i(TAG,"save pic OK!");
            }
        } catch (FileNotFoundException e) {
            Log.i(TAG,"FileNotFoundException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(TAG,"IOException");
            e.printStackTrace();
        }
    }

}
