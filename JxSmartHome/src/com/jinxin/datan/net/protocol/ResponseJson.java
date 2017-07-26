package com.jinxin.datan.net.protocol;

import com.jinxin.datan.net.module.RemoteJsonResultInfo;

import org.json.JSONException;
import org.json.JSONObject;

import com.jinxin.jxsmarthome.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
/**
 * json解析器父类
 * @author JackeyZhang
 *
 */
public abstract class ResponseJson {
    private RemoteJsonResultInfo resultInfo = null;
//    private JSONObject jsonObject = null;
//    public JSONObject getJsonObject() {
//        return jsonObject;
//    }
//    public void setJsonObject(JSONObject jsonObject) {
//        this.jsonObject = jsonObject;
//    }
	/**
	 * post方式请求时需实现加入请求字节数据
	 * 
	 * @return
	 */
	public abstract byte[] toOutputBytes();
	/**
	 * 解析方法
	 * @param in
	 * @throws Exception
	 */
    public abstract void response (InputStream in)throws Exception;
    public RemoteJsonResultInfo getResultInfo(){
        return this.resultInfo;
    }
    public void setResultInfo(RemoteJsonResultInfo resultInfo){
        this.resultInfo = resultInfo;
    }
    public void closeInputStream(InputStream in) {
        if (in != null) {
            try {
                in.close();
                in = null;
            } catch (Exception ex) {
            	Logger.error("ResponseJson", ex.toString());
            }
        }
    }
    /**
     * 从输入流转json
     * @return
     */
    public JSONObject getJsonObjectFromIn(InputStream in){
        StringBuffer strBuf = new StringBuffer();
        JSONObject dataJson = null;
        BufferedReader buffer = null;
        try {
//        byte[] buf = new byte[1024];
//        int len = 0;
//        while((len = in.read(buf)) >=0){
//            strBuf.append(new String(buf,0,len));
//        }
     // 使用IO流读取数据    
        String line;
        buffer = new BufferedReader(new InputStreamReader(   
                in));   
        while ((line = buffer.readLine()) != null) {   
            strBuf.append(line);  
        }   

          dataJson = new JSONObject(strBuf.toString());
          Logger.error("-->", dataJson.toString());
        }catch (IOException ex) {
            // TODO Auto-generated catch block
        	Logger.error("IOException", ex.toString());
        }catch (JSONException ex) {
            // TODO Auto-generated catch block
            Logger.error("JSONException", ex.toString());
        }finally{
        	if(buffer != null){
        		try {
					buffer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		buffer = null;
        	}
        }
        return dataJson;
    }
    /**
     * 读服务器返回结果
     * @param jsonObject
     * @return
     * @throws JSONException 
     */
    public RemoteJsonResultInfo readResultInfo(JSONObject jsonObject) throws JSONException{ 
        RemoteJsonResultInfo resultInfo = new RemoteJsonResultInfo();
        if(jsonObject != null){
        	JSONObject response = jsonObject.getJSONObject("response");
            resultInfo.validResultCode = response.getString("rspCode");
            resultInfo.validResultInfo = response.getString("rspDesc");
        }
        this.setResultInfo(resultInfo);
        return resultInfo;
    }
    /**
     * 封装取json字段，避免字段不存的异常
     * @param jobj
     * @param key
     * @return
     * @throws JSONException
     */
    public String getJsonString(JSONObject jobj,String key) throws JSONException{
    	String _str = "";
    	if(jobj == null || key == null)return _str;
    	return jobj.has(key) ? jobj.getString(key) : _str;
    }
    /**
     * 封装取json字段，避免字段不存的异常
     * @param jobj
     * @param key
     * @return
     * @throws JSONException
     */
    public int getJsonInt(JSONObject jobj,String key) throws JSONException{
    	int _int = -1;
    	if(jobj == null || key == null)return _int;
    	return jobj.has(key) ? jobj.getInt(key) : _int;
    }
    /**
     * 封装取json字段，避免字段不存的异常
     * @param jobj
     * @param key
     * @return
     * @throws JSONException
     */
    public double getJsonDouble(JSONObject jobj,String key) throws JSONException{
    	double _double = -1.0;
    	if(jobj == null || key == null)return _double;
    	return jobj.has(key) ? jobj.getInt(key) : _double;
    }
}
