package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import com.jinxin.datan.net.command.ChangeConnectionStateTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.db.impl.FunDetailDaoImpl;
import com.jinxin.db.impl.ProductConnectionDaoImpl;
import com.jinxin.db.impl.ProductFunDaoImpl;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductConnection;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.FileManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 联动设备列表适配器
 * @author TangLong
 * @company 金鑫智慧
 */
public class ProductConnectionAdapter extends BaseAdapter {
	private Context context;
	private List<ProductConnection> connList;
	private ProductConnectionDaoImpl pcDaoImol;
	private ProductFunDaoImpl pfDao;
	private FunDetailDaoImpl fdDao;
	private Handler mHandler;
	
	@SuppressLint("HandlerLeak") 
	public ProductConnectionAdapter(List<ProductConnection> listData, Context context) {
		this.context = context;
		pfDao = new ProductFunDaoImpl(context);
		fdDao = new FunDetailDaoImpl(context);
		pcDaoImol = new ProductConnectionDaoImpl(context);
		mHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					notifyDataSetChanged();
					break;
				default:
					break;
				}
			}
			
		};
		
		//初始化是否启用状态，设置
		ProductFun fun = null;
		ProductFun bindFun = null;
		for(int i = 0; i < listData.size(); i++) {
			ProductConnection mConnection = listData.get(i);
			fun = getFunByFunId(mConnection.getFunId());
			bindFun = getFunByFunId(Integer.valueOf(mConnection.getBindFunId()));
			if (fun != null) {
				String mIcon = getIconPathByFunType(fun.getFunType());
				mConnection.setIcon(mIcon);
				mConnection.setFunName(fun.getFunName());
				mConnection.setFunType(fun.getFunType());
			}
			if (bindFun != null) {
				String bindIcon = getIconPathByFunType(bindFun.getFunType());
				mConnection.setBindIcon(bindIcon);
				mConnection.setBindFunName(bindFun.getFunName());
				mConnection.setBindFunType(bindFun.getFunType());
			}
		}
		this.connList = listData;
	}
	
	public void setListData(List<ProductConnection> listData){
		//初始化是否启用状态，设置
		ProductFun fun = null;
		ProductFun bindFun = null;
		for(int i = 0; i < listData.size(); i++) {
			ProductConnection mConnection = listData.get(i);
			fun = getFunByFunId(mConnection.getFunId());
			bindFun = getFunByFunId(Integer.valueOf(mConnection.getBindFunId()));
			if (fun != null) {
				String mIcon = getIconPathByFunType(fun.getFunType());
				mConnection.setIcon(mIcon);
				mConnection.setFunName(fun.getFunName());
				mConnection.setFunType(fun.getFunType());
			}
			if (bindFun != null) {
				String bindIcon = getIconPathByFunType(bindFun.getFunType());
				mConnection.setBindIcon(bindIcon);
				mConnection.setBindFunName(bindFun.getFunName());
				mConnection.setBindFunType(bindFun.getFunType());
			}
		}
		connList = listData;
		mHandler.sendEmptyMessage(0);
	}
	
	@Override
	public int getCount() {
		return connList.size();
	}

	@Override
	public Object getItem(int position) {
		return connList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;
		int pos = position;
		final ProductConnection mConnection = (ProductConnection) getItem(pos);
		if(convertView == null) {
			convertView = CommDefines.getSkinManager().view(R.layout.item_product_connection);
			holder = new Holder(convertView);
			convertView.setTag(holder);
		} else {
			holder=(Holder) convertView.getTag();
		}
		
		if (mConnection != null) {
			holder.mName.setText(mConnection.getFunName());
			holder.mBindName.setText(mConnection.getBindFunName());
			holder.mDelayTime.setText(mConnection.getTimeInterval()+"s");
			String mIcon = mConnection.getIcon();
			String bindIcon = mConnection.getBindIcon();
			//关联设备图标
			if (!TextUtils.isEmpty(mIcon)){
				Bitmap _defaultBitmap = CommUtil.drawableToBitamp(CommDefines.getSkinManager().drawable(R.drawable.icon_default));
				JxshApp.instance.getFinalBitmap().display(holder.mIcon,
						FileManager.instance().createImageUrl(mIcon), _defaultBitmap,_defaultBitmap);
			}
			//被关联设备图标
			if (!TextUtils.isEmpty(bindIcon)) {
				Bitmap _defaultBitmap = CommUtil.drawableToBitamp(CommDefines.getSkinManager().drawable(R.drawable.icon_default));
				JxshApp.instance.getFinalBitmap().display(holder.bindIcon,
						FileManager.instance().createImageUrl(bindIcon), _defaultBitmap,_defaultBitmap);
			}
			
			//触发设备状态初始化
			if (ProductConstants.FUN_TYPE_AUTO_LOCK.equals(mConnection.getFunType())||
					ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT.
						equals(mConnection.getFunType())) {
				holder.mStatus.setText(mConnection.getStatus().equals("1") ? "开启" : "关闭");
			} else if(ProductConstants.FUN_TYPE_CURTAIN.equals(mConnection.getFunType())){
				holder.mStatus.setText(mConnection.getStatus().equals("0001") ? "开启" : "关闭");
			} else if(ProductConstants.FUN_TYPE_WIRELESS_CURTAIN.equals(mConnection.getFunType())){
				holder.mStatus.setText(mConnection.getStatus().equals("00") ? "开启" : "关闭");
			} else if( ProductConstants.FUN_TYPE_AIRCONDITION.equals(mConnection.getFunType())||
					ProductConstants.FUN_TYPE_WIRELESS_SOCKET.equals(mConnection.getFunType())){
				holder.mStatus.setText(mConnection.getStatus().equals("01") ? "开启" : "关闭");
			} else if(ProductConstants.FUN_TYPE_DOUBLE_SWITCH.equals(mConnection.getFunType())){
				holder.mStatus.setText(mConnection.getStatus().equals("0000") ? "关闭" : "开启");
			}else{
				holder.mStatus.setText(mConnection.getStatus().equals("1") ? "开启" : "关闭");
			}
			
			//关联设备状态初始化
//			if (ProductConstants.FUN_TYPE_AUTO_LOCK.equals(mConnection.getBindFunType())||
//					ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT.
//						equals(mConnection.getBindFunType())) {
//				holder.mBindStatus1.setText(mConnection.getBindStatus().equals("1") ? "开启" : "关闭");
//				holder.mBindStatus2.setVisibility(View.GONE);
//			} else if (ProductConstants.FUN_TYPE_CURTAIN.equals(mConnection.getBindFunType())) {
//				holder.mBindStatus1.setText(mConnection.getBindStatus().equals("0001") ? "开启" : "关闭");
//				holder.mBindStatus2.setVisibility(View.GONE);
//			} else if (ProductConstants.FUN_TYPE_WIRELESS_CURTAIN.equals(mConnection.getBindFunType())) {//无线窗帘
//				holder.mBindStatus1.setText(mConnection.getBindStatus().equals("00") ? "开启" : "关闭");
//				holder.mBindStatus2.setVisibility(View.GONE);
//			} else if (ProductConstants.FUN_TYPE_AIRCONDITION.equals(mConnection.getBindFunType())||
//					ProductConstants.FUN_TYPE_WIRELESS_SOCKET.equals(mConnection.getBindFunType())) {//无线双路开关，插座，空调
//				holder.mBindStatus1.setText(mConnection.getBindStatus().equals("01") ? "开启" : "关闭");
//				holder.mBindStatus2.setVisibility(View.GONE);
//			}else if(ProductConstants.FUN_TYPE_DOUBLE_SWITCH.equals(mConnection.getBindFunType())
//					||ProductConstants.FUN_TYPE_MULITIPLE_SWITCH.equals(mConnection.getBindFunType())
//					||ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE.equals(mConnection.getBindFunType())
//					||ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR.equals(mConnection.getBindFunType())
//					||ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE.equals(mConnection.getBindFunType())
//					||ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO.equals(mConnection.getBindFunType())
//					||ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE.equals(mConnection.getBindFunType())
//					||ProductConstants.FUN_TYPE_THREE_SWITCH_SIX.equals(mConnection.getBindFunType())
//					||ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE.equals(mConnection.getBindFunType())
//					||ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR.equals(mConnection.getBindFunType())
//					||ProductConstants.FUN_TYPE_THREE_SWITCH_THREE.equals(mConnection.getBindFunType())){
//				holder.mBindStatus1.setText(mConnection.getBindStatus().equals("0") ? "关闭" : "开启");
//				holder.mBindStatus2.setVisibility(View.GONE);
//			}else if(ProductConstants.FUN_TYPE_POWER_AMPLIFIER.equals(mConnection.getBindFunType())){
////				try {
////					JSONObject _jb = new JSONObject(mConnection.getParaDesc());
////					String input = _jb.getString(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD);
////					holder.mBindStatus1.setText(input);
////				} catch (JSONException e) {
////					e.printStackTrace();
////				}
//				holder.mBindStatus2.setVisibility(View.GONE);
//				holder.mBindStatus1.setText(mConnection.getBindStatus().equals("1") ? "播放" : "暂停");
//			}else if(ProductConstants.FUN_TYPE_COLOR_LIGHT.equals(mConnection.getBindFunType())){//彩灯
//				holder.mBindStatus1.setText(mConnection.getBindStatus().equals("1") ? "开启" : "关闭");
//				String strColor = mConnection.getParaDesc();
//				holder.mBindStatus2.setVisibility(View.VISIBLE);
//				if (!TextUtils.isEmpty(strColor)) {
//					try {
//						int intR = stringToInt((String) strColor.subSequence(0, 3));
//						int intG = stringToInt((String) strColor.subSequence(3, 6));
//						int intB = stringToInt((String) strColor.subSequence(6, 9));
//						holder.mBindStatus2.setBackgroundColor(Color.rgb(intR, intG, intB));// 初始背景颜色
//					} catch (NumberFormatException e) {
//						e.printStackTrace();
//					} catch (StringIndexOutOfBoundsException e) {
//						e.printStackTrace();
//					}
//				} else {
//					holder.mBindStatus2.setBackgroundColor(Color.rgb(0, 0, 0));// 初始背景颜色
//				}
//			}else if(ProductConstants.FUN_TYPE_POP_LIGHT.equals(mConnection.getBindFunType())||
//					ProductConstants.FUN_TYPE_CRYSTAL_LIGHT.equals(mConnection.getBindFunType())||
//					ProductConstants.FUN_TYPE_LIGHT_BELT.equals(mConnection.getBindFunType())||
//					ProductConstants.FUN_TYPE_CEILING_LIGHT.equals(mConnection.getBindFunType())){//无线彩灯
//				
//				holder.mBindStatus1.setText(mConnection.getBindStatus().equals("1") ? "开启" : "关闭");
//				//初始化颜色
//				String operation = mConnection.getOperation(); 
//				String strColor = mConnection.getParaDesc();
//				if ("autoMode".equals(operation) ||"automode".equals(operation)) {
//					holder.mBindStatus1.setText("魔幻灯光");
//					holder.mBindStatus2.setVisibility(View.GONE);
//				}else{
//					holder.mBindStatus2.setVisibility(View.VISIBLE);
//					if (!TextUtils.isEmpty(strColor)) {
//						try {
//							JSONObject jb = new JSONObject(strColor);
//							String _color = jb.getString("color");
//							int intR = stringToInt((String) _color.subSequence(0, 3));
//							int intG = stringToInt((String) _color.subSequence(3, 6));
//							int intB = stringToInt((String) _color.subSequence(6, 9));
//							holder.mBindStatus2.setBackgroundColor(Color.rgb(intR, intG, intB));// 初始背景颜色
//						} catch (NumberFormatException e) {
//							e.printStackTrace();
//						} catch (StringIndexOutOfBoundsException e) {
//							e.printStackTrace();
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//					} else {
//						holder.mBindStatus2.setBackgroundColor(Color.rgb(0, 0, 0));// 初始背景颜色
//					}
//				}
//			}else{
//				holder.mBindStatus2.setVisibility(View.GONE);
//				holder.mBindStatus1.setText(mConnection.getBindStatus().equals("1") ? "开启" : "关闭");
//			}
			holder.mBindStatus2.setVisibility(View.GONE);
			holder.mBindStatus1.setText(R.string.setting_already);
		}
		
		holder.mSwitcher.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				ChangeConnectionStateTask ccsTask = new ChangeConnectionStateTask(context,
						mConnection.getId(), mConnection.getIsvalid().equals("1") ? "2":"1");
				ccsTask.addListener(new ITaskListener<ITask>() {

					@Override
					public void onStarted(ITask task, Object arg) {
						JxshApp.showLoading(context, "同步数据");
					}

					@Override
					public void onCanceled(ITask task, Object arg) {
						JxshApp.closeLoading();
					}

					@Override
					public void onFail(ITask task, Object[] arg) {
						JxshApp.closeLoading();
					}

					@Override
					public void onSuccess(ITask task, Object[] arg) {
						JxshApp.closeLoading();
						mConnection.setIsvalid(mConnection.getIsvalid().equals("1") ? "2":"1");
						pcDaoImol.update(mConnection);
						mHandler.sendEmptyMessage(0);
					}

					@Override
					public void onProcess(ITask task, Object[] arg) {
						
					}
				});
				ccsTask.start();
			}
		});
		holder.mSwitcher.setImageDrawable(CommDefines.getSkinManager().
				drawable(mConnection.getIsvalid().equals("1") ?
						R.drawable.ico_swithch_on : R.drawable.ico_swithch_off));
		
		return convertView;
	}
	
	class Holder{
		ImageView mIcon, bindIcon;
		TextView mName;
		TextView mStatus;
		TextView mBindName;
		TextView mBindStatus1;
		TextView mBindStatus2;
		TextView mDelayTime;
		ImageButton mSwitcher;
		
		public Holder(View view){
			mIcon = (ImageView) view.findViewById(R.id.item_icon);
			bindIcon = (ImageView) view.findViewById(R.id.item_bind_icon);
			mName = (TextView)view.findViewById(R.id.item_fun_name);
			mStatus = (TextView)view.findViewById(R.id.item_status);
			mBindName = (TextView)view.findViewById(R.id.item_bind_fun_name);
			mBindStatus1 = (TextView)view.findViewById(R.id.item_bind_status_1);
			mBindStatus2 = (TextView)view.findViewById(R.id.item_bind_status_2);
			mDelayTime = (TextView)view.findViewById(R.id.item_delay_time);
			mSwitcher = (ImageButton)view.findViewById(R.id.item_toggle_btn);
		}
	}
	
	private ProductFun getFunByFunId(int funId){
		if (context == null) {
			return null;
		}
		
		pfDao = new ProductFunDaoImpl(context);
		ProductFun pf = pfDao.get(funId);
		if (pf != null) {
			return pf;
		}
		return null;
	}
	
	private String getIconPathByFunType(String funType){
		if (context == null) {
			return null;
		}
		
		if (!TextUtils.isEmpty(funType)) {
			fdDao = new FunDetailDaoImpl(context);	
			List<FunDetail> list = fdDao.find(null, "funType=?", new String[]{funType}, null, null, null, null);
			if (list != null && list.size() > 0) {
				FunDetail fd = list.get(0);
				return fd.getIcon();
			}
		}
		return null;
	}
	
//	private int stringToInt(String str) {
//		int i = 0;
//		if (str.startsWith("00")) {
//			i = Integer.parseInt(str.substring(2));
//			return i;
//		} else if (str.startsWith("0")) {
//			i = Integer.parseInt(str.substring(1));
//			return i;
//		} else {
//			i = Integer.parseInt(str);
//		}
//		return i;
//	}
	
}
