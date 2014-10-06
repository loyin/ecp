package net.loyin.util.mail;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

//import net.loyin.model.mail.MailAttach;
import net.loyin.util.FileTool;
import net.loyin.util.PropertiesContent;

import org.apache.commons.lang.StringUtils;

import com.jfinal.log.Logger;

public class ReciveMail {
	public static Logger log = Logger.getLogger(ReciveMail.class);
    private MimeMessage msg = null;
    private String saveAttchPath = "";
    private StringBuffer bodytext = new StringBuffer();
    
    public ReciveMail(MimeMessage msg){
        this.msg = msg;
        }
    public void setMsg(MimeMessage msg) {
        this.msg = msg;
    }
    
    /**
     * 获取发送邮件者信息
     * @return
     * @throws MessagingException
     */
    public String getFrom() throws MessagingException{
        InternetAddress[] address = (InternetAddress[]) msg.getFrom();
        String from = address[0].getAddress();
        if(from == null){
            from = "";
        }
        String personal = address[0].getPersonal();
        if(personal == null){
            personal = "";
        }
        String fromaddr =personal +"<"+from+">";
        return fromaddr;
    }
    
    /**
     * 获取邮件收件人，抄送，密送的地址和信息。根据所传递的参数不同 "to"-->收件人,"cc"-->抄送人地址,"bcc"-->密送地址
     * @param type TO,CC,BCC
     * @return
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public String getMailAddress(String type) throws MessagingException, UnsupportedEncodingException{
        String mailaddr = "";
        String addrType = type.toUpperCase();
        InternetAddress[] address = null;
        
        if(addrType.equals("TO")||addrType.equals("CC")||addrType.equals("BCC")){
            if(addrType.equals("TO")){
                address = (InternetAddress[]) msg.getRecipients(Message.RecipientType.TO);
            }
            if(addrType.equals("CC")){
                address = (InternetAddress[]) msg.getRecipients(Message.RecipientType.CC);
            }
            if(addrType.equals("BCC")){
                address = (InternetAddress[]) msg.getRecipients(Message.RecipientType.BCC);
            }
            
            if(address != null){
                for(int i=0;i<address.length;i++){
                    String mail = address[i].getAddress();
                    if(mail == null){
                        mail = "";
                    }else{
                        mail = MimeUtility.decodeText(mail);
                    }
                    String personal = address[i].getPersonal();
                    if(personal == null){
                        personal = "";
                    }else{
                        personal = MimeUtility.decodeText(personal);
                    }
                    mailaddr += ";"+personal +"<"+mail+">"; 
                }
                mailaddr = mailaddr.substring(1);
            }
        }else{
            throw new RuntimeException("Error email Type!");
        }
        return mailaddr;
    }
    
    /**
     * 获取邮件主题
     * @return
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     */
    public String getSubject() throws UnsupportedEncodingException, MessagingException{
        String subject = "";
        subject = MimeUtility.decodeText(msg.getSubject());
        if(subject == null){
            subject = "";
        }
        return subject;
    }
    
    /**
     * 获取邮件发送日期
     * @return
     * @throws MessagingException
     */
    public Date getSendDate() throws MessagingException{
        return msg.getSentDate();
    }
    
    /**
     * 获取邮件正文内容
     * @return
     */
    public String getBodyText(){
        return bodytext.toString();
    }
    
    /**
     * 解析邮件，将得到的邮件内容保存到一个stringBuffer对象中，解析邮件 主要根据MimeType的不同执行不同的操作，一步一步的解析
     * @param part
     * @throws MessagingException
     * @throws IOException
     */
    public void getMailContent(Part part) throws MessagingException, IOException{
        
        String contentType = part.getContentType();
        int nameindex = contentType.indexOf("name");
        boolean conname = false;
        if(nameindex != -1){
            conname = true;
        }
        log.debug("CONTENTTYPE:"+contentType);
        if(part.isMimeType("text/plain")&&!conname){
            bodytext.append((String)part.getContent());
        }else if(part.isMimeType("text/html")&&!conname){
            bodytext.append((String)part.getContent());
        }else if(part.isMimeType("multipart/*")){
            Multipart multipart = (Multipart) part.getContent();
            int count = multipart.getCount();
            for(int i=0;i<count;i++){
                getMailContent(multipart.getBodyPart(i));
            }
        }else if(part.isMimeType("message/rfc822")){
            getMailContent((Part) part.getContent()); 
        }
        
    }
    
    /**
     * 判断邮件是否需要回执，如需回执返回true，否则返回false
     * @return
     * @throws MessagingException
     */
    public boolean getReplySign() throws MessagingException{
        boolean replySign = false;
        String needreply[] = msg.getHeader("Disposition-Notification-TO");
        if(needreply != null){
            replySign = true;
        }
        return replySign;
    }
    
