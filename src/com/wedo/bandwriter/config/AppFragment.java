package com.wedo.bandwriter.config;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

public class AppFragment extends Fragment implements AppFragmentSupport {

	Context context;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context=(AppActivity)this.getActivity();
	}
	
	@Override
	public void showToast(String text) {
		// TODO Auto-generated method stub
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

}
