package com.huiwei.roomreservationlib.task.pay;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.R.string;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;

import com.huiwei.commonlib.MD5;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.TaskHttpClient;
import com.huiwei.roomreservationlib.data.UrlConstant;
import com.huiwei.roomreservationlib.info.StoreInfo;

public class YLCreateOrderTask extends AsyncTask<String, Void, Integer> {
	private Context context = null;
	private Handler handler = null;
	private Message message = null;
	private String money;
	private String orderID;

	public YLCreateOrderTask(Context context, Handler handler, String money, String orderID) {
		this.context = context;
		this.handler = handler;
		this.money = money;
		this.orderID = orderID;
	}

	@Override
	protected Integer doInBackground(String... params) {
		message = new Message();
		int flag = Constant.SUCCESS;
		String merchantName = "乐自有我";
		String mobile = "";
		String payType = "60";
		String description = "";
		String app = "0";
		String md5 = MD5.md5s(merchantName+Data.payId+orderID+mobile+
				money+payType+description+app+Data.payMD5);
		
		try {
			HttpPost httpPost = new HttpPost(UrlConstant.YL_CREATE_ORDER_URL);
			
			String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
			xmlString += "<Payment>";
			xmlString += "<merchantName>"+merchantName+"</merchantName>";
			xmlString += "<merchantId>"+Data.payId+"</merchantId>";
			xmlString += "<businessOrderId>"+orderID+"</businessOrderId>";
			xmlString += "<mobile>"+mobile+"</mobile>";
			xmlString += "<amount>"+money+"</amount>";
			xmlString += "<payType>"+payType+"</payType>";
			xmlString += "<description>"+description+"</description>";
			xmlString += "<app>"+app+"</app>";
			xmlString += "<md5>"+md5+"</md5>";
			xmlString += "</Payment>";

			httpPost.setEntity(new StringEntity(xmlString, HTTP.UTF_8));
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(httpPost);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				if (!retSrc.equals("null")) {
					
					XmlPullParser xmlParser = Xml.newPullParser();
					xmlParser.setInput(new StringReader(retSrc));
					xmlParser.next();  
					xmlParser.require(XmlPullParser.START_TAG, null, "Payment");  
					xmlParser.nextTag(); 
					Log.v("",xmlParser.getName()+"="+ xmlParser.nextText()); 
					xmlParser.nextTag(); 
					Log.v("",xmlParser.getName()+"="+ xmlParser.nextText());
					xmlParser.nextTag(); 
					Log.v("",xmlParser.getName()+"="+ xmlParser.nextText());
					
					
				} else {
					flag = Constant.DATA_ERROR;
				}
			} else {
				flag = Constant.NET_ERROR;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			flag = Constant.OTHER_ERROR;
		} 

		return flag;

	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);

		message.what = result;
		handler.sendMessage(message);
	}
}
