package com.xiaos.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.db.impl.ProductPatternOperationDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.cmd.OfflineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OfflineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.OnlineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
import com.jinxin.jxsmarthome.entity.WHproductUnfrared;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.adapter.data.ProductFunVO;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.jxsmarthome.util.StringUtils;
import com.xiaos.view.PinnedHeaderExpandableListView;
import com.xiaos.view.PinnedHeaderExpandableListView.HeaderAdapter;

public class AddNewModePinnedHeaderExpandableAdapter extends  BaseExpandableListAdapter implements HeaderAdapter{
	private List<List<WHproductUnfrared>> childrenData;
	private List<ProductFunVO> groupData;
	private Context context;
	private PinnedHeaderExpandableListView listView;
	private LayoutInflater inflater;
	private FunDetail funDetail;
	private List<String> names;
	private List<List<String>> nameLists;
	private Handler mHandler = null;
	//设置初始的选中状态
	private Map<Integer, Boolean> map;
	private List<Map<Integer, Boolean>> booleans;
	private List<List<Map<Integer, Boolean>>> lists;
	private int patternId=-1;
	
	public AddNewModePinnedHeaderExpandableAdapter(List<List<WHproductUnfrared>> childrenData,List<ProductFunVO> groupData
			,Context context,PinnedHeaderExpandableListView listView,FunDetail expandDetail){
		this.groupData = groupData; 
		this.childrenData = childrenData;
		this.context = context;
		this.listView = listView;
		this.funDetail=expandDetail;
		inflater = LayoutInflater.from(this.context);
		initName(childrenData,groupData);
		initData(groupData);
		
	}

