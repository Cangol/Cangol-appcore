package com.example.appcore;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

@SuppressLint("ResourceAsColor")
public class MainActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}
}
