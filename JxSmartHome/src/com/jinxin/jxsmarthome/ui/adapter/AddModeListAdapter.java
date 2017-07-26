package com.jinxin.jxsmarthome.ui.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.AddNewModeActivity;
import com.jinxin.jxsmarthome.activity.AmendModeNewActivity;
import com.jinxin.jxsmarthome.constant.ProductConstants;
import com.jinxin.jxsmarthome.constant.StaticConstant;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
import com.jinxin.jxsmarthome.fragment.ModeFiveSocketFragment;
import com.jinxin.jxsmarthome.fragment.ModeDoubleSocketFragment;
import com.jinxin.jxsmarthome.fragment.ModeLightBeltFragment;
import com.jinxin.jxsmarthome.fragment.ModeLightsColorFragment;
import com.jinxin.jxsmarthome.fragment.ModeOneSocketFragment;
import com.jinxin.jxsmarthome.fragment.ModePopLightFragment;
import com.jinxin.jxsmarthome.fragment.ModeThreeSocketFragment;
import com.jinxin.jxsmarthome.fragment.MusicSettingForPatternFragment;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.adapter.data.ProductFunVO;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.StringUtils;

/**
 * 添加模式组合列表适配器
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class AddModeListAdapter extends BaseAdapter {

	private List<ProductFunVO> productFunVOList;
	private Context con;
	private Handler mHandler = null;
	private FunDetail funDetail = null;
	private FragmentTransaction fragmentTransaction;
	private int whichMode = -1;//是添加或修改操作的标识  “0”是添加 “1”是修改 
	
	private static String OPERATION_CLOSE = "close";
	private static String OPERATION_SET = "set";
	private static String OPERATION_HUEANDSAT = "hueandsat";
	private static String OPERATION_SEND = "send";
	private static String OPERATION_PLAY = "play";
	private static String OPERATION_PAUSE = "pause";
	private static String OPERATION_COLOR_OFF = "000000000000";
	private static String OPERATION_DOUBLE_SOCKET = "double-on-off";
	private static String OPERATION_FIVE_SOCKET = "five-on-off";
	private static String OPERATION_SIX_SOCKET = "six-on-off";
	private static String OPERATION_THREE_SOCKET = "three-on-off";
	
	private boolean isChecked = false;
	private boolean isOpen = false;
	
	/**
	 * @param whichMode 添加或修改操作的标识  “0”是添加 “1”是修改
	 */
	public AddModeListAdapter(Context con,List<ProductFunVO> productFunVOList,FunDetail funDetail,int whichMode){
		this.con=con;
		this.productFunVOList=productFunVOList;
		this.funDetail = funDetail;
		this.whichMode = whichMode;
		this.mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				notifyDataSetChanged();
			}
			
		};
	}
	
	@Override
	public int getCount() {
		return this.productFunVOList == null ? 0 : productFunVOList.size();
	}

	@Override
	public Object getItem(int position) {
		return this.productFunVOList == null ? null : productFunVOList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public boolean getCheckStatus(){
		if (isChecked) {
			return true;
		}else{
			return false;
		}
	}
	
	public void setCheckStatus(boolean isChecked){
		this.isChecked = isChecked;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Holder holder;
		if(convertView==null){
			convertView=CommDefines.getSkinManager().view(R.layout.add_mode_list_item);
			holder=new Holder(convertView);
			convertView.setTag(holder);
		}else{
			holder=(Holder) convertView.getTag();
		}
		final ProductFunVO _productFunVO = this.productFunVOList.get(position);
		if(_productFunVO != null){
			holder.checkBox_selected.setChecked(_productFunVO.isSelected());
			
			if (holder.checkBox_selected.isChecked()) {
				isChecked = true;
			}else {
				isChecked = false;
			}
			holder.imageButton_open.setImageDrawable(CommDefines.getSkinManager().
					drawable(_productFunVO.isOpen() ?
							R.drawable.ico_swithch_on : R.drawable.ico_swithch_off));
			if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_UFO1) ||
					funDetail.getFunType().equals(ProductConstants.FUN_TYPE_HUMAN_CAPTURE_NEW2)){
				holder.imageButton_open.setVisibility(View.INVISIBLE);
			}
			holder.textView_name.setText(_productFunVO.getProductFun().getFunName());
			if(ProductConstants.FUN_TYPE_POWER_AMPLIFIER.equalsIgnoreCase(_productFunVO.getProductFun().getFunType())){
				   String alias = AppUtil.getFunDetailConfigByWhId(JxshApp.getContext(),_productFunVO.getProductFun().getWhId()).getAlias();
             if(!TextUtils.isEmpty(alias)){
          	   holder.textView_name.setText(alias);
             }
			}
			holder.checkBox_selected.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					_productFunVO.setSelected(holder.checkBox_selected.isChecked());
					if(_productFunVO.isSelected() && _productFunVO.getProductPatternOperation() == null){
						//如果选中设备功能，操作对象不存在则创建
						if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT)||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_AUTO_LOCK)||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_AUTO_CHA_ZUO)) {
							_productFunVO.setProductPatternOperation(new ProductPatternOperation(-1,
									_productFunVO.getProductFun().getFunId(), _productFunVO.getProductFun()
									.getWhId(), -1, funDetail.getShortcutClose(), null, null));
						}else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_COLOR_LIGHT)){
							_productFunVO.setProductPatternOperation(new ProductPatternOperation(-1,
									_productFunVO.getProductFun().getFunId(), _productFunVO.getProductFun()
									.getWhId(), -1, OPERATION_SET, OPERATION_COLOR_OFF, null));
						}else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_POP_LIGHT) ||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CRYSTAL_LIGHT) ||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CEILING_LIGHT) ||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_LIGHT_BELT)||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_WIRELESS_SEND_LIGHT)){
							_productFunVO.setProductPatternOperation(new ProductPatternOperation(-1,
									_productFunVO.getProductFun().getFunId(), _productFunVO.getProductFun()
									.getWhId(), -1, OPERATION_CLOSE, StringUtils.rgbToHsvJson(0, 0, 0), null));
						}else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_UFO1) ||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_HUMAN_CAPTURE_NEW2)){
							_productFunVO.setProductPatternOperation(new ProductPatternOperation(-1,
									_productFunVO.getProductFun().getFunId(), _productFunVO.getProductFun()
									.getWhId(), -1, OPERATION_SEND, _productFunVO.getProductFun().getFunUnit(), null));
						}else if(ProductConstants.FUN_TYPE_POWER_AMPLIFIER.equals(funDetail.getFunType())) {
							JSONObject jb = new JSONObject();
							try {
								jb.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT, "00000000");
								jb.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD, StaticConstant.INPUT_TYPE_USB);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							_productFunVO.setProductPatternOperation(new ProductPatternOperation(-1,
									_productFunVO.getProductFun().getFunId(), _productFunVO.getProductFun()
									.getWhId(), -1, OPERATION_PAUSE, jb.toString(), null));
						}else if(ProductConstants.FUN_TYPE_CURTAIN.equals(funDetail.getFunType())){
							_productFunVO.setProductPatternOperation(new ProductPatternOperation(-1,
									_productFunVO.getProductFun().getFunId(), _productFunVO.getProductFun()
									.getWhId(), -1, "close", null, null));
						}else if(ProductConstants.FUN_TYPE_WIRELESS_SOCKET.equals(funDetail.getFunType())){
							_productFunVO.setProductPatternOperation(new ProductPatternOperation(-1,
									_productFunVO.getProductFun().getFunId(), _productFunVO.getProductFun()
									.getWhId(), -1, "off", null, null));
						}else if(ProductConstants.FUN_TYPE_ONE_SWITCH.equals(funDetail.getFunType())){
							_productFunVO.setProductPatternOperation(new ProductPatternOperation(-1,
									_productFunVO.getProductFun().getFunId(), _productFunVO.getProductFun()
									.getWhId(), -1, "off", null, null));
						}else if(ProductConstants.FUN_TYPE_WIRELESS_AIRCODITION_OUTLET.equals(funDetail.getFunType())){
							_productFunVO.setProductPatternOperation(new ProductPatternOperation(-1,
									_productFunVO.getProductFun().getFunId(), _productFunVO.getProductFun()
									.getWhId(), -1, "off", null, null));
						}else if(ProductConstants.FUN_TYPE_DOUBLE_SWITCH.equals(funDetail.getFunType())){
//							JSONObject jb = new JSONObject();
//							try {
//								jb.put(StaticConstant.TYPE_SOCKET_LEFT, "off");
//								jb.put(StaticConstant.TYPE_SOCKET_RIGHT, "off");
//							} catch (JSONException e) {
//								e.printStackTrace();
//							}
							_productFunVO.setProductPatternOperation(new ProductPatternOperation(-1,
									_productFunVO.getProductFun().getFunId(), _productFunVO.getProductFun()
									.getWhId(), -1, OPERATION_DOUBLE_SOCKET, "", null));
						}else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_FIVE_SWITCH)){
//							JSONObject jb = new JSONObject();
//							try {
//								jb.put(StaticConstant.TYPE_SOCKET_KEY1, "off");
//								jb.put(StaticConstant.TYPE_SOCKET_KEY2, "off");
//								jb.put(StaticConstant.TYPE_SOCKET_KEY3, "off");
//								jb.put(StaticConstant.TYPE_SOCKET_KEY4, "off");
//								jb.put(StaticConstant.TYPE_SOCKET_KEY5, "off");
//							} catch (JSONException e) {
//								e.printStackTrace();
//							}
							_productFunVO.setProductPatternOperation(new ProductPatternOperation(-1,
									_productFunVO.getProductFun().getFunId(), _productFunVO.getProductFun()
									.getWhId(), -1, OPERATION_FIVE_SOCKET, "", null));
						}else if((funDetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_THREE)||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR)||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE)||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_SIX))){
//							JSONObject jb = new JSONObject();
//							try {
//								jb.put(StaticConstant.TYPE_SOCKET_KEY1, "off");
//								jb.put(StaticConstant.TYPE_SOCKET_KEY2, "off");
//								jb.put(StaticConstant.TYPE_SOCKET_KEY3, "off");
////								jb.put(StaticConstant.TYPE_SOCKET_KEY4, "off");
////								jb.put(StaticConstant.TYPE_SOCKET_KEY5, "off");
//							} catch (JSONException e) {
//								e.printStackTrace();
//							}
							_productFunVO.setProductPatternOperation(new ProductPatternOperation(-1,
									_productFunVO.getProductFun().getFunId(), _productFunVO.getProductFun()
									.getWhId(), -1, OPERATION_THREE_SOCKET, "", null));
						}else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH)||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE)||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR)||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE)||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO)||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE)){
//							JSONObject jb = new JSONObject();
//							try {
//								jb.put(StaticConstant.TYPE_SOCKET_KEY1, "off");
//								jb.put(StaticConstant.TYPE_SOCKET_KEY2, "off");
//							} catch (JSONException e) {
//								e.printStackTrace();
//							}
							_productFunVO.setProductPatternOperation(new ProductPatternOperation(-1,
									_productFunVO.getProductFun().getFunId(), _productFunVO.getProductFun()
									.getWhId(), -1, OPERATION_SIX_SOCKET, "", null));
						}else if(ProductConstants.FUN_TYPE_WIRELESS_CURTAIN.equals(funDetail.getFunType())){
							_productFunVO.setProductPatternOperation(new ProductPatternOperation(-1,
									_productFunVO.getProductFun().getFunId(), _productFunVO.getProductFun()
									.getWhId(), -1, "down", _productFunVO.getProductFun().getFunUnit(), null));
						}else if(ProductConstants.FUN_TYPE_ZG_LOCK.equals(funDetail.getFunType())){
							_productFunVO.setProductPatternOperation(new ProductPatternOperation(-1,
									_productFunVO.getProductFun().getFunId(), _productFunVO.getProductFun()
									.getWhId(), -1, "open", null, null));
						}else if(ProductConstants.FUN_TYPE_WIRELESS_INFRARED_TRANDPOND.equals(funDetail.getFunType())){//无线红外发送
							_productFunVO.setProductPatternOperation(new ProductPatternOperation(-1,
									_productFunVO.getProductFun().getFunId(), _productFunVO.getProductFun()
									.getWhId(), -1, "send", null, null));
						}
						
					}
				}
			});
			
			if (ProductConstants.FUN_TYPE_ZG_LOCK.equals(funDetail.getFunType()) ||
					funDetail.getFunType().equals(ProductConstants.FUN_TYPE_UFO1) ||
					funDetail.getFunType().equals(ProductConstants.FUN_TYPE_HUMAN_CAPTURE_NEW2)) {
				holder.imageButton_open.setVisibility(View.INVISIBLE);
			}else{
				holder.imageButton_open.setVisibility(View.VISIBLE);
			}
			
			holder.imageButton_open.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(_productFunVO.isSelected()){
							if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT)||
									funDetail.getFunType().equals(ProductConstants.FUN_TYPE_AUTO_LOCK)||
									funDetail.getFunType().equals(ProductConstants.FUN_TYPE_AUTO_CHA_ZUO)) {
								_productFunVO.setOpen(!_productFunVO.isOpen());
								_productFunVO.getProductPatternOperation().setOperation(_productFunVO.isOpen()
										? "open" : "close");
							}else if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_POWER_AMPLIFIER)) {
								
								if (!_productFunVO.isOpen()) {
									MusicSettingForPatternFragment fragment = new MusicSettingForPatternFragment();
									JSONObject jb = new JSONObject();
									try {
										jb.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT, "11111111");
										jb.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD, StaticConstant.INPUT_TYPE_USB);
									} catch (JSONException e) {
										e.printStackTrace();
									}
									_productFunVO.getProductPatternOperation().setParaDesc(jb.toString());
									Bundle bundle = new Bundle();
									bundle.putSerializable("funDetail", funDetail);
									bundle.putSerializable("productFunVO", _productFunVO);
									bundle.putInt("funId", _productFunVO.getProductFun().getFunId());
									fragment.setArguments(bundle);
									if (whichMode == 0) {
										fragmentTransaction = ((AddNewModeActivity)con).getSupportFragmentManager().beginTransaction();
									}else{
										fragmentTransaction = ((AmendModeNewActivity)con).getSupportFragmentManager().beginTransaction();
									}
									fragmentTransaction.add(R.id.light_fragment_layout, fragment);
									fragmentTransaction.addToBackStack(null);
									fragmentTransaction.commit();
								}else{
									JSONObject jb = new JSONObject();
									try {
										jb.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT, "00000000");
										jb.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD, StaticConstant.INPUT_TYPE_USB);
									} catch (JSONException e) {
										e.printStackTrace();
									}
									_productFunVO.getProductPatternOperation().setParaDesc(jb.toString());
								}
								_productFunVO.setOpen(!_productFunVO.isOpen());
								_productFunVO.getProductPatternOperation().setOperation(_productFunVO.isOpen()
										? "play" : "pause");
							}else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_COLOR_LIGHT)){
								if (!_productFunVO.isOpen()) {
									ModeLightsColorFragment fragment = new ModeLightsColorFragment();
									Bundle bundle = new Bundle();
									bundle.putSerializable("funDetail", funDetail);
									bundle.putSerializable("productFunVO", _productFunVO);
									fragment.setArguments(bundle);
									if (whichMode == 0) {
										fragmentTransaction = ((AddNewModeActivity)con).getSupportFragmentManager().beginTransaction();
									}else{
										fragmentTransaction = ((AmendModeNewActivity)con).getSupportFragmentManager().beginTransaction();
									}
									fragmentTransaction.add(R.id.light_fragment_layout, fragment);
									fragmentTransaction.addToBackStack(null);
									fragmentTransaction.commit();
								}else{
									_productFunVO.getProductPatternOperation().setParaDesc(OPERATION_COLOR_OFF);
								}
								_productFunVO.setOpen(!_productFunVO.isOpen());
								_productFunVO.getProductPatternOperation().setOperation(OPERATION_SET);
							
						}else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_POP_LIGHT) ||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CRYSTAL_LIGHT) ||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CEILING_LIGHT) ||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_LIGHT_BELT)||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_WIRELESS_SEND_LIGHT)){
							Fragment fragment = null;
							if (!_productFunVO.isOpen()) {
								if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_LIGHT_BELT)) {
									fragment = new ModeLightBeltFragment();
								}else{
									fragment = new ModePopLightFragment();
								}
								Bundle bundle = new Bundle();
								bundle.putSerializable("funDetail", funDetail);
								bundle.putSerializable("productFunVO", _productFunVO);
								fragment.setArguments(bundle);
								if (whichMode == 0) {
									fragmentTransaction = ((AddNewModeActivity)con).getSupportFragmentManager().beginTransaction();
								}else{
									fragmentTransaction = ((AmendModeNewActivity)con).getSupportFragmentManager().beginTransaction();
								}
								fragmentTransaction.add(R.id.light_fragment_layout, fragment);
								fragmentTransaction.addToBackStack(null);
								fragmentTransaction.commit();
							}else{
								String operation = StringUtils.rgbToHsvJson(0, 0, 0);
								_productFunVO.getProductPatternOperation().setParaDesc(operation);
							}
							_productFunVO.setOpen(!_productFunVO.isOpen());
							_productFunVO.getProductPatternOperation().setOperation(
									_productFunVO.isOpen() ? OPERATION_HUEANDSAT : OPERATION_CLOSE);
						}else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_UFO1) ||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_HUMAN_CAPTURE_NEW2)){
							_productFunVO.setOpen(true);
							_productFunVO.getProductPatternOperation().setParaDesc(_productFunVO.getProductFun().getFunUnit());
						}else if(ProductConstants.FUN_TYPE_CURTAIN.equals(funDetail.getFunType())){
							_productFunVO.setOpen(!_productFunVO.isOpen());
							_productFunVO.getProductPatternOperation().setOperation(_productFunVO.isOpen()
									? "open" : "close");
						}else if(ProductConstants.FUN_TYPE_WIRELESS_INFRARED_TRANDPOND.equals(funDetail.getFunType())){//无线红外发送
							_productFunVO.setOpen(!_productFunVO.isOpen());
							_productFunVO.getProductPatternOperation().setOperation("send");
						}else if(ProductConstants.FUN_TYPE_WIRELESS_SOCKET.equals(funDetail.getFunType())){
							_productFunVO.setOpen(!_productFunVO.isOpen());
							_productFunVO.getProductPatternOperation().setOperation(_productFunVO.isOpen()
									? "on" : "off");
						}else if(ProductConstants.FUN_TYPE_ONE_SWITCH.equals(funDetail.getFunType())){
							_productFunVO.setOpen(!_productFunVO.isOpen());
							_productFunVO.getProductPatternOperation().setOperation(_productFunVO.isOpen()
									? "on" : "off");
						}else if(ProductConstants.FUN_TYPE_WIRELESS_AIRCODITION_OUTLET.equals(funDetail.getFunType())){
							_productFunVO.setOpen(!_productFunVO.isOpen());
							_productFunVO.getProductPatternOperation().setOperation(_productFunVO.isOpen()
									? "on" : "off");
						}else if(ProductConstants.FUN_TYPE_DOUBLE_SWITCH.equals(funDetail.getFunType())){
							if (!_productFunVO.isOpen()) {
								ModeDoubleSocketFragment fragment = new ModeDoubleSocketFragment();
								Bundle bundle = new Bundle();
								bundle.putSerializable("funDetail", funDetail);
								bundle.putSerializable("productFunVO", _productFunVO);
								fragment.setArguments(bundle);
								if (whichMode == 0) {
									fragmentTransaction = ((AddNewModeActivity)con).getSupportFragmentManager().beginTransaction();
								}else{
									fragmentTransaction = ((AmendModeNewActivity)con).getSupportFragmentManager().beginTransaction();
								}
								fragmentTransaction.add(R.id.light_fragment_layout, fragment);
								fragmentTransaction.addToBackStack(null);
								fragmentTransaction.commit();
							}
							_productFunVO.setOpen(!_productFunVO.isOpen());
							_productFunVO.getProductPatternOperation().setOperation(OPERATION_DOUBLE_SOCKET);
						}else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH)||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE)||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR)||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE)||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO)||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE)){
							if (!_productFunVO.isOpen()) {
								ModeDoubleSocketFragment fragment = new ModeDoubleSocketFragment();
								Bundle bundle = new Bundle();
								bundle.putSerializable("funDetail", funDetail);
								bundle.putSerializable("productFunVO", _productFunVO);
								fragment.setArguments(bundle);
								if (whichMode == 0) {
									fragmentTransaction = ((AddNewModeActivity)con).getSupportFragmentManager().beginTransaction();
								}else{
									fragmentTransaction = ((AmendModeNewActivity)con).getSupportFragmentManager().beginTransaction();
								}
								fragmentTransaction.add(R.id.light_fragment_layout, fragment);
								fragmentTransaction.addToBackStack(null);
								fragmentTransaction.commit();
							}
							_productFunVO.setOpen(!_productFunVO.isOpen());
							_productFunVO.getProductPatternOperation().setOperation(OPERATION_SIX_SOCKET);
						}else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_THREE)||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR)||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE)||
								funDetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_SIX)){
							if (!_productFunVO.isOpen()) {
								ModeThreeSocketFragment fragment = new ModeThreeSocketFragment();
								Bundle bundle = new Bundle();
								bundle.putSerializable("funDetail", funDetail);
								bundle.putSerializable("productFunVO", _productFunVO);
								fragment.setArguments(bundle);
								if (whichMode == 0) {
									fragmentTransaction = ((AddNewModeActivity)con).getSupportFragmentManager().beginTransaction();
								}else{
									fragmentTransaction = ((AmendModeNewActivity)con).getSupportFragmentManager().beginTransaction();
								}
								fragmentTransaction.add(R.id.light_fragment_layout, fragment);
								fragmentTransaction.addToBackStack(null);
								fragmentTransaction.commit();
							}
							_productFunVO.setOpen(!_productFunVO.isOpen());
							_productFunVO.getProductPatternOperation().setOperation(OPERATION_THREE_SOCKET);
						}else if(ProductConstants.FUN_TYPE_WIRELESS_CURTAIN.equals(funDetail.getFunType())){
							_productFunVO.setOpen(!_productFunVO.isOpen());
							_productFunVO.getProductPatternOperation().setOperation(_productFunVO.isOpen()
									? "open" : "down");
						}else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_FIVE_SWITCH)){
							if (!_productFunVO.isOpen()) {
								ModeFiveSocketFragment fragment = new ModeFiveSocketFragment();
								Bundle bundle = new Bundle();
								bundle.putSerializable("funDetail", funDetail);
								bundle.putSerializable("productFunVO", _productFunVO);
								fragment.setArguments(bundle);
								if (whichMode == 0) {
									fragmentTransaction = ((AddNewModeActivity)con).getSupportFragmentManager().beginTransaction();
								}else{
									fragmentTransaction = ((AmendModeNewActivity)con).getSupportFragmentManager().beginTransaction();
								}
								fragmentTransaction.add(R.id.light_fragment_layout, fragment);
								fragmentTransaction.addToBackStack(null);
								fragmentTransaction.commit();
							}
							_productFunVO.setOpen(!_productFunVO.isOpen());
							_productFunVO.getProductPatternOperation().setOperation(OPERATION_FIVE_SOCKET);
						}
					}
					mHandler.sendEmptyMessage(0);
				}
			});
		}
		return convertView;
	}

	 class Holder{
		CheckBox checkBox_selected;
		TextView textView_name;
		ImageButton imageButton_open;
		LinearLayout linearItem;
		public Holder(View view){
			checkBox_selected=(CheckBox) view.findViewById(R.id.checkBox_selected);
			textView_name=(TextView) view.findViewById(R.id.textView_name);
			imageButton_open=(ImageButton) view.findViewById(R.id.imageButton_open);
			linearItem=(LinearLayout) view.findViewById(R.id.linearItem);
		}
	}
}
