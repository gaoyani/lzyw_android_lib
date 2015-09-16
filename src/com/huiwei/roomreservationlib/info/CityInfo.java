package com.huiwei.roomreservationlib.info;

import java.util.ArrayList;
import java.util.List;

public class CityInfo  {

	public String id;
	public String name;
	public List<AreaInfo> areaList = new ArrayList<AreaInfo>();
	
	public static class AreaInfo {
		public String id;
		public String name;
		public List<RegionInfo> regionList = new ArrayList<RegionInfo>();
	}
	
	public static class RegionInfo {
		public String id;
		public String name;
	}
}
	
