package com.huiwei.roomreservationlib.task;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.TaskHttpClient;
import com.huiwei.roomreservationlib.data.Data;
import com.huiwei.roomreservationlib.data.UrlConstant;
import com.huiwei.roomreservationlib.info.CommentInfo;

public class CommentListTask extends AsyncTask<String, Void, Integer> {

	Context context = null;
	Handler handler = null;
	Message message = null;
	int commentType = CommentInfo.COMMENT_STORE;
	List<CommentInfo> infoList = null;

	public CommentListTask(Context context, Handler handler, int commentType,
			List<CommentInfo> infoList) {
		this.context = context;
		this.handler = handler;
		this.commentType = commentType;
		this.infoList = infoList;
	}

	@Override
	protected Integer doInBackground(String... params) {
		message = new Message();
		int flag = Constant.SUCCESS;
		String url = UrlConstant.COMMENT_LIST_URL;
		switch (commentType) {
		case CommentInfo.COMMENT_STORE:
		case CommentInfo.COMMENT_ORDER:
			url += "business_id/" + Data.storeDetailInfo.id + "/p/"
					+ infoList.size();
			break;

		case CommentInfo.COMMENT_ROOM:
			url += "room_id/" + Data.roomInfo.id + "/p/" + infoList.size();
			break;
			
		case CommentInfo.COMMENT_SERVICE:
			url += "service_id/" + Data.serviceInfo.id + "/p/" + infoList.size();
			break;

		case CommentInfo.COMMENT_NEWS:
			url += "news_id/" + Data.newsInfo.id + "/p/" + infoList.size();
			break;

		default:
			break;
		}

		try {
			HttpGet httpGet = new HttpGet(url);
			TaskHttpClient taskClient = new TaskHttpClient();
			HttpResponse httpResponse = taskClient.client.execute(httpGet);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				JSONArray jsonArray = new JSONArray(retSrc);
				if (jsonArray.length() != 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonComment = jsonArray.getJSONObject(i);
						CommentInfo commentInfo = new CommentInfo();
						commentInfo.comment = jsonComment.getString("content");
						commentInfo.name = jsonComment.getString("nickname");
						commentInfo.time = jsonComment.getString("add_time");
						commentInfo.stars = Float.valueOf(jsonComment.getString("rank"));
						infoList.add(commentInfo);
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
