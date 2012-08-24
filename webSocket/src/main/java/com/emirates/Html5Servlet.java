package com.emirates;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocket.OnFrame;
import org.eclipse.jetty.websocket.WebSocketServlet;

public class Html5Servlet extends WebSocketServlet {

	private AtomicInteger index = new AtomicInteger();

	private static final List<String> tickers = new ArrayList<String>();
	static{
		tickers.add("ajeesh");
		tickers.add("peeyu");
		tickers.add("kidillan");
		tickers.add("entammo");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WebSocket doWebSocketConnect(HttpServletRequest req, String resp) {
		System.out.println("On server");
		return new StockTickerSocket();
	}
	protected String getMyJsonTicker(){
		StringBuilder start=new StringBuilder("{");
		start.append("\"stocks\":[");
		int counter=0;
		for (String aTicker : tickers) {
			counter++;

			start.append("{ \"ticker\":\""+aTicker +"\""+","+"\"price\":\""+index.incrementAndGet()+"\" }");
			if(counter<tickers.size()){
				start.append(",");
			}
		}
		start.append("]");
		start.append("}");
		return start.toString();
	}
	public class StockTickerSocket implements OnFrame
	{

		private Connection connection;
		Timer timer; 

		@Override
		public void onClose(int arg0, String arg1) {
			// TODO Auto-generated method stub
			timer.cancel();
			
		}

		@Override
		public void onOpen(Connection connection) {
			// TODO Auto-generated method stub
			this.connection=connection;
			timer=new Timer();
			
		}

		@Override
		public boolean onFrame(byte arg0, byte arg1, byte[] arg2, int arg3,
				int arg4) {
			System.out.println("Incoming data :" + arg2);
			String data = new String(arg2);
			if(data.indexOf("disconnect")>=0){
				connection.close();
			}else{
				timer.schedule(new TimerTask() {

						@Override
						public void run() {
							try{
								System.out.println("Running task");
								connection.sendMessage(getMyJsonTicker());
							}
							catch (IOException e) {
								e.printStackTrace();
							}
						}
					}, new Date(),5000);

			}
			return true;
		}

		@Override
		public void onHandshake(FrameConnection arg0) {
			// TODO Auto-generated method stub
			
		}

	}


}