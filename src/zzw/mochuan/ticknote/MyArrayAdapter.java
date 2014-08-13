package zzw.mochuan.ticknote;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyArrayAdapter<T> extends ArrayAdapter<T> {
	private int resource;

	public MyArrayAdapter(Context context, int resource, List<T> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.resource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (convertView == null) {
			LayoutInflater inf = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// parent must be null
			RelativeLayout rl = (RelativeLayout) inf.inflate(resource, null);
			v = rl;
		}

		Map<String, String> map = (Map<String, String>) this.getItem(position);
		Long _id = Long.valueOf(map.get("_id"));

		TextView digestV = (TextView) v.findViewById(R.id.note_item_text1);
		TextView dateV = (TextView) v.findViewById(R.id.note_itme_text2);

		v.setTag(_id);
		digestV.setText(map.get("digest"));
		dateV.setText(map.get("create_date_time"));

		return v;

	}

}
