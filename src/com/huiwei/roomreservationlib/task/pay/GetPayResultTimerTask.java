package com.huiwei.roomreservationlib.task.pay;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

public class GetPayResultTimerTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	String orderID;
	boolean isRecharge;
	Timer timer;

	public GetPayResultTimerTask(Context context, Handler handler, String orderID, boolean isRecharge) {
		this.context = context;
		this.handler = handler;
		this.orderID = orderID;
		this.isRecharge = isRecharge;
	}

	@Override
	protected Integer doInBackground(String... params) {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					GetPayResultTask at = new GetPayResultTask(context, handler, orderID, isRecharge);
					at.execute();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 0, 1000*1);
		
		return null;
	}

	public void stopTimer() {
		if (timer != null) {
			timer.cancel();
		}
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
	}

}
