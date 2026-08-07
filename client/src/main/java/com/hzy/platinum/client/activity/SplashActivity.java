package com.hzy.platinum.client.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hzy.platinum.client.databinding.ActivitySplashBinding;
import com.plutinosoft.platinum.NativeUpnpApi;

public class SplashActivity extends Activity {

    private ActivitySplashBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.textVersion.setText("Platinum: " + NativeUpnpApi.getVersionString());
        binding.textVersion.postDelayed(this::startMainActivity, 1000);
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
