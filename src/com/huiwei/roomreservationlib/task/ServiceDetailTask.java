package com.huiwei.roomreservationlib.task;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.huiwei.commonlib.CommonFunction;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.TaskHttpClient;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.UrlConstant;
import com.huiwei.roomreservationlib.info.TimeSlotInfo;

public class ServiceDetailTask extends AsyncTask<String, Void, Integer> {
	private Context context = null;
	private Handler handler = null;
	private Message message = null;
	private String serviceID;

	public ServiceDetailTask(Context context, Handler handler, String serviceID) {
		this.context = context;
		this.handler = handler;
		this.serviceID = serviceID;
	}

	@Override
	protected Integer doInBackground(String... params) {
		message = new Message();
		int flag = Constant.SUCCESS;
		
		try {
			HttpPost request = new HttpPost(UrlConstant.SERVICE_DETAIL_URL);
			JSONObject param = new JSONObject();
			param.put("service_id", serviceID);
			param.put("user_id", Data.memberInfo.id);
			param.put("mac", CommonFunction.getLocalMacAddress(context));
			request.setEntity(new StringEntity(param.toString()));
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(request);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				if (!parseDetail(retSrc)) {
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
	
	private boolean parseDetail(String retSrc) throws JSONException {
		JSONObject json = new JSONObject(retSrc);
		String content = json.getString("content");
		if (content.equals("[]")) {
			return false;
		}
		
		Data.serviceInfo.clearData();
		JSONObject jsonObject = new JSONObject(content);
		Data.serviceInfo.id = jsonObject.getString("sid");
		Data.serviceInfo.nameTitle = jsonObject.getString("nameTitle");
		Data.serviceInfo.name = jsonObject.getString("servicename");
		Data.serviceInfo.otherInfo = jsonObject.getString("feature");
		Data.serviceInfo.otherTitle = jsonObject.getString("featureshow");
		Data.serviceInfo.consumeTitle = jsonObject.getString("feildname");
		Data.serviceInfo.consumeValue = jsonObject.getString("consumption");
		Data.serviceInfo.consumeOriginal = jsonObject.getString("market_price");
		Data.serviceInfo.serviceDuration = jsonObject.getString("duration");
		Data.serviceInfo.serviceTime = jsonObject.getString("office_hours");
		Data.serviceInfo.picture360Url = jsonObject.getString("picture360Url");
		
		Data.serviceInfo.hot = (int) (Integer.valueOf(jsonObject.getString("room_hot")));
		Data.serviceInfo.isOrdered = jsonObject.getBoolean("is_order");
		Data.serviceInfo.isCommented = jsonObject.getBoolean("is_comment");
		Data.serviceInfo.isComplainted = jsonObject.getBoolean("is_complain");
		
		JSONArray picArray = jsonObject.getJSONArray("common_pic");
		for (int i=0; i<picArray.length(); i++) {
			Data.serviceInfo.picUrlList.add(picArray.getString(i));
		}
		
		Data.autoLogout(context, json.getBoolean("is_login"));
		
		return true;
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);

		if (handler != null) {
			message.what = result;
			handler.sendMessage(message);
		}
	}
}
