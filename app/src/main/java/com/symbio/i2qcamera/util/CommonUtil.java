package com.symbio.i2qcamera.util;

import com.blankj.utilcode.util.ImageUtils;

import java.io.File;
import java.io.FileFilter;

public class CommonUtil {

    public static boolean isNeedShowDocIcon(File item) {
        String name = item.getName();
        boolean check = name.contains("Check") || name.contains("check") || name.contains("(ID=");
        return check;
    }

    public static boolean isNeedAddIMG(File item) {
        File[] files = item.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        boolean isLastPath = false;
        if (files == null || files.length == 0) {
            isLastPath = true;
        }
        return isLastPath;
    }

    public static int isNeedShowNum(File item) {
        File[] files = item.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return ImageUtils.isImage(pathname);
            }
        });
        return files == null ? 0 : files.length;
    }

}
