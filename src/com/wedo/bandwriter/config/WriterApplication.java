package com.wedo.bandwriter.config;

import java.util.HashMap;
import java.util.Map;

import tools.AppManager;
import tools.ImageCacheUtil;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;

public class WriterApplication extends Application {
	private static WriterApplication makeApplication;
	private NotificationManager notificationManager;
	private Map<String, Object> appMap;
	public synchronized static WriterApplication getInstance(){
		return makeApplication;
	}
	
	public NotificationManager getNotificationManager(){
		if (notificationManager==null) {
			notificationManager=(NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		}
		return notificationManager;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		makeApplication=this;
		notificationManager=(NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		ImageCacheUtil.init(this);
		ImageLoaderConfiguration configuration=ImageLoaderConfiguration.createDefault(this);
		ImageLoader.getInstance().init(configuration);
		Intent intent=new Intent();
		intent.setAction("tools.NetworkState.Service");
		startService(intent);
		setAppMap(new HashMap<String, Object>());
	}
	
	public void exit() {
		AppManager.getAppManager().finishAllActivity();
	}

	public Map<String, Object> getAppMap() {
		return appMap;
	}

	public void setAppMap(Map<String, Object> appMap) {
		this.appMap = appMap;
	}
	
}
