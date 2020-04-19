package com.oris.wigdet;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class MarqueeTextView extends AppCompatTextView {
    private boolean isMarqueeEnable = false;

    public MarqueeTextView(Context context) {
        super(context);
        setMarqueeEnable(true);
    }

    public MarqueeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setMarqueeEnable(true);
    }

    public MarqueeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setMarqueeEnable(true);
    }

    public void setMarqueeEnable(boolean enable) {
        if (isMarqueeEnable != enable) {
            isMarqueeEnable = enable;
            if (enable) {
                setEllipsize(TextUtils.TruncateAt.MARQUEE);
            } else {
                setEllipsize(TextUtils.TruncateAt.END);
            }
            onWindowFocusChanged(enable);
        }
    }

    public boolean isMarqueeEnable() {
        return isMarqueeEnable;
    }

    @Override
    public boolean isFocused() {
        return isMarqueeEnable;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(isMarqueeEnable, direction, previouslyFocusedRect);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(isMarqueeEnable);
    }
}
