package mm.com.privatenote;
//package zzw.mochuan.privatenote;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
//import zzw.mochuan.ticknote.R;
//import android.app.Activity;
//import android.app.Fragment;
//import android.content.Intent;
//import android.database.Cursor;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//public class MainActivity extends Activity {
//
//	public static DBAdapter db;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
//		if (savedInstanceState == null) {
//			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
//		}
//
//		db = DBAdapter.getInstance(this);
//		db.open();
//
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//
//	/**
//	 * A placeholder fragment containing a simple view.
//	 */
//	public static class PlaceholderFragment extends Fragment {
//
//		public PlaceholderFragment() {
//			Log.d("FFFFFF", "construct");
//		}
//
//		@Override
//		public void onCreate(Bundle sa) {
//			super.onCreate(sa);
//		}
//
//		@Override
//		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//			Button btn = (Button) rootView.findViewById(R.id.button1);
//			btn.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//
//					EditText ed = (EditText) PlaceholderFragment.this.getView().findViewById(R.id.editText1);
//					String content = ed.getText().toString();
//					if (content.length() <= 0) {
//						Toast.makeText(getActivity(), "content can't be empty", Toast.LENGTH_SHORT).show();
//						;
//						Intent it = new Intent(getActivity(), HistoryActivity.class);
//						getActivity().startActivity(it);
//						return;
//					}
//
//					String digest = content.substring(0, 10 < content.length() ? 10 : content.length());
//
//					long rowid = MainActivity.db.insertNote(digest, content);
//
//					Intent it = new Intent(getActivity(), HistoryActivity.class);
//					getActivity().startActivity(it);
//
//					// Cursor cursor = db.getTop10Notes();
//					// if (cursor.moveToFirst()) {
//					// do {
//					// DisplayNote(cursor);
//					// } while (cursor.moveToNext());
//					// }
//					//
//					// Toast.makeText(getActivity(), "inserted : " + rowid,
//					// Toast.LENGTH_LONG).show();
//
//				}
//			});
//
//			return rootView;
//		}
//
//		protected void DisplayNote(Cursor cursor) {
//			Toast.makeText(
//					getActivity(),
//					"id:" + cursor.getInt(0) + " digest:" + cursor.getString(1) + " content:" + cursor.getString(2)
//							+ "  create_datetime:" + cursor.getLong(3) + " update:" + cursor.getLong(4),
//					Toast.LENGTH_SHORT).show();
//
//			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.CHINA);
//
//			Log.d("DBAdapter:",
//					"id:" + cursor.getInt(0) + " digest:" + cursor.getString(1) + " content:" + cursor.getString(2)
//							+ "  create_datetime:" + df.format(new Date(cursor.getLong(3))) + " update:"
//							+ df.format(new Date(cursor.getLong(4))));
//		}
//
//		@Override
//		public void onActivityCreated(Bundle savedInstance) {
//			super.onActivityCreated(savedInstance);
//
//		}
//
//	}
//
//}
