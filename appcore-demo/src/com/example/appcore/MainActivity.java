package com.example.appcore;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.HashMap;

import mobi.cangol.mobile.http.extras.PollingHttpClient;
import mobi.cangol.mobile.http.extras.PollingResponseHandler;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.utils.BitmapUtils;

@SuppressLint("ResourceAsColor")
public class MainActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        pollingUnfinishedOrder();


	}
    public void pollingUnfinishedOrder(){
        PollingHttpClient pollingHttpClient = new PollingHttpClient();
        pollingHttpClient.send(
                this,
                "http://139.196.12.103/1/1.0.0/customer/unfinishedOrder?customer_id=31bae825-08f8-469d-841b-1484f3c5abe0",
                null,
                new PollingResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        Log.d("PollingHttpClient onStart");
                    }

                    @Override
                    public boolean isFailResponse(String content) {
                        Log.d("PollingHttpClient isFailResponse:" + content);
                        return true;
                    }

                    @Override
                    public void onFailure(Throwable error, String content) {
                        super.onFailure(error, content);
                        Log.d("PollingHttpClient onFailure:" + content);
                    }

                    @Override
                    public void onSuccess(int statusCode, String content) {
                        super.onSuccess(statusCode, content);
                        Log.d("PollingHttpClient onSuccess:" + content);
                    }

                    @Override
                    public void onPollingFinish(int execTimes, String content) {
                        super.onPollingFinish(execTimes, content);
                        Log.d("PollingHttpClient onPollingFinish:" + content);
                    }
                },
                5, 5000);
    }

}
