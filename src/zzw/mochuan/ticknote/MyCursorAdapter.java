package zzw.mochuan.ticknote;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import zzw.mochuan.ticknote.R;
import android.content.Context;
import android.database.Cursor;
import android.widget.CursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyCursorAdapter extends CursorAdapter {
	Context context = null;
	int viewResId;

	public MyCursorAdapter(Context context, int resource, Cursor cursor) {
		super(context, cursor, false);
		viewResId = resource;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		Log.d("MyCursorAdapter", "MyCursorAdapter.newView() called");
		LayoutInflater inflater = null;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(viewResId, parent, false);
		
		return v;
	}

	// @Override
	// public View getView(int position, View convert, ViewGroup parent){
	// return null;
	// }

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Log.d("MyCursorAdapter", "bindView() called");
		TextView digest = (TextView) view.findViewById(R.id.note_item_text1);
		TextView dateTime = (TextView) view.findViewById(R.id.note_itme_text2);
		// Set the name
		digest.setText(cursor.getString(cursor.getColumnIndex(DBAdapter.DIGEST)));

		view.setTag(Long.valueOf(cursor.getLong(0)));

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss",
				Locale.CHINA);
		dateTime.setText(df.format(new Date(cursor.getLong(cursor
				.getColumnIndex(DBAdapter.UPDATE_DATE_TIME)))));
		// TitlesFragment.note_ids.add(cursor.getLong(0));
	}

}
