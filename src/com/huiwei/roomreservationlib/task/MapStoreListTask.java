package com.huiwei.roomreservationlib.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.TaskHttpClient;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.UrlConstant;
import com.huiwei.roomreservationlib.info.StoreInfo;

public class MapStoreListTask extends AsyncTask<String, Void, Integer> {
	private Context context = null;
	private Handler handler = null;
	private Message message = null;
	private String keyWords;

	public MapStoreListTask(Context context, Handler handler, String keyWords) {
		this.context = context;
		this.handler = handler;
		this.keyWords = keyWords;
	}

	@Override
	protected Integer doInBackground(String... params) {
		message = new Message();
		int flag = Constant.SUCCESS;
		try {
			HttpPost httpPost = new HttpPost(UrlConstant.MAP_STORE_LIST_URL);
			JSONObject param = new JSONObject();
			param.put("location", ""+Data.memberInfo.longitude+","+Data.memberInfo.latitude);
			param.put("keywords", keyWords);

			httpPost.setEntity(new StringEntity(param.toString(), HTTP.UTF_8));
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(httpPost);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				JSONObject json = new JSONObject(retSrc);
				JSONArray jsonArray = json.getJSONArray("content");
				
				if (jsonArray.length() == 0) {
					flag = Constant.DATA_ERROR;
				} else {
					List<StoreInfo> storeInfoList = new ArrayList<StoreInfo>();
					for (int i=0; i<jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						StoreInfo storeInfo = new StoreInfo();
						storeInfo.id = jsonObject.getString("business_id");
						storeInfo.name = jsonObject.getString("name");
						storeInfo.address = jsonObject.getString("address");
						storeInfo.stars = Float.valueOf(jsonObject.getString("recommend"));
						storeInfo.cpp = jsonObject.getString("cpp");
						
						String location = jsonObject.getString("map_label");
						String coordinate[] = location.split(",");
						
						storeInfo.longitude = Double.valueOf(coordinate[0]);
						storeInfo.latitude = Double.valueOf(coordinate[1]);
						
						storeInfoList.add(storeInfo);
					}
					message.obj = storeInfoList;
				}
				
				Data.autoLogout(context, json.getBoolean("is_login"));
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
