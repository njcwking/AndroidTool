package com.njking.tool;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.support.annotation.IntDef;
import android.text.TextUtils;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * @ClassName : PackageUtils
 * @Author : 陈伟
 * @Date : 2018/10/29
 * @Description : say something
 */
public class PackageUtils {
    /**
     * MD5签名
     */
    public static final String MD5 = "MD5";
    /**
     * SHA1签名
     */
    public static final String SHA1 = "SHA1";

    /**
     * 所有应用
     */
    public static final int TYPE_ALL_PACKAGE = 0;

    /**
     * 系统内置应用
     */
    public static final int TYPE_SYSTEM_PACKAGE = 1;

    /**
     * 用户应用（可卸载应用）
     */
    public static final int TYPE_USER_PACKAGE = 2;

    @IntDef({TYPE_ALL_PACKAGE, TYPE_SYSTEM_PACKAGE, TYPE_USER_PACKAGE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AppType {
    }

    /**
     * 应用程序名称
     * Map对应的值为String类型
     */
    public static final String APP_S_NAME = "app_name";

    /**
     * 应用包名
     * Map对应的值为String类型
     */
    public static final String APP_S_PACKAGE_NAME = "package_name";

    /**
     * 应用版本名称
     * Map对应的值为String类型
     */
    public static final String APP_S_VERSION_NAME = "version_name";

    /**
     * 应用版本号
     * Map对应的值为int类型
     */
    public static final String APP_I_VERSION_CODE = "version_code";

    /**
     * 应用首次安装的时间
     * Map对应的值为long类型
     */
    public static final String APP_L_FIRST_INSTALLED_TIME = "first_installed_time";

    /**
     * 应用最近一次更新的时间
     * Map对应的值为long类型
     */
    public static final String APP_L_LAST_UPDATE_TIME = "last_update_time";

    /**
     * 应用大小
     * Map对应的值为long类型
     */
    public static final String APP_L_SIZE = "size";

    /**
     * 应用存储位置
     * Map对应的值为String类型
     */
    public static final String APP_S_STORAGE = "storage";

    /**
     * 应用程序图标
     * Map对应的值为Drawable类型
     */
    public static final String APP_DRAWABLE_ICON = "icon";

    /**
     * 获取应用的MD5签名
     *
     * @param context     上下文
     * @param packageName 应用包名
     * @return
     */
    public static String getAppMD5Signature(Context context, String packageName) {
        return getAppSignature(context, packageName, MD5);
    }

    /**
     * 获取应用的SHA1信息
     *
     * @param context     上下文
     * @param packageName 应用包名
     * @return
     */
    public static String getAppSHA1Signature(Context context, String packageName) {
        return getAppSignature(context, packageName, SHA1);
    }

    /**
     * 获取应用签名信息
     *
     * @param context      上下文
     * @param packageName  应用包名
     * @param signatureWay 签名方式
     * @return
     */
    private static String getAppSignature(Context context, String packageName, String signatureWay) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);

            byte[] cert = info.signatures[0].toByteArray();

            MessageDigest md = MessageDigest.getInstance(signatureWay);
            byte[] publicKey = md.digest(cert);
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            if (hexString.length() > 0) {
                hexString.deleteCharAt(hexString.length() - 1);
            }
            return hexString.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取所有应用
     *
     * @param context   应用上下文
     * @param searchKey 关键词
     * @param appType   应用类型
     * @return
     */
    public static List<PackageInfo> getAllPackage(Context context, String searchKey, @AppType int appType) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> appInfos = pm.getInstalledPackages(0);
        List<PackageInfo> filterApps = new ArrayList<>();
        for (PackageInfo appInfo : appInfos) {
            if (!TextUtils.isEmpty(searchKey)) {
                if (!(appInfo.applicationInfo.loadLabel(pm).toString().contains(searchKey) || appInfo.packageName.contains(searchKey))) {
                    continue;
                }
            }
            if ((appInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && appType == TYPE_USER_PACKAGE) {
                // 未系统应用
                filterApps.add(appInfo);
            } else if ((appInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 && appType == TYPE_SYSTEM_PACKAGE) {
                //系统应用
                filterApps.add(appInfo);
            } else if (appType == TYPE_ALL_PACKAGE) {
                filterApps.add(appInfo);
            }
        }
        return filterApps;
    }

    /**
     * 获取某个应用的信息
     *
     * @param context     应用上下文
     * @param packageName 包名
     * @param loadIcon    是否加载图标
     * @return
     */
    public static HashMap<String, Object> getAppInfo(Context context, String packageName, boolean loadIcon) {
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo != null) {
            HashMap<String, Object> appInfo = new HashMap<>();
            appInfo.put(APP_S_NAME, packageInfo.applicationInfo.loadLabel(pm));
            appInfo.put(APP_S_PACKAGE_NAME, packageName);
            appInfo.put(APP_S_VERSION_NAME, packageInfo.versionName);
            appInfo.put(APP_I_VERSION_CODE, packageInfo.versionCode);
            appInfo.put(APP_L_FIRST_INSTALLED_TIME, packageInfo.firstInstallTime);
            appInfo.put(APP_L_LAST_UPDATE_TIME, packageInfo.lastUpdateTime);
            appInfo.put(APP_S_STORAGE, packageInfo.applicationInfo.sourceDir);
            File apkFile = new File(packageInfo.applicationInfo.sourceDir);
            long size = apkFile.length();
            appInfo.put(APP_L_SIZE, size);
            if (loadIcon) {
                appInfo.put(APP_DRAWABLE_ICON, packageInfo.applicationInfo.loadIcon(pm));
            }
            return appInfo;
        }
        return null;
    }

    /**
     * 获取应用需要的权限信息
     *
     * @param context     应用上下文
     * @param packageName 包名
     * @return
     */
    public static String[] getAppPermission(Context context, String packageName) {
        PackageInfo pkgInfo = null;
        PackageManager pm = context.getPackageManager();
        try {
            pkgInfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return pkgInfo.requestedPermissions;
    }

    /**
     * 获取权限信息
     *
     * @param context        上下文
     * @param permissionName 权限名称
     * @return
     */
    public static String[] getPermissionInfo(Context context, String permissionName) {
        PackageManager pm = context.getPackageManager();
        try {
            PermissionInfo permissionInfo = pm.getPermissionInfo(permissionName, 0);
            CharSequence label = permissionInfo.loadLabel(pm);
            CharSequence description = permissionInfo.loadDescription(pm);
            return new String[]{label == null ? "" : label.toString(), description == null ? "" : description.toString()};
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取权限组信息
     *
     * @param context        上下文
     * @param permissionName 权限名称
     * @return
     */
    public static String[] getPermissionGroupInfo(Context context, String permissionName) {
        PackageManager pm = context.getPackageManager();
        try {
            PermissionGroupInfo permissionGroupInfo = pm.getPermissionGroupInfo(permissionName, 0);
            if (permissionGroupInfo != null) {
                CharSequence label = permissionGroupInfo.loadLabel(pm);
                CharSequence description = permissionGroupInfo.loadDescription(pm);
                return new String[]{label == null ? "" : label.toString(), description == null ? "" : description.toString()};
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
