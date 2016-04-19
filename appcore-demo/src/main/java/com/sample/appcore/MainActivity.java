package com.example.appcore;


import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.session.SessionService;
import mobi.cangol.mobile.stat.StatAgent;
import mobi.cangol.mobile.utils.TimeUtils;

public class MainActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        SessionService session=((CoreApplication) this.getApplicationContext()).getSession();
        if(session.containsKey("key")){
            Log.d(">>",session.getString("key","ddd"));
        }else{
            session.saveString("key", "value=" + TimeUtils.getCurrentHoursMinutes());
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("app.exit");
        ((CoreApplication)this.getApplicationContext()).exit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //StatAgent.getInstance(this).onActivityResume("MainActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //StatAgent.getInstance(this).onActivityPause("MainActivity");
    }
}
