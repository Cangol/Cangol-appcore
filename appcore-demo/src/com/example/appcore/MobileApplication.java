package com.example.appcore;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.AppService;
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
        Log.d("初始化CrashService");
        CrashService crashService = (CrashService) getAppService(AppService.CRASH_SERVICE);
        crashService.setDebug(false);
        Log.d("开发模式 不报告crash");
        if(this.isDevMode())
            crashService.report(new CrashReportListener(){

                @Override
                public void report(String path,String error,String position,String context,String timestamp,String fatal) {
                    Log.d("sendException " + error);
                    StatAgent.getInstance(MobileApplication.this)
                            .send(StatAgent.Builder.createException(error, position, context, timestamp, fatal));
                    FileUtils.delFileAsync(path);
                }

            });
    }

}