    /**
     * 获取此邮件的message-id
     * @return
     * @throws MessagingException
     */
    public String getMessageId() throws MessagingException{
        return msg.getMessageID();
    }
    
    /**
     * 判断此邮件是否已读，如果未读则返回false，已读返回true
     * @return
     * @throws MessagingException
     */
    public boolean isNew() throws MessagingException{
        boolean isnew = false;
        Flags flags = ((Message)msg).getFlags();
        Flags.Flag[] flag = flags.getSystemFlags();
        log.debug("flags's length:"+flag.length);
        for(int i=0;i<flag.length;i++){
            if(flag[i]==Flags.Flag.SEEN){
                isnew = true;
                log.debug("seen message .......");
                break;
            }
        }
        
        return isnew;
    }
    
    /**
     * 判断是是否包含附件
     * @param part
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    @SuppressWarnings("unused")
	public boolean isContainAttch(Part part) throws MessagingException, IOException{
        boolean flag = false;
        
        String contentType = part.getContentType();
        if(part.isMimeType("multipart/*")){
            Multipart multipart = (Multipart) part.getContent();
            int count = multipart.getCount();
            for(int i=0;i<count;i++){
                BodyPart bodypart = multipart.getBodyPart(i);
                String dispostion = bodypart.getDisposition();
                if((dispostion != null)&&(dispostion.equals(Part.ATTACHMENT)||dispostion.equals(Part.INLINE))){
                    flag = true;
                }else if(bodypart.isMimeType("multipart/*")){
                    flag = isContainAttch(bodypart);
                }else{
                    String conType = bodypart.getContentType();
                    if(conType.toLowerCase().indexOf("appliaction")!=-1){
                        flag = true;
                    }
                    if(conType.toLowerCase().indexOf("name")!=-1){
                        flag = true;
                    }
                }
            }
        }else if(part.isMimeType("message/rfc822")){
            flag = isContainAttch((Part) part.getContent());
        }
        
        return flag;
    }

	public static String getCid(Part p) throws MessagingException {
		String cidraw = null, cid = null;
		String[] headers = p.getHeader("Content-id");
		if (headers != null && headers.length > 0) {
			cidraw = headers[0];
		} else {
			return null;
		}
		if (cidraw.startsWith("<") && cidraw.endsWith(">")) {
			cid = "cid:" + cidraw.substring(1, cidraw.length() - 1);
		} else {
			cid = "cid:" + cidraw;
		}
		return cid;

	}
	private static String mailsavedir;
    /**
     * 保存附件
     * @param part
     * @throws MessagingException
     * @throws IOException
     */
    public void saveAttchMent(Part part,Map<String,Object> attrs) throws MessagingException, IOException{
    	if(mailsavedir==null){
    		mailsavedir=PropertiesContent.get("mail.saveDir");
    	}
        String filename = "";
        if(part.isMimeType("multipart/*")){
            Multipart mp = (Multipart) part.getContent();
            for(int i=0;i<mp.getCount();i++){
                BodyPart mpart = mp.getBodyPart(i);
                String dispostion = mpart.getDisposition();
                if((dispostion != null)&&(dispostion.equals(Part.ATTACHMENT)||dispostion.equals(Part.INLINE))){
                    filename = this.base64Decoder(mpart.getFileName());
//                    if (filename != null&& filename.toLowerCase().indexOf("utf-8")!=-1){
//                        filename = MimeUtility.decodeText(filename);
//                    }
                    log.debug("==邮件中的文件名称："+filename+"\t"+mpart.getDescription());
	                attrs.put("savepath",this.getSaveAttchPath()+filename);
	                attrs.put("filepath",filename);
	                attrs.put("filename",filename);
                    log.debug("保存文件："+filename+"\t"+mpart.getDescription());
                    saveFile(mailsavedir+"/"+this.getSaveAttchPath()+filename,mpart.getInputStream());
                    
                    attrs.put("size",FileTool.me.FormetFileSize(FileTool.me.getFileSize(new File(mailsavedir+"/"+this.getSaveAttchPath()+filename))));//文件大小MB
                    /*MailAttach po=new MailAttach();
                    po.setAttrs(attrs);
                    po.save();*/
                }else if(mpart.isMimeType("multipart/*")){
                    saveAttchMent(mpart,attrs);
                }else{
                    filename = mpart.getFileName();
                    if(filename != null&&(filename.toLowerCase().indexOf("utf-8")!=-1)){
                        filename = MimeUtility.decodeText(filename);
                        saveFile(mailsavedir+"/"+this.getSaveAttchPath()+filename,mpart.getInputStream());
                    }
                }
            }
            
        }else if(part.isMimeType("message/rfc822")){
            saveAttchMent((Part) part.getContent(),attrs);
        }
    }
    //base64解码
    public String base64Decoder(String s){
    	try{
    		String cset="GB2312",temp;
    		if(s.startsWith("=?")==false){
    			return s;
    		}else{
    			String[] strs=s.split("\\?");
    			temp=strs[3];
    			cset=strs[1];
    		}
//    		String temp = s.substring(12, s.indexOf("?="));
	        sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
	        byte[] b = decoder.decodeBuffer(temp);
//	        return MimeUtility.decodeText(new String(b,"GB2312"));
	        return new String(b,cset);
    	}catch(Exception e){
    		log.error("解码异常",e);
    		return s;
    	}
    }
    /**
     * 获得保存附件的地址
     * @return
     */
    public String getSaveAttchPath() {
        return saveAttchPath;
    }
    /**
     * 设置保存附件地址
     * @param saveAttchPath
     */
    public void setSaveAttchPath(String saveAttchPath) {
        this.saveAttchPath = saveAttchPath;
    }
    /**
     * 保存文件内容
     * @param filename
     * @param inputStream
     * @throws IOException
     */
    private void saveFile(String filename, InputStream inputStream) throws IOException {
        String osname = System.getProperty("os.name");
        String storedir = getSaveAttchPath();
        String sepatror = "";
        if(osname == null){
            osname = "";
        }
        
        if(osname.toLowerCase().indexOf("win")!=-1){
            sepatror = "//";
            if(StringUtils.isEmpty(storedir)){
                storedir = "d://temp";
            }
        }else{
            sepatror = "/";
            storedir = "/temp";
        }
        
        
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        
        try {
        	 File storefile = new File(storedir+sepatror+filename.replaceAll("\\?","wenhao"));
             storefile.getParentFile().mkdirs();
             log.debug("storefile's path:"+storefile.toString());
            bos = new BufferedOutputStream(new FileOutputStream(storefile));
            bis = new BufferedInputStream(inputStream);
            int c;
            while((c= bis.read())!=-1){
                bos.write(c);
                bos.flush();
            }
        } catch (FileNotFoundException e) {
        	log.error("文件未找到",e);
        } catch (IOException e) {
        	log.error("文件读取失败", e);
        }finally{
        	try{
            bos.close();
            bis.close();
        	} catch (Exception e) {log.error("",e);}
        }
        
    }
    
