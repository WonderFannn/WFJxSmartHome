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
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
import com.jinxin.jxsmarthome.entity.WHproductUnfrared;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.ui.adapter.data.ProductFunVO;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.xiaos.view.PinnedHeaderExpandableListView;
import com.xiaos.view.PinnedHeaderExpandableListView.HeaderAdapter;

public class AmendTimerPinnedHeaderExpandableAdapter extends  BaseExpandableListAdapter implements HeaderAdapter{
	
	private Context context;
	private PinnedHeaderExpandableListView listView;
	private LayoutInflater inflater;
	private List<List<WHproductUnfrared>> childrenData;
	private List<ProductFun> groupData;
	private Handler mHandler = null;
	private List<ProductFunVO> productFunVOList;
	private List<ProductFun> funs=new ArrayList<>();
	private List<ProductPatternOperation> ppoList = null;
	private List<List<ProductPatternOperation>> ppoLists = null;
	private int patternId;
	private Map<Integer, Boolean> map;
	private List<Map<Integer, Boolean>> booleans;
	private List<List<Map<Integer, Boolean>>> lists;
	
	public AmendTimerPinnedHeaderExpandableAdapter(List<List<WHproductUnfrared>> childrenData,List<ProductFun> groupData
			,Context context,PinnedHeaderExpandableListView listView,
			final List<ProductFunVO> productFunVOList,FunDetail funDetail,int patternId){
		this.groupData = groupData; 
		this.childrenData = childrenData;
		this.context = context;
		this.productFunVOList=productFunVOList;
		this.listView = listView;
		this.patternId=patternId;
		inflater = LayoutInflater.from(this.context);
		initData(groupData);
	}
	private void initData(List<ProductFun> _pfList){
		if (productFunVOList==null||productFunVOList.size()==0) {
			return;
		}
		ppoLists=new ArrayList<>();
		lists=new ArrayList<>();
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
		
		ProductPatternOperationDaoImpl ppoDaoImpl = new ProductPatternOperationDaoImpl(
				context);
		funs.clear();
		for (int i = 0; i < childrenData.size(); i++) {
			List<WHproductUnfrared> unfrareds=childrenData.get(i);
			 WHproductUnfrared wHproductUnfrared=unfrareds.get(0);
			 for (int m = 0; m < groupData.size(); m++) {
		        	ProductFun fun=groupData.get(m);
					if (wHproductUnfrared.getFundId()==fun.getFunId()) {
						
						funs.add(groupData.get(m));
					}
			 }
			 ppoList = ppoDaoImpl.find(null, "funId=? and patternId=?",
						new String[] { Integer.toString(wHproductUnfrared.getFundId()) ,Integer.toString(patternId)}, null,
						null, null, null);
			 ppoLists.add(ppoList);
		}
		//确定
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
		
	};
	
	
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
//		if (childrenData!=null&&childrenData.size()>0) {
//			return childrenData.get(groupPosition).get(childPosition);
//		}else {
//			return null;
//		}
		return null;
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
        final ProductFunVO productFunVO=productFunVOList.get(groupPosition);
        final List<ProductPatternOperation> operations=productFunVO.getProductPatternOperations();
        final ProductFun productFun=productFunVO.getProductFun();
        List<WHproductUnfrared> whList=null;
        System.out.println("=====productFunVO"+productFunVO.toString());
      //为空时自动添加
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
			}
			
			
			
		}
       
        if (productFun.getFunType().equals("032")&&
        		operations!=null) {
			List<ProductPatternOperation> patternOperations=new ArrayList<>();
			
			for (int i = 0; i < childrenData.size(); i++) {
				List<WHproductUnfrared> unfrareds=childrenData.get(i);
				for (int j = 0; j < unfrareds.size(); j++) {
					if (productFun.getFunId()==unfrareds.get(0).getFundId()) {
						whList=unfrareds;
					}
				}
			}
			if (whList!=null&&whList.size()>0&&
					productFunVO.getProductPatternOperations().size()<whList.size()) {
				List<Integer> order=new ArrayList<>();
				for (int i = 0; i < whList.size(); i++) {
					for (int k = 0; k < operations.size(); k++) {
						if (whList.get(i).getInfraRedCode().equals(operations.get(k).getParaDesc())) {
							order.add(i);
						}
					}
					patternOperations.add(new ProductPatternOperation(whList.get(childPosition).getId(), 
							productFun.getFunId(), productFun.getWhId(),patternId, "send", 
							null, String.valueOf(whList.get(childPosition).getUpdateTime())));
					if (order.size()>0) {
						for (int j = 0; j < order.size(); j++) {
							patternOperations.get(order.get(j)).setParaDesc(childrenData.get(groupPosition).get(childPosition).getInfraRedCode());;
						}
						productFunVO.setProductPatternOperations(patternOperations);
					}
				}
			}
			
		}
        
        TextView text = (TextView)view.findViewById(R.id.childto);
        final CheckBox checkBox = (CheckBox)view.findViewById(R.id.child_checkBox_selected);
        
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Map< Integer, Boolean> mmap=new HashMap<>();
				mmap.put(childPosition, isChecked);
				lists.get(groupPosition).set(childPosition, mmap);
				String infraredCode=null;
				if (childrenData!=null&&childrenData.size()>0&&groupData!=null&&groupData.size()>0) {				if (childrenData!=null&&childrenData.size()>0&&groupData!=null&&groupData.size()>0) {
					for (int i = 0; i < childrenData.size(); i++) {
						List<WHproductUnfrared> unfrareds=childrenData.get(i);
						List<ProductPatternOperation> patternOperations=new ArrayList<>();
						for (int j = 0; j < unfrareds.size(); j++) {
							patternOperations.add(new ProductPatternOperation());
						}
						for (int j = 0; j < unfrareds.size(); j++) {
							if (groupData.get(groupPosition).getFunId()==unfrareds.get(j).getFundId()) {
								
								infraredCode=unfrareds.get(childPosition).getInfraRedCode();
								if(productFunVO.getProductPatternOperations()!=null){//有ProductPatternOperation
									if (isChecked) {
										ProductFun productFun=productFunVO.getProductFun();
										if (productFunVO.getProductPatternOperations().size()<unfrareds.size()) {
											ProductPatternOperation operation=new ProductPatternOperation(
													unfrareds.get(childPosition).getId(), productFun.getFunId(), productFun.getWhId(),
													patternId, "send", 
												null, String.valueOf(unfrareds.get(childPosition).getUpdateTime()));
											productFunVO.getProductPatternOperations().add(childPosition,operation);
										}else {
											ProductPatternOperation operation=new ProductPatternOperation(
													unfrareds.get(childPosition).getId(), productFun.getFunId(), productFun.getWhId(),
													patternId, "send", 
												infraredCode, String.valueOf(unfrareds.get(childPosition).getUpdateTime()));
											productFunVO.getProductPatternOperations().set(childPosition, operation);
										}
									}else {
										ProductFun productFun=productFunVO.getProductFun();
										if (productFunVO.getProductPatternOperations().size()<unfrareds.size()) {
											ProductPatternOperation operation=new ProductPatternOperation(
													unfrareds.get(childPosition).getId(), productFun.getFunId(), productFun.getWhId(),
													patternId, "send", 
												null, String.valueOf(unfrareds.get(childPosition).getUpdateTime()));
											productFunVO.getProductPatternOperations().add(childPosition,operation);
										}else {
											ProductPatternOperation operation=new ProductPatternOperation(
													unfrareds.get(childPosition).getId(), productFun.getFunId(), productFun.getWhId(),
													patternId, "send", 
												null, String.valueOf(unfrareds.get(childPosition).getUpdateTime()));
											productFunVO.getProductPatternOperations().get(childPosition).setParaDesc(null);;
										}
									}
								}
							}
						}				
					}
				}
}
				mHandler.sendEmptyMessage(0);
			}
        	
        });
        //设置选中状态
        productFunVO.setOpen(false);
		productFunVO.setSelected(false);
		if (operations!=null) {
			for (int j = 0; j < operations.size(); j++) {
				if (operations.get(j)!=null&&
						operations.get(j).getParaDesc()!=null) {
					productFunVO.setOpen(true);
					productFunVO.setSelected(true);
				}
				
			}
		}
