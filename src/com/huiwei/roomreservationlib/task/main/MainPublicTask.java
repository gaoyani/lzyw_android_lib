package com.huiwei.roomreservationlib.task.main;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.huiwei.commonlib.Preferences;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.TaskHttpClient;
import com.huiwei.roomreservationlib.data.UrlConstant;
import com.huiwei.roomreservationlib.info.CityInfo;
import com.huiwei.roomreservationlib.info.CityInfo.AreaInfo;
import com.huiwei.roomreservationlib.info.CityInfo.RegionInfo;
import com.huiwei.roomreservationlib.info.RoomSizeInfo;
import com.huiwei.roomreservationlib.info.TimeSlotInfo;

public class MainPublicTask extends AsyncTask<String, Void, Integer> {
	private Context context = null;
	private Handler handler = null;
	private Message message = null;
	private int flag = 0;

	public MainPublicTask(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	@Override
	protected Integer doInBackground(String... params) {
		flag = Constant.SUCCESS;
		try {
			HttpPost httpPost = new HttpPost(UrlConstant.MAIN_PUBLIC_URL);
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(httpPost);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				Data.roomSizeList.clear();
				
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonObject = new JSONObject(retSrc);
				
				JSONArray roomTypeArray = jsonObject.getJSONArray("room_cat");
				for (int i = 0; i < roomTypeArray.length(); i++) {
					JSONObject roomJson = roomTypeArray.getJSONObject(i);
					RoomSizeInfo info = new RoomSizeInfo();
					info.id = roomJson.getString("cat_id");
					info.name = roomJson.getString("cat_name");
					Data.roomSizeList.add(info);
				}
				
				JSONObject cppObject = jsonObject.getJSONObject("business_expense");
				Data.cppMax = Integer.valueOf(cppObject.getString("cpp_max"));
				Data.cppMin = Integer.valueOf(cppObject.getString("cpp_max"));
				
				String reasons[] = jsonObject.getString("complain_reason").split(",");
				for (int i = 0; i < reasons.length; i++) {
					TimeSlotInfo info = new TimeSlotInfo();
					info.time = reasons[i];
					info.isBookable = true;
					Data.complaintReasonList.add(info);
				}
				
				Data.complaintReasonList.get(0).isReserved = true;
				
				JSONArray orderTypeArray = jsonObject.getJSONArray("order_type");
				for (int i = 0; i < orderTypeArray.length(); i++) {
					JSONObject typeJson = orderTypeArray.getJSONObject(i);
					TimeSlotInfo typeInfo = new TimeSlotInfo();
					typeInfo.id = typeJson.getString("id");
					typeInfo.time = typeJson.getString("name");
					typeInfo.isBookable = true;
					Data.resTypeList.add(typeInfo);
				}
				
				JSONArray cityArray = jsonObject.getJSONArray("region_list");
				boolean hasDefaultCity = false;
				for (int i = 0; i < cityArray.length(); i++) {
					JSONObject cityJson = cityArray.getJSONObject(i);
					CityInfo cityInfo = new CityInfo();
					cityInfo.id = cityJson.getString("id");
					cityInfo.name = cityJson.getString("name");
					JSONArray areaArray = cityJson.getJSONArray("child");
					for (int j = 0; j < areaArray.length(); j++) {
						JSONObject areaJson = areaArray.getJSONObject(j);
						AreaInfo areaInfo = new AreaInfo();
						areaInfo.id = areaJson.getString("id");
						areaInfo.name = areaJson.getString("name");
						JSONArray regionArray = areaJson.getJSONArray("child");
						for (int k = 0; k < regionArray.length(); k++) {
							JSONObject regionJson = regionArray.getJSONObject(k);
							RegionInfo regionInfo = new RegionInfo();
							regionInfo.id = regionJson.getString("id");
							regionInfo.name = regionJson.getString("name");
							areaInfo.regionList.add(regionInfo);
						}
						cityInfo.areaList.add(areaInfo);
					}
					Data.cityList.add(cityInfo);
					
					if (cityInfo.id.equals(Preferences.GetString(context, "city_id"))) {
						hasDefaultCity = true;
					}
				}
				
				if (!hasDefaultCity) {
					Preferences.SetString(context, "city_id", Data.cityList.get(0).id);
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
		super.onPostExecute(result);

		if (handler != null) {
			message = new Message();
			message.what = result;
			handler.sendMessage(message);
		}
	}
}
