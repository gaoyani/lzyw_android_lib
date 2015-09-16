package com.huiwei.roomreservationlib.data;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.provider.Contacts.ContactMethods;
import android.widget.Toast;

import com.huiwei.roomreservationlib.info.CityInfo;
import com.huiwei.roomreservationlib.info.CommentInfo;
import com.huiwei.roomreservationlib.info.MemberInfo;
import com.huiwei.roomreservationlib.info.NewsInfo;
import com.huiwei.roomreservationlib.info.OrderInfo;
import com.huiwei.roomreservationlib.info.RecommendInfo;
import com.huiwei.roomreservationlib.info.RoomSizeInfo;
import com.huiwei.roomreservationlib.info.OrderDetialInfo.SubOrderInfo;
import com.huiwei.roomreservationlib.info.RecommendInfo.RecommendItemInfo;
import com.huiwei.roomreservationlib.info.CardInfo;
import com.huiwei.roomreservationlib.info.OrderDetialInfo;
import com.huiwei.roomreservationlib.info.RoomInfo;
import com.huiwei.roomreservationlib.info.ScoreInfo;
import com.huiwei.roomreservationlib.info.SearchInfo;
import com.huiwei.roomreservationlib.info.ServiceInfo;
import com.huiwei.roomreservationlib.info.StoreDetailInfo;
import com.huiwei.roomreservationlib.info.StoreInfo;
import com.huiwei.roomreservationlib.info.StoreSceneInfo;
import com.huiwei.roomreservationlib.info.TimeSlotInfo;
import com.huiwei.roomreservationlib.task.order.OperationOrderTask;

public class Data {

	public static String payId = "m_outerPay";
	public static String payMD5 = "hrTu89DDrplqLMhvCFRt91dfBGhNW39j";
	
	public static List<StoreSceneInfo> storeSceneList = new ArrayList<StoreSceneInfo>();
	
	//Main Page
	public static RecommendInfo recommendInfo = new RecommendInfo();
	
	//Main Public
	public static int cppMax;
	public static int cppMin;
	public static List<RoomSizeInfo> roomSizeList = new ArrayList<RoomSizeInfo>();
	public static List<CityInfo> cityList = new ArrayList<CityInfo>();
	public static List<TimeSlotInfo> resTypeList = new ArrayList<TimeSlotInfo>();
	public static List<TimeSlotInfo> complaintReasonList = new ArrayList<TimeSlotInfo>();
	
	public static StoreDetailInfo storeDetailInfo = new StoreDetailInfo();
	public static RoomInfo roomInfo = new RoomInfo();
	public static ServiceInfo serviceInfo = new ServiceInfo();
	public static SearchInfo searchInfo = new SearchInfo(); 
	public static OrderDetialInfo orderDetialInfo = new OrderDetialInfo();
	public static NewsInfo newsInfo = new NewsInfo();
	
	public static List<StoreInfo> storeList = new ArrayList<StoreInfo>();
	public static List<StoreInfo> aroundStoreList = new ArrayList<StoreInfo>();
	public static List<StoreInfo> favoriteStoreList = new ArrayList<StoreInfo>();
	
	public static MemberInfo memberInfo = new MemberInfo();
	public static List<OrderInfo> orderList = new ArrayList<OrderInfo>();
	public static List<ScoreInfo> scoreList = new ArrayList<ScoreInfo>();
	public static List<CommentInfo> commentList = new ArrayList<CommentInfo>();
	
	public static AlertDialog autoLogoutDialog;
	
	public static void collectStore(String id, boolean isCollect) {
		for (StoreInfo info : storeList) {
			if (info.id.equals(id)) {
				info.favorite = isCollect;
				break;
			}
		}
		
		for (StoreInfo info : aroundStoreList) {
			if (info.id.equals(id)) {
				info.favorite = isCollect;
				break;
			}
		}
		
		recommendInfo.collectRecommendStore(id, isCollect);
	}
	
