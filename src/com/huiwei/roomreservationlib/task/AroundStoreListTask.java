package com.huiwei.roomreservationlib.task;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
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
import com.huiwei.roomreservationlib.info.StoreInfo;

public class AroundStoreListTask extends AsyncTask<String, Void, Integer> {
	private Context context = null;
	private Handler handler = null;
	private Message message = null;
	private int startIndex = 0;

	public AroundStoreListTask(Context context, Handler handler, int startIndex) {
		this.context = context;
		this.handler = handler;
		this.startIndex = startIndex;
	}

	@Override
	protected Integer doInBackground(String... params) {
		int flag = Constant.SUCCESS;
		try {
			
			HttpPost httpPost = new HttpPost(UrlConstant.AROUND_STORE_LIST_URL);
			JSONObject param = new JSONObject();
			param.put("p", startIndex);
			param.put("cat_id", Data.searchInfo.classifyID);
			param.put("user_id", Data.memberInfo.id);
			param.put("mac", CommonFunction.getLocalMacAddress(context));
			param.put("location", ""+Data.memberInfo.longitude+","+Data.memberInfo.latitude);
			param.put("cpp_min", Data.searchInfo.fromSearch ? Data.searchInfo.cppMin : "");
			param.put("cpp_max", Data.searchInfo.fromSearch ? Data.searchInfo.cppMax : "");
			param.put("recommend", Data.searchInfo.fromSearch ? Data.searchInfo.recommend : "");
			JSONArray roomTypes = new JSONArray();
			for(int i=0; i<Data.searchInfo.roomSizeList.size(); i++) {
				roomTypes.put(Data.searchInfo.roomSizeList.get(i));
			}
			param.put("room_types", Data.searchInfo.fromSearch ? roomTypes : "");

			httpPost.setEntity(new StringEntity(param.toString()));
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(httpPost);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				if (startIndex == 0) {
					Data.clearAroundStoreList();
				}

				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				JSONObject json = new JSONObject(retSrc);
				JSONArray jsonArray = json.getJSONArray("content");
				if (jsonArray.length() != 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						StoreInfo storeInfo = new StoreInfo();
						storeInfo.parseJson(jsonObject);
						Data.aroundStoreList.add(storeInfo);
					}
				} else {
					flag = Constant.DATA_LOAD_COMPLETE;
				}
				
//				Data.autoLogout(context, json.getBoolean("is_login"));
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

		if (handler != null) {
			message = new Message();
			message.what = result;
			handler.sendMessage(message);
		}
	}
}
