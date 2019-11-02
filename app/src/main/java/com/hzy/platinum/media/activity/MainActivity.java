package com.hzy.platinum.media.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.SnackbarUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hzy.platinum.media.R;
import com.hzy.platinum.media.event.ServerStateEvent;
import com.hzy.platinum.media.instance.ServerInstance;
import com.hzy.platinum.media.service.DLNAService;
import com.hzy.platinum.media.utils.UUIDUtils;
import com.plutinosoft.platinum.ServerParams;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    private SharedPreferences mPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        EventBus.getDefault().register(this);
        initFabStatue();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.main_menu_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initFabStatue() {
        ServerInstance.State state = ServerInstance.INSTANCE.getState();
        setFabStatus(state);
    }

    /**
     * When The Server status changed, update the UI
     *
     * @param event hold server status
     */
    @SuppressWarnings("UnusedDeclaration")
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onServerStateChange(ServerStateEvent event) {
        ServerInstance.State state = event.getState();
        setFabStatus(state);
        switch (state) {
            case RUNNING:
                SnackbarUtils.with(mFab).setMessage(getString(R.string.server_started)).show();
                break;
            case IDLE:
                SnackbarUtils.with(mFab).setMessage(getString(R.string.server_stopped)).show();
                break;
        }
    }

    private void setFabStatus(ServerInstance.State state) {
        switch (state) {
            case IDLE:
                mFab.setImageResource(R.drawable.ic_service_idle);
                break;
            case RUNNING:
                mFab.setImageResource(R.drawable.ic_service_active);
                break;
            case STARTING:
            case STOPPING:
                mFab.setImageResource(R.drawable.ic_service_busy);
                break;
        }
    }

    @OnClick(R.id.fab)
    public void onFabButtonClicked() {
        ServerInstance.State state = ServerInstance.INSTANCE.getState();
        switch (state) {
            case IDLE:
                startServerService();
                break;
            case RUNNING:
                stopServerService();
                break;
        }
    }

    /**
     * Start the server
     */
    private void startServerService() {
        Intent intent = new Intent(this, DLNAService.class);
        intent.putExtra(DLNAService.EXTRA_SERVER_PARAMS, loadServerParams());
        startService(intent);
    }

    /**
     * Stop the server service
     */
    private void stopServerService() {
        Intent intent = new Intent(this, DLNAService.class);
        stopService(intent);
    }

    /**
     * load Params from shared preferences
     * if preferences are not exists, save defaults
     *
     * @return params
     */
    private ServerParams loadServerParams() {
        String key = getString(R.string.pref_server_name_key);
        String name = mPreference.getString(key, getString(R.string.app_name));
        if (!mPreference.contains(key)) {
            mPreference.edit().putString(key, name).apply();
        }
        key = getString(R.string.pref_if_show_ip_key);
        boolean showIp = mPreference.getBoolean(key, true);
        if (!mPreference.contains(key)) {
            mPreference.edit().putBoolean(key, showIp).apply();
        }
        key = getString(R.string.pref_uuid_key);
        String uuid = mPreference.getString(key, UUIDUtils.getRandomUUID());
        if (!mPreference.contains(key)) {
            mPreference.edit().putString(key, uuid).apply();
        }
        return new ServerParams(name, showIp, uuid);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
