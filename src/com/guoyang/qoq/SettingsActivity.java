package com.guoyang.qoq;

import com.guoyang.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;



public class SettingsActivity extends Activity implements OnClickListener {
	 EditText et_ip;
     EditText et_sendport;
     EditText et_rcvport;
	 public void onCreate(Bundle savedInstanceState) {    
	        super.onCreate(savedInstanceState);    
	        setContentView(R.layout.activity_settings);    
	            
	         et_ip = (EditText)findViewById(R.id.et_ip);
	         et_sendport = (EditText)findViewById(R.id.et_sendport);
	         et_rcvport = (EditText)findViewById(R.id.et_rcvport);
	        Button btn_ok = (Button)findViewById(R.id.btn_ok);
	        
	        btn_ok.setOnClickListener(this);
	        
	        SharedPreferences sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);

	        //getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值

	        String ip= sharedPreferences.getString("ip", "");
	        String sendport= sharedPreferences.getString("sendport", "");
	        String rcvport= sharedPreferences.getString("rcvport", "");
	         et_ip.setText(ip);
	         et_sendport.setText(sendport);
	         et_rcvport.setText(rcvport);
	      
	    }

	@Override
	public void onClick(View arg0) {
		
		SharedPreferences mySharedPreferences=getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=mySharedPreferences.edit();    
        
        editor.putString("ip",et_ip.getText()+"");    
        editor.putString("sendport",et_sendport.getText()+"");    
        editor.putString("rcvport",et_rcvport.getText()+"");    
        editor.commit();    
	}    
}

