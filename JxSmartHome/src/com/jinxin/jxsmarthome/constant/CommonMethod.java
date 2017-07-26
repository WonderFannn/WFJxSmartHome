package com.jinxin.jxsmarthome.constant;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;

import com.jinxin.cd.smarthome.product.service.newcmd.CmdEntry;
import com.jinxin.cd.smarthome.product.service.newcmd.CmdPolicy;
import com.jinxin.cd.smarthome.product.service.newcmd.GenColorLight;
import com.jinxin.cd.smarthome.product.service.newcmd.GenCurtain;
import com.jinxin.cd.smarthome.product.service.newcmd.GenGateway;
import com.jinxin.cd.smarthome.product.service.newcmd.GenHumanSense;
import com.jinxin.cd.smarthome.product.service.newcmd.GenLock;
import com.jinxin.cd.smarthome.product.service.newcmd.GenMCLight;
import com.jinxin.cd.smarthome.product.service.newcmd.GenPowerAmplifier;
import com.jinxin.cd.smarthome.product.service.newcmd.GenPowerAmplifier2;
import com.jinxin.cd.smarthome.product.service.newcmd.GenUFO01;
import com.jinxin.cd.smarthome.product.service.newcmd.GenZGAirConditionSocket;
import com.jinxin.cd.smarthome.product.service.newcmd.GenZGAirController;
import com.jinxin.cd.smarthome.product.service.newcmd.GenZGBulb;
import com.jinxin.cd.smarthome.product.service.newcmd.GenZGCommon;
import com.jinxin.cd.smarthome.product.service.newcmd.GenZGCurtain;
import com.jinxin.cd.smarthome.product.service.newcmd.GenZGGateway;
import com.jinxin.cd.smarthome.product.service.newcmd.GenZGLock;
import com.jinxin.cd.smarthome.product.service.newcmd.GenZGSocket;
import com.jinxin.datan.local.serviceimpl.LocalEncoderVersion10;
import com.jinxin.datan.local.serviceimpl.ZigbeeEncoderVersion10;
import com.jinxin.datan.local.util.NetworkModeSwitcher;
import com.jinxin.db.impl.CloudSettingDaoImpl;
import com.jinxin.db.impl.CustomerDaoImpl;
import com.jinxin.db.impl.CustomerMusicLibDaoImpl;
import com.jinxin.db.impl.CustomerPatternDaoImpl;
import com.jinxin.db.impl.CustomerProductCMDDaoImpl;
import com.jinxin.db.impl.CustomerProductDaoImpl;
import com.jinxin.db.impl.CustomerTimerTaskDaoImpl;
import com.jinxin.db.impl.FeedbackDaoImpl;
import com.jinxin.db.impl.FunDetailConfigDaoImpl;
import com.jinxin.db.impl.FunDetailDaoImpl;
import com.jinxin.db.impl.ProductConnectionDaoImpl;
import com.jinxin.db.impl.ProductDoorContactDaoImpl;
import com.jinxin.db.impl.ProductFunDaoImpl;
import com.jinxin.db.impl.ProductPatternOperationDaoImpl;
import com.jinxin.db.impl.ProductStateDaoImpl;
import com.jinxin.db.impl.ProductVoiceConfigDaoImpl;
import com.jinxin.db.impl.ProductVoiceTypeDaoImpl;
import com.jinxin.db.impl.SingerLibDaoImpl;
import com.jinxin.db.impl.TimerTaskOperationDaoImpl;
import com.jinxin.db.impl.WhProductUnfraredDaoImpl;
import com.jinxin.jxsmarthome.cmd.entity.PackageMessage;
import com.jinxin.jxsmarthome.cmd.entity.PackageMessage2;
import com.jinxin.jxsmarthome.entity.CloudSetting;
import com.jinxin.jxsmarthome.entity.Customer;
import com.jinxin.jxsmarthome.entity.CustomerMusicLib;
import com.jinxin.jxsmarthome.entity.CustomerPattern;
import com.jinxin.jxsmarthome.entity.CustomerProduct;
import com.jinxin.jxsmarthome.entity.CustomerProductCMD;
import com.jinxin.jxsmarthome.entity.CustomerProductDevice;
import com.jinxin.jxsmarthome.entity.CustomerProductType;
import com.jinxin.jxsmarthome.entity.CustomerTimer;
import com.jinxin.jxsmarthome.entity.Feedback;
import com.jinxin.jxsmarthome.entity.FunDetail;
import com.jinxin.jxsmarthome.entity.FunDetailConfig;
import com.jinxin.jxsmarthome.entity.ProductConnection;
import com.jinxin.jxsmarthome.entity.ProductDoorContact;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
import com.jinxin.jxsmarthome.entity.ProductState;
import com.jinxin.jxsmarthome.entity.ProductVoiceConfig;
import com.jinxin.jxsmarthome.entity.ProductVoiceType;
import com.jinxin.jxsmarthome.entity.SingerLib;
import com.jinxin.jxsmarthome.entity.TimerTaskOperation;
import com.jinxin.jxsmarthome.entity.WHproductUnfrared;
import com.jinxin.jxsmarthome.fragment.CurtainFragment;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.jxsmarthome.util.AppUtil;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.jxsmarthome.util.Logger;
import com.jinxin.jxsmarthome.util.StringUtils;

/**
 * 公共方法
 * 
 * @author JackeyZhang
 * @company 金鑫智慧
 */
@SuppressWarnings("deprecation")
public class CommonMethod {
	private static List<CustomerProductType> customerProductTypeList = null;
	private static List<ProductFun> typeProductFunList = null;
	
//	public static List<byte[]> productFunToCMD(Context con, String cmdType4Device, String address485, 
//			Map<String, Object> params) {
//		String customerId = AppUtil.getCustomerId(con);
//		/********2016-01-26 修改多网关指令*********/
////		String whId485 = AppUtil.getGetwayWhIdByProductWhId(con, productFun.getWhId());
//		/*************END**********************/
//		return CommonMethod.createCmdWithLength(customerId,
//				address485, cmdType4Device, null, cmdType4Device, params);
//	}

	/**
	 * 更新问题反馈
	 * 
	 * @param feedbackList
	 */
	public static void updateFeedback(Context context,
			List<Feedback> feedbackList) {
		if (context == null || feedbackList == null)
			return;
		FeedbackDaoImpl cpDaoImpl = new FeedbackDaoImpl(context);
		if (cpDaoImpl != null) {
			for (Feedback cp : feedbackList) {
				List<Feedback> _cpList = cpDaoImpl.find(null, "messageId=?",
						new String[] { String.valueOf(cp.getMessageId()) }, null, null, null, null);
				if (_cpList == null || _cpList.size() <= 0) {
					// 设备不存在插入（主键自增）
					cpDaoImpl.insert(cp, false);
				} else {
					// 设备存在，修改主键ID更新
					Feedback _cp = _cpList.get(0);// 由设备序列号筛选出的，应该是只有一项
					if (_cp != null) {
						cp.setMessageId(_cp.getMessageId());
						cpDaoImpl.update(cp);
					}
				}
			}
		}
	}
	
	/**
	 * 更新客户设备信息
	 * 
	 * @param customerProductList
	 */
	public static void updateCustomerProduct(Context context,
			List<CustomerProduct> customerProductList) {
		if (context == null || customerProductList == null)
			return;
		CustomerProductDaoImpl cpDaoImpl = new CustomerProductDaoImpl(context);
		if (cpDaoImpl != null) {
			for (CustomerProduct cp : customerProductList) {
				List<CustomerProduct> _cpList = cpDaoImpl.find(null, "whId=?",
						new String[] { cp.getWhId() }, null, null, null, null);
				if (_cpList == null || _cpList.size() <= 0) {
					// 设备不存在插入（主键自增）
					cpDaoImpl.insert(cp, true);
				} else {
					// 设备存在，修改主键ID更新
					CustomerProduct _cp = _cpList.get(0);// 由设备序列号筛选出的，应该是只有一项
					if (_cp != null) {
						cp.setId(_cp.getId());
						cpDaoImpl.update(cp);
					}
				}
			}
		}
	}

	/**
	 * 更新产品功能操作列表
	 * 
	 * @param customerProductList
	 */
	public static void updateProductFunList(Context context,
			List<ProductFun> productFunList) {
		if (context == null || productFunList == null)
			return;
		ProductFunDaoImpl pfDaoImpl = new ProductFunDaoImpl(context);
		if (pfDaoImpl != null) {
			for (ProductFun pf : productFunList) {
				if (pf != null) {
					if (pfDaoImpl.isExist(
							"select funId from product_fun where funId='"
									+ pf.getFunId() + "'", null)) {
						pfDaoImpl.update(pf);
					} else {
						pfDaoImpl.insert(pf, false);
					}
				}
			}
		}
	}

	/**
	 * 更新红外转发器列表
	 * 
	 * @param customerProductList
	 */
	public static void updateWHproductUnfraredList(Context context,
			List<WHproductUnfrared> wHproductUnfrareds) {
		if (context == null || wHproductUnfrareds == null)
			return;
		WhProductUnfraredDaoImpl unfraredDaoImpl = new WhProductUnfraredDaoImpl(context);
		if (unfraredDaoImpl != null) {
			for (WHproductUnfrared wh : wHproductUnfrareds) {
				if (wh != null) {
					if (unfraredDaoImpl.isExist(
							"select funId from whproductfun_uninfrared where funId='"
									+ wh.getFundId() + "'", null)) {
						unfraredDaoImpl.update(wh);
					} else {
						unfraredDaoImpl.insert(wh, false);
					}
				}
			}
		}
	}
	
	/**
	 * 更新设备状态列表
	 * 
	 * @param customerProductList
	 */
	public static void updateProductStateList(Context context,
			List<ProductState> productStateList) {
		if (context == null || productStateList == null) {
			return;
		}
		ProductStateDaoImpl psDaoImpl = new ProductStateDaoImpl(context);
		if (psDaoImpl != null) {
			for (ProductState pf : productStateList) {
				if (pf != null) {
					if (psDaoImpl.isExist(
							"select funId from product_state where funId='"
									+ pf.getFunId() + "'", null)) {
						psDaoImpl.update(pf);
					} else {
						psDaoImpl.insert(pf, false);
					}
				}
			}
		}
	}

	/**
	 * 更新客户模式
	 * 
	 * @param customerPatternList
	 */
	public static void updateCustomerPattern(Context context,
			List<CustomerPattern> customerPatternList) {
		if (context == null || customerPatternList == null)
			return;
		CustomerPatternDaoImpl cpDaoImpl = new CustomerPatternDaoImpl(context);
		if (cpDaoImpl != null) {
			for (CustomerPattern cp : customerPatternList) {
				if (cp != null) {
					if (cpDaoImpl.isExist(
							"select patternId from customer_pattern where patternId='"
									+ cp.getPatternId() + "'", null)) {
						cpDaoImpl.update(cp);
					} else {
						cpDaoImpl.insert(cp, false);
					}
				}
			}
		}
	}

	/**
	 * 更新产品模式操作
	 * 
	 * @param customerPatternCMDList
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void updateProductPatternOperationList(Context context,
			List<ProductPatternOperation> productPatternOperationList) {
		if (context == null || productPatternOperationList == null)
			return;
		ProductPatternOperationDaoImpl ppoDaoImpl = new ProductPatternOperationDaoImpl(
				context);
		if (ppoDaoImpl != null) {
			Map<String, Integer> map = new HashMap<String, Integer>();
			// 增量更新时，先删除有做修改的模式下的所有操作，不包含新增的模式
			for (ProductPatternOperation _ppo : productPatternOperationList) {
				if (_ppo != null) {
					if (ppoDaoImpl.isExist(
							"select id from product_pattern_operation where patternId='"
									+ _ppo.getPatternId() + "'", null)) {
						map.put(String.valueOf(_ppo.getPatternId()),
								_ppo.getPatternId());
					}
				}
			}

			//删除已经存在的模式的操作数据
			Set<Entry<String, Integer>> entryset = map.entrySet();
			Iterator iter = entryset.iterator();
			while (iter.hasNext()) {
				Entry<String, String> entry = (Entry<String, String>) iter
						.next();
				String sql = "delete from product_pattern_operation where patternId='"
						+ entry.getKey() + "'";
				ppoDaoImpl.execSql(sql, null);
			}

			//重新插入数据
			for (ProductPatternOperation ppo : productPatternOperationList) {
				if (ppo != null) {
					if (ppoDaoImpl.isExist(
							"select id from product_pattern_operation where id='"
									+ ppo.getId() + "'", null)) {
						ppoDaoImpl.update(ppo);
					} else {
						ppoDaoImpl.insert(ppo, false);
					}
				}
			}
		}
	}
	
	/**
	 * 更新客户模式
	 * 
	 * @param customerPatternList
	 */
	public static void updateProductConnectionList(Context context,
			List<ProductConnection> connectionList) {
		if (context == null || connectionList == null)
			return;
		ProductConnectionDaoImpl cpDaoImpl = new ProductConnectionDaoImpl(context);
		cpDaoImpl.clear();
		if (cpDaoImpl != null) {
			for (ProductConnection pc : connectionList) {
				if (pc != null) {
					if (cpDaoImpl.isExist(
							"select patternId from product_conntection where id='"
									+ pc.getId() + "'", null)) {
						cpDaoImpl.update(pc);
					} else {
						cpDaoImpl.insert(pc, false);
					}
				}
			}
		}
	}
	
