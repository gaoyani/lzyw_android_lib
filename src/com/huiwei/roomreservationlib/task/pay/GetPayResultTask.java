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

import com.huiwei.commonlib.MD5;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.TaskHttpClient;
import com.huiwei.roomreservationlib.data.UrlConstant;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public class GetPayResultTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	int flag = 0;
	String orderID;
	boolean isRecharge;

	public GetPayResultTask(Context context, Handler handler, 
			String orderID, boolean isRecharge) {
		this.context = context;
		this.handler = handler;
		this.orderID = orderID;
		this.isRecharge = isRecharge;
	}

	@Override
	protected Integer doInBackground(String... params) {
		flag = Constant.SUCCESS;
		message = new Message();
		String url = UrlConstant.GET_PAY_RESULT_URL+"/order_sn/"+orderID
				+"/order_type/"+ (isRecharge ? 1 : 0);
		
		try {
			HttpGet httpGet = new HttpGet(url);
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(httpGet);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonObject = new JSONObject(retSrc);
				int errorCode = jsonObject.getInt("error");
				if (errorCode != 0) {
					flag = Constant.DATA_ERROR;
					message.obj = jsonObject.getString("message");
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
