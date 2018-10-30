package com.njking.tool;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * App详情信息页
 * 功能：
 * 1. 分享应用
 * 2. 查看签名
 * 属性：
 * 安装时间
 * 版本号
 * 版本名称
 * 应用名称
 * 包名
 *
 * @author
 * @date 2018/10/29 11:48
 */
public class AppDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private String appName;

    private String packageName;

    private TextView tvAppName;
    private TextView tvPackageName;
    private TextView tvVersionName;
    private TextView tvVersionCode;
    private TextView tvFirstInstalledTime;
    private TextView tvLastUpdateTime;
    private TextView tvAppSize;
    private TextView tvAppLocation;
    private TextView tvPermissions;

    private TextView tvMD5;

    private TextView tvSHA1;

    private Context mContext;

    public static Intent createIntent(Context context, String packageName, String appName) {
        Intent intent = new Intent(context, AppDetailActivity.class);
        intent.putExtra("packageName", packageName);
        intent.putExtra("appName", appName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);
        mContext = this;

        packageName = getIntent().getStringExtra("packageName");
        appName = getIntent().getStringExtra("appName");

        getSupportActionBar().setTitle(appName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        tvAppName = findViewById(R.id.tvAppName);
        tvPackageName = findViewById(R.id.tvPackageName);
        tvVersionName = findViewById(R.id.tvVersionName);
        tvVersionCode = findViewById(R.id.tvVersionCode);
        tvFirstInstalledTime = findViewById(R.id.tvFirstInstalledTime);
        tvLastUpdateTime = findViewById(R.id.tvLastUpdateTime);
        tvAppSize = findViewById(R.id.tvAppSize);
        tvAppLocation = findViewById(R.id.tvAppLocation);
        tvPermissions = findViewById(R.id.tvPermissions);
        tvMD5 = findViewById(R.id.tvMD5);
        tvSHA1 = findViewById(R.id.tvSHA1);
        String MD5signature = PackageUtils.getAppMD5Signature(mContext, packageName);
        tvMD5.setText("应用M D 5：" + MD5signature);
        tvMD5.setTag(MD5signature);
        String sha1Signature = PackageUtils.getAppSHA1Signature(mContext, packageName);
        tvSHA1.setText("应用SHA1：" + sha1Signature);
        tvSHA1.setTag(sha1Signature);

        findViewById(R.id.btnCopyMD5).setOnClickListener(this);
        findViewById(R.id.btnCopyMD5Without).setOnClickListener(this);
        findViewById(R.id.btnCopySHA1).setOnClickListener(this);
        findViewById(R.id.btnCopySHA1Without).setOnClickListener(this);

        tvPackageName.setText("应用包名：" + packageName);
        //获取应用信息
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
            tvAppName.setText("应用名称：" + packageInfo.applicationInfo.loadLabel(pm));
            tvVersionName.setText("版本名称：" + packageInfo.versionName);
            tvVersionCode.setText("版本号：" + packageInfo.versionCode);
            tvFirstInstalledTime.setText("首次安装时间：" + formatTime(packageInfo.firstInstallTime, "yyyy-MM-dd HH:mm"));
            tvLastUpdateTime.setText("最近更新时间：" + formatTime(packageInfo.lastUpdateTime, "yyyy-MM-dd HH:mm"));
            tvAppLocation.setText("应用位置：" + packageInfo.applicationInfo.sourceDir);
            apkFile = new File(packageInfo.applicationInfo.sourceDir);
            long size = apkFile.length();
            tvAppSize.setText("应用大小：" + Formatter.formatFileSize(mContext, size));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tvPermissions.setText(getString(packageName));
    }
    private File apkFile;

    public String formatTime(long millisecond, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(new Date(millisecond));
    }

    private String getString(String pkgName) {
        StringBuffer sb = new StringBuffer();
        PackageInfo pkgInfo = null;
        PackageManager pm = getPackageManager();
        try {
            pkgInfo = pm.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String[] perms = pkgInfo.requestedPermissions;
        if (perms !=null) {
            for (String permName : perms) {
                sb.append(permName).append('\n');
            /*try {
                PermissionInfo permInfo = pm.getPermissionInfo(permName, 0);
                PermissionGroupInfo pgi = pm.getPermissionGroupInfo(permInfo.group, 0);
                sb.append(permInfo.loadLabel(pm)).append('\n');
                sb.append(permInfo.loadDescription(pm)).append("\n\n");
                sb.append(pgi.loadLabel(pm)).append('\n');
                sb.append(pgi.loadDescription(pm)).append("\n\n\n");
            } catch (PackageManager.NameNotFoundException e) {
                sb.append("\n\n");
            }*/
            }
        }
        if (TextUtils.isEmpty(sb.toString())) {
            sb.append("没有相关权限");
        }
        return sb.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.share:
                final ProgressDialog progressDialog = new ProgressDialog(mContext);
                progressDialog.setMessage("正在复制文件...");
                progressDialog.show();
                Observable.create(new ObservableOnSubscribe<File>() {
                    @Override
                    public void subscribe(ObservableEmitter<File> emitter) throws Exception {
                        File copyApkFile = new File(getExternalFilesDir(null), "apk/"+appName + ".apk");
                        if (copyApkFile.exists()) {
                            copyApkFile.delete();
                        }
                        if (!copyApkFile.getParentFile().exists()) {
                            copyApkFile.getParentFile().mkdirs();
                        }
                        copyFileUsingStream(apkFile,copyApkFile);
                        emitter.onNext(copyApkFile);
                        emitter.onComplete();
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
                        progressDialog.dismiss();
                        shareFile(file);
                    }
                });
                break;
            case R.id.openApp:
                try {
                    PackageInfo pi = getPackageManager().getPackageInfo(packageName, 0);
                    Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
                    resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    resolveIntent.setPackage(pi.packageName);
                    List<ResolveInfo> apps = getPackageManager().queryIntentActivities(resolveIntent, 0);
                    if (apps != null && apps.size() > 0) {
                        ResolveInfo ri = apps.iterator().next();
                        if (ri != null ) {
                            String packageName = ri.activityInfo.packageName;
                            String className = ri.activityInfo.name;

                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_LAUNCHER);
                            ComponentName cn = new ComponentName(packageName, className);
                            intent.setComponent(cn);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
                            startActivity(intent);
                        }
                    }
                    else{
                        Toast.makeText(mContext, "无法打开该应用程序", Toast.LENGTH_SHORT).show();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_detail, menu);
        return true;
    }
    /**
     *
     * @Title: copyFileUsingStream
     * @Description: 使用Stream拷贝文件
     * @param: @param source
     * @param: @param dest
     * @param: @throws IOException
     * @return: void
     * @throws
     */
    public static void copyFileUsingStream(File source, File dest)
            throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            if(is != null) {
                is.close();
            }
            if(os != null) {
                os.close();
            }
        }
    }

    private void shareFile(File file) {
        if (file != null && file.exists()) {
            Uri contentUri = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".myprovider", file);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.setType("application/vnd.android.package-archive");
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent,"分享文件"));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCopyMD5: {
                String md5Tag = (String) tvMD5.getTag();
                copyToClipboard(md5Tag);
            }
                break;
            case R.id.btnCopyMD5Without:
            {
                String md5Tag = (String) tvMD5.getTag();
                md5Tag = md5Tag.replace(":","");
                copyToClipboard(md5Tag);
            }
                break;
            case R.id.btnCopySHA1:
            {
                String sha1Tag = (String) tvSHA1.getTag();
                copyToClipboard(sha1Tag);
            }
                break;
            case R.id.btnCopySHA1Without:
            {
                String sha1Tag = (String) tvSHA1.getTag();
                sha1Tag = sha1Tag.replace(":","");
                copyToClipboard(sha1Tag);
            }
                break;
        }
    }

    private void copyToClipboard(String text) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(text);
        Toast.makeText(mContext, "复制成功", Toast.LENGTH_SHORT).show();
    }
}
