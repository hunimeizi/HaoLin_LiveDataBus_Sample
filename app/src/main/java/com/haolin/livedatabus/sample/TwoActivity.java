package com.haolin.livedatabus.sample;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * 作者：haoLin_Lee on 2019/04/25 22:40
 * 邮箱：Lhaolin0304@sina.com
 * class:
 */
public class TwoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        LiveDataBus.get().with("MainActivity", HuaWei.class).observe(this, new Observer<HuaWei>() {
            @Override
            public void onChanged(@Nullable HuaWei huaWei) {
                if (huaWei != null)
                    Toast.makeText(TwoActivity.this, huaWei.getType(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendMessageOnClick(View view) {
        HuaWei huaWei = new HuaWei("华为", "P30");
        LiveDataBus.get().with("MainActivity", HuaWei.class).postValue(huaWei);
    }
}
