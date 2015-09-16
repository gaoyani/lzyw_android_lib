package com.huiwei.roomreservationlib.info;

import java.util.ArrayList;
import java.util.List;

public class OrderInfo  {
	
	public static final int ORDER_DELETE = 1;
	public static final int ORDER_COMMENT = 2;
	public static final int ORDER_REORDER = 3;
	public static final int ORDER_CANCEL = 4;
	public static final int ORDER_CONFIRM = 5;
	public static final int ORDER_PAY = 6;

	public String id;
	public String orderID;
	public String storeName;
	public String storeID;
	public String roomID;
	public String time;
	public String price;
	public String info;
	public String state;
	public String phoneNumber;
	public int orderType;
	public boolean isCommented;
	public List<Integer> operations = new ArrayList<Integer>();
}
	
