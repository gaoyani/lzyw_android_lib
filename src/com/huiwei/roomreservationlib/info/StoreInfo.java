package com.huiwei.roomreservationlib.info;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;

public class StoreInfo  {
	
	public static final int RECOMMEND = 0;
	public static final int AROUND = 1;
	public static final int FAVORITE = 2;

	public String iconUrl;
	public String id;
	public String name;
	public String cpp;
	public String address;
	public String distance;
	
	public float stars;
	public boolean favorite;
	public int classify;
	public double longitude;
	public double latitude;
	
	public String localPicPath = "";
	
	public void parseJson(JSONObject jsonObject) throws JSONException {
		id = jsonObject.getString("business_id");
		name = jsonObject.getString("name");
		iconUrl = jsonObject.getString("iconUrl");
		address = jsonObject.getString("address");
		distance = jsonObject.getString("distance");
		stars = Float.valueOf(jsonObject.getString("recommend"));
		cpp = jsonObject.getString("cpp");
		favorite = jsonObject.getBoolean("favorite");
	}
}
	
