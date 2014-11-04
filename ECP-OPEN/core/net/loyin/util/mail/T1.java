package net.loyin.util.mail;

import java.net.URLEncoder;
import java.util.Date;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;
import org.junit.Test;

public class T1 {
	public static void main(String[] args) throws Exception {
		// 这个类主要是设置邮件
		/*
		 * MailSenderInfo mailInfo = new MailSenderInfo();
		 * mailInfo.setMailServerHost("smtp.qq.com");
		 * mailInfo.setMailServerPort("25"); // mailInfo.setValidate(true);
		 * mailInfo.setUserName("327030870@qq.com");
		 * mailInfo.setPassword("");//您的邮箱密码
		 * mailInfo.setFromAddress("327030870@qq.com");
		 * mailInfo.setToAddress(new String[]{"327030870@qq.com"});
		 * mailInfo.setCcAddress(new String[]{"350293229@qq.com"});
		 * mailInfo.setBccAddress(new String[]{"290238838@qq.com"});
		 * mailInfo.setSubject("设置邮箱标题1"); mailInfo.setContent("设置邮箱内容2");
		 * mailInfo.setAttachFileNames(new
		 * String[]{"E:\\3395432644061476435.jpg","E:\\xingxing.jpg"});
		 * mailInfo.sendMail();
		 */
		Properties props = new Properties();
		Session recesession = Session.getDefaultInstance(props, null);
		recesession.setDebug(false);

		Store store = recesession.getStore("pop3");
		String user = "loyin@1818mq.com";// 帐户
		String password = "loyin123456";// 密码
		store.connect("1818mq.com", user, password);

		Folder folder = store.getFolder("INBOX");

		folder.open(Folder.READ_ONLY);

		Message[] msgs = folder.getMessages();
		for (int msgNum = 0; msgNum < msgs.length; msgNum++) {
			System.out.println(msgNum + "\t" + msgs[msgNum].getFrom()[0] + "\t"
					+ msgs[msgNum].getSubject() + "\t"
					+ msgs[msgNum].getSentDate());
			// System.out.println(msgs[msgNum].getContent());
		}
		folder.close(false);
		store.close();
	}

	@Test
	public void sendEmail() {
		try {
			// Create the attachment

			EmailAttachment attachment = new EmailAttachment();
			attachment.setPath("E:/ff.jpg");
			attachment.setDisposition(EmailAttachment.ATTACHMENT);
			attachment.setDescription("施瓦辛格.jpg");
			attachment.setName(URLEncoder.encode("阿诺●施瓦辛格.jpg"));
			EmailAttachment attachment1 = new EmailAttachment();
			attachment1.setPath("E:/ff.jpg");
			attachment1.setDisposition(EmailAttachment.ATTACHMENT);
			attachment1.setDescription("施瓦辛格.jpg");
			attachment1.setName(URLEncoder.encode("阿诺●施瓦辛格.jpg"));
//			Email email = new SimpleEmail();
			MultiPartEmail  email=new MultiPartEmail ();
			email.setHostName("smtp.loyin.net");
			email.setSmtpPort(25);
			email.setAuthenticator(new DefaultAuthenticator("loyin@loyin.net","123456"));
			email.setSSL(false);
			email.setFrom("loyin@loyin.net");
			email.setSubject("TestMail"+new Date());
			email.setMsg("This is a test mail ... :-)");
			email.addTo("loyin@loyin.net");
			email.addBcc("user1@loyin.net");
//			email.addBcc("loyin@loyin.net");
//			email.addCc("loyin@loyin.net");// 密抄
			email.attach(attachment);//添加本地附件
			email.attach(attachment1);//添加本地附件1
			email.send();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
