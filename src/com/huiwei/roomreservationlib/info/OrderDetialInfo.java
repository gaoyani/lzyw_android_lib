package com.huiwei.roomreservationlib.info;

import java.util.ArrayList;
import java.util.List;

public class OrderDetialInfo extends OrderInfo {
	
	public String address;
	public List<SubOrderInfo> subOrderList = new ArrayList<SubOrderInfo>();
	
	public static class SubOrderInfo {
		public static final int SUB_ORDER_CANCEL = 1;
		public static final int SUB_ORDER_REORDER = 2;
		public static final int SUB_ORDER_DELETE = 3;
		
		public String id;
		public String orderID;
		public String info;
		public String price;
		public String state;

		public List<Integer> operations = new ArrayList<Integer>();
	}
	
	public void copy(OrderInfo orderInfo) {
		this.id = orderInfo.id;
		this.orderID = orderInfo.orderID;
		this.storeName = orderInfo.storeName;
		this.storeID = orderInfo.storeID;
		this.roomID = orderInfo.roomID;
		this.time = orderInfo.time;
		this.price = orderInfo.price;
		this.info = orderInfo.info;
		this.state = orderInfo.state;
		this.phoneNumber = orderInfo.phoneNumber;
		this.isCommented = orderInfo.isCommented;
		this.orderType = orderInfo.orderType;
		
		this.operations.clear();
		for (int i=0; i<orderInfo.operations.size(); i++) {
			this.operations.add(orderInfo.operations.get(i));
		}
	}
	
	public void clearData() {
		for (SubOrderInfo info : subOrderList) {
			info = null;
		}
		subOrderList.clear();
	}
	
	public void setSubOrderInfo(String id, String time, String state, List<Integer> operations) {
		for (SubOrderInfo subOrderInfo : subOrderList) {
			if (subOrderInfo.id.equals(id)) {
				subOrderInfo.info = time;
				subOrderInfo.state = state;
				subOrderInfo.operations = operations;
			}
		}
	}
	
	public boolean isNoPayment() {
		for (Integer operation : operations) {
			if (operation == ORDER_PAY)
				return true;
		}
		
		return false;
	}
}
	
