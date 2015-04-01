package com.dispenser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class DiagnosticMode extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.activity_diagnostic_mode, container, false);
		EditText mPS1=(EditText) rootView.findViewById(R.id.ps1);
		EditText mPS2=(EditText) rootView.findViewById(R.id.ps2);
		EditText mPS3=(EditText) rootView.findViewById(R.id.ps3);
		EditText mFW=(EditText) rootView.findViewById(R.id.fw);
		EditText mFT=(EditText) rootView.findViewById(R.id.ft);
		EditText mFT1=(EditText) rootView.findViewById(R.id.ft1);
		EditText valve1=(EditText) rootView.findViewById(R.id.valve1);
		EditText valve2=(EditText) rootView.findViewById(R.id.valve2);
		
		
		return rootView;
	}
}

