package com.njking.tool;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

/**
 * @ClassName : IntentUtils
 * @Author : 陈伟
 * @Date : 2018/10/30
 * @Description : 调用Intent打开系统应用
 */
public class IntentUtils {

    /**
     * 打开第三方应用
     *
     * @param context     应用上下文
     * @param packageName 应用包名
     * @return
     */
    public static boolean openApp(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pi != null) {
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveIntent.setPackage(pi.packageName);
            List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
            if (apps != null && apps.size() > 0) {
                ResolveInfo ri = apps.iterator().next();
                if (ri != null) {
                    String appPackageName = ri.activityInfo.packageName;
                    String className = ri.activityInfo.name;
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    ComponentName cn = new ComponentName(appPackageName, className);
                    intent.setComponent(cn);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 调用系统原生的文件分享
     * Android7.0及以上需要在AndroidManifest.xml中注册FileProvider，将需要分享的文件目录提供给第三方应用,并指定读写权限
     *
     * @param context    应用上下文
     * @param contentUri 文件路径
     * @return
     */
    public static boolean shareApkFile(Context context, Uri contentUri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.setType("application/vnd.android.package-archive");
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(shareIntent, 0);
        if (apps != null && apps.size() > 0) {
            context.startActivity(Intent.createChooser(shareIntent, "分享文件"));
            return true;
        }
        return false;
    }
}
