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


    /**
     * 是否为需要添加照片的目录
     *
     * @param item 当前目录
     * @return
     */
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

    /**
     * 当前路径下的图片数量
     *
     * @param item 当前目录
     * @return
     */
    public static int numberOfPictures(File item) {
        File[] files = item.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return ImageUtils.isImage(pathname);
            }
        });
        return files == null ? 0 : files.length;
    }

    public static String simplifyFileName(String fileName) {
        if (fileName.contains("ID=")) {
            String[] checkPathName = fileName.split(" \\(ID=");
            if (checkPathName != null && checkPathName.length > 1) {
                return checkPathName[0];
            }
        }
        return fileName;

    }

}
