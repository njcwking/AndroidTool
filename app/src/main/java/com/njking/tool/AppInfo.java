package com.njking.tool;

/**
 * @ClassName : AppInfo
 * @Author : 陈伟
 * @Date : 2018/10/29
 * @Description : say something
 */
public class AppInfo {
    private String appName;
    private String packageName;
    private String versionName;
    private int versionCode;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }
}
