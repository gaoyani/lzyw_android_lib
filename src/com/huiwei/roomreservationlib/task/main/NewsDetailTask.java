package com.huiwei.roomreservationlib.task.main;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.huiwei.commonlib.CommonFunction;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.TaskHttpClient;
import com.huiwei.roomreservationlib.data.UrlConstant;

public class NewsDetailTask extends AsyncTask<String, Void, Integer> {
	private Context context = null;
	private Handler handler = null;
	private Message message = null;
	private String newsID;

	public NewsDetailTask(Context context, Handler handler, String newsID) {
		this.context = context;
		this.handler = handler;
		this.newsID = newsID;
	}

	@Override
	protected Integer doInBackground(String... params) {
		message = new Message();
		int flag = Constant.SUCCESS;
		
		try {
			HttpPost request = new HttpPost(UrlConstant.NEWS_DETAIL_URL);
			JSONObject param = new JSONObject();
			param.put("news_id", newsID);
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
		
		JSONObject jsonObject = new JSONObject(content);
		Data.newsInfo.id = newsID;
		Data.newsInfo.title = jsonObject.getString("title");
		Data.newsInfo.author = jsonObject.getString("author");
		Data.newsInfo.scanTimes = jsonObject.getString("click_count");
		Data.newsInfo.time = jsonObject.getString("add_time");
		Data.newsInfo.isCommented = jsonObject.getBoolean("is_comment");
		Data.newsInfo.content = jsonObject.getString("content");
		Data.newsInfo.content = Data.newsInfo.content.replaceAll("\\\"", "\"");

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
