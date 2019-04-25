package com.haolin.livedatabus.sample;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LiveDataBus.get().with("MainActivity", HuaWei.class).observe(this, new Observer<HuaWei>() {
            @Override
            public void onChanged(@Nullable HuaWei huaWei) {
                if (huaWei != null)
                    Toast.makeText(MainActivity.this, huaWei.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendMessageOnClick(View view) {
        HuaWei huaWei = new HuaWei("华为","P30Pro");
        LiveDataBus.get().with("MainActivity",HuaWei.class).postValue(huaWei);
    }

    public void jumpActivityOnClick(View view) {

        startActivity(new Intent().setClass(MainActivity.this,TwoActivity.class));

    }
}
