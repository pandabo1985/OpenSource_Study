package com.example.circularprogressdrawable;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		LinearLayout ll = (LinearLayout) findViewById(R.id.main_bg);
	
		CustomView customView = new CustomView(this);
		LayoutParams params  = new LayoutParams(100, 100);
		
		ll.addView(customView,params);
		
	}

	

}
