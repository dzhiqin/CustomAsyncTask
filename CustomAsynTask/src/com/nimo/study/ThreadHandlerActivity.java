package com.nimo.study;

import java.lang.ref.WeakReference;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;




/**
 * @author ss
 *
 */
public class ThreadHandlerActivity extends Activity {
	private static final int MSG_SUCCESS = 0;// 获取图片成功的标识
	private static final int MSG_FAILURE = 1;// 获取图片失败的标识

	private ImageView mImageView;
	private Button mButton;

	private Thread mThread;

	private Handler mHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.thread_layout);

		mHandler = new MyHandler(this);

		mImageView = (ImageView) findViewById(R.id.imageView);// 显示图片的ImageView
		mButton = (Button) findViewById(R.id.button);
		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mThread == null) {
					mThread = new Thread(runnable);
					mThread.start();// 线程启动
				} else {
					Toast.makeText(
							getApplication(),
							getApplication().getString(R.string.thread_started),
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	Runnable runnable = new Runnable() {

		@Override
		public void run() {// run()在新的线程中运行
			HttpClient hc = new DefaultHttpClient();
			HttpGet hg = new HttpGet(
					"http://csdnimg.cn/www/images/csdnindex_logo.gif");// 获取csdn的logo
			final Bitmap bm;
			try {
				HttpResponse hr = hc.execute(hg);
				bm = BitmapFactory.decodeStream(hr.getEntity().getContent());
			} catch (Exception e) {
				mHandler.obtainMessage(MSG_FAILURE).sendToTarget();// 获取图片失败
				return;
			}
			mHandler.obtainMessage(MSG_SUCCESS, bm).sendToTarget();// 获取图片成功，向ui线程发送MSG_SUCCESS标识和bitmap对象

			// mImageView.setImageBitmap(bm); //出错！不能在非ui线程操作ui元素
            
			/*
			 * 
			 * 另外一种更简洁的发送消息给ui线程的方法。
			mHandler.post(new Runnable() {
			//其实这个Runnable并没有创建什么线程，而是发送了一条消息
			 * 通过源码可以看出其实post出的runnable对象最终也是转化成message加入到队列
				@Override
				public void run() {
				    // run()方法会在ui线程执行
					mImageView.setImageBitmap(bm);
				}
			});
			*
			*/
			
		}
	};
    
	/**
	 * @author ss
	 * 问题：如何避免Handler引起的内存泄露
	 * 1、不使用非静态内部类，继承Handler的时候，要么放在单独的类文件中，要么就使用静态内部类
	 * why----->在java中，非静态的内部类和匿名内部类都会隐式地持有其外部类的引用，而静态的内部类
	 * 不会持有外部类的引用。
	 * 假如在线程中执行如下方法：
	           mHandler.postDelayed(new Runnable() {
	           这里的runnable如果不采用匿名内部类，而是在外面声明，则也应该设置成静态
                 @Override
                 public void run() { 
                	 ...
                 }
			 }, 1000 * 60 * 10);
			 外部调用finish()销毁Activity
	 * 如果我们使用非静态的MyHandler, 当我们的代码执行finish方法时，被延迟的消息会在被处理之前存在于
	 * 主线程消息队列中10分钟，我们发送一个target为这个Handler的消息到Looper处理的消息队列时，实际上
	 * 已经发送的消息已经包含了一个Handler实例的引用，只有这样Looper在处理到这条消息时才可以
	 * 调用Handler#handleMessage(Message)完成消息的正确处理。
	 * 但是非静态的MyHandler持有外部ThreadHandlerActivity的引用
	 * 所以这导致了ThreadHandlerActivity无法回收，进行导致ThreadHandlerActivity持有的很多资源都
	 * 无法回收，这就是我们常说的内存泄露。
	 * 
	 */
	private static class MyHandler extends Handler {
		//2、当你需要在静态内部类中调用外部的Activity时，我们可以使用弱引用来处理。
		WeakReference<ThreadHandlerActivity> thisLayout;

		MyHandler(ThreadHandlerActivity layout) {
			thisLayout = new WeakReference<ThreadHandlerActivity>(layout);
		}

		public void handleMessage(Message msg) {// 此方法在ui线程运行
			final ThreadHandlerActivity theLayout = thisLayout.get();
			if (theLayout == null) {
				return;
			}

			switch (msg.what) {
			case MSG_SUCCESS:
				theLayout.mImageView.setImageBitmap((Bitmap) msg.obj);// imageview显示从网络获取到的logo
				Toast.makeText(theLayout,
						theLayout.getString(R.string.get_pic_success),
						Toast.LENGTH_LONG).show();
				break;

			case MSG_FAILURE:
				Toast.makeText(theLayout,
						theLayout.getString(R.string.get_pic_failure),
						Toast.LENGTH_LONG).show();
				break;
			}
		}
	}
}
