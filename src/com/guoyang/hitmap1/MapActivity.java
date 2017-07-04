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
    
  
    // ��λ�������  
    public LocationClient locationClient = null;  
    //�Զ���ͼ��  
    BitmapDescriptor mCurrentMarker = null;  
    boolean isFirstLoc = true;// �Ƿ��״ζ�λ  
	protected double locLatitude;
	protected double locLongititude;
  
    public BDLocationListener myListener = new BDLocationListener() {  
        @Override  
        public void onReceiveLocation(BDLocation location) {  
            // map view ���ٺ��ڴ����½��յ�λ��  
            if (location == null || mapView == null)  
                return;  
              
            MyLocationData locData = new MyLocationData.Builder()  
                    .accuracy(location.getRadius())  
                    // �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360  
                    .direction(100).latitude(location.getLatitude())  
                    .longitude(location.getLongitude()).build();  
            baiduMap.setMyLocationData(locData);    //���ö�λ����  
              
              locLatitude = location.getLatitude();
              locLongititude= location.getLongitude();
            if (isFirstLoc) {  
                isFirstLoc = false;  
                  
                  
                LatLng ll = new LatLng(location.getLatitude(),  
                        location.getLongitude());  
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);   //���õ�ͼ���ĵ��Լ����ż���  
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
        // ��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext  
        // ע��÷���Ҫ��setContentView����֮ǰʵ��  
        SDKInitializer.initialize(getApplicationContext());  
        setContentView(R.layout.activity_map);  
        
          init();
        
  
    }  
  
    private void init() {
    	mapView = (MapView) this.findViewById(R.id.bmapView); // ��ȡ��ͼ�ؼ�����  
        baiduMap = mapView.getMap();  
        //������λͼ��  
        baiduMap.setMyLocationEnabled(true);  
          
        locationClient = new LocationClient(getApplicationContext()); // ʵ����LocationClient��  
        locationClient.registerLocationListener(myListener); // ע���������  
        this.setLocationOption();   //���ö�λ����  
        locationClient.start(); // ��ʼ��λ  
        // baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL); // ����Ϊһ���ͼ  
  
        // baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE); //����Ϊ���ǵ�ͼ  
        // baiduMap.setTrafficEnabled(true); //������ͨͼ  
     
		
        Button btn_dangqian = (Button) this.findViewById(R.id.btn_dangqian); 
        Button btn_jingwei = (Button) this.findViewById(R.id.btn_jingwei); 
        Button btn_miaoshu = (Button) this.findViewById(R.id.btn_miaoshu); 
        
        //����ǰλ�ö�λ
        btn_dangqian.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				LatLng ll = new LatLng(locLatitude,  
                        locLongititude);  
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);   //���õ�ͼ���ĵ��Լ����ż���  
                baiduMap.animateMapStatus(u);  
				
			}
        	
        });
        //����γ�ȶ�λ
        btn_jingwei.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				 LayoutInflater inflater = getLayoutInflater();
				 final View layout = inflater.inflate(R.layout.dialog_jingwei, null);
				new AlertDialog.Builder(MapActivity.this).setTitle("�����뾭γ��").setView(layout)
				.setPositiveButton("ȷ��",new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						  EditText la_et = (EditText)layout.findViewById(R.id.latitude);
							EditText long_et = (EditText)layout.findViewById(R.id.longititude);
						String latitude = la_et.getText()+"";
						String longititude = long_et.getText()+"";
						if(latitude.equals("")){
							la_et.setError("���벻��Ϊ�գ�");
							
							return;
						}
						if(longititude.equals("")){
							long_et.setError("���벻��Ϊ�գ�");
							return;
						}
						
						  LatLng ll = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longititude));
				          MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);   //���õ�ͼ���ĵ��Լ����ż���  
				          baiduMap.animateMapStatus(u);   
				         
					}
					}) 
					.setNegativeButton("ȡ��", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							
						}
						
					}
						
					).show();
					}
					
				
        	
        });
        //��������Ϣ��λ
        btn_miaoshu.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
			
				 LayoutInflater inflater = getLayoutInflater();
				final View layout= inflater.inflate(R.layout.dialog_miaoshu, null);
				new AlertDialog.Builder(MapActivity.this).setTitle("������������Ϣ").setView(layout)
				.setPositiveButton("ȷ��",new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						EditText city_et = (EditText)layout.findViewById(R.id.cityname);
						EditText address_et = (EditText)layout.findViewById(R.id.address);
						String city = city_et.getText()+"";
						String address = address_et.getText()+"";
						if(city.equals("")){
							city_et.setError("���벻��Ϊ�գ�");
							return;
						}
						if(address.equals("")){
							address_et.setError("���벻��Ϊ�գ�");
							return;
						}
						GeoCoder mSearch = GeoCoder.newInstance();
						OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {  
						    public void onGetGeoCodeResult(GeoCodeResult result) {  
						        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
						        	Toast toast = Toast.makeText(getApplicationContext(),
						        		     "û�м����������", Toast.LENGTH_LONG);
						        	toast.show();
									return;
						        }  
						        LatLng ll = result.getLocation();
				                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);   //���õ�ͼ���ĵ��Լ����ż���  
				                baiduMap.animateMapStatus(u);   
						        
						    }  
						 
						    @Override  
						    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {  
						        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
						            //û���ҵ��������  
						        }  
						        //��ȡ������������  
						    }  
						};
						mSearch.setOnGetGeoCodeResultListener(listener);
						mSearch.geocode(new GeoCodeOption().city(city).address(address));
					}
					
				}) 
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						
					}
					
				}
					
				).show();
				}
				
			
        	
        });
	}

	// ����״̬ʵ�ֵ�ͼ�������ڹ���  
    @Override  
    protected void onDestroy() {  
        //�˳�ʱ���ٶ�λ  
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
     * ���ö�λ���� 
     */  
    private void setLocationOption() {  
        LocationClientOption option = new LocationClientOption();  
        option.setOpenGps(true); // ��GPS  
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// ���ö�λģʽ  
        option.setCoorType("bd09ll"); // ���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02  
        option.setScanSpan(5000); // ���÷���λ����ļ��ʱ��Ϊ5000ms  
        option.setIsNeedAddress(true); // ���صĶ�λ���������ַ��Ϣ  
        option.setNeedDeviceDirect(true); // ���صĶ�λ��������ֻ���ͷ�ķ���  
          
        locationClient.setLocOption(option);  
    }  
  
}  