package com.symbio.i2qcamera.base;

import android.os.Environment;

import java.io.File;

public interface Config {

    String BASE_PATH = Environment.getExternalStorageDirectory().toString() + File.separator + "I2Q Camera" + File.separator + "Jobs";
    String EMPTY_DIR_TEXT = "No Job can be operated.\nPress OK to quit.";
}