	/**
	 * 更新定时任务（全部更新）
	 * 
	 * @param customerTimerList
	 */
	public static void updateCustomerTimerAll(Context context,
			List<CustomerTimer> customerTimerList) {
		if (context == null || customerTimerList == null)
			return;
		CustomerTimerTaskDaoImpl ctDaoImpl = new CustomerTimerTaskDaoImpl(context);
		ctDaoImpl.clear();
		if (ctDaoImpl != null) {
			for (CustomerTimer ct : customerTimerList) {
				if (ct != null) {
//					if (ctDaoImpl.isExist(
//							"select taskId from customer_timer_task where taskId='"
//									+ ct.getTaskId() + "'", null)) {
//						ctDaoImpl.update(ct);
//					}
					CustomerTimer _ct = ctDaoImpl.get(ct.getTaskId());
					if (_ct != null) {
						ct.setStatus(_ct.getStatus());
						ctDaoImpl.update(ct);
					}else {
						ctDaoImpl.insert(ct, false);
					}
				}
			}
		}
	}
	/**
	 * 更新定时任务（增量方式）
	 * 
	 * @param customerTimerList
	 */
	public static void updateCustomerTimer(Context context,
			List<CustomerTimer> customerTimerList) {
		if (context == null || customerTimerList == null)
			return;
		CustomerTimerTaskDaoImpl ctDaoImpl = new CustomerTimerTaskDaoImpl(context);
		if (ctDaoImpl != null) {
			for (CustomerTimer ct : customerTimerList) {
				if (ct != null) {
//					if (ctDaoImpl.isExist(
//							"select taskId from customer_timer_task where taskId='"
//									+ ct.getTaskId() + "'", null)) {
//						ctDaoImpl.update(ct);
//					}
					CustomerTimer _ct = ctDaoImpl.get(ct.getTaskId());
					if (_ct != null) {
						ct.setStatus(_ct.getStatus());
						ctDaoImpl.update(ct);
					}else {
						ctDaoImpl.insert(ct, false);
					}
				}
			}
		}
	}
	
	/**
	 * 更新定时任务操作
	 * 
	 * @param customerPatternCMDList
	 */
	@SuppressWarnings("unchecked")
	public static void updateTimerTaskOperationList(Context context,
			List<TimerTaskOperation> timerTaskOperationList) {
		if (context == null || timerTaskOperationList == null)
			return;
		TimerTaskOperationDaoImpl ttoDaoImpl = new TimerTaskOperationDaoImpl(
				context);
		if (ttoDaoImpl != null) {
			
			Map<String, Integer> map = new HashMap<String, Integer>();
			// 增量更新时，先删除有做修改的定时下的所有操作，不包含新增的定时
			for (TimerTaskOperation _tto : timerTaskOperationList) {
				if (_tto != null) {
					if (ttoDaoImpl.isExist(
							"select id from timer_task_operation where taskId='"
									+ _tto.getTaskId() + "'", null)) {
						map.put(String.valueOf(_tto.getPatternId()),
								_tto.getPatternId());
					}
				}
			}

			//删除已经存在的定时的操作数据
			Set<Entry<String, Integer>> entryset = map.entrySet();
			Iterator iter = entryset.iterator();
			while (iter.hasNext()) {
				Entry<String, String> entry = (Entry<String, String>) iter
						.next();
				String sql = "delete from timer_task_operation where taskId='"
						+ entry.getKey() + "'";
				ttoDaoImpl.execSql(sql, null);
			}

			
			for (TimerTaskOperation tto : timerTaskOperationList) {
				if (tto != null) {
					if (ttoDaoImpl.isExist(
							"select id from timer_task_operation where id='"
									+ tto.getId() + "'", null)) {
						ttoDaoImpl.update(tto);
					} else {
						ttoDaoImpl.insert(tto, false);
					}
				}
			}
		}
	}

	/**
	 * 更新产品功能明细
	 * 
	 * @param funDetailList
	 */
	public static void updateFunDetailList(Context context,
			List<FunDetail> funDetailList) {
		if (context == null || funDetailList == null)
			return;
		FunDetailDaoImpl fdDaoImpl = new FunDetailDaoImpl(context);
		if (fdDaoImpl != null) {
			for (FunDetail fd : funDetailList) {
				Logger.debug(null, fd.toString());
				if (fd != null) {
					if (fdDaoImpl.isExist(
							"select id from fun_detail where id='"
									+ fd.getId() + "'", null)) {
						fdDaoImpl.update(fd);
					} else {
						fdDaoImpl.insert(fd, false);
					}
				}
			}
		}
	}
	/**
	 * 更新歌手列表明细
	 * 
	 * @param funDetailList
	 */
	public static void updateSingerList(Context context,
			List<SingerLib> singerList) {
		if (context == null || singerList == null) return;
		SingerLibDaoImpl sldImpl = new SingerLibDaoImpl(context);
		if (sldImpl != null) {
			for (SingerLib sl : singerList) {
				if (sl != null) {
					if (sldImpl.isExist(
							"select id from singer_lib where id='"
									+ sl.getId() + "'", null)) {
						sldImpl.update(sl);
					} else {
						sldImpl.insert(sl, false);
					}
				}
			}
		}
	}
	/**
	 * 更新收藏音乐列表明细
	 * 
	 * @param funDetailList
	 */
	public static void updateMusicList(Context context,
			List<CustomerMusicLib> musicList) {
		if (context == null || musicList == null) return;
		CustomerMusicLibDaoImpl cmDaoImpl = new CustomerMusicLibDaoImpl(context);
		if (cmDaoImpl != null) {
			for (CustomerMusicLib cml : musicList) {
				if (cml != null) {
					if (cmDaoImpl.isExist(
							"select id from singer_lib where id='"
									+ cml.getId() + "'", null)) {
						cmDaoImpl.update(cml);
					}else{
						cmDaoImpl.insert(cml,false);
					}
				}
			}
		}
		
	}

	/**
	 * 更新产品功能明细配置参数
	 * 
	 * @param funDetailList
	 */
	public static void updateFunDetailConfigList(Context context,
			List<FunDetailConfig> funDetailConfigs) {
		if (context == null || funDetailConfigs == null)
			return;
		FunDetailConfigDaoImpl fdDaoImpl = new FunDetailConfigDaoImpl(context);
		if (fdDaoImpl != null) {
			for (FunDetailConfig fdc : funDetailConfigs) {
				if (fdc != null) {
					Logger.debug("tangl.update", fdc.toString());
					if (fdDaoImpl.isExist(
							"select whId from fun_detail_config where whId='"
									+ fdc.getWhId() + "'", null)) {
						List<FunDetailConfig> list = fdDaoImpl.find(null,
								"whId=?", new String[] { fdc.getWhId() }, null,
								null, null, null);
						if (list != null && list.size() > 0) {
							fdc.setId(list.get(0).getId());
						}

						fdDaoImpl.update(fdc);
					} else {
						fdDaoImpl.insert(fdc, true);
					}
				}
			}
		}
	}

	/**
	 * 更新语音类型列表
	 * @param context
	 * @param productVoiceTypes
	 */
	public static void updateProductVoiceTypeList(Context context,
			List<ProductVoiceType> productVoiceTypes){
		if (context == null || productVoiceTypes == null) return;
		
		ProductVoiceTypeDaoImpl pvtDaoImpl = new ProductVoiceTypeDaoImpl(context);
		if (pvtDaoImpl != null) {
			for (ProductVoiceType pvt : productVoiceTypes) {
				if (pvt != null) {
					if (pvtDaoImpl.isExist(
							"select id from product_voice_type where id='"
									+ pvt.getId() + "'", null)) {
						pvtDaoImpl.update(pvt);
					} else {
						pvtDaoImpl.insert(pvt, false);
					}
				}
			}
		}
	}
	/**
	 * 更新语音配置列表
	 * @param context
	 * @param productVoiceConfigs
	 */
	public static void updateProductVoiceConfigList(Context context,
			List<ProductVoiceConfig> productVoiceConfigs){
		if (context == null || productVoiceConfigs == null) return;
		
		ProductVoiceConfigDaoImpl pvcfDaoImpl = new ProductVoiceConfigDaoImpl(context);
		if (pvcfDaoImpl != null) {
			for (ProductVoiceConfig pvc : productVoiceConfigs) {
				if (pvc != null) {
					if (pvcfDaoImpl.isExist(
							"select id from product_voice_config where id='"
									+ pvc.getId() + "'", null)) {
						pvcfDaoImpl.update(pvc);
					} else {
						pvcfDaoImpl.insert(pvc, false);
					}
				}
			}
		}
	}
	/**
	 * 更新语音配置列表
	 * @param context
	 * @param productVoiceConfigs
	 */
	public static void updateProductDoorContactList(Context context,
			List<ProductDoorContact> productDoorContacts){
		if (context == null || productDoorContacts == null) return;
		
		ProductDoorContactDaoImpl pvcfDaoImpl = new ProductDoorContactDaoImpl(context);
		if (pvcfDaoImpl != null) {
			for (ProductDoorContact pdc : productDoorContacts) {
				if (pdc != null) {
//					if (pvcfDaoImpl.isExist(
//							"select whId from product_door_contact where whId='"
//									+ pdc.getWhId() + "'", null)) {
					List<ProductDoorContact> list = pvcfDaoImpl.find(null,
							"whId=?", new String[]{pdc.getWhId()}, null, null, null, null);
					if (list != null && list.size() > 0) {
						ProductDoorContact _pdc = list.get(0);
						pdc.setIsWarn(_pdc.getIsWarn());
						pvcfDaoImpl.update(pdc);
					}
//					} 
					else {
							pvcfDaoImpl.insert(pdc, false);
						}
				}
			}
		}
	}
	
	/**
	 * 更新云设置
	 * 
	 * @param funDetailList
	 */
	public static void updateCloudSettings(Context context,
			List<CloudSetting> csList) {
		if (context == null || csList == null)
			return;
		CloudSettingDaoImpl csDaoImpl = new CloudSettingDaoImpl(context);
		if (csDaoImpl != null) {
			for (CloudSetting cs : csList) {
				if (cs != null) {

					boolean b = csDaoImpl.isExist(
							"select * from cloud_setting where items='"
									+ cs.getItems() + "' and customer_id='"
									+ cs.getCustomerId() + "' and category='"
									+ cs.getCategory() + "'", null);
					if (b) {
						List<CloudSetting> css = csDaoImpl.find(null,
								"items=? and category=? and customer_id=?",
								new String[] { cs.getItems(), cs.getCategory(),
										cs.getCustomerId() }, null, null, null,
								null);
						for (CloudSetting c : css) {
							cs.setId(c.getId());
							csDaoImpl.update(cs);
						}
					} else {
						csDaoImpl.insert(cs, true);
					}
				}
			}
		}
	}
	
	

	/**
	 * 初始化设备/类型/命令
	 * 
	 * @deprecated
	 */
	public static void initDeviceAndCMD(Context context) {
		splitDeviceType(context);
		for (CustomerProductType cpt : customerProductTypeList) {
			if (cpt != null) {
				cpt.setCustomerProductDeviceList(currentTypeFillingDeviceAndCMD(
						context, cpt.getCode(), cpt.getIcon()));
			}
		}
	}

	/**
	 * @deprecated 初始化类型设备
	 */
	public static void initTypeDevice(Context context) {
		splitTypeDevice(context);
	}

	/**
	 * 当前类型填充设备及指令
	 * 
	 * @deprecated
	 * @param context
	 * @param code
	 */
	private static List<CustomerProductDevice> currentTypeFillingDeviceAndCMD(
			Context context, String code, String icon) {
		CustomerProductCMDDaoImpl cpcmdDaoImpl = new CustomerProductCMDDaoImpl(
				context);
		List<CustomerProductCMD> customerProductCMDList = cpcmdDaoImpl.find(
				null, "code=?", new String[] { code }, null, null, null, null);
		List<CustomerProductDevice> customerProductDeviceList = new ArrayList<CustomerProductDevice>();
		if (customerProductCMDList != null) {
			// 设备填充
			for (CustomerProductCMD cpCMD : customerProductCMDList) {
				CustomerProductDevice cpd = isWhIdExistCurrentType(
						customerProductDeviceList, cpCMD.getWhId());
				if (cpd == null) {
					// 不存在该设备
					customerProductDeviceList.add(new CustomerProductDevice(
							cpCMD.getCode(), cpCMD.getCmdName(), icon,
							new ArrayList<CustomerProductCMD>(), cpCMD
									.getWhId()));
				} else {
					// 已存在该设备
					List<CustomerProductCMD> _list = cpd
							.getCustomerProductCMDList();
					if (_list != null) {
						_list.add(cpCMD);
					}
				}
			}
		}
		return customerProductDeviceList;
	}

	/**
	 * 当前类型填充设备
	 * 
	 * @param context
	 * @param funType
	 */
	public static List<ProductFun> currentTypeFillingDevice(Context context,
			String funType) {
		ProductFunDaoImpl pfDaoImpl = new ProductFunDaoImpl(context);
		List<ProductFun> productFunList = pfDaoImpl.find(null, "funType=? and enable=?", 
				new String[]{funType,Integer.toString(1)}, null, null, null, null);
		return productFunList;
	}

	
	
