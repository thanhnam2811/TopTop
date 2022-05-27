package com.toptop.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.toptop.R;
import com.toptop.models.Statistic;
import com.toptop.utils.firebase.StatisticFirebase;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

	private DashboardFragment() {
	}
	private static final DashboardFragment instance = new DashboardFragment();
	public static DashboardFragment getInstance() {
		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

		AnyChartView anyChartView = view.findViewById(R.id.any_chart_view);
		anyChartView.setProgressBar(view.findViewById(R.id.progress_bar));

		Cartesian cartesian = AnyChart.line();

		prepareChartData(cartesian, anyChartView);

		return view;
	}

	private void prepareChartData(Cartesian cartesian, AnyChartView anyChartView) {
		cartesian.animation(true);
		cartesian.padding(10d, 20d, 5d, 20d);

		cartesian.crosshair().enabled(true);
		cartesian.crosshair()
				.yLabel(true)
				.yStroke((Stroke) null, null, null, (String) null, (String) null);

		cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

		cartesian.title("Thống kê");

		cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

		List<DataEntry> seriesData = new ArrayList<>();
		StatisticFirebase.getStatisticInWeek().addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
					Statistic statistic = dataSnapshot.getValue(Statistic.class);
					if (statistic != null) {
						seriesData.add(new CustomDataEntry(statistic));
					}
				}

				Set set = Set.instantiate();
				set.data(seriesData);

				Mapping newViews = set.mapAs(String.format("{ x: 'x', value: '%s' }", CustomDataEntry.NEW_VIEWS));
				prepareLine(cartesian, newViews, "New Views");

				Mapping newUsers = set.mapAs(String.format("{ x: 'x', value: '%s' }", CustomDataEntry.NEW_USERS));
				prepareLine(cartesian, newUsers, "New Users");

				Mapping newVideos = set.mapAs(String.format("{ x: 'x', value: '%s' }", CustomDataEntry.NEW_VIDEOS));
				prepareLine(cartesian, newVideos, "New Videos");

				Mapping newComments = set.mapAs(String.format("{ x: 'x', value: '%s' }", CustomDataEntry.NEW_COMMENTS));
				prepareLine(cartesian, newComments, "New Comments");

				Mapping newLikes = set.mapAs(String.format("{ x: 'x', value: '%s' }", CustomDataEntry.NEW_LIKES));
				prepareLine(cartesian, newLikes, "New Likes");

				Mapping newReports = set.mapAs(String.format("{ x: 'x', value: '%s' }", CustomDataEntry.NEW_REPORTS));
				prepareLine(cartesian, newReports, "New Reports");

				cartesian.legend().enabled(true);
				cartesian.legend().fontSize(13d);
				cartesian.legend().padding(0d, 0d, 10d, 0d);

				anyChartView.setChart(cartesian);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
			}
		});
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
		static final String NEW_VIEWS = "newViews",
		NEW_USERS = "newUsers",
		NEW_VIDEOS = "newVideos",
		NEW_COMMENTS = "newComments",
		NEW_LIKES = "newLikes",
		NEW_REPORTS = "newReports";

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