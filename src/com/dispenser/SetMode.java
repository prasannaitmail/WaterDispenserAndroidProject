package com.dispenser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SetMode extends Fragment {
	EditText sPS1,sPS2,sPS3,sFw;
	ChatService mChatService;
	RadioGroup rg;
	RadioButton radioLitre;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View rootView = inflater.inflate(R.layout.activity_set_mode, container, false);
		//Set
				sPS1=(EditText) rootView.findViewById(R.id.pressure1);
				sPS2=(EditText) rootView.findViewById(R.id.pressure2);
				sPS3=(EditText) rootView.findViewById(R.id.pressure3);
				sFw=(EditText) rootView.findViewById(R.id.flow);
				rg=(RadioGroup) rootView.findViewById(R.id.radiolitre);
				Button mSet= (Button) rootView.findViewById(R.id.setBtn);
				
			//	mChatService = new ChatService(this, MainActivity.mHandler);
				mSet.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(sPS1.getText().length()!=0 && sPS2.getText().length()!=0 && sPS3.getText().length()!=0 && sFw.getText().length()!=0  ){
			
				//	float pressure1=Float.parseFloat(sPS1.getText().toString());
				//	float pressure2=Float.parseFloat(sPS2.getText().toString());
					
					 int selectedId = rg.getCheckedRadioButtonId();
					 radioLitre=(RadioButton) rootView.findViewById(selectedId);
						if (MainActivity.mChatService.getState() != ChatService.STATE_CONNECTED) {
							Toast.makeText(getActivity().getApplicationContext(), R.string.not_connected, Toast.LENGTH_SHORT)
							.show();
							return;
						}
					
						
						// XXX !!!
						String message = sPS1.getText().toString()+","+sPS2.getText().toString()+","+sPS3.getText().toString()+","+sFw.getText().toString() +","+radioLitre.getText().toString()+"#"+ "\r\n"; // terminate for pc bluetooth spp server
						
						// Get the message bytes and tell the BluetoothChatService to write
						
						byte[] send = message.getBytes();
						MainActivity.mChatService.write(send);

						// Reset out string buffer to zero and clear the edit text field
					//	mOutStringBuffer.setLength(0);
						// mOutEditText.setText(mOutStringBuffer);
					
				}
				else{
					sPS1.setError("Enter PS1");
					sPS2.setError("Enter PS2");
					sPS3.setError("Enter PS3");
					sFw.setError("Enter Flow");
				}
				
			}

			
		});
		
		return rootView;
	}
	

}