	/**
	 * 获取对应类型的红外学习记录
	 * 
	 * @param context
	 * @param funType
	 */
	public static List<WHproductUnfrared> catchWhProductFrarerd(Context context,
			int fundId) {
		WhProductUnfraredDaoImpl whDaoImpl = new WhProductUnfraredDaoImpl(context);
		List<WHproductUnfrared> wHproductUnfrareds = whDaoImpl.find(null, "fundId=?", 
				new String[]{String.valueOf(fundId)}, null, null, null, null);
		return wHproductUnfrareds;
	}
	
	/**
	 * 当前模式填充操作
	 * 
	 * @param context
	 * @param patternId
	 */
	public static List<ProductPatternOperation> currentModeFillingOperation(
			Context context, String patternId) {
		ProductPatternOperationDaoImpl ppoDaoImpl = new ProductPatternOperationDaoImpl(
				context);
		List<ProductPatternOperation> productPatternOperationList = ppoDaoImpl
				.find(null, "patternId=?", new String[] { patternId }, null,
						null, null, null);
		return productPatternOperationList;
	}

	/**
	 * @deprecated 当前设备是否已经生成于该类型列表中（存在返回设备对象）
	 * @param list
	 * @param whId
	 */
	private static CustomerProductDevice isWhIdExistCurrentType(
			List<CustomerProductDevice> list, String whId) {
		if (list == null || whId == null)
			return null;
		for (CustomerProductDevice cpd : list) {
			if (whId.equals(cpd.getWhId())) {
				return cpd;
			}
		}
		return null;
	}

	/**
	 * 设备分类
	 * 
	 * @deprecated
	 */
	private static void splitDeviceType(Context context) {
		if (context == null)
			return;
		CustomerProductDaoImpl cpDaoImpl = new CustomerProductDaoImpl(context);
		List<CustomerProduct> customerProductList = cpDaoImpl.find();
		if (customerProductList != null) {
			customerProductTypeList = new ArrayList<CustomerProductType>();
			for (CustomerProduct cp : customerProductList) {
				if (cp != null) {
					if (!isExistCustomerProductType(cp.getCode())) {
						customerProductTypeList.add(new CustomerProductType(cp
								.getCode(), cp.getIcon(), cp.getTypeName()));
					}

				}
			}
		}
	}

