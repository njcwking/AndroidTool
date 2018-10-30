package com.njking.tool;

import android.content.Context;
import android.text.ClipboardManager;

/**
 * @ClassName : ClipboardUtils
 * @Author : 陈伟
 * @Date : 2018/10/30
 * @Description : say something
 */
public class ClipboardUtils {
    /**
     * 复制到剪切板
     * @param context
     * @param text
     */
    public static void copyToClipboard(Context context,String text) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(text);
    }
}
