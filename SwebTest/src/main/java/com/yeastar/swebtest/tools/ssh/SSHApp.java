package com.yeastar.swebtest.tools.ssh;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.testng.annotations.Test;
import org.xbill.DNS.NULLRecord;

import javax.sound.midi.Soundbank;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.apache.log4j.spi.Configurator.NULL;

/**
 * Created by Yeastar on 2017/6/6.
 */
public class SSHApp {

    public String m_HostName = NULL;
    public String m_UserName = NULL;
    public String m_Password = NULL;
    private static final int TIME_OUT = 1000 * 5 * 60;
    Connection conn;
    //创建SSH连接
    public int CreatConnect(String HostName,String UserName, String Password){
        m_HostName = HostName ;
        m_UserName = UserName;
        m_Password = Password;
        try {
            conn = new Connection(HostName);
            conn.connect();
            /* Authenticate.
			 * If you get an IOException saying something like
			 * "Authentication method password not supported by the server at this stage."
			 * then please check the FAQ.
			 */
            boolean isAuthenticated = conn.authenticateWithPassword(UserName, Password);
            if (isAuthenticated == false)
                throw new IOException("Authentication failed.");

            return 0;
        }catch (IOException e){
            e.printStackTrace(System.err);
            System.exit(2);
            return -1;
        }
    }
    //关闭SSH连接
    public int Close(){
        conn.close();
        return 0;
    }

    //通过SSH向设备输入命令   返回设备响应的值
    //"CLI>"
    public StringBuilder WriteToSSH(String cmd,String checkdone,int duration) {
        StringBuilder ret = new StringBuilder();
        StringBuilder txt = new StringBuilder();
        /* Create a session */
        try {
            Session sess = conn.openSession();
            sess.execCommand(cmd);
//            sess.waitForCondition(ChannelCondition.EOF,0);
            //打印信息
            InputStream stdout = new StreamGobbler(sess.getStdout());
            //打印错误
            InputStream stderr = new StreamGobbler(sess.getStderr());
//            InputStreamReader in = new InputStreamReader(stdout,"UTF-8");
            int a = -1;

            BufferedReader br = new BufferedReader(new InputStreamReader(stdout,"UTF-8"));
            BufferedReader brerr = new BufferedReader(new InputStreamReader(stderr,"UTF-8"));
//            StringBuilder s = new StringBuilder();
            System.out.println("Commound Sending...." + checkdone);
            String s = null;
            int tmp =0;
            while ((a =br.read()) != -1){
//                System.out.println("in..."+(char)a);
//                s = String.valueOf((char)a);
                s += (char)a;
                if(tmp%20 == 0){
                    if(s.contains(checkdone)){
                        break;
                    }
                    System.out.println("s = "+ s);
//                    s = "";
                    tmp=0;
                }
                tmp++;
                Thread.sleep(1);
            }
            System.out.println("in ..........."+s);
//            while (true)
//            {
//                String line = br.readLine();
//                System.out.println(line);
//                if (line == null)
//                    break;
//                ret.append(line).append("\n");
//            }


//            while(true){
//                String line = brerr.readLine();
//                if(line==null){
//                    break;
//                }
//                txt.append(line).append("\n");
//                System.out.println("error111 "+line);
//             }
        /* Show exit status, if available (otherwise "null") */
            System.out.println("ExitCode: " + sess.getExitStatus());
            br.close();
            brerr.close();
            sess.close();

            return ret;
        }catch (IOException e){
            e.printStackTrace(System.err);
            System.exit(2);
            return new StringBuilder("error");
        } catch (InterruptedException e) {
            e.printStackTrace();
            return ret;
        }
    }
    private String processStream(InputStream in, String charset) throws Exception {
        byte[] buf = new byte[1024];
        StringBuilder sb = new StringBuilder();
        while (in.read(buf) != -1) {
            sb.append(new String(buf, charset));
        }
        return sb.toString();
    }
    //
    public String ReadFromSSH(){
        String ret = NULL;
        return ret ;
    }

    @Test
    public  void testT(){

        int creatconnect =CreatConnect("192.168.7.153","ls@yf","");
        System.out.println("creat connect to ssh ret ="+creatconnect);
        StringBuilder ret;


//        ret = WriteToSSH("echo $PATH");
//        System.out.println("ret1111111 = "+ret +"\n");

//        System.out.println(WriteToSSH("cd /etc/")+"\n");
//        System.out.println(WriteToSSH("ls")+"\n");
//        System.out.println(WriteToSSH("export PATH=/ysdisk/support/bin:/usr/local/bin:/usr/bin:/bin:/usr/local/sbin:/usr/sbin:/sbin"));
//        System.out.println(WriteToSSH("export PWD=bb"));
//        System.out.println(WriteToSSH("export"));
//        System.out.println(WriteToSSH("export LD_LIBRARY_PATH=/ysdisk/ysapps/pbxcenter/lib/")+"333\n");
//        System.out.println(WriteToSSH("echo $LD_LIBRARY_PATH")+"1111\n");
//        System.out.println(WriteToSSH("sh /etc/profile "));
        System.out.println(WriteToSSH("/bin/ping 192.168.7.1","CLI",2000));
        Close();
    }
}
