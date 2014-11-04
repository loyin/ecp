/**
 * 微信公众平台开发模式(JAVA) SDK
 * (c) 2012-2013 ____′↘夏悸 <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/wechat
 */
package com.gson;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gson.bean.Articles;
import com.gson.bean.Attachment;
import com.gson.bean.InMessage;
import com.gson.bean.OutMessage;
import com.gson.inf.MessageProcessingHandler;
import com.gson.oauth.Group;
import com.gson.oauth.Menu;
import com.gson.oauth.Message;
import com.gson.oauth.Qrcod;
import com.gson.oauth.User;
import com.gson.util.ConfKit;
import com.gson.util.HttpKit;
import com.gson.util.Tools;
import com.gson.util.XStreamFactory;
import com.thoughtworks.xstream.XStream;

/**
 * 微信常用的API
 *
 * @author L.cm & ____′↘夏悸
 * @date 2013-11-5 下午3:01:20
 */
public class WeChat {
	private static final String ACCESSTOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";
	private static final String PAYFEEDBACK_URL = "https://api.weixin.qq.com/payfeedback/update";
	private static final String DEFAULT_HANDLER = "com.gson.inf.DefaultMessageProcessingHandlerImpl";
	private static final String GET_MEDIA_URL= "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=";
	private static final String UPLOAD_MEDIA_URL= "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=";
    
    private static Class<?>  messageProcessingHandlerClazz = null;
    /**
     * 消息操作接口
     */
    public static final Message message = new Message();
    /**
     * 菜单操作接口
     */
    public static final Menu menu = new Menu();
    /**
     * 用户操作接口
     */
    public static final User user = new User();
    /**
     * 分组操作接口
     */
    public static final Group group = new Group();
    
    /**
     * 二维码操作接口
     */
    public static final Qrcod qrcod = new Qrcod();
    /**微信临时存放参数*/
	public static Map<String,Object> weChatConfig=new HashMap<String,Object>();
    /**
     * 获取access_token
     * @return
     * @throws Exception
     */
    public static String getAccessToken() throws Exception {
    	Long nowtime=new Date().getTime();
		if ((weChatConfig.isEmpty() || weChatConfig.keySet().isEmpty())
				|| (nowtime > ((Long) weChatConfig.get("exptime")))) {
			return getAccessToken(ConfKit.get("AppId"),ConfKit.get("AppSecret"));
		} else {
			return (String) weChatConfig.get("access_token");
		}
    }
    /**
     * 获取access_token
     * @return
     * @throws Exception
     */
    private static String getAccessToken(String appid,String secret) throws Exception {
        String jsonStr = HttpKit.get(ACCESSTOKEN_URL.concat("&appid=") + appid + "&secret=" + secret);
        Map<String, Object> map = JSONObject.parseObject(jsonStr);
        weChatConfig.put("access_token",map.get("access_token"));
		weChatConfig.put("exptime",new Date().getTime()+7200);
        return map.get("access_token").toString();
    }

