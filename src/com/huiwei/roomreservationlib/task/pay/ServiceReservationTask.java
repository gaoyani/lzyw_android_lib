package com.huiwei.roomreservationlib.task.pay;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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

public class ServiceReservationTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	int flag = 0;
	ReservationInfo reservationInfo;

	public ServiceReservationTask(Context context, Handler handler, ReservationInfo reservationInfo) {
		this.context = context;
		this.handler = handler;
		this.reservationInfo = reservationInfo;
	}

	@Override
	protected Integer doInBackground(String... params) {
		String url = UrlConstant.ADD_SERVICE_ORDER_URL;
		message = new Message();
		flag = Constant.SUCCESS;
		try {
			HttpPost request = new HttpPost(url);
			JSONObject param = new JSONObject();
			param.put("user_id", Data.memberInfo.id);
			param.put("mac", CommonFunction.getLocalMacAddress(context));
			param.put("sid", Data.serviceInfo.id);
			param.put("time", reservationInfo.time);
			param.put("contact", reservationInfo.contacts);
			param.put("phone_number", reservationInfo.phoneNum);
			param.put("order_call", reservationInfo.sex);
//			param.put("man_num", reservationInfo.peopleNum);
			param.put("other_info", reservationInfo.otherInfo);

			request.setEntity(new StringEntity(param.toString(), HTTP.UTF_8));
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(request);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonObject = new JSONObject(retSrc);
				int errorCode = jsonObject.getInt("error");
				List<String> infoList = new ArrayList<String>();
				if (errorCode == 0) {
					JSONObject jsonContent = jsonObject.getJSONObject("content");
					infoList.add(jsonContent.getString("order_id"));
					infoList.add(jsonContent.getString("order_sn"));
					infoList.add(jsonContent.getString("price"));
					message.obj = infoList;
				} else {
					message.obj = jsonObject.getString("message");
					flag = Constant.DATA_ERROR;
//					Data.autoLogout(context, jsonObject.getBoolean("is_login"));
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
