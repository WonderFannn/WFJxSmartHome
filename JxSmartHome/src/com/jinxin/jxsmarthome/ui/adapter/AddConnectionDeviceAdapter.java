package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductConnectionVO;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.record.FileManager;

/**
 * 被关联设备列表适配器
 * 
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class AddConnectionDeviceAdapter extends BaseExpandableListAdapter {

	private Context context;
	private List<FunDetail> groupList;
	private List<List<ProductConnectionVO>> childList;

	private Handler mHandler = null;
	private int lastGroupPos = -1, lastChildPos = -1;
	private ProductFun groupFun = null;
	private String groupStatus = "";//除设置双路开关为设置点时，其他都设置为 空
	
	String leftStatus = "0";//当前选择双路开关左路状态
	String rightStatus = "0";//当前选择双路开关右路状态
	boolean isLeftOpen = false, isRightOpen = false;

	public AddConnectionDeviceAdapter(Context context,
			List<FunDetail> funDetailList,
			List<List<ProductConnectionVO>> connectionVOList) {
		this.context = context;
		this.groupList = funDetailList;
		this.childList = connectionVOList;
		this.mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				notifyDataSetChanged();
				if (lastGroupPos > -1 && lastChildPos > -1) {
					if (childList.get(lastGroupPos).get(lastChildPos).isSelected()) {
						groupFun = childList.get(lastGroupPos).get(lastChildPos).getProductFun();
					}else{
						groupFun = null;
					}
				}
			}

		};
	}
	
	public ProductFun getGroupFun(){
		return groupFun;
	}
	
	public String getDoubleStatus(){
		return groupStatus;
	}

	// 返回父列表个数
	@Override
	public int getGroupCount() {
		return groupList.size();
	}

	// 返回子列表个数
	@Override
	public int getChildrenCount(int groupPosition) {
		return childList.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {

		return groupList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childList.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupHolder holder = null;
		if (convertView == null) {
			convertView = CommDefines.getSkinManager().view(
					R.layout.group_item_tirgger_product);
			holder = new GroupHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (GroupHolder) convertView.getTag();
		}

		FunDetail fd = groupList.get(groupPosition);
		String iconPath = fd.getIcon();
		if (!TextUtils.isEmpty(iconPath)) {
			Bitmap _defaultBitmap = CommUtil.drawableToBitamp(CommDefines.getSkinManager().drawable(R.drawable.icon_default));
			JxshApp.instance.getFinalBitmap().display(holder.mIcon,
					FileManager.instance().createImageUrl(iconPath), _defaultBitmap,_defaultBitmap);
		}
		holder.mGroupName.setText(groupList.get(groupPosition).getFunName());
		if (isExpanded) {
			holder.mArrow.setImageResource(R.drawable.ico_big_arrow_open);
		} else {
			holder.mArrow.setImageResource(R.drawable.ico_big_arrow_close);
		}

		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		SwitchHolder switchHolder = null;
		DoubleSwitchHolder doubleHolder = null;
		
		final int gPos = groupPosition;
		final int cPos = childPosition;
		final ProductConnectionVO connnectionVO = childList.get(gPos).get(cPos);
		ProductFun productFun = connnectionVO.getProductFun();
		
		int viewType = 0;
		if (ProductConstants.FUN_TYPE_DOUBLE_SWITCH
				.equals(groupList.get(groupPosition).getFunType())) {//双路开关
			viewType = 1;
		} else{
			viewType = 0;
		}
		
		//无convertView，需要new出各个控件及相应holder
		if (convertView == null) {
			switch (viewType) {
			case 0:
				convertView = CommDefines.getSkinManager().view(
						R.layout.item_tirgger_switch, null);
				switchHolder = new SwitchHolder(convertView);
				convertView.setTag(switchHolder);
				break;
			case 1:
				convertView = CommDefines.getSkinManager().view(
						R.layout.item_tirgger_double_switch, null);
				doubleHolder = new DoubleSwitchHolder(convertView);
				convertView.setTag(doubleHolder);
				break;
			}

		} else {
			switch (viewType) {
			case 0:
				switchHolder = (SwitchHolder) convertView.getTag();
				break;
			case 1:
				doubleHolder = (DoubleSwitchHolder) convertView.getTag();
				break;
			}
		}
		//设置资源及点击事件
		switch (viewType) {
		case 0:
			switchHolder.mName.setText(productFun.getFunName());
			switchHolder.mCheck.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					connnectionVO.setSelected(!connnectionVO.isSelected());
					lastGroupPos =  gPos;
					lastChildPos = cPos;
					groupStatus = "";
					mHandler.sendEmptyMessage(0);
				}
			});

			if (lastGroupPos == gPos && lastChildPos == cPos) {
				switchHolder.mCheck.setChecked(connnectionVO.isSelected());
			}else{
				connnectionVO.setSelected(false);
				switchHolder.mCheck.setChecked(false);
			}
			if (connnectionVO.isSelected()) {
				switchHolder.mOpenBtn.setImageDrawable(CommDefines.getSkinManager().
						drawable(connnectionVO.isOpen() ?
								R.drawable.ico_swithch_on : R.drawable.ico_swithch_off));
			}else{
				switchHolder.mOpenBtn.setImageDrawable(CommDefines.getSkinManager().
						drawable(R.drawable.ico_swithch_off));
			}
			switchHolder.mOpenBtn
					.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							if (connnectionVO.isSelected()) {
								connnectionVO.setOpen(!connnectionVO.isOpen());
								connnectionVO.getProductFun().setOpen(connnectionVO.isOpen());
								mHandler.sendEmptyMessage(0);
							}else{
								JxshApp.showToast(context, "请先选择设备");
							}
						}
					});
					
			break;
		case 1://双路开关
			doubleHolder.mName.setText(productFun.getFunName());
			
			doubleHolder.mCheck.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
							connnectionVO.setSelected(!connnectionVO.isSelected());
							leftStatus = "00";
							rightStatus ="00";
							isLeftOpen = false;
							isRightOpen = false;
							lastGroupPos =  gPos;
							lastChildPos = cPos;
							mHandler.sendEmptyMessage(0);
						}
					});

			if (lastGroupPos == gPos && lastChildPos == cPos) {
				doubleHolder.mCheck.setChecked(connnectionVO.isSelected());
			}else{
				doubleHolder.mCheck.setChecked(false);
				connnectionVO.setSelected(false);
			}
			
			doubleHolder.mLeftBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (connnectionVO.isSelected()) {
						isLeftOpen = !isLeftOpen;
						leftStatus = isLeftOpen ? "01":"00";
						mHandler.sendEmptyMessage(0);
					}else{
						JxshApp.showToast(context, "请先选择设备");
					}
				}
			});
			
			doubleHolder.mRightBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (connnectionVO.isSelected()) {
						isRightOpen = !isRightOpen;
						rightStatus = isRightOpen ? "01":"00";
						mHandler.sendEmptyMessage(0);
					}else{
						JxshApp.showToast(context, "请先选择设备");
					}
				}
			});
			if (connnectionVO.isSelected()) {
				doubleHolder.mLeftBtn.setImageDrawable(CommDefines.getSkinManager().
						drawable(isLeftOpen ?
								R.drawable.ico_swithch_on : R.drawable.ico_swithch_off));
				doubleHolder.mRightBtn.setImageDrawable(CommDefines.getSkinManager().
						drawable(isRightOpen ?
								R.drawable.ico_swithch_on : R.drawable.ico_swithch_off));
				groupStatus = leftStatus + rightStatus;
				connnectionVO.getProductFun().setOpen(groupStatus.equals("0000") ? false : true);
			}else{
				doubleHolder.mLeftBtn.setImageDrawable(CommDefines.getSkinManager().
						drawable(R.drawable.ico_swithch_off));
				doubleHolder.mRightBtn.setImageDrawable(CommDefines.getSkinManager().
						drawable(R.drawable.ico_swithch_off));
				connnectionVO.getProductFun().setOpen(false);
			}
			break;
		}

		return convertView;
	}
	

	@Override
	public int getChildType(int groupPosition, int childPosition) {
		int viewType = 0;
		if (ProductConstants.FUN_TYPE_DOUBLE_SWITCH
				.equals(groupList.get(groupPosition).getFunType())) {//双路开关
			viewType = 1;
		} else{//其它设备
			viewType = 0;
		}
		
		return viewType;
	}

	@Override
	public int getChildTypeCount() {
		return 2;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	class GroupHolder {
		TextView mGroupName;
		ImageView mArrow, mIcon;

		public GroupHolder(View view) {
			mGroupName = (TextView) view.findViewById(R.id.group_item);
			mIcon = (ImageView) view.findViewById(R.id.item_icon);
			mArrow = (ImageView) view.findViewById(R.id.item_image);
		}
	}

	class SwitchHolder {
		CheckBox mCheck;
		TextView mName;
		ImageButton mOpenBtn;

		public SwitchHolder(View view) {
			mCheck = (CheckBox) view.findViewById(R.id.item_cb_btn);
			mName = (TextView) view.findViewById(R.id.item_fun_name);
			mOpenBtn = (ImageButton)view.findViewById(R.id.imageButton_open);
		}
	}
	
	class DoubleSwitchHolder{
		CheckBox mCheck;
		TextView mName;
		ImageButton mLeftBtn, mRightBtn;
		public DoubleSwitchHolder(View view) {
			mCheck = (CheckBox) view.findViewById(R.id.item_cb_btn);
			mName = (TextView) view.findViewById(R.id.item_fun_name);
			mLeftBtn = (ImageButton) view.findViewById(R.id.image_left_open);
			mRightBtn = (ImageButton) view.findViewById(R.id.image_right_open);
			
		}
	}

}
