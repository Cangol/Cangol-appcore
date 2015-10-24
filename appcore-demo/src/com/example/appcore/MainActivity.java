package com.example.appcore;


import android.app.Activity;
import android.os.Bundle;

import mobi.cangol.mobile.CoreApplication;
import mobi.cangol.mobile.logging.Log;
import mobi.cangol.mobile.service.session.SessionService;

public class MainActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        SessionService session=((CoreApplication) this.getApplicationContext()).getSession();
        //session.saveString("key","value="+System.currentTimeMillis());
        Log.e(">>",session.getString("key","ddd"));
	}
}
