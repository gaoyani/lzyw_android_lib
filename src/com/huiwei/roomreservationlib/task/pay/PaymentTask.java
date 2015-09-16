package com.huiwei.roomreservationlib.task.pay;

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

import com.huiwei.commonlib.CommonFunction;
import com.huiwei.commonlib.MD5;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.TaskHttpClient;
import com.huiwei.roomreservationlib.data.UrlConstant;
import com.huiwei.roomreservationlib.info.ReservationInfo;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public class PaymentTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	int flag = Constant.SUCCESS;
	String orderID;
	int payType;
	String password;
	int payNum;

	public PaymentTask(Context context, Handler handler, String orderID, 
			int payType, String password, int payNum) {
		this.context = context;
		this.handler = handler;
		this.orderID = orderID;
		this.payType = payType;
		this.password = password;
		this.payNum = payNum;
	}

	@Override
	protected Integer doInBackground(String... params) {
		message = new Message();
		flag = Constant.SUCCESS;
		String url = UrlConstant.PAYMENT_URL;
		try {
			HttpPost request = new HttpPost(url);
			JSONObject param = new JSONObject();
			param.put("order_id", orderID);
			param.put("user_id", Data.memberInfo.id);
			param.put("mac", CommonFunction.getLocalMacAddress(context));
			param.put("error_numb", payNum);
			param.put("pay_id", payType);
			param.put("payment_password", MD5.md5s(password));

			request.setEntity(new StringEntity(param.toString()));
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(request);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonObject = new JSONObject(retSrc);
				int errorCode = jsonObject.getInt("error");
				if (errorCode == 0) {
					String content = "";
					JSONArray contentArray = jsonObject.getJSONArray("content");
					for (int i = 0; i < contentArray.length(); i++) {
						content += contentArray.getString(i);
					}
					
					message.obj = content.replaceAll("\'", "\"");
				} else {
					flag = Constant.DATA_ERROR;
					message.obj = jsonObject.getString("message");
					Data.autoLogout(context, jsonObject.getBoolean("is_login"));
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
	
	public String getLocalMacAddress() { 
        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE); 
        WifiInfo info = wifi.getConnectionInfo(); 
        return info.getMacAddress();
   }  

	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		message.what = result;
		handler.sendMessage(message);
	}
}
