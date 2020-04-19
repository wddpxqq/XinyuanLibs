/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oris.wigdet;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;

/**
 * A [TextureView] that can be adjusted to a specified aspect ratio.
 */
public class AutoFitTextureView  extends TextureView {

    AutoFitTextureView(Context context,AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    private int ratioWidth = 0;
    private int ratioHeight = 0;

    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
     * calculated from the parameters. Note that the actual sizes of parameters don't matter, that
     * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
     *
     * @param width  Relative horizontal size
     * @param height Relative vertical size
     */
     private void setAspectRatio(int width,int height) {
         if (width < 0 || height < 0) {
             throw new IllegalArgumentException("Size cannot be negative.");
         }
         ratioWidth = width;
         ratioHeight = height;
         requestLayout();
     }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        if (ratioWidth == 0 || ratioHeight == 0) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * ratioWidth / ratioHeight) {
                setMeasuredDimension(width, width * ratioHeight / ratioWidth);
            } else {
                setMeasuredDimension(height * ratioWidth / ratioHeight, height);
            }
        }
    }

}
