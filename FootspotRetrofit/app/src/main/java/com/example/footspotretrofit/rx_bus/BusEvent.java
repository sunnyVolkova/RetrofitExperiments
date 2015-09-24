package com.example.footspotretrofit.rx_bus;

public class BusEvent {
	public static final int EVENT_TYPE_UNKNOWN = 0;
	public static final int EVENT_TYPE_LOGIN = 1;
	public static final int EVENT_TYPE_USERINFO = 2;
	public static final int EVENT_TYPE_FOLLOWERS = 3;

	public static final int ERROR_TYPE_UNKNOWN = 0;

	public int eventType;
	public boolean success;
	public int errorType;

	public BusEvent(int eventType, boolean success, int errorType){
		this.eventType = eventType;
		this.success = success;
		this.errorType = errorType;
	}
}
