package com.toptop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.toptop.R;
import com.toptop.utils.MyUtil;

import java.util.List;

public class DashboardItemAdapter extends RecyclerView.Adapter<DashboardItemAdapter.ViewHolder> {
	public void setData(List<Data> data) {
		this.dataList = data;
		notifyItemRangeChanged(0, dataList.size());
	}

	public static class Data {
		public String title;
		public String value;

		public Data(String title, Long value) {
			this.title = title;
			this.value = MyUtil.getNumberToText(value, 0);
		}
	}
	List<Data> dataList;

	public DashboardItemAdapter(List<Data> list) {
		this.dataList = list;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dashboard, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		holder.tvTitle.setText(dataList.get(position).title);
		holder.tvValue.setText(dataList.get(position).value);
	}

	@Override
	public int getItemCount() {
		return dataList.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		TextView tvTitle, tvValue;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			tvTitle = itemView.findViewById(R.id.txt_title);
			tvValue = itemView.findViewById(R.id.txt_number);
		}
	}
}
