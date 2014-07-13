package com.vpowerrc.vesuviusserver;



import java.io.IOException;

import com.omt.remote.util.net.WifiApControl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;


public class HomeFragment extends Fragment {
	public ProgressDialog progressBar;
	public static String TAG = "Vesuvius Server";	
	public void addProgresBar(String message) {
			
			
			
			progressBar = new ProgressDialog(getActivity());
			progressBar.setMessage(message+" ...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.setProgress(0);
			progressBar.setProgressNumberFormat(null);
			progressBar.setCancelable(false);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.show();
			
	
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
        // inflate the layout for this fragment    
    	final View view = inflater.inflate(R.layout.fragment_home, container, false);    	
    	  
    	ToggleButton toggle = (ToggleButton) view.findViewById(R.id.serverToggleButton);   
    	   
    	// create notification  	
		final Intent notificationIntent = new Intent(getActivity(), VesuviusNotificationService.class);   
					    	    	
    	toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    	    
    		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    			final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Please wait","processig", true);
    	        if (isChecked) {    	          	    	
    	        	               	        	       	
    	           	new Thread(new Runnable() {
    	                @Override
    	                public void run() {
    	                   
    	                	WifiApControl.getInstance().turnOnOffHotspot(true);
    	        			try {
								if(!Server.getInstance().isServerRunning()) {  
									Server.getInstance().start();
									Log.e(TAG, "Server start");
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
    	        			
	                        //check if hotspot started
	                        while (!WifiApControl.getInstance().isWifiApEnabled()) {    	                           
	                            try {
									Thread.sleep(200);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}                     
	                        }
	                        
                        	final String ipAddress = WifiApControl.getInstance().getIpAddress();
	                        Log.e(TAG, ipAddress);
	                        
	                        getActivity().runOnUiThread(new Runnable() {  
	                            @Override
	                            public void run() {
	                            	TextView text = (TextView) view.findViewById(R.id.textView2);
	    	                        text.setText("Vesuvius is running on \n\nhttp://"+ipAddress+":"+Server.serverPort+"/vesuvius");
	    	                        getActivity().startService(notificationIntent);    	 
	                            }
	                        });
	                        
	                        try {
								while (!Server.getInstance().isServerRunning()) {    	                           
								    Thread.sleep(500); 				    
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	                        
	                        progressDialog.dismiss();                     
    	                          	                  
    	                }
    	        	}).start();   	          
    	        	    	        
    	        } else {    	        	
    	            // The toggle is disabled	    	    
	    	        
	    	        new Thread(new Runnable() {
	    		        public void run() {	        	
	    		        	
	    		        	Server.getInstance().stop();
	    	        		Log.e(TAG, "Server stop");
	    		        	WifiApControl.getInstance().turnOnOffHotspot(false);	
	    	        		getActivity().stopService(notificationIntent);   	    		            
	    		             		            	
							try {
								while (Server.getInstance().isServerRunning()) {    	                           
								    Thread.sleep(500); 				    
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							getActivity().runOnUiThread(new Runnable() {  
	                            @Override
	                            public void run() {
									TextView text = (TextView) view.findViewById(R.id.textView2);
					                text.setText("");
					                progressDialog.dismiss();
	                            }
							});
	    		        }
	    		    }).start();		    	                   
                    
    	        }
    	    }
    		
    	
    		
    	});
    	
    	
    	
    	
           	
    	
    	return view;
    
    }
	
	
}
