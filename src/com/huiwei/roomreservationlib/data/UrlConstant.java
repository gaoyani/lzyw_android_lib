package com.huiwei.roomreservationlib.data;

public class UrlConstant {
	public static String getNetworkUrl(String gateway, String mac) {
		return "http://"+gateway+":81/baimd/api.php?mac="+mac;
	}
			
	public static String YL_CREATE_ORDER_URL = "http://third.mbpay.cn:8080/outpay/payment/createOrder";
	public static String YL_QUERY_ORDER_URL = "http://third.mbpay.cn:8080/outpay/payment/queryOrder?";
	
//	public static String URL_DOMAIN_NAME = "http://182.92.158.59/api.php";     //lezi
  public static String URL_DOMAIN_NAME = "http://125.35.14.247:8000/lezi/api.php";  //huiwei
//    public static String URL_DOMAIN_NAME = "http://125.35.14.247/lezi/api.php";  //huiwei
	
	public static String MAIN_PAGE_URL =  URL_DOMAIN_NAME + "/index/index/";
	public static String MAIN_PUBLIC_URL =  URL_DOMAIN_NAME + "/index/app_public/";
	public static String NEWS_DETAIL_URL =  URL_DOMAIN_NAME + "/index/news_info/";
	public static String GET_AGREEMENT_URL =  URL_DOMAIN_NAME + "/index/get_agreement/";
	
	public static String STORE_LIST_URL = URL_DOMAIN_NAME + "/Business/business_list/";
	public static String AROUND_STORE_LIST_URL = URL_DOMAIN_NAME + "/Business/business_ambitus/";
	public static String FAVORITE_STORE_LIST_URL = URL_DOMAIN_NAME + "/Business/business_collect/";
	public static String STORE_DETAIL_URL = URL_DOMAIN_NAME + "/Business/business_info/";
	public static String ROOM_DETAIL_URL = URL_DOMAIN_NAME + "/Business/room_info/";
	public static String SERVICE_DETAIL_URL = URL_DOMAIN_NAME + "/Business/service_info/";
	public static String MAP_STORE_LIST_URL = URL_DOMAIN_NAME + "/Business/business_map/";
	public static String COLLECT_STORE_URL = URL_DOMAIN_NAME + "/Business/collect_business/";
	
	public static String CREATE_ORDER_URL = URL_DOMAIN_NAME + "/pay/insert/";
	public static String DEL_ORDER_URL = URL_DOMAIN_NAME + "/pay/del/";
	public static String CONFIRM_ORDER_URL = URL_DOMAIN_NAME + "/pay/update/";
	
	public static String LOGIN_URL = URL_DOMAIN_NAME+"/user/login/";
	public static String REGISTER_URL = URL_DOMAIN_NAME+"/user/register/";
	public static String LOGOUT_URL = URL_DOMAIN_NAME+"/user/login_out/";
	public static String MEMBER_INFO_URL = URL_DOMAIN_NAME+"/user/user_info/";
	public static String RESET_PASSWORD_URL = URL_DOMAIN_NAME+"/user/reset_rassword/";
	public static String SET_PAY_PASSWORD_URL = URL_DOMAIN_NAME+"/user/reset_payment_password/";
	
	public static String AUTH_CODE_URL = URL_DOMAIN_NAME+"/user/getcode/";  //login 
	public static String EDIT_AUTH_CODE_URL = URL_DOMAIN_NAME+"/user/geteditcode/"; //edit info
	public static String RESET_PASSWORD_AUTH_CODE_URL = URL_DOMAIN_NAME+"/user/getpasswordcode/"; //reset password
	public static String SET_PAY_PASSWORD_AUTH_CODE_URL = URL_DOMAIN_NAME+"/user/get_payment_code/"; //set or reset pay password
	
	public static String MODIFY_PHONE_NUMBER_URL = URL_DOMAIN_NAME+"/user/edit_phone/";
	public static String MODIFY_PASSWORD_URL = URL_DOMAIN_NAME+"/user/edit_password/";
	public static String MODIFY_USER_INFO_URL = URL_DOMAIN_NAME+"/user/edit_user/";
	public static String MODIFY_NORMAL_INFO_URL = URL_DOMAIN_NAME+"/user/edit_user_use/";
	
	public static String ORDER_LIST_URL = URL_DOMAIN_NAME+"/user/order_list/";
	public static String ORDER_DETAIL_URL = URL_DOMAIN_NAME+"/user/order_info/";
	public static String MEMBER_COMMENT_LIST_URL = URL_DOMAIN_NAME+"/user/user_comment/";
	public static String SCORE_LIST_URL = URL_DOMAIN_NAME+"/user/user_pay_points/";
	public static String ORDER_OPERATION_URL = URL_DOMAIN_NAME+"/user/order_operable/";
	
	public static String TIME_SLOT_URL = URL_DOMAIN_NAME+"/order/get_calendar/";
	public static String ADD_ROOM_ORDER_URL = URL_DOMAIN_NAME+"/order/order_insert/";
	public static String ADD_SERVICE_ORDER_URL = URL_DOMAIN_NAME+"/order/service_insert/";
	public static String PAYMENT_URL = URL_DOMAIN_NAME+"/order/set_pay_status/";
	public static String RESERVATION_DETAIL_URL = URL_DOMAIN_NAME+"/order/index/";
	public static String SUB_ORDER_MODIFY_URL = URL_DOMAIN_NAME+"/order/calendar_edit/";
	public static String SUB_ORDER_OPERATION_URL = URL_DOMAIN_NAME+"/order/calendar_operable/";
	
	public static String GET_QR_URL = URL_DOMAIN_NAME+"/qrcode/index/";
	public static String UPLOAD_RECOMMEND_URL = URL_DOMAIN_NAME+"/qrcode/recommend/";
	
	public static String SUBMIT_COMMENT_URL = URL_DOMAIN_NAME+"/comment/comment_insert";
	public static String SUBMIT_COMPLAINT_URL = URL_DOMAIN_NAME+"/comment/complain_insert";
	public static String COMMENT_LIST_URL = URL_DOMAIN_NAME+"/comment/comment_list/";
	
	public static String SHARE_LEZI = URL_DOMAIN_NAME+"/share/lezi/";
	public static String SHARE_ROOM = URL_DOMAIN_NAME+"/share/room/id/";
	public static String SHARE_STORE = URL_DOMAIN_NAME+"/share/business/id/";
	public static String SHARE_ORDER = URL_DOMAIN_NAME+"/share/order/id/";
	public static String SHARE_SERVICE = URL_DOMAIN_NAME+"/share/service/id/";
	
	public static String GET_PAY_RESULT_URL = URL_DOMAIN_NAME+"/respond/verify_pay/";
	
	public static String SERVER_VERSION_URL = URL_DOMAIN_NAME+"/app";
	public static String getAPVersionUrl(String gateway) {
		return "http://" + gateway + ":8080/leziapp/api.php";
	}
	
	public static String CHECK_SSID_URL = URL_DOMAIN_NAME + "/app/gateway/";
}