    public void recive(Part part,int i) throws MessagingException, IOException{
        log.debug("------------------START-----------------------");
        log.debug("Message"+i+" subject:" + getSubject());
        log.debug("Message"+i+" from:" + getFrom());
        log.debug("Message"+i+" isNew:" + isNew());
        boolean flag = isContainAttch(part);
        log.debug("Message"+i+" isContainAttch:" +flag);
        log.debug("Message"+i+" replySign:" + getReplySign());
        getMailContent(part);
        log.debug("Message"+i+" content:" + getBodyText());
        setSaveAttchPath("D://temp//"+i);
        if(flag){
            saveAttchMent(part,null);
        }
        log.debug("------------------END-----------------------");
    }
    
    
    public static void main(String[] args) throws MessagingException, IOException {
        /*Properties props = new Properties();
//        props.setProperty("mail.pop3.host", "pop3.qq.com");
//        props.setProperty("mail.pop3.auth", "true");
        Session session = Session.getDefaultInstance(props,null);
//        URLName urlname = new URLName("pop3","pop.qq.com",110,null,"327030870@qq.com","");
        
//        Store store = session.getStore(urlname);
//        store.connect();
        Store store = session.getStore("pop3");
		  String user = "loyin@loyin.net";//PropertiesContent.config.get("mail.username");//帐户
		  String password ="123456";// PropertiesContent.config.get("mail.password");//密码
		  store.connect("pop3.loyin.net", user, password);
        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_WRITE);
        Message msgs[] = folder.getMessages();
        int count = msgs.length;
        log.debug("Message Count:"+count);
        log.debug("新邮件数量\t"+folder.getNewMessageCount());
        ReciveMail rm = null;
        for(int i=0;i<count;i++){
        	MimeMessage msg=(MimeMessage) msgs[i];
            rm = new ReciveMail(msg);
            rm.recive(msg,i);
        }*/
    	try{
       	 sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
       	 String s="=?gb18030?B?vPLA+i5kb2M=?=";
   	        byte[] b = decoder.decodeBuffer(s.substring(12,s.indexOf("?=")));
   	        System.out.println( (new String(b,"GB2312")));
       	}catch(Exception e){
       		e.printStackTrace();
       	}
        
    }
    
}