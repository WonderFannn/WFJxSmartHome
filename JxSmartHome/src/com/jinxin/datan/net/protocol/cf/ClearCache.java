package com.jinxin.datan.net.protocol.cf;


import java.io.IOException;

import com.jinxin.jxsmarthome.util.Logger;

import com.jinxin.datan.net.protocol.CommandFile;
import com.jinxin.datan.toolkit.task.Task;
import com.jinxin.jxsmarthome.main.JxshApp;
import com.jinxin.record.FileManager;
import com.jinxin.record.FileUtil;

/**
 * 清空缓存接口解析
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class ClearCache extends CommandFile {
	private Task task;

	/**
	 * 解析清空缓存接口构造方法
	 * 
	 * @param observer
	 */
	public ClearCache(Task task) {
		this.task = task;
	}

	@Override
	public boolean read() {
		// TODO Auto-generated method stub
		boolean isSuccess = false;
//		this.list = new ArrayList<Object>();
//		FileInputStream fis = null;
//		List<File> _files = FileManager.instance().getFileList(FileManager.TYPE_AD);
//		if(_files != null ){
//			for(int i = 0;i < _files.size();i++){
//				File _file= _files.get(i);
//				if(_file != null){
//					try {
//						fis = new FileInputStream(_file);
//						AdvertInfo _info = this.readVO(fis);
//						if(_info != null){
//							this.list.add(_info);
//						}
//						isSuccess = true;
//					} catch (FileNotFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						isSuccess = false;
//					}finally{
//						closeInputStream(fis);
//					}
//				}
//			}
//		}
//		// 回调
//				if (observer != null) {
//					if (isSuccess) {
//						observer.callback(list);
//					} else {
//						observer.onError(list, "读取缓存文件异常");
//					}
//				}
		return isSuccess;
	}

//	/**
//	 * 读一个单元文件
//	 */
//	public AdvertInfo readVO(InputStream in) {
//		AdvertInfo _info = new AdvertInfo(new AdvertVO());
//
//		try {
//			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//			factory.setNamespaceAware(true);
//			XmlPullParser xmlParser = factory.newPullParser();
//
//			xmlParser.setInput(in, "utf-8");
//
//			int eventType = xmlParser.getEventType();
//			// 一直循环，直到文档结束
//			while (eventType != XmlPullParser.END_DOCUMENT) {
//				switch (eventType) {
//				case XmlPullParser.START_TAG:
//					String tag = xmlParser.getName();
//					if (tag.equalsIgnoreCase("advertVO")) {
//						// 广告位
//						_info.getAdvertVO().setId(xmlParser.getAttributeValue(null, "id"));// ID
//						_info.getAdvertVO().setName(xmlParser.getAttributeValue(null, "name"));// 名字
//						_info.getAdvertVO().setImgUrl(xmlParser.getAttributeValue(null, "imageUrl"));// 图片地址
//					} else if (tag.equalsIgnoreCase("cache")) {
//						_info.getDataCacheInfo().setFilePath(xmlParser.getAttributeValue(null, "filePath"));//文件路径
//						_info.getDataCacheInfo().setThumbnailPath(xmlParser.getAttributeValue(null, "thumbnailPath"));//略缩图路径
//						_info.getDataCacheInfo().setOriginalImgPath(xmlParser.getAttributeValue(null, "originalImgPath"));//原图路径
//					}
//					break;
//				case XmlPullParser.END_TAG:
//					break;
//				default:
//					break;
//				}
//				eventType = xmlParser.next();
//			}
//			return _info;
//		} catch (Exception ex) {
//			// TODO Auto-generated catch block
//			ex.printStackTrace();
//			Logger.error("-读取缓存->", "读取缓存文件异常");
//			return null;
//		} finally {
//			closeInputStream(in);
//		}
//	}

	@Override
	public boolean write() {
		// TODO Auto-generated method stub
		boolean isSuccess = true;
		try {
			//先清图片缓存
			JxshApp.instance.getFinalBitmap().clearCache();
			FileUtil.delFiles(FileManager.instance().getSDPath()+FileManager.PROJECT_NAME+FileManager.CACHE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.error("-清缓存->", "清缓存异常："+e.toString());
			isSuccess = false;
		}
		// 回调
		if (this.task != null) {
			if (isSuccess) {
				this.task.callback("清空缓存成功");
			} else {
				this.task.onError("清空缓存异常");
			}
		}
		return isSuccess;
	}

//	/**
//	 * 写一个单元文件
//	 * 
//	 * @return
//	 */
//	public boolean writeVO(AdvertInfo info) {
//		if (info == null)
//			return false;
//		boolean isSuccess = false;
//		StringWriter stringWriter = new StringWriter();
//		try {
//
//			// 获取XmlSerializer对象
//			XmlPullParserFactory factory;
//			factory = XmlPullParserFactory.newInstance();
//			XmlSerializer xmlSerializer = factory.newSerializer();
//			// 设置输出流对象
//			xmlSerializer.setOutput(stringWriter);
//			/*
//			 * startDocument(String encoding, Boolean standalone)encoding代表编码方式 standalone 用来表示该文件是否呼叫其它外部的文件。 若值是 ”yes” 表示没有呼叫外部规则文件，若值是
//			 * ”no” 则表示有呼叫外部规则文件。默认值是 “yes”。
//			 */
//			xmlSerializer.startDocument("utf-8", true);
//
//			xmlSerializer.startTag(null, "eddhc");
//			xmlSerializer.startTag(null, "advertVO");
//			xmlSerializer.attribute(null, "id", CommUtil.checkNull(info.getAdvertVO().getId()));
//			xmlSerializer.attribute(null, "name", CommUtil.checkNull(info.getAdvertVO().getName()));
//			xmlSerializer.attribute(null, "imageUrl", CommUtil.checkNull(info.getAdvertVO().getImgUrl()));
//			xmlSerializer.endTag(null, "advertVO");
//			xmlSerializer.startTag(null, "cache");
//			if (CommUtil.isNull(info.getDataCacheInfo().getFilePath())) {
//				info.getDataCacheInfo().setFilePath(FileManager.instance().getFilePath(FileManager.TYPE_AD));
//			}
//			xmlSerializer.attribute(null, "filePath", CommUtil.checkNull(info.getDataCacheInfo().getFilePath()));
//			xmlSerializer.attribute(null, "thumbnailPath", CommUtil.checkNull(info.getDataCacheInfo().getThumbnailPath()));
//			xmlSerializer.attribute(null, "originalImgPath", CommUtil.checkNull(info.getDataCacheInfo().getOriginalImgPath()));
//			xmlSerializer.endTag(null, "cache");
//			xmlSerializer.endTag(null, "eddhc");
//			xmlSerializer.endDocument();
//			// 文件输出
//			info.getDataCacheInfo().setFilePath(
//					FileManager.instance().writeFile(info.getAdvertVO().getId(), FileUtil.getStringStream(stringWriter.toString()),
//							FileManager.TYPE_AD));
//			if (CommUtil.isNull(info.getDataCacheInfo().getFilePath()))
//				isSuccess = false;
//			else
//				isSuccess = true;
//		} catch (Exception ex) {
//			// TODO Auto-generated catch block
//			ex.printStackTrace();
//			Logger.error("-写入缓存->", "写入缓存文件异常");
//			isSuccess = false;
//		} finally {
//			// closeInputStream(in);
//			closeStringWriter(stringWriter);
//		}
//		return isSuccess;
//	}
}
