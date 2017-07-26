package com.jinxin.jxsmarthome.ui.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.datan.net.DatanAgentConnectResource;
import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.TaskListener;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.broadcast.BroadcastManager;
import com.jinxin.jxsmarthome.cmd.OfflineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OfflineCmdSenderLong;
import com.jinxin.jxsmarthome.cmd.OnlineCmdGenerator;
import com.jinxin.jxsmarthome.cmd.OnlineCmdSenderLong;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.WHproductUnfrared;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.Logger;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class InfraredControlGridAdapter extends BaseAdapter {
	
	private List<ProductFun> funs;
	private LayoutInflater inflater;
	private Context mContext;
	private Map<String, Object> params = null;
	private ProductFun productFun = null;
	private FunDetail funDetail = null;
	private WHproductUnfrared wHproductUnfrared;
	private List<WHproductUnfrared> wHproductUnfrareds;
	
	
	public InfraredControlGridAdapter(Context context,
			List<WHproductUnfrared> wHproductUnfrareds,ProductFun productFun,FunDetail funDetail) {
		this.wHproductUnfrareds=wHproductUnfrareds;
		this.mContext=context;
		this.funDetail=funDetail;
		this.productFun=productFun;
		this.inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return wHproductUnfrareds.size();
	}

	@Override
	public Object getItem(int position) {
		return wHproductUnfrareds.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder;
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.item_grid_infrared_transport, parent, false);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		} else {
			holder=(Holder) convertView.getTag();
		}
		if (wHproductUnfrareds.size()>0) {
			holder.mName.setText(wHproductUnfrareds.get(position).getRecoorName());
			
			holder.mThumb.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					plutoSoundBoxControl(productFun, funDetail, "0C",wHproductUnfrareds.get(position).getInfraRedCode());
					Intent intent=new Intent();
					intent.setAction(BroadcastManager.ACTION_INFRARED_TRANSPOND);
					intent.putExtra("infraredCode", wHproductUnfrareds.get(position).getInfraRedCode()+"");
					mContext.sendBroadcast(intent);
					System.out.println("==po==="+position);
//					JxshApp.showToast(mContext, ""+position);
				}
			});
		}else {
			JxshApp.showToast(mContext, "暂无设备");
		}
		
		return convertView;
	}
	
	class Holder{
		ImageView mThumb;
		TextView mName;
		
		public Holder(View view){
			mThumb = (ImageView)view.findViewById(R.id.item_thumb);
			mName = (TextView)view.findViewById(R.id.item_name);
		}
	}
	
	/**
	 * 发送控制命令
	 * 
	 * @param productFun 产品
	 * @param funDetail 产品详情
	 * @param cmdStr 命令
	 */
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
		if (NetworkModeSwitcher.useOfflineMode(mContext)) {
			String zegbingWhId = AppUtil.getGetwayMACByProductWhId(mContext, productFun.getWhId());
			String localHost = OfflineCmdGenerator.getGatewayLocalIpBySn(mContext, zegbingWhId);
			if (localHost == null || "".equals(localHost)) {
				Logger.error(null, "localHost is null");
				Toast.makeText(mContext, "未找到对应网关", Toast.LENGTH_SHORT).show();
				return;
			}
			OfflineCmdGenerator cmdGenerator = new OfflineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(mContext, productFun, funDetail, params, type);

			OfflineCmdSenderLong offlineSender = new OfflineCmdSenderLong(mContext, localHost + ":3333", cmdList, true,
					false);
			offlineSender.addListener(listener);
			offlineSender.send();
		} else {
			OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
			List<byte[]> cmdList = cmdGenerator.generateCmd2(mContext, productFun, funDetail, params, type);
			OnlineCmdSenderLong onlineSender = new OnlineCmdSenderLong(mContext, DatanAgentConnectResource.HOST_ZEGBING,
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
			JxshApp.showToast(mContext, mContext.getString(R.string.mode_contorl_fail));
			if (arg == null) {
				JxshApp.showToast(mContext, mContext.getString(R.string.mode_contorl_fail));
			} else {
				RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
				JxshApp.showToast(mContext, resultObj.validResultInfo);
			}
			JxshApp.instance.isClinkable = true;
		}

		@Override
		public void onSuccess(ITask task, Object[] arg) {
			
			RemoteJsonResultInfo resultObj = (RemoteJsonResultInfo) arg[0];
//			JxshApp.showToast(mContext, "=="+resultObj.validResultInfo+"===="+resultObj.validResultCode);
			System.out.println("====="+resultObj.validResultCode);
			if ("-1".equals(resultObj.validResultInfo)){
				JxshApp.showToast(mContext, mContext.getString(R.string.mode_contorl_fail));
				return;
			}
			String resultStr=resultObj.validResultInfo;
			String payload=resultStr.substring(resultStr.indexOf("[")+1, resultStr.indexOf("]"));
			JxshApp.showToast(mContext,getResponseStr(payload));
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
			stb.append("发送失败");
		}

		return stb.toString();
	}
	
}
