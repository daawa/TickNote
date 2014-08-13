package zzw.mochuan.util;

import java.util.Map;

import zzw.mochuan.ticknote.JsonData;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class MyThread {

	public static class UpdateThread extends Thread {
		private Context context;
		private Handler handler;
		private Map<String, String> map;
		private String recordId;
		private int position = -1;
		private int flag = -1;

		@Override
		public void run() {

			int ret = JsonData.update(map, recordId, flag);
			if (flag != -1) {
				Bundle b = new Bundle();
				b.putString(ali.com.Constants.UPDATE_MODE.name, String.valueOf(flag));
				b.putInt("ret", ret);
				b.putInt("position", position);
				Log.w("update thread,position:", "" + position);
				Message msg = Message.obtain();
				msg.setData(b);
				if (null != handler)
					handler.sendMessage(msg);
			}
			// TODO: handler .. according to flag

		}

		public UpdateThread setArgs(Context c, Handler h, String id, Map<String, String> m, int flag) {
			this.context = c;
			this.handler = h;
			this.recordId = id;
			this.map = m;
			this.flag = flag;

			return this;
		}
		
		public UpdateThread setPosition(int pos){
			this.position = pos;
			return this;
		}

	}

	public static class QueryDetailThread extends Thread {
		private Handler handler;
		private String recordId;
		private Context context;

		@Override
		public void run() {

			Looper.prepare();

			// Toast.makeText(context,
			// "now requesting data from network.. args: id : " + recordId,
			// Toast.LENGTH_LONG)
			// .show();

			String str = JsonData.connServerForResult(JsonData.queryUrl, recordId);

			Bundle bundle = new Bundle();
			bundle.putString("dataStr", str);
			Message msg = handler.obtainMessage();
			msg.setData(bundle);
			handler.sendMessage(msg);

			Looper.loop();
		}

		public Thread setArgs(Context c, Handler h, long id) {
			this.context = c;
			this.handler = h;
			this.recordId = String.valueOf(id);

			return this;
		}

	}

	public static class QueryTitlesThread extends Thread {

		private Handler handler;
		private Context context;
		private int limit_start = -1;

		@Override
		public void run() {

			Looper.prepare();

			String dataStr = new JsonData().getDataResourceStr(limit_start);
			Bundle bundle = new Bundle();
			bundle.putString("dataStr", dataStr);
			Message msg = handler.obtainMessage();
			msg.setData(bundle);
			handler.sendMessage(msg);

			Looper.loop();
		}

		public QueryTitlesThread setArgs(Context c, Handler h) {
			this.context = c;
			this.handler = h;

			return this;
		}
		
		public QueryTitlesThread setLimitStart(int start){
			this.limit_start = start;
			return this;
		}

	}

}
