package net.loyin.util;

/**
 * OnlineUserManager.java of oschina.net
 * 作者: Winter Lau
 * 时间: 2008-5-29
 */

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 在线用户管理器
 * FIXME: 当同时在线用户数太多的时候（超过十万），可能会占用太大的内存
 */
public class OnlineUserManager {
	
	private static final Log log = LogFactory.getLog(OnlineUserManager.class);
	
	private final static String CACHE = "online-users";
	private final static int INTERVAL = 60000; //one minute
	private final static int SESSION_TIMEOUT = 30 * 60 * 1000;	//会话超时时间30分钟
	private static Timer timer;	

	/**
	 * 启动在线用户状态维护线程
	 */
	public synchronized static void init() {
		if(timer==null){
			timer = new Timer("OnlineUserManager", true);
			timer.schedule(new TimerTask(){
				@Override
				public void run() {
					//扫描出数据库中所有在线的用户名
					try{
						/*long curTime = System.currentTimeMillis();
						List<Long> users = User.ListOnlineUsers();
						for(long userid : users) {
							long lastActTime = getLastActiveTime(userid);
							if(curTime - lastActTime >= SESSION_TIMEOUT){//会话已经超时
//								User.Logout(userid);
								setLastActiveTime(userid, -1L);
								log.info("=> Login Session[UID:"+userid+"] timeout, logout it.");
							}
						}*/
					}finally{
//						DBManager.closeConnection();
					}
				}}, SESSION_TIMEOUT, INTERVAL);//30分钟后开始，每分钟扫描一次
			log.info("=============> OnlineUserManager started. <=============");
		}
	}
	
	/**
	 * 停止在线用户状态维护线程
	 */
	public synchronized static void destroy(){
		if(timer != null){
			timer.cancel();
			timer = null;
			log.info("=============> OnlineUserManager stopped. <=============");
		}
	}
	
	/**
	 * 在缓存中激活用户
	 * @param userid
	 * @param time;
	 */
	public final static void setLastActiveTime(long userid, long time) {
		/*if(time > 0)
			CacheManager.set(CACHE, userid, time);
		else
			CacheManager.evict(CACHE, userid);
			*/
	}
	
	/**
	 * 返回上一次用户活动的时间
	 * @param userid
	 * @return
	 */
	/*private final static long getLastActiveTime(long userid) {
		Long lastTime = (Long)CacheManager.get(CACHE, userid);
		return (lastTime!=null)?lastTime:-1L;
	}*/
	
}