	public static CityInfo findCityInfo(String id) {
		if (cityList.size() == 0)
			return null;
		
		for (int i=0; i<cityList.size(); i++) {
			CityInfo info = cityList.get(i);
			if (info.id.equals(id)) {
				return info;
			}
		}
		
		return cityList.get(0);
	}
	
	public static void clearStoreSceneList() {
		for (StoreSceneInfo info : storeSceneList) {
			info = null;
		}
		
		storeSceneList.clear();
	}
	
	public static void clearStoreList() {
		for (StoreInfo info : storeList) {
			info = null;
		}
		
		storeList.clear();
	}
	
	public static void clearAroundStoreList() {
		for (StoreInfo info : aroundStoreList) {
			info = null;
		}
		
		aroundStoreList.clear();
	}
	
	public static void clearFavoriteStoreList() {
		for (StoreInfo info : favoriteStoreList) {
			info = null;
		}
		
		favoriteStoreList.clear();
	}
	
	
	public static void clearScoreList() {
		for (ScoreInfo info : scoreList) {
			info = null;
		}
		
		scoreList.clear();
	}
	
	public static void clearCommentList() {
		for (CommentInfo info : commentList) {
			info = null;
		}
		
		commentList.clear();
	}

	public static CommentInfo findCommentInfo(String orderID) {
		for (CommentInfo info : commentList) {
			if (info.orderID.equals(orderID))
				return info;
		}
		
		return null;
	}
	
	public static void autoLogout(final Context context, boolean isLogin) {
		if (!Data.memberInfo.isLogin || isLogin) {
			return;
		}

		Handler handler = new Handler(context.getMainLooper()) {
			@Override
			public void handleMessage(android.os.Message msg) {
				super.handleMessage(msg);
				if (autoLogoutDialog != null) {
					autoLogoutDialog.dismiss();
					autoLogoutDialog = null;
				}
				
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("安全提示");
				builder.setMessage("您的帐号在另一个设备登录，您被迫下线，如果这不是您本人操作，那么您的密码可能已经泄露，请联系我们修改密码。");
				builder.setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						 Data.memberInfo = null;
						 Data.memberInfo = new MemberInfo();
						 Data.memberInfo.isLogin = false;
					}
				});
				autoLogoutDialog = builder.create();
				autoLogoutDialog.show();
			};
		};

		handler.sendEmptyMessage(0);
	}
	
	//for test
	public static void addMemberCard() {
		
				CardInfo cardInfo1 = new CardInfo();
				cardInfo1.cardBG = "5a0084";
				cardInfo1.cardName = "权品E卡";
				cardInfo1.cardNameColor = "ffffff";
				cardInfo1.cardNumber = "No.1463546514314";
				cardInfo1.cardNumberColor = "ffffff";
			    cardInfo1.storeName = "权品机构";
			    cardInfo1.storeNameColor = "000000";
				Data.memberInfo.cardList.add(cardInfo1);
				
				CardInfo cardInfo2 = new CardInfo();
				cardInfo2.cardBG = "00775b";
				cardInfo2.cardName = "会员卡";
				cardInfo2.cardNameColor = "ffffff";
				cardInfo2.cardNumber = "No.442112442323";
				cardInfo2.cardNumberColor = "ffffff";
				cardInfo2.storeName = "上岛咖啡";
				cardInfo2.storeNameColor = "000000";
				Data.memberInfo.cardList.add(cardInfo2);
				
				CardInfo cardInfo3 = new CardInfo();
				cardInfo3.cardBG = "854400";
				cardInfo3.cardName = "贵宾卡";
				cardInfo3.cardNameColor = "ffffff";
				cardInfo3.cardNumber = "No.43544623032355578545";
				cardInfo3.cardNumberColor = "ffffff";
				cardInfo3.storeName = "陆琴脚艺";
				cardInfo3.storeNameColor = "000000";
				Data.memberInfo.cardList.add(cardInfo3);
	}
}
