package zzw.mochuan.ticknote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import zzw.mochuan.util.MyThread;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class TitlesFragment extends ListFragment {

	ArrayList<Map<String, String>> note_list = new ArrayList<Map<String, String>>();
	ArrayAdapter<Map<String, String>> adapter;
	boolean mDualPane;
	int mCurCheckPosition = 0;
	Long curRecId = 0L;
	int limit_start = 0;
	final int list_len = 7;

	int visibleLastIndex = 0;

	ListView lv;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		new zzw.mochuan.util.MyThread.QueryTitlesThread().setArgs(getActivity(), init_delete_handler).start();

		// 为 listview 注册 context menu
		TitlesFragment.this.registerForContextMenu(this.getListView());

		lv = getListView();

		final View load_more = this.getActivity().getLayoutInflater().inflate(R.layout.note_load_more_view, null);
		load_more.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "todo: load more ..", Toast.LENGTH_LONG).show();
			}
		});

		// lv.addHeaderView(load_more);
		lv.addFooterView(load_more);

		lv.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				visibleLastIndex = firstVisibleItem + visibleItemCount - 1;

			}

			/*
			 * auto load more
			 * 
			 * @see
			 * android.widget.AbsListView.OnScrollListener#onScrollStateChanged
			 * (android.widget.AbsListView, int)
			 */
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				int itemsLastIndex = adapter.getCount() - 1; // 数据集最后一项的索引
				int lastIndex = itemsLastIndex + 1; // 加上底部的loadMoreView项
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
					// if (adapter.getCount() < list_len) {
					// TextView tv = (TextView)
					// load_more.findViewById(R.id.note_load_more_textview);
					// tv.setText("see previos notes");
					// lv.addHeaderView(load_more);
					// lv.removeFooterView(load_more);
					// return;
					// }
					limit_start += list_len;
					new zzw.mochuan.util.MyThread.QueryTitlesThread().setArgs(getActivity(), load_more_handler)
							.setLimitStart(limit_start).start();
					Log.w("LOADMORE", "loading...");
				}
			}

		});

		TextView text = new TextView(getActivity());
		text.setLineSpacing(0.5f, 1);
		text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
		int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getActivity().getResources()
				.getDisplayMetrics());
		text.setPadding(padding, padding, padding, padding);
		text.setGravity(Gravity.CENTER_VERTICAL);
		text.setText(" loading data  ..");
		lv.setEmptyView(text);

		View detailsFrame = getActivity().findViewById(R.id.details);
		mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

		if (savedInstanceState != null) {
			// Restore last state for checked position.
			mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
			this.curRecId = savedInstanceState.getLong("curRecId");

		}

		if (mDualPane) {
			if (curRecId != null) {
				getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				showDetails(mCurCheckPosition, curRecId);
			} else {
				Log.w("初始化时为横屏模式", "当前没有选项");
			}

		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("curChoice", mCurCheckPosition);
		outState.putLong("curRecId", curRecId);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.w("ListItemClicked:", "id: " + v.getId());
		showDetails(position, (Long) v.getTag());
	}

	void showDetails(int position, Long recId) {
		mCurCheckPosition = position;

		if (mDualPane) {
			getListView().setItemChecked(position, true);

			// Check what fragment is currently shown, replace if needed.
			DetailsFragment details_f = (DetailsFragment) getFragmentManager().findFragmentById(R.id.details);

			if (details_f == null) {
				details_f = DetailsFragment.newInstance(position, recId);
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.add(R.id.details, details_f);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();

			} else if (details_f.getShownIndex() != position) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.remove(details_f);
				details_f = DetailsFragment.newInstance(position, recId);
				ft.add(R.id.details, details_f);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				// ft.addToBackStack(null);
				ft.commit();
			}

		} else {
			Intent intent = new Intent();
			intent.setClass(getActivity(), DetailsActivity.class);
			intent.putExtra("index", position);
			intent.putExtra("recId", recId);
			startActivity(intent);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		this.getActivity().getMenuInflater().inflate(R.menu.history, menu);
		menu.setHeaderTitle("Choose a item");
		menu.add("view is list view ? " + (view.getId() == getListView().getId() ? "yes" : "no"));

	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo mi = (AdapterContextMenuInfo) item.getMenuInfo();
		Log.w("Context MENU:::: ", "context item seleted ID=" + mi.id + " position:" + mi.position);
		switch (item.getItemId()) {
		case R.id.menu_delete:
			Toast.makeText(this.getActivity(), "delete position:" + mi.position, Toast.LENGTH_SHORT).show();
			// View it = (View)lv.getItemAtPosition(mi.position);
			View it = lv.getChildAt(mi.position);

			long recordId = (Long) it.getTag();
			Log.w("View.tag:", Long.toString(recordId));

			new MyThread.UpdateThread()
					.setArgs(getActivity(), init_delete_handler, String.valueOf(recordId), null,
							ali.com.Constants.UPDATE_MODE.DELETE).setPosition(mi.position).start();

			return true;
		case R.id.menu_edite:
			// TODO:edit
			Toast.makeText(this.getActivity(), "edit tag:" + lv.getChildAt(mi.position).getTag(), Toast.LENGTH_SHORT)
					.show();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	Handler update_handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();

			String val = data.getString("dataStr");

			Log.i("handler in TitlesFragment", "请求结果-->" + val);

			note_list.clear();

			JSONArray ja = JsonData.parseJsonMulti(val);
			JSONObject jo = null;
			if (ja == null) {
				// TODO;
				Toast.makeText(getActivity(), "update, ja is null !!", Toast.LENGTH_LONG).show();
			}

			for (int i = 0; i < ja.length(); ++i) {
				try {
					jo = ja.getJSONObject(i);
					Map<String, String> item = new HashMap<String, String>(3);
					item.put("_id", jo.getString("_id"));
					item.put("digest", jo.getString("digest"));
					item.put("create_date_time", jo.getString("create_date_time"));

					note_list.add(item);

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			Log.w("TitlesFragment.update_handler:", "note_list.size:" + note_list.size());
			adapter.notifyDataSetChanged();

		}

	};

	Handler init_delete_handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();

			String init_delete = data.getString(ali.com.Constants.UPDATE_MODE.name);

			if (init_delete != null && init_delete.equals(String.valueOf(ali.com.Constants.UPDATE_MODE.DELETE))) {
				Log.w("delete handler:", "init_deleter:" + init_delete);
				int ret = data.getInt("ret");
				if (ret <= 0) {
					Toast.makeText(getActivity(), "update failed!!", Toast.LENGTH_SHORT).show();
					return;
				}

				int pos = data.getInt("position");

				Log.w("delete handler, pos", "pos:" + pos);

				if (pos >= 0) {
//					Log.w("list view size:", "" + lv.getCount());
//					Log.w("note_list.lenght:", "" + note_list.size());
//					Log.w("note_list pos:" + pos + " digest:", note_list.get(pos).get("digest"));
//					TextView ttt = (TextView)lv.getChildAt(pos).findViewById(R.id.note_item_text1);
//					Log.w("list view digest:", ttt.getText().toString());
					note_list.remove(pos);
//					Log.w("note_list.remove", "pos:" + pos);
//					Log.w("note_list.lenght:", "" + note_list.size());
					adapter.notifyDataSetChanged();
					// lv.removeViewAt(pos);
					lv.invalidate();
//					Log.w("list view size:", "" + lv.getCount());
					
//					TextView ttt1 = (TextView)lv.getChildAt(pos).findViewById(R.id.note_item_text1);
//					Toast.makeText(getActivity(), "list view digest:"+ttt1.getText().toString(), Toast.LENGTH_LONG).show();;
//					Log.w("note_list pos:" + pos + " digest:", note_list.get(pos).get("digest"));
					

				} else {

					new MyThread.QueryTitlesThread().setArgs(getActivity(), update_handler).start();

					Log.w("TitlesFragment.init_delete_handler ", "init_delete: " + init_delete + "   note_list.size:"
							+ note_list.size());

				}

				return;

			}

			String val = data.getString("dataStr");
			Log.i("handler in TitlesFragment", "请求结果:" + val);

			JSONArray ja = JsonData.parseJsonMulti(val);
			JSONObject jo = null;
			if (ja == null) {
				// TODO;
				Toast.makeText(getActivity(), "init , ja is null !!", Toast.LENGTH_LONG).show();

			}

			for (int i = 0; i < ja.length(); ++i) {
				try {
					jo = ja.getJSONObject(i);
					Map<String, String> item = new HashMap<String, String>(3);
					item.put("_id", jo.getString("_id"));
					item.put("digest", jo.getString("digest"));
					item.put("create_date_time", jo.getString("create_date_time"));
					item.put("update_date_time", jo.getString("update_date_time"));

					note_list.add(item);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			adapter = new MyArrayAdapter<Map<String, String>>(getActivity(), R.layout.note_list_item_layout, note_list);

			setListAdapter(adapter);

		}

	};

	private Handler load_more_handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();

			String val = data.getString("dataStr");
			if (val == null || val.equals("")) {
				Log.w("load_more_handler: ", "no more data ..");
				return;
			}
			Log.i("handler in TitlesFragment", "请求结果-->" + val);

			// note_list.clear();

			JSONArray ja = JsonData.parseJsonMulti(val);
			JSONObject jo = null;
			if (ja == null || ja.length() == 0) {
				// TODO;
				Log.w("load_more_handler", "update, ja is null or empty!!");
				Toast.makeText(getActivity(), "update, ja is null or empty!!", Toast.LENGTH_LONG).show();
				((TextView) (getActivity().findViewById(R.id.note_load_more_textview))).setText("no more data..");
				return;
			}

			for (int i = 0; i < ja.length(); ++i) {
				try {
					jo = ja.getJSONObject(i);
					Map<String, String> item = new HashMap<String, String>(3);
					item.put("_id", jo.getString("_id"));
					item.put("digest", jo.getString("digest"));
					item.put("create_date_time", jo.getString("create_date_time"));

					note_list.add(item);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			Log.w("TitlesFragment.update_handler:", "note_list.size:" + note_list.size());
			adapter.notifyDataSetChanged();

		}

	};

}
