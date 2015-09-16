package com.huiwei.roomreservationlib.task.member;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
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

public class RegisterTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	private String phoneNum = null;
	private String passWrod = null;
	private String authCode = null;

	String isStr = "";

	int flag = 0;

	public RegisterTask(Context context, Handler handler, String phoneNum,
			String password, String authCode) {
		this.context = context;
		this.handler = handler;
		this.phoneNum = phoneNum;
		this.passWrod = password;
		this.authCode = authCode;
	}

	@Override
	protected Integer doInBackground(String... params) {
		String url = UrlConstant.REGISTER_URL;
		message = new Message();
		try {
			HttpPost request = new HttpPost(url);
			JSONObject param = new JSONObject();
			param.put("mobile_phone", phoneNum);
			param.put("password", MD5.md5s(passWrod));
			param.put("mac", CommonFunction.getLocalMacAddress(context));
			param.put("code", authCode);

			request.setEntity(new StringEntity(param.toString()));
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(request);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonObject = new JSONObject(retSrc);
				int errorCode = jsonObject.getInt("error");
				if (errorCode == 0) {
					JSONObject jsonContent = jsonObject.getJSONObject("user_info");
					Data.memberInfo.parseJson(jsonContent);
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
