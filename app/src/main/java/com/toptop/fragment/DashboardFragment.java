package com.toptop.fragment;

import static com.toptop.models.Statistic.ALL_TIME;
import static com.toptop.models.Statistic.MONTH;
import static com.toptop.models.Statistic.NEW_COMMENTS;
import static com.toptop.models.Statistic.NEW_LIKES;
import static com.toptop.models.Statistic.NEW_REPORTS;
import static com.toptop.models.Statistic.NEW_USERS;
import static com.toptop.models.Statistic.NEW_VIDEOS;
import static com.toptop.models.Statistic.NEW_VIEWS;
import static com.toptop.models.Statistic.YEAR;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.davidmiguel.multistateswitch.MultiStateSwitch;
import com.toptop.R;
import com.toptop.adapters.DashboardItemAdapter;
import com.toptop.models.Statistic;
import com.toptop.utils.firebase.StatisticFirebase;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
	// Tag
	private static final String TAG = "DashboardFragment";

	private DashboardFragment() {
	}

	private static final DashboardFragment instance = new DashboardFragment();

	public static DashboardFragment getInstance() {
		return instance;
	}

	ProgressBar progressBar;
	RecyclerView recyclerView;
	MultiStateSwitch multiStateSwitch;
	Cartesian cartesian;
	AnyChartView anyChartView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

		anyChartView = view.findViewById(R.id.any_chart_view);
		progressBar = view.findViewById(R.id.progress_bar);
		anyChartView.setProgressBar(progressBar);
		cartesian = AnyChart.line();
		initChart();

		recyclerView = view.findViewById(R.id.recycler_view);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
		layoutManager.setOrientation(RecyclerView.HORIZONTAL);
		recyclerView.setLayoutManager(layoutManager);

		StatisticFirebase.getStatisticToday(
				statistic -> {
					List<DashboardItemAdapter.Data> data = new ArrayList<>();
					data.add(new DashboardItemAdapter.Data(getString(R.string.number_of_new_views), statistic.getNumberOfNewViews()));
					data.add(new DashboardItemAdapter.Data(getString(R.string.number_of_new_users), statistic.getNumberOfNewUsers()));
					data.add(new DashboardItemAdapter.Data(getString(R.string.number_of_new_likes), statistic.getNumberOfNewLikes()));
					data.add(new DashboardItemAdapter.Data(getString(R.string.number_of_new_comments), statistic.getNumberOfNewComments()));
					data.add(new DashboardItemAdapter.Data(getString(R.string.number_of_new_videos), statistic.getNumberOfNewVideos()));
					data.add(new DashboardItemAdapter.Data(getString(R.string.number_of_new_reports), statistic.getNumberOfNewReports()));

					DashboardItemAdapter adapter = (DashboardItemAdapter) recyclerView.getAdapter();
					if (adapter != null) {
						adapter.setData(data);
					} else {
						recyclerView.setAdapter(new DashboardItemAdapter(data));
					}
				}, error -> {
					Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
				}
		);

		multiStateSwitch = view.findViewById(R.id.switch_state);
		multiStateSwitch.addStateFromString(getString(R.string.month));
		multiStateSwitch.addStateFromString(getString(R.string.year));
		multiStateSwitch.addStateFromString(getString(R.string.all_time));

		multiStateSwitch.addStateListener((i, state) -> {
			if (i == 0)
				prepareChartData(MONTH);
			else if (i == 1)
				prepareChartData(YEAR);
			else
				prepareChartData(ALL_TIME);
		});

		return view;
	}

	private void initChart() {
		Log.i(TAG, "initChart: ");
		cartesian.animation(true);
		cartesian.padding(10d, 20d, 5d, 20d);

		cartesian.crosshair().enabled(true);
		cartesian.crosshair()
				.yLabel(true)
				.yStroke((Stroke) null, null, null, (String) null, (String) null);

		cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

		cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

		prepareChartData(MONTH);
	}

	private void prepareChartData(String period) {
		String title = "";
		switch (period) {
			case MONTH:
				title = getString(R.string.statistic_month);
				break;
			case YEAR:
				title = getString(R.string.statistic_year);
				break;
			case ALL_TIME:
				title = getString(R.string.statistic_all_time);
				break;
		}
		cartesian.title(title);
		StatisticFirebase.getStatistic(period, this::prepareChartData,
				error -> Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
		);
	}

	private void prepareChartData(List<Statistic> listStatistic) {
		List<DataEntry> seriesData = new ArrayList<>();
		for (Statistic statistic : listStatistic) {
			seriesData.add(new CustomDataEntry(statistic));
		}
		Set set = Set.instantiate();
		set.data(seriesData);

		Mapping newViews = set.mapAs(String.format("{ x: 'x', value: '%s' }", NEW_VIEWS));
		prepareLine(cartesian, newViews, "New Views");

		Mapping newUsers = set.mapAs(String.format("{ x: 'x', value: '%s' }", NEW_USERS));
		prepareLine(cartesian, newUsers, "New Users");

		Mapping newVideos = set.mapAs(String.format("{ x: 'x', value: '%s' }", NEW_VIDEOS));
		prepareLine(cartesian, newVideos, "New Videos");

		Mapping newComments = set.mapAs(String.format("{ x: 'x', value: '%s' }", NEW_COMMENTS));
		prepareLine(cartesian, newComments, "New Comments");

		Mapping newLikes = set.mapAs(String.format("{ x: 'x', value: '%s' }", NEW_LIKES));
		prepareLine(cartesian, newLikes, "New Likes");

		Mapping newReports = set.mapAs(String.format("{ x: 'x', value: '%s' }", NEW_REPORTS));
		prepareLine(cartesian, newReports, "New Reports");

		cartesian.legend().enabled(true);
		cartesian.legend().fontSize(13d);
		cartesian.legend().padding(0d, 0d, 10d, 0d);

		anyChartView.setChart(cartesian);
	}

	private void prepareLine(Cartesian cartesian, Mapping seriesMapping, String name) {
		Line line = cartesian.line(seriesMapping);
		line.name(name);
		line.hovered().markers().enabled(true);
		line.hovered().markers()
				.type(MarkerType.CIRCLE)
				.size(4d);
		line.tooltip()
				.position("right")
				.anchor(Anchor.LEFT_CENTER)
				.offsetX(5d)
				.offsetY(5d);
	}

	private static class CustomDataEntry extends ValueDataEntry {
		CustomDataEntry(Statistic statistic) {
			super(statistic.getDate(), statistic.getNumberOfNewViews());
			setValue(NEW_VIEWS, statistic.getNumberOfNewViews());
			setValue(NEW_USERS, statistic.getNumberOfNewUsers());
			setValue(NEW_VIDEOS, statistic.getNumberOfNewVideos());
			setValue(NEW_COMMENTS, statistic.getNumberOfNewComments());
			setValue(NEW_LIKES, statistic.getNumberOfNewLikes());
			setValue(NEW_REPORTS, statistic.getNumberOfNewReports());
		}
	}
}