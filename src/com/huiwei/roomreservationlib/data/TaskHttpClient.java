package com.huiwei.roomreservationlib.data;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

public class TaskHttpClient {
    public HttpClient client;

    public TaskHttpClient() {
    	client = new DefaultHttpClient();
    	client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, Constant.CONNECTION_TIMEOUT);
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, Constant.SO_TIMEOUT);
    }
}