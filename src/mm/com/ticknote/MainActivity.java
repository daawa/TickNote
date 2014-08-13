package mm.com.ticknote;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import mm.com.util.MyThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import zzw.mochuan.ticknote.R;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Fragment;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static DBAdapter db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}

		db = DBAdapter.getInstance(this);
		db.open();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		static Handler handler;

		public PlaceholderFragment() {
			Log.d("FFFFFF", "construct");
		}

		@Override
		public void onCreate(Bundle sa) {
			super.onCreate(sa);
			handler = new MyHandler();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			Button btn = (Button) rootView.findViewById(R.id.button_save);
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					EditText ed = (EditText) PlaceholderFragment.this.getView().findViewById(R.id.editText_content);
					String content = ed.getText().toString();

					if (content.length() <= 0) {
						Intent it = new Intent(getActivity(), HistoryActivity.class);
						getActivity().startActivity(it);
						return;
					}

					String digest = content.substring(0, 10 < content.length() ? 10 : content.length());

					Map<String, String> map = new HashMap<String, String>();
					map.put("digest", digest);
					map.put("content", content);

					new MyThread.UpdateThread().setArgs(getActivity(), handler, null, map,
							mm.com.Constants.UPDATE_MODE.CREATE).start();

					Log.w("MYThread.UpdateThrad. create..", "");

					Intent it = new Intent(getActivity(), HistoryActivity.class);
					getActivity().startActivity(it);

				}
			});

			return rootView;
		}

		protected void DisplayNote(Cursor cursor) {
			Toast.makeText(
					getActivity(),
					"id:" + cursor.getInt(0) + " digest:" + cursor.getString(1) + " content:" + cursor.getString(2)
							+ "  create_datetime:" + cursor.getLong(3) + " update:" + cursor.getLong(4),
					Toast.LENGTH_SHORT).show();

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.CHINA);

			Log.d("DBAdapter:",
					"id:" + cursor.getInt(0) + " digest:" + cursor.getString(1) + " content:" + cursor.getString(2)
							+ "  create_datetime:" + df.format(new Date(cursor.getLong(3))) + " update:"
							+ df.format(new Date(cursor.getLong(4))));
		}

		@Override
		public void onActivityCreated(Bundle savedInstance) {
			super.onActivityCreated(savedInstance);

		}

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Looper.prepare();

				String resstr = JsonData.connServerForResult(JsonData.queryUrl, null);
				JSONObject jb = JsonData.parseJson(resstr);

				if (jb != null) {
					try {
						JSONArray ja = jb.getJSONArray("records");
						String content = ja.getJSONObject(0).getString("content");

						Toast.makeText(getActivity(), "content can't be empty, json:" + content, Toast.LENGTH_SHORT)
								.show();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					Toast.makeText(getActivity(), "content can't be empty, json is null !!", Toast.LENGTH_SHORT).show();
				}

				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("value", "请求结果");
				msg.setData(data);
				handler.sendMessage(msg);

				Looper.loop();
			}

		};
	}

	public static class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String val = data.getString("value");
			Log.i("MainActivity:", "请求结果-->" + val);
		}
	}

}
