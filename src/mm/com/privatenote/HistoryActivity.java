package mm.com.privatenote;

import java.util.ArrayList;
import java.util.List;

import zzw.mochuan.ticknote.R;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.CursorAdapter;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryActivity extends Activity {

	public static List<Long> note_ids = new ArrayList<Long>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_history);
	}

	public static class DetailsActivity extends Activity {

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				// If the screen is now in landscape mode, we can show the
				// dialog in-line with the list so we don't need this activity.
				// Toast.makeText(getApplicationContext(), "will return",
				// Toast.LENGTH_LONG).show();
				finish();
				return;
			}

			if (savedInstanceState == null) {
				// During initial setup, plug in the details fragment.
				DetailsFragment details = new DetailsFragment();
				details.setArguments(getIntent().getExtras());
				getFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
			}
		}
	}

	/**
	 * This is the "top-level" fragment, showing a list of items that the user
	 * can pick. Upon picking an item, it takes care of displaying the data to
	 * the user as appropriate based on the currrent UI layout.
	 */

	public static class TitlesFragment extends ListFragment {
		boolean mDualPane;
		int mCurCheckPosition = 0;

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			
//			//this.registerForContextMenu(view);

			// setListAdapter(new ArrayAdapter<String>(getActivity(),
			// android.R.layout.simple_list_item_activated_1,
			// Shakespeare.TITLES));
			Cursor c = DBAdapter.getInstance(getActivity()).getTop10Notes();
			if (c.moveToFirst()) {
				Log.w("HistoryActivity:", c.getString(2));
				c.moveToFirst();
			}
			CursorAdapter ca = new MyCursorAdapter(getActivity(),
					R.layout.note_list_item_layout, DBAdapter.getInstance(
							getActivity()).getTop10Notes());
			setListAdapter(ca);
//			this.registerForContextMenu(getListView());
//			getListView().setOnItemLongClickListener((new AdapterView.OnItemLongClickListener() {
//			
//				public void onItimClicked(){
//					
//				}
//					
//			
//			});

			View detailsFrame = getActivity().findViewById(R.id.details);
			mDualPane = detailsFrame != null
					&& detailsFrame.getVisibility() == View.VISIBLE;

			if (savedInstanceState != null) {
				// Restore last state for checked position.
				mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
			}

			if (mDualPane) {
				getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				showDetails(mCurCheckPosition);
			}
		}

		@Override
		public void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);
			outState.putInt("curChoice", mCurCheckPosition);
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			Log.w("ListItemClicked:", "id: " + v.getId());
			showDetails(position);
		}

		void showDetails(int index) {
			mCurCheckPosition = index;

			if (mDualPane) {
				getListView().setItemChecked(index, true);

				// Check what fragment is currently shown, replace if needed.
				DetailsFragment details_f = (DetailsFragment) getFragmentManager().findFragmentById(R.id.details);

				if (details_f == null) {
					details_f = DetailsFragment.newInstance(index);
					FragmentTransaction ft = getFragmentManager().beginTransaction();
					ft.add(R.id.details, details_f);
					ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
					ft.commit();

				} else if (details_f.getShownIndex() != index) {
					FragmentTransaction ft = getFragmentManager().beginTransaction();
					ft.remove(details_f);
					details_f = DetailsFragment.newInstance(index);
					ft.add(R.id.details, details_f);
					ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
					// ft.addToBackStack(null);
					ft.commit();
				}

			} else {
				Intent intent = new Intent();
				intent.setClass(getActivity(), DetailsActivity.class);
				intent.putExtra("index", index);
				startActivity(intent);
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		this.getMenuInflater().inflate(R.menu.history, menu);
		menu.setHeaderTitle("Choose a item");

	}

	public boolean onContextMenuItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_delete:
			Toast.makeText(this.getApplicationContext(), "delete", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menu_edite:
			Toast.makeText(getBaseContext(), "edit", Toast.LENGTH_SHORT).show();
			return true;
		}
		return super.onMenuItemSelected(R.menu.history, item);
	}

	public static class DetailsFragment extends Fragment {

		public static DetailsFragment newInstance(int index) {
			DetailsFragment f = new DetailsFragment();

			// Supply index input as an argument.
			Bundle args = new Bundle();
			args.putInt("index", index);
			f.setArguments(args);

			return f;
		}

		public int getShownIndex() {
			return getArguments().getInt("index", 0);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			super.onCreateView(inflater, container, savedInstanceState);
			// if(this.isInLayout()){
			// Toast.makeText(getActivity(), "details fragment in layout",
			// Toast.LENGTH_LONG).show();;
			// }
			// else
			// Toast.makeText(getActivity(), "details frag not in layout",
			// Toast.LENGTH_LONG).show();;
			if (container == null) {
				// We have different layouts, and in one of them this
				// fragment's containing frame doesn't exist. The fragment
				// may still be created from its saved state, but there is
				// no reason to try to create its view hierarchy because it
				// won't be displayed. Note this is not needed -- we could
				// just run the code below, where we would create and return
				// the view hierarchy; it would just never be used.
				return null;
			}

			ScrollView scroller = new ScrollView(getActivity());
			TextView text = new TextView(getActivity());
			int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getActivity().getResources()
					.getDisplayMetrics());
			text.setPadding(padding, padding, padding, padding);
			scroller.addView(text);

			Cursor c = DBAdapter.getInstance(getActivity()).queryById(note_ids.get(getShownIndex()));
			if (!c.moveToFirst()) {
				text.setText("Error.., lost you note.");
			}
			Log.w("HistoryActivity:", "conten column: " + c.getColumnIndex(DBAdapter.CONTENT));
			text.setText(c.getString(c.getColumnIndex(DBAdapter.CONTENT)));
			// text.setText(Shakespeare.DIALOGUE[getShownIndex()]);
			// text.setText("test text, note id:" +
			// note_ids.get(getShownIndex()));
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

			// else
			// Toast.makeText(getActivity(), "null", Toast.LENGTH_SHORT)
			// .show();

		}
	}

}