   /**
    * 支付反馈
    * @param openid
    * @param feedbackid
    * @return
    * @throws Exception
    */
    public static boolean payfeedback(String openid, String feedbackid) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        String accessToken = getAccessToken();
        map.put("access_token", accessToken);
        map.put("openid", openid);
        map.put("feedbackid", feedbackid);
        String jsonStr = HttpKit.get(PAYFEEDBACK_URL, map);
        Map<String, Object> jsonMap = JSONObject.parseObject(jsonStr);
        return "0".equals(jsonMap.get("errcode").toString());
    }

    /**
     * 签名检查
     * @param token
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     */
    public static Boolean checkSignature(String token, String signature, String timestamp, String nonce) {
        return Tools.checkSignature(token, signature, timestamp, nonce);
    }

    /**
     * 根据接收到用户消息进行处理
     * @param responseInputString   微信发送过来的xml消息体
     * @return
     */
    public static String processing(String responseInputString) {
        InMessage inMessage = parsingInMessage(responseInputString);
        OutMessage oms = null;
        // 加载处理器
        if (messageProcessingHandlerClazz == null) {
            // 获取自定消息处理器，如果自定义处理器则使用默认处理器。
            String handler = ConfKit.get("MessageProcessingHandlerImpl");
            handler = handler == null ? DEFAULT_HANDLER : handler;
            try {
            	messageProcessingHandlerClazz = Thread.currentThread().getContextClassLoader().loadClass(handler);
            } catch (Exception e) {
                throw new RuntimeException("messageProcessingHandler Load Error！");
            }
        }
        String xml = "";
        try {
        	MessageProcessingHandler messageProcessingHandler = (MessageProcessingHandler) messageProcessingHandlerClazz.newInstance();
            //取得消息类型
            String type = inMessage.getMsgType();
            Method getOutMessage = messageProcessingHandler.getClass().getMethod("getOutMessage");
            Method alMt = messageProcessingHandler.getClass().getMethod("allType", InMessage.class);
            Method mt = messageProcessingHandler.getClass().getMethod(type + "TypeMsg", InMessage.class);
            
            alMt.invoke(messageProcessingHandler, inMessage);
           
            if(mt != null){
            	mt.invoke(messageProcessingHandler, inMessage);
            }
            
            Object obj = getOutMessage.invoke(messageProcessingHandler);
            if(obj != null){
            	oms = (OutMessage) obj;
            }
            //调用事后处理
            try {
            	Method aftMt =  messageProcessingHandler.getClass().getMethod("afterProcess",InMessage.class,OutMessage.class);
            	aftMt.invoke(messageProcessingHandler, inMessage, oms);
			} catch (Exception e) {}
            
            obj = getOutMessage.invoke(messageProcessingHandler);
            if(obj != null){
            	oms = (OutMessage) obj;
            	setMsgInfo(oms, inMessage);
            }
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }
        if(oms != null){
            // 把发送发送对象转换为xml输出
            XStream xs = XStreamFactory.init(true);
            xs.alias("xml", oms.getClass());
            xs.alias("item", Articles.class);
            xml = xs.toXML(oms);
        }
        return xml;
    }

    /**
     * 设置发送消息体
     * @param oms
     * @param msg
     * @throws Exception
     */
    private static void setMsgInfo(OutMessage oms,InMessage msg) throws Exception {
    	if(oms != null){
    		Class<?> outMsg = oms.getClass().getSuperclass();
            Field CreateTime = outMsg.getDeclaredField("CreateTime");
            Field ToUserName = outMsg.getDeclaredField("ToUserName");
            Field FromUserName = outMsg.getDeclaredField("FromUserName");

            ToUserName.setAccessible(true);
            CreateTime.setAccessible(true);
            FromUserName.setAccessible(true);

            CreateTime.set(oms, new Date().getTime());
            ToUserName.set(oms, msg.getFromUserName());
            FromUserName.set(oms, msg.getToUserName());
    	}
    }

    /**
     *消息体转换
     * @param responseInputString
     * @return
     */
    private static InMessage parsingInMessage(String responseInputString) {
        //转换微信post过来的xml内容
        XStream xs = XStreamFactory.init(false);
        xs.ignoreUnknownElements();
        xs.alias("xml", InMessage.class);
        InMessage msg = (InMessage) xs.fromXML(responseInputString);
        return msg;
    }
    
    /**
     * 获取媒体资源
     * @param accessToken
     * @param mediaId
     * @return
     * @throws IOException
     */
    public static Attachment getMedia(String accessToken,String mediaId) throws IOException{
    	String url = GET_MEDIA_URL + accessToken + "&media_id=" + mediaId;
        return HttpKit.download(url);
    }
    
    /**
     * 上传素材文件
     * @param type
     * @param file
     * @return
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
	public static Map<String, Object> uploadMedia(String accessToken,String type,File file) throws KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        String url = UPLOAD_MEDIA_URL + accessToken +"&type="+type;
        String jsonStr = HttpKit.upload(url,file);
        return JSON.parseObject(jsonStr, Map.class);
    }
    
    /**
     * 判断是否来自微信, 5.0 之后的支持微信支付
     * @param request
     * @return
     */
 	public static boolean isWeiXin(HttpServletRequest request) {
 		String userAgent = request.getHeader("User-Agent");
 		if (StringUtils.isNotBlank(userAgent)) {
 			Pattern p = Pattern.compile("MicroMessenger/(\\d+).+");
 			Matcher m = p.matcher(userAgent);
 			String version = null;
 			if(m.find()){
 				version = m.group(1);
 			}
 			return (null != version && NumberUtils.toInt(version) >= 5); 
 		}
 		return false;
 	}
}
