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

public class OrdeDetailTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;

	public OrdeDetailTask(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	@Override
	protected Integer doInBackground(String... params) {
		message = new Message();
		int flag = Constant.SUCCESS;

		try {
			HttpPost request = new HttpPost(UrlConstant.ORDER_DETAIL_URL);
			JSONObject param = new JSONObject();
			param.put("user_id", Data.memberInfo.id);
			param.put("mac", CommonFunction.getLocalMacAddress(context));
			param.put("order_id", Data.orderDetialInfo.id);

			request.setEntity(new StringEntity(param.toString()));
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(request);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				Data.orderDetialInfo.clearData();

				String retSrc = EntityUtils.toString(httpResponse.getEntity());

				JSONObject json = new JSONObject(retSrc);
				String content = json.getString("content");
				if (content.equals("[]")) {
					flag = Constant.NET_ERROR;
					Data.autoLogout(context, json.getBoolean("is_login"));
				} else {
					JSONObject jsonOrder = new JSONObject(content);
					Data.orderDetialInfo.id = jsonOrder.getString("order_id");
					Data.orderDetialInfo.orderID = jsonOrder.getString("order_sn");
					Data.orderDetialInfo.storeName = jsonOrder
							.getString("business_name");
					Data.orderDetialInfo.storeID = jsonOrder
							.getString("business_id");
					Data.orderDetialInfo.roomID = jsonOrder.getString("room_id");
					Data.orderDetialInfo.phoneNumber = jsonOrder
							.getString("mobile");
					Data.orderDetialInfo.price = jsonOrder.getString("room_amount");
					Data.orderDetialInfo.time = jsonOrder.getString("add_time");
					Data.orderDetialInfo.state = jsonOrder.getString("status");
					Data.orderDetialInfo.address = jsonOrder.getString("address");

					JSONArray subOrderArray = jsonOrder
							.getJSONArray("calendar_list");
					for (int i = 0; i < subOrderArray.length(); i++) {
						JSONObject jsonSubOrder = subOrderArray.getJSONObject(i);
						SubOrderInfo info = new SubOrderInfo();
						info.id = jsonSubOrder.getString("id");
						info.orderID = jsonSubOrder.getString("calendar_sn");
						info.state = jsonSubOrder.getString("status");
						info.info = jsonSubOrder.getString("time");
						info.price = jsonSubOrder.getString("price");
						JSONArray operationArray = jsonSubOrder
								.getJSONArray("operable_list");
						for (int j = 0; j < operationArray.length(); j++) {
							info.operations.add(operationArray.getInt(j));
						}
						Data.orderDetialInfo.subOrderList.add(info);
					}
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
