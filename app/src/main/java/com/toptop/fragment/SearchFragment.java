package com.toptop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;

import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.adapters.ListSearchAdapter;
import com.toptop.models.Video;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener {

	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";
	// Declare Variables
	ListView listView;
	ListSearchAdapter adapter;
	SearchView editsearch;
	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	public SearchFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment SearchFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static SearchFragment newInstance(String param1, String param2) {
		SearchFragment fragment = new SearchFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_search, container, false);
		// Generate sample data
		ArrayList<Video> videos = new ArrayList<Video>();
		videos.add(new Video("1", "Nguyễn Ngọc Trung", "Video xinh đẹp", R.drawable.avatar, new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime()));
		videos.add(new Video("2", "Nguyễn Ngọc Trung", "Video ca nhạc", R.drawable.avatar, new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime()));
		videos.add(new Video("3", "Nguyễn Ngọc Trung", "Video ca hát", R.drawable.avatar, new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime()));
		videos.add(new Video("4", "Nguyễn Ngọc Trung", "Video nhảy nhót", R.drawable.avatar, new GregorianCalendar(2014, Calendar.FEBRUARY, 11).getTime()));

		// Locate the ListView in listview_main.xml
		listView = view.findViewById(R.id.listsearch);


		// Pass results to ListViewAdapter Class
		adapter = new ListSearchAdapter(this.getContext(), videos);

		// Binds the Adapter to the ListView
		listView.setAdapter(adapter);

		editsearch = view.findViewById(R.id.search);
		editsearch.setOnQueryTextListener(this);

		// Set status bar color
		((MainActivity) requireActivity()).setStatusBarColor(MainActivity.STATUS_BAR_LIGHT_MODE);

		return view;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {

		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		String text = newText;
		adapter.filter(text);
		return false;
	}
}