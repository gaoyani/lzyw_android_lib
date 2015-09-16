package com.huiwei.roomreservationlib.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
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
import com.huiwei.roomreservationlib.info.TimeSlotInfo;

public class GetTimeSlotTask extends
		AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	String date;
	List<TimeSlotInfo> timeList;

	public GetTimeSlotTask(Context context, Handler handler, String date, List<TimeSlotInfo> timeList) {
		this.context = context;
		this.handler = handler;
		this.date = date;
		this.timeList = timeList;
	}

	@Override
	protected Integer doInBackground(String... params) {
		message = new Message();
		int flag = Constant.SUCCESS;
		String url = UrlConstant.TIME_SLOT_URL+"time/"+date+"/room_id/"+Data.roomInfo.id;
		
		try {
			HttpGet httpGet = new HttpGet(url);
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(httpGet);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonObject = new JSONObject(retSrc);
				int errorCode = jsonObject.getInt("error");
				if (errorCode == 0) {
					if (timeList == null) {
						timeList = new ArrayList<TimeSlotInfo>();
					} else {
						timeList.clear();
					}
					JSONArray timeArray = jsonObject.getJSONArray("content");
					for(int i=0; i<timeArray.length(); i++) {
						JSONObject timeJson = timeArray.getJSONObject(i);
						TimeSlotInfo info = new TimeSlotInfo();
						info.id = timeJson.getString("id");
						info.time = timeJson.getString("time");
						info.isBookable = !timeJson.getBoolean("disable");
						timeList.add(info);
					}
					message.obj = timeList;
				} else {
					message.obj = jsonObject.getString("message");
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
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (handler != null) {
			message.what = result;
			handler.sendMessage(message);
		}
	}
}
