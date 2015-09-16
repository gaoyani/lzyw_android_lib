package com.huiwei.roomreservationlib.task.order;

import java.lang.reflect.Member;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.R.integer;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.huiwei.commonlib.CommonFunction;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.TaskHttpClient;
import com.huiwei.roomreservationlib.data.UrlConstant;
import com.huiwei.roomreservationlib.info.OrderDetialInfo;
import com.huiwei.roomreservationlib.info.OrderDetialInfo.SubOrderInfo;
import com.huiwei.roomreservationlib.info.OrderInfo;
import com.huiwei.roomreservationlib.info.ReservationInfo;

public class ReservationDetailTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	String subOrderID = null;
	ReservationInfo reservationInfo;

	public ReservationDetailTask(Context context, Handler handler, String subOrderID, 
			ReservationInfo reservationInfo) {
		this.context = context;
		this.handler = handler;
		this.subOrderID = subOrderID;
		this.reservationInfo = reservationInfo;
	}

	@Override
	protected Integer doInBackground(String... params) {
		message = new Message();
		int flag = Constant.SUCCESS;

		try {
			HttpPost request = new HttpPost(UrlConstant.RESERVATION_DETAIL_URL);
			JSONObject param = new JSONObject();
			param.put("user_id", Data.memberInfo.id);
			param.put("mac", CommonFunction.getLocalMacAddress(context));
			param.put("order_id", Data.orderDetialInfo.id);
			param.put("calendar_id", subOrderID);

			request.setEntity(new StringEntity(param.toString()));
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(request);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				JSONObject json = new JSONObject(retSrc);
				int errorCode = json.getInt("error");
				if (errorCode == 0) {
					JSONObject jsonObject = json.getJSONObject("content");
					reservationInfo.storeName = jsonObject.getString("storeName");
					reservationInfo.storeIconUrl = jsonObject.getString("storeIconUrl");
					reservationInfo.priceType = jsonObject.getString("price_name");
					reservationInfo.price = jsonObject.getString("price");
					reservationInfo.storePhone = jsonObject.getString("storePhone");
					reservationInfo.roomName = jsonObject.getString("roomName");
					reservationInfo.storeTips = jsonObject.getString("storeTips");
					reservationInfo.orderID = jsonObject.getString("orderID");
					reservationInfo.contacts = jsonObject.getString("contacts");
					reservationInfo.phoneNum = jsonObject.getString("phoneNum");
					reservationInfo.type = jsonObject.getString("type");
					reservationInfo.otherInfo = jsonObject.getString("otherInfo");
					reservationInfo.time = jsonObject.getString("time");
					reservationInfo.roomInfo = jsonObject.getString("roomInfo");
					JSONArray timeArray = jsonObject.getJSONArray("timeList");
					for (int i = 0; i < timeArray.length(); i++) {
						reservationInfo.timeList.add(timeArray.getString(i));
					}
					message.obj = reservationInfo;
				} else {
					message.obj = json.getString("message");
					flag = Constant.DATA_ERROR;
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
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (handler != null) {
			message.what = result;
			handler.sendMessage(message);
		}
	}
}
