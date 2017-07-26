package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.entity.Feedback;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.DateUtil;

public class FeedbackHisAdapter extends BaseAdapter {
	private List<Feedback> list;
	private Context context;
	
	public void setList(List<Feedback> list) {
		this.list = list;
	}
	
	public FeedbackHisAdapter(Context context, List<Feedback> list) {
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Holder holder;
		
		if(convertView == null) {
			convertView = CommDefines.getSkinManager().view(R.layout.item_feedback_his);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		} else {
			holder=(Holder) convertView.getTag();
		}
		
		final Feedback fd = list.get(position);
		
		holder.fdTitle.setText(fd.getContent());
		holder.fdCreTime.setText(DateUtil.formatDate(fd.getCreTime()));
		if(TextUtils.isEmpty(fd.getResult())) {
			holder.fdResult.setTextColor(context.getResources().getColor(R.color.feedback_item_sub));
			holder.fdResult.setText("未反馈");
		} else {
			holder.fdResult.setTextColor(context.getResources().getColor(R.color.feedback_item_replyed));
			holder.fdResult.setText("已反馈");
		}
		return convertView;
	}
	
	class Holder{
		TextView fdTitle;
		TextView fdCreTime;
		TextView fdResult;
		
		public Holder(View view) {
			fdTitle = (TextView) view.findViewById(R.id.fdTitle);
			fdCreTime = (TextView) view.findViewById(R.id.fdCreTime);
			fdResult = (TextView) view.findViewById(R.id.fdResult);
		}
	}
	
}
