package com.xinyuan522.lib.statusbar;

import android.view.Window;

/**
 * 状态栏接口
 *
 * @author
 * @version
 * @since
 */

interface IStatusBar {
    /**
     * Set the status bar color
     *
     * @param window The window to set the status bar color
     * @param color  Color value
     */
    void setStatusBarColor(Window window, int color);
}
