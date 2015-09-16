package com.huiwei.roomreservationlib.task.order;

import java.lang.reflect.Member;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
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
import com.huiwei.roomreservationlib.info.CommentInfo;
import com.huiwei.roomreservationlib.info.OrderInfo;

public class OperationOrderTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	int operation;
	OrderInfo orderInfo;

	public OperationOrderTask(Context context, Handler handler, 
			OrderInfo orderInfo, int operation) {
		this.context = context;
		this.handler = handler;
		this.orderInfo = orderInfo;
		this.operation = operation;
	}

	@Override
	protected Integer doInBackground(String... params) {
		message = new Message();
		int flag = Constant.SUCCESS;
		
		try {
			HttpPost request = new HttpPost(UrlConstant.ORDER_OPERATION_URL);
			JSONObject param = new JSONObject();
			param.put("user_id", Data.memberInfo.id);
			param.put("mac", CommonFunction.getLocalMacAddress(context));
			param.put("order_id", orderInfo.id);
			param.put("operation_type", operation);
			request.setEntity(new StringEntity(param.toString()));
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(request);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonObject = new JSONObject(retSrc);
				int errorCode = jsonObject.getInt("error");
				if (errorCode != 0) {
					flag = Constant.DATA_ERROR;
					message.obj = jsonObject.getString("message");
					Data.autoLogout(context, jsonObject.getBoolean("is_login"));
				} else {
					JSONObject jsonContent = jsonObject.getJSONObject("content");
					orderInfo.state = jsonContent.getString("status");
					orderInfo.operations.clear();
					JSONArray operationArray = jsonContent
							.getJSONArray("operable_list");
					for (int j = 0; j < operationArray.length(); j++) {
						orderInfo.operations.add(operationArray.getInt(j));
					}
					message.obj = orderInfo;
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
