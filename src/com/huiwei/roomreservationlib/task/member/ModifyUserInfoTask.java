package com.huiwei.roomreservationlib.task.member;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.huiwei.commonlib.CommonFunction;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.TaskHttpClient;
import com.huiwei.roomreservationlib.data.UrlConstant;

public class ModifyUserInfoTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	String userName = null;
	String nickname = null;
	String realName = null;
	String imgPath = null;
	int sex;
	int flag = 0;

	public ModifyUserInfoTask(Context context, Handler handler, String userName, 
			String nickname, String realName, String imgPath, int sex) {
		this.context = context;
		this.handler = handler;
		this.userName = userName;
		this.nickname = nickname;
		this.realName = realName;
		this.imgPath = imgPath;
		this.sex = sex;
	}

	@Override
	protected Integer doInBackground(String... params) {
		message = new Message();
		flag = Constant.SUCCESS;
		String url = UrlConstant.MODIFY_USER_INFO_URL;
		try {
			HttpPost request = new HttpPost(url);
			MultipartEntity reqEntity = new MultipartEntity();
			JSONObject param = new JSONObject();
			param.put("user_id", Data.memberInfo.id);
			param.put("mac", CommonFunction.getLocalMacAddress(context));
			param.put("user_name", userName);
			param.put("nickname", nickname);
			param.put("name", realName);
			param.put("sex", sex);
			
			reqEntity.addPart("params", 
					new StringBody(param.toString(), 
					"text/plain", Charset.forName("utf-8")));
			
			if (imgPath != null && imgPath.length() != 0) {
				File file = new File(imgPath);
				reqEntity.addPart("image", new FileBody(file));
			}

			request.setEntity(reqEntity);
			
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
