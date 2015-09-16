package com.huiwei.roomreservationlib.task;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.huiwei.commonlib.CommonFunction;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.TaskHttpClient;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.UrlConstant;
import com.huiwei.roomreservationlib.info.CommentInfo;

public class SubmitCommentTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	
	int commentType;
	CommentInfo info;

	public SubmitCommentTask(Context context, Handler handler, 
			CommentInfo info, int commentType) {
		this.context = context;
		this.handler = handler;
		this.info = info;
		this.commentType = commentType;
	}

	@Override
	protected Integer doInBackground(String... params) {
		message = new Message();
		int flag = Constant.SUCCESS;
		String url = UrlConstant.SUBMIT_COMMENT_URL;
		
		try {
			HttpPost httpPost = new HttpPost(url);
			JSONObject param = new JSONObject();
			param.put("user_id", Data.memberInfo.id);
			param.put("mac", CommonFunction.getLocalMacAddress(context));
			param.put("rank", info.stars);
			param.put("content", info.comment);
			switch (commentType) {
			case CommentInfo.COMMENT_STORE:
				param.put("business_id", Data.storeDetailInfo.id);
				break;
				
			case CommentInfo.COMMENT_ROOM:
				param.put("business_id", Data.storeDetailInfo.id);
				param.put("room_id", Data.roomInfo.id);
				break;
				
			case CommentInfo.COMMENT_SERVICE:
				param.put("business_id", Data.storeDetailInfo.id);
				param.put("sid", Data.serviceInfo.id);
				break;
				
			case CommentInfo.COMMENT_NEWS:
				param.put("news_id", info.newsID);
				break;
				
			case CommentInfo.COMMENT_ORDER:
				param.put("business_id", info.storeID);
				param.put("order_id", info.orderID);
				break;

			default:
				break;
			}
			
			httpPost.setEntity(new StringEntity(param.toString(), HTTP.UTF_8));
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(httpPost);
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
					message.obj = info;
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
