package com.wedo.bandwriter.view;

import java.util.ArrayList;
import java.util.List;

import tools.StringUtils;
import cn.pda.serialport.Tools;

import com.wedo.bandwriter.R;
import com.wedo.bandwriter.config.AppActivity;
import com.wedo.bandwriter.config.AppFragment;
import com.wedo.bandwriter.config.Band;
import com.wedo.bandwriter.uitl.BandScannerManager;
import com.wedo.bandwriter.uitl.BandScannerUtil;
import com.wedo.bandwriter.uitl.CommonValues;
import com.wedo.bandwriter.uitl.BandScannerManager.ScannerCallback;

import android.R.integer;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class WriteFragment extends AppFragment {

	
	public static final int STOPPED=-1;
	public static final int RUNNING=0;
	private Context context;
	private AppActivity activity;
	private View view;
	private TextView currentNumberText;
	private EditText startEditText,endEditText,infoEditText;
	private Button startButton,stopButton;
	private int startNumber;
	private int stopNumber;
	private int currentNumber;
	private int currentIndex;
	private List<Integer> numberList;
	private int status;
	private boolean stop;
	private BandScannerManager scannerManager;
	private Handler scanHandler;
	List<String> bandList;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.write, container,false);
		status=STOPPED;
		activity=(AppActivity) getActivity();
		context=activity.getWriterApplication();
		scannerManager=BandScannerManager.getInstance(context);
		if (!scannerManager.initBandScanner()) {
			showToast("该设备不具有扫描功能");
			activity.finish();
		}
		stop=false;
		initView();
		return view;
	}
	
	private void initView(){
		currentNumberText=(TextView) view.findViewById(R.id.currentID);
		startEditText=(EditText) view.findViewById(R.id.startNumber);
		endEditText=(EditText) view.findViewById(R.id.endNumber);
		infoEditText=(EditText) view.findViewById(R.id.info);
		infoEditText.setMovementMethod(ScrollingMovementMethod.getInstance());
		infoEditText.setSelection(infoEditText.getText().length(), infoEditText.getText().length());
		startButton=(Button) view.findViewById(R.id.startButton);
		stopButton=(Button) view.findViewById(R.id.stopButton);
		scanHandler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				Bundle data = msg.getData();
				if (!StringUtils.empty(data.getString("i"))) {
					logging(data.getString("i"));
				}
				if (data.containsKey("w")) {
					currentNumberText.setText(data.getInt("w")+"");
				}
				if (data.containsKey("c")) {
					if (data.getInt("c")==1) {
						startEditText.setEnabled(true);
						endEditText.setEnabled(true);
						startButton.setEnabled(true);
					}
				}
			}
		};
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (status==RUNNING) {
					logging("别点了，写着呢");
					return;
				}
				if (StringUtils.empty(startEditText.getText().toString())||(StringUtils.empty(endEditText.getText().toString()))) {
					showToast("请输入完整手环编号");
					return;
				}
				startNumber=Integer.valueOf(startEditText.getText().toString());
				stopNumber=Integer.valueOf(endEditText.getText().toString());
				if (startNumber>stopNumber) {
					showToast("开始编号必须<=结束编号");
					return;
				}
				numberList=new ArrayList<Integer>();
				for (int i = startNumber; i <= stopNumber; i++) {
					numberList.add(i);
				}
				currentIndex=0;
				currentNumberText.setText(currentNumber+"");
				bandList=new ArrayList<String>();
				BandScannerUtil.play(1, 0);
				status=RUNNING;
				stop=false;
				startEditText.setEnabled(false);
				endEditText.setEnabled(false);
				startButton.setEnabled(false);
				logging("开始写入");
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						scannerManager.start();
						while (!stop) {
							Bundle bundle=new Bundle();
							byte[] uid = scannerManager.find();
							if (uid!=null) {
								if (bandList.contains(Tools.Bytes2HexString(uid, uid.length))) {
									Message cMessage=new Message();
									bundle.clear();
									bundle.putString("i", "已有: "+Tools.Bytes2HexString(uid, uid.length));
									cMessage.setData(bundle);
									scanHandler.sendMessage(cMessage);
									continue;
								}
								Message fMessage=new Message();
								bundle.clear();
								bundle.putString("i", "找到: "+Tools.Bytes2HexString(uid, uid.length));
								fMessage.setData(bundle);
								scanHandler.sendMessage(fMessage);
								boolean passed=true;
								int passwordType=-1;
								try {
									Thread.currentThread().sleep(500);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								if (scannerManager.auth(Tools.HexString2Bytes(CommonValues.ORI_BAND_ACCESS1))==0) {
									passwordType=1;
								}else {
									uid=scannerManager.find();
									if (uid==null) {
										continue;
									}
									if (scannerManager.auth(Tools.HexString2Bytes(CommonValues.ORI_BAND_ACCESS2))==0) {
										passwordType=0;
									}else {
										uid=scannerManager.find();
										if (uid==null) {
											continue;
										}
										if (scannerManager.auth(Tools.HexString2Bytes(CommonValues.NEW_BAND_ACCESS))==0) {
											passwordType=2;
										}else {
											passed=false;
										}
									}
								}
								if (passed==false) {
									Message aMessage=new Message();
									bundle.clear();
									bundle.putString("i", "认证: "+Tools.Bytes2HexString(uid, uid.length)+"失败");
									aMessage.setData(bundle);
									scanHandler.sendMessage(aMessage);
									continue;
								}else {
									Message aMessage=new Message();
									bundle.clear();
									byte[] realP=scannerManager.readPassword();
									String rP="";
									if (realP!=null) {
										rP=Tools.Bytes2HexString(realP, realP.length);
									}
									bundle.putString("i", "认证: "+Tools.Bytes2HexString(uid, uid.length)+"成功,密码:\n"+rP);
									aMessage.setData(bundle);
									scanHandler.sendMessage(aMessage);
								}
								if (passwordType==0||passwordType==1) {
									byte[] oldPassword = scannerManager.readPassword();
									if (oldPassword==null) {
										continue;
									}
									Message pMessage=new Message();
									bundle.clear();
									bundle.putString("i", Tools.Bytes2HexString(uid, uid.length)+" 原密码:\n"+Tools.Bytes2HexString(oldPassword, oldPassword.length));
									pMessage.setData(bundle);
									scanHandler.sendMessage(pMessage);
									byte[] newPassword=scannerManager.changePassword(oldPassword, Tools.HexString2Bytes(CommonValues.NEW_BAND_ACCESS));
									int writePassword = scannerManager.writePassword(newPassword);
									Message rpMessage=new Message();
									if (writePassword!=0) {
										bundle.clear();
										bundle.putString("i", Tools.Bytes2HexString(uid, uid.length)+"写入新密码:\n"+Tools.Bytes2HexString(newPassword, newPassword.length)+"失败");
										rpMessage.setData(bundle);
										scanHandler.sendMessage(rpMessage);
										continue;
									}
									bundle.clear();
									bundle.putString("i", Tools.Bytes2HexString(uid, uid.length)+"写入新密码:\n"+Tools.Bytes2HexString(newPassword, newPassword.length)+"成功");
									rpMessage.setData(bundle);
									scanHandler.sendMessage(rpMessage);
									continue;
								}
								currentNumber=numberList.get(currentIndex);
								int writeData = scannerManager.writeData(Tools.bandNumberToByte(currentNumber));
								if (writeData!=0) {
									Message wMessage=new Message();
									bundle.clear();
									bundle.putString("i", Tools.Bytes2HexString(uid, uid.length)+"写入数据:"+currentNumber+" 失败");
									wMessage.setData(bundle);
									scanHandler.sendMessage(wMessage);
									continue;
								}
								uid=scannerManager.find();
								if (uid==null) {
									continue;
								}
								if (scannerManager.auth(Tools.HexString2Bytes(CommonValues.NEW_BAND_ACCESS))!=0) {
									continue;
								}
								bundle.clear();
								byte[] readData = scannerManager.readData();
								Message ckMessage=new Message();
								if (readData==null) {
									bundle.clear();
									bundle.putString("i", "测试读取结果失败");
									ckMessage.setData(bundle);
									scanHandler.sendMessage(ckMessage);
									continue;
								}else {
									bundle.clear();
									bundle.putString("i", "测试读取结果成功:"+Tools.byteTohexStringExceptZero(readData));
									ckMessage.setData(bundle);
									scanHandler.sendMessage(ckMessage);
								}
								try {
									Thread.currentThread().sleep(100);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Message yMessage=new Message();
								bundle.clear();
								bundle.putString("i", Tools.Bytes2HexString(uid, uid.length)+"写入数据:"+currentNumber+" 成功");
								bundle.putInt("w", currentNumber);
								yMessage.setData(bundle);
								scanHandler.sendMessage(yMessage);
								bandList.add(Tools.Bytes2HexString(uid, uid.length));
								if (currentIndex==numberList.size()-1) {
									stop=true;
								}
								currentIndex++;
								BandScannerUtil.play(1, 0);
							}
						}
						scannerManager.stop();
						status=STOPPED;
						stop=false;
						Message sMessage=new Message();
						Bundle bundle=new Bundle();
						bundle.clear();
						bundle.putString("i", "已停止写入线程");
						bundle.putInt("c", 1);
						sMessage.setData(bundle);
						scanHandler.sendMessage(sMessage);
					}
				}).start();
			}
		});
		
		stopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				if (status!=RUNNING) {
					logging("别点了，没写");
					return;
				}
				BandScannerUtil.play(1, 0);
				logging("停止写入");
				stop=true;
			}
		});
		
	}
	
	private void logging(String string){
		infoEditText.append(string+"\n");
	}
}	
