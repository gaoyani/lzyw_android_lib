package com.huiwei.roomreservationlib.task.member;

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
import com.huiwei.roomreservationlib.info.OrderInfo;
import com.huiwei.roomreservationlib.info.ScoreInfo;

public class ScoreListTask extends
		AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;

	public ScoreListTask(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	@Override
	protected Integer doInBackground(String... params) {
		message = new Message();
		int flag = Constant.SUCCESS;
		
		try {
			HttpPost request = new HttpPost(UrlConstant.SCORE_LIST_URL);
			JSONObject param = new JSONObject();
			param.put("user_id", Data.memberInfo.id);
			param.put("mac", CommonFunction.getLocalMacAddress(context));
			param.put("p", Data.scoreList.size());
			request.setEntity(new StringEntity(param.toString()));
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(request);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				JSONArray jsonArray = new JSONArray(retSrc);
				if (jsonArray.length() != 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonScore = jsonArray.getJSONObject(i);
						ScoreInfo scoreInfo = new ScoreInfo();
						scoreInfo.name = jsonScore.getString("name");
						scoreInfo.price = jsonScore.getString("pay_points");
						scoreInfo.time = jsonScore.getString("time");
						scoreInfo.type = jsonScore.getString("type");
						Data.scoreList.add(scoreInfo);
					}
				} else {
					flag = Constant.DATA_LOAD_COMPLETE;
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
