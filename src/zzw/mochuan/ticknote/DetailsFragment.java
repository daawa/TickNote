package zzw.mochuan.ticknote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsFragment extends Fragment {

	TextView text;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String str = data.getString("dataStr");
			Log.i("handler in DetailsFragment:", "请求结果 str: " + str);

			try {
				JSONObject jo = new JSONObject(str);
				text.setText(jo.getString("content"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				text.setText("exception:" + e.getLocalizedMessage());
			}

		}

	};

	public static DetailsFragment newInstance(int position, Long recId) {
		DetailsFragment f = new DetailsFragment();

		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putInt("index", position);
		args.putLong("recId", recId);
		f.setArguments(args);

		return f;
	}

	public int getShownIndex() {
		return getArguments().getInt("index", 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		if (container == null) {
			return null;
		}

		ScrollView scroller = new ScrollView(getActivity());
		text = new TextView(getActivity());
		text.setLineSpacing(0.5f, 1);
		text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
		int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getActivity().getResources()
				.getDisplayMetrics());
		text.setPadding(padding, padding, padding, padding);
		text.setGravity(Gravity.CENTER_VERTICAL);

		scroller.addView(text);

		zzw.mochuan.util.MyThread.QueryDetailThread mt = new zzw.mochuan.util.MyThread.QueryDetailThread();
		mt.setArgs(getActivity(), handler, getArguments().getLong("recId"));
		mt.start();

		return scroller;
	}

	@Override
	public void onResume() {
		super.onResume();
		int index = this.getArguments().getInt("index");

		TitlesFragment tt = (TitlesFragment) getFragmentManager().findFragmentById(R.id.titles);

		if (tt != null) {
			tt.getListView().setItemChecked(index, true);
		}

	}

}