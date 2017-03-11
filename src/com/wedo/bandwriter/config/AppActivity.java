package com.wedo.bandwriter.config;

import java.util.HashMap;
import java.util.Map;

import tools.AppManager;
import tools.ImageUtils;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class AppActivity extends Activity implements AppActivitySupport {

	protected WriterApplication appContext;
	protected Context context;
	protected ProgressDialog progressDialog;
	protected NotificationManager notificationManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appContext=(WriterApplication) getApplication();
		context=this;
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	@Override
	public WriterApplication getWriterApplication() {
		// TODO Auto-generated method stub
		return appContext;
	}

	@Override
	public void stopService() {
		//停止手环扫描服务
	}

	@Override
	public void startService() {
		//开始手环扫描服务
	}

	
	

	@Override
	public void isExit() {
		new AlertDialog.Builder(context).setTitle("确定退出吗?")
		.setNeutralButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				appContext.exit();
			}
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		}).show();
		
	}

	@Override
	public void showToast(String text, int longint) {
		Toast.makeText(context, text, longint).show();
	}

	@Override
	public void showToast(String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public ProgressDialog getProgressDialog() {
		return progressDialog;
	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return context;
	}


	@Override
	public void setNotiType(int iconId, String contentTitle,
			String contentText, Class activity, String from) {
		// TODO Auto-generated method stub
		
	}
	
	public void closeInput() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null && this.getCurrentFocus() != null) {
			inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

}
