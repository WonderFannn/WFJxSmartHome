package com.jinxin.datan.net.protocol;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jinxin.datan.net.module.RemoteJsonResultInfo;
import com.jinxin.datan.toolkit.task.Task;
import com.jinxin.jxsmarthome.constant.ControlDefine;
import com.jinxin.jxsmarthome.entity.ProductFun;
import com.jinxin.jxsmarthome.entity.ProductState;
import com.jinxin.record.SharedDB;

/**
 * 设备状态列表解析
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class ProductStateList extends ResponseJson {
	private Task task = null;
	private byte[] requestBytes;

	public ProductStateList(Task task, byte[] requestBytes) {
		this.task = task;
		this.requestBytes = requestBytes;
	}

	@Override
	public void response(InputStream in) throws Exception {
		if (in != null) {
			boolean isSuccess = false;
			RemoteJsonResultInfo _resultInfo = null;

			List<ProductState> productStateList = null;
			try {
				JSONObject jsonObject = this.getJsonObjectFromIn(in);

				_resultInfo = this.readResultInfo(jsonObject);
				if (_resultInfo.validResultCode.equals(ControlDefine.CONNECTION_SUCCESS)) {// 成功获取数据
					// 具体的解析工作在这里实现
					// 提示：任务中途检测任务是否被中止，同时可以直接返回任务取消数据（如有必要）
					if (this.task.ismTryCancel())
						return;
					// ////具体解析区////////////////////////
					// 取更新时间
//					String _processTime = this.getJsonString(jsonObject,
//							"processTime");
//					String _account = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, ControlDefine.KEY_ACCOUNT,"");
//					SharedDB.saveStrDB(_account,
//							ControlDefine.KEY_PRODUCT_STATE_LIST, _processTime);
					// 取列表数据
//					JSONArray jsonArray = jsonObject
//							.getJSONArray("serviceContent");
					JSONObject _jo = jsonObject.getJSONObject("serviceContent");
					productStateList = new ArrayList<ProductState>();
					if (this.task.ismTryCancel())
						return;
//						JSONObject _jo = (JSONObject) jsonArray.get(i);
					
					@SuppressWarnings("unchecked")
					Iterator<String> it = _jo.keys();
					while(it.hasNext()){
						String key = it.next().toString();
						ProductState _ps = new ProductState();
						_ps.setFunId(Integer.parseInt(key));
						_ps.setState( _jo.getString(key));
						productStateList.add(_ps);
					}
					isSuccess = true;
				}
			} catch (JSONException e) {
				isSuccess = false;
			} catch (Exception e) {
				isSuccess = false;
			} finally {
				if (isSuccess) {
					this.task.callback(productStateList);
				} else {
					this.task.onError(_resultInfo.validResultInfo);
				}
				this.closeInputStream(in);// 必须关闭流
			}
		}
	}

	@Override
	public byte[] toOutputBytes() {
		return this.requestBytes;
	}

}
