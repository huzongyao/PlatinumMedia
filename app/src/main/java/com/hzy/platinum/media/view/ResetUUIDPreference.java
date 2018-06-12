package com.hzy.platinum.media.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.hzy.platinum.media.R;
import com.hzy.platinum.media.utils.UUIDUtils;

/**
 * Created by huzongyao on 2018/6/11.
 * A kind of preference that could show and generate UUID
 */

public class ResetUUIDPreference extends DialogPreference {

    private String mText;

    public ResetUUIDPreference(Context context) {
        this(context, null);
    }

    public ResetUUIDPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDefaultValue(UUIDUtils.getRandomUUID());
    }

    @Override
    public CharSequence getSummary() {
        if (super.getSummary() == null)
            return null;
        String summary = super.getSummary().toString();
        return String.format(summary, mText);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setText(restoreValue ? getPersistedString(mText) : (String) defaultValue);
    }

    @Override
    protected View onCreateDialogView() {
        setDialogMessage(R.string.pref_dialog_title_uuid);
        return super.onCreateDialogView();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            // regenerate a uuid
            String value = UUIDUtils.getRandomUUID();
            if (callChangeListener(value)) {
                setText(value);
            }
        }
    }

    public void setText(String text) {
        boolean changed = !TextUtils.equals(mText, text);
        mText = text;
        persistString(text);
        if (changed) {
            notifyDependencyChange(shouldDisableDependents());
            notifyChanged();
        }
    }

    public String getText() {
        return mText;
    }
}
