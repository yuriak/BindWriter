package cn.pda.serialport;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

public class SerialPort {

	private static final String TAG = "SerialPort";
	
	
	public static int TNCOM_EVENPARITY = 0;//偶校验
	public static int TNCOM_ODDPARITY = 1 ;//奇校验

	/*
	 * Do not remove or rename the field mFd: it is used by native method close();
	 */
	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;
	private boolean trig_on=false;
	byte[] test;
	public SerialPort(int port, int baudrate, int flags) throws SecurityException, IOException {
		mFd = open(port, baudrate);
		
		if (mFd == null) {
			Log.e(TAG, "native open returns null");
			throw new IOException();
		}
		
		
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
		


	}

	// Getters and setters
	public InputStream getInputStream() {
		return mFileInputStream;
	}

	public OutputStream getOutputStream() {
		return mFileOutputStream;
	}
	public void power_5Von() {
		zigbeepoweron();
	}
	public void power_5Voff(){
		zigbeepoweroff();
	}
	public void power_3v3on(){
		power3v3on();
	}
	public void power_3v3off(){
		power3v3off();
	}
	public void rfid_poweron(){
		rfidPoweron();
	}
	public void rfid_poweroff(){
		rfidPoweroff();
	}
	public void psam_poweron() {
		psampoweron();
	}
	public void psam_poweroff() {
		psampoweroff();
		//scaner_trigoff();
	}
	public void scaner_poweron() {
		scanerpoweron();
		scaner_trigoff();
	}
	public void scaner_poweroff() {
		scanerpoweroff();
		//scaner_trigoff();
	}
	public void scaner_trigon() {
		scanertrigeron();
		trig_on=true;
	}
	public void scaner_trigoff() {
		scanertrigeroff();
		trig_on=false;
	}
	public boolean scaner_trig_stat(){
		return trig_on;
	}
	
	
	
//	/**
//	 * 设置奇偶校验
//	 * @param mode  
//	 */
//	public void setPortparity(int mode){
//		setPortParity(mode);
//	}
	
	// JNI
	
	private native static FileDescriptor open(int port, int baudrate);
	private native static FileDescriptor open(int port, int baudrate, int portparity);
	public native void close(int port);
	public native void zigbeepoweron();
	public native void zigbeepoweroff();
	
	public native void scanerpoweron();
	public native void scanerpoweroff();
	public native void psampoweron();
	public native void psampoweroff();
	public native void scanertrigeron();
	public native void scanertrigeroff();
	public native void power3v3on();
	public native void power3v3off();
	
	public native void rfidPoweron();
	public native void rfidPoweroff();
	
	public native void usbOTGpowerOn();
	public native void usbOTGpowerOff();
	
	public native void irdapoweron();
	public native void irdapoweroff();
	
//	public native void setPortParity(int mode); //设置校验位
	
	public native void test(byte[] bytes);
	
	static {
		System.loadLibrary("devapi");
		System.loadLibrary("irdaSerialPort");
	}
	
}
