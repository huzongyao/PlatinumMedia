package com.hzy.platinum.server.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hzy.platinum.server.utils.UUIDUtils;
import com.plutinosoft.platinum.NativeUpnpApi;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NativeUpnpApi.INSTANCE.init();
        String uuid = UUIDUtils.getRandomUUID();
        NativeUpnpApi.INSTANCE.addRenderer("name", true, uuid, 0);
        NativeUpnpApi.INSTANCE.stop();
        NativeUpnpApi.INSTANCE.start();
    }
}
