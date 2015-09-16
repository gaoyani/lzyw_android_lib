package com.huiwei.roomreservationlib.info;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.huiwei.commonlib.FileManager;
import com.huiwei.roomreservationlib.data.Constant;
import com.huiwei.roomreservationlib.data.Data;

public class RecommendInfo  {

	public List<NewsInfo> newsList = new ArrayList<NewsInfo>();
	public RecommendItemInfo leftItemInfo = new RecommendItemInfo();
	public RecommendItemInfo rightTopItemInfo = new RecommendItemInfo();
	public RecommendItemInfo rightBottomItemInfo = new RecommendItemInfo();
	public List<StoreInfo> recommendStoreList = new ArrayList<StoreInfo>();
	
	public static class RecommendItemInfo {
		public static final int RECOMMEND_STORE = 1;
		public static final int RECOMMEND_NEWS = 2;
		public static final int RECOMMEND_WEB = 3;
		
		public String picUrl;
		public String title;
		public String price;
		public int type;
		public String content;
		
		public String localPicPath = "";
	}
	
	public void parseJson(String retSrc, boolean isLocalData) throws JSONException, IOException {
		JSONObject jsonObject = new JSONObject(retSrc);
		JSONArray newsArray = jsonObject.getJSONArray("news_list");
		for (int i = 0; i < newsArray.length(); i++) {
			JSONObject newsJson = newsArray.getJSONObject(i);
			NewsInfo newsInfo = new NewsInfo();
			newsInfo.id = newsJson.getString("id");
			newsInfo.name = newsJson.getString("title");
			newsList.add(newsInfo);
		}
		
		JSONObject leftJson = jsonObject.getJSONObject("pic1");
		leftItemInfo.title = leftJson.getString("name");
		leftItemInfo.price = leftJson.getString("price");
		leftItemInfo.picUrl = leftJson.getString("pic");
		leftItemInfo.type = Integer.valueOf(leftJson.getString("type"));
		leftItemInfo.content = leftJson.getString("content");
		
		JSONObject rightTopJson = jsonObject.getJSONObject("pic2");
		rightTopItemInfo.title = rightTopJson.getString("name");
		rightTopItemInfo.price = rightTopJson.getString("price");
		rightTopItemInfo.picUrl = rightTopJson.getString("pic");
		rightTopItemInfo.type = Integer.valueOf(rightTopJson.getString("type"));
		rightTopItemInfo.content = rightTopJson.getString("content");
		
		JSONObject rightbottomJson = jsonObject.getJSONObject("pic3");
		rightBottomItemInfo.title = rightbottomJson.getString("name");
		rightBottomItemInfo.price = rightbottomJson.getString("price");
		rightBottomItemInfo.picUrl = rightbottomJson.getString("pic");
		rightBottomItemInfo.type = Integer.valueOf(rightbottomJson.getString("type"));
		rightBottomItemInfo.content = rightbottomJson.getString("content");
		
		if (!isLocalData) {
			leftItemInfo.localPicPath = FileManager.getSDPath()+
					Constant.MAIN_PAGE_PIC_DIR+"pic1.jpg";
			leftJson.put("pic", leftItemInfo.localPicPath);
			rightTopItemInfo.localPicPath = FileManager.getSDPath()+
					Constant.MAIN_PAGE_PIC_DIR+"pic2.jpg";
			rightTopJson.put("pic", rightTopItemInfo.localPicPath);
			rightBottomItemInfo.localPicPath = FileManager.getSDPath()+
					Constant.MAIN_PAGE_PIC_DIR+"pic3.jpg";
			rightbottomJson.put("pic", rightBottomItemInfo.localPicPath);
		}
		
		JSONArray storeArray = jsonObject.getJSONArray("business_list");
		for (int i = 0; i < storeArray.length(); i++) {
			JSONObject storeJson = storeArray.getJSONObject(i);
			StoreInfo storeInfo = new StoreInfo();
			storeInfo.parseJson(storeJson);
			if (!isLocalData) {
				storeInfo.localPicPath = FileManager.getSDPath()+
						Constant.MAIN_PAGE_PIC_DIR+"icon"+i+".jpg";
				storeJson.put("iconUrl", storeInfo.localPicPath);
			}
			
			recommendStoreList.add(storeInfo);
		}
		
		if (!isLocalData) {
			FileManager.writeTxtFile(Constant.MAIN_PAGE_PIC_DIR, 
					Constant.MAIN_PAGE_DATA_FILE_NAME, jsonObject.toString());
		}
	}
	
	public void collectRecommendStore(String id, boolean isCollect) {
		for (StoreInfo info : recommendStoreList) {
			if (info.id.equals(id)) {
				info.favorite = isCollect;
				break;
			}
		}
	}

	public void clearData() {
		newsList.clear();
		for (StoreInfo info : recommendStoreList) {
			info = null;
		}
		
		recommendStoreList.clear();
	}
}
	
