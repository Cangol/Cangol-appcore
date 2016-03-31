package com.example.appcore;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
import mobi.cangol.mobile.service.conf.ConfigService;
import mobi.cangol.mobile.service.crash.CrashReportListener;
import mobi.cangol.mobile.service.crash.CrashService;
import mobi.cangol.mobile.stat.StatAgent;
import mobi.cangol.mobile.utils.FileUtils;

/**
 * Created by weixuewu on 15/9/14.
 */
public class MobileApplication extends CoreApplication {
    public void onCreate() {
        this.setDevMode(true);
        super.onCreate();
        init();
    }
    public void init(){
        //ConfigService
        Log.d("--------------CrashService begin---------------");
        CrashService crashService = (CrashService) getAppService(AppService.CRASH_SERVICE);
        crashService.setDebug(true);
        if(!this.isDevMode()){
            Log.d("report crash");
            crashService.report(new CrashReportListener(){

                @Override
                public void report(String path,String error,String position,String context,String timestamp,String fatal) {
                    Log.d("sendException " + error);
                    StatAgent.getInstance(MobileApplication.this)
                            .send(StatAgent.Builder.createException(error, position, context, timestamp, fatal));
                    FileUtils.delFileAsync(path);
                }

            });
        }else{
            Log.d("DevMode="+this.isDevMode()+",don't report crash");
        }
        Log.d("--------------CrashService begin---------------");

        //ConfigService
        Log.d("--------------ConfigService begin---------------");
        ConfigService configService= (ConfigService) getAppService(AppService.CONFIG_SERVICE);
        Log.d("getAppDir="+configService.getAppDir());
        Log.d("getCacheDir="+configService.getCacheDir());
        Log.d("getDownloadDir="+configService.getDownloadDir());
        Log.d("getUpgradeDir="+configService.getUpgradeDir());
        Log.d("getTempDir="+configService.getTempDir());
        Log.d("getImageDir="+configService.getImageDir());
        Log.d(" ");
        boolean result=configService.setAppDir("/sdcard/appcore");
        Log.d("setAppDir="+result);
        Log.d("getAppDir exists="+configService.getAppDir().exists());
        Log.d("getAppDir="+configService.getAppDir());
        Log.d("getCacheDir="+configService.getCacheDir());
        Log.d("getDownloadDir="+configService.getDownloadDir());
        Log.d("getUpgradeDir="+configService.getUpgradeDir());
        Log.d("getTempDir="+configService.getTempDir());
        Log.d("getImageDir="+configService.getImageDir());
        Log.d("--------------ConfigService end---------------");
    }

}
