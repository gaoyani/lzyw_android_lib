package com.huiwei.roomreservationlib.task.pay;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.huiwei.commonlib.CommonFunction;
import com.huiwei.commonlib.MD5;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.TaskHttpClient;
import com.huiwei.roomreservationlib.data.UrlConstant;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public class ConfirmRechargeOrderTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	int flag = 0;
	String price;
	int payType;
	String orderID;

	public ConfirmRechargeOrderTask(Context context, Handler handler, 
			String orderID, int payType, String price) {
		this.context = context;
		this.handler = handler;
		this.orderID = orderID;
		this.payType = payType;
		this.price = price;
	}

	@Override
	protected Integer doInBackground(String... params) {
		flag = Constant.SUCCESS;
		message = new Message();
		try {
			HttpPost request = new HttpPost(UrlConstant.CONFIRM_ORDER_URL);
			JSONObject param = new JSONObject();
			param.put("pay_id", payType);
			param.put("order_sn", orderID);
			param.put("price", price);
			param.put("user_id", Data.memberInfo.id);
			param.put("mac", CommonFunction.getLocalMacAddress(context));
			request.setEntity(new StringEntity(param.toString()));
			
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(request);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonObject = new JSONObject(retSrc);
				int errorCode = jsonObject.getInt("error");
				if (errorCode == 0) {
					String content = "";
					JSONArray contentArray = jsonObject.getJSONArray("content");
					for (int i = 0; i < contentArray.length(); i++) {
						content += contentArray.getString(i);
					}
					
					message.obj = content.replaceAll("\'", "\"");
				} else {
					flag = Constant.DATA_ERROR;
					message.obj = jsonObject.getString("message");
					Data.autoLogout(context, jsonObject.getBoolean("is_login"));
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
