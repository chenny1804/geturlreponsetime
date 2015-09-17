package com.example.geturlresponestime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Intent;

public class MainActivity extends Activity {
	Button visitWebBtn = null;
	Button downImgBtn = null;
	TextView showTextView = null;
	ImageView showImageView = null;
	String resultStr = "";
	ProgressBar progressBar = null;
	ViewGroup viewGroup = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initUI();
		Intent intent = getIntent();
		ComponentName comp = intent.getComponent();
		String url=intent.getDataString();
		System.out.println(url);
		String result=getURLResponse(url);
		System.out.println(result);
		showImageView.setVisibility(View.GONE);
		showTextView.setVisibility(View.VISIBLE);
		showTextView.setText(result);
//		int i=0;
//		while(i < 10){
//			Thread visitBaiduThread = new Thread(new VisitWebRunnable());
//			visitBaiduThread.start();
//			try {
//				visitBaiduThread.join();
//				if(!resultStr.equals("")){
//					//showTextView.setText(i);
//					showTextView.setText(resultStr);
//					visitBaiduThread.sleep(5000);
//				}
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			i++;
//		}
		visitWebBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showImageView.setVisibility(View.GONE);
				showTextView.setVisibility(View.VISIBLE);
				int i=1;
				while(i < 10){
					Thread visitBaiduThread = new Thread(new VisitWebRunnable());
					visitBaiduThread.start();
					try {
						visitBaiduThread.join();
						if(!resultStr.equals("")){
							showTextView.setText(resultStr);
							visitBaiduThread.sleep(5000);
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					i++;
				}
			}
		});
		downImgBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showImageView.setVisibility(View.VISIBLE);
				showTextView.setVisibility(View.GONE);
				String imgUrl = "http://www.shixiu.net/d/file/p/2bc22002a6a61a7c5694e7e641bf1e6e.jpg";
				new DownImgAsyncTask().execute(imgUrl);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	public void initUI(){
		showTextView = (TextView)findViewById(R.id.textview_show);
		showImageView = (ImageView)findViewById(R.id.imagview_show);
		downImgBtn = (Button)findViewById(R.id.btn_download_img);
		visitWebBtn = (Button)findViewById(R.id.btn_visit_web);
	}
	/**
	 * 获取指定URL的响应字符串
	 * @param urlString
	 * @return
	 */
	private String getURLResponse(String urlString){
		HttpURLConnection conn = null; //连接对象
		InputStream is = null;
		String resultData = "";
		try {
			URL url = new URL(urlString); //URL对象
			long starttime=System.currentTimeMillis();
			System.out.println(starttime);
			conn = (HttpURLConnection)url.openConnection(); //使用URL打开一个链接
			conn.setDoInput(true); //允许输入流，即允许下载
			conn.setDoOutput(true); //允许输出流，即允许上传
			conn.setUseCaches(false); //不使用缓冲
			conn.setRequestMethod("GET"); //使用get请求
			is = conn.getInputStream();   //获取输入流，此时才真正建立链接
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader bufferReader = new BufferedReader(isr);
			String inputLine  = "";
			while((inputLine = bufferReader.readLine()) != null){
				resultData += inputLine + "\n";
			}
			long endtime=System.currentTimeMillis();
			System.out.println(endtime-starttime);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(conn != null){
				conn.disconnect();
			}
		}
		return resultData;
	}

	/**
	 * 从指定URL获取图片
	 * @param url
	 * @return
	 */
	private Bitmap getImageBitmap(String url){
		URL imgUrl = null;
		Bitmap bitmap = null;
		try {
			imgUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection)imgUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		return bitmap;
	}
	class VisitWebRunnable implements Runnable{
		public void run() {
			// TODO Auto-generated method stub
			String data = getURLResponse("http://www.baidu.com");
			resultStr = data;
		}

	}
	class DownImgAsyncTask extends AsyncTask<String, Void, Bitmap>{


		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showImageView.setImageBitmap(null);
			showProgressBar();//显示进度条提示框

		}

		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			Bitmap b = getImageBitmap(params[0]);
			return b;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result!=null){
				dismissProgressBar();
				showImageView.setImageBitmap(result);
			}
		}



	}
	/**
	 * 在母布局中间显示进度条
	 */
	private void showProgressBar(){
		progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT,  RelativeLayout.TRUE);
		progressBar.setVisibility(View.VISIBLE);
		Context context = getApplicationContext();
		viewGroup = (ViewGroup)findViewById(R.id.parent_view);
		//		MainActivity.this.addContentView(progressBar, params);
		viewGroup.addView(progressBar, params);		
	}
	/**
	 * 隐藏进度条
	 */
	private void dismissProgressBar(){
		if(progressBar != null){
			progressBar.setVisibility(View.GONE);
			viewGroup.removeView(progressBar);
			progressBar = null;
		}
	}

}
