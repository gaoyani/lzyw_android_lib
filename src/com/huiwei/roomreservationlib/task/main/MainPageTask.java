package com.huiwei.roomreservationlib.task.main;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
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
import com.huiwei.commonlib.FileManager;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.TaskHttpClient;
import com.huiwei.roomreservationlib.data.UrlConstant;
import com.huiwei.roomreservationlib.info.NewsInfo;
import com.huiwei.roomreservationlib.info.StoreInfo;

public class MainPageTask extends AsyncTask<String, Void, Integer> {
	private Context context = null;
	private Handler handler = null;
	private Message message = null;
	private int flag = 0;
	private boolean isLocalData = true;

	public MainPageTask(Context context, Handler handler, boolean isLocalData) {
		this.context = context;
		this.handler = handler;
		this.isLocalData = isLocalData;
	}

	@Override
	protected Integer doInBackground(String... params) {
		flag = Constant.SUCCESS;
		try {
			if (isLocalData) {
				String data = FileManager.readTxtFile(
						Constant.getMainPageJsonFile());
				if (data.equals("")) {
					getNetData();
				} else {
					Data.recommendInfo.parseJson(data, isLocalData);
				}
			} else {
				getNetData();
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag = Constant.OTHER_ERROR;
		} 
		
		return flag;
	}
	
	private void getNetData() throws Exception {
		HttpPost httpGet = new HttpPost(UrlConstant.MAIN_PAGE_URL);
		JSONObject param = new JSONObject();
		param.put("user_id", Data.memberInfo.id);
		param.put("location", ""+Data.memberInfo.longitude+","+Data.memberInfo.latitude);
		httpGet.setEntity(new StringEntity(param.toString()));
		TaskHttpClient taskClient = new TaskHttpClient();
		HttpResponse httpResponse = taskClient.client.execute(httpGet);
		int code = httpResponse.getStatusLine().getStatusCode();
		if (code == HttpStatus.SC_OK) {
			Data.recommendInfo.clearData();
			String retSrc = EntityUtils.toString(httpResponse.getEntity());
			Log.d("json", retSrc);
			Data.recommendInfo.parseJson(retSrc, false);
		} else {
			flag = Constant.NET_ERROR;
		}
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
