package com.wedo.bandwriter.uitl;

import java.io.InputStream;
import java.io.OutputStream;








import com.wedo.bandwriter.config.AppActivity;

import tools.StringUtils;
import tools.UIHelper;
import cn.pda.rfid.hf.Error;
import cn.pda.rfid.hf.HfConmmand;
import cn.pda.rfid.hf.HfError;
import cn.pda.rfid.hf.HfReader;
import cn.pda.serialport.SerialPort;
import cn.pda.serialport.Tools;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class BandScannerManager {
	
	public interface ScannerCallback{
		abstract void onSuccess(Object data);
		
		abstract void onFailure(String message);
		
		abstract void onError(Exception e);
	}
	/*
	 * 扇区：1
	 * 数据块号：0
	 * 密码块号：3
	 * 密码A、B:112358132134/66306A8DB29E
	 */
	
	private boolean running = true ;
	private boolean startFlag = false ;
	private SerialPort serialport ;
	private HfConmmand hf ;
	private int port;
	private int buad;
	private String powerStr;
	private static InputStream is ;
	private static OutputStream os ;
	private static BandScannerManager manager;
	
	private byte[] uid ;
	private int dataSector ;
	private int dataBlock ;
	private int passwordBlock;
	private int passwordSector;
	private byte[] access ;
	private byte[] oriAccess;
	private byte[] readData ;
	private byte[] writeData ;
	private int accessType ;
	private HfError hfError;
	private boolean inited=false;
	
	
	public void testScan(AppActivity activity,final ScannerCallback callback){
		Handler handler=new Handler(activity.getMainLooper()){
			@Override
			public void handleMessage(Message msg) {
				String 	string = msg.getData().getString("data");
				callback.onSuccess(string);
			}
		};
		Message message=new Message();
		Bundle bundle=new Bundle();
		bundle.putCharSequence("data", "data");
		message.setData(bundle);
		handler.sendMessage(message);
	}
	
	public boolean start(){
		if (!inited) {
			return false;
		}
		serialport.rfid_poweron();
		hfError=new HfError();
		return true;
	}
	
	public byte[] find(){
		uid=hf.findCard14443A(hfError);
		return uid;
	}
	
	public int auth(byte[] a){
		return hf.auth14443A(accessType, a, uid, dataSector*4, hfError);
	}
	
	public int authNew(){
		return hf.auth14443A(accessType, access, uid, dataSector*4, hfError);
	}
	
	public int authOri(byte[] access){
		return hf.auth14443A(accessType, access, uid, dataSector*4, hfError);
	}
	public int getError(){
		return hfError.getErrorCode();
	}
	
	public byte[] readData(){
		return hf.read14443A(dataSector*4+dataBlock, hfError);
	}
	
	public byte[] readPassword(){
		return hf.read14443A(passwordSector*4+passwordBlock, hfError);
	}
	
	public int writePassword(byte[] data){
		return hf.write14443A(data, passwordSector*4+passwordBlock, hfError);
	}
	
	public int writeData(byte[] data){
		return hf.write14443A(data, dataSector*4 + dataBlock, hfError);
	}
	
	public boolean stop(){
		serialport.rfid_poweroff();
		return true;
	}
	public void scan(Activity context,final ScannerCallback callback){
		if (!inited) {
			callback.onFailure("没有扫描设备，无法使用");
			return;
		}
		final Handler scanHandler=new Handler(context.getMainLooper()){
			public void handleMessage(Message msg) {
				Bundle data = msg.getData();
				if (!StringUtils.empty(data.getString("e"))) {
					callback.onFailure(data.getString("e"));
				}
				if (!StringUtils.empty(data.getString("s"))) {
					callback.onSuccess(data.getString("s"));
				}
			};
		};
		serialport.rfid_poweron();
		hfError=new HfError();
		final long start=System.currentTimeMillis();
		final ProgressDialog progressDialog = UIHelper.showProgress(context, null, "扫描手环中");
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message=new Message();
				Bundle bundle=new Bundle();
				while (true) {
					uid=hf.findCard14443A(hfError);
					if (uid!=null) {
						UIHelper.dismissProgress(progressDialog);
						break;
					}
					if (System.currentTimeMillis()-start>2000) {
						UIHelper.dismissProgress(progressDialog);
						break;
					}
				}
				if (uid==null) {
					serialport.rfid_poweroff();
					bundle.putString("e", "寻找手环失败: "+hfError.getErrorCode()+"");
					message.setData(bundle);
					scanHandler.sendMessage(message);
					return;
				}
				int auth = hf.auth14443A(accessType, access, uid, dataSector*4, hfError);
				if (auth != 0) {
					serialport.rfid_poweroff();
					bundle.putString("e", "认证手环失败: "+hfError.getErrorCode()+"");
					message.setData(bundle);
					scanHandler.sendMessage(message);
					return;
				}
				readData=hf.read14443A(dataSector*4+dataBlock, hfError);
				if (readData==null) {
					serialport.rfid_poweroff();
					bundle.putString("e", "读取手环失败: "+hfError.getErrorCode()+"");
					message.setData(bundle);
					scanHandler.sendMessage(message);
					return;
				}
				BandScannerUtil.play(1, 0);
				serialport.rfid_poweroff();
				bundle.putString("s", Tools.byteTohexStringExceptZero(readData));
				message.setData(bundle);
				scanHandler.sendMessage(message);
			}
		}).start();
	}
	
	public void write(Activity activity,final ScannerCallback callback){
		if (!inited) {
			callback.onFailure("没有扫描设备，无法使用");
			return;
		}
		serialport.rfid_poweron();
		hfError=new HfError();
		final long start=System.currentTimeMillis();
		final ProgressDialog progressDialog = UIHelper.showProgress(activity, null, "扫描手环中");
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					uid=hf.findCard14443A(hfError);
					if (uid!=null) {
						UIHelper.dismissProgress(progressDialog);
						break;
					}
					if (System.currentTimeMillis()-start>2000) {
						UIHelper.dismissProgress(progressDialog);
						break;
					}
				}
				if (uid==null) {
					serialport.rfid_poweroff();
					callback.onFailure("寻找手环失败: "+hfError.getErrorCode()+"");
					return;
				}
				int auth = hf.auth14443A(accessType, access, uid, dataSector*4, hfError);
				if (auth != 0) {
					serialport.rfid_poweroff();
					callback.onFailure("认证手环失败: "+hfError.getErrorCode()+"");
					return;
				}
				if (hf.write14443A(writeData, dataSector*4 + dataBlock, hfError) == 0) {
					BandScannerUtil.play(1, 0);
					serialport.rfid_poweroff();
					callback.onSuccess("写入手环成功");
				}else {
					callback.onFailure("写入手环失败: "+hfError.getErrorCode());
				}
			}
		}).start();
		
	}
	
	public static BandScannerManager getInstance(Context context){
		if (manager==null) {
			synchronized (BandScannerManager.class) {
				if (manager==null) {
					manager=new BandScannerManager(context);
				}
			}
		}
		return manager;
	}
	
	private BandScannerManager(Context context){
		try {
			port=CommonValues.PHONE_BAND_PORT;
			buad=CommonValues.BAND_BUAD;
			powerStr=CommonValues.BAND_POWER_STR;
			accessType=HfReader.AUTH_B;
			dataSector=CommonValues.BAND_DATA_SECTOR;
			dataBlock=CommonValues.BAND_DATA_BLOCK;
			passwordBlock=CommonValues.BAND_PASSWORD_BLOCK;
			passwordSector=CommonValues.BAND_PASSWORD_SELCOR;
			access=Tools.HexString2Bytes(CommonValues.NEW_BAND_ACCESS);
			oriAccess=Tools.HexString2Bytes(CommonValues.ORI_BAND_ACCESS1);
			BandScannerUtil.initSoundPool(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean initBandScanner(){
		try {
			serialport=new SerialPort(port, buad, 0);
			is=serialport.getInputStream();
			os=serialport.getOutputStream();
			serialport.rfid_poweron();
			hf=new HfReader(is, os);
			hfError=new HfError();
			uid=hf.findCard14443A(hfError);
			if (hfError.getErrorCode()==-1) {
				serialport.rfid_poweroff();
				port=CommonValues.TABLET_BAND_PORT;
				serialport=new SerialPort(port, buad, 0);
				is=serialport.getInputStream();
				os=serialport.getOutputStream();
				serialport.rfid_poweron();
				hf=new HfReader(is, os);
				hfError=new HfError();
				uid=hf.findCard14443A(hfError);
				serialport.rfid_poweroff();
			}else {
				serialport.rfid_poweroff();
			}
			inited=true;
		} catch (Exception e) {
			e.printStackTrace();
			serialport.rfid_poweroff();
			inited=false;
		}
		return inited;
	}
	
	public boolean checkEquipment(){
		
		return false;
	}
	
	public static byte[] changePassword(byte[] access,byte[] password){
		byte[] result=new byte[access.length];
		for (int i = 0; i < password.length; i++) {
			result[i]=password[i];
		}
		for (int i = 6; i < 10; i++) {
			result[i]=access[i];
		}
		for (int i = 0; i < password.length; i++) {
			result[10+i]=password[i];
		}
		return result;
	}
}
