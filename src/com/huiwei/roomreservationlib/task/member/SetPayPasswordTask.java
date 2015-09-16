package com.huiwei.roomreservationlib.task.member;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.huiwei.commonlib.CommonFunction;
import com.huiwei.commonlib.MD5;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.TaskHttpClient;
import com.huiwei.roomreservationlib.data.UrlConstant;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public class SetPayPasswordTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	private String phoneNum = null;
	private String passWrod = null;
	private String safeAnswer = null;

	String isStr = "";

	int flag = 0;

	public SetPayPasswordTask(Context context, Handler handler, String phoneNum,
			String password, String safeAnswer) {
		this.context = context;
		this.handler = handler;
		this.phoneNum = phoneNum;
		this.passWrod = password;
		this.safeAnswer = safeAnswer;
	}

	@Override
	protected Integer doInBackground(String... params) {
		message = new Message();
		try {
			HttpPost request = new HttpPost(UrlConstant.SET_PAY_PASSWORD_URL);
			JSONObject param = new JSONObject();
			param.put("user_id", Data.memberInfo.id);
			param.put("mac", CommonFunction.getLocalMacAddress(context));
			param.put("mobile_phone", phoneNum);
			param.put("password", MD5.md5s(passWrod));
			param.put("passwd_answer", safeAnswer);

			request.setEntity(new StringEntity(param.toString(), HTTP.UTF_8));
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(request);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonObject = new JSONObject(retSrc);
				int errorCode = jsonObject.getInt("error");
				if (errorCode != 0) {
					message.obj = jsonObject.getString("message");
					flag = Constant.DATA_ERROR;
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
	
	public String getLocalMacAddress() { 
        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE); 
        WifiInfo info = wifi.getConnectionInfo(); 
        return info.getMacAddress();
   }  

	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		message.what = result;
		handler.sendMessage(message);
	}
}
