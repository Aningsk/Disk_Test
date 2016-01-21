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
import android.widget.TextView;

public class MainActivity extends Activity {
	private Intent service;
	private serviceReceiver receiver; 
	private SystemInfo systemInfo;
	private TextView showView, showDiskSize, showRamSize, showInformation;
	private Button startButton, stopButton, reslutButton;
	private boolean startFlag = false; //make sure Service cannot start before stop.
	private boolean inforFlag = true;
	private String resultPath = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showView = (TextView)findViewById(R.id.textView1);
        startButton = (Button)findViewById(R.id.button1);
        stopButton = (Button)findViewById(R.id.button2);
        reslutButton = (Button)findViewById(R.id.button3);
        showRamSize = (TextView)findViewById(R.id.textView2);
        showDiskSize = (TextView)findViewById(R.id.textView3);
        showInformation = (TextView)findViewById(R.id.textView4);
        
        systemInfo = new SystemInfo();
        receiver = new serviceReceiver();
		IntentFilter testFilter = new IntentFilter("TestEnd");
		IntentFilter failFilter = new IntentFilter("TestFail");
		IntentFilter resultFilter = new IntentFilter("TestResult");
		registerReceiver(receiver, testFilter);
		registerReceiver(receiver, failFilter);
		registerReceiver(receiver, resultFilter);
        service = new Intent(MainActivity.this, TestService.class);
        
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
        		systemInfo.getRamSize());
//        showDiskSize.setText(getResources().getString(R.string.disk_size) + ":" + 
//        		Integer.parseInt(systemInfo.getDiskSize()) * 512 / 1024 / 1024 + " MB");
        showDiskSize.setText(getResources().getString(R.string.disk_size) + ":" + 
        		systemInfo.getAvailableInternalDiskSize() / 1024 / 1024 + " MB " + 
        		getResources().getString(R.string.available)+ " - " + getResources().getString(R.string.total) + " " +
        		systemInfo.getTotalInternalDiskSize() / 1024 / 1024 + " MB ");
        showInformation.setText(getResources().getString(R.string.partitions) + ":" + 
        		"\n" + systemInfo.getPartitions());
        
        showInformation.setMovementMethod(ScrollingMovementMethod.getInstance());  
        showInformation.setHorizontallyScrolling(true);
    }
    
    protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		stopService(service);
    }
    
	public void clickStart(View v) {
		if (!startFlag) {
			showView.setText(R.string.please_wait);
			startFlag = true;
			startService(service);
		} else {
			showView.setText(R.string.please_stop);
		}
	}
	
	public void clickStop(View v) {
		if (startFlag)
			startFlag = false;
		showView.setText(R.string.test_stop);
		stopService(service);
	}
	
	public void clickResult(View v) throws IOException{
		String result = "";
		resultPath = DiskTestApplication.getContext().getFilesDir() + File.separator + "DiskTest";
		File resultFile = new File(resultPath + File.separator + "TestResult.txt");
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

