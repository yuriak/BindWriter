package com.wedo.bandwriter.view;

import com.wedo.bandwriter.R;
import com.wedo.bandwriter.config.AppActivity;
import com.wedo.bandwriter.config.AppFragment;
import com.wedo.bandwriter.uitl.BandScannerManager;
import com.wedo.bandwriter.uitl.BandScannerManager.ScannerCallback;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ReadFragment extends AppFragment {
	private View view;
	private AppActivity activity;
	private Context context;
	private TextView numberView;
	private Button scanButton;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view=inflater.inflate(R.layout.read, container,false);
		activity=(AppActivity) getActivity();
		context=activity.getContext();
		BandScannerManager.getInstance(context);
		initView();
		return view;
	}
	
	private void initView(){
		numberView=(TextView) view.findViewById(R.id.bandNumber);
		scanButton=(Button) view.findViewById(R.id.scanButton);
		scanButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				BandScannerManager.getInstance(context).scan(activity, new ScannerCallback() {
					
					@Override
					public void onSuccess(Object data) {
						// TODO Auto-generated method stub
						numberView.setText(data.toString());
					}
					
					@Override
					public void onFailure(String message) {
						// TODO Auto-generated method stub
						showToast(message);
					}
					
					@Override
					public void onError(Exception e) {
						// TODO Auto-generated method stub
						
					}
				});
			}
		});
	}
}
