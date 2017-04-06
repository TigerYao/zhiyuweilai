package cn.robotpen.act.connect;

import android.app.ActivityGroup;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.zhiyuweilai.tiger.robotpen.R;
import com.zhiyuweilai.tiger.robotpen.databinding.ActivityMyDeviceBinding;

import cn.robotpen.core.PenManage;
import cn.robotpen.core.services.UsbPenService;
import cn.robotpen.model.entity.DeviceEntity;

public class DeviceActivity extends ActivityGroup {

    PenManage mPenManage;
    ActivityMyDeviceBinding myDeviceBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDeviceBinding = DataBindingUtil.setContentView(this,R.layout.activity_my_device);
        myDeviceBinding.gotoUsb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(DeviceActivity.this, USBConnectActivity.class);
                startActivity(intent);
            }
        });
        myDeviceBinding.gotoBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeviceActivity.this, BleConnectActivity.class);
                startActivity(intent);
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
            mPenManage = new PenManage(this, UsbPenService.TAG); //这样新建服务会记住连接方式
        }
       DeviceEntity device =  mPenManage.getLastDevice(DeviceActivity.this);
        if(device!=null){
            myDeviceBinding.devicesStatus.setText("上次连接设备："+device.getName()+"。 "+"类型为："+device.getDeviceType().name());
        }else{
            myDeviceBinding.devicesStatus.setText("未连接设备！");
        }
    }


}