	/**
	 * 该设备类型是否已存在
	 * 
	 * @deprecated
	 * @param code
	 * @return
	 */
	private static boolean isExistCustomerProductType(String code) {
		for (CustomerProductType cpt : customerProductTypeList) {
			if (cpt != null) {
				if (cpt.getCode().equals(code)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @deprecated 设备分类
	 */
	private static void splitTypeDevice(Context context) {
		if (context == null)
			return;
		ProductFunDaoImpl pfDaoImpl = new ProductFunDaoImpl(context);
		List<ProductFun> productFunList = pfDaoImpl.find();
		if (productFunList != null) {
			typeProductFunList = new ArrayList<ProductFun>();
			for (ProductFun pf : productFunList) {
				if (pf != null) {
					if (!isExistTypeProductFun(pf.getFunType())) {
						typeProductFunList.add(pf);
					}

				}
			}
		}
	}

	/**
	 * 该类型设备是否已存在
	 * 
	 * @deprecated
	 * @param funType
	 * @return
	 */
	private static boolean isExistTypeProductFun(String funType) {
		for (ProductFun pf : typeProductFunList) {
			if (pf != null) {
				if (pf.getFunType().equals(funType)) {
					return true;
				}
			}
		}
		return false;
	}

	public static List<CustomerProductType> getCustomerProductTypeList() {
		return customerProductTypeList;
	}

	// public static void setCustomerProductTypeList(
	// List<CustomerProductType> customerProductTypeList) {
	// customerProductTypeList = customerProductTypeList;
	// }

	/**
	 * 取用户设备类型列表（每种类型存放一个排在最前的该设备对象）
	 * 
	 * @return
	 */
	public static List<ProductFun> getTypeProductFunList() {
		return typeProductFunList;
	}

	public static void setTypeProductFunList(List<ProductFun> typeProductFunList) {
		CommonMethod.typeProductFunList = typeProductFunList;
	}

	/**
	 * 设备巡检操作生成指令字节码
	 * 
	 * @param con
	 * @param data.productFun
	 * @return
	 */
	public static List<byte[]> productDetectToCMD(Context con,String whId,String code) {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(StaticConstant.OPERATE_SET_TYPE, code);
		
		String type = "inspection";
		CustomerProductDaoImpl cpDaoImpl = new CustomerProductDaoImpl(con);
		List<CustomerProduct> cpList = cpDaoImpl.find(null, "whId=?",
				new String[] {whId}, null, null, null, null);
		String addr485 = "";
		if (cpList != null && cpList.size() > 0) {
			CustomerProduct cp = cpList.get(0);
			if (cp != null) {
				addr485 = cp.getAddress485();
			}
		}
		
//		String customerId = CommUtil.getMainAccount();
		/********2016-01-27 修改多网关指令*********/
		String whId485 = AppUtil.getGetwayWhIdByProductWhId(con, whId);
		/*************END**********************/
		List<byte[]> cmd = null;
		cmd = CommonMethod.createCmdWithLength(whId485, addr485, type, null,
				ProductConstants.FUN_TYPE_DEVICE_DETECT, params);
		return cmd;
	}
	
	/**
	 * 设备单路操作生成指令字节码
	 * 
	 * @param con
	 * @param productFun
	 * @return
	 */
	public static List<byte[]> productFunToCMD(Context con,
			ProductFun productFun, FunDetail funDetail,Map<String, Object> params) {
		if (productFun == null || funDetail == null)
			return new ArrayList<byte[]>();
		if (params == null || params.size() < 1) {
			params = new HashMap<String, Object>();
			params.put(StaticConstant.PARAM_INDEX, productFun.getFunUnit());
		}
		String type = getType(con, productFun, funDetail);
		List<String> funUnits = new ArrayList<String>();
		funUnits.add(productFun.getFunUnit());
		CustomerProductDaoImpl cpDaoImpl = new CustomerProductDaoImpl(con);
		List<CustomerProduct> cpList = cpDaoImpl.find(null, "whId=?",
				new String[] { productFun.getWhId() }, null, null, null, null);
		String addr485 = "";
		if (cpList != null && cpList.size() > 0) {
			CustomerProduct cp = cpList.get(0);
			if (cp != null) {
				addr485 = cp.getAddress485();
			}
		}
		
//		String customerId = CommUtil.getMainAccount();
		/********2016-01-26 修改多网关指令*********/
		String whId485 = AppUtil.getGetwayWhIdByProductWhId(con, productFun.getWhId());
		/*************END**********************/
		List<byte[]> cmd = CommonMethod.createCmdWithLength(whId485,
				addr485, type, funUnits, productFun.getFunType(), params);
		return cmd;
	}

	/**
	 * 生成命令 多条指令只会生成一条命令
	 * 命令格式：1000005628001010003|user20open002:c10000000|user20open002
	 * :c01000000...
	 * 
	 * @param con
	 *            Context
	 * @param productFun
	 *            功能点
	 * @param funDetail
	 *            控制点
	 * @param parms
	 *            命令的参数
	 * @return 生成的命令
	 */
	public static byte[] productPatternOperationToCMD(Context con,
			ProductFun productFun, FunDetail funDetail,
			Map<String, Object> params) {

		if (productFun == null || funDetail == null || con == null) {
			Logger.debug(null, "productFun or funDetail is null");
			return null;
		}

		byte[] cmd = null;
		String addr485 = "";
		String funType = "";

		// 获取用户ID
//		String customerId = CommUtil.getMainAccount();
		
		// 获取funType
		funType = productFun.getFunType();

		// 获取addr485
		CustomerProductDaoImpl cpDaoImpl = new CustomerProductDaoImpl(con);
		List<CustomerProduct> cpList = cpDaoImpl.find(null, "whId=?",
				new String[] { productFun.getWhId() }, null, null, null, null);
		if (cpList != null && cpList.size() > 0) {
			CustomerProduct cp = cpList.get(0);
			if (cp != null) {
				addr485 = cp.getAddress485();
			}
		}

		/********2016-01-26 修改多网关指令*********/
		String whId485 = AppUtil.getGetwayWhIdByProductWhId(con, productFun.getWhId());
		/*************END**********************/
		// 生成原始命令
		List<String> originalCmd = CommonMethod.createCmdWithNotAccount(
				whId485, addr485, null, null, funType, params);
		
		StringBuilder sb = new StringBuilder();
		if (originalCmd != null && originalCmd.size() > 0) {
			for (int i = 0; i < originalCmd.size(); i++) {
				sb.append("|");
				sb.append(originalCmd.get(i));
			}
		} else {
			return null;
		}

		List<byte[]> encodeCmd = CommonMethod.createCmdListWithLength(whId485,
				sb.toString());

		if (encodeCmd != null) {
			cmd = encodeCmd.get(0);
		}

		return cmd;
	}
	
	/**
	 * 模式操作命令生成
	 * @param con context 对象
	 * @param ppoList	模式操作列表
	 * @param params	生成命令需要的参数
	 * @param wireless true: 无线网关命令，false:有线网关命令
	 * @return	生成的命令
	 */
	public static List<byte[]> productPatternOperationToCMD_V1(Context con,
			List<ProductPatternOperation> ppoList, Map<String, Object> params) {
		// 传入参数校验
		if (ppoList == null || ppoList.size() < 1 || con == null) {
			Logger.error(null, "ppoList or context cannot be null");
			return Collections.emptyList();
		}
		
		// 获取customerid
//		String customerId = CommUtil.getMainAccount();
//		if (CommUtil.isEmpty(customerId)) {
//			Logger.error(null, "customerId cannot be null");
//			return Collections.emptyList();
//		}
		
		// 对模式控制操作做对于控制单元的分组（根据whid设备）
		Map<String, List<ProductPatternOperation>> groupedOperation = new HashMap<String, List<ProductPatternOperation>>();

		for (ProductPatternOperation ppo : ppoList) {
			String key = ppo.getWhId();
			Logger.debug(null, "tangl.whid "+key);
			if (groupedOperation.containsKey(key)) {
				groupedOperation.get(key).add(ppo);
			} else {
				groupedOperation.put(key,
						new ArrayList<ProductPatternOperation>());
				groupedOperation.get(key).add(ppo);
			}
		}

		// 对分组后的模式操作按控制单元生成命令
		StringBuilder sb = new StringBuilder();
		List<byte[]> result = new ArrayList<byte[]>();
		Set<String> keySet = groupedOperation.keySet();
		if (keySet != null && keySet.size() > 0) {
			Iterator<String> keys = keySet.iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				List<ProductPatternOperation> groupList = groupedOperation
						.get(key);
				String cmdOriginal = generateOneGroudOperationToCMD(con,
						groupList);
				sb.append(cmdOriginal);
			}
		}

		String cmdStr = sb.toString();
		Logger.debug(null, "tangl.CMD "+cmdStr);
		
		/********2016-01-26 修改多网关指令*********/
		String whId485 = AppUtil.getGetwayWhIdByProductWhId(con, ppoList.get(0).getWhId());
		/*************END**********************/

		result = CommonMethod.createCmdListWithLength(whId485,cmdStr);

		return result;
	}
	@Deprecated
	/**
	 * 模式操作命令生成
	 * @param con context 对象
	 * @param ppoList	模式操作列表
	 * @param params	生成命令需要的参数
	 * @return	生成的命令
	 */
	public static List<byte[]> productPatternOperationToCMD_V2(Context con,
			List<ProductPatternOperation> ppoList, Map<String, Object> params) {
		// 传入参数校验
		if (ppoList == null || ppoList.size() < 1 || con == null) {
			Logger.error(null, "ppoList or context cannot be null");
			return Collections.emptyList();
		}
		
		// 获取customerid
		String customerId = null;
		CustomerDaoImpl cDaoImpl = new CustomerDaoImpl(con);
		List<Customer> cList = cDaoImpl.find();
		if (cList != null && cList.size() > 0) {
			Customer c = cList.get(0);
			if (c != null) {
				customerId = c.getCustomerId();
			}
		}
		if (CommUtil.isEmpty(customerId)) {
			Logger.error(null, "customerId cannot be null");
			return Collections.emptyList();
		}
		
		// 对模式控制操作做对于控制单元的分组（根据whid）
		Map<String, List<ProductPatternOperation>> groupedOperation = new HashMap<String, List<ProductPatternOperation>>();

		for (ProductPatternOperation ppo : ppoList) {
			String key = ppo.getWhId();
			Logger.debug("tangl.whid", key);
			if (groupedOperation.containsKey(key)) {
				groupedOperation.get(key).add(ppo);
			} else {
				groupedOperation.put(key,
						new ArrayList<ProductPatternOperation>());
				groupedOperation.get(key).add(ppo);
			}
		}

		// 对分组后的模式操作按控制单元生成命令
		String zegbingWhId = null;
		StringBuilder sb = new StringBuilder();
		List<byte[]> result = new ArrayList<byte[]>();
		Set<String> keySet = groupedOperation.keySet();
		if (keySet != null && keySet.size() > 0) {
			Iterator<String> keys = keySet.iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				zegbingWhId = AppUtil.getGetwayWhIdByProductWhId(con, key);
				List<ProductPatternOperation> groupList = groupedOperation
						.get(key);
				String cmdOriginal = generateOneGroudOperationToCMD(con,
						groupList);
				sb.append(cmdOriginal);
			}
		}

		String cmdStr = sb.toString();
		if(cmdStr != null) {
			String[] strArr = cmdStr.split("\\|");
			for(int i = 1; i < strArr.length; i++) {
				String s = strArr[0] + "," + strArr[i] ;
				Logger.debug(null, s);
				List<byte[]> tempList = CommonMethod.createCmdListWithLength(zegbingWhId, s);
				result.addAll(tempList);
			}
		}

		return result;
	}
	
	/**
	 * 针对功能单元生成命令(zj说明：只针对普通网关在线使用)
	 * 
	 * @param con
	 * @param ppoList
	 * @return
	 */
	public static String generateOneGroudOperationToCMD(Context con,
			List<ProductPatternOperation> ppoList) {
		// 获取whid
		String whId = ppoList.get(0).getWhId();

		if (CommUtil.isEmpty(whId)) {
			Logger.error(null, "whId cannot be null");
			return null;
		}

		// 获取address485地址
		CustomerProductDaoImpl dao = new CustomerProductDaoImpl(con);
		List<CustomerProduct> customerProducts = dao.find(null, "whId=?",
				new String[] { whId }, null, null, null, null);
		if (customerProducts == null || customerProducts.size() < 1) {
			Logger.error(null, "customerproduct with whid=" + whId
					+ "cannot be found");
			return null;
		}

		String address485 = customerProducts.get(0).getAddress485();
		if (CommUtil.isEmpty(address485)) {
			Logger.error(null, "address485 cannot be null");
			return null;
		}

		// 获取customerid
		String customerId = CommUtil.getMainAccount();
		if (CommUtil.isEmpty(customerId)) {
			Logger.error(null, "customerId cannot be null");
			return null;
		}

		// 获取funType
		String funType = null;

		// 生成针对于控制单元的命令
		String result = null;
		List<String> openList = new ArrayList<String>();
		List<String> closeList = new ArrayList<String>();
		List<String> setList = new ArrayList<String>();
		Map<String, Object> musicMap = new HashMap<String, Object>();
		List<String> upList = new ArrayList<String>();
		List<String> downList = new ArrayList<String>();
		List<String> securityList = new ArrayList<String>();
		List<String> redList = new ArrayList<String>();
		Map<String, Object> popLightList = new HashMap<String, Object>();
		List<String> wirelessSocketOnList = new ArrayList<String>();
		List<String> wirelessSocketOffList = new ArrayList<String>();

		for (ProductPatternOperation ppo : ppoList) {
			Logger.debug("tangl", ppo.toString());
			ProductFun pf = new ProductFun();
			List<ProductFun> pfs = new ProductFunDaoImpl(con).find(null,
					"funId=?", new String[] { String.valueOf(ppo.getFunId()) },
					null, null, null, null);
			if (pfs == null || pfs.size() < 1) {
				Logger.error(null, "cannot find ProductFun with the funId "
						+ ppo.getFunId() + ", while whId=" + ppo.getWhId());
				continue;
			}
			pf = pfs.get(0);
			if (funType == null) {
				funType = pf.getFunType();
			}

			if (ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT
					.equals(funType) ||
					funType.equals(ProductConstants.FUN_TYPE_AUTO_CHA_ZUO)) { // 电灯
				if ("open".equals(ppo.getOperation())) {
					openList.add(pf.getFunUnit());
				} else if ("close".equals(ppo.getOperation())) {
					closeList.add(pf.getFunUnit());
				}
			} else if (ProductConstants.FUN_TYPE_COLOR_LIGHT.equals(funType)) { // 彩灯
				if ("set".equals(ppo.getOperation())) {
					setList.add(ppo.getParaDesc());
				} else if ("close".equals(ppo.getOperation())) {
					setList.add("000000000000");
				}
			} else if (ProductConstants.FUN_TYPE_CURTAIN.equals(funType)) {
				if ("open".equals(ppo.getOperation())) {
					upList.add(pf.getFunUnit());
				} else if ("close".equals(ppo.getOperation())) {
					downList.add(pf.getFunUnit());
				}
			} else if (ProductConstants.FUN_TYPE_POWER_AMPLIFIER
					.equals(funType)) { // 功放
				String desc = ppo.getParaDesc();
				if (!CommUtil.isEmpty(desc)) {
					try {
						JSONObject jo = new JSONObject(desc);
						if ("play".equals(ppo.getOperation()) ||"playByLushu".equals(ppo.getOperation())) {
							musicMap.put("operation",
									ProductConstants.POWER_AMPLIFIER_SOUND_PLAY);
							String selectLine = jo
									.getString(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT);
							String input = jo
									.getString(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD);
							if (CommUtil.isEmpty(selectLine)
									|| CommUtil.isEmpty(input)) {
								Logger.error("tangl",
										"music paramters is error");
								musicMap.clear();
							} else {
								musicMap.put(
										StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD,
										input);
								musicMap.put(
										StaticConstant.PARAM_MUSIC_SELECT_INPUT,
										input);
								musicMap.put(
										StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT,
										selectLine);
							}
						} else if ("pause".equals(ppo.getOperation())) {
							musicMap.put("operation",ProductConstants.POWER_AMPLIFIER_SOUND_PAUSE);
							String selectLine = jo
									.getString(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT);
							String input = jo
									.getString(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD);
							if (CommUtil.isEmpty(selectLine)
									|| CommUtil.isEmpty(input)) {
								Logger.error("tangl",
										"music paramters is error");
								musicMap.clear();
							} else {
								musicMap.put(
										StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD,
										input);
								musicMap.put(
										StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT,
										selectLine);
							}
						}else if("preSong".equals(ppo.getOperation())){
							musicMap.put("operation",ProductConstants.POWER_AMPLIFIER_PROVIOUS);
							String selectLine = jo
									.getString(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT);
							String input = jo
									.getString(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD);
							if (CommUtil.isEmpty(selectLine)
									|| CommUtil.isEmpty(input)) {
								Logger.error("tangl",
										"music paramters is error");
								musicMap.clear();
							} else {
								musicMap.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD,
										input);
								musicMap.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT,
										selectLine);
							}
						}else if("nextSong".equals(ppo.getOperation())){
							musicMap.put("operation",ProductConstants.POWER_AMPLIFIER_NEXT);
							String selectLine = jo
									.getString(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT);
							String input = jo
									.getString(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD);
							if (CommUtil.isEmpty(selectLine)
									|| CommUtil.isEmpty(input)) {
								Logger.error("tangl",
										"music paramters is error");
								musicMap.clear();
							} else {
								musicMap.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD,
										input);
								musicMap.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT,
										selectLine);
							}
						}else if("soundAdd".equals(ppo.getOperation())){
							musicMap.put("operation",ProductConstants.POWER_AMPLIFIER_SOUND_ADD);
							String selectLine = jo
									.getString(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT);
							String input = jo
									.getString(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD);
							if (CommUtil.isEmpty(selectLine)
									|| CommUtil.isEmpty(input)) {
								Logger.error("tangl",
										"music paramters is error");
								musicMap.clear();
							} else {
								musicMap.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD,
										input);
								musicMap.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT,
										selectLine);
							}
						}else if("soundSub".equals(ppo.getOperation())){
							musicMap.put("operation",ProductConstants.POWER_AMPLIFIER_SOUND_SUB);
							String selectLine = jo
									.getString(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT);
							String input = jo
									.getString(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD);
							if (CommUtil.isEmpty(selectLine)
									|| CommUtil.isEmpty(input)) {
								Logger.error("tangl",
										"music paramters is error");
								musicMap.clear();
							} else {
								musicMap.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD,
										input);
								musicMap.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT,
										selectLine);
							}
						}else if("muteSingle".equals(ppo.getOperation())){
							musicMap.put("operation",ProductConstants.POWER_AMPLIFIER_MUTE);
							String selectLine = jo
									.getString(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT);
							String input = jo
									.getString(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD);
							if (CommUtil.isEmpty(selectLine)
									|| CommUtil.isEmpty(input)) {
								Logger.error("tangl",
										"music paramters is error");
								musicMap.clear();
							} else {
								musicMap.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD,
										input);
								musicMap.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT,
										selectLine);
							}
						}else if("unmuteSingle".equals(ppo.getOperation())){
							musicMap.put("operation",ProductConstants.POWER_AMPLIFIER_UNMUTE);
							String selectLine = jo
									.getString(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT);
							String input = jo
									.getString(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD);
							if (CommUtil.isEmpty(selectLine)
									|| CommUtil.isEmpty(input)) {
								Logger.error("tangl",
										"music paramters is error");
								musicMap.clear();
							} else {
								musicMap.put(StaticConstant.PARAM_MUSIC_SELECT_USB_OR_SD,
										input);
								musicMap.put(StaticConstant.PARAM_MUSIC_ROAD_LINE_SELECT,
										selectLine);
							}
						}
						
					} catch (JSONException e) {
						Logger.error("tangl",
								"music parameters is error or cannot parse");
						musicMap.clear();
					}
				} else {
					Logger.error("tangl", "music parameters is null");
					musicMap.clear();
				}
			} else if (ProductConstants.FUN_TYPE_SECUTIRY.equals(funType)) {
				Logger.error("tangl", "securiety");
				if ("open".equals(ppo.getOperation())) {
					securityList.add(String.valueOf((pf.getFunId())));
					securityList.add(StaticConstant.OPERATE_OPEN_SECURITY);
				} else if ("close".equals(ppo.getOperation())) {
					securityList.add(String.valueOf((pf.getFunId())));
					securityList.add(StaticConstant.OPERATE_CLOSE_SECURITY);
				}

			} else if (ProductConstants.FUN_TYPE_UFO1.equals(funType) ||
					ProductConstants.FUN_TYPE_HUMAN_CAPTURE_NEW2.equals(funType)) {
				if ("send".equals(ppo.getOperation())) {
					redList.add(pf.getFunUnit());
				}
			} else if (ProductConstants.FUN_TYPE_POP_LIGHT.equals(funType)) {
				String desc = ppo.getParaDesc();
				if (!CommUtil.isEmpty(desc)) {
					
				}
			} else if (ProductConstants.FUN_TYPE_WIRELESS_SOCKET.equals(funType)||
					ProductConstants.FUN_TYPE_ONE_SWITCH.equals(funType)) {
				if ("off".equals(ppo.getOperation())) {
					wirelessSocketOffList.add(pf.getFunUnit());
				} else if ("on".equals(ppo.getOperation())) {
					wirelessSocketOnList.add(pf.getFunUnit());
				}
			}// zj:普通网关在线控制，如果需要对模式中其他类型的设备做扩展，在这里添加代码 else if()...
		}

		StringBuilder _sb = new StringBuilder();
		if (openList.size() > 0) {
			List<String> cmd = CommonMethod.createCmdWithNotAccount(customerId,
					address485, "open", openList, funType, null);
			if (cmd != null && cmd.size() > 0) {
				_sb.append("|" + cmd.get(0));
			}
		}
		if (closeList.size() > 0) {
			List<String> cmd = CommonMethod.createCmdWithNotAccount(customerId,
					address485, "close", closeList, funType, null);
			if (cmd != null && cmd.size() > 0) {
				_sb.append("|" + cmd.get(0));
			}
		}
		if (setList.size() > 0) {
			List<String> cmd = CommonMethod.createCmdWithNotAccount(customerId,
					address485, StaticConstant.OPERATE_SET_COLOR, setList,
					funType, null);
			if (cmd != null && cmd.size() > 0) {
				_sb.append("|" + cmd.get(0));
			}
		}
		if (musicMap.size() > 0) {
			String operation = (String) musicMap.get("operation");
			List<String> cmd = CommonMethod.createCmdWithNotAccount(customerId,
					address485, operation, null, operation, musicMap);
			if (cmd != null && cmd.size() > 0) {
				// _sb.append("|" + cmd.get(0));
				for (String c : cmd) {
					_sb.append("|" + c);
				}
			}
		}
		if (upList.size() > 0) {
			List<String> cmd = CommonMethod.createCmdWithNotAccount(customerId,
					address485, StaticConstant.OPERATE_UP, upList, funType, null);
			if (cmd != null && cmd.size() > 0) {
				_sb.append("|" + cmd.get(0));
			}
		}
		if (downList.size() > 0) {
			List<String> cmd = CommonMethod.createCmdWithNotAccount(customerId,
					address485, StaticConstant.OPERATE_DOWN, downList, funType,
					null);
			if (cmd != null && cmd.size() > 0) {
				_sb.append("|" + cmd.get(0));
			}
		}
		if (securityList.size() > 1) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(StaticConstant.PARAM_FUN_ID, securityList.get(0));
			List<String> cmd = CommonMethod
					.createCmdWithNotAccount(customerId, address485,
							securityList.get(1), securityList, funType, map);
			if (cmd != null && cmd.size() > 0) {
				_sb.append("|" + cmd.get(0));
			}
		}
		if (redList.size() > 0) {
			for(int i = 0; i < redList.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(StaticConstant.PARAM_INDEX, redList.get(i));
				List<String> cmd = CommonMethod.createCmdWithNotAccount(customerId,
						address485, null, securityList, funType, map);
				if (cmd != null && cmd.size() > 0) {
					_sb.append("|" + cmd.get(0));
				}
			}
		}
		if(wirelessSocketOffList.size() > 0) {
			Map<String, Object> map = new HashMap<String, Object>();
			List<String> cmd = CommonMethod.createCmdWithNotAccount(customerId,
					address485, "off", securityList, funType, map);
			if (cmd != null && cmd.size() > 0) {
				for(String c : cmd) _sb.append("|" + c);
			}
		}
		if(wirelessSocketOnList.size() > 0) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("dst", "0x01");
			map.put("src", "0x01");
			List<String> cmd = CommonMethod.createCmdWithNotAccount(customerId,
					address485, "on", securityList, funType, map);
			if (cmd != null && cmd.size() > 0) {
				for(String c : cmd) _sb.append("|" + c);
			}
		}
		
		result = _sb.toString();
		Logger.warn("tangl", _sb.toString());
		return result;
	}
	
	/**
	 * 模式功能操作生成指令字节码
	 * 
	 * @param ppo
	 * @return
	 */
	public static List<byte[]> productPatternOperationToCMD1(Context con,
			List<ProductPatternOperation> ppoList) {
		String cmdStr = "";
		StringBuffer _sb = new StringBuffer();
		// 获取用户ID
//		String customerId = CommUtil.getMainAccount();
		/********2016-01-27 修改多网关指令*********/
		String whId485 = AppUtil.getGetwayWhIdByProductWhId(con, ppoList.get(0).getWhId());
		/*************END**********************/
		// 循环生成指令CMD，并转换成一个字符串
		for (ProductPatternOperation _ppo : ppoList) {
			String _operation = _ppo.getOperation();
			String addr485 = "";
			String funType = "";
			List<String> funUnits = new ArrayList<String>();
			ProductFunDaoImpl _pfDaoImpl = new ProductFunDaoImpl(con);
			ProductFun _pf = _pfDaoImpl.get(_ppo.getFunId());
			if (_pf != null) {
				funType = _pf.getFunType();
				funUnits.add(_pf.getFunUnit());
				CustomerProductDaoImpl cpDaoImpl = new CustomerProductDaoImpl(
						con);
				List<CustomerProduct> cpList = cpDaoImpl.find(null, "whId=?",
						new String[] { _pf.getWhId() }, null, null, null, null);
				if (cpList != null && cpList.size() > 0) {
					CustomerProduct cp = cpList.get(0);
					if (cp != null) {
						addr485 = cp.getAddress485();
					}
				}
			}
			List<String> cmd = CommonMethod.createCmdWithNotAccount(whId485,
					addr485, _operation, funUnits, funType, null);
			if (cmd != null && cmd.size() > 0) {
				_sb.append("|" + cmd.get(0));
			}
		}
		cmdStr = _sb.toString();
		List<byte[]> cmd = CommonMethod.createCmdListWithLength(whId485,
				cmdStr);
		return cmd;
	}
	

	/**
	 * 模式功能操作生成指令字节码
	 * 
	 * @param ppo
	 * @return
	 */
	public static List<byte[]> productPatternOperationToCMD(Context con,
			List<ProductPatternOperation> ppoList) {
		String cmdStr = "";
		StringBuffer _sb = new StringBuffer();
		String customerId = "";
		// 获取用户ID
		CustomerDaoImpl cDaoImpl = new CustomerDaoImpl(con);
		List<Customer> cList = cDaoImpl.find();
		if (cList != null && cList.size() > 0) {
			Customer c = cList.get(0);
			if (c != null) {
				customerId = c.getCustomerId();
			}
		}
		// 循环生成指令CMD，并转换成一个字符串
		for (ProductPatternOperation _ppo : ppoList) {
			String _operation = _ppo.getOperation();
			String addr485 = "";
			String funType = "";
			List<String> funUnits = new ArrayList<String>();
			ProductFunDaoImpl _pfDaoImpl = new ProductFunDaoImpl(con);
			ProductFun _pf = _pfDaoImpl.get(_ppo.getFunId());
			if (_pf != null) {
				funType = _pf.getFunType();
				funUnits.add(_pf.getFunUnit());
				CustomerProductDaoImpl cpDaoImpl = new CustomerProductDaoImpl(
						con);
				List<CustomerProduct> cpList = cpDaoImpl.find(null, "whId=?",
						new String[] { _pf.getWhId() }, null, null, null, null);
				if (cpList != null && cpList.size() > 0) {
					CustomerProduct cp = cpList.get(0);
					if (cp != null) {
						addr485 = cp.getAddress485();
					}
				}
			}
			List<String> cmd = CommonMethod.createCmdWithNotAccount(customerId,
					addr485, _operation, funUnits, funType, null);
			if (cmd != null && cmd.size() > 0) {
				_sb.append("|" + cmd.get(0));
			}
		}
		cmdStr = _sb.toString();
		List<byte[]> cmd = CommonMethod.createCmdListWithLength(customerId,
				cmdStr);
		return cmd;
	}
	
	/**
	 * 生成无线网关命令
	 */
	public static List<byte[]> productPatternOperationToCMD2(Context con,
			List<ProductPatternOperation> ppoList) {
		List<byte[]> cmds = new ArrayList<byte[]>();
		
		// 循环生成指令CMD，并转换成一个字符串
		for (ProductPatternOperation _ppo : ppoList) {
			String _operation = _ppo.getOperation();
			String addr485 = "";
			String funType = "";
			List<String> funUnits = new ArrayList<String>();
			ProductFunDaoImpl _pfDaoImpl = new ProductFunDaoImpl(con);
			ProductFun _pf = _pfDaoImpl.get(_ppo.getFunId());
			if (_pf != null) {
				funType = _pf.getFunType();
				funUnits.add(_pf.getFunUnit());
				CustomerProductDaoImpl cpDaoImpl = new CustomerProductDaoImpl(
						con);
				List<CustomerProduct> cpList = cpDaoImpl.find(null, "whId=?",
						new String[] { _pf.getWhId() }, null, null, null, null);
				if (cpList != null && cpList.size() > 0) {
					CustomerProduct cp = cpList.get(0);
					if (cp != null) {
						addr485 = cp.getAddress485();
					}
				}
			}
			
			String zegbingWhid = AppUtil.getGetwayWhIdByProductWhId(con, _pf.getWhId());
			List<String> cmdList = CommonMethod.createCmdWithNotAccount(zegbingWhid,
					addr485, _operation, funUnits, funType, null);
			for(String cmd : cmdList) {
				List<byte[]> lengthCmd = CommonMethod.createCmdListWithLength(zegbingWhid,
						cmd);
				cmds.addAll(lengthCmd);
			}
		}
		return cmds;
	}

	/**
	 * 彩灯控制操作生成指令字节码
	 * 
	 * @param con
	 * @param productFun
	 * @param funDetail
	 * @return
	 */
	public static List<byte[]> lightFunToCMD(Context con,
			ProductFun productFun, FunDetail funDetail, String lightColor) {
		if (productFun == null || funDetail == null)
			return null;

		String type = getType(con, productFun, funDetail);
		List<String> _lightColors = new ArrayList<String>();
		_lightColors.add(lightColor);
		CustomerProductDaoImpl cpDaoImpl = new CustomerProductDaoImpl(con);
		List<CustomerProduct> cpList = cpDaoImpl.find(null, "whId=?",
				new String[] { productFun.getWhId() }, null, null, null, null);
		String addr485 = "";
		if (cpList != null && cpList.size() > 0) {
			CustomerProduct cp = cpList.get(0);
			if (cp != null) {
				addr485 = cp.getAddress485();
			}
		}

//		String customerId = CommUtil.getMainAccount();
		/********2016-01-27 修改多网关指令*********/
		String whId485 = AppUtil.getGetwayWhIdByProductWhId(con, productFun.getWhId());
		/*************END**********************/
		List<byte[]> cmd = CommonMethod.createCmdWithLength(whId485,
				addr485, type, _lightColors, productFun.getFunType(), null);
		return cmd;
	}
	
	/**
	 * 彩灯控制操作生成指令字节码
	 * 
	 * @param con
	 * @param productFun
	 * @param funDetail
	 * @return
	 */
	public static List<byte[]> lightFunToCMD2(Context con,
			ProductFun productFun, FunDetail funDetail, String lightColor, String type) {
		Logger.debug(null, "generate pop light cmd");
		if (productFun == null || funDetail == null) {
			Logger.error(null, "error parameters: productFun or funDetail is null");
			return null;
		}

		Map<String, Object> params = new HashMap<String, Object>();
		if("mvToLevel".equals(type)) {
			params.put("light", StringUtils.integerToHexString(productFun.getBrightness()));
			params.put("time", 1);
		}else if("hueandsat".equals(type)) {
			if(lightColor.length() >= 9) {
				float[] huesat = StringUtils.rgb2hsb(Integer.parseInt(lightColor.substring(0, 3)), 
						Integer.parseInt(lightColor.substring(3, 6)), Integer.parseInt(lightColor.substring(6, 9)));
				params.put("hue", StringUtils.integerToHexString(Math.round(huesat[0])));
				params.put("sat", StringUtils.integerToHexString(Math.round(huesat[1])));
				params.put("light", StringUtils.integerToHexString(Math.round(huesat[2])));
				params.put("time", 1);
			}else {
				Logger.error(null, "color set error");
				return null;
			}
		}
		
		List<String> _lightColors = new ArrayList<String>();
		_lightColors.add(lightColor);
		CustomerProductDaoImpl cpDaoImpl = new CustomerProductDaoImpl(con);
		List<CustomerProduct> cpList = cpDaoImpl.find(null, "whId=?",
				new String[] { productFun.getWhId() }, null, null, null, null);
		String addr485 = "";
		if (cpList != null && cpList.size() > 0) {
			CustomerProduct cp = cpList.get(0);
			if (cp != null) {
				addr485 = cp.getAddress485();
			}
		}

//		String customerId = "";
//		CustomerDaoImpl cDaoImpl = new CustomerDaoImpl(con);
//		List<Customer> cList = cDaoImpl.find();
//		if (cList != null && cList.size() > 0) {
//			Customer c = cList.get(0);
//			if (c != null) {
//				customerId = c.getCustomerId();
//			}
//		}
		/********2016-01-27 修改多网关指令*********/
		String whId485 = AppUtil.getGetwayWhIdByProductWhId(con, productFun.getWhId());
		/*************END**********************/
		List<byte[]> cmd = CommonMethod.createCmdWithLength(whId485,
				addr485, type, _lightColors, productFun.getFunType(), params);
		return cmd;
	}

	/**
	 * 生成彩灯变换模式命令
	 * 
	 * @param addr485
	 * @param params
	 * @return
	 */
	public static List<byte[]> createColorModeCmd(Context con,List<ProductFun> proFunlist,
			FunDetail funDetail,Map <String , Object> params) {
		if (proFunlist == null || funDetail == null) {
			return null;
		}
		String customerId = CommUtil.getMainAccount();// 用户ID
		StringBuffer _sb = new StringBuffer();
//		CustomerDaoImpl cDaoImpl = new CustomerDaoImpl(con);
//		List<Customer> cList = cDaoImpl.find();
//		if (cList != null && cList.size() > 0) {
//			Customer c = cList.get(0);
//			if (c != null) {
//				if (c.getAccountType() == 0) {//判断是否子账号， 0：子账号， 1：主账号
//					customerId = c.getCreateUser();
//				}else{
//					customerId = c.getCustomerId();
//				}
//			}
//		}
		// 设置控制模式
		String type = StaticConstant.OPERATE_SET_CHANGEMODE;
		String funType = funDetail.getFunType();
		CustomerProductDaoImpl cpDaoImpl = new CustomerProductDaoImpl(con);
		List<CustomerProduct> tempList = new ArrayList<CustomerProduct>();
		for (int i = 0; i < proFunlist.size(); i++) {
			List<CustomerProduct> cpList = cpDaoImpl.find(null, "whId=?",
					new String[] { proFunlist.get(i).getWhId() }, null, null,
					null, null);
			if (cpList != null && cpList.size() > 0) {
				tempList.add(cpList.get(0));
			}
		}
		List<String> tempCmd = new ArrayList<String>();
		if (tempList != null && tempList.size() > 0) {
			for (CustomerProduct cp : tempList) {
				if (cp != null) {
					String addr485 = cp.getAddress485();
					List<String> cmd = CommonMethod.createCmdWithNotAccount(
							customerId, addr485, type, null, funType, params);
					if (cmd != null && cmd.size() > 0) {
						tempCmd.add(cmd.get(0));
					}
				}
			}
		}
		if (tempCmd.size() > 0) {
			for (int i = 0; i < tempCmd.size(); i++) {
				_sb.append("|" + tempCmd.get(i));
			}
		}
		List<byte[]> _cmd = CommonMethod.createCmdListWithLength(customerId,
				_sb.toString());
		return _cmd;
	}

	/**
	 * @param con
	 * @param productFun
	 * @param funDetail
	 * @return 获取控制命令
	 */
	public static String getType(Context con, ProductFun productFun,
			FunDetail funDetail) {
		String type = "";
		if (productFun == null || funDetail == null)
			return null;
		// /////test窗帘/////////
		if (funDetail.getFunType().equals(
				ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT)) {
			if (funDetail.getShortcutOpen() == null
					&& funDetail.getShortcutClose() == null) {
				funDetail.setShortcutOpen("open");
				funDetail.setShortcutClose("close");
			}
			type = productFun.isOpen() ? funDetail.getShortcutClose()
					: funDetail.getShortcutOpen();
		} else if(funDetail.getFunType().equals(
				ProductConstants.FUN_TYPE_AUTO_CHA_ZUO)) {
			if (funDetail.getShortcutOpen() == null
					&& funDetail.getShortcutClose() == null) {
				funDetail.setShortcutOpen("open");
				funDetail.setShortcutClose("close");
			}
			type = productFun.isOpen() ? funDetail.getShortcutClose()
					: funDetail.getShortcutOpen();
		} else if (funDetail.getFunType().equals(
				ProductConstants.FUN_TYPE_MASTER_CONTROLLER_CURTAIN)) {
			if (funDetail.getShortcutOpen() == null
					&& funDetail.getShortcutClose() == null) {
				funDetail.setShortcutOpen("down");
				funDetail.setShortcutClose("up");
			}
			String temType = CurtainFragment.getType();
			if (temType != "") {
				type = temType;
				CurtainFragment.setType("");
			} else {
				type = productFun.isOpen() ? "down"
						: "up";
			}
		} else if (funDetail.getFunType()
				.equals(ProductConstants.FUN_TYPE_UFO1)) {
			// funDetail.setShortcutOpen("send");
			// funDetail.setShortcutClose("learn");
			// type = productFun.isOpen() ? funDetail.getShortcutClose() :
			// funDetail.getShortcutOpen();
			type = StaticConstant.OPERATE_SEND;
		} else if (funDetail.getFunType().equals(
				ProductConstants.FUN_TYPE_AUTO_LOCK)) {
			type = productFun.isOpen() ? "close": "open";
		} else if (funDetail.getFunType().equals(
				ProductConstants.FUN_TYPE_COLOR_LIGHT)) {
			type = StaticConstant.OPERATE_SET_COLOR;
		} else if (funDetail.getFunType().equals(
				ProductConstants.FUN_TYPE_GATEWAY)) {
			type = "setInput";
		} else if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_POP_LIGHT) || 
				funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CRYSTAL_LIGHT) ||
				funDetail.getFunType().equals(ProductConstants.FUN_TYPE_CEILING_LIGHT)||
				funDetail.getFunType().equals(ProductConstants.FUN_TYPE_WIRELESS_SEND_LIGHT)) {//新增无线射灯
			type = "mvToLevel";
		}else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_GATEWAY_VOICE_LANGUAGE)){
			type = "voiceRole";
		}else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_WIRELESS_GATEWAY)){
			type = "networking";
		}else if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_ZG_LOCK)) {
			type = "open";
		} else if(funDetail.getFunType().equals(ProductConstants.FUN_TYPE_DOUBLE_SWITCH) ||
				funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH) ||
				funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE) ||
				funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR) ||
				funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE) ||
				funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO) ||
				funDetail.getFunType().equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE) ||
				funDetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_SIX) ||
				funDetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE) ||
				funDetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR) ||
				funDetail.getFunType().equals(ProductConstants.FUN_TYPE_THREE_SWITCH_THREE) ||
				funDetail.getFunType().equals(ProductConstants.FUN_TYPE_FIVE_SWITCH)){
			type = productFun.isOpen() ? "off": "on";
		} else if (funDetail.getFunType().equals(ProductConstants.FUN_TYPE_PLUTO_SOUND_BOX)) {
			type = StaticConstant.OPERATE_COMMON_CMD;
		}
		return type;
	}

	// 使用 Stack方式--模式功能操作生成指令字节码
	/*
	 * public static List<byte[]> productPatternOperationToCMD(Context con,
	 * ProductPatternOperation ppo) { if (ppo == null) return null; String
	 * _operation = ppo.getOperation(); String addr485 = ""; String customerId =
	 * ""; String funType = ""; List<String> funUnits = new ArrayList<String>();
	 * ProductFunDaoImpl _pfDaoImpl = new ProductFunDaoImpl(con); ProductFun _pf
	 * = _pfDaoImpl.get(ppo.getFunId()); if (_pf != null) { funType =
	 * _pf.getFunType(); funUnits.add(_pf.getFunUnit()); CustomerProductDaoImpl
	 * cpDaoImpl = new CustomerProductDaoImpl(con); List<CustomerProduct> cpList
	 * = cpDaoImpl.find(null, "whId=?", new String[] { _pf.getWhId() }, null,
	 * null, null, null); if (cpList != null && cpList.size() > 0) {
	 * CustomerProduct cp = cpList.get(0); if (cp != null) { addr485 =
	 * cp.getAddress485(); } } } CustomerDaoImpl cDaoImpl = new
	 * CustomerDaoImpl(con); List<Customer> cList = cDaoImpl.find(); if (cList
	 * != null && cList.size() > 0) { Customer c = cList.get(0); if (c != null)
	 * { customerId = c.getCustomerId(); } } List<byte[]> cmd =
	 * CommonMethod.createCmdWithLength(customerId, addr485, _operation,
	 * funUnits, funType, null); return cmd; }
	 */

	/**
	 * 生成原始命令(不带账号)
	 * 
	 * @param account
	 *            账户
	 * @param addr485
	 *            功能点的485地址
	 * @param type
	 *            命令操作（在生成命令的原始文件中定义的，如灯的“open”“close”）
	 * @param funUnits
	 *            功能点的funUnits参数
	 * @param funType
	 *            功能点的类型（对应于ProductConstants）中的各个类型值
	 * @param params
	 *            生成命令需要的参数
	 * @return 生成的原始命令
	 */
	
	public static List<String> createCmdWithNotAccount(String account,
			String addr485, String type, List<String> funUnits, String funType,
			Map<String, Object> params) {
		Map<String, Object> map = null;
		if (params == null) {
			map = new HashMap<String, Object>();
		} else {
			map = params;
		}
		/*****************指令包对象****************/
		CmdEntry cmdEntry = new CmdPolicy();
		try {
			cmdEntry.setCmdInputStream(JxshApp.getContext().
					getResources().getAssets().open("cmd.ini"),
						JxshApp.getContext().getResources().getAssets().open("cmdModel.ini"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		/****************************************/

		List<String> _cmdList = null;

		if (funType.equals(ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT) ||
				funType.equals(ProductConstants.FUN_TYPE_AUTO_CHA_ZUO)) {// 灯
			map.put(StaticConstant.PARAM_UNITS, funUnits);
			_cmdList = cmdEntry.getCmd(GenMCLight.class,type, addr485, map);
		}  else if (funType.equals(ProductConstants.FUN_TYPE_CURTAIN)) {// 窗帘（新接口）
			_cmdList = cmdEntry.getCmd(GenCurtain.class,type, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_HUMAN_CAPTURE)) {// 人体感应

		} else if (funType.equals(ProductConstants.FUN_TYPE_TFT_LIGHT)) {// 触摸屏

		} else if (funType.equals(ProductConstants.FUN_TYPE_POWER_AMPLIFIER)) {// 功放

		} else if (funType.equals(ProductConstants.FUN_TYPE_UFO1)) {// 飞碟一号
			map.put(StaticConstant.PARAM_INDEX, params.get(StaticConstant.PARAM_INDEX));
			_cmdList = cmdEntry.getCmd(GenUFO01.class,StaticConstant.OPERATE_SEND, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_UFO1_TEMP_HUMI)) {// 飞碟一号:温度和湿度
			_cmdList = cmdEntry.getCmd(GenUFO01.class,StaticConstant.OPERATE_GET_ENV, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_HUMAN_CAPTURE_NEW2)) {// 新人体感应遥控控制
			_cmdList = cmdEntry.getCmd(GenHumanSense.class,
					StaticConstant.OPERATE_SEND, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_AUTO_LOCK)) {// 自动锁
			map.put("units", funUnits);
			_cmdList = cmdEntry.getCmd(GenLock.class,type, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_AUTO_LOCK_SEARCH)) {// 自动锁状态
			_cmdList = cmdEntry.getCmd(GenLock.class,StaticConstant.OPERATE_SEARCH, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_AUTO_LOCK_OPEN)) {// 打开自动锁
			_cmdList = cmdEntry.getCmd(GenLock.class,
					StaticConstant.OPERATE_OPEN, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_AUTO_LOCK_CLOSE)) {// 关闭自动锁
			_cmdList = cmdEntry.getCmd(GenLock.class,
					StaticConstant.OPERATE_CLOSE, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_GATEWAY)) {// 网关 -文字
			map.put(StaticConstant.PARAM_TEXT, params.get(StaticConstant.PARAM_TEXT));
			addr485 = (String) map.get(StaticConstant.PARAM_ADDR);
			if(!TextUtils.isEmpty((String)params.get(StaticConstant.PARAM_TEXT)))
				_cmdList = cmdEntry.getCmd(GenGateway.class,StaticConstant.OPERATE_TEXT2SPEECH,addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_GATEWAY_WATCH)) {// 网关:打开语音智能控制
			addr485 = (String) map.get(StaticConstant.PARAM_ADDR);
			_cmdList = cmdEntry.getCmd(GenGateway.class, "watch",null, null);
		} else if (funType.equals(ProductConstants.FUN_TYPE_SECUTIRY)) {// 网关:安防操作
			_cmdList = cmdEntry.getCmd(GenGateway.class,type,addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_GESTURE)) {// 网关:手势识别
			_cmdList = cmdEntry.getCmd(GenGateway.class,type,addr485,map);
			Logger.debug(null, type + "," + addr485 + "," + map.get("usbSd"));
			Logger.debug(null, "cmd size:" + _cmdList.size());
		} else if (funType.equals(ProductConstants.FUN_TYPE_COLOR_LIGHT)) { // 彩灯
			if (map.size() < 1) {
				if (funUnits != null) {
					String color = funUnits.get(0);
					map.put(StaticConstant.PARAM_COLOR, color);
				}
			}
			_cmdList = cmdEntry.getCmd(GenColorLight.class,type, addr485, map);
		} else if (funType.equals(ProductConstants.POWER_AMPLIFIER_SOUND_PLAY)) { // 功放-播放
			_cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
					StaticConstant.OPERATE_PLAY_LUSHU, addr485, map);
		} else if (funType.equals(ProductConstants.POWER_AMPLIFIER_SOUND_PAUSE)) { // 功放-停止
			_cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
					StaticConstant.OPERATE_PAUSE, addr485, map);
		} else if (funType.equals(ProductConstants.POWER_AMPLIFIER_MUTE)) { // 功放-静音
			_cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
					StaticConstant.OPERATE_MUTE, addr485, map);
		} else if (funType.equals(ProductConstants.POWER_AMPLIFIER_UNMUTE)) { // 功放-取消静音
			_cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
					StaticConstant.OPERATE_UNMUTE, addr485, map);
		} else if (funType.equals(ProductConstants.POWER_AMPLIFIER_PROVIOUS)) { // 功放-上一曲
			_cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
					StaticConstant.OPERATE_PRE, addr485, map);
		} else if (funType.equals(ProductConstants.POWER_AMPLIFIER_NEXT)) { // 功放-下一曲
			_cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
					StaticConstant.OPERATE_NEXT, addr485, map);
		} else if (funType.equals(ProductConstants.POWER_AMPLIFIER_SOUND_ADD)) { // 功放-音量+
			_cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
					StaticConstant.OPERATE_SOUND_ADD, addr485, map);
		} else if (funType.equals(ProductConstants.POWER_AMPLIFIER_SOUND_SUB)) { // 功放-音量-
			_cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
					StaticConstant.OPERATE_SOUND_SUB, addr485, map);
		} else if (funType.equals(ProductConstants.POWER_AMPLIFIER_SOUND_LIST)) { // 功放-获取播放列表
			_cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
					StaticConstant.OPERATE_COUNT, addr485, map);
		} else if (funType.equals(ProductConstants.POWER_AMPLIFIER_SOUND_SONG)) { // 功放-获取播放歌曲名字
			_cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
					StaticConstant.OPERATE_READ_NAME, addr485, map);
		} else if (funType
				.equals(ProductConstants.POWER_AMPLIFIER_SOUND_PLAY_SONG)) { // 功放-播放指定歌曲
			_cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
					StaticConstant.OPERATE_USBSD_PLAY, addr485, map);
		} else if (funType
				.equals(ProductConstants.POWER_AMPLIFIER_SOUND_REPEAT_ALL)) { // 功放-列表循环
			_cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
					StaticConstant.OPERATE_REPEAT_ALL, addr485, map);
		} else if (funType
				.equals(ProductConstants.POWER_AMPLIFIER_SOUND_REPEAT_SINGLE)) { // 功放-单曲循环
			_cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
					StaticConstant.OPERATE_REPEAT_SINGLE, addr485, map);
		} else if (funType
				.equals(ProductConstants.POWER_AMPLIFIER_DEVICE_LINK)) { // 功放-获取连接状态
			_cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
					StaticConstant.OPERATE_GET_LINK, addr485, map);
		}else if (funType
				.equals(ProductConstants.POWER_AMPLIFIER_GET_VOLUME)) { // 功放-获取音量
			_cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
					StaticConstant.OPERATE_GET_VOLUMN, addr485, map);
		}else if (funType
				.equals(ProductConstants.POWER_AMPLIFIER_SET_VOLUME)) { // 功放-设置音量
			_cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
					StaticConstant.OPERATE_SET_VOLUMN, addr485, map);
		}else if (funType
				.equals(ProductConstants.POWER_AMPLIFIER_PLAY_STATUS)) { // 功放-获取播放状态
			_cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
					StaticConstant.OPERATE_PLAY_STATUS, addr485, map);
		}else if (funType
				.equals(ProductConstants.POWER_AMPLIFIER_GET_VERSION)) { // 功放-获取功放版本
			_cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
					StaticConstant.OPERATE_GET_VERSION, addr485, map);
		}else if (funType
				.equals(ProductConstants.POWER_AMPLIFIER_GET_BIND)) { // 功放-获取扬声器绑定
			_cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
					StaticConstant.OPERATE_GET_BIND, addr485, map);
		}else if (funType
				.equals(ProductConstants.POWER_AMPLIFIER_GET_MUTE_STATUS)) { // 功放-扬声器静音状态
			_cmdList = cmdEntry.getCmd(GenPowerAmplifier2.class,
					StaticConstant.OPERATE_GET_MUTE_STATUS, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_MIC)) {
			_cmdList = new ArrayList<String>();
			_cmdList.add("USERKSSB");
		} else if (funType.equals(ProductConstants.FUN_TYPE_VOICE)) {
			addr485 = (String) map.get(StaticConstant.PARAM_ADDR);
			_cmdList =  cmdEntry.getCmd(GenGateway.class, "setInput", addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_WIRELESS_SOCKET)) {
			Logger.debug(null, "generate socket");
			map.put("dst", "0x01");
			map.put("src", "0x01");
			_cmdList =  cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_ONE_SWITCH)) {
			map.put("dst", "0x01");
			map.put("src", "0x01");
			_cmdList =  cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_DOUBLE_SWITCH)) {//双向开关
			_cmdList =  cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH)||
				funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE)||
				funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR)||
				funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE)||
				funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO)||
				funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE)) {//多路开关
			Map<String,Object> map1 = new HashMap<>();
			if(map.get("dst").equals("0x01")){
				map1.clear();
				map1.put("src","0x01");
				map1.put("dst","0x02");
				_cmdList =  cmdEntry.getCmd(GenZGSocket.class, type, addr485, map1);
			}else	if(map.get("dst").equals("0x02")){
				map1.clear();
				map1.put("src","0x01");
				map1.put("dst","0x01");
				_cmdList =  cmdEntry.getCmd(GenZGSocket.class, type, addr485, map1);
			}else{
				_cmdList =  cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
			}
		}else if (funType.equals(ProductConstants.FUN_TYPE_THREE_SWITCH_THREE)||
				funType.equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR)||
				funType.equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE)||
				funType.equals(ProductConstants.FUN_TYPE_THREE_SWITCH_SIX)) {//三路开关
			_cmdList =  cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_POP_LIGHT)) {//球灯泡
			_cmdList =  cmdEntry.getCmd(GenZGBulb.class, type, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_CRYSTAL_LIGHT)) {//水晶灯
			_cmdList =  cmdEntry.getCmd(GenZGBulb.class, type, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_CEILING_LIGHT)) {//吸顶灯
			_cmdList =  cmdEntry.getCmd(GenZGBulb.class, type, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_WIRELESS_SEND_LIGHT)) {//无线射灯
				_cmdList =  cmdEntry.getCmd(GenZGBulb.class, type, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_LIGHT_BELT)) {//灯带
			_cmdList =  cmdEntry.getCmd(GenZGBulb.class, type, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_WIRELESS_CURTAIN)) {//无线窗帘
			_cmdList =  cmdEntry.getCmd(GenZGCurtain.class, type, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_DEVICE_DETECT)) {// 终端定义 设备巡检funType
			_cmdList = cmdEntry.getCmd(GenGateway.class,"inspection",addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_ZG_DEVICE_DETECT)) {// 终端定义 zg设备巡检funType
			_cmdList = cmdEntry.getCmd(GenZGGateway.class,"inspection",addr485, map);
		}else if(funType.equals(ProductConstants.FUN_TYPE_WIRELESS_GATEWAY)){//无线网关
			_cmdList = cmdEntry.getCmd(GenZGGateway.class,type,addr485, map);
		}else if(funType.equals(ProductConstants.FUN_TYPE_DOOR_CONTACT)){//门磁
			_cmdList = cmdEntry.getCmd(GenZGGateway.class,type,addr485, map);
		}else if(funType.equals(ProductConstants.FUN_TYPE_INFRARED)){//ZG人体感应
			_cmdList = cmdEntry.getCmd(GenZGGateway.class,type,addr485, map);
		}else if(funType.equals(ProductConstants.FUN_TYPE_SMOKE_SENSE)){//烟感
			_cmdList = cmdEntry.getCmd(GenZGGateway.class,type,addr485, map);
		}else if(funType.equals(ProductConstants.FUN_TYPE_GAS_SENSE)){//气感
			_cmdList = cmdEntry.getCmd(GenZGGateway.class,type,addr485, map);
		}else if(funType.equals(ProductConstants.FUN_TYPE_AIRCONDITION)){//空调控制
			_cmdList = cmdEntry.getCmd(GenZGAirController.class,type,addr485, map);
		}else if(funType.equals(ProductConstants.FUN_TYPE_ZG_LOCK) ){// ZG 锁
			_cmdList =  cmdEntry.getCmd(GenZGLock.class, type, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_PLUTO_SOUND_BOX)) {// Pluto音箱
			_cmdList = cmdEntry.getCmd(GenZGCommon.class, type, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_FIVE_SWITCH)) {//五路开关
			_cmdList =  cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_WIRELESS_AIRCODITION_OUTLET)) {//无线智能空调控制
			if (type==StaticConstant.OPERATE_COMMON_CMD) {
				_cmdList =  cmdEntry.getCmd(GenZGCommon.class, type, addr485, map);
			} else  {
				_cmdList =  cmdEntry.getCmd(GenZGAirConditionSocket.class, type, addr485, map);//读开关状态
			}
		}else if (funType.equals(ProductConstants.FUN_TYPE_WIRELESS_INFRARED_TRANDPOND)) {//无线红外转发
			_cmdList =  cmdEntry.getCmd(GenZGCommon.class, type, addr485, map);
		}
		if (_cmdList != null) {
			for (int i = 0; i < _cmdList.size(); i++) {
				StringBuffer sb = new StringBuffer();
				sb.append(_cmdList.get(i));
				_cmdList.set(i, sb.toString());
				System.out.println("original cmd-->>>" + sb);
			}
		}

		return _cmdList;
	}

	/**
	 * 通过指令算法生成(带用户名)的指令
	 * @param account
	 * @param addr485
	 * @param type
	 * @param lightNumbers
	 * @param funType
	 * @param params
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List<String> createCmdWithAccount(String account,
			String addr485, String type, List<String> lightNumbers,
			String funType, Map<String, Object> params) {
		Map<String, Object> map = null;
		if (params == null) {
			map = new HashMap<String, Object>();
		} else {
			map = params;
		}
		/*****************指令包对象****************/
		CmdEntry cmdEntry = new CmdPolicy();//指令包对象
		try {
			cmdEntry.setCmdInputStream(JxshApp.getContext().
					getResources().getAssets().open("cmd.ini"),
						JxshApp.getContext().getResources().getAssets().open("cmdModel.ini"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		/******************************************/
		
		List<String> _cmdList = null;

		if (funType.equals(ProductConstants.FUN_TYPE_MASTER_CONTROLLER_LIGHT) ||
				funType.equals(ProductConstants.FUN_TYPE_AUTO_CHA_ZUO)) {// 灯
			map.put(StaticConstant.PARAM_UNITS, lightNumbers);
			_cmdList = cmdEntry.getCmd(GenMCLight.class,type, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_MASTER_CONTROLLER_CURTAIN)) {// 新窗帘
			map.put(StaticConstant.PARAM_TEXT, params.get(StaticConstant.PARAM_TEXT));
			_cmdList = cmdEntry.getCmd(GenCurtain.class,type, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_HUMAN_CAPTURE)) {// 人体感应

		} else if (funType.equals(ProductConstants.FUN_TYPE_TFT_LIGHT)) {// 触摸屏

		} else if (funType.equals(ProductConstants.FUN_TYPE_POWER_AMPLIFIER)) {// 功放

		} else if (funType.equals(ProductConstants.FUN_TYPE_UFO1)) {// 飞碟一号
			map.put(StaticConstant.PARAM_INDEX, params.get(StaticConstant.PARAM_INDEX));
			_cmdList = cmdEntry.getCmd(GenUFO01.class,
					StaticConstant.OPERATE_SEND, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_UFO1_TEMP_HUMI)) {// 飞碟一号:温度和湿度
			_cmdList = cmdEntry.getCmd(GenUFO01.class,
					StaticConstant.OPERATE_GET_ENV, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_HUMAN_CAPTURE_NEW2)) {// 新人体感应遥控控制
			_cmdList = cmdEntry.getCmd(GenHumanSense.class,
					StaticConstant.OPERATE_SEND, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_AUTO_LOCK)) {// 自动锁
			map.put("units", lightNumbers);
//			_cmdList = GenLock.generateCmd(type, addr485, map);
			_cmdList = cmdEntry.getCmd(GenLock.class,type, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_AUTO_LOCK_SEARCH)) {// 自动锁状态
//			_cmdList = GenLock
//					.generateCmd(GenLock.OPERATE_SEARCH, addr485, map);
			_cmdList = cmdEntry.getCmd(GenLock.class,
					StaticConstant.OPERATE_SEARCH, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_AUTO_LOCK_OPEN)) {// 打开自动锁
//			_cmdList = GenLock.generateCmd(GenLock.OPERATE_OPEN, addr485, map);
			_cmdList = cmdEntry.getCmd(GenLock.class,
					StaticConstant.OPERATE_OPEN, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_AUTO_LOCK_CLOSE)) {// 关闭自动锁
//			_cmdList = GenLock.generateCmd(GenLock.OPERATE_CLOSE, addr485, map);
			_cmdList = cmdEntry.getCmd(GenLock.class,
					StaticConstant.OPERATE_CLOSE, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_GATEWAY)) {// 网关 文字-->语音
			map.put(StaticConstant.PARAM_TEXT, params.get(StaticConstant.PARAM_TEXT));
//			_cmdList = GenGateway.generateCmd(addr485,
//					GenGateway.OPERATE_TEXT2SPEECH, map);
			if(!TextUtils.isEmpty((String)params.get(StaticConstant.PARAM_TEXT)))
				_cmdList = cmdEntry.getCmd(GenGateway.class, StaticConstant.OPERATE_TEXT2SPEECH, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_GATEWAY_VOICE_LANGUAGE)) {// 网关:方言切换
			if(!TextUtils.isEmpty((String)params.get(StaticConstant.PARAM_TEXT)))
			_cmdList = cmdEntry.getCmd(GenGateway.class,StaticConstant.OPERATE_VOICE_ROLE, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_GATEWAY_WATCH)) {// 网关:打开语音智能控制
//			_cmdList = GenGateway.generateCmd(null, GenGateway.OPERATE_WATCH,
//					map);
			_cmdList = cmdEntry.getCmd(GenGateway.class,
					StaticConstant.OPERATE_WATCH, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_SECUTIRY)) {// 网关:安防操作
//			_cmdList = GenGateway.generateCmd(null, type, map);
			_cmdList = cmdEntry.getCmd(GenGateway.class, type, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_GESTURE)) {// 网关:手势识别
//			_cmdList = GenGateway.generateCmd(addr485, type, map);
			_cmdList = cmdEntry.getCmd(GenGateway.class,type,addr485,map);
			Logger.debug(null, type + "," + addr485 + "," + map.get("usbSd"));
			Logger.debug(null, "cmd size:" + _cmdList.size());
		} else if (funType.equals(ProductConstants.FUN_TYPE_COLOR_LIGHT)) {// 彩灯
			if (lightNumbers != null) {
				String color = lightNumbers.get(0);
				map.put(StaticConstant.PARAM_COLOR, color);
			}
//			_cmdList = GenColorLight.generateCmd(type, addr485, map);
			_cmdList = cmdEntry.getCmd(GenColorLight.class,type, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_MIC)) {
			_cmdList = new ArrayList<String>();
			_cmdList.add("USERKSSB");
		} else if (funType.equals(ProductConstants.FUN_TYPE_DEVICE_DETECT)) {// 终端定义 设备巡检funType
			_cmdList = cmdEntry.getCmd(GenGateway.class,"inspection",addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_ZG_DEVICE_DETECT)) {// 终端定义 zg设备巡检funType
			_cmdList = cmdEntry.getCmd(GenZGGateway.class,"inspection",addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_VOICE)) {
			_cmdList =  cmdEntry.getCmd(GenGateway.class, "setInput", addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_WIRELESS_SOCKET)) {
			map.put("dst", "0x01");
			map.put("src", "0x01");
			_cmdList =  cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_ONE_SWITCH)) {
			map.put("dst", "0x01");
			map.put("src", "0x01");
			_cmdList =  cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
		} else if (funType.equals(ProductConstants.FUN_TYPE_POP_LIGHT)) {
			map.put("src", "0x01");
			map.put("dst", "0x01");
			_cmdList =  cmdEntry.getCmd(GenZGBulb.class, type, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_CEILING_LIGHT)) {
			map.put("src", "0x01");
			map.put("dst", "0x01");
			_cmdList =  cmdEntry.getCmd(GenZGBulb.class, type, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_CRYSTAL_LIGHT)) {
			map.put("src", "0x01");
			map.put("dst", "0x01");
			_cmdList =  cmdEntry.getCmd(GenZGBulb.class, type, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_LIGHT_BELT)) {
			map.put("src", "0x01");
			map.put("dst", "0x01");
			_cmdList =  cmdEntry.getCmd(GenZGBulb.class, type, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_WIRELESS_SEND_LIGHT)) {//无线射灯
			map.put("src", "0x01");
			map.put("dst", "0x01");
			_cmdList =  cmdEntry.getCmd(GenZGBulb.class, type, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_WIRELESS_CURTAIN)) {
			_cmdList =  cmdEntry.getCmd(GenZGCurtain.class, type, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_DOUBLE_SWITCH)) {//双向开关
			_cmdList =  cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_THREE_SWITCH_THREE)||
				funType.equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FOUR)||
				funType.equals(ProductConstants.FUN_TYPE_THREE_SWITCH_FIVE)||
				funType.equals(ProductConstants.FUN_TYPE_THREE_SWITCH_SIX)) {//三路开关
			_cmdList =  cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH)||
				funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FIVE)||
				funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_FOUR)||
				funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_THREE)||
				funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_TWO)||
				funType.equals(ProductConstants.FUN_TYPE_MULITIPLE_SWITCH_ONE)) {//多路开关
			Map<String,Object> map1 = new HashMap<>();
			if(map.get("dst").equals("0x01")){
				map1.clear();
				map1.put("src","0x01");
				map1.put("dst","0x02");
				_cmdList =  cmdEntry.getCmd(GenZGSocket.class, type, addr485, map1);
			}else	if(map.get("dst").equals("0x02")){
				map1.clear();
				map1.put("src","0x01");
				map1.put("dst","0x01");
				_cmdList =  cmdEntry.getCmd(GenZGSocket.class, type, addr485, map1);
			}else{
				_cmdList =  cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
			}
		}else if(funType.equals(ProductConstants.FUN_TYPE_ZG_LOCK) ){// ZG 锁
			_cmdList =  cmdEntry.getCmd(GenZGLock.class, type, addr485, map);
		}else if(funType.equals(ProductConstants.FUN_TYPE_PLUTO_SOUND_BOX)){//Pluto音箱
			map.put("src", "0x01");
			map.put("dst", "0x01");
			_cmdList =  cmdEntry.getCmd(GenZGCommon.class, type, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_FIVE_SWITCH)) {//五路开关
			_cmdList =  cmdEntry.getCmd(GenZGSocket.class, type, addr485, map);
		}else if (funType.equals(ProductConstants.FUN_TYPE_WIRELESS_AIRCODITION_OUTLET)) {//无线智能空调控制
			if (type==StaticConstant.OPERATE_COMMON_CMD) {
				_cmdList =  cmdEntry.getCmd(GenZGCommon.class, type, addr485, map);
			} else  {
				_cmdList =  cmdEntry.getCmd(GenZGAirConditionSocket.class, type, addr485, map);//读开关状态
			}
		}else if (funType.equals(ProductConstants.FUN_TYPE_WIRELESS_INFRARED_TRANDPOND)) {//无线红外转发
			map.put("src", "0x01");
			map.put("dst", "0x01");
			_cmdList =  cmdEntry.getCmd(GenZGCommon.class, type, addr485, map);
		}
		if (_cmdList != null) {
			for (int i = 0; i < _cmdList.size(); i++) {
				StringBuffer sb = new StringBuffer(account);
				sb.append("|");
				sb.append(_cmdList.get(i));
				_cmdList.set(i, sb.toString());
				System.out.println("original cmd-->>>" + sb);
			}
		}
		return _cmdList;
	}

	/**
	 * 生成编码后的离线模式命令
	 * 
	 * @param cmds
	 *            原始命令
	 * @return 编码后的命令
	 */
	public static List<byte[]> createCmdOffline(List<String> cmds) {
		List<byte[]> byteList = new ArrayList<byte[]>();

		if (cmds == null || cmds.size() < 1) {
			Logger.error("CommonMethod",
					"parameter in method createCmdOffline() is null");
			return byteList;
		}

		for (int i = 0; i < cmds.size(); i++) {
			String temp = cmds.get(i);
			PackageMessage pm = new PackageMessage(temp);
			LocalEncoderVersion10 lev = new LocalEncoderVersion10(pm);
			Logger.debug("offline.encode", "offline cmd:" + lev.encode());
			try {
				byteList.add(lev.encode().getBytes(ProductConstants.CHARSET_UTF8));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return byteList;
	}
	
	/**
	 * 生成编码后的离线模式命令
	 * 
	 * @param cmds
	 *            原始命令
	 * @return 编码后的命令
	 */
	public static List<byte[]> createCmdOffline2(List<String> cmds) {
		List<byte[]> byteList = new ArrayList<byte[]>();

		if (cmds == null || cmds.size() < 1) {
			Logger.error("CommonMethod",
					"parameter in method createCmdOffline2() is null");
			return byteList;
		}

		for (int i = 0; i < cmds.size(); i++) {
			String temp = cmds.get(i);
			PackageMessage2 pm = new PackageMessage2(temp);
			ZigbeeEncoderVersion10 lev = new ZigbeeEncoderVersion10(pm);
			Logger.debug("offline.encode2", "offline cmd:" + lev.encode());
			try {
				byteList.add(lev.encode().getBytes(ProductConstants.CHARSET_UTF8));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return byteList;
	}

	/**
	 * 生成编码后的离线模式命令
	 * 
	 * @param cmds
	 *            原始命令
	 * @return 编码后的命令
	 */
	public static List<byte[]> createCmdOffline(String cmdStr) {
		List<byte[]> byteList = new ArrayList<byte[]>();

		if (cmdStr == null || cmdStr.length() < 1) {
			Logger.error("CommonMethod",
					"parameter in method createCmdOffline() is null");
			return byteList;
		}

		PackageMessage pm = new PackageMessage(cmdStr);
		LocalEncoderVersion10 lev = new LocalEncoderVersion10(pm);

		Logger.debug("offline.encode", "offline cmd:" + lev.encode());

		try {
			byteList.add(lev.encode().getBytes(ProductConstants.CHARSET_UTF8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return byteList;
	}

	/**
	 * 生成编码后的离线模式命令
	 * 
	 * @param cmds
	 *            原始命令
	 * @return 编码后的命令
	 */
	public static String createCmdOfflineVersion10(String cmdStr) {
		if (cmdStr == null || cmdStr.length() < 1) {
			Logger.error("CommonMethod",
					"parameter in method createCmdOffline() is null");
			return null;
		}

		PackageMessage pm = new PackageMessage(cmdStr);
		LocalEncoderVersion10 lev = new LocalEncoderVersion10(pm);

		return lev.encode();
	}

	// 生成多条带长度的指令时调用此方法
	@SuppressWarnings("finally")
	public static List<byte[]> createCmdListWithLength(String account,
			String _cmd) {
		List<byte[]> byteList = new ArrayList<byte[]>();
		StringBuffer sb = new StringBuffer(account);
		sb.append(_cmd);
		String cmd = sb.toString();
		try {
			Logger.debug(null, "-->" + _cmd);
			int len = cmd.getBytes("utf-8").length;
			ByteBuffer bbf = ByteBuffer.allocate(len + 8);
			bbf.putInt(1); // 命令或响应类型，0=web向平台发送执行命令请求/应答，
							// 1=手机向平台发送执行命令请求/应答,
							// 2= web向平台发送查询命令请求/应答，
							// 3=手机向平台发送查询命令请求/应答，
							// 4=平台主动向终端推送消息
			bbf.putInt(len);
			bbf.put(cmd.getBytes("utf-8"));
			bbf.flip();
			byteList.add(bbf.array());
			
			System.out.println("send cmd-->>>" + new String(bbf.array()));
			System.out.println(byte2hex(bbf.array()));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			return byteList;
		}
	}
	
	private static String byte2hex(byte [] buffer){  
        String h = "";  
          
        for(int i = 0; i < buffer.length; i++){  
            String temp = Integer.toHexString(buffer[i] & 0xFF);  
            if(temp.length() == 1){  
                temp = "0" + temp;  
            }  
            h = h + " "+ temp;  
        }  
          
        return h;  
          
    }  
	@Deprecated
	/**
	 * 生成带长度的指令 replace("\\|", ",");
	 * @param customerId	用户id
	 * @param addr485		设备的485地址
	 * @param operation		操作
	 * @param funUnits		设备的funUnits
	 * @param funType		设备的funType
	 * @param params		生成命令需要的参数
	 * @return				生成的命令
	 */
	public static List<byte[]> createCmdWithLength2(String customerId,
			String addr485, String operation, List<String> funUnits,
			String funType, Map<String, Object> params) {
		
		List<byte[]> byteList = new ArrayList<byte[]>();
		
		// 离线模式,返回离线命令
		if (NetworkModeSwitcher.useOfflineMode(JxshApp.getContext())) {
			List<String> _cmdList = createCmdWithNotAccount(customerId,
					addr485, operation, funUnits, funType, params);
			return createCmdOffline(_cmdList);
		}
		
		// 在线模式，返回平台命令
		List<String> _cmdList = createCmdWithAccount(customerId, addr485,
				operation, funUnits, funType, params);
		try {
			if (_cmdList != null) {

				for (int i = 0; i < _cmdList.size(); i++) {
					int len;
					String _cmd = _cmdList.get(i).toString().replace("\\|", ",");
					Logger.debug(null, "-->" + _cmd);

					len = _cmd.getBytes("utf-8").length;

					ByteBuffer bbf = ByteBuffer.allocate(len + 8);
					bbf.putInt(1); 
					bbf.putInt(len);
					bbf.put(_cmd.getBytes("utf-8"));
					bbf.flip();
					byteList.add(bbf.array());
					
					System.out.println("send cmd-->>>" + new String(bbf.array()));
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		
		return byteList;
	}

	/**
	 * 生成带长度的指令(带账号)
	 * @param customerId	用户id
	 * @param addr485		设备的485地址
	 * @param operation		操作
	 * @param funUnits		设备的funUnits
	 * @param funType		设备的funType
	 * @param params		生成命令需要的参数
	 * @return				生成的命令
	 */
	public static List<byte[]> createCmdWithLength(String customerId,
			String addr485, String operation, List<String> funUnits,
			String funType, Map<String, Object> params) {
		
		List<byte[]> byteList = new ArrayList<byte[]>();
		
		// 离线模式,返回离线命令
		if (NetworkModeSwitcher.useOfflineMode(JxshApp.getContext())) {
			List<String> _cmdList = createCmdWithNotAccount(customerId,
					addr485, operation, funUnits, funType, params);
			return createCmdOffline(_cmdList);
		}
		
		// 在线模式，返回平台命令
		List<String> _cmdList = createCmdWithAccount(customerId, addr485,
				operation, funUnits, funType, params);
		try {
			if (_cmdList != null) {

				for (int i = 0; i < _cmdList.size(); i++) {
					int len;
					String _cmd = _cmdList.get(i).toString();
					Logger.debug(null, "-->" + _cmd);

					len = _cmd.getBytes("utf-8").length;
					
					ByteBuffer bbf = ByteBuffer.allocate(len + 8);
					bbf.putInt(1);
					bbf.putInt(len);
					bbf.put(_cmd.getBytes("utf-8"));
					bbf.flip();
					byteList.add(bbf.array());
					
					System.out.println("send cmd-->>>" + new String(bbf.array()));
					StringUtils.printHexString("send cmd-->>>", bbf.array());
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		
		return byteList;
	}

	/***********************************/

	/**
	 * String转成int颜色值
	 * 
	 * @param str
	 * @return
	 */
	public static int getIntFromString(String str) {
		int mColor = 0;
		if (str == null || str == "")
			return 0;
		int r = Integer.parseInt(setStringToInt(str.substring(0, 3)));
		int g = Integer.parseInt(setStringToInt(str.substring(3, 6)));
		int b = Integer.parseInt(setStringToInt(str.substring(6, 9)));
		int a = Integer.parseInt(setStringToInt(str.substring(9, 11)));
		mColor = Color.argb(a, r, g, b);
		return mColor;
	}

	/**
	 * 去掉以“00”和“0”开头的字符串前的“0”
	 * 
	 * @param s
	 * @return
	 */
	public static String setStringToInt(String s) {
		if (TextUtils.isEmpty(s)) {
			return null;
		}
		String temp = "";
		if (s.startsWith("00")) {
			temp = s.substring(2);
			return temp;
		} else if (s.startsWith("0")) {
			temp = s.substring(1);
			return temp;
		} else {
			temp = s;
		}
		return temp;
	}

}
