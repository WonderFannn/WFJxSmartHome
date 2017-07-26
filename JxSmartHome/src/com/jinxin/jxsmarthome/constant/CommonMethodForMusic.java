//package com.jinxin.jxsmarthome.constant;
//
//import java.io.UnsupportedEncodingException;
//import java.nio.ByteBuffer;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import android.content.Context;
//import com.jinxin.jxsmarthome.util.Logger;
//
//import com.jinxin.db.impl.CustomerDaoImpl;
//import com.jinxin.db.impl.CustomerPatternDaoImpl;
//import com.jinxin.db.impl.CustomerProductCMDDaoImpl;
//import com.jinxin.db.impl.CustomerProductDaoImpl;
//import com.jinxin.db.impl.FunDetailConfigDaoImpl;
//import com.jinxin.db.impl.FunDetailDaoImpl;
//import com.jinxin.db.impl.ProductFunDaoImpl;
//import com.jinxin.db.impl.ProductPatternOperationDaoImpl;
//import com.jinxin.jxsmarthome.entity.Customer;
//import com.jinxin.jxsmarthome.entity.CustomerPattern;
//import com.jinxin.jxsmarthome.entity.CustomerProduct;
//import com.jinxin.jxsmarthome.entity.CustomerProductCMD;
//import com.jinxin.jxsmarthome.entity.CustomerProductDevice;
//import com.jinxin.jxsmarthome.entity.CustomerProductType;
//import com.jinxin.jxsmarthome.entity.FunDetail;
//import com.jinxin.jxsmarthome.entity.FunDetailConfig;
//import com.jinxin.jxsmarthome.entity.ProductFun;
//import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
//import com.jinxin.jxsmarthome.fragment.MusicFragment;
//import com.jinxin.jxsmarthome.util.Logger;
//
///**
// * 公共方法
// * 
// * @author JackeyZhang
// * @company 金鑫智慧
// */
//@SuppressWarnings("deprecation")
//public class CommonMethodForMusic {
//	private static List<CustomerProductType> customerProductTypeList = null;
//	private static List<ProductFun> typeProductFunList = null;
//
//	/**
//	 * 更新客户设备信息
//	 * 
//	 * @param customerProductList
//	 */
//	public static void updateCustomerProduct(Context context,
//			List<CustomerProduct> customerProductList) {
//		if (context == null || customerProductList == null)
//			return;
//		CustomerProductDaoImpl cpDaoImpl = new CustomerProductDaoImpl(context);
//		if (cpDaoImpl != null) {
//			for (CustomerProduct cp : customerProductList) {
//				List<CustomerProduct> _cpList = cpDaoImpl.find(null, "whId=?",
//						new String[] { cp.getWhId() }, null, null, null, null);
//				if (_cpList == null || _cpList.size() <= 0) {
//					// 设备不存在插入（主键自增）
//					cpDaoImpl.insert(cp, true);
//				} else {
//					// 设备存在，修改主键ID更新
//					CustomerProduct _cp = _cpList.get(0);// 由设备序列号筛选出的，应该是只有一项
//					if (_cp != null) {
//						cp.setId(_cp.getId());
//						cpDaoImpl.update(cp);
//					}
//				}
//			}
//		}
//	}
//
//	/**
//	 * 更新产品功能操作列表
//	 * 
//	 * @param customerProductList
//	 */
//	public static void updateProductFunList(Context context,
//			List<ProductFun> productFunList) {
//		if (context == null || productFunList == null)
//			return;
//		ProductFunDaoImpl pfDaoImpl = new ProductFunDaoImpl(context);
//		if (pfDaoImpl != null) {
//			for (ProductFun pf : productFunList) {
//				if (pf != null) {
//					if (pfDaoImpl.isExist(
//							"select funId from product_fun where funId='"
//									+ pf.getFunId() + "'", null)) {
//						pfDaoImpl.update(pf);
//					} else {
//						pfDaoImpl.insert(pf, false);
//					}
//				}
//			}
//		}
//	}
//
//	/**
//	 * 更新客户模式
//	 * 
//	 * @param customerPatternList
//	 */
//	public static void updateCustomerPattern(Context context,
//			List<CustomerPattern> customerPatternList) {
//		if (context == null || customerPatternList == null)
//			return;
//		CustomerPatternDaoImpl cpDaoImpl = new CustomerPatternDaoImpl(context);
//		if (cpDaoImpl != null) {
//			for (CustomerPattern cp : customerPatternList) {
//				if (cp != null) {
//					if (cpDaoImpl.isExist(
//							"select patternId from customer_pattern where patternId='"
//									+ cp.getPatternId() + "'", null)) {
//						cpDaoImpl.update(cp);
//					} else {
//						cpDaoImpl.insert(cp, false);
//					}
//				}
//			}
//		}
//	}
//
//	/**
//	 * 更新产品模式操作
//	 * 
//	 * @param customerPatternCMDList
//	 */
//	public static void updateProductPatternOperationList(Context context,
//			List<ProductPatternOperation> productPatternOperationList) {
//		if (context == null || productPatternOperationList == null)
//			return;
//		ProductPatternOperationDaoImpl ppoDaoImpl = new ProductPatternOperationDaoImpl(
//				context);
//		if (ppoDaoImpl != null) {
//			for (ProductPatternOperation ppo : productPatternOperationList) {
//				if (ppo != null) {
//					if (ppoDaoImpl.isExist(
//							"select id from product_pattern_operation where id='"
//									+ ppo.getId() + "'", null)) {
//						ppoDaoImpl.update(ppo);
//					} else {
//						ppoDaoImpl.insert(ppo, false);
//					}
//				}
//			}
//		}
//	}
//
//	/**
//	 * 更新产品功能明细
//	 * 
//	 * @param funDetailList
//	 */
//	public static void updateFunDetailList(Context context,
//			List<FunDetail> funDetailList) {
//		if (context == null || funDetailList == null)
//			return;
//		FunDetailDaoImpl fdDaoImpl = new FunDetailDaoImpl(context);
//		if (fdDaoImpl != null) {
//			for (FunDetail fd : funDetailList) {
//				if (fd != null) {
//					if (fdDaoImpl.isExist(
//							"select id from fun_detail where id='" + fd.getId()
//									+ "'", null)) {
//						fdDaoImpl.update(fd);
//					} else {
//						fdDaoImpl.insert(fd, false);
//					}
//				}
//			}
//		}
//	}
//	/**
//	 * 更新产品功能明细配置参数
//	 * 
//	 * @param funDetailList
//	 */
//	public static void updateFunDetailConfigList(Context context,
//			List<FunDetailConfig> funDetailConfigs) {
//		if (context == null || funDetailConfigs == null)
//			return;
//		FunDetailConfigDaoImpl fdDaoImpl = new FunDetailConfigDaoImpl(context);
//		if (fdDaoImpl != null) {
//			for (FunDetailConfig fdc : funDetailConfigs) {
//				if (fdc != null) {
//					if (fdDaoImpl.isExist(
//							"select whId from fun_detail_config where whId='" + fdc.getWhId()
//							+ "'", null)) {
//						fdDaoImpl.update(fdc);
//					} else {
//						fdDaoImpl.insert(fdc, true);
//					}
//				}
//			}
//		}
//	}
//
//	/**
//	 * 初始化设备/类型/命令
//	 * 
//	 * @deprecated
//	 */
//	public static void initDeviceAndCMD(Context context) {
//		splitDeviceType(context);
//		for (CustomerProductType cpt : customerProductTypeList) {
//			if (cpt != null) {
//				cpt.setCustomerProductDeviceList(currentTypeFillingDeviceAndCMD(
//						context, cpt.getCode(), cpt.getIcon()));
//			}
//		}
//	}
//
//	/**
//	 * @deprecated 初始化类型设备
//	 */
//	public static void initTypeDevice(Context context) {
//		splitTypeDevice(context);
//	}
//
//	/**
//	 * 当前类型填充设备及指令
//	 * 
//	 * @deprecated
//	 * @param context
//	 * @param code
//	 */
//	private static List<CustomerProductDevice> currentTypeFillingDeviceAndCMD(
//			Context context, String code, String icon) {
//		CustomerProductCMDDaoImpl cpcmdDaoImpl = new CustomerProductCMDDaoImpl(
//				context);
//		List<CustomerProductCMD> customerProductCMDList = cpcmdDaoImpl.find(
//				null, "code=?", new String[] { code }, null, null, null, null);
//		List<CustomerProductDevice> customerProductDeviceList = new ArrayList<CustomerProductDevice>();
//		if (customerProductCMDList != null) {
//			// 设备填充
//			for (CustomerProductCMD cpCMD : customerProductCMDList) {
//				CustomerProductDevice cpd = isWhIdExistCurrentType(
//						customerProductDeviceList, cpCMD.getWhId());
//				if (cpd == null) {
//					// 不存在该设备
//					customerProductDeviceList.add(new CustomerProductDevice(
//							cpCMD.getCode(), cpCMD.getCmdName(), icon,
//							new ArrayList<CustomerProductCMD>(), cpCMD
//									.getWhId()));
//				} else {
//					// 已存在该设备
//					List<CustomerProductCMD> _list = cpd
//							.getCustomerProductCMDList();
//					if (_list != null) {
//						_list.add(cpCMD);
//					}
//				}
//			}
//		}
//		return customerProductDeviceList;
//	}
//
//	/**
//	 * 当前类型填充设备
//	 * 
//	 * @param context
//	 * @param funType
//	 */
//	public static List<ProductFun> currentTypeFillingDevice(Context context,
//			String funType) {
//		ProductFunDaoImpl pfDaoImpl = new ProductFunDaoImpl(context);
//		List<ProductFun> productFunList = pfDaoImpl.find(null, "funType=?",
//				new String[] { funType }, null, null, null, null);
//		return productFunList;
//	}
//
//	/**
//	 * 当前模式填充操作
//	 * 
//	 * @param context
//	 * @param patternId
//	 */
//	public static List<ProductPatternOperation> currentModeFillingOperation(
//			Context context, String patternId) {
//		ProductPatternOperationDaoImpl ppoDaoImpl = new ProductPatternOperationDaoImpl(
//				context);
//		List<ProductPatternOperation> productPatternOperationList = ppoDaoImpl
//				.find(null, "patternId=?", new String[] { patternId }, null,
//						null, null, null);
//		return productPatternOperationList;
//	}
//
//	/**
//	 * @deprecated 当前设备是否已经生成于该类型列表中（存在返回设备对象）
//	 * @param list
//	 * @param whId
//	 */
//	private static CustomerProductDevice isWhIdExistCurrentType(
//			List<CustomerProductDevice> list, String whId) {
//		if (list == null || whId == null)
//			return null;
//		for (CustomerProductDevice cpd : list) {
//			if (whId.equals(cpd.getWhId())) {
//				return cpd;
//			}
//		}
//		return null;
//	}
//
//	/**
//	 * 设备分类
//	 * 
//	 * @deprecated
//	 */
//	private static void splitDeviceType(Context context) {
//		if (context == null)
//			return;
//		CustomerProductDaoImpl cpDaoImpl = new CustomerProductDaoImpl(context);
//		List<CustomerProduct> customerProductList = cpDaoImpl.find();
//		if (customerProductList != null) {
//			customerProductTypeList = new ArrayList<CustomerProductType>();
//			for (CustomerProduct cp : customerProductList) {
//				if (cp != null) {
//					if (!isExistCustomerProductType(cp.getCode())) {
//						customerProductTypeList.add(new CustomerProductType(cp
//								.getCode(), cp.getIcon(), cp.getTypeName()));
//					}
//
//				}
//			}
//		}
//	}
//
//	/**
//	 * 该设备类型是否已存在
//	 * 
//	 * @deprecated
//	 * @param code
//	 * @return
//	 */
//	private static boolean isExistCustomerProductType(String code) {
//		for (CustomerProductType cpt : customerProductTypeList) {
//			if (cpt != null) {
//				if (cpt.getCode().equals(code)) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}
//
//	/**
//	 * @deprecated 设备分类
//	 */
//	private static void splitTypeDevice(Context context) {
//		if (context == null)
//			return;
//		ProductFunDaoImpl pfDaoImpl = new ProductFunDaoImpl(context);
//		List<ProductFun> productFunList = pfDaoImpl.find();
//		if (productFunList != null) {
//			typeProductFunList = new ArrayList<ProductFun>();
//			for (ProductFun pf : productFunList) {
//				if (pf != null) {
//					if (!isExistTypeProductFun(pf.getFunType())) {
//						typeProductFunList.add(pf);
//					}
//
//				}
//			}
//		}
//	}
//
//	/**
//	 * 该类型设备是否已存在
//	 * 
//	 * @deprecated
//	 * @param funType
//	 * @return
//	 */
//	private static boolean isExistTypeProductFun(String funType) {
//		for (ProductFun pf : typeProductFunList) {
//			if (pf != null) {
//				if (pf.getFunType().equals(funType)) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}
//
//	public static List<CustomerProductType> getCustomerProductTypeList() {
//		return customerProductTypeList;
//	}
//
//	public static void setCustomerProductTypeList(List<CustomerProductType> customerProductTypeList) {
//		customerProductTypeList = customerProductTypeList;
//	}
//
//	/**
//	 * 取用户设备类型列表（每种类型存放一个排在最前的该设备对象）
//	 * 
//	 * @return
//	 */
//	public static List<ProductFun> getTypeProductFunList() {
//		return typeProductFunList;
//	}
//
//	public static void setTypeProductFunList(List<ProductFun> typeProductFunList) {
//		CommonMethodForMusic.typeProductFunList = typeProductFunList;
//	}
//	/**
//	 * 设备单路操作生成指令字节码
//	 * @param con
//	 * @param productFun
//	 * @return
//	 */
//	public static List<byte[]> productFunToCMD(Context con,ProductFun productFun,FunDetail funDetail, 
//			Integer index){
//		if (productFun == null || funDetail == null)return null;
//		///////test窗帘/////////
//		if(funDetail.getFunType().equals("00101")){
//			funDetail.setShortcutOpen("open");
//			funDetail.setShortcutClose("close");
//		}
//		else if(funDetail.getFunType().equals("00102")){
//			funDetail.setShortcutOpen("up");
//			funDetail.setShortcutClose("down");
//		}else if (funDetail.getFunType().equals("006")) {
//			funDetail.setShortcutOpen("open");
//			funDetail.setShortcutClose("close");
//		}
//		////////////////////////
//		String type = "1111111"; //productFun.isOpen() ? funDetail.getShortcutClose() : funDetail.getShortcutOpen();
//		List <String> lightNumbers = new ArrayList<String>();
//		lightNumbers.add(productFun.getFunUnit());
//		CustomerProductDaoImpl cpDaoImpl = new CustomerProductDaoImpl(con);
//		List<CustomerProduct> cpList = cpDaoImpl.find(null, "whId=?", new String[]{productFun.getWhId()}, null, null, null, null);
//		String addr485 = "";
//		if(cpList != null &&  cpList.size() > 0){
//			CustomerProduct cp = cpList.get(0);
//			if(cp != null){
//				addr485 = cp.getAddress485();
//			}
//		}
//		String customerId = "";
//		CustomerDaoImpl cDaoImpl = new CustomerDaoImpl(con);
//		 List<Customer> cList = cDaoImpl.find();
//		 if(cList != null && cList.size() > 0){
//			 Customer c = cList.get(0);
//			 if(c != null){
//				 customerId = c.getCustomerId();
//			 }
//		 }
//		List<byte[]> cmd = CommonMethodForMusic.createCmdWithLength(customerId,addr485, type, lightNumbers,
//				productFun.getFunType(), index);
//		return cmd;
//	}
//	/**
//	 * 模式功能操作生成指令字节码
//	 * @param ppo
//	 * @return
//	 */
//	public static List<byte[]> productPatternOperationToCMD(Context con,ProductPatternOperation ppo, Integer index){
//		if(ppo == null)return null;
//		String _operation = ppo.getOperation();
//		String addr485 = "";
//		String customerId = "";
//		String funType = "";
//		List <String> funUnits = new ArrayList<String>();
//		ProductFunDaoImpl _pfDaoImpl = new ProductFunDaoImpl(con);
//		 ProductFun _pf = _pfDaoImpl.get(ppo.getFunId());
//		 if(_pf != null){
//			 funType = _pf.getFunType();
//			 funUnits.add(_pf.getFunUnit());
//			 CustomerProductDaoImpl cpDaoImpl = new CustomerProductDaoImpl(con);
//			 List<CustomerProduct> cpList = cpDaoImpl.find(null, "whId=?", new String[]{_pf.getWhId()}, null, null, null, null);
//			 if(cpList != null &&  cpList.size() > 0){
//					CustomerProduct cp = cpList.get(0);
//					if(cp != null){
//						addr485 = cp.getAddress485();
//					}
//				}
//		 }
//		 CustomerDaoImpl cDaoImpl = new CustomerDaoImpl(con);
//		 List<Customer> cList = cDaoImpl.find();
//		 if(cList != null && cList.size() > 0){
//			 Customer c = cList.get(0);
//			 if(c != null){
//				 customerId = c.getCustomerId();
//			 }
//		 }
//		 List<byte[]> cmd = CommonMethodForMusic.createCmdWithLength(customerId,addr485, _operation, funUnits, funType, index);
//		return cmd;
//	}
//
//	/***** 指令生成测试 *********************/
//	/**
//	 * 灯
//	 * @param addr485
//	 * @param type
//	 * @param lightNumbers
//	 * @return
//	 */
//	private static String generateLightCmd(String addr485, String type,
//			List<String> lightNumbers) {
//		StringBuffer sb = new StringBuffer("user20");
//		if (type.equals("open")) {
//			sb.append("open");
//		} else if (type.equals("close")) {
//			sb.append("clos");
//		} else if (type.equals("ackA")) {
//			sb.append("readA");
//		}
//		sb.append(addr485);
//		sb.append(':');
//
//		if (type.equals("ackA")) {
//			// sb.append( "usermi" );
//			// sb.append( lightNumber );
//			// sb.append( "d" );
//		} else {
//			sb.append("c");
//			for (int i = 1; i <= 8; i++) {
//				if (lightNumbers.contains(new Integer(i).toString())) {
//					sb.append('1');
//				} else {
//					sb.append('0');
//				}
//			}
//		}
//
//		// equipmentService.findByEntity(t)
//		// user20Statefeedback046:c00000001XXXXXX
//		// return "user20open002:c00000001123456";
//		return sb.toString();
//	}
//	/**
//	 * 
//	 * @param addr485
//	 * @param type
//	 * @param unitNumbers
//	 * @return
//	 */
//	public static String generateCurtainCmd ( String addr485 , String type , List <String> unitNumbers ) {
//		StringBuffer sb = new StringBuffer( "user20" );
//		if ( type.equals( "up" ) ) {
//			sb.append( "open" );
//		} else if ( type.equals( "down" ) ) {
//			sb.append( "open" );
//		} else if ( type.equals( "stop" ) ) {
//			sb.append( "clos" );
//		}
//		sb.append( addr485 );
//		sb.append( ':' );
//		sb.append( "c" );
//		for ( int i = 1; i <= 4; i++ ) {
//			if ( unitNumbers.contains( new Integer( i ).toString() ) ) {
//				if ( type.equals( "up" ) ) {
//					sb.append( "10" );
//				} else if ( type.equals( "down" ) ) {
//					sb.append( "01" );
//				} else if ( type.equals( "stop" ) ) {
//					sb.append( "11" );
//				}
//			} else {
//				sb.append( "00" );
//			}
//		}
//		return sb.toString();
//	}
//
//
//	@SuppressWarnings("unchecked")
//	private static List<String> createCmdWithAccount(String account, String addr485,
//			String type, List<String> lightNumbers,String funType, Integer index) {
//		Map map = MusicFragment.params;
//		
//		List<String> _cmdList = null;
//		map.put("units", lightNumbers);
//		
//		if(funType.equals(ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT)){//灯
////			sb.append(generateLightCmd(addr485, type, lightNumbers));
//			_cmdList = GenMCLight.generateCmd(type, addr485, map);
////			sb.append(strList.get(0).toString());
//		}else if(funType.equals(ProductConstants.FUN_TYPE_MASTER_CONTROLLER_CURTAIN)){//窗帘
////			sb.append(generateCurtainCmd(addr485, type, lightNumbers));
//			_cmdList = GenMCCurtain.generateCmd(type, addr485, map);
////			if (strList.size()>0) {
////				sb.append(strList.get(0).toString());
////			}
//		}else if(funType.equals(ProductConstants.FUN_TYPE_HUMAN_CAPTURE)){//人体感应
//			
//		}else if(funType.equals(ProductConstants.FUN_TYPE_TFT_LIGHT)){//触摸屏
//			
//		}else if(funType.equals(ProductConstants.FUN_TYPE_POWER_AMPLIFIER)){//功放
//			
//		}else if(funType.equals(ProductConstants.POWER_AMPLIFIER_SOUND_PLAY)){ // 功放- 播放
////			map.put(GenPowerAmplifier.PARAM_MUSIC_SELECT_USB_OR_SD, GenPowerAmplifier.INPUT_TYPE_USB);
////			map.put(GenPowerAmplifier.PARAM_MUSIC_ROAD_LINE_SELECT, "01000000");
//			_cmdList = GenPowerAmplifier.generateCmd(GenPowerAmplifier.OPERATE_PLAY, addr485, map);
//			if (_cmdList.size()>0) {
//				Logger.debug("tangl.play", _cmdList.get(0).toString());
//			}else {
//				Logger.debug("tangl.play", "commd null");
//			}
//		}else if(funType.equals(ProductConstants.POWER_AMPLIFIER_SOUND_PAUSE)){ // 功放- 停止
////			map.put(GenPowerAmplifier.PARAM_MUSIC_SELECT_USB_OR_SD, GenPowerAmplifier.INPUT_TYPE_USB);
////			map.put(GenPowerAmplifier.PARAM_MUSIC_ROAD_LINE_SELECT, "01000000");
//			_cmdList = GenPowerAmplifier.generateCmd(GenPowerAmplifier.OPERATE_PAUSE, addr485, map);
//			if (_cmdList.size()>0) {
//				Logger.debug("tangl.pause", _cmdList.get(0).toString());
//			}else {
//				Logger.debug("tangl.pause", "commd null");
//			}
//		}else if(funType.equals(ProductConstants.POWER_AMPLIFIER_MUTE)) {	// 功放- 静音
////			map.put(GenPowerAmplifier.PARAM_MUSIC_ROAD_LINE_SELECT, "01000000");
//			_cmdList = GenPowerAmplifier.generateCmd(GenPowerAmplifier.OPERATE_MUTE, addr485, map);
//			if (_cmdList.size()>0) {
//				Logger.debug("tangl.mute", _cmdList.get(0).toString());
//			}else {
//				Logger.debug("tangl.mute", "commd null");
//			}	
//		}else if(funType.equals(ProductConstants.POWER_AMPLIFIER_UNMUTE)) {	// 功放- 取消静音
////			map.put(GenPowerAmplifier.PARAM_MUSIC_ROAD_LINE_SELECT, "01000000");
//			_cmdList = GenPowerAmplifier.generateCmd(GenPowerAmplifier.OPERATE_UNMUTE, addr485, map);
//			if (_cmdList.size()>0) {
//				Logger.debug("tangl.unmute", _cmdList.get(0).toString());
//			}else {
//				Logger.debug("tangl.unmute", "commd null");
//			}	
//		}else if(funType.equals(ProductConstants.POWER_AMPLIFIER_PROVIOUS)){ // 功放- 上一曲
//			map.put(GenPowerAmplifier.PARAM_MUSIC_SELECT_USB_OR_SD, GenPowerAmplifier.INPUT_TYPE_USB);
////			map.put(GenPowerAmplifier.PARAM_MUSIC_ROAD_LINE_SELECT, "01000000");
//			_cmdList = GenPowerAmplifier.generateCmd(GenPowerAmplifier.OPERATE_PRE, addr485, map);
//			if (_cmdList.size()>0) {
//				Logger.debug("tangl.privous", _cmdList.get(0).toString());
//			}else {
//				Logger.debug("tangl.privous", "commd null");
//			}
//		}else if(funType.equals(ProductConstants.POWER_AMPLIFIER_NEXT)){ // 公放- 下一曲
////			map.put(GenPowerAmplifier.PARAM_MUSIC_SELECT_USB_OR_SD, GenPowerAmplifier.INPUT_TYPE_USB);
////			map.put(GenPowerAmplifier.PARAM_MUSIC_ROAD_LINE_SELECT, "01000000");
//			_cmdList = GenPowerAmplifier.generateCmd(GenPowerAmplifier.OPERATE_NEXT, addr485, map);
//			if (_cmdList.size()>0) {
//				Logger.debug("tangl.next", _cmdList.get(0).toString());
//			}else {
//				Logger.debug("tangl.next", "commd null");
//			}
//		}else if(funType.equals(ProductConstants.POWER_AMPLIFIER_SOUND_ADD)){ // 公放- 音量 +
////			map.put(GenPowerAmplifier.PARAM_MUSIC_SELECT_USB_OR_SD, GenPowerAmplifier.INPUT_TYPE_USB);
////			map.put(GenPowerAmplifier.PARAM_MUSIC_ROAD_LINE_SELECT, "01000000");
//			_cmdList = GenPowerAmplifier.generateCmd(GenPowerAmplifier.OPERATE_SOUND_ADD, addr485, map);
//			if (_cmdList.size()>0) {
//				Logger.debug("tangl.sound+", _cmdList.get(0).toString());
//			}else {
//				Logger.debug("tangl.sound+", "commd null");
//			}
//		}else if(funType.equals(ProductConstants.POWER_AMPLIFIER_SOUND_SUB)){ // 公放- 音量 -
//			map.put(GenPowerAmplifier.PARAM_MUSIC_SELECT_USB_OR_SD, GenPowerAmplifier.INPUT_TYPE_USB);
////			map.put(GenPowerAmplifier.PARAM_MUSIC_ROAD_LINE_SELECT, "01000000");
//			_cmdList = GenPowerAmplifier.generateCmd(GenPowerAmplifier.OPERATE_SOUND_SUB, addr485, map);
//			if (_cmdList.size()>0) {
//				Logger.debug("tangl.sound-", _cmdList.get(0).toString());
//			}else {
//				Logger.debug("tangl.sound-", "commd null");
//			}
//		}else if(funType.equals(ProductConstants.POWER_AMPLIFIER_SOUND_LIST)){ // 公放- 获取播放列表
////			map.put(GenPowerAmplifier.PARAM_MUSIC_SELECT_USB_OR_SD, GenPowerAmplifier.INPUT_TYPE_USB);
////			map.put(GenPowerAmplifier.PARAM_MUSIC_ROAD_LINE_SELECT, "01000000");
//			_cmdList = GenPowerAmplifier.genCmdCount(addr485, map);
//			if (_cmdList.size()>0) {
//				Logger.debug("tangl.songlist", _cmdList.get(0).toString());
//			}else {
//				Logger.debug("tangl.songlist", "commd null");
//			}
//		}else if(funType.equals(ProductConstants.POWER_AMPLIFIER_SOUND_SONG)){ // 公放- 获取播放曲目
//			Logger.debug("tangl.song", "invoked!");
////			map.put(GenPowerAmplifier.PARAM_MUSIC_SELECT_USB_OR_SD, GenPowerAmplifier.INPUT_TYPE_USB);
//			map.put(GenPowerAmplifier.PARAM_MUSIC_INDEX, index);
//			_cmdList = GenPowerAmplifier.generateCmd(GenPowerAmplifier.OPERATE_READ_NAME, addr485, map);
//			if (_cmdList.size()>0) {
//				Logger.debug("tangl.song", _cmdList.get(0).toString());
//			}else {
//				Logger.debug("tangl.song", "commd null");
//			}
//		}else if(funType.equals(ProductConstants.POWER_AMPLIFIER_SOUND_PLAY_SONG)){ // 公放- 播放指定歌曲
//			Logger.debug("tangl.song", "invoked!");
////			map.put(GenPowerAmplifier.PARAM_MUSIC_SELECT_USB_OR_SD, GenPowerAmplifier.INPUT_TYPE_USB);
//			map.put(GenPowerAmplifier.PARAM_MUSIC_INDEX, index);
////			map.put(GenPowerAmplifier.PARAM_MUSIC_ROAD_LINE_SELECT, "01000000");
//			_cmdList = GenPowerAmplifier.generateCmd(GenPowerAmplifier.OPERATE_SONG, addr485, map);
//			if (_cmdList.size()>0) {
//				for(String str : _cmdList) {
//					Logger.warn(null, str);
//				}
//			}else {
//				Logger.debug("tangl.song.play", "commd null");
//			}
//		}else if(funType.equals(ProductConstants.POWER_AMPLIFIER_SOUND_REPEAT_ALL)){ // 公放- 列表循环
//			_cmdList = GenPowerAmplifier.generateCmd(GenPowerAmplifier.OPERATE_REPEAT_ALL, addr485, map);
//			if (_cmdList.size()>0) {
//				Logger.debug("tangl.song.play", _cmdList.get(0).toString());
//			}else {
//				Logger.debug("tangl.song.play", "commd null");
//			}
//		}else if(funType.equals(ProductConstants.POWER_AMPLIFIER_SOUND_REPEAT_SINGLE)){ // 公放- 单曲循环
//			_cmdList = GenPowerAmplifier.generateCmd(GenPowerAmplifier.OPERATE_REPEAT_SINGLE, addr485, map);
//			if (_cmdList.size()>0) {
//				Logger.debug("tangl.song.play", _cmdList.get(0).toString());
//			}else {
//				Logger.debug("tangl.song.play", "commd null");
//			}
//		}else if(funType.equals(ProductConstants.FUN_TYPE_UFO1)){//飞碟一号
//			
//		}else if(funType.equals(ProductConstants.FUN_TYPE_AUTO_LOCK)){//自动锁
//			_cmdList = GenLock.generateCmd(type, addr485, map);
////			if (strList.size()>0) {
////				sb.append(strList.get(0).toString());
////			}
//		}else if(funType.equals(ProductConstants.FUN_TYPE_GATEWAY)){//网关
//			
//		}
//		if(_cmdList != null){
//			for(int i = 0;i < _cmdList.size();i++){
//				StringBuffer sb = new StringBuffer(account);
//				sb.append("|");
//				sb.append(_cmdList.get(i));
//				_cmdList.set(i,sb.toString());
//			}
//		}
//		return _cmdList;
//	}
//
//	public static List<byte[]> createCmdWithLength(String customerId, String addr485,
//			String operation, List<String> funUnits,String funType, Integer index) {
//		List<byte[]> byteList = new ArrayList<byte[]>();
//		List<String> _cmdList = createCmdWithAccount(customerId, addr485, operation,funUnits,
//				funType, index);
//		if(_cmdList != null){
//			for(int i = 0;i < _cmdList.size();i++){
//				try {
//					int len;
//					String _cmd = _cmdList.get(i);
//					len = _cmd.getBytes("utf-8").length;
//
//					ByteBuffer bbf = ByteBuffer.allocate(len + 4);
//					bbf.putInt(len);
//					bbf.put(_cmd.getBytes("utf-8"));
//					bbf.flip();
//					byteList.add( bbf.array());
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return byteList;
//	}
//}
