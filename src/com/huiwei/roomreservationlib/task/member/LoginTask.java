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
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public class LoginTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	private String userName = null;
	private String passWrod = null;
	String isStr = "";
	boolean isLogin;

	int flag = 0;

	public LoginTask(Context context, Handler handler, String username,
			String password, boolean isLogin) {
		this.context = context;
		this.handler = handler;
		this.userName = username;
		this.passWrod = password;
		this.isLogin = isLogin;
	}

	@Override
	protected Integer doInBackground(String... params) {
		flag = Constant.SUCCESS;
		message = new Message();
		String url = UrlConstant.LOGIN_URL;
		try {
			HttpPost request = new HttpPost(url);
			JSONObject param = new JSONObject();
			param.put("user_name", userName);
			param.put("password", MD5.md5s(passWrod));
			param.put("mac", CommonFunction.getLocalMacAddress(context));
			param.put("login", isLogin);

			request.setEntity(new StringEntity(param.toString(), HTTP.UTF_8));
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(request);

			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
				JSONObject jsonObject = new JSONObject(retSrc);
				int errorCode = jsonObject.getInt("error");
				if (errorCode == 0) {
					JSONObject jsonContent = jsonObject.getJSONObject("user_info");
					Data.memberInfo.parseJson(jsonContent);
				} else if (errorCode == 2) {
					flag = Constant.ALREADY_LOGIN;
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
