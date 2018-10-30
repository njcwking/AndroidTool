package com.njking.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @ClassName : FileUtils
 * @Author : 陈伟
 * @Date : 2018/10/30
 * @Description : say something
 */
public class FileUtils {
    /**
     * @throws
     * @Title: copyFileUsingStream
     * @Description: 使用Stream拷贝文件
     * @param: @param source
     * @param: @param dest
     * @param: @throws IOException
     * @return: void
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
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }
}
