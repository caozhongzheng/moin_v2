package com.moinapp.wuliao.modules.ipresource;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.modules.ipresource.model.Information;

import java.util.List;

public class ListViewAdapter extends BaseAdapter {

	private Context mContext = null;
	private List<Information> mInformationList;
	private LayoutInflater mLayoutInflater;

	public ListViewAdapter(Context context, List<Information> informationList) {
		mContext = context;
		mInformationList = informationList;
		if (mLayoutInflater == null) {
			mLayoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
	}

	@Override
	public int getCount() {
		return mInformationList != null ? mInformationList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mInformationList != null ? mInformationList.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.list_item, null);
			viewHolder = getListViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			// ȡ�û������
			viewHolder = (ListViewHolder) convertView.getTag();
		}

		setContentView(viewHolder, position);

		return convertView;
	}

	private void setContentView(ListViewHolder viewHolder, int position) {
		Information information = null;
		if (mInformationList != null && mInformationList.size() > 0) {
			information = mInformationList.get(position);
			viewHolder.mTitleTextView.setText(information.getTitle());
			viewHolder.mDescTextView.setText(information.getDesc());
		}
	}

	private ListViewHolder getListViewHolder(View convertView) {
		ListViewHolder holder = new ListViewHolder();
		holder.mTitleTextView = (TextView) convertView
				.findViewById(R.id.tv_title);
		holder.mDescTextView = (TextView) convertView
				.findViewById(R.id.tv_desc);
		return holder;
	}

	public class ListViewHolder {
		public TextView mTitleTextView;
		public TextView mDescTextView;
	}

}
