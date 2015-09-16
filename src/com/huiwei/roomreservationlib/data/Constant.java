package com.huiwei.roomreservationlib.data;

import com.huiwei.commonlib.FileManager;

public class Constant {
	public static final int SUCCESS = 0;
	public static final int NET_ERROR = 1;
	public static final int DATA_ERROR = 2;
	public static final int OTHER_ERROR = 3;
	public static final int ALREADY_LOGIN = 4;
	
	
	public static final int DATA_LOAD_COMPLETE = 10;
	public static final int AUTO_LOGOUT = 11;
	
	public static final int CONNECTION_TIMEOUT = 10000;
	public static final int SO_TIMEOUT = 10000;
	
	public static final String MAIN_PAGE_DATA_FILE_NAME = "mainPage.json";
	public static final String MAIN_PAGE_PIC_DIR = "/roomreservation/";
	
	public static final String getMainPageJsonFile() {
		return FileManager.getSDPath()+MAIN_PAGE_PIC_DIR+MAIN_PAGE_DATA_FILE_NAME;
	}
}
