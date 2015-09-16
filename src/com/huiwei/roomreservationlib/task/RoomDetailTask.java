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

public class RoomDetailTask extends AsyncTask<String, Void, Integer> {
	private Context context = null;
	private Handler handler = null;
	private Message message = null;
	private String roomID;

	public RoomDetailTask(Context context, Handler handler, String roomID) {
		this.context = context;
		this.handler = handler;
		this.roomID = roomID;
	}

	@Override
	protected Integer doInBackground(String... params) {
		message = new Message();
		int flag = Constant.SUCCESS;
		
		try {
			HttpPost request = new HttpPost(UrlConstant.ROOM_DETAIL_URL);
			JSONObject param = new JSONObject();
			param.put("room_id", roomID);
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
		
		Data.roomInfo.clearData();
		JSONObject jsonObject = new JSONObject(content);
		Data.roomInfo.id = jsonObject.getString("room_id");
		Data.roomInfo.nameTitle = jsonObject.getString("nameTitle");
		Data.roomInfo.name = jsonObject.getString("room_name");
		Data.roomInfo.otherInfo = jsonObject.getString("otherInfo");
		Data.roomInfo.recommend = jsonObject.getString("recommend");
		Data.roomInfo.otherTitle = jsonObject.getString("otherTitle");
		Data.roomInfo.hot = (int) (Integer.valueOf(jsonObject.getString("room_hot")));
		Data.roomInfo.picture360Url = jsonObject.getString("picture360Url");
		Data.roomInfo.roomClassify = jsonObject.getString("cat_name");
		Data.roomInfo.picture360Url = jsonObject.getString("picture360Url");
		Data.roomInfo.feature = jsonObject.getString("special");
		Data.roomInfo.consumeTitle = jsonObject.getString("feildname");
		Data.roomInfo.consumeValue = jsonObject.getString("consumption");
		Data.roomInfo.isOrdered = jsonObject.getBoolean("is_order");
		Data.roomInfo.isCommented = jsonObject.getBoolean("is_comment");
		Data.roomInfo.isComplainted = jsonObject.getBoolean("is_complain");
		if (jsonObject.getString("privilege").equals("[]")) {
			Data.roomInfo.privilegeID = "0";
			Data.roomInfo.privilegeTitle = "";
		} else {
			JSONObject privilegeObj = jsonObject.getJSONObject("privilege");
			Data.roomInfo.privilegeID = privilegeObj.getString("id");
			Data.roomInfo.privilegeTitle = privilegeObj.getString("title");
		}
		
		JSONArray picArray = jsonObject.getJSONArray("common_pic");
		for (int i=0; i<picArray.length(); i++) {
			Data.roomInfo.picUrlList.add(picArray.getString(i));
		}
		
		JSONArray timeArray = jsonObject.getJSONArray("calendar_list");
		for(int i=0; i<timeArray.length(); i++) {
			JSONObject timeJson = timeArray.getJSONObject(i);
			TimeSlotInfo info = new TimeSlotInfo();
			info.id = timeJson.getString("id");
			info.time = timeJson.getString("time");
			info.isBookable = !timeJson.getBoolean("disable");
			Data.roomInfo.todayTimeList.add(info);
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
