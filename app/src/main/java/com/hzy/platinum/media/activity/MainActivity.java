package com.hzy.platinum.media.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hzy.platinum.media.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_simple).setOnClickListener(v ->
                startActivity(new Intent(this, SimpleActivity.class)));
    }
}
