package com.hzy.platinum.media.activity;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.hzy.platinum.media.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huzongyao on 2018/3/27.
 * About activity
 */

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.textview_version)
    TextView mTextviewVersion;
    @BindView(R.id.about_content)
    TextView mAboutContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getPackageVersionInfo();
        mAboutContent.setText(R.string.about_content);
    }

    private void getPackageVersionInfo() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            mTextviewVersion.setText(packageInfo.versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
