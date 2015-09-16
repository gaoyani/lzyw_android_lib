package com.huiwei.roomreservationlib.data;

import com.huiwei.roomreservationlib.info.OrderDetialInfo.SubOrderInfo;
import com.huiwei.roomreservationlib.info.OrderInfo;

public class OrderManager {

	public static void clearOrderList() {
		for (OrderInfo info : Data.orderList) {
			info = null;
		}
		
		Data.orderList.clear();
	}
	
	public static void operationOrder(OrderInfo orderInfo) {
		for (int i=0; i<Data.orderList.size(); i++) {
			OrderInfo info = Data.orderList.get(i);
			if (info.id.equals(orderInfo.id)) {
				if (orderInfo.operations.size() == 0) {
					Data.orderList.remove(info);
				} else {
					Data.orderList.set(i, orderInfo);
				}
			}
		}
	}
	
	public static void commentOrder(String id) {
		for (int i=0; i<Data.orderList.size(); i++) {
			OrderInfo info = Data.orderList.get(i);
			if (info.id.equals(id)) {
				info.isCommented = true;
			}
		}
	}
	
	public static void operationSubOrder(SubOrderInfo orderInfo) {
		for (int i=0; i<Data.orderDetialInfo.subOrderList.size(); i++) {
			SubOrderInfo info = Data.orderDetialInfo.subOrderList.get(i);
			if (info.id.equals(orderInfo.id)) {
				if (orderInfo.operations.size() == 0) {
					Data.orderDetialInfo.subOrderList.remove(info);
				} else {
					Data.orderDetialInfo.subOrderList.set(i, orderInfo);
				}
			}
		}
	}
}
