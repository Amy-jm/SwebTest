package com.yeastar.untils;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class SSHLinuxUntils {
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
}
