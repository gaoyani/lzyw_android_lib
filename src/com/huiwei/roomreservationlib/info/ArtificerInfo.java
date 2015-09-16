package com.huiwei.roomreservationlib.info;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;

public class ArtificerInfo  {
	public String iconUrl;
	public String id;
	public String name;
	public String nameTitle;
	public String priceType;
	public String price;
	public String type;
	public String otherTitle;
	public String otherInfo;
	public String feature;
	public String recommend;
	public String privilege;
	
	public String consumeTitle;
	public String consumeValue;
	public int hot;
	public boolean bookable;
	public boolean isOrdered;
	public boolean isCommented;
	public boolean isComplainted;
	
	public String picture360Url;
	public List<String> picUrlList = new ArrayList<String>();
	
	public List<TimeSlotInfo> todayTimeList = new ArrayList<TimeSlotInfo>();
	
	public List<CommentInfo> commentList = new ArrayList<CommentInfo>();
	
	public void clearData() {
		for (CommentInfo info : commentList) {
			info = null;
		}
		commentList.clear();
		
		for (TimeSlotInfo info : todayTimeList) {
			info = null;
		}
		todayTimeList.clear();
		picUrlList.clear();
	}
}
	
