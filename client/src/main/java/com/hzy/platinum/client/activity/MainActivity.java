package com.hzy.platinum.client.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hzy.platinum.client.R;
import com.plutinosoft.platinum.NativeUpnpApi;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DLNA Digital Media Controller (DMC).
 *
 * <p>Discovers renderers on the local network via the native control point and
 * sends cast commands (SetAVTransportURI / Play / Pause / Stop / Seek / SetVolume)
 * to the selected renderer.
 */
public class MainActivity extends AppCompatActivity implements NativeUpnpApi.NativeDelegate {

    private ListView mDeviceList;
    private EditText mUriInput;
    private EditText mSeekInput;
    private SeekBar mVolumeBar;
    private TextView mStatusText;
    private Button mBtnCast;

    private final List<DeviceItem> mDevices = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;
    private String mSelectedUuid;
    private NativeUpnpApi mApi;

    private static class DeviceItem {
        String uuid;
        String name;

        DeviceItem(String uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }

        @Override
        public String toString() {
            return name + "\n" + uuid;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDeviceList = findViewById(R.id.device_list);
        mUriInput = findViewById(R.id.input_uri);
        mSeekInput = findViewById(R.id.input_seek);
        mVolumeBar = findViewById(R.id.volume_bar);
        mStatusText = findViewById(R.id.text_status);
        mBtnCast = findViewById(R.id.btn_cast);

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        mDeviceList.setAdapter(mAdapter);
        mDeviceList.setOnItemClickListener((parent, view, position, id) -> {
            if (position < mDevices.size()) {
                mSelectedUuid = mDevices.get(position).uuid;
                mStatusText.setText("已选择：" + mDevices.get(position).name);
                mBtnCast.setEnabled(true);
            }
        });

        mVolumeBar.setProgress(100);
        mVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mSelectedUuid != null) {
                    Map<String, Object> extra = new HashMap<>();
                    extra.put("volume", progress);
                    extra.put("channel", "Master");
                    invoke("SetVolume", extra);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        findViewById(R.id.btn_cast).setOnClickListener(v -> cast());
        findViewById(R.id.btn_play).setOnClickListener(v -> {
            Map<String, Object> extra = new HashMap<>();
            extra.put("speed", "1");
            invoke("Play", extra);
        });
        findViewById(R.id.btn_pause).setOnClickListener(v -> invoke("Pause", null));
        findViewById(R.id.btn_stop).setOnClickListener(v -> invoke("Stop", null));
        findViewById(R.id.btn_seek).setOnClickListener(v -> {
            String t = mSeekInput.getText().toString().trim();
            if (!TextUtils.isEmpty(t)) {
                Map<String, Object> extra = new HashMap<>();
                extra.put("unit", "REL_TIME");
                extra.put("target", t);
                invoke("Seek", extra);
            }
        });

        mApi = NativeUpnpApi.INSTANCE.init(param -> {
            NativeUpnpApi.dispatch(param, this);
            return null;
        });
        mApi.addController(true, 0);
        mApi.start();
    }

    private void cast() {
        if (mSelectedUuid == null) {
            Toast.makeText(this, "请先选择一个渲染设备", Toast.LENGTH_SHORT).show();
            return;
        }
        String uri = mUriInput.getText().toString().trim();
        if (TextUtils.isEmpty(uri)) {
            Toast.makeText(this, "请输入媒体 URL", Toast.LENGTH_SHORT).show();
            return;
        }
        // SetAVTransportURI then Play
        Map<String, Object> setUri = new HashMap<>();
        setUri.put("uri", uri);
        setUri.put("metadata", "");
        invoke("SetAVTransportURI", setUri);

        Map<String, Object> play = new HashMap<>();
        play.put("speed", "1");
        invoke("Play", play);
    }

    private void invoke(String action, Map<String, Object> extra) {
        if (mSelectedUuid == null) return;
        try {
            JSONObject obj = new JSONObject();
            obj.put("uuid", mSelectedUuid);
            obj.put("action", action);
            obj.put("instance_id", 0);
            // merge extra fields
            if (extra != null) {
                for (Map.Entry<String, Object> e : extra.entrySet()) {
                    obj.put(e.getKey(), e.getValue());
                }
            }
            mApi.invokeAction(obj);
        } catch (Exception e) {
            Toast.makeText(this, "指令发送失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRendererEvent(NativeUpnpApi.RendererEvent event) {
        // not used on the controller side
    }

    @Override
    public void onDeviceEvent(NativeUpnpApi.DeviceEvent event) {
        runOnUiThread(() -> {
            if ("added".equals(event.action) || "ready".equals(event.action)) {
                boolean exists = false;
                for (DeviceItem d : mDevices) {
                    if (d.uuid.equals(event.uuid)) {
                        exists = true;
                        break;
                    }
                }
                if (!exists && event.uuid != null && !event.uuid.isEmpty()) {
                    mDevices.add(new DeviceItem(event.uuid, event.name));
                    refreshList();
                }
            } else if ("removed".equals(event.action)) {
                mDevices.removeIf(d -> d.uuid.equals(event.uuid));
                if (event.uuid.equals(mSelectedUuid)) mSelectedUuid = null;
                refreshList();
            }
        });
    }

    @Override
    public void onActionResult(String action, int code) {
        runOnUiThread(() -> Toast.makeText(this,
                action + " -> " + (code == 0 ? "成功" : ("失败(" + code + ")")),
                Toast.LENGTH_SHORT).show());
    }

    private void refreshList() {
        List<String> names = new ArrayList<>();
        for (DeviceItem d : mDevices) names.add(d.toString());
        mAdapter.clear();
        mAdapter.addAll(names);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        if (mApi != null) mApi.stop();
        super.onDestroy();
    }
}
