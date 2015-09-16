package com.huiwei.roomreservationlib.info;

public class CommentInfo  {
	
	public static final int COMMENT_STORE = 0;
	public static final int COMMENT_ROOM = 1;
	public static final int COMMENT_NEWS = 2;
	public static final int COMMENT_ORDER = 3;
	public static final int COMMENT_SERVICE = 4;

	public String orderID;
	public String storeID;
	public String newsID;
	public String name;
	public String time;
	public float stars;
	public String comment;
	
	public boolean isComment;
	public boolean isStartComment;
	public String info;
}
	
