package com.guoyang.qoq;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.guoyang.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class QqActivity extends TitleActivity {
	private  int PORT;
	String ip;
	String sendport;
	private List<Socket> mList = new ArrayList<Socket>();
	private volatile ServerSocket server = null;
	private ExecutorService mExecutorService = null; // 线程池
	private String hostip;// 本机IP
	private Handler myHandler = null;
	private volatile boolean flag = true;// 线程标志位
	Thread  client;
	// 定义界面上的两个文本框  
    EditText input;  
    TextView show;  
    // 定义界面上的一个按钮  
    Button send;  
	 Handler handler;  
	    // 定义与服务器通信的子线程  
	 ClientThread clientThread; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qq);
		setTitle("聊天");
		showBackwardView(R.string.text_back, false);
		showForwardView(R.string.text_settings, true);
		hostip = getLocalIpAddress(); // 获取本机IP
		Log.i("hostip=",hostip);
		
		
		
	     init();//初始化界面
		startServer();
		startClient();
		//startClient();
		Button btn_stClt = (Button) findViewById(R.id.btn_startClient);
		btn_stClt.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				startClient();
			}
			
		});
		
		send.setOnClickListener(new OnClickListener()  
        {  
            @Override  
            public void onClick(View v)  
            {  
                try  
                {  
                	startClient();
                	//Thread.sleep(1000);
                    // 当用户按下发送按钮后，将用户输入的数据封装成Message，  
                    // 然后发送给子线程的Handler  
                    Message msg = new Message();  
                    msg.what = 0x345;  
                    msg.obj = input.getText().toString();  
                    while(clientThread.revHandler==null)
                    {
                    	Thread.yield();
                    }
                    clientThread.revHandler.sendMessage(msg);  
                   // handler.sendMessage(msg);
                    // 清空input文本框  
                    input.setText("");  
                   clientThread.stop();
                 
                }  
                catch (Exception e)  
                {  
                    e.printStackTrace();  
                }  
            }  
        });  
    
		
	}

	private void init() {
		input = (EditText) findViewById(R.id.et_msg);  
        send = (Button) findViewById(R.id.btn_send);  
        show = (TextView) findViewById(R.id.tv_cnt);  
        handler = new Handler() //①  
        {  
            @Override  
            public void handleMessage(Message msg)  
            {  
                
                    // 将读取的内容追加显示在文本框中  
                    show.append("\n" + msg.obj.toString());  
             
            }  
        };  
        TextView tv_ip = (TextView) findViewById(R.id.tv_ip);
		tv_ip.setText(hostip);
	}

	private void startClient() {
		
		
        SharedPreferences sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);

	      //getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值

	     ip= sharedPreferences.getString("ip", "");
	     sendport= sharedPreferences.getString("sendport", "");
	     
        clientThread = new ClientThread(handler,ip,sendport);  
        // 客户端启动ClientThread线程创建网络连接、读取来自服务器的数据  
       client= new Thread(clientThread); //①  
       client.start();
        
	}

	private void startServer(){
	
			// 取得非UI线程传来的msg，以改变界面
			myHandler = new Handler() {
			public void handleMessage(Message msg) {
				  Log.i("136myHandler", msg.obj.toString());
				// 将读取的内容追加显示在文本框中  
                show.append("\n" + msg.obj.toString());  
			}
		};
		ServerThread serverThread = new ServerThread();
		flag = true;
		
		serverThread.start();
		
	}
	@Override
	protected void onForward(View forwardView) {
		Intent intent = new Intent(QqActivity.this, SettingsActivity.class);
		QqActivity.this.startActivity(intent);
	}

	// Server端的主线程
	class ServerThread extends Thread {

		public void stopServer() {
			try {
				if (server != null) {
					server.close();
					System.out.println("close task successed");
				}
			} catch (IOException e) {
				System.out.println("close task failded");
			}
		}

		public void run() {

			try {
				SharedPreferences sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
			     PORT =Integer.parseInt(sharedPreferences.getString("rcvport", "")) ;
				server = new ServerSocket(PORT);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println("S2: Error");
				e1.printStackTrace();
			}
			mExecutorService = Executors.newCachedThreadPool(); // 创建一个线程池
			System.out.println("服务器已启动...");
			Socket client = null;
			while (flag) {
				try {
					System.out.println("S3: Error");
					client = server.accept();
					Log.d("sockettest","dd");
					System.out.println("S4: Error");
					// 把客户端放入客户端集合中
					mList.add(client);
					mExecutorService.execute(new Service(client)); // 启动一个新的线程来处理连接
				} catch (IOException e) {
					System.out.println("S1: Error");
					e.printStackTrace();
				}
			}

		}
	}

	// 获取本地IP
	public static String getLocalIpAddress() {
		String hostIp = null;  
	    try {  
	        Enumeration nis = NetworkInterface.getNetworkInterfaces();  
	        InetAddress ia = null;  
	        while (nis.hasMoreElements()) {  
	            NetworkInterface ni = (NetworkInterface) nis.nextElement();  
	            Enumeration<InetAddress> ias = ni.getInetAddresses();  
	            while (ias.hasMoreElements()) {  
	                ia = ias.nextElement();  
	                if (ia instanceof Inet6Address) {  
	                    continue;// skip ipv6  
	                }  
	                String ip = ia.getHostAddress();  
	                if (!"127.0.0.1".equals(ip)) {  
	                    hostIp = ia.getHostAddress();  
	                    break;  
	                }  
	            }  
	        }  
	    } catch (SocketException e) {  
	        Log.i("yao", "SocketException");  
	        e.printStackTrace();  
	    }  
	    return hostIp;  
	  
	}

	// 处理与client对话的线程
	class Service implements Runnable {
		private volatile boolean kk = true;
		private Socket socket;
		private BufferedReader in = null;
		private String msg = "";

		public Service(Socket socket) {
			this.socket = socket;
			try {
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				Log.i("inok", "ok");
				msg = "OK";
				this.sendmsg(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		public void run() {

			while (kk) {
				try {
					if ((msg = in.readLine()) != null) {
						  Log.i("244run", msg);
						Message msgLocal = new Message();
						msgLocal.what = 0x1234;
						msgLocal.obj = hostip+":"+msg;
						myHandler.sendMessage(msgLocal);
					
						this.sendmsg(msg);

					}
				} catch (IOException e) {
					System.out.println("close");
					kk = false;
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}

			}

		}

		// 向客户端发送信息
		public void sendmsg(String msg) {
			System.out.println(msg);
			PrintWriter pout = null;
			try {
				pout = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);
				pout.println(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	class ClientThread implements Runnable  
	{  
	    private Socket s;  
	    // 定义向UI线程发送消息的Handler对象  
	    private Handler handler;  
	    // 定义接收UI线程的消息的Handler对象  
	    public Handler revHandler;  
	    public String ip;
	    public String port;
	    // 该线程所处理的Socket所对应的输入流  
	    BufferedReader br = null;  
	    OutputStream os = null;  
	  
	    public ClientThread(Handler handler,String ip,String port)  
	    {  
	        this.handler = handler;  
	        this.ip=ip;
	        this.port=port;
	    }  
	  
	    
	    public void stop(){
	    	try {
				
				
                 this.s.shutdownOutput();
                 this.s.close();
                 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
		public void run()  
	    {  
	        try  
	        {  
	            s = new Socket(ip, Integer.parseInt(port));  
	            br = new BufferedReader(new InputStreamReader(  
	                s.getInputStream()));  
	            os = s.getOutputStream();  
	            // 启动一条子线程来读取服务器响应的数据  
	            new Thread()  
	            {  
	                @Override  
	                public void run()  
	                {  
	                    String content = null;  
	                    // 不断读取Socket输入流中的内容。  
	                    try  
	                    {  
	                        while ((content = br.readLine()) != null)  
	                        {  
	                        	
	                            // 每当读到来自服务器的数据之后，发送消息通知程序界面显示该数据  
	                            Message msg = new Message();  
	                            msg.what = 0x123;  
	                            msg.obj = content;  
	                            handler.sendMessage(msg);  
	                            System.out.println(msg.obj.toString());  
	                        }  
	                    }  
	                    catch (IOException e)  
	                    {  
	                        e.printStackTrace();  
	                    }  
	                }  
	            }.start();  
	            // 为当前线程初始化Looper  
	            Looper.prepare();  
	            // 创建revHandler对象  
	            revHandler = new Handler()  
	            {  
	                @Override  
	                public void handleMessage(Message msg)  
	                {  
	                    
	                        // 将用户在文本框内输入的内容写入网络  
	                        try  
	                        {  
	                            os.write((msg.obj.toString() + "\r\n")  
	                                .getBytes("utf-8"));  
	                            show.append("\r\n"+msg.obj.toString());
	                            Log.i("349revHandler", msg.obj.toString());
	                        }  
	                        catch (Exception e)  
	                        {  
	                            e.printStackTrace();  
	                        }  
	                   
	                }  
	            };  
	            // 启动Looper  
	            Looper.loop();  
	        }  
	        catch (SocketTimeoutException e1)  
	        {  
	            System.out.println("网络连接超时！！");  
	        }  
	        catch (Exception e)  
	        {  
	            e.printStackTrace();  
	        }  
	    }  
	}  
}
