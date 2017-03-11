package com.wedo.bandwriter.view;

import tools.StringUtils;

import com.wedo.bandwriter.R;
import com.wedo.bandwriter.config.AppActivity;
import com.wedo.bandwriter.uitl.BandScannerUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppActivity {
	
	
	private FragmentManager fragmentManager ;
	private FragmentTransaction fragmentTransaction;
	private TextView writeTextView;
	private TextView readTextView;
	private LinearLayout writeLayout;
	private LinearLayout readLayout;
	private View writeBar;
	private View readBar;
//	private Fragment settingFragment;
	private Fragment writeFragment;
	private Fragment readFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		fragmentManager=getFragmentManager();
		writeFragment=new WriteFragment();
		readFragment=new ReadFragment();
		replaceFragment(writeFragment);
		final EditText editText=new EditText(this);
		new AlertDialog.Builder(this).setView(editText).setTitle("请输入密码").setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String password = editText.getText().toString();
				if (StringUtils.empty(password)) {
					showToast("好好输入，别空着");
					return;
				}
				if (password.equals("fucker")) {
					showToast("恭喜你，可以用了");
					BandScannerUtil.play(1, 0);
					return;
				}else {
					showToast("这个不是给你用的!");
					BandScannerUtil.play(2, 0);
					finish();
				}
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				showToast("不知道密码吧，拜拜");
				finish();
			}
		}).setCancelable(false).show();
		initView();
		
	}
	
	private void replaceFragment(Fragment fragment){
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.layoutFragment, fragment);
		fragmentTransaction.commit();
	}
	private void initView() {
		writeLayout = (LinearLayout) findViewById(R.id.writeLayout);
		readLayout = (LinearLayout) findViewById(R.id.readLayout);
		writeTextView = (TextView) findViewById(R.id.writeTextView);
		readTextView = (TextView) findViewById(R.id.readTextView);
		writeBar = (View) findViewById(R.id.writeView);
		readBar = (View) findViewById(R.id.readView);
		setListener();
	}
	
	private void setListener() {
		writeLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				writeTextView.setTextColor(getResources().getColor(R.color.tabSelect));
				writeBar.setBackgroundColor(getResources().getColor(R.color.tabSelect));
				readTextView.setTextColor(getResources().getColor(R.color.black));
				readBar.setBackgroundColor(getResources().getColor(R.color.white));
				replaceFragment(writeFragment);
				return false;
			}
		});
		readLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				writeTextView.setTextColor(getResources().getColor(R.color.black));
				writeBar.setBackgroundColor(getResources().getColor(R.color.white));
				readTextView.setTextColor(getResources().getColor(R.color.tabSelect));
				readBar.setBackgroundColor(getResources().getColor(R.color.tabSelect));
				replaceFragment(readFragment);
				return false;
			}
		});
	}

	
}
