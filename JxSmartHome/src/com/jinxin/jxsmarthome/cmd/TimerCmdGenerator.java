package com.jinxin.jxsmarthome.cmd;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.jinxin.db.impl.ProductPatternOperationDaoImpl;
import com.jinxin.db.impl.TimerTaskOperationDaoImpl;
import com.jinxin.jxsmarthome.cmd.entity.Command;
import com.jinxin.jxsmarthome.entity.ProductPatternOperation;
import com.jinxin.jxsmarthome.entity.TimerTaskOperation;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 定时器模式命令生成器
 * @author TangLong
 * @company 金鑫智慧
 */
public class TimerCmdGenerator extends CmdGenerator {
	private Context context;
	
	public TimerCmdGenerator(Context context) {
		this.context = context;
	}
	
	/**
	 * 根据定时模式的id生成命令集合(离线)
	 * @param taskId	定时模式的id
	 * @return	生成的命令集合
	 */
	public List<Command> generateOfflineCmd(int taskId) {
		Logger.debug(null, "generateCmd for timer:" + taskId);
		List<Command> cmdList = new ArrayList<Command>();
		
		TimerTaskOperationDaoImpl dao = new TimerTaskOperationDaoImpl(context);
		List<TimerTaskOperation> list = dao.find(null, "taskId=?", new String[]{String.valueOf(taskId)}, 
				null, null, null, null);
		List<ProductPatternOperation> tempPatternListCmd = new ArrayList<ProductPatternOperation>();
		if(list != null && list.size() > 0) {
			for(TimerTaskOperation tto : list) {
				int operationType = tto.getOperationType();
				
				// 模式
				if(1 == operationType) {
					int patternId = tto.getPatternId();
					ProductPatternOperationDaoImpl ppoDao = new ProductPatternOperationDaoImpl(context);
					List<ProductPatternOperation> ppos = ppoDao.find(null, "patternId=?", 
							new String[]{String.valueOf(patternId)}, null, null, null, null);
					tempPatternListCmd.addAll(ppos);
				
					// 设备
				}else if(2 == operationType) {
					ProductPatternOperation ppo = new ProductPatternOperation();
					ppo.setFunId(tto.getFunId());
					ppo.setOperation(tto.getOperation());
					ppo.setParaDesc(tto.getParaDesc());
					ppo.setPatternId(tto.getPatternId());
					ppo.setUpdateTime(tto.getUpdateTime());
					ppo.setWhId(tto.getWhId());
					tempPatternListCmd.add(ppo);
				}
			}
		}
		
		OfflineCmdGenerator cmdGenerator = new OfflineCmdGenerator();
		cmdList = cmdGenerator.generateProductPatternOperationCommand(context, 
				tempPatternListCmd, null);
		
		return cmdList;
	}
	
	/**
	 * 根据定时模式的id生成命令集合(在线)
	 * @param taskId	定时模式的id
	 * @return	生成的命令集合
	 */
	public List<Command> generateOnlineCmd(int taskId) {
		Logger.debug(null, "generateCmd for timer:" + taskId);
		List<Command> cmdList = new ArrayList<Command>();
		
		TimerTaskOperationDaoImpl dao = new TimerTaskOperationDaoImpl(context);
		List<TimerTaskOperation> list = dao.find(null, "taskId=?", new String[]{String.valueOf(taskId)}, 
				null, null, null, null);
		
		List<ProductPatternOperation> tempPatternListCmd = new ArrayList<ProductPatternOperation>();
		if(list != null && list.size() > 0) {
			for(TimerTaskOperation tto : list) {
				int operationType = tto.getOperationType();
				// 模式
				if(1 == operationType) {
					int patternId = tto.getPatternId();
					ProductPatternOperationDaoImpl ppoDao = new ProductPatternOperationDaoImpl(context);
					
					List<ProductPatternOperation> ppos = ppoDao.find(null, "patternId=?", 
							new String[]{String.valueOf(patternId)}, null, null, null, null);
					tempPatternListCmd.addAll(ppos);
				
				// 设备
				}else if(2 == operationType) {
					ProductPatternOperation ppo = new ProductPatternOperation();
					ppo.setFunId(tto.getFunId());
					ppo.setOperation(tto.getOperation());
					ppo.setParaDesc(tto.getParaDesc());
					ppo.setPatternId(tto.getPatternId());
					ppo.setUpdateTime(tto.getUpdateTime());
					ppo.setWhId(tto.getWhId());
					tempPatternListCmd.add(ppo);
				}
			}
		}
		
		OnlineCmdGenerator cmdGenerator = new OnlineCmdGenerator();
		cmdList = cmdGenerator.generateProductPatternOperationCommand(context, 
				tempPatternListCmd, null);
		
		return cmdList;
	}
}
