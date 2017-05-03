package cn.robotpen.act.show;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Time;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hanwang.hwdoclib.HWDoc;
import com.zhiyuweilai.tiger.robotpen.R;
import com.zhiyuweilai.tiger.robotpen.databinding.ActivityShowPointBinding;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.robotpen.act.connect.DeviceActivity;
import cn.robotpen.act.model.ArticalResult;
import cn.robotpen.act.utils.HttpUtils;
import cn.robotpen.act.utils.RectTxtInfo;
import cn.robotpen.core.PenManage;
import cn.robotpen.core.services.PenService;
import cn.robotpen.core.services.SmartPenService;
import cn.robotpen.model.PointObject;
import cn.robotpen.model.entity.DeviceEntity;
import cn.robotpen.model.symbol.ConnectState;
import cn.robotpen.model.symbol.Keys;
import cn.robotpen.model.symbol.SceneType;
import cn.robotpen.utils.StringUtil;

import cn.robotpen.utils.TimeUtil;
/*
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.protocol.HTTP;
*/

public class ShowPointActivity extends Activity {

    String TAG = getClass().getSimpleName();
    PenManage mPenManage;
    Handler mHandler;
    Handler mRegHandler;
    Handler mWriteHandler;

    public static final int WR_REG_TEXT = 0x10;
    private ActivityShowPointBinding showPointBinding;

    public static final int WR_PEN_MOVE = 0x10;
    public static final int WR_PEN_DOWN = 0x11;
    public static final int WR_PEN_UP = 0x12;
    public static final int WR_RANGE_STRING = 0x13;
    public static final int PC_NET_RESULT = 0x14;

    private HWDoc mHwrDoc = null;// = new HWDoc();
    public static final Rect oldRect = new Rect();
    private android.os.Environment Environment;

    ArrayList<EditText> mMuliteEditText = new ArrayList<EditText>();
    public String mRegAllTxt = "";
    ArrayList<RectTxtInfo>  mRectTxtInfo = new ArrayList<RectTxtInfo>();   //保存识别结果信息
    public   String sRegPingce = "";   //评测显示的作文
    public   ArrayList<String> mModifyRegText = new ArrayList<String>();  //保存修改后的识别结果
    public Dialog progressDialog = null;   //等待窗口

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        showPointBinding = DataBindingUtil.setContentView(this, R.layout.activity_show_point);
        mHandler = new Handler();
        // 获取关键服务
        mPenManage = new PenManage(this);
        // 设置状态监听
        mPenManage.setOnConnectStateListener(onConnectStateListener);
        mHwrDoc = new HWDoc();

        Spinner spinner01 = (Spinner) findViewById(R.id.Spinner01);

        //数据
        /*
        记叙文(叙事、写景、状物、写他人、写自己、写场面画面)、议论文
         */
        ArrayList<String> data_list = new ArrayList<String>();
        data_list.add("记叙文");
        data_list.add("议论文");

        //适配器
        ArrayAdapter<String> arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner01.setAdapter(arr_adapter);

        Spinner spinner02 = (Spinner) findViewById(R.id.Spinner02);

        //
        //
        //
        /*数据   高三、高二、高一
        初三、初二、初一
        六年级、五年级
        四年级、三年级、二年级、一年级
                */
        ArrayList<String> data_list1 = new ArrayList<String>();
        data_list1.add("一年级");
        data_list1.add("二年级");
        data_list1.add("三年级");
        data_list1.add("四年级");
        data_list1.add("五年级");
        data_list1.add("六年级");
        data_list1.add("初一");
        data_list1.add("初二");
        data_list1.add("初三");
        data_list1.add("高一");
        data_list1.add("高二");
        data_list1.add("高三");

