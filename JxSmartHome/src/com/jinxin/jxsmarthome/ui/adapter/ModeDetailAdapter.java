package com.jinxin.jxsmarthome.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.adapter.data.ProductFunVO;
import com.jinxin.jxsmarthome.util.AppUtil;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 模式查看
 * @author YangJiJun
 * @company 金鑫智慧
 */
public class ModeDetailAdapter extends BaseExpandableListAdapter {

	private List<List<ProductFunVO>> productFunVOLists = null;
	private List<FunDetail> funDetailList = null;
	
	public ModeDetailAdapter(Context context, List<List<ProductFunVO>> 
	pfVOLists,List<FunDetail> funDetailList) {
		this.productFunVOLists = new ArrayList<List<ProductFunVO>>();
		this.funDetailList = new ArrayList<FunDetail>();
		System.out.println(funDetailList.size() +"|"+pfVOLists.size());
		if (funDetailList != null && pfVOLists != null) {
			for (int i = 0; i < funDetailList.size(); i++) {
				List<ProductFunVO> tempList = pfVOLists.get(i);
				if (tempList.size() > 0) {
					this.funDetailList.add(funDetailList.get(i));
					this.productFunVOLists.add(tempList);
				}
			}
		}
	}
	
	@Override
	public int getGroupCount() {
		return this.funDetailList == null  ? 0 : funDetailList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		List<ProductFunVO> pfvList = productFunVOLists.get(groupPosition);
		return pfvList == null ? 0 : pfvList.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		List<ProductFunVO> pfvoList = productFunVOLists
				.get(groupPosition);
		if (pfvoList != null) {
			return pfvoList.get(childPosition);
		}
		return null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupViewHolder groupHolder;
		if (convertView == null) {
			convertView = CommDefines.getSkinManager().
					view(R.layout.mode_device_group_item);
			groupHolder = new GroupViewHolder(convertView);
			convertView.setTag(groupHolder);
		}else{
			groupHolder = (GroupViewHolder) convertView.getTag();
		}
		FunDetail funDetail = funDetailList.get(groupPosition);
		if (funDetail != null) {
			groupHolder.deviceName.setText(funDetail.getFunName());
		}
		
		if (ProductConstants.FUN_TYPE_POWER_AMPLIFIER.equals(funDetail.getFunType())) {//功放
			groupHolder.groupIcon.setImageResource(R.drawable.icon_mode_small_music);
		}else if(ProductConstants.FUN_TYPE_WIRELESS_CURTAIN.equals(funDetail.getFunType()) ||
				ProductConstants.FUN_TYPE_CURTAIN.equals(funDetail.getFunType()) ||
				ProductConstants.FUN_TYPE_MASTER_CONTROLLER_CURTAIN.equals(funDetail.getFunType())){//窗帘
			groupHolder.groupIcon.setImageResource(R.drawable.icon_mode_small_curtain);
		}else if(ProductConstants.FUN_TYPE_CEILING_LIGHT.equals(funDetail.getFunType()) ||
				ProductConstants.FUN_TYPE_LIGHT_BELT.equals(funDetail.getFunType()) ||
				ProductConstants.FUN_TYPE_CRYSTAL_LIGHT.equals(funDetail.getFunType()) ||
				ProductConstants.FUN_TYPE_COLOR_LIGHT.equals(funDetail.getFunType()) ||
				ProductConstants.FUN_TYPE_POP_LIGHT.equals(funDetail.getFunType())){//所有彩灯
			groupHolder.groupIcon.setImageResource(R.drawable.icon_mode_small_color);
		}else if (ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT.equals(funDetail.getFunType())) {//普通白灯
			groupHolder.groupIcon.setImageResource(R.drawable.icon_mode_small_light);
		}else if(ProductConstants.FUN_TYPE_AUTO_CHA_ZUO.equals(funDetail.getFunType()) ||
				ProductConstants.FUN_TYPE_WIRELESS_SOCKET.equals(funDetail.getFunType()) ||
				ProductConstants.FUN_TYPE_DOUBLE_SWITCH.equals(funDetail.getFunType())){//插座
			groupHolder.groupIcon.setImageResource(R.drawable.icon_mode_small_socket);
		}else if(ProductConstants.FUN_TYPE_AUTO_LOCK.equals(funDetail.getFunType())){//锁
			groupHolder.groupIcon.setImageResource(R.drawable.icon_mode_small_lock);
		}else{
			groupHolder.groupIcon.setImageResource(R.drawable.icon_mode_small_light);
		}
		
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ChildViewHolder childHolder;
		if (convertView == null) {
			convertView = CommDefines.getSkinManager().
					view(R.layout.mode_device_child_item);
			childHolder = new ChildViewHolder(convertView);
			convertView.setTag(childHolder);
		}else{
			childHolder = (ChildViewHolder) convertView.getTag();
		}
		ProductFunVO _productFunVO = (ProductFunVO) getChild(groupPosition, childPosition);
		if (_productFunVO != null) {
			childHolder.productName.setText(_productFunVO.getProductFun().getFunName());
			if(ProductConstants.FUN_TYPE_DOUBLE_SWITCH.
					equals(_productFunVO.getProductFun().getFunType())){//双路开关状态
				String opreationDesc = _productFunVO.getProductPatternOperation().getParaDesc();
				if (!TextUtils.isEmpty(opreationDesc)) {
					try {
						JSONObject jb = new JSONObject(opreationDesc);
						String leftStatus = jb.getString("left");
						String rightStatus = jb.getString("right");
						String left = leftStatus.equals("on") ? "开" : "关";
						String right = rightStatus.equals("on") ? "开" : "关";
						childHolder.tvOpen.setText(left + "|" + right);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}else{
				childHolder.tvOpen.setText(_productFunVO.isOpen() ? "开" : "关");
			}
			childHolder.tvOpen.setTextColor(_productFunVO.isOpen() ? 
					CommDefines.getSkinManager().color(R.color.text_green) 
					: CommDefines.getSkinManager().color(R.color.text_color));
		}
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
	
	class GroupViewHolder{
		TextView deviceName;
		ImageView groupIcon;
		public GroupViewHolder(View view) {
			deviceName = (TextView) view.findViewById(R.id.device_detail_name);
			groupIcon = (ImageView) view.findViewById(R.id.iv_device_icon);
		}
	}
	
	class ChildViewHolder{
		TextView productName;
		TextView tvOpen;
		public ChildViewHolder(View view) {
			productName = (TextView) view.findViewById(R.id.product_detail_name);
			tvOpen = (TextView) view.findViewById(R.id.child_imageView);
		}
	}

}
