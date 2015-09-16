package com.huiwei.roomreservationlib.info;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;

import com.huiwei.roomreservationlib.data.Data;

public class MemberInfo  {
	public static final int MALE = 1;
	public static final int FAMALE  = 2;
	
	public static final int IDENT_ID_CARD = 1;
	public static final int IDENT_PASSPORT = 2;
	public static final int IDENT_OFFICER_CARD = 3;
	public static final int IDENT_OTHER = 4;
	
	public static final int PASSWORD_LOGIN = 1;
	public static final int PASSWORD_PAY  = 2;
	
	public String id = "";
	
	public String nickName;
	public String realName;
	public String userName;
	public String uidName;
	public String phoneNum;
	public int sex;
	public String birthday;
	public String email;
	public String billTitile;
	public String address;
	public int idType;
	public String identifyNum;
	public String identifyImg;
	
	public boolean isLogin = false;
	public boolean isAutoLogout = false;
	public boolean isSetPayPassword;
	public double longitude;
	public double latitude;
	public String imgUrl;
	public String curLevel;
	public int curLevelPoints;
	public String nextLevel;
	public int nextLevelPoints;
	public int curPoints;
	public String promotionTips;
	public String account;
	public String grades;
	public String mouthBook;
	public String mouthConsume;
	public String mouthGrades;
	public String recommend;
	public String totalBook;
	public String totleConsume;
	
	public String safeQuestion;
	public String safeAnswer;
	
	public List<CardInfo> cardList = new ArrayList<CardInfo>();
	
	public String getName() {
		if (nickName != null && nickName.length() != 0) {
			return nickName;
		} else if (userName != null && userName.length() != 0){
			return userName;
		} else {
			return uidName;
		}
	}
	
	public void parseJson(JSONObject jsonContent) throws JSONException {
		Data.memberInfo.id = jsonContent.getString("user_id");
		Data.memberInfo.userName = jsonContent.getString("user_name");
		Data.memberInfo.nickName = jsonContent.getString("nickname");
		Data.memberInfo.uidName = jsonContent.getString("uid");
		Data.memberInfo.realName = jsonContent.getString("name");
		Data.memberInfo.sex = jsonContent.getInt("sex");
		Data.memberInfo.birthday = jsonContent.getString("birthday");
		Data.memberInfo.phoneNum = jsonContent.getString("mobile_phone");
		Data.memberInfo.imgUrl = jsonContent.getString("head_pic");
		Data.memberInfo.account = jsonContent.getString("user_money");
		Data.memberInfo.grades = jsonContent.getString("pay_points");
		Data.memberInfo.curLevel = jsonContent.getString("rank_name");
		Data.memberInfo.nextLevel = jsonContent.getString("next_rank_name");
		Data.memberInfo.curLevelPoints = Integer.valueOf(jsonContent.getString("min_points"));
		Data.memberInfo.nextLevel = jsonContent.getString("next_rank_name");
		Data.memberInfo.nextLevelPoints = Integer.valueOf(jsonContent.getString("next_min_points"));
		Data.memberInfo.curPoints = Integer.valueOf(jsonContent.getString("rank_points"));
		Data.memberInfo.promotionTips = jsonContent.getString("rank_tab");
		Data.memberInfo.mouthBook = jsonContent.getString("month_order");
		Data.memberInfo.mouthConsume = jsonContent.getString("month_money");
		Data.memberInfo.mouthGrades = jsonContent.getString("month_points");
		Data.memberInfo.recommend = jsonContent.getString("nocomment");
		Data.memberInfo.totalBook = jsonContent.getString("order_count");
		Data.memberInfo.totleConsume = jsonContent.getString("order_money");
		
		Data.memberInfo.safeQuestion = jsonContent.getString("passwd_question");
		Data.memberInfo.safeAnswer = jsonContent.getString("passwd_answer");
		Data.memberInfo.email = jsonContent.getString("email");
		Data.memberInfo.billTitile = jsonContent.getString("invoice_title");
		Data.memberInfo.address = jsonContent.getString("invoice_address");
		Data.memberInfo.idType = Integer.valueOf(jsonContent.getString("card_id_type"));
		Data.memberInfo.identifyNum = jsonContent.getString("card_id");
		Data.memberInfo.identifyImg = jsonContent.getString("card_id_pic");
		
		Data.memberInfo.isSetPayPassword = jsonContent.getBoolean("is_set_payment_password");

		Data.memberInfo.isLogin = true;
	}
}
	