        //适配器
        ArrayAdapter<String> arr_adapter1= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list1);
        //设置样式
        arr_adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner02.setAdapter(arr_adapter1);


        showPointBinding.myTextView.setText(showPointBinding.myTextView.getText(), TextView.BufferType.EDITABLE);

        oldRect.left = 0;
        oldRect.top = 0;
        oldRect.right = 0;
        oldRect.bottom = 0;

        HandlerThread handlerThread = new HandlerThread("handler_thread");
        // 在使用HandlerThread的getLooper()方法之前，必须先调用该类的start();
        handlerThread.start();

        mRegHandler = new Handler(handlerThread.getLooper()) {
            public void handleMessage(Message msg) {
               /* if(mHwrDoc != null)
                    mHwrDoc.clear();
                    */

                switch (msg.what) {
                    case WR_REG_TEXT:   // move
                    {
                        if(mHwrDoc == null)
                            return;
                        //mHwrDoc.clear();
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
                                String s_ = sReg.substring(j, j + 1);
                                if(s_.equals(":"))   //转义
                                    s_ = "&";
                                String strTmp = s_ + ":" + sRectList.get(j).left + "," + sRectList.get(j).top + "," + sRectList.get(j).right + "," + sRectList.get(j).bottom + ","
                                        + String.valueOf(nIndex[j]);
                                sRect_ = sRect_.concat(strTmp).concat(";");
                            }
                            Log.i("lxz:", "Rect:" + sRect_);

                            Message msg_ = mWriteHandler.obtainMessage();
                            msg_.what = WR_RANGE_STRING;
                            msg_.obj = sRect_;
                            mWriteHandler.sendMessage(msg_);
                        }
                      //  mHwrDoc.clear();
                      //  mHwrDoc = null;
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
                        mRegAllTxt = mRegAllTxt.concat(msg.obj.toString());
                        String sReg = GetRegStext(msg.obj.toString());//计算
                        if(showPointBinding.myView.GetRegTxtShowType() == 1)   //修改排版
                        {
                            CreateMuliteLineText_();  //刷新;
                        }


                        /*
                        String sReg = GetRegStext(msg.obj.toString());
                        Editable text = (Editable)showPointBinding.myTextView.getText();
                        text.append(sReg);
                        showPointBinding.myTextView.setText(text);;// myEdit.setText();
                        */

                    }
                        break;
                    case PC_NET_RESULT:   //评测结果处理
                    {
                        if(showPointBinding.myView.GetRegTxtShowType() == 2) {
                            EditText myPingceResulteText = (EditText) findViewById(R.id.myPingceResulteView);
                            String s_ = ParsePingceResult(msg.obj.toString());
                            //s_ = s_ + "\r\n\r\n" + msg.obj.toString();
                            myPingceResulteText.setText(s_);
                        }
                        hideCheckDialog();
                        break;
                    }
                    default:
                        break;
                }
            };
        };
    }

    public Rect preRect = new Rect(0, 0, 0, 0);
    public String GetRegStext(String sRegText) {
        String sRet = "";
        if (TextUtils.isEmpty(sRegText))
            return sRet;
        String sVarray[] = sRegText.split(";");

        for (int j = 0; j < sVarray.length; j++) {
            String sItem[] = sVarray[j].split(":");
            if (sItem.length > 1) {
                // String sPos[] = sItem[1].split(",");
                String sRect[] = sItem[1].split(",");
                if(sRect.length <= 0)
                    return sRet;
                if(sRect[0].isEmpty() || sRect[1].isEmpty() || sRect[2].isEmpty() || sRect[3].isEmpty())
                    return sRet;
                int leftPos = Integer.parseInt(sRect[0]);
                int topPos = Integer.parseInt(sRect[1]);
                int rightPos = Integer.parseInt(sRect[2]);
                int bottomPos = Integer.parseInt(sRect[3]);

                boolean isFlag = false;
                if (j == 0) {
                    if (oldRect.top == 0 && oldRect.top == 0 && oldRect.right == 0 && oldRect.bottom == 0) {
                        sRet = sRet.concat("            ");
                    }
                    /*else if ((leftPos - oldRect.left) < -100) {
                        sRet = sRet.concat("\r\n");
                        if (leftPos > 100)
                            sRet = sRet.concat("        ");
                    }*/
                    else if ( (preRect.bottom != 0) && (bottomPos - preRect.bottom) > 70) {
                        // sRet = sRet.concat("\r\n");
                        if (leftPos > 100) {
                            sRet = sRet.concat("\r\n");
                            sRet = sRet.concat("        ");
                        }
                        else if(preRect.right < 900 )
                        {
                            sRet = sRet.concat("\r\n");
                            sRet = sRet.concat("        ");
                        }
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
                    if (bottomPos - preRect.bottom >  80) {
                       // sRet = sRet.concat("\r\n");
                        if (leftPos > 100) {
                            sRet = sRet.concat("\r\n");
                            sRet = sRet.concat("        ");
                        }
                        else if(preRect.right < 900 )
                        {
                            sRet = sRet.concat("\r\n");
                            sRet = sRet.concat("        ");
                        }
                    }
                }

                preRect = new Rect(leftPos, topPos, rightPos, bottomPos);
                String sText = sItem[0];
                if(sText.equals("&"))   //转义  //转义)
                    sText = ":";
                sRet = sRet.concat(sText);

                RectTxtInfo rinfo = new RectTxtInfo();
                rinfo.setType(0);
                rinfo.setVal(sText);
                rinfo.setRect(preRect);
                mRectTxtInfo.add(rinfo);
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
        hideCheckDialog();
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

    boolean isPenDown = false;
    List<Short> original_xy = new ArrayList<>();
    short noriginalCount = 0;
    private MyThread thread = null;
    static short xReversevVal = 8191;
    static short yReversevVal = 24335;     //24335;      // 14335
    /*
     * static short xDCx0 = 105;
     * static short xDCy0 = 1787;
     * static short xDCx1 = 7257;
     * static short xDCy1 = 12268; //13268
     */
    /*
    static short xGZx0 = 7;
    static short xGZy0 = 86;
    static short xGZx1 = 1069;
    static short xGZy1 = 1318;
    */

    static short xGZx0 = 0;
    static short xGZy0 = 0;
    static short xGZx1 = 1024;    //1024
    static short xGZy1 = 1280;    //1280

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
                    int data_size = original_xy.size();
                    //Log.i("lxz1", "data_size:" + data_size);
                    if (sSleepCount > 150 && data_size > 0) {
                        regflag = true;
                        Log.i("lxz1", "regflag:" + regflag + "" + data_size);
                        short[] vv = new short[data_size];
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
            stop = true;
            regflag = false;
            thread = null;
        }
    }

    /**
     * 启动线程
     */
    private void start_regxy() {
        if (thread == null) {
          //  thread = new MyThread();    及时识别关闭
          //  thread.start();
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

    private void waiteforreg()
    {
        if (thread != null) {
            while (thread != null && thread.regflag) {
                ;
            }
        }
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
                        waiteforreg();

                        isPenDown = true;
                        if (is_regxy()) {
                            stop_regxy();
                        }

                        //切换到原笔迹输入上
                        if(showPointBinding.myView.GetRegTxtShowType() != 0)
                        {
                            SetButtonStatus(0);
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
                         /*   xDCx0 = 502;
                            xDCy0 = 2246;
                            xDCx1 = 7556;
                            xDCy1 = 13229;   // 13268
                            */
                            xDCx0 = 139;
                            xDCy0 = 678;
                            xDCx1 = 7980;
                            xDCy1 = 14077;   // 13268
                            xReversevVal = 8240;
                            nX = (short)(xReversevVal - point.originalY);
                            nY = (short)(point.originalX);
                        } else if (point.getDeviceType().name() == "P7") {
                            xDCx0 = 46;
                            xDCy0 = 199;
                            xDCx1 = 15356;
                            xDCy1 = 22012;   // 13268
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
                        short nX_ = (short)((nX - xDCx0) * (xGZx1 - xGZx0) / (xDCx1 - xDCx0) + 20);
                        short nY_ = (short)((nY - xDCy0) * (xGZy1 - xGZy0) / (xDCy1 - xDCy0) + 20);

                        waiteforreg();
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
                        waiteforreg();
                        original_xy.add((short)-1);
                        original_xy.add((short)0);
                        isPenDown = false;

                        Message msg = mWriteHandler.obtainMessage();
                        msg.what = WR_PEN_UP;
                        msg.arg1 = 0;
                        msg.arg1 = 0;
                        mWriteHandler.sendMessage(msg);

                        /*
                        List<Short> original_xy_ = original_xy;
                        Message msg = mWriteHandler.obtainMessage();
                        msg.what = WR_PEN_UP;
                        msg.arg1 = 0;
                        msg.obj = original_xy_;
                        mWriteHandler.sendMessage(msg);
                        */

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

                        waiteforreg();

                        short nX;
                        short nY;
                        short xDCx0;
                        short xDCy0;
                        short xDCx1;
                        short xDCy1;   // 13268
                        if (point.getDeviceType().name() == "ELITE") {
                            /*
                            xDCx0 = 502;
                            xDCy0 = 2246;
                            xDCx1 = 7556;
                            xDCy1 = 13229;   // 13268
                            */
                            xDCx0 = 139;
                            xDCy0 = 678;
                            xDCx1 = 7980;
                            xDCy1 = 14077;   // 13268
                            xReversevVal = 8240;
                            nX = (short)(xReversevVal - point.originalY);
                            nY = (short)(point.originalX);
                        } else if (point.getDeviceType().name() == "P7") {
                            xDCx0 = 46;
                            xDCy0 = 199;
                            xDCx1 = 15356;
                            xDCy1 = 22012;   // 13268
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

                        short nX_ = (short)((nX - xDCx0) * (xGZx1 - xGZx0) / (xDCx1 - xDCx0) + 20);
                        short nY_ = (short)((nY - xDCy0) * (xGZy1 - xGZy0) / (xDCy1 - xDCy0) + 20);

                        waiteforreg();
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

    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString().toUpperCase();
    }

    //获取当前时间
    private String getNowDateTime(){

    //    Calendar c = null;
        String str  = "";

        // 初始话一个Time对象，也可以写成:Time time = new Time("GTM+8"),即加上时区
        Time time = new Time("GTM+8");
        // 设置当前时间
        time.setToNow();
        time.month += 1;
        // 输出当前日期
        str = String.format(time.year+"/"+time.month+"/"+time.monthDay);

        return str;
    }
/*
POST /interface/ZY_WebService.asmx/submitTest HTTP/1.1
Host:www.jz600.com
Content-Type:application/x-www-form-urlencoded
Content-Length:754

{"stitle":"时事评述","sanswer":"根据英国金融时报今年稍早的报道，中国目前的投票比例为26 ，略高于一般组织中实际否决权所需比例，一般组织做出某些决策需要四分之三的投票比例。据悉，作为扩大亚投行影响力的代价，中国愿意放弃否决权。今年早些时候亚投行行长金立群曾表示，成员国增加将有利于扩大这个规模为1000亿美元的多边组织的贷款，亚投行于去年创建，共有57个创始成员国。据悉，在今年预计今年将有大约25个非洲、欧洲和南美国家加入亚投行，将包括爱尔兰、加拿大、埃塞俄比亚和苏丹，有几个国家可能会在今年6月亚投行年会时加入。亚投行会员国增加的一个重要来源是非洲，目前只有埃及和南非加入。","sgrade":"一年级","swenti":"记叙文","number":"1245","sname":"陈志强","snum":"023","yqzishu":"240","yqtitle":"擦擦是","yqwenti":"大撒旦撒","smiyao":"94C7019C2ED5205925B6241B4D9CD621"}
 */
      public String executePost() {
        String strNowDateTime=getNowDateTime();//当前时间
        String sDate_ = "kfj3jfk"+strNowDateTime+"KJU893";
        final String sDateTime = getMD5Str(sDate_);

          Spinner spinner01 = (Spinner) findViewById(R.id.Spinner01);
          Spinner spinner02 = (Spinner) findViewById(R.id.Spinner02);

          final String sTitile = GetPingceTextTitle();      //题目
          final String sContent = GetPingceTexContent();    //正文
          final String sGrade = spinner02.getSelectedItem().toString();
          final String sTicai = spinner01.getSelectedItem().toString();

          String result = null;

          Thread th = new Thread(new Runnable() {
              @Override
              public void run() {
                  // TODO Auto-generated method stub

                  //String sData=String.format("{\"stitle\":\"时事评述\",\"sanswer\":\"根据英国金融时报今年稍早的报道，中国目前的投票比例为26 ，略高于一般组织中实际否决权所需比例，一般组织做出某些决策需要四分之三的投票比例。据悉，作为扩大亚投行影响力的代价，中国愿意放弃否决权。今年早些时候亚投行行长金立群曾表示，成员国增加将有利于扩大这个规模为1000亿美元的多边组织的贷款，亚投行于去年创建，共有57个创始成员国。据悉，在今年预计今年将有大约25个非洲、欧洲和南美国家加入亚投行，将包括爱尔兰、加拿大、埃塞俄比亚和苏丹，有几个国家可能会在今年6月亚投行年会时加入。亚投行会员国增加的一个重要来源是非洲，目前只有埃及和南非加入。\",\"sgrade\":\"三年级\",\"swenti\":\"记叙文\",\"number\":\"1245\",\"sname\":\"陈志强\",\"snum\":\"023\",\"yqzishu\":\"240\",\"yqtitle\":\"擦擦是\",\"yqwenti\":\"大撒旦撒\",\"smiyao\":\"%s\"}", sDateTime);

                  String sData=String.format("{\"stitle\":\"%s\",\"sanswer\":\"%s\",\"sgrade\":\"%s\",\"swenti\":\"%s\",\"number\":\"0001\",\"sname\":\"test1\",\"snum\":\"0001\",\"yqzishu\":\"0\",\"yqtitle\":\"\",\"yqwenti\":\"\",\"smiyao\":\"%s\"}", sTitile, sContent, sGrade, sTicai,sDateTime);
                  sData = toUtf8(sData);
                //  String sData = "{\"stitle\":\"时事评述\",\"sanswer\":\"根据英国金融时报今年稍早的报道，中国目前的投票比例为26 ，略高于一般组织中实际否决权所需比例，一般组织做出某些决策需要四分之三的投票比例。据悉，作为扩大亚投行影响力的代价，中国愿意放弃否决权。今年早些时候亚投行行长金立群曾表示，成员国增加将有利于扩大这个规模为1000亿美元的多边组织的贷款，亚投行于去年创建，共有57个创始成员国。据悉，在今年预计今年将有大约25个非洲、欧洲和南美国家加入亚投行，将包括爱尔兰、加拿大、埃塞俄比亚和苏丹，有几个国家可能会在今年6月亚投行年会时加入。亚投行会员国增加的一个重要来源是非洲，目前只有埃及和南非加入。\",\"sgrade\":\"三年级\",\"swenti\":\"记叙文\",\"number\":\"1245\",\"sname\":\"陈志强\",\"snum\":\"023\",\"yqzishu\":\"240\",\"yqtitle\":\"擦擦是\",\"yqwenti\":\"大撒旦撒\",\"smiyao\":\"94C7019C2ED5205925B6241B4D9CD621\"}";
                  String sRet = HttpUtils.submitPostData1("http://www.jz600.com/interface/ZY_WebService.asmx/submitTest",sData);

                  Message msg_ = mWriteHandler.obtainMessage();
                  msg_.what = PC_NET_RESULT;
                  if(sRet.equals("-1"))   //评测失败，网络无连接！
                  {
                      msg_.obj = "评测失败，网络无连接！";
                  }
                  else if(sRet.equals("-2"))  //评测失败，网络异常！
                  {
                      msg_.obj = "评测失败，网络异常！";
                  }
                  else
                  {
                      msg_.obj = sRet;
                  }
                  mWriteHandler.sendMessage(msg_);
              }
          });
          th.start();
          //   String sData = "{\"stitle\":\"时事评述\",\"sanswer\":\"根据英国金融时报今年稍早的报道，中国目前的投票比例为26 ，略高于一般组织中实际否决权所需比例，一般组织做出某些决策需要四分之三的投票比例。据悉，作为扩大亚投行影响力的代价，中国愿意放弃否决权。今年早些时候亚投行行长金立群曾表示，成员国增加将有利于扩大这个规模为1000亿美元的多边组织的贷款，亚投行于去年创建，共有57个创始成员国。据悉，在今年预计今年将有大约25个非洲、欧洲和南美国家加入亚投行，将包括爱尔兰、加拿大、埃塞俄比亚和苏丹，有几个国家可能会在今年6月亚投行年会时加入。亚投行会员国增加的一个重要来源是非洲，目前只有埃及和南非加入。\",\"sgrade\":\"三年级\",\"swenti\":\"记叙文\",\"number\":\"1245\",\"sname\":\"陈志强\",\"snum\":\"023\",\"yqzishu\":\"240\",\"yqtitle\":\"擦擦是\",\"yqwenti\":\"大撒旦撒\",\"smiyao\":\"94C7019C2ED5205925B6241B4D9CD621\"}";
      //  HttpUtils.submitPostData1("http://www.jz600.com/interface/ZY_WebService.asmx/submitTest",sData);

        return result;

       // HttpUtils.submitPostData();

    }

    //提交评测
    public void pingcebutton(View view) throws UnsupportedEncodingException {
        if(progressDialog == null) {
            showCheckDialog( "正在评测......");
            executePost();
        }
    }

    //保存
    public void save(View view) throws UnsupportedEncodingException {

        //executePost();
        /*
        String uriAPI = "http://127.0.0.1/xxx/xx.jsp";  //声明网址字符串
        HttpPost httpRequest = new HttpPost(uriAPI);   //建立HTTP POST联机
        List <NameValuePair> params = new ArrayList <NameValuePair>();   //Post运作传送变量必须用NameValuePair[]数组储存
        params.add((NameValuePair) new BasicNameValuePair("str", "I am Post String"));
        httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));   //发出http请求
        HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);   //取得http响应
        if(httpResponse.getStatusLine().getStatusCode() == 200)
            String strResult = EntityUtils.toString(httpResponse.getEntity());   //获取字符串
            */

        if(showPointBinding.myView.GetWordRowCount() < 1) {
            Toast.makeText(this, "请先输入三行作文!", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap b = showPointBinding.myView.getBitmap();
        FileOutputStream fos = null;
        try {
            Log.i(TAG,"start savePic");

         String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/xiaoyuzhishi";
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
            Log.i(TAG, "IOException");
            e.printStackTrace();
        }
    }

    //原笔迹 版面
    public void status(View view) throws UnsupportedEncodingException {
        if(showPointBinding.myView.GetRegTxtShowType() == 0)
            return;
        SetButtonStatus(0);
    }
    //识别修改  版面
    public void modifystatus(View view) throws UnsupportedEncodingException {
        if(showPointBinding.myView.GetRegTxtShowType() == 1)
            return;
        if(progressDialog == null) {
            if (showPointBinding.myView.GetWordRowCount() > 1) {
                showCheckDialog("正在识别并排版......");
                CreateMuliteLineText();
                SetButtonStatus(1);
            } else
                Toast.makeText(this, "请先输入三行作文!", Toast.LENGTH_SHORT).show();
        }
    }

    //篇章文本  版面
    public void regTxt(View view) throws UnsupportedEncodingException {
        //showPointBinding.myView.SetRegTxtShowType(2);
        //CreateMuliteLineText();
        if(showPointBinding.myView.GetRegTxtShowType() == 2)
            return;
        SetButtonStatus(2);

        EditText myPingceText = (EditText) findViewById(R.id.myTextView);
        myPingceText.setText(sRegPingce);
    }

    //篇章识别
    public void RegText_() {
        mRectTxtInfo.clear();
        int data_size = original_xy.size();
        if (data_size > 0) {
            short[] vv = new short[data_size];
            int index = 0;
            for (Short vItem : original_xy) {
                vv[index] = vItem;
                ++index;
            }
            Message msg_ = mRegHandler.obtainMessage();
            msg_.what = WR_REG_TEXT;
            msg_.obj = vv;
            mRegHandler.sendMessage(msg_);
        }
    }

    public void regFGTxt(View view) throws UnsupportedEncodingException, InterruptedException {
               Editable text = (Editable)showPointBinding.myTextView.getText();
        text.append("\r\n分割识别：\r\n");
        ArrayList<Point> mWordLine = showPointBinding.myView.GetAllWordRectLine();
        for(int i =0 ; i < mWordLine.size(); i++)
        {
            original_xy.clear();
            original_xy = showPointBinding.myView.GetWordRectLine(i);

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

           // wait(2000);
        }
    }

    //动态获取识别文本以及行排序
    public ArrayList<RectTxtInfo> GetRectText(Rect rt)
    {
        ArrayList<RectTxtInfo> reMuliteInof = new ArrayList<RectTxtInfo>();
        for(int i = 0; i< mRectTxtInfo.size(); i++)
        {
            RectTxtInfo rInfo = mRectTxtInfo.get(i);
            if(RectTxtInfo.rectToRect(rt, rInfo.getRect()))   //区域包含在内
            {
                reMuliteInof = RectTxtInfo.AddRectInfo_sort(reMuliteInof,rInfo);
            }
        }
        return reMuliteInof;
    }

    //设置是原笔迹还是修改版面
    public void SetButtonStatus(int type)
    {
        Button buttonpianzhang = (Button) findViewById(R.id.regTxt);
        Spinner spinner01 = (Spinner) findViewById(R.id.Spinner01);
        Spinner spinner02 = (Spinner) findViewById(R.id.Spinner02);

        if(type == 0)   //原笔迹
        {
            FrameLayout myWriteFrame = (FrameLayout) findViewById(R.id.mywrtite);
            myWriteFrame.setVisibility(View.VISIBLE);
            EditText myPingceText = (EditText) findViewById(R.id.myTextView);
            myPingceText.setVisibility(View.INVISIBLE);

            spinner01.setVisibility(View.INVISIBLE);
            spinner02.setVisibility(View.INVISIBLE);
            Button pingceBtn = (Button) findViewById(R.id.pingce);
            pingceBtn.setVisibility(View.INVISIBLE);
            EditText myPingceResulteText = (EditText) findViewById(R.id.myPingceResulteView);
            myPingceResulteText.setVisibility(View.INVISIBLE);

            buttonpianzhang.setVisibility(View.INVISIBLE);
            RemoveMuliteLineText();
            showPointBinding.myView.SetRegTxtShowType(0);
        }
        else if(type == 1) //识别修改
        {
            FrameLayout myWriteFrame = (FrameLayout) findViewById(R.id.mywrtite);
            myWriteFrame.setVisibility(View.VISIBLE);

            EditText myPingceText = (EditText) findViewById(R.id.myTextView);
            myPingceText.setVisibility(View.INVISIBLE);
            spinner01.setVisibility(View.INVISIBLE);
            spinner02.setVisibility(View.INVISIBLE);
            Button pingceBtn = (Button) findViewById(R.id.pingce);
            pingceBtn.setVisibility(View.INVISIBLE);
            EditText myPingceResulteText = (EditText) findViewById(R.id.myPingceResulteView);
            myPingceResulteText.setVisibility(View.INVISIBLE);

            buttonpianzhang.setVisibility(View.VISIBLE);
            showPointBinding.myView.SetRegTxtShowType(1);
        }
        else if(type == 2) //篇章
        {
            RemoveMuliteLineText();

            FrameLayout myWriteFrame = (FrameLayout) findViewById(R.id.mywrtite);
            myWriteFrame.setVisibility(View.GONE);

            spinner01.setVisibility(View.VISIBLE);
            spinner02.setVisibility(View.VISIBLE);
            EditText myPingceText = (EditText) findViewById(R.id.myTextView);
            myPingceText.setVisibility(View.VISIBLE);
            EditText myPingceResulteText = (EditText) findViewById(R.id.myPingceResulteView);
            myPingceResulteText.setVisibility(View.VISIBLE);

            Button pingceBtn = (Button) findViewById(R.id.pingce);
            pingceBtn.setVisibility(View.VISIBLE);

            buttonpianzhang.setVisibility(View.VISIBLE);
            showPointBinding.myView.SetRegTxtShowType(2);
        }
    }

    //动态创建识别文本框
    public void RemoveMuliteLineText()
    {
        //清空EditText
        FrameLayout mainLayout = (FrameLayout)findViewById(R.id.mywrtite);
        for(int i = 0; i< mMuliteEditText.size(); i++)
        {
            EditText v = mMuliteEditText.get(i);
            mainLayout.removeView(v);
        }
        mMuliteEditText.clear();
    }

    //动态创建识别文本框
    public void CreateMuliteLineText()
    {
        if(showPointBinding.myView.GetIsMustRegText())   //需要识别
        {
            mModifyRegText.clear();
            RegText_();
        }
        else   //已经识别过
            CreateMuliteLineText_();
    }

    //动态创建识别文本框
    public void CreateMuliteLineText_()
    {
        ArrayList<Rect> mMuliteRect = showPointBinding.myView.GetMuliteRect();
        FrameLayout mainLayout = (FrameLayout)findViewById(R.id.mywrtite);

        RemoveMuliteLineText();

        for(int i = 0; i < mMuliteRect.size(); i++)
        {
            Rect rt_ = mMuliteRect.get(i);
            int w = showPointBinding.myView.GetWordRectWidth();
            Rect rt = new Rect(rt_.left, rt_.top + (i-1)*w, rt_.right, rt_.top + i*w );

            ArrayList<RectTxtInfo> rectinfo = GetRectText(rt_);
            String sText = "";
            if(mModifyRegText.size() > 0 && i < mModifyRegText.size())
            {
                sText = mModifyRegText.get(i);
            }
            else {
                for (int j = 0; j < rectinfo.size(); j++) {
                    RectTxtInfo rInfo_ = rectinfo.get(j);
                    sText = sText.concat(rInfo_.getVal());
                }
            }

            //创建一个edittext
            EditText editText = new EditText(this);
            mMuliteEditText.add(editText);
            editText.setTextSize(18);
            editText.setSingleLine(true);  //单行
            //editText.setTextStyle();
            editText.setBackgroundColor(0x00000000);

            if(!sText.isEmpty()) {
                SpannableString spanString = new SpannableString(sText);
                StyleSpan span = new StyleSpan(Typeface.BOLD);

                spanString.setSpan(span, 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                editText.setText(spanString);
            }
            editText.setVisibility(View.VISIBLE);
            //editText.setTextScaleX((float)1.5);

            editText.setTextColor(Color.BLACK);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    SetTextInfo();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            int heightDip= px2dip(this, rt.height());
            int widthDip= px2dip(this, rt.width());
            //确定这个控件的大小和位置
            /*
            FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(widthDip, heightDip);

            lp1.leftMargin= px2dip(this, rt.left);
            lp1.topMargin = px2dip(this, rt.top);
               */
            FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams( rt.width(), rt.height());

            lp1.leftMargin= rt.left;
            lp1.topMargin = rt.top;
            mainLayout.addView(editText, lp1);
        }
        SetTextInfo();
        hideCheckDialog();
        //showPointBinding.myView.RefreshCavans();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public void SetTextInfo()
    {
        mModifyRegText.clear();
        ArrayList<Rect> mMuliteRect = showPointBinding.myView.GetMuliteRect();
        String sText = "";
        int nCount = 0;
        boolean isReturn = false;   //是否换行
        boolean isPreReturn = false;
        for(int i = 0; i < mMuliteRect.size(); i++) {
            Rect rt_ = mMuliteRect.get(i);
            int w = showPointBinding.myView.GetWordRectWidth();

            ArrayList<RectTxtInfo> rectinfo = GetRectText(rt_);
            if(rectinfo.size() > 0)
            {
                nCount = (rectinfo.get(0).getRect().left -  rt_.left) /w ;
                Rect rightRect = rectinfo.get(rectinfo.size()-1).getRect();
                if(rt_.right - rightRect.right > w )
                {
                    isReturn = true;
                }
                else
                    isReturn = false;
            }
            if(nCount > 0 && i != 0) {
                if(!isPreReturn) {
                    sText = sText.concat("\r\n");
                }
            }
            for(int j = 0; j < nCount; j++)
            {
                sText = sText.concat("    ");
            }
            if(i < mMuliteEditText.size()) {
                String s_ = mMuliteEditText.get(i).getText().toString();
                mModifyRegText.add(s_);
                sText = sText.concat(s_);
            }
            //是否在末尾加换行
            if(isReturn)
            {
                sText = sText.concat("\r\n");
                isPreReturn = true;
            }
            else
                isPreReturn = false;
        }
        sRegPingce = sText;
        //showPointBinding.myTextView.setText(sText);;// myEdit.setText();
    }

    public static String toUtf8(String str) {
        String result = null;
        try {
            result = new String(str.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    //获取评测的标题
    public String GetPingceTextTitle()
    {
        String s_ = "";
        String sItem[] = sRegPingce.split("\r\n");
        if(sItem.length > 0) {
            s_ = sItem[0];
        }
      //  s_ = sRegPingce;
        s_ = s_.replaceAll("  ", "");
        return s_;
    }

    //获取评测的正文
    public String GetPingceTexContent() {
        String s_ = "";
        String sItem[] = sRegPingce.split("\r\n");
        for (int i = 1; i < sItem.length; i++) {
            s_ = s_.concat(sItem[i]).concat("\\r\\n");
        }
        if (sItem.length < 2)
            s_ = sRegPingce;
        return s_;
    }

    //动态装载动画窗口
    private void showCheckDialog(String sTitle) {
        if (progressDialog == null) {
            progressDialog = createLoadingDialog(this, sTitle);
        }
        progressDialog.show();
    }

    private void hideCheckDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    public Dialog createLoadingDialog(Context context, String msg) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.progressdialog_new, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
        return loadingDialog;
    }

    // 普通Json数据解析
    public String ParsePingceResult(String strResult) {
        if(TextUtils.isEmpty(strResult))
            return "";
        String s_ = "";
        if(strResult.contains("yqzishu")) {
            if (strResult.endsWith(",}"))
                strResult = strResult.replace(",}", "}");
            if (strResult.contains("\"MeiList\":\"\""))
                strResult = strResult.replace("\"MeiList\":\"\"", "\"MeiList\":[]");
            if (strResult.contains("\"ErrorList\":\"\""))
                strResult = strResult.replace("\"ErrorList\":\"\"", "\"ErrorList\":[]");
            Log.i("lxz3", strResult);
            Gson gson = new Gson();
            ArticalResult articalResult = gson.fromJson(strResult, ArticalResult.class);

            articalResult.comments = articalResult.comments.replaceAll("</br>", "\r\n");
            articalResult.comments = articalResult.comments.replaceAll("<br/>", "\r\n");
            articalResult.comments = articalResult.comments.replaceAll("&nbsp;", " ");

           // "dlshu":"1（段落数）","zishu":"615（字数）","jvshu":"23（句子数）

            s_ = "分数:" + articalResult.bfscore + "\r\n" + "段落数:" + articalResult.dlshu + "\r\n" + "字数:" + articalResult.zishu + "\r\n" + "句数:" + articalResult.jvshu + "\r\n" + "评语:" + articalResult.comments + "\r\n" + "等级:" + articalResult.level + "\r\n" + "归类:" + articalResult.gtype + "\r\n\r\n";
            String sError = "";
            String sMeipi = "";
            for (int i = 0; i < articalResult.MeiList.size(); i++) {
                ArticalResult.MeiList meiList = articalResult.MeiList.get(i);
                sMeipi += "眉批" + (i + 1) + "：\r\n" + "眉批位置：" + meiList.Mxb + "\r\n" + "眉批内容：" + meiList.Mcontent + "\r\n";
            }
            sMeipi += "\r\n";
            s_ += sMeipi;

            for (int i = 0; i < articalResult.ErrorList.size(); i++) {
                ArticalResult.Errorlist errList = articalResult.ErrorList.get(i);
                sError += "错误" + (i + 1) + "：\r\n" + "错误类型：" + errList.EType + "\r\n" + "错误位置：" + errList.Elocation + "\r\n" + "错误句子：" + errList.Esentence + "\r\n" + "错误描述：" + errList.Edescription + "\r\n\r\n";
            }
            s_ += sError;
        }
        else
            s_ = strResult;

        return s_;
    }
}
