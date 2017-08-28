package com.jyh.byzb.common.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 项目名:CJDJCZBS
 * 类描述:系统工具类
 * 创建人:苟蒙蒙
 * 创建日期:2016/11/28.
 */

public class SystemUtils {

//---------------------------------------------------------------------------------------------------屏幕相关-------------------------------------------------------------------------------------------

    /**
     * 判断不同机器的不同分辨率，然后设置不同的devider
     */
    @SuppressWarnings({"deprecation"})
    public static int setDividerHeight(Context context) {
        // TODO Auto-generated method stub
        Display display = ((Activity) context).getWindowManager()
                .getDefaultDisplay();
        int width = display.getWidth();

        // 720×1184
        // 根据宽度width来判断，因为高度有点误差
        if (width >= 720 && width < 1000) {
            return 10;
        } else if (width >= 1080) {
            return 15;
        } else if (width > 480 && width < 720) {
            return 8;
        } else if (width == 480) {
            return 7;
        } else if (width < 480) {
            return 5;
        }

        return 10; // 以上条件都不符合，返回10（默认值）
    }

    public static float getDpi(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //240*320 0.75
        //320*480 1.0
        //480*800 1.5
        //720*1280 2.0
        //1080*1920 3.0
        return metrics.density;
    }

    /**
     * 根据手机的分辨率�?dp 的单�?转成�?px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率�?px(像素) 的单�?转成�?dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatuBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            // e1.printStackTrace();
        }
        return sbar;
    }

//---------------------------------------------------------------------------------------------------Service相关---------------------------------------------------------------------------------------
    /**
     * 判断一个服务是否在后台运行
     *
     * @param mContext
     * @param className
     * @return
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        if (null != mContext) {
            ActivityManager activityManager = (ActivityManager) mContext
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                    .getRunningServices(400);
            if (null == serviceList || !(serviceList.size() > 0)) {
                return false;
            }
            if (serviceList.contains(className)) {
                isRunning = true;
            }
            return isRunning;
        }
        return false;
    }

//---------------------------------------------------------------------------------------------------文件相关---------------------------------------------------------------------------------------
    /**
     * 检查是否存在SD卡
     *
     * @return
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 创建目录
     *
     * @param context
     * @param dirName
     *            文件夹名称
     * @return
     */
    @SuppressWarnings("unused")
    public static File createFileDir(Context context, String dirName) {
        String filePath;
        // 如SD卡已存在，则存储；反之存在data目录下
        if (hasSdcard()) {
            // SD卡路径
            filePath = Environment.getExternalStorageDirectory()
                    + File.separator + dirName;
        } else {
            filePath = context.getCacheDir().getPath() + File.separator
                    + dirName;
        }
        File destDir = new File(filePath);
        if (!destDir.exists()) {
            boolean isCreate = destDir.mkdirs();
        }
        return destDir;
    }

    /**
     * 删除文件（若为目录，则递归删除子目录和文件）
     *
     * @param file
     * @param delThisPath
     *            true代表删除参数指定file，false代表保留参数指定file
     */
    public static void delFile(File file, boolean delThisPath) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            if (subFiles != null) {
                int num = subFiles.length;
                // 删除子目录和文件
                for (int i = 0; i < num; i++) {
                    delFile(subFiles[i], true);
                }
            }
        }
        if (delThisPath) {
            file.delete();
        }
    }

    /**
     * 获取文件大小，单位为byte（若为目录，则包括所有子目录和文件）
     *
     * @param file
     * @return
     */
    public static long getFileSize(File file) {
        long size = 0;
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] subFiles = file.listFiles();
                if (subFiles != null) {
                    int num = subFiles.length;
                    for (int i = 0; i < num; i++) {
                        size += getFileSize(subFiles[i]);
                    }
                }
            } else {
                size += file.length();
            }
        }
        return size;
    }

    /**
     * 保存Bitmap到指定目录
     *
     * @param dir
     *            目录
     * @param fileName
     *            文件名
     * @param bitmap
     * @throws IOException
     */
    public static void savaBitmap(File dir, String fileName, Bitmap bitmap,
                                  String url) {
        if (bitmap == null) {
            return;
        }
        File file = new File(dir, fileName);
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            if (!url.substring(url.lastIndexOf("/") + 1).toUpperCase()
                    .contains("PNG")) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            } else {
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, fos);
            }
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断某目录下文件是否存在
     *
     * @param dir
     *            目录
     * @param fileName
     *            文件名
     * @return
     */
    public static boolean isFileExists(File dir, String fileName) {
        return new File(dir, fileName).exists();
    }
}
