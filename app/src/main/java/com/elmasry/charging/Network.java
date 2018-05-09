/*
 * Copyright (C) 2018 Yahia H. El-Tayeb
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.elmasry.charging;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Network extends Activity implements OnClickListener {
	
	static final byte NETWORK_COUNT = 3;
	static final byte ETISALAT = 0;
	static final byte VODAFONE = 1;
	static final byte MOBINIL  = 2;
	
	/** Running at first time this activity created */ 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		// set the root view for this activity
		setContentView(R.layout.network);
		
		// get all view from the layout
		TextView vodafoneTxt = ((TextView) findViewById(R.id.vodafoneTxt));
		TextView etisalatTxt = ((TextView) findViewById(R.id.etisalatTxt));
		TextView mobinilTxt = ((TextView) findViewById(R.id.mobinilTxt));
		
		// set event handlers
		vodafoneTxt.setOnClickListener(this);
		etisalatTxt.setOnClickListener(this);
		mobinilTxt.setOnClickListener(this);
		
		// set background colors for each text view
		// these lines used to avoid a freak error happening to the 
		// text views' colors in this activity 
		vodafoneTxt.setBackgroundColor(getResources().getColor(R.color.red));
		etisalatTxt.setBackgroundColor(getResources().getColor(R.color.green));
		mobinilTxt.setBackgroundColor(getResources().getColor(R.color.orange));
		
	}

	/*
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		startActivity(new Intent(this, ChargingActivity.class));
		finish();
	}
	*/
	
	/* handle the text views events */
	@Override
	public void onClick(View v) {
		
		// get network type
		byte network;
		switch (v.getId()) {
		case R.id.etisalatTxt:
			network = ETISALAT;
			break;
		case R.id.mobinilTxt:
			network = MOBINIL;
			break;
		case R.id.vodafoneTxt:
		default:
			network = VODAFONE;
			break;
		}
		
		// set network type in my preferences
		SharedPreferences.Editor ed = getSharedPreferences("setting", Context.MODE_PRIVATE).edit();
		ed.putInt("network", network);
		ed.putBoolean("isFirstTime", false);
		ed.commit();
		
		// finish this activity
		finish();
	}

}
