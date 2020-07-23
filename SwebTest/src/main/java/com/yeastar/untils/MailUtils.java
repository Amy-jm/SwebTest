package com.yeastar.untils;


import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.protocol.IMAPProtocol;
import lombok.extern.log4j.Log4j2;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.util.Properties;

/**
 * @program: SwebTest
 * @description: Mail
 * @author: huangjx@yeastar.com
 * @create: 2020/07/01
 */
@Log4j2
public class MailUtils {
    public static void main(String[] args) throws IOException, MessagingException {
        getEmailUnreadMessageCountFrom163FromIMAPS();
    }

    /**
     * 获取 收件箱的 数量
     * pop协议
     * @return
     */
    public static int getEmailUnreadMessageCountFrom163() {
//        int unreadMessageCount = 0;
//        Properties props = new Properties();
//        props.put("mail.transport.protocol", "smtp");//指定邮件发送协议  只接受邮件是可以不要写的
//        props.put("mail.store.protocol", "imap");    //指定邮件接收协议
//        props.put("mail.smtp.class", "com.sun.mail.smtp.SMTPTransport");//指定支持SMTP协议的Transport具体类，允许由第三方提供
//        props.put("mail.imap.class", "com.sun.mail.imap.IMAPStore");    //指定支持IMAP协议的Store具体类，允许由第三方提供
//        props.put("mail.smtp.host", "pop.163.com");//指定采用SMTP协议的邮件发送服务器的IP地址或主机名
//
//        try {
//            Session session = Session.getInstance(props);// 设置环境信息
//            Store store = session.getStore("pop3");//指定接收邮件协议
//            store.connect("pop.163.com", "yeastarautotest@163.com", "AYWETMHBOFQQSPMB");//密码为网页端的，开启SMTP服务，验证码
//            //获得名为默认"inbox"的邮件夹当你自己有定义其他的邮件夹也可以写上去
//            Folder folder = store.getFolder("inbox");
//            //打开邮件夹
//            folder.open(Folder.READ_ONLY);//它是一个邮件文件夹类。Folder类有两个常见的属性，READ_ONLY表示只读，READ_WRITE表示其内容可读可写
//            //获得邮件夹中的未读邮件数目
//            unreadMessageCount = folder.getUnreadMessageCount();
//          log.debug("[未读邮件]{}" , unreadMessageCount);
//        } catch (Exception ex) {
//            ex.getMessage();
//        }

        return getEmailUnreadMessageCountFrom163FromIMAPS();
    }


    /**
     * 获取最近一封邮件
     *
     * @return
     */
    public static int getLastEmailUnreadMessageContentFrom163() {
        int unreadMessageCount = 0;
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");//指定邮件发送协议  只接受邮件是可以不要写的
        props.put("mail.store.protocol", "imap");    //指定邮件接收协议
        props.put("mail.smtp.class", "com.sun.mail.smtp.SMTPTransport");//指定支持SMTP协议的Transport具体类，允许由第三方提供
        props.put("mail.imap.class", "com.sun.mail.imap.IMAPStore");    //指定支持IMAP协议的Store具体类，允许由第三方提供
        props.put("mail.smtp.host", "pop.163.com");//指定采用SMTP协议的邮件发送服务器的IP地址或主机名

        try {
            Session session = Session.getInstance(props);// 设置环境信息
            Store store = session.getStore("pop3");//指定接收邮件协议
            store.connect("pop.163.com", "yeastarautotest@163.com", "ZNASFTEUAJLZDBON");//密码为网页端的，开启SMTP服务，验证码
            //获得名为默认"inbox"的邮件夹当你自己有定义其他的邮件夹也可以写上去
            Folder folder = store.getFolder("inbox");
            //打开邮件夹
            folder.open(Folder.READ_ONLY);//它是一个邮件文件夹类。Folder类有两个常见的属性，READ_ONLY表示只读，READ_WRITE表示其内容可读可写
            //获得邮件夹中的未读邮件数目
            unreadMessageCount = folder.getUnreadMessageCount();
//            System.out.println("[未读邮件]" + unreadMessageCount);
            for (int i = 1; i <= folder.getMessageCount(); i++) {
                if (i == folder.getMessageCount()) {
                    Message msg = folder.getMessage(i);
                    log.info("INFO-->{}","===============最近一封邮件=========================");
                    //获得邮件的发送者、主题和正文
                    if (msg.getFrom()[0].toString().contains("<")) {
                        System.out.println("邮件来自:" + msg.getFrom()[0].toString().substring(msg.getFrom()[0].toString().lastIndexOf("<") + 1, msg.getFrom()[0].toString().lastIndexOf(">")));
                    } else {
                        System.out.println("邮件来自:" + msg.getFrom()[0]);
                    }
                    System.out.println("邮件主题:" + msg.getSubject());
                    System.out.println("发送日期:" + msg.getSentDate());
                    String type = msg.getContentType().toString().substring(0, msg.getContentType().toString().indexOf(";"));
                    System.out.println("邮件类型:" + type);
                    System.out.println("邮件内容:" + msg.getContentType().toString());//multipart  当文件类型为multipart/* 时不能正确显示
                    if (type.equals("text/html")) {
                        //请你根据文件的类型来更改文件的解析方式  text/html  multipart/alternative表示复合类型
                    }
                    InternetAddress[] address = (InternetAddress[]) msg.getFrom();
                    String from = address[0].getAddress();//这个是发邮件人的地址
                    if (from == null) {
                        from = "";
                    }
                    String personal = address[0].getPersonal();//这个是发邮件的人的姓名
                    if (personal == null) {
                        personal = "";
                    }
                    String fromaddr = personal + "<" + from + ">";
                    System.out.println("邮件作者：" + fromaddr);
                    System.out.println("========================================\r\n");
                }
            }
        } catch (Exception ex) {
            log.error("[connect 163 email server error]"+ex.getCause()+
            ex.getMessage()+"-->[stackTrace]");
        }

        return unreadMessageCount;
    }


    /**
     * 获取收件箱的数量--imap协议
     * @return
     */
    public static int getEmailUnreadMessageCountFrom163FromIMAPS() {
        int sunEmail = 0;
        Store store = null;
        try {
            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "imaps");
            Session session = Session.getDefaultInstance(props, null);
            store = session.getStore("imaps");
            store.connect("imap.163.COM", "yeastarautotest@163.com", "FLVGDHEJOKEWPQRX");
            Folder folder = store.getFolder("INBOX");
            if (folder instanceof IMAPFolder) {
                IMAPFolder imapFolder = (IMAPFolder)folder;
                //javamail中使用id命令有校验checkOpened, 所以要去掉id方法中的checkOpened();
                imapFolder.doCommand(new IMAPFolder.ProtocolCommand() {
                    public Object doCommand(IMAPProtocol p) throws com.sun.mail.iap.ProtocolException {p.id("FUTONG");
                        return null;                }
                });
            }
            if(folder != null) {folder.open(Folder.READ_WRITE);}
            log.debug("[未读邮件]{}" , +folder.getMessageCount());
        } catch (NoSuchProviderException ex) {
            log.error("[connect 163 email server error]"+ex.getCause()+ ex.getMessage());
        } catch (MessagingException ex) {
            log.error("[get email MessagingException]"+ex.getCause()+ ex.getMessage());
        }

        return sunEmail;
    }
}
