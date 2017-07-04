package com.guoyang;

import hit.treasure.activity.CalculatorActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.guoyang.hitmap1.MapActivity;
import com.guoyang.qoq.QqActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity  {
    private GridView gview;
    private List<Map<String, Object>> data_list;
    private SimpleAdapter sim_adapter;
    // 图片
    private int[] icon = { R.drawable.calculator, R.drawable.qq,
            R.drawable.map, R.drawable.other};
    private String[] iconName = { "计算器", "即时通讯", "地图查询", "其他"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        setTitle("百宝箱");
        
        
        gview = (GridView) findViewById(R.id.gview);
        data_list = new ArrayList<Map<String, Object>>();
        getData();
        String [] from ={"image","text"};
        int [] to = {R.id.image,R.id.text};
        sim_adapter = new SimpleAdapter(this, data_list, R.layout.griditem, from, to);
        gview.setAdapter(sim_adapter);
        
        gview.setOnItemClickListener(new OnItemClickListener(){
            public void onItemClick(AdapterView<?> arg0, View arg1, int idx, long arg3) {
          // TODO Auto-generated method stub

            	Intent intent;
                switch (icon[idx]){  //构建起来的图标数组
                    case R.drawable.qq:                             
                    	 intent=new Intent(MainActivity.this, QqActivity.class);
                    	 MainActivity.this.startActivity(intent);
                        break;
                    case R.drawable.calculator:                             
                    	 intent=new Intent(MainActivity.this, CalculatorActivity.class);
                    	 MainActivity.this.startActivity(intent);
                            break;
                    case R.drawable.map:        
                    	 intent=new Intent(MainActivity.this, MapActivity.class);
                    	 MainActivity.this.startActivity(intent);
                            break;
                    case R.drawable.other:                             
                    	
                 		
                            break;
                    default:
                        break;
                }
                
            }
        });
    }

    
    
    public List<Map<String, Object>> getData(){        
        for(int i=0;i<icon.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            data_list.add(map);
        }
            
        return data_list;
    }
    
   
}