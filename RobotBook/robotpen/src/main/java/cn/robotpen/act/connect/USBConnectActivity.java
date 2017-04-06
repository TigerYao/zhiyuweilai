package cn.robotpen.act.connect;

import com.zhiyuweilai.tiger.robotpen.R;
import com.zhiyuweilai.tiger.robotpen.databinding.ActivityUsbconnectBinding;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import cn.robotpen.core.PenManage;
import cn.robotpen.core.services.PenService;
import cn.robotpen.core.services.UsbPenService;
import cn.robotpen.model.entity.DeviceEntity;
import cn.robotpen.model.symbol.ConnectState;
import cn.robotpen.model.symbol.DeviceType;
import cn.robotpen.model.symbol.SceneType;

public class USBConnectActivity extends Activity {


    PenManage mPenManage;
    ActivityUsbconnectBinding usbconnectBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usbconnectBinding = DataBindingUtil.setContentView(this,R.layout.activity_usbconnect);
        usbconnectBinding.usbBtn.setOnClickListener(new View.OnClickListener() { //当设备已提前连接或者连接失效后，可以通过点击进行重连
            @Override
            public void onClick(View view) {
                CheckDevice();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        CheckDevice();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPenManage != null)
            mPenManage.disconnectDevice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPenManage != null)
            mPenManage.shutdown(); //退出Activity时将服务释放，方便其他地方继续使用
    }
    /*
      * 检测设备连接
      */
    private void CheckDevice(){
        if (null == mPenManage) {
            PenManage.setServiceType(USBConnectActivity.this, UsbPenService.TAG);//这样新建服务会记住连接方式
            mPenManage = new PenManage(this, UsbPenService.TAG); //这样新建服务会记住连接方式
            mPenManage.setScanTime(2000);
            mPenManage.scanDevice(null);
            mPenManage.setOnConnectStateListener(onConnectStateListener);
        } else {
            mPenManage.scanDevice(null);
        }
    }

    /*
      * 此处监听是为了弹出授权
      */
    private PenService.OnConnectStateListener onConnectStateListener = new PenService.OnConnectStateListener() {
        @Override
        public void stateChange(String arg0, ConnectState arg1) {
            if (arg1 == ConnectState.CONNECTED) {
                DeviceEntity device = mPenManage.getConnectDevice();
                if(null!=device){
                    String device_name = device.getName();
                    DeviceType dp = device.getDeviceType();
                    SceneType sceneType = SceneType.getSceneType(false,dp);//根据设备型号获取场景模式 falsew为竖屏
                    mPenManage.setSceneObject(sceneType);
                    usbconnectBinding.usbstatus.setText("已连接："+device_name+"。 "+"类型为："+dp.name());
                    mPenManage.saveLastDevice(USBConnectActivity.this, device);
                }
            } else if (arg1 == ConnectState.DISCONNECTED) {
                usbconnectBinding.usbstatus.setText("已断开连接!");
            }else
                usbconnectBinding.usbstatus.setText("未连接设备！");
        }
    };
}
