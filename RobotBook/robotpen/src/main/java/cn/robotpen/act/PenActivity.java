package cn.robotpen.act;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.zhiyuweilai.tiger.robotpen.R;
import com.zhiyuweilai.tiger.robotpen.databinding.ActivityPenBinding;

import cn.robotpen.core.PenManage;
import cn.robotpen.core.services.UsbPenService;
import cn.robotpen.act.connect.DeviceActivity;
import cn.robotpen.act.show.StartActivity;
import cn.robotpen.model.entity.DeviceEntity;

public class PenActivity extends Activity {


    PenManage mPenManage;
    ActivityPenBinding penBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        penBinding = DataBindingUtil.setContentView(this,R.layout.activity_pen);
        penBinding.mainLinearDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PenActivity.this, DeviceActivity.class);
                startActivity(intent);
            }
        });

        penBinding.mainLinearDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PenActivity.this, StartActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckDevice();
    }

    /*
    * 检测设备连接
    */
    private void CheckDevice(){
        if (null == mPenManage) {
            mPenManage = new PenManage(this, UsbPenService.TAG); //这样新建服务会记住连接方式
        }
        DeviceEntity device =  mPenManage.getLastDevice(PenActivity.this);
        if(device!=null){
            penBinding.mainConnectStatus.setText("上次连接设备："+device.getName()+"。 ");
            penBinding.mainConnectDevice.setText("类型为："+device.getDeviceType().name());
        }else {
            penBinding.mainConnectStatus.setText("未连接设备！");
            penBinding.mainConnectDevice.setText("");
        }
    }

}
