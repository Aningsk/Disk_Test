package com.aningsk.disktest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {
	private Intent service;
	private serviceReceiver receiver; 
	private TextView showView, showDiskSize, showRamSize, showInformation;
	private Button startButton, stopButton, reslutButton;
	private RadioGroup selectDisk;
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
        
        selectDisk.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				if (!lockRadioGroup) {
					changeSelectDisk(arg0, arg1);
					selectedRadioButton = arg1;
					
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
			showView.setText(R.string.test_on_inter);
			DiskTestApplication.selectInternalDisk(true);
			break;
		case R.id.radioButton2: //External Disk
			if (SystemInfo.externalMemoryAvailable()) {
				showView.setText(R.string.test_on_exter);
				DiskTestApplication.selectInternalDisk(false);
			} else {
				showView.setText(R.string.cannot_change);
				DiskTestApplication.selectInternalDisk(true);
				arg0.check(R.id.radioButton1);
			}
			break;
		}
    }
    
	public void clickStart(View v) {
		lockRadioGroup = true;
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

