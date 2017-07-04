package com.guoyang.hitmap1;
import android.app.Activity;  
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;  
import android.util.Log;  
  

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;  
import com.baidu.location.BDLocationListener;  
import com.baidu.location.LocationClient;  
import com.baidu.location.LocationClientOption;  
import com.baidu.mapapi.SDKInitializer;  
import com.baidu.mapapi.map.BaiduMap;  
import com.baidu.mapapi.map.BitmapDescriptor;  
import com.baidu.mapapi.map.MapStatusUpdate;  
import com.baidu.mapapi.map.MapStatusUpdateFactory;  
import com.baidu.mapapi.map.MapView;  
import com.baidu.mapapi.map.MyLocationData;  
import com.baidu.mapapi.model.LatLng;  
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.guoyang.R;
  
  
public class MapActivity extends Activity {  
    public MapView mapView = null;  
    public BaiduMap baiduMap = null;  
    
  
    // 定位相关声明  
    public LocationClient locationClient = null;  
    //自定义图标  
    BitmapDescriptor mCurrentMarker = null;  
    boolean isFirstLoc = true;// 是否首次定位  
	protected double locLatitude;
	protected double locLongititude;
  
    public BDLocationListener myListener = new BDLocationListener() {  
        @Override  
        public void onReceiveLocation(BDLocation location) {  
            // map view 销毁后不在处理新接收的位置  
            if (location == null || mapView == null)  
                return;  
              
            MyLocationData locData = new MyLocationData.Builder()  
                    .accuracy(location.getRadius())  
                    // 此处设置开发者获取到的方向信息，顺时针0-360  
                    .direction(100).latitude(location.getLatitude())  
                    .longitude(location.getLongitude()).build();  
            baiduMap.setMyLocationData(locData);    //设置定位数据  
              
              locLatitude = location.getLatitude();
              locLongititude= location.getLongitude();
            if (isFirstLoc) {  
                isFirstLoc = false;  
                  
                  
                LatLng ll = new LatLng(location.getLatitude(),  
                        location.getLongitude());  
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);   //设置地图中心点以及缩放级别  
                baiduMap.animateMapStatus(u);  
            }  
        }

		@Override
		public void onConnectHotSpotMessage(String arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}  
    };  
  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        // 在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        // 注意该方法要再setContentView方法之前实现  
        SDKInitializer.initialize(getApplicationContext());  
        setContentView(R.layout.activity_map);  
        
          init();
        
  
    }  
  
    private void init() {
    	mapView = (MapView) this.findViewById(R.id.bmapView); // 获取地图控件引用  
        baiduMap = mapView.getMap();  
        //开启定位图层  
        baiduMap.setMyLocationEnabled(true);  
          
        locationClient = new LocationClient(getApplicationContext()); // 实例化LocationClient类  
        locationClient.registerLocationListener(myListener); // 注册监听函数  
        this.setLocationOption();   //设置定位参数  
        locationClient.start(); // 开始定位  
        // baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); // 设置为一般地图  
  
        // baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE); //设置为卫星地图  
        // baiduMap.setTrafficEnabled(true); //开启交通图  
     
		
        Button btn_dangqian = (Button) this.findViewById(R.id.btn_dangqian); 
        Button btn_jingwei = (Button) this.findViewById(R.id.btn_jingwei); 
        Button btn_miaoshu = (Button) this.findViewById(R.id.btn_miaoshu); 
        
        //按当前位置定位
        btn_dangqian.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				LatLng ll = new LatLng(locLatitude,  
                        locLongititude);  
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);   //设置地图中心点以及缩放级别  
                baiduMap.animateMapStatus(u);  
				
			}
        	
        });
        //按经纬度定位
        btn_jingwei.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				 LayoutInflater inflater = getLayoutInflater();
				 final View layout = inflater.inflate(R.layout.dialog_jingwei, null);
				new AlertDialog.Builder(MapActivity.this).setTitle("请输入经纬度").setView(layout)
				.setPositiveButton("确定",new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						  EditText la_et = (EditText)layout.findViewById(R.id.latitude);
							EditText long_et = (EditText)layout.findViewById(R.id.longititude);
						String latitude = la_et.getText()+"";
						String longititude = long_et.getText()+"";
						if(latitude.equals("")){
							la_et.setError("输入不能为空！");
							
							return;
						}
						if(longititude.equals("")){
							long_et.setError("输入不能为空！");
							return;
						}
						
						  LatLng ll = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longititude));
				          MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);   //设置地图中心点以及缩放级别  
				          baiduMap.animateMapStatus(u);   
				         
					}
					}) 
					.setNegativeButton("取消", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							
						}
						
					}
						
					).show();
					}
					
				
        	
        });
        //按描述信息定位
        btn_miaoshu.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
			
				 LayoutInflater inflater = getLayoutInflater();
				final View layout= inflater.inflate(R.layout.dialog_miaoshu, null);
				new AlertDialog.Builder(MapActivity.this).setTitle("请输入描述信息").setView(layout)
				.setPositiveButton("确定",new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						EditText city_et = (EditText)layout.findViewById(R.id.cityname);
						EditText address_et = (EditText)layout.findViewById(R.id.address);
						String city = city_et.getText()+"";
						String address = address_et.getText()+"";
						if(city.equals("")){
							city_et.setError("输入不能为空！");
							return;
						}
						if(address.equals("")){
							address_et.setError("输入不能为空！");
							return;
						}
						GeoCoder mSearch = GeoCoder.newInstance();
						OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {  
						    public void onGetGeoCodeResult(GeoCodeResult result) {  
						        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
						        	Toast toast = Toast.makeText(getApplicationContext(),
						        		     "没有检索到结果！", Toast.LENGTH_LONG);
						        	toast.show();
									return;
						        }  
						        LatLng ll = result.getLocation();
				                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);   //设置地图中心点以及缩放级别  
				                baiduMap.animateMapStatus(u);   
						        
						    }  
						 
						    @Override  
						    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {  
						        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
						            //没有找到检索结果  
						        }  
						        //获取反向地理编码结果  
						    }  
						};
						mSearch.setOnGetGeoCodeResultListener(listener);
						mSearch.geocode(new GeoCodeOption().city(city).address(address));
					}
					
				}) 
				.setNegativeButton("取消", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						
					}
					
				}
					
				).show();
				}
				
			
        	
        });
	}

	// 三个状态实现地图生命周期管理  
    @Override  
    protected void onDestroy() {  
        //退出时销毁定位  
        locationClient.stop();  
        baiduMap.setMyLocationEnabled(false);  
        // TODO Auto-generated method stub  
        super.onDestroy();  
        mapView.onDestroy();  
        mapView = null;  
    }  
  
    @Override  
    protected void onResume() {  
        // TODO Auto-generated method stub  
        super.onResume();  
        mapView.onResume();  
    }  
  
    @Override  
    protected void onPause() {  
        // TODO Auto-generated method stub  
        super.onPause();  
        mapView.onPause();  
    }  
  
    /** 
     * 设置定位参数 
     */  
    private void setLocationOption() {  
        LocationClientOption option = new LocationClientOption();  
        option.setOpenGps(true); // 打开GPS  
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式  
        option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02  
        option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms  
        option.setIsNeedAddress(true); // 返回的定位结果包含地址信息  
        option.setNeedDeviceDirect(true); // 返回的定位结果包含手机机头的方向  
          
        locationClient.setLocOption(option);  
    }  
  
}  