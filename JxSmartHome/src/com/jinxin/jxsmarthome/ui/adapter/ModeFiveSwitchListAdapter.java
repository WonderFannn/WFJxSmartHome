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
import android.widget.TextView;

/**
 * 
 * @author Huangl
 * @company 金鑫智慧
 */
public class ModeFiveSwitchListAdapter extends BaseAdapter {

	/**
	 * 列表中选项点击接口
	 */
	public interface OnSwitchCheckBoxClickListener {
		void onSwitchCheckBoxEnableClick(int pos);

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
	/**
	 * 可使用状态
	 */
	private boolean[] enableStatus;

	public static final String[] THREE_SWITCHES_NAMES = JxshApp.getContext().getResources()
			.getStringArray(R.array.three_switches);

	private FunDetail fundetail;

	public ModeFiveSwitchListAdapter(DialogFragment fragment, FunDetail fundetail, boolean[] switchEnableStatus,
			boolean[] switchStatus) {
		this.onSwitchCheckBoxClickListener = (OnSwitchCheckBoxClickListener) fragment;
		this.enableStatus = switchEnableStatus;
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
			convertView = CommDefines.getSkinManager().view(R.layout.item_mode_five_switch);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		// 五路开关
		if (fundetail.getFunType().equals(ProductConstants.FUN_TYPE_FIVE_SWITCH)) {
			holder.tvSwitcher.setText("第 " + (position + 1) + " 路开关");
		}
		// 三路开关
		if (fundetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_THREE)
				|| fundetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR)
				|| fundetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE)
				|| fundetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_SIX)) {
			holder.tvSwitcher.setText(THREE_SWITCHES_NAMES[position] + "开关");
		}

		// 改变按钮背景图
		if (enableStatus[position]) {
			holder.cbSwitcher.setBackgroundResource(R.drawable.ico_check_on);
		} else {
			holder.cbSwitcher.setBackgroundResource(R.drawable.ico_check_off);
		}
		if (status[position]) {
			holder.ibSwitcher.setBackgroundResource(R.drawable.ico_swithch_on);
		} else {
			holder.ibSwitcher.setBackgroundResource(R.drawable.ico_swithch_off);
		}

		// 按钮的点击响应事件
		holder.cbSwitcher.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSwitchCheckBoxClickListener.onSwitchCheckBoxEnableClick(pos);
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
	public void notifyDataSetChanged(boolean[] switchEnableStatus, boolean[] switchStatus) {
		this.enableStatus = switchEnableStatus;
		this.status = switchStatus;
		super.notifyDataSetChanged();
	}

	static class Holder {
		ImageButton cbSwitcher;
		TextView tvSwitcher;
		ImageButton ibSwitcher;

		public Holder(View view) {
			cbSwitcher = (ImageButton) view.findViewById(R.id.ib_item_open_switch);
			tvSwitcher = (TextView) view.findViewById(R.id.tv_item_switch);
			ibSwitcher = (ImageButton) view.findViewById(R.id.ib_item_switch_toggle);
		}
	}

}
