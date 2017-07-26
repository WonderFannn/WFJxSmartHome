package com.jinxin.jxsmarthome.ui.adapter;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;

import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author Huangl
 * @company 金鑫智慧
 */
public class FiveSwitchListAdapter extends BaseAdapter {

	/**
	 * 列表中选项点击接口
	 */
	public interface OnSwitchCheckBoxClickListener {
		void onSwitchCheckBoxClick(int pos);
	}

	/**
	 * 接口实例化
	 */
	private OnSwitchCheckBoxClickListener onSwitchCheckBoxClickListener;
	/**
	 * 开关状态
	 */
	private boolean[] status;
	public static final String[] MULTI_SWITCHES_NAMES = JxshApp.getContext().getResources()
			.getStringArray(R.array.multi_switches);
	public static final String[] THREE_SWITCHES_NAMES = JxshApp.getContext().getResources()
			.getStringArray(R.array.three_switches);
	private FunDetail fundetail;

	public FiveSwitchListAdapter(DialogFragment fragment, FunDetail fundetail, boolean[] switchStatus) {
		this.onSwitchCheckBoxClickListener = (OnSwitchCheckBoxClickListener) fragment;
		this.status = switchStatus;
		this.fundetail = fundetail;
	}

	@Override
	public int getCount() {
		return status.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		final int pos = position;
		if (convertView == null) {
			convertView = CommDefines.getSkinManager().view(R.layout.item_five_switch);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		// 五路开关
		if (fundetail.getFunType().equals("037")) {
			holder.tvSwitcher.setText("第 " + (position + 1) + " 路开关");
		}
		// holder.tvSwitcher.setText("第 " + (position + 1) + " 路开关");
		// 多路开关
		if (fundetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_THREE)
				|| fundetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR)
				|| fundetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE)
				|| fundetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_SIX)) {
			holder.tvSwitcher.setText(THREE_SWITCHES_NAMES[position] + "开关");
		}
		if (fundetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH)
				|| fundetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE)
				|| fundetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR)
				|| fundetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE)
				|| fundetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO)
				|| fundetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE)) {
			holder.tvSwitcher.setText(MULTI_SWITCHES_NAMES[position] + "开关");
		}

		// 改变按钮背景图
		if (status[position]) {
			holder.ivSwitcher.setBackgroundResource(R.drawable.ico_switch_on);
			holder.ibSwitcher.setBackgroundResource(R.drawable.ico_swithch_on);
		} else {
			holder.ivSwitcher.setBackgroundResource(R.drawable.ico_switch_off);
			holder.ibSwitcher.setBackgroundResource(R.drawable.ico_swithch_off);
		}

		// 按钮的点击响应事件
		holder.ivSwitcher.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSwitchCheckBoxClickListener.onSwitchCheckBoxClick(pos);
			}
		});
		holder.ibSwitcher.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSwitchCheckBoxClickListener.onSwitchCheckBoxClick(pos);
			}
		});
		return convertView;
	}

	/**
	 * 更新列表视图
	 * 
	 * @param switchStatus
	 *            开关状态
	 */
	public void notifyDataSetChanged(boolean[] switchStatus) {
		this.status = switchStatus;
		super.notifyDataSetChanged();
	}

	static class Holder {
		ImageView ivSwitcher;
		TextView tvSwitcher;
		ImageButton ibSwitcher;

		public Holder(View view) {
			ivSwitcher = (ImageView) view.findViewById(R.id.img_switch);
			tvSwitcher = (TextView) view.findViewById(R.id.tv_item_switch);
			ibSwitcher = (ImageButton) view.findViewById(R.id.ib_item_switch_toggle);
		}
	}

}
