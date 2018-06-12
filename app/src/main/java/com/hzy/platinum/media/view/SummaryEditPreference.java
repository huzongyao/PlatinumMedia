package com.hzy.platinum.media.view;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * Created by huzongyao on 2018/6/11.
 * A kind of EditTextPreference that input text will show in summary
 */

public class SummaryEditPreference extends EditTextPreference {

    public SummaryEditPreference(Context context) {
        super(context);
    }

    public SummaryEditPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public CharSequence getSummary() {
        if (super.getSummary() == null)
            return null;
        String summary = super.getSummary().toString();
        return String.format(summary, getText());
    }
}
