package com.njking.tool;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

//    public static List<ResolveInfo> get
}
