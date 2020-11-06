package com.yeastar.untils;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.sun.jna.platform.win32.WinUser;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.yeastar.swebtest.driver.DataReader2.*;
@Log4j2
public class SSHLinuxUntils {
    static StringBuffer outputstream = null;
    public static void main(String[] args) throws IOException, JSchException, InterruptedException {
        // TODO Auto-generated method stub
        String host = "192.168.3.125";
        int port = 8022;
        String user = "ls@yf";
        String password = "";
        String command_del = "export LD_LIBRARY_PATH=/ysdisk/ysapps/pbxcenter/lib;asterisk -rx \"database deltree registrar\"";
        String command_reboot = "export PATH=$PATH:$HOME/bin:/sbin:/usr/bin:/usr/sbin;reboot";
        String command_cat_version = "export PATH=$PATH:$HOME/bin:/sbin:/usr/bin:/usr/sbin;cat /etc/version";

        System.out.println(exeCommand(host,port,user,password,command_cat_version));
//        System.out.println(exeCommand(host,port,user,password,command_del));
//        Thread.sleep(3000);
//        System.out.println(exeCommand(host,port,user,password,command_reboot));

    }

    /**
     *
     * @param host
     * @param port
     * @param user
     * @param password
     * @param command
     * @return
     * @throws JSchException
     * @throws IOException
     */
    public static String exeCommand(String host, int port, String user, String password, String command) throws JSchException, IOException {
        System.out.println("host:"+host+ "  port:"+port+ "  user:"+user+"   password:"+password+"   cmd:"+command);
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, port);
        session.setConfig("StrictHostKeyChecking", "no");
        //    java.util.Properties config = new java.util.Properties();
        //   config.put("StrictHostKeyChecking", "no");
        session.setPassword(password);
        session.connect();

        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        InputStream in = channelExec.getInputStream();
        channelExec.setCommand(command);
        channelExec.setErrStream(System.err);
        channelExec.connect();
        String out = IOUtils.toString(in, "UTF-8");

        channelExec.disconnect();
        session.disconnect();

        return out;
    }

    public static String exeCommand(String host, String command)  {
        JSch jsch = new JSch();
        Session session = null;
        try {
            session = jsch.getSession(PJSIP_SSH_USER, host, PJSIP_TCP_PORT);
            session.setConfig("StrictHostKeyChecking", "no");
            //    java.util.Properties config = new java.util.Properties();
            //   config.put("StrictHostKeyChecking", "no");
            session.setPassword(PJSIP_SSH_PASSWORD);
            session.connect();

            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            InputStream in = channelExec.getInputStream();
            channelExec.setCommand(command);
            channelExec.setErrStream(System.err);
            channelExec.connect();
            String out = IOUtils.toString(in, "UTF-8");

            channelExec.disconnect();
            session.disconnect();
            log.debug("[exeCommand return] "+out);
            return out;
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "[error]";
    }

    /**
     *
     * @param host
     * @param port
     * @param user
     * @param password
     * @param command
     * @return
     * @throws JSchException
     * @throws IOException
     */
    public static String exePjsip(String host, int port, String user, String password, String command) throws JSchException, IOException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, port);
        session.setConfig("StrictHostKeyChecking", "no");
        //    java.util.Properties config = new java.util.Properties();
        //   config.put("StrictHostKeyChecking", "no");
        session.setPassword(password);
        session.connect();

        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        InputStream in = channelExec.getInputStream();
        channelExec.setCommand(command);
        channelExec.setErrStream(System.err);
        channelExec.connect();
        String out = IOUtils.toString(in, "UTF-8");

        channelExec.disconnect();
        session.disconnect();

        return out;
    }

    /**
     * @param command
     * @return
     * @throws JSchException
     * @throws IOException
     */
    public static String exePjsip(String command) throws JSchException, IOException {
        String result = exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD,command);
        log.debug("[exePjsip result] "+ result);
        return result;
    }

    /**
     *
     * @param host
     * @param port
     * @param user
     * @param password
     * @param command
     * @return
     * @throws JSchException
     * @throws IOException
     */
    public static String exePjsipNew(String host, int port, String user, String password, String command,String containsString,int appearCount,int timout,List<AsteriskObject> asteriskObjectList) throws JSchException, IOException {
        JSch jsch = new JSch();
        outputstream = new StringBuffer();
        Session session = jsch.getSession(user, host, port);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(password);
        session.connect();

        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        BufferedReader br = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));
        channelExec.setCommand(command);
        channelExec.setErrStream(System.err);
        channelExec.connect();
        String msg = null;
        int tmp=0;
//        long startTime = System.currentTimeMillis();
        while ((msg = br.readLine()) != null && tmp <= timout) {
            outputstream.append(msg).append("\n");
            log.debug("[CLI]"+ msg);
            if(msg.contains(containsString)){
                AsteriskObject  asteriskObject1 = new AsteriskObject();
//                log.debug("[get key success ，appear "+appearCount+"] "+(System.currentTimeMillis() - startTime)/1000+" Seconds ！"+msg);
                asteriskObject1.setName(msg);
//                asteriskObject1.setTime(msg.substring(1,20));
                if(msg.contains("'")){
                    String[] str = msg.split("'");
                    asteriskObject1.setKeyword(str[1]);
                    asteriskObjectList.add(asteriskObject1);
                }
            }
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            tmp++;
//            if(tmp==timout){
//                AsteriskObject  asteriskObject2 = new AsteriskObject();
//                log.debug("[TIMEOUT]");
//                asteriskObject2.setTag("TIMEOUT");
//                asteriskObject2.setName("TIMEOUT_NAME");
//                asteriskObject2.setTime("TIMEOUT_TIME");
//                asteriskObjectList.add(asteriskObject2);
//            }
        }
//        log.debug("[get cli time] "+(System.currentTimeMillis() - startTime)/1000+" Seconds");
        channelExec.disconnect();
        session.disconnect();

        return outputstream.toString();
    }



    /**
     * ssh操作执行静态命令查看
     * @return
     * @throws IOException
     * @throws JSchException
     */
    public static void getPbxlog(String containsStr, int appearCount, int seconds, List<AsteriskObject> asteriskObject)  {
        String ASTERISK_CLI = "export LD_LIBRARY_PATH=/ysdisk/ysapps/pbxcenter/lib;tail -c +100 /ysdisk/syslog/pbxlog.0";
        String ASTERISK = "export LD_LIBRARY_PATH=/ysdisk/ysapps/pbxcenter/lib;asterisk -rx";
        log.debug("[============= CLI start loading =====================]\n"+ASTERISK_CLI);
        try {
           SSHLinuxUntils.exePjsipNew(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, ASTERISK_CLI,containsStr,appearCount,seconds, asteriskObject);
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.debug("[asterisk result] "+outputstream);
    }

    /**
     * 子线程，监控 asterisk的日志输出
     */
    public static class AsteriskThread extends Thread {
        List<AsteriskObject> asteriskObject;
        String asteriskKey;

        public AsteriskThread(List<AsteriskObject> asteriskObject, String asteriskKey) {
            this.asteriskObject=asteriskObject;
            this.asteriskKey=asteriskKey;
        }

        public void run() {
            log.debug("[PbxLog Thread]" + "正在执行...");
//         SSHLinuxUntils.getPbxlog("ivr-greeting-dial-ext.slin",1,60,asteriskObject);
            SSHLinuxUntils.getPbxlog(asteriskKey,1,12000,asteriskObject);//2分钟 1200 * 100
        }
    }
}
