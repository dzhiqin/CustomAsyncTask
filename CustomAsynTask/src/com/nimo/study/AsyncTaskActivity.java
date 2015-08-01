package com.nimo.study;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.nimo.study.thread.ProgressBarAsyncTask;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AsyncTaskActivity extends Activity {
	 private Button startTaskBtn;  
	    private ProgressBar progressBar;  
	    private TextView progress_info;  
	      
	    @Override  
	    public void onCreate(Bundle savedInstanceState) {  
	        super.onCreate(savedInstanceState);  
	        setContentView(R.layout.asyntask_layout);  
	          
	        startTaskBtn = (Button)findViewById(R.id.startTaskBtn);  
	        progressBar = (ProgressBar)findViewById(R.id.progressBar);
	        
	        progress_info = (TextView)findViewById(R.id.progress_info);
	        
	        startTaskBtn.setOnClickListener(new OnClickListener() {
	            @Override  
	            public void onClick(View v) {  
//	            	Executor exec = new ThreadPoolExecutor(15, 200, 10,  
//	            	        TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	                ProgressBarAsyncTask asyncTask = new ProgressBarAsyncTask(progress_info, progressBar);  
	                asyncTask.execute(1000);  
	            }  
	        });  
	    }  
	}  
