package com.symbio.i2qcamera.app;

import android.app.Application;
import android.os.StrictMode;

import com.tencent.bugly.crashreport.CrashReport;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        CrashReport.initCrashReport(getApplicationContext());
    }
}
