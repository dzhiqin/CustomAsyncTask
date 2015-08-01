package com.nimo.study;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		findViewById(R.id.threadStartBtn).setOnClickListener(this);
		findViewById(R.id.taskStartBtn).setOnClickListener(this);
		findViewById(R.id.looperStartBtn).setOnClickListener(this);
		
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.threadStartBtn:
			startActivity(new Intent(this, ThreadHandlerActivity.class));
			break;
		case R.id.taskStartBtn:
			startActivity(new Intent(this, AsyncTaskActivity.class));
			break;
		case R.id.looperStartBtn:
			startActivity(new Intent(this, LooperThreadActivity.class));
			break;
		default:
			break;
		}
		
	}

}
