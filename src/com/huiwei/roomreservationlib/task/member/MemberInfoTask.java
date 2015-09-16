package com.huiwei.roomreservationlib.task.member;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
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
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public class MemberInfoTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;

	int flag = 0;

	public MemberInfoTask(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	@Override
	protected Integer doInBackground(String... params) {
		flag = Constant.SUCCESS;
		message = new Message();
		
		try {
			HttpPost request = new HttpPost(UrlConstant.MEMBER_INFO_URL);
			JSONObject param = new JSONObject();
			param.put("user_id", Data.memberInfo.id);
			param.put("mac", CommonFunction.getLocalMacAddress(context));

			request.setEntity(new StringEntity(param.toString()));
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(request);

			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				JSONObject json = new JSONObject(retSrc);
				String content = json.getString("content");
				if (content.equals("[]")) {
					flag = Constant.NET_ERROR;
					Data.memberInfo.isAutoLogout = 
							(Data.memberInfo.isLogin && !json.getBoolean("is_login"));
					Data.autoLogout(context, json.getBoolean("is_login"));
				} else {
					JSONObject jsonObject = new JSONObject(content);
					Data.memberInfo.parseJson(jsonObject);
					Data.memberInfo.cardList.clear();
					Data.addMemberCard();
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
