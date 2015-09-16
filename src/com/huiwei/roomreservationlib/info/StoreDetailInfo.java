package com.huiwei.roomreservationlib.info;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;

public class StoreDetailInfo extends StoreInfo {
	public static int TYPE_ROOM = 0;
	public static int TYPE_SERVICE = 1;
	public static int TYPE_ARTIFICER = 2;
	
	public String service;
	public String recommend;
	public String phoneNumber;
	public String time;
	public String picture360Url;
	public String storeTips;
	public String roomNum;
	public String serviceNum;
	public int favoriteNum;
	public boolean isOrdered;
	public boolean isCommented;
	public boolean isComplainted;

	public String consumeTitle;
	public String consumeValue;
	
	public List<String> picUrlList = new ArrayList<String>();
	public List<NewsInfo> newsList = new ArrayList<NewsInfo>();
	
	public List<RoomInfo> roomList = new ArrayList<RoomInfo>();
	public List<ServiceInfo> serviceList = new ArrayList<ServiceInfo>();
	public List<ArtificerInfo> artificerList = new ArrayList<ArtificerInfo>();

	public void clearData() { 
		picUrlList.clear();
		picture360Url = null;
		for (RoomInfo info : roomList) {
			info = null;
		}
		roomList.clear();
		
		for (ServiceInfo info : serviceList) {
			info = null;
		}
		serviceList.clear();
		
		for (ArtificerInfo info : artificerList) {
			info = null;
		}
		artificerList.clear();
		
		for (NewsInfo info : newsList) {
			info = null;
		}
		newsList.clear();
	}
}
	