	private void initData(final List<ProductFunVO> productFunVOList){
		if (productFunVOList==null||productFunVOList.size()==0) {
			return;
		}
		this.mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					notifyDataSetChanged();
					break;
				default:
					break;
				}
			}
			
		};
		//确定初始时的CheckBox状态
		lists=new ArrayList<>();
		for (int i = 0; i < productFunVOList.size(); i++) {
			ProductFunVO productFunVO=productFunVOList.get(i);
			booleans=new ArrayList<>();
			for (int j = 0; j < childrenData.size(); j++) {
				List<WHproductUnfrared> hproductUnfrareds=childrenData.get(j);
				for (int k = 0; k < hproductUnfrareds.size(); k++) {
					if (productFunVO.getProductPatternOperation()==null&&productFunVO.getProductFun().getFunId()==hproductUnfrareds.get(k).getFundId()) {
						map=new HashMap<>();
						map.put(k, false);
						booleans.add(map);
					}else if (productFunVO.getProductPatternOperation()!=null&&
							hproductUnfrareds.get(k).getInfraRedCode().equals(productFunVO.getProductPatternOperation().getParaDesc())
							&&productFunVO.getProductFun().getFunId()==hproductUnfrareds.get(k).getFundId()) {
						map=new HashMap<>();
						map.put(k, true);
						booleans.add(map);
					}else if (productFunVO.getProductPatternOperation()!=null&&productFunVO.getProductFun().getFunId()==hproductUnfrareds.get(k).getFundId()) {
						map=new HashMap<>();
						map.put(k, false);
						booleans.add(map);
					}
				}
			}
			lists.add(booleans);
		}
		
		//取选中的数据
		for (int i = 0; i < productFunVOList.size(); i++) {
			ProductFunVO productFunVO=productFunVOList.get(i);
			if (productFunVO.getProductPatternOperations()!=null) {
				for (int j = 0; j < childrenData.size(); j++) {
					List<WHproductUnfrared> hproductUnfrareds=childrenData.get(j);
					for (int k = 0; k < hproductUnfrareds.size(); k++) {
						for (int m = 0; m < productFunVO.getProductPatternOperations().size(); m++) {
							List<ProductPatternOperation> patternOperations=productFunVO.getProductPatternOperations();
							if (hproductUnfrareds.get(k).getInfraRedCode().equals(patternOperations.get(m).getParaDesc())) {
								map=new HashMap<>();
								map.put(k, true);
								lists.get(i).set(k, map);
							}
						}
						
					}
				}
			}
		}
		
	}
	
	private void initName(List<List<WHproductUnfrared>> childrenData,List<ProductFunVO> groupData) {
		if (childrenData==null||groupData==null) {
			return;
		}
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View view = null;  
        if (convertView != null) {  
            view = convertView;  
        } else {  
            view = createChildrenView();  
        }  
        //设置子项的item
        TextView text = (TextView)view.findViewById(R.id.childto);
        final ProductFunVO productFunVO = this.groupData.get(groupPosition);
        ProductFun productFun=productFunVO.getProductFun();
		if (productFun!=null&&childrenData!=null) {
			for (int i = 0; i < childrenData.size(); i++) {
				List<WHproductUnfrared> wHproductUnfrareds=childrenData.get(i);
				for (int j = 0; j < wHproductUnfrareds.size(); j++) {
					if (wHproductUnfrareds.get(j).getFundId()==productFun.getFunId()) {
						text.setText(wHproductUnfrareds.get(childPosition).getRecoorName()); 
					}
				}
			}
		}
		List<WHproductUnfrared> whList=null;
		 if (productFunVO.getProductFun().getFunType().equals("032")&&
	        		productFunVO.getProductPatternOperations()==null) {
				List<ProductPatternOperation> patternOperations=new ArrayList<>();
				
				for (int i = 0; i < childrenData.size(); i++) {
					List<WHproductUnfrared> unfrareds=childrenData.get(i);
					for (int j = 0; j < unfrareds.size(); j++) {
						if (productFun.getFunId()==unfrareds.get(0).getFundId()) {
							whList=unfrareds;
						}
					}
				}
				if (whList!=null&&whList.size()>0) {
					for (int i = 0; i < whList.size(); i++) {
						patternOperations.add(new ProductPatternOperation(whList.get(childPosition).getId(), 
								productFun.getFunId(), productFun.getWhId(),patternId, "send", 
								null, String.valueOf(whList.get(childPosition).getUpdateTime())));
					}
					productFunVO.setProductPatternOperations(patternOperations);
					mHandler.sendEmptyMessage(0);
				}
			}
		
		//设置checkbox
	    final CheckBox checkBox = (CheckBox)view.findViewById(R.id.child_checkBox_selected);
	    checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Map< Integer, Boolean> mmap=new HashMap<>();
				mmap.put(childPosition, isChecked);
				lists.get(groupPosition).set(childPosition, mmap);
				
				//重新赋予状态

				
				String infraredCode=null;
				if (childrenData!=null&&childrenData.size()>0&&groupData!=null&&groupData.size()>0) {
					for (int i = 0; i < childrenData.size(); i++) {
						List<WHproductUnfrared> unfrareds=childrenData.get(i);
						List<ProductPatternOperation> patternOperations=new ArrayList<>();
						for (int j = 0; j < unfrareds.size(); j++) {
							patternOperations.add(new ProductPatternOperation());
						}
						for (int j = 0; j < unfrareds.size(); j++) {
							if (groupData.get(groupPosition).getProductFun().getFunId()==unfrareds.get(j).getFundId()) {
								
								infraredCode=unfrareds.get(childPosition).getInfraRedCode();
								if (productFunVO.getProductPatternOperations()==null) {//没有ProductPatternOperation
									ProductFun productFun=productFunVO.getProductFun();
									if (isChecked) {
										patternOperations.set(childPosition,new ProductPatternOperation(
												unfrareds.get(childPosition).getId(), productFun.getFunId(), productFun.getWhId(),
												patternId, "send", 
											infraredCode, String.valueOf(unfrareds.get(childPosition).getUpdateTime())));
										productFunVO.setProductPatternOperations(patternOperations);
									}else {
										
										patternOperations.set(childPosition,new ProductPatternOperation(
												unfrareds.get(childPosition).getId(), productFun.getFunId(), productFun.getWhId(),
												patternId, "send", 
											null, String.valueOf(unfrareds.get(childPosition).getUpdateTime())));
										productFunVO.setProductPatternOperations(patternOperations);
									}
									
								}else if(productFunVO.getProductPatternOperations()!=null){//有ProductPatternOperation
									if (isChecked) {
										ProductFun productFun=productFunVO.getProductFun();
										ProductPatternOperation operation=new ProductPatternOperation(
												unfrareds.get(childPosition).getId(), productFun.getFunId(), productFun.getWhId(),
												patternId, "send", 
											infraredCode, String.valueOf(unfrareds.get(childPosition).getUpdateTime()));
										productFunVO.getProductPatternOperations().set(childPosition, operation);
									}else {
										ProductFun productFun=productFunVO.getProductFun();
										ProductPatternOperation operation=new ProductPatternOperation(
												unfrareds.get(childPosition).getId(), productFun.getFunId(), productFun.getWhId(),
												patternId, "send", 
											null, String.valueOf(unfrareds.get(childPosition).getUpdateTime()));
										productFunVO.getProductPatternOperations().set(childPosition, operation);
									}
								}
							}
						}				
					}
				}
				List<ProductPatternOperation> patternOperations=productFunVO.getProductPatternOperations();
				if (patternOperations!=null) {
					productFunVO.setOpen(false);
					productFunVO.setSelected(false);
					for (ProductPatternOperation productPatternOperation : patternOperations) {
					
						if (productPatternOperation.getParaDesc()!=null) {
							productFunVO.setOpen(true);
							productFunVO.setSelected(true);
						}
					}
				}
				mHandler.sendEmptyMessage(0);
			}
		});
	    if (lists.get(groupPosition)!=null&&lists.get(groupPosition).get(childPosition)!=null) {
      	  checkBox.setChecked(lists.get(groupPosition).get(childPosition).get(Integer.valueOf(childPosition)));
		}
	    
        return view;    
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (groupData==null||groupData.size()==0||childrenData.size()==0||childrenData==null) {
			return 0;
		}else {
			ProductFun productFun=groupData.get(groupPosition).getProductFun();
			if (productFun!=null&&childrenData!=null) {
				for (int i = 0; i < childrenData.size(); i++) {
					List<WHproductUnfrared> wHproductUnfrareds=childrenData.get(i);
					for (int j = 0; j < wHproductUnfrareds.size(); j++) {
						if (wHproductUnfrareds.get(j).getFundId()==productFun.getFunId()) {
							return wHproductUnfrareds.size();
						}
					}
				}
			}
		}
		
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return 0;
	}

	@Override
	public int getGroupCount() {
		if (groupData==null||groupData.size()==0) {
			return 0;
		}
		return groupData.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View view = null;  
        if (convertView != null) {  
            view = convertView;  
        } else {  
            view = createGroupView();  
        } 
        
        ImageView iv = (ImageView)view.findViewById(R.id.groupIcon);
		
		if (isExpanded) {
			iv.setImageResource(R.drawable.ico_arrow_open);
		}
		else{
			iv.setImageResource(R.drawable.ico_arrow_close);
		}
        
        TextView text = (TextView)view.findViewById(R.id.groupto);
        if (groupData!=null&&groupData.get(groupPosition)!=null&&groupData.get(groupPosition).getProductFun()!=null) {
            text.setText(groupData.get(groupPosition).getProductFun().getFunName());  
		}
        return view;  
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	private View createChildrenView() {
		return inflater.inflate(R.layout.child, null);
	}
	
	private View createGroupView() {
		return inflater.inflate(R.layout.group, null);
	}

	@Override
	public int getHeaderState(int groupPosition, int childPosition) {
		final int childCount = getChildrenCount(groupPosition);
		if (childPosition == childCount - 1) {
			return PINNED_HEADER_PUSHED_UP;
		} else if (childPosition == -1
				&& !listView.isGroupExpanded(groupPosition)) {
			return PINNED_HEADER_GONE;
		} else {
			return PINNED_HEADER_VISIBLE;
		}
	}

	@Override
	public void configureHeader(View header, int groupPosition,
			int childPosition, int alpha) {
		String groupData =  this.groupData.get(groupPosition).getProductFun().getFunName();
		((TextView) header.findViewById(R.id.groupto)).setText(groupData);
		
	}
	
	private SparseIntArray groupStatusMap = new SparseIntArray(); 
	
	@Override
	public void setGroupClickStatus(int groupPosition, int status) {
		groupStatusMap.put(groupPosition, status);
	}

	@Override
	public int getGroupClickStatus(int groupPosition) {
		if (groupStatusMap.keyAt(groupPosition)>=0) {
			return groupStatusMap.get(groupPosition);
		} else {
			return 0;
		}
	}

}