//        for (int i = 0; i < productFunVOList.size(); i++) {
//			ProductFunVO operation=productFunVOList.get(i);
//			if (operation.getProductPatternOperations()!=null) {
//				List<ProductPatternOperation> operations=operation.getProductPatternOperations();
//				for (int j = 0; j < operations.size(); j++) {
//					if (operations.get(j)!=null&&
//							operations.get(j).getParaDesc()!=null&&
//							operations.get(j).getParaDesc()!="0") {
//						operation.setOpen(true);
//						operation.setSelected(true);
//					}
//					
//				}
//			}
//			
//		}
        if (lists.get(groupPosition)!=null&&lists.get(groupPosition).get(childPosition)!=null) {
        	  checkBox.setChecked(lists.get(groupPosition).get(childPosition).get(Integer.valueOf(childPosition)));
		}
      
        if (childrenData!=null&&childrenData.size()>0&&groupData!=null&&groupData.size()>0) {
			for (int i = 0; i < childrenData.size(); i++) {
				List<WHproductUnfrared> unfrareds=childrenData.get(i);
				for (int j = 0; j < unfrareds.size(); j++) {
					if (groupData.get(groupPosition).getFunId()==unfrareds.get(j).getFundId()) {
						text.setText(unfrareds.get(childPosition).getRecoorName()); 
					}
				}				
			}
		}
        return view;    
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (groupData!=null&&groupData.size()>0) {
			//便利匹配的数据
			if (childrenData!=null&&childrenData.size()>0&&groupData!=null&&groupData.size()>0) {
				for (int i = 0; i < childrenData.size(); i++) {
					List<WHproductUnfrared> unfrareds=childrenData.get(i);
					for (int j = 0; j < unfrareds.size(); j++) {
						if (groupData.get(groupPosition).getFunId()==unfrareds.get(0).getFundId()) {
							return unfrareds.size();
						}					
					}				
				}
			}
		}
		return 0;
		
	}

	@Override
	public Object getGroup(int groupPosition) {
//		if (groupData!=null&&groupData.size()>0) {
//			return groupData.get(groupPosition);
//		}else {
//			return null;
//		}
		return null;
	}

	@Override
	public int getGroupCount() {
		if (groupData!=null&&groupData.size()>0) {
			return groupData.size();
		}else {
			return 0;
		}
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
        text.setText(groupData.get(groupPosition).getFunName());  
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
		String groupData =  this.groupData.get(groupPosition).getFunName();
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
	
	/**
	 * 发送控制命令
	 * 
	 * @param productFun 产品
	 * @param funDetail 产品详情
	 * @param cmdStr 命令
	 */
	private Map<String, Object> params = null;
	public void plutoSoundBoxControl(ProductFun productFun, FunDetail funDetail, String cmdStr,String infraredCode) {
		JxshApp.instance.isClinkable = false;
		params = new HashMap<String, Object>();
		params.put("src", "0x01");
		params.put("dst", "0x01");
		params.put("type", "00 32");
		params.put("op", cmdStr);
		StringBuffer buffer=new StringBuffer(infraredCode);
		int count=0;
		for (int i = 2; i < infraredCode.length();i+=2) {
			buffer.insert(i+count, " ");
			count+=1;
		}
		params.put(StaticConstant.PARAM_TEXT, buffer.toString()+" ");
		String type = StaticConstant.OPERATE_COMMON_CMD;
		if (NetworkModeSwitcher.useOfflineMode(context)) {
			String zegbingWhId = AppUtil.getGetwayMACByProductWhId(context, productFun.getWhId());
			String localHost = OfflineCmdGenerator.getGatewayLocalIpBySn(context, zegbingWhId);
			if (localHost == null || "".equals(localHost)) {
				Logger.error(null, "localHost is null");
				Toast.makeText(context, "未找到对应网关", Toast.LENGTH_SHORT).show();
				return;
			}
			OfflineCmdGenerator cmdGenerator = new OfflineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(context, productFun, funDetail, params, type);

			OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(context, localHost + ":3333", cmdList, true,
					false);
			offlineSender.addListener(listener);
			offlineSender.send();
		} else {
			OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(context, productFun, funDetail, params, type);
			OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(context, DatanAgentConnectResource.HOST_ZEGBING,
					true, cmdList, true, 0, false);
			onlineSender.addListener(listener);
			onlineSender.send();
		}
	}
	
	/**
	 * 应答监听
	 */
	TaskListener<ITask> listener = new TaskListener<ITask>() {

		@Override
		public void onFail(ITask task, Object[] arg) {
			JxshApp.showToast(context, context.getString(R.string.mode_contorl_fail));
			if (arg == null) {
				JxshApp.showToast(context, context.getString(R.string.mode_contorl_fail));
			} else {
				RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
				JxshApp.showToast(context, resultObj.validResultInfo);
			}
			JxshApp.instance.isClinkable = true;
		}

		@Override
		public void onSuccess(ITask task, Object[] arg) {
			
			RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
			JxshApp.showToast(context, "=="+resultObj.validResultInfo+"===="+resultObj.validResultCode);
			System.out.println("====="+resultObj.validResultCode);
			if ("-1".equals(resultObj.validResultInfo))
				return;
			String resultStr=resultObj.validResultInfo;
			String payload=resultStr.substring(resultStr.indexOf("[")+1, resultStr.indexOf("]"));
			JxshApp.showToast(context,getResponseStr(payload));
		}

	};
	
	/**
	 * 获取状态字符串
	 * 
	 * @param result 应答字符串
	 * @return string 状态字符
	 */
	protected String getResponseStr(String result) {
		if (result == null)
			return null;
		StringBuffer stb = new StringBuffer();
		int length = result.length();
		String statusCode = result.substring(length - 9, length - 1);
		String[] code = statusCode.split(" ");
		for (int i = 0; i < code.length; i++) {
			System.out.println("code="+code[i].toString()+"==");
		}
		if (code[code.length-1].equals("00")) {//读取功率数值
			stb.append("发送成功");
		}else {
			stb.append("发送成功");
		}

		return stb.toString();
	}

}
