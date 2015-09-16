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
import com.huiwei.roomreservationlib.info.MemberInfo;

public class ModifyNormalInfoTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	MemberInfo memberInfo = null;
	int flag = 0;

	public ModifyNormalInfoTask(Context context, Handler handler, MemberInfo memberInfo) {
		this.context = context;
		this.handler = handler;
		this.memberInfo = memberInfo;
	}

	@Override
	protected Integer doInBackground(String... params) {
		message = new Message();
		flag = Constant.SUCCESS;
		try {
			HttpPost request = new HttpPost(UrlConstant.MODIFY_NORMAL_INFO_URL);
			MultipartEntity reqEntity = new MultipartEntity();
			JSONObject param = new JSONObject();
			param.put("user_id", Data.memberInfo.id);
			param.put("mac", CommonFunction.getLocalMacAddress(context));
			param.put("passwd_question", memberInfo.safeQuestion);
			param.put("passwd_answer", memberInfo.safeAnswer);
			param.put("email", memberInfo.email);
			param.put("birthday", memberInfo.birthday);
			param.put("invoice_title", memberInfo.billTitile);
			param.put("invoice_address", memberInfo.address);
			param.put("card_id_type", memberInfo.idType);
			param.put("card_id", memberInfo.identifyNum);
			
			reqEntity.addPart("params", 
					new StringBody(param.toString(), 
					"text/plain", Charset.forName("utf-8")));
			
			if (memberInfo.identifyImg != null && memberInfo.identifyImg.length() != 0) {
				File file = new File(memberInfo.identifyImg);
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
