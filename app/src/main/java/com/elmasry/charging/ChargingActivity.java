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

/**
 * Free application
 * El-Masry
 * My simple application
 * Used to fast charging your balance
 */

package com.elmasry.charging;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChargingActivity extends Activity implements OnClickListener, TextWatcher/*, OnKeyListener*/ {

	private static final byte MIN_LENGTH = 15;
	private EditText numberTxt;
	private byte network;
	private TextView networkTxt;
	private SharedPreferences mPrefs;
	
	// if you change the value of this variable, don't forget to change 
	// *_digits_hint variables in strings.xml
	// separator after N digits according to the user choice
	private final String SEPARATOR = " ";
	
	// we put separator after N digits
	private int N;
	
	// according to the text of this button we setting N
	private Button fieldCapacityBtn;
	
	// track the cursor position to distinguish between deletion and insertion number
	// and move the cursor in appropriate position after number insertion process done
	private int cursorBeforePos;
	private int numBeforeLen;
	
	// using this tag to track some value
	final String TAG = "chargingActivity";
	
	// using this to set text size for numberTxt
	private float txtSize;

	/** run at first time activity created */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		// set the root view of this activity
		setContentView(R.layout.charging);
		
		// get views from this layout
		Button chargingBtn      = (Button)findViewById(R.id.chargingBtn);
		//Button changeNwBtn      = (Button)findViewById(R.id.changeNwBtn);
		Button shareBtn         = (Button)findViewById(R.id.shareBtn);
		Button lastNumBtn       = (Button)findViewById(R.id.lastNumBtn);
		Button zoomInBtn        = (Button)findViewById(R.id.zoomInBtn);
		Button zoomOutBtn       = (Button)findViewById(R.id.zoomOutBtn);
		fieldCapacityBtn = (Button)findViewById(R.id.fieldCapacity);
		networkTxt = (TextView)findViewById(R.id.networkTxt);
		numberTxt = (EditText)findViewById(R.id.number);
		
		// set event handlers for buttons
		chargingBtn.setOnClickListener(this);
		//changeNwBtn.setOnClickListener(this);
		shareBtn.setOnClickListener(this);
		lastNumBtn.setOnClickListener(this);
		zoomInBtn.setOnClickListener(this);
		zoomOutBtn.setOnClickListener(this);
		fieldCapacityBtn.setOnClickListener(this);
		numberTxt.addTextChangedListener(this);
		networkTxt.setOnClickListener(this);
		
		// get mPrefs
		mPrefs = getSharedPreferences("setting", Context.MODE_PRIVATE);
		
		// at first time, make user choose the network name first
		if (mPrefs.getBoolean("isFirstTime", true))
			startActivity(new Intent(this, Network.class));
		
		// setting the value of N (the number of digits before inserting the separator)
		// according to the user preference and make necessary changes according to N
		N = mPrefs.getInt("N", 4);
		setFieldCapacityBtnTxt(N);
		if (N == 3) numberTxt.setHint(R.string.three_digits_hint);
		else        numberTxt.setHint(R.string.four_digits_hint);
		
		
		// setting the text size for numberTxt according to user preference
		txtSize = mPrefs.getFloat("txtSize", 19);
		numberTxt.setTextSize(txtSize);
		
		// number.setOnKeyListener(this);
		
		
		
		/* number.setText("1123-3424-test");
		// set cursor at the end of the text
		number.setSelection(number.getText().length()); */
		
		
	}
	

	/** run after getting from stop state */
	@Override
	protected void onStart() {
		super.onStart();
		// get network name
		network = (byte) mPrefs.getInt("network", Network.VODAFONE);
	    
		decorateNetworkTxt();	
		
		// showing soft keypad
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
	}
	
	private void decorateNetworkTxt() {
		// adjust network text view
		switch (network) {
		case Network.ETISALAT:
			// networkTxt.setText(R.string.etisalat_str);
			networkTxt.setBackgroundColor(getResources().getColor(R.color.green));
			networkTxt.setText(R.string.etisalat_str);
			break;
		case Network.MOBINIL:
			// networkTxt.setText(R.string.mobinil_str);
			networkTxt.setBackgroundColor(getResources().getColor(R.color.orange));
			networkTxt.setText(R.string.mobinil_str);
			break;
		case Network.VODAFONE:
		default:
			// networkTxt.setText(R.string.vodafone_str);
			networkTxt.setBackgroundColor(getResources().getColor(R.color.red));
			networkTxt.setText(R.string.vodafone_str);
			break;
		}
	}
	
	/** run when some thing block SOME of activity view 
	 * running after onResume() state 
	 * Save any data you want in onPause() for the android version 10 and before
	 * After android version 10 you can save the data in onStop() (the application doesn't terminate until execute this method)*/
	@Override
	protected void onPause() {
		super.onPause();
		// save preferences according to the user choices
		savePreferences();
	}
	
	
	/** handler to Button click event */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.chargingBtn:
			String code = getStartingCode();
			String num = numberTxt.getText().toString();
			num = unformatNum(num);
			if (num.length() < MIN_LENGTH) {
				showMessage(getString(R.string.err_msg));
				break;
			}
			saveLastChargingNum(num);
			//startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+code+num+"%23")));
			startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+code+num+"%23")));
			finish();
			break;
		case R.id.lastNumBtn:
			insertLastChargingNum();
			break;
			
			/*
		case R.id.changeNwBtn:
			startActivity(new Intent(this, Network.class));
			break;
			*/

		case R.id.fieldCapacity:
			toggleNstate(fieldCapacityBtn.getText());
			break;
		
		case R.id.zoomInBtn:
			numberTxt.setTextSize(++txtSize);
			Log.i(TAG, "text size of numberTxt: " + numberTxt.getTextSize());
			break;
		case R.id.zoomOutBtn:
			numberTxt.setTextSize(--txtSize);
			Log.i(TAG, "text size of numberTxt: " + numberTxt.getTextSize());
			break;
			
		case R.id.networkTxt:
			network++;
			if (network == Network.NETWORK_COUNT) network = 0;
			decorateNetworkTxt();
			break;
			
		case R.id.shareBtn:
		default:
			Intent target = new Intent(Intent.ACTION_SEND)
		        .setType("text/plain")
		        .putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.elmasry.charging");
		    startActivity(Intent.createChooser(target, getString(R.string.share_str)));
			break;
		}
		
	}

	/** getting the starting code writing for charging before charging number 
	 * according to the given network
	 */
	
	private String getStartingCode() {
		String code;
		switch (network) {
		case Network.ETISALAT:
			code = "*556*";
			break;
		case Network.MOBINIL:
			code = "%23"+"102*";
			break;
		case Network.VODAFONE:
		default:
			code = "*858*";
			break;
		}
		return code;
	}

	/**
	 * we toggle N and the text of the button and other stuffs according to the 
	 * current text of the button remember we put separator after N digits
	 */
	private void toggleNstate(CharSequence text) {
		if (text.equals(getString(R.string.four_digits_separator_str))) {
			// toggle N and the text of the button
			N = 3;
			setFieldCapacityBtnTxt(N);
			
			// make some adaption for the new state
			numberTxt.setHint(R.string.three_digits_hint);
			makeAdaption();
			
		}
		else {
			// toggle the N and the text of the button
			N = 4;
			setFieldCapacityBtnTxt(N);
			
			// make some adaption for the new state
			numberTxt.setHint(R.string.four_digits_hint);
			makeAdaption();
		}
	}

	/** changing numberTxt string format and cursor position if the user decide to change N
	 * (number of digits before putting separator) or inserting last charging number
	 */
	private void makeAdaption() {
		// changing the text format of the numberTxt according to the new state
		String tmpTxt = numberTxt.getText().toString(); 
		tmpTxt = unformatNum(tmpTxt);
		numberTxt.setText(formatNum(tmpTxt));
		// setting the cursor at the end of the string of the numberTxt
		numberTxt.setSelection(numberTxt.length());
	}

	/** set the text of the field capacity button according to N
	 * remember we put separator after N digits
	 */
	void setFieldCapacityBtnTxt(int N) {
		switch (N) {
		case 3:
			fieldCapacityBtn.setText(getString(R.string.three_digits_separator_str));
			break;
		case 4:
		default:
			fieldCapacityBtn.setText(getString(R.string.four_digits_separator_str));
			break;
		}
	}
	
	/** getting the last charging number entered by the user
	 * and passing it to the number text 
	 */
	private void insertLastChargingNum() {
		String num = mPrefs.getString("lastNum", "");
		// check if there is stored code or not
		if (num.equals("")) {
			showMessage("لم يتم تخزين أخر رقم استخدمته فى الشحن بعد");
			return;
		}
		numberTxt.setText(num);
		makeAdaption();
	}

	/** save the last charging number entered by the user */
	private void saveLastChargingNum(String num) {
		// ===== IMPORTANT ======
		// PLEASE DON'T MODIFY KEY STRING WHICH WILL CAUSE BUGS IN APPLICATION PURPOSE
		SharedPreferences.Editor ed = mPrefs.edit();
		// saving the last charging number
		ed.putString("lastNum", num);
		// we have to commit to save my preferences
		ed.commit();
	}

	/** save N (the number of digits before putting the separator)
	 * and some other preferences according to the user choices
	 */
	private void savePreferences() {
		// ===== IMPORTANT ======
		// PLEASE DON'T MODIFY KEY STRING WHICH WILL CAUSE BUGS IN APPLICATION PURPOSE
		SharedPreferences.Editor ed = mPrefs.edit();
		// saving N (the number of digits before inserting any separator)
		ed.putInt("N", N);
		// saving the text size of the numberTxt
		ed.putFloat("txtSize", txtSize);
		
		// set network type in my preferences
		ed.putInt("network", network);
		
		// we have to commit to save my preferences
		ed.commit();
	}
	
	/*
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		showMessage("onKey method");
		return true;
	}*/

	private void showMessage(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();	
	}

	//=================================================
	/*
	 * handler for edit text changing
	 */
	/** 
	 * getting the cursor position and number string length before stroking any number or deletion key 
	 * */ 
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// getting the cursor position and number string length for using it 
		// afterwards in distinguishing between insertion or deletion number operation was done 
		cursorBeforePos = numberTxt.getSelectionStart();
		numBeforeLen = numberTxt.length();
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * putting separator after N digits
	 */
	@Override
	public void afterTextChanged(Editable s) {
		String formattedNum = formatNum(s.toString());
		if (formattedNum.length() == 0) return;
		// if you remove lines "line^^*" 
		// you will get stack overflow error
		/* line^^1 */
		numberTxt.removeTextChangedListener(this);
		numberTxt.setText(formattedNum);
		/* line^^2 */
		numberTxt.addTextChangedListener(this);
		
		// insert the cursor into the appropriate position
		int formattedNumLen = formattedNum.length();
		if      (numBeforeLen >= formattedNumLen && cursorBeforePos > 0)
			// then it is a deletion process so we need to move the cursor
			if (formattedNumLen < numBeforeLen - 2)
				// we handle the following scenario: TRY THIS 1234 1234 5678 
				// THEN PUT THE CURSOR AFTER 5 THEN LONG CLICKING ON DELETION KEY ON SOFT KEY
				numberTxt.setSelection(0);
			else if (cursorBeforePos - 1 > formattedNumLen)
				// imagine the following scenario: putting 1234 5| and delete 5
				numberTxt.setSelection(cursorBeforePos - 2);
			else
				// normal case: just deletion one number (or moving one step backward) from the previous string
				numberTxt.setSelection(cursorBeforePos - 1);
		// we begin with the most common case in insertion and simple one
		else if (cursorBeforePos == formattedNumLen - 1)
			// then the cursor cann't move further than that (since 
			// formattedNumLen - 1 represents the last index of the number string)
			// so we put the cursor after last character in the string
			numberTxt.setSelection(cursorBeforePos + 1);
		else if (cursorBeforePos < formattedNumLen - 1)
			if (formattedNum.charAt(cursorBeforePos) == ' ') 
				// then we need to move the cursor 2 step forward
				numberTxt.setSelection(cursorBeforePos + 2);
		    // else we move the cursor one step forward
			else
				numberTxt.setSelection(cursorBeforePos + 1);		
	}

	//================================================
	
	/**
	 * putting a number in a certain format according to field capacity
	 * putting separator after 3 or 4 digits according to the user choice 
	 */
	private String formatNum(String numTxt) {
		if (numTxt.length() == 0) return "";
		StringBuilder formattedNum = new StringBuilder();
		numTxt = unformatNum(numTxt);
		for (int i = 0, len = numTxt.length(); i < len ; i++) {
			formattedNum.append(numTxt.charAt(i));
			// "i < len - 1" because we don't want to insert SEPARATOR after the LAST N digits
			if (i < len - 1 && (i + 1) % N == 0) formattedNum.append(SEPARATOR); 
		}
		return formattedNum.toString();
	}

	/** remove all separators from the number string */
	private String unformatNum(String num) {
		return num.replace(SEPARATOR, "");
	}


}
