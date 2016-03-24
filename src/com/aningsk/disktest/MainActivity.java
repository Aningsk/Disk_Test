package com.aningsk.disktest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Intent service;
	private serviceReceiver receiver; 
	private TextView showView, showDiskSize, showRamSize, showInformation;
	private Button startButton, stopButton, reslutButton;
	private EditText timesEditText;
	private RadioGroup selectDisk;
	private Spinner bufferSpinner;
	private List<String> bufferList;
	private ArrayAdapter<String> bufferAdapter;
	private boolean startFlag = false; //make sure Service cannot start before stop.
	private boolean inforFlag = true;
	private String resultPath = null;
	
	private boolean lockRadioGroup = false;
	private int selectedRadioButton;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showView = (TextView)findViewById(R.id.textView1);
        selectDisk = (RadioGroup)findViewById(R.id.radioGroup);
        startButton = (Button)findViewById(R.id.button1);
        stopButton = (Button)findViewById(R.id.button2);
        reslutButton = (Button)findViewById(R.id.button3);
        showRamSize = (TextView)findViewById(R.id.textView2);
        showDiskSize = (TextView)findViewById(R.id.textView3);
        showInformation = (TextView)findViewById(R.id.textView4);
        bufferSpinner = (Spinner)findViewById(R.id.spinner);
        timesEditText = (EditText)findViewById(R.id.editText);
    
        bufferList = new ArrayList<String>();
        for (int i = 0; i < DiskTestApplication.BUFFER.length - 1; i++) 
        	if (0 != DiskTestApplication.BUFFER[i])
        		bufferList.add(DiskTestApplication.BUFFER[i] + " B");

        bufferAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bufferList);
        bufferAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bufferSpinner.setAdapter(bufferAdapter);
        
        receiver = new serviceReceiver();
		IntentFilter testFilter = new IntentFilter("TestEnd");
		IntentFilter failFilter = new IntentFilter("TestFail");
		IntentFilter resultFilter = new IntentFilter("TestResult");
		registerReceiver(receiver, testFilter);
		registerReceiver(receiver, failFilter);
		registerReceiver(receiver, resultFilter);
        service = new Intent(MainActivity.this, TestService.class);
        
        selectedRadioButton = DiskTestApplication.getInternalDiskSelectState() ? R.id.radioButton1 : R.id.radioButton2;
        selectDisk.check(selectedRadioButton);
        
        timesEditText.setOnEditorActionListener(new OnEditorActionListener(){
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				String valueStr = timesEditText.getText().toString();
				if (valueStr.length() == 0)
					valueStr = "0";
				//The "Times" is COUNT in TestService. Just different names.
				DiskTestApplication.setCount(Integer.valueOf(valueStr).intValue());
				return false;
			}
        });
        bufferSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub		
				DiskTestApplication.setBufferSize(DiskTestApplication.BUFFER[(int)id]);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
        });
        selectDisk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				if (!lockRadioGroup) {
					changeSelectDisk(arg0, arg1);
					
					if (DiskTestApplication.getTakeCrossTestSelectState()) 
						showDiskSize.setText("");
					else
						showDiskSize.setText(getResources().getString(R.string.disk_size) + ":" + 
				        		SystemInfo.getAvailableDiskSize() / 1024 / 1024 + " MB " + 
				        		getResources().getString(R.string.available)+ " - " + getResources().getString(R.string.total) + " " +
				        		SystemInfo.getTotalDiskSize() / 1024 / 1024 + " MB ");
					showInformation.setText(getResources().getString(R.string.partitions) + ":" + 
			        		"\n" + SystemInfo.getPartitions());
					reslutButton.setText(R.string.result_button);
					inforFlag = true;
				} else {
					showView.setText(R.string.change_locked);
					selectDisk.check(selectedRadioButton);
				}
			}
        });
        startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				clickStart(arg0);
			}
        });
        stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				clickStop(arg0);
			}
        });
        reslutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					clickResult(arg0);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        });
        
        showRamSize.setText(getResources().getString(R.string.ram_size) + ":" + 
        		SystemInfo.getRamSize());
        showDiskSize.setText(getResources().getString(R.string.disk_size) + ":" + 
        		SystemInfo.getAvailableDiskSize() / 1024 / 1024 + " MB " + 
        		getResources().getString(R.string.available)+ " - " + getResources().getString(R.string.total) + " " +
        		SystemInfo.getTotalDiskSize() / 1024 / 1024 + " MB ");
        showInformation.setText(getResources().getString(R.string.partitions) + ":" + 
        		"\n" + SystemInfo.getPartitions());
        
        showInformation.setMovementMethod(ScrollingMovementMethod.getInstance());  
        showInformation.setHorizontallyScrolling(true);
    }
    
    protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		stopService(service);
    }
    
    public void changeSelectDisk(RadioGroup arg0, int arg1) {
		switch (arg1) {
		case R.id.radioButton1: //Internal Disk
			DiskTestApplication.selectInternalDisk(true);
			selectedRadioButton = R.id.radioButton1;
			showView.setText(R.string.test_on_inter);
			break;
		case R.id.radioButton2: //External Disk
			if (SystemInfo.externalMemoryAvailable()) {
				DiskTestApplication.selectInternalDisk(false);
				selectedRadioButton = R.id.radioButton2;
				showView.setText(R.string.test_on_exter);
			} else {
				DiskTestApplication.selectInternalDisk(true);
				selectedRadioButton = R.id.radioButton1;
				arg0.check(R.id.radioButton1);
				showView.setText(R.string.cannot_change);
			}
			break;
		case R.id.radioButton3: //Cross Test
			if (SystemInfo.externalMemoryAvailable()) {
				DiskTestApplication.selectCrossTest(true);
				selectedRadioButton = R.id.radioButton3;
				showView.setText(R.string.test_on_both);
			}else {
				DiskTestApplication.selectInternalDisk(true);
				selectedRadioButton = R.id.radioButton1;
				arg0.check(R.id.radioButton1);
				showView.setText(R.string.cannot_change);
			}
			break;
		}
    }
    
	public void clickStart(View v) {
		if (DiskTestApplication.getCount() == 0) {
			DiskTestApplication.setCount(DiskTestApplication.defaultCount);
			timesEditText.setText(Integer.valueOf(DiskTestApplication.defaultCount).toString());
			Toast.makeText(MainActivity.this, getResources().getString(R.string.set_default_times), Toast.LENGTH_LONG).show(); 
		}
		
		if (checkDiskBigEnough() == false)
			return;
		
		lockRadioGroup = true;
		selectDisk.setEnabled(false);
		bufferSpinner.setEnabled(false);
		timesEditText.setEnabled(false);
		if (!startFlag) {
			showView.setText(R.string.please_wait);
			startFlag = true;
			startService(service);
		} else {
			showView.setText(R.string.please_stop);
		}
	}
	
	public void clickStop(View v) {
		lockRadioGroup = false;
		selectDisk.setEnabled(true);
		bufferSpinner.setEnabled(true);
		timesEditText.setEnabled(true);
		if (startFlag)
			startFlag = false;
		showView.setText(R.string.test_stop);
		stopService(service);
	}
	
	public void clickResult(View v) throws IOException{
		String result = "Disk Test Result: \n";
		if (inforFlag) {
			resultPath = DiskTestApplication.getResultPath();
			File resultFile = new File(resultPath + File.separator + DiskTestApplication.getResultFileName());
			InputStream instream = new FileInputStream(resultFile);
			if (instream != null) {
				InputStreamReader inputreader = new InputStreamReader(instream);
				BufferedReader bufferreader = new BufferedReader(inputreader);
				String line;
				while ((line = bufferreader.readLine()) != null)
					result += line + "\n";
				instream.close();
			}
			showInformation.setText(result);
			reslutButton.setText(R.string.partitions_button);
		} else {
			showInformation.setText(getResources().getString(R.string.partitions) + ":" + 
	        		"\n" + SystemInfo.getPartitions());
			reslutButton.setText(R.string.result_button);
		}
		inforFlag = !inforFlag;
	}
	
	/**
	 * private boolean checkDiskBigEnough()
	 * 		used by clickStart()
	 * 
	 * Check whether the disk(internal OR/AND external) is big enough.
	 * If the disk is too small to running any test, 
	 * RETURN false!
	 * If the disk is not enough big to run all test, or it's big enough,
	 * RETURN true!
	 *
	 * The function is TOO UGLY, you can fold it. Orz...
	 */
	private boolean checkDiskBigEnough() {
		Toast toast;
		
		switch (selectedRadioButton) {
		case R.id.radioButton1: //Internal Disk
			if (SystemInfo.getAvailableInternalMemorySize() <= 
					DiskTestApplication.testsize[0] * DiskTestApplication.KB) {
				toast = Toast.makeText(MainActivity.this, 
						getResources().getString(R.string.too_small_disk), 
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				DiskTestApplication.setDiskBigEnough(false);
				return false;
			} else if (SystemInfo.getAvailableInternalMemorySize() <= 
					DiskTestApplication.testsize[DiskTestApplication.testsize.length - 1] * DiskTestApplication.MB) {
				toast = Toast.makeText(MainActivity.this, 
						getResources().getString(R.string.need_more_disk), 
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				DiskTestApplication.setDiskBigEnough(false);
			} else {
				DiskTestApplication.setDiskBigEnough(true);
			}
			break;
		case R.id.radioButton2: //External Disk
			if (SystemInfo.getAvailableExternalMemorySize() <= 
					DiskTestApplication.testsize[0] * DiskTestApplication.KB) {
				toast = Toast.makeText(MainActivity.this, 
						getResources().getString(R.string.too_small_disk), 
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				DiskTestApplication.setDiskBigEnough(false);
				return false;
			} else if (SystemInfo.getAvailableExternalMemorySize() <= 
					DiskTestApplication.testsize[DiskTestApplication.testsize.length - 1] * DiskTestApplication.MB) {
				toast = Toast.makeText(MainActivity.this, 
						getResources().getString(R.string.need_more_disk), 
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				DiskTestApplication.setDiskBigEnough(false);
			} else {
				DiskTestApplication.setDiskBigEnough(true);
			}
			break;
		case R.id.radioButton3: //Cross Test
			if (SystemInfo.getAvailableExternalMemorySize() <= 
					DiskTestApplication.testsize[0] * DiskTestApplication.KB || 
					SystemInfo.getAvailableInternalMemorySize() <= 
					DiskTestApplication.testsize[0] * DiskTestApplication.KB) {
				toast = Toast.makeText(MainActivity.this, 
						getResources().getString(R.string.too_small_disk), 
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				DiskTestApplication.setDiskBigEnough(false);
				return false;
			} else if (SystemInfo.getAvailableExternalMemorySize() <= 
					DiskTestApplication.testsize[DiskTestApplication.testsize.length - 1] * DiskTestApplication.MB || 
					SystemInfo.getAvailableInternalMemorySize() <= 
					DiskTestApplication.testsize[DiskTestApplication.testsize.length - 1] * DiskTestApplication.MB) {
				toast = Toast.makeText(MainActivity.this, 
						getResources().getString(R.string.need_more_disk), 
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				DiskTestApplication.setDiskBigEnough(false);
			} else {
			DiskTestApplication.setDiskBigEnough(true);
			}
			break;
		}
		
		return true;
	}

	private class serviceReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			boolean endFlag = false;
			boolean successFlag = false;
			boolean failFlag = false;

			endFlag = intent.getBooleanExtra("endFlag", false);
			successFlag = intent.getBooleanExtra("successFlag", false);
			failFlag = intent.getBooleanExtra("failFlag", false);
			
			if (endFlag && successFlag)
				showView.setText(R.string.test_success);
			else if (endFlag && !successFlag)
				showView.setText(R.string.test_fail);
			
			if (failFlag)
				showView.setText(R.string.test_fail);
		}
		
	}
}

