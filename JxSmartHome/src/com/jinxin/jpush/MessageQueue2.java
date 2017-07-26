package com.jinxin.jpush;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 消息队列
 * @author: Guan.yuxuan.CD
 * @date 2014-3-11 下午3:03:45
 * @version V1.0
 */
public class MessageQueue2 {

	private Logger logger = LoggerFactory.getLogger( getClass() );

	private  static MessageQueue2 instance = null;

	private static BlockingQueue <BroadPushMessage> messageQueue = new LinkedBlockingQueue <BroadPushMessage>();
	private Lock lock = new ReentrantLock();
	private final Condition produceCondition = lock.newCondition();
	private final Condition consumeCondition = lock.newCondition();
	
	private static Integer maxMessageSize = 5000;
	private static long timeout = 500;
	
	private MessageQueue2(){
	    
	}
	
	public static MessageQueue2 getInstance(){
	    if(instance == null) {
	        instance = new MessageQueue2();
	        instance.initQueue(maxMessageSize, timeout);
	        instance.logger.info("   Initialize message push queue success，maxMessageSize = {}，put.timeout = {}ms",maxMessageSize,timeout);
	    }
		return instance;
	}
	
	public void initQueue(Integer maxMessageSize,long timeout){
		this.maxMessageSize = maxMessageSize;
		this.timeout = timeout;
	}
	
	public boolean offer ( BroadPushMessage message ) {
		lock.lock();
		boolean status = false;
		try {
			int messageSize = messageQueue.size();
			while ( messageSize == maxMessageSize ) {
				produceCondition.await( timeout , TimeUnit.MILLISECONDS );
				logger.warn( "待推送消息队列已满，增加新消息超时！" );
				break;
			}
			messageSize = messageQueue.size();
			if ( messageSize < maxMessageSize ) {
				status = messageQueue.offer( message );
				logger.error( "offer 推送消息队列消息数为:{}" , messageQueue.size() );
			}
			consumeCondition.signal();
		} catch ( Exception e ) {
			logger.error( "向消息队列添加推送消息失败:",e );
		} finally {
			lock.unlock();
		}

		return status;
	}
    
	public BroadPushMessage poll ( ) {
		logger.debug( "获取待推送消息" );
		lock.lock();
		BroadPushMessage message = null;
		try {
			while ( messageQueue.size()  == 0 ) {
				logger.debug( "即时消息队列中待发送消息数量为 0，线程 {} 进入等待状态！" ,Thread.currentThread().getName());
				consumeCondition.await();
			}
			message = messageQueue.poll();
			logger.error( "poll {} 推送消息队列消息数为:{}" ,message.getContent(), messageQueue.size() );
			produceCondition.signal();
		} catch ( Exception e ) {
			logger.error( "从消息队列获取推送消息失败:" , e );
		} finally {
			lock.unlock();
		}

		return message;
	}
	
	
	public Integer getMaxMessageSize ( ) {
		return maxMessageSize;
	}
	
	public void setMaxMessageSize ( Integer maxMessageSize ) {
		this.maxMessageSize = maxMessageSize;
	}
	
	public long getTimeout ( ) {
		return timeout;
	}
	
	public void setTimeout ( long timeout ) {
		this.timeout = timeout;
	}

	
	
	
}
