package com.nimo.study;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class LooperThreadActivity extends Activity{ 
	/**
	 * MessageQueue,Message,handler,looper,主线程,子thread之间的关系
	 * 
	 * MessageQueue：消息队列，每个线程最多拥有一个
	 * 
	 * Message 消息对象
	 * 
	 * Looper：与当前线程绑定，保证一个线程只会有一个Looper实例，同时一个Looper也只有一个
	 * MessageQueue不断从MessageQueue中去取消息，交给消息的target属性对应的Handler的dispatchMessage
	 * 去处理
	 * 
	 * Looper是MessageQueue的管理者。每一个MessageQueue都不能脱离Looper而存在，Looper
	 * 对象的创建是通过prepare函数来实现的。同时每一个Looper对象和一个线程关联。通过调用
	 * Looper.myLooper()可以获得当前线程的Looper对象 。创建一个Looper对象时，会同时创建
	 * 一个MessageQueue对象。除了主线程有默认的Looper，其他线程默认是没有MessageQueue对象的
	 * ，所以，不能接受Message。如需要接受，自己定义 一个Looper对象(通过prepare函数),
	 * 这样该线程就有了自己的Looper对象和MessageQueue数据结构了。  Looper从MessageQueue中
	 * 取出Message然后，交由Handler的handleMessage进行处理。处理完成后，调用
	 * Message.recycle()将其放入Message Pool中。
	 * */
      
     private final int MSG_HELLO = 0; 
     private Handler mHandler; 
      
    @Override 
     public void onCreate(Bundle savedInstanceState) { 
         super.onCreate(savedInstanceState); 
         setContentView(R.layout.main); 
         new CustomThread().start();//新建并启动CustomThread实例  
          
        findViewById(R.id.send_btn).setOnClickListener(new OnClickListener() { 
              
             @Override 
             public void onClick(View v) {//点击界面时发送消息  
                 String str = "hello"; 
                 Log.d("Test", "MainThread is ready to send msg:" + str); 
                 mHandler.obtainMessage(MSG_HELLO, str).sendToTarget();//发送消息到CustomThread实例  
                  
             } 
         }); 
          
     } 
      
     class CustomThread extends Thread { 
         @Override 
        public void run() { 
             //建立消息循环的步骤  
            Looper.prepare();//1、初始化Looper  
            //Handler的构造方法，会首先得到当前线程中保存的Looper实例，
            //进而与Looper实例中的MessageQueue关联。
            mHandler = new Handler(){//2、绑定handler到CustomThread实例的Looper对象  
                 public void handleMessage (Message msg) {//3、定义处理消息的方法  
                     switch(msg.what) { 
                     case MSG_HELLO: 
                        Log.d("Test", "CustomThread receive msg:" + (String) msg.obj); 
                     } 
                 } 
             }; 
             Looper.loop();//4、启动消息循环  
         } 
     } 
 } 