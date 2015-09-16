package com.huiwei.roomreservationlib.task.main;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huiwei.commonlib.CommonFunction;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.TaskHttpClient;
import com.huiwei.roomreservationlib.data.UrlConstant;
import com.huiwei.roomreservationlib.info.StoreSceneInfo;

public class CheckSSIDTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;

	public CheckSSIDTask(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	@Override
	protected Integer doInBackground(String... params) {
		message = new Message();
		int flag = Constant.SUCCESS;
		Data.clearStoreSceneList();
		try {
			HttpPost request = new HttpPost(UrlConstant.CHECK_SSID_URL);
			JSONObject param = new JSONObject();
			param.put("longitude", Data.memberInfo.longitude);
			param.put("latitude", Data.memberInfo.latitude);
			param.put("gatway", CommonFunction.getGetway(context));
			param.put("ssid", CommonFunction.getSSID(context));
			param.put("user_id", Data.memberInfo.id);
			param.put("mac", CommonFunction.getLocalMacAddress(context));
			request.setEntity(new StringEntity(param.toString()));
			
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(request);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				JSONObject json = new JSONObject(retSrc);
				JSONArray jsonArray = json.getJSONArray("content");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					StoreSceneInfo info = new StoreSceneInfo();
					info.storeName = jsonObject.getString("business_name");
					info.storeUrl = jsonObject.getString("business_url");
					info.storeUrl2 = jsonObject.getString("business_url_other");
					Data.storeSceneList.add(info);
				}
				Data.autoLogout(context, json.getBoolean("is_login"));
			} else {
				flag = Constant.NET_ERROR;
			}
		} catch (Exception e) {
			Log.d("Store Scene", e.getMessage());
			e.printStackTrace();
			flag = Constant.OTHER_ERROR;
		}

		return flag;
	}

	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (handler != null) {
			message.what = result;
			handler.sendMessage(message);
		}
	}
}
