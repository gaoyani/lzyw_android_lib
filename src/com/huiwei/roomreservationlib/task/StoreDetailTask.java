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
import com.huiwei.commonlib.MD5;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.TaskHttpClient;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.UrlConstant;
import com.huiwei.roomreservationlib.info.NewsInfo;
import com.huiwei.roomreservationlib.info.RoomInfo;
import com.huiwei.roomreservationlib.info.ServiceInfo;
import com.huiwei.roomreservationlib.info.StoreDetailInfo;

public class StoreDetailTask extends AsyncTask<String, Void, Integer> {
	private Context context = null;
	private Handler handler = null;
	private Message message = null;
	private String id;
	private int startIndex;

	public StoreDetailTask(Context context, String storeID, int startIndex, Handler handler) {
		this.context = context;
		this.handler = handler;
		this.id = storeID;
		this.startIndex = startIndex;
	}

	@Override
	protected Integer doInBackground(String... params) {
		message = new Message();
		int flag = Constant.SUCCESS;
		
		try {
			HttpPost request = new HttpPost(UrlConstant.STORE_DETAIL_URL);
			JSONObject param = new JSONObject();
			param.put("business_id", id);
			param.put("user_id", Data.memberInfo.id);
			param.put("location", ""+Data.memberInfo.longitude+","+Data.memberInfo.latitude);
			param.put("mac", CommonFunction.getLocalMacAddress(context));
			param.put("p", startIndex);

			request.setEntity(new StringEntity(param.toString()));
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(request);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				if (startIndex == 0) {
					flag = parseStoreDetail(retSrc);
				} else {
					flag = parseRoomList(retSrc);
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
	
	private int parseStoreDetail(String retSrc) throws JSONException {
		JSONObject json = new JSONObject(retSrc);
		int errorCode = json.getInt("error");
		if (errorCode != 0) {
			message.obj = json.getString("message");
			return Constant.DATA_ERROR;
		}
		
		if (Data.storeDetailInfo == null) {
			Data.storeDetailInfo = new StoreDetailInfo();
		} else {
			Data.storeDetailInfo.clearData();
		}
		
		JSONObject jsonObject = json.getJSONObject("content");
		Data.storeDetailInfo.id = jsonObject.getString("business_id");
		Data.storeDetailInfo.name = jsonObject.getString("name");
		Data.storeDetailInfo.iconUrl = jsonObject.getString("iconUrl");
		Data.storeDetailInfo.address = jsonObject.getString("address");
		Data.storeDetailInfo.stars = Float.valueOf(jsonObject.getString("recommend"));
		Data.storeDetailInfo.cpp = jsonObject.getString("cpp");
		Data.storeDetailInfo.service = jsonObject.getString("service");
		Data.storeDetailInfo.phoneNumber = jsonObject.getString("phoneNumber");
		Data.storeDetailInfo.distance = jsonObject.getString("distance");
		Data.storeDetailInfo.time = jsonObject.getString("time");
		Data.storeDetailInfo.recommend = jsonObject.getString("food");
		Data.storeDetailInfo.consumeTitle = jsonObject.getString("feildname");
		Data.storeDetailInfo.consumeValue = jsonObject.getString("consumption");
		Data.storeDetailInfo.storeTips = jsonObject.getString("merchantTips");
		Data.storeDetailInfo.roomNum = jsonObject.getString("room_numb");
		Data.storeDetailInfo.serviceNum = jsonObject.getString("serviceNumber");
		Data.storeDetailInfo.favorite = jsonObject.getBoolean("favorite");
		Data.storeDetailInfo.favoriteNum = jsonObject.getInt("favoriteNum");
		Data.storeDetailInfo.isOrdered = jsonObject.getBoolean("is_order");
		Data.storeDetailInfo.isCommented = jsonObject.getBoolean("is_comment");
		Data.storeDetailInfo.isComplainted = jsonObject.getBoolean("is_complain");
		String location = jsonObject.getString("map_label");
		String coordinate[] = location.split(",");
		Data.storeDetailInfo.longitude = Double.valueOf(coordinate[0]);
		Data.storeDetailInfo.latitude = Double.valueOf(coordinate[1]);
		
		JSONArray newsArray = jsonObject.getJSONArray("news_list");
		for (int i = 0; i < newsArray.length(); i++) {
			JSONObject newsJson = newsArray.getJSONObject(i);
			NewsInfo newsInfo = new NewsInfo();
			newsInfo.id = newsJson.getString("id");
			newsInfo.name = newsJson.getString("title");
			Data.storeDetailInfo.newsList.add(newsInfo);
		}
		
		Data.storeDetailInfo.picture360Url = jsonObject.getString("picture360Url");
		JSONArray picArray = jsonObject.getJSONArray("common_pic");
		for (int i=0; i<picArray.length(); i++) {
			Data.storeDetailInfo.picUrlList.add(picArray.getString(i));
		}
		
		JSONArray roomArray = jsonObject.getJSONArray("room_list");
		for (int i=0; i<roomArray.length(); i++) {
			JSONObject jsonRoom = roomArray.getJSONObject(i);
			RoomInfo roomInfo = new RoomInfo();
			roomInfo.id = jsonRoom.getString("room_id");
			roomInfo.name = jsonRoom.getString("room_name");
			roomInfo.nameTitle = jsonRoom.getString("nameTitle");
			roomInfo.priceType = jsonRoom.getString("price_name");
			roomInfo.price = jsonRoom.getString("price");
			roomInfo.otherInfo = jsonRoom.getString("otherInfo");
			roomInfo.otherTitle = jsonRoom.getString("otherTitle");
			roomInfo.iconUrl = jsonRoom.getString("iconUrl");
			roomInfo.feature = jsonRoom.getString("special");
			roomInfo.consumeTitle = jsonRoom.getString("feildname");
			roomInfo.consumeValue = jsonRoom.getString("consumption");
			roomInfo.bookable = jsonRoom.getBoolean("noreserv");
			
			Data.storeDetailInfo.roomList.add(roomInfo);
		}
		
		JSONArray serviceArray = jsonObject.getJSONArray("service_list");
		for (int i=0; i<serviceArray.length(); i++) {
			JSONObject jsonService = serviceArray.getJSONObject(i);
			ServiceInfo serviceInfo = new ServiceInfo();
			serviceInfo.id = jsonService.getString("sid");
			serviceInfo.name = jsonService.getString("room_name");
			serviceInfo.nameTitle = jsonService.getString("nameTitle");
			serviceInfo.price = jsonService.getString("price");
			serviceInfo.priceType = jsonService.getString("price_name");
			serviceInfo.otherInfo = jsonService.getString("feature");
			serviceInfo.otherTitle = jsonService.getString("featureshow");
			serviceInfo.iconUrl = jsonService.getString("iconUrl");
			serviceInfo.consumeOriginal = jsonService.getString("market_price");
			serviceInfo.consumeTitle = jsonService.getString("feildname");
			serviceInfo.consumeValue = jsonService.getString("consumption");
			serviceInfo.bookable = jsonService.getBoolean("noreserv");
			
			Data.storeDetailInfo.serviceList.add(serviceInfo);
		}
		
		Data.autoLogout(context, json.getBoolean("is_login"));
		
		return Constant.SUCCESS;
	}
	
	private int parseRoomList(String retSrc) throws JSONException {
		JSONObject jsonObject = new JSONObject(retSrc);
		JSONArray roomArray = jsonObject.getJSONArray("room_list");
		if (roomArray.length() == 0)
			return Constant.SUCCESS;
		
		for (int i=0; i<roomArray.length(); i++) {
			JSONObject jsonRoom = roomArray.getJSONObject(i);
			RoomInfo roomInfo = new RoomInfo();
			roomInfo.id = jsonRoom.getString("room_id");
			roomInfo.name = jsonRoom.getString("room_name");
			roomInfo.priceType = jsonRoom.getString("price_name");
			roomInfo.price = jsonRoom.getString("price");
//			roomInfo.peopleNum = jsonRoom.getString("galleryful");
			roomInfo.iconUrl = jsonRoom.getString("iconUrl");
			roomInfo.feature = jsonRoom.getString("special");
			roomInfo.consumeTitle = jsonRoom.getString("feildname");
			roomInfo.consumeValue = jsonRoom.getString("consumption");
			roomInfo.bookable = jsonRoom.getBoolean("noreserv");
			
			Data.storeDetailInfo.roomList.add(roomInfo);
		}
		
		return Constant.DATA_LOAD_COMPLETE;
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
