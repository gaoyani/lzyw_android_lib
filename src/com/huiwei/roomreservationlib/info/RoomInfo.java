package com.huiwei.roomreservationlib.info;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;

public class RoomInfo extends ServiceBaseInfo {
	
	public String roomClassify;
	
	public String feature;
	public String recommend;
	public String privilegeID;
	public String privilegeTitle;
	
	public List<TimeSlotInfo> todayTimeList = new ArrayList<TimeSlotInfo>();
	
	public void clearData() {
		for (TimeSlotInfo info : todayTimeList) {
			info = null;
		}
		todayTimeList.clear();
		
		super.clearData();
	}
}
	
