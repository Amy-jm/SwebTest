package com.yeastar.swebtest.tools.TcpSocket;
//package socketTest2;
import org.testng.annotations.Test;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

/**
 * Created by Yeastar on 2017/8/9.
 */


import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.yeastar.swebtest.driver.Config.DEVICE_IP_LAN;
import static com.yeastar.swebtest.driver.Config.currentPath;
import static com.yeastar.swebtest.driver.DataReader2.AMI_LOG_FILE;
import static com.yeastar.swebtest.driver.DataReader2.AMI_PROT;
import static com.yeastar.swebtest.driver.DataReader2.LOCAL_LOG_FILE;

public class TcpSocket {
//    public static final String IP_ADDR = DEVICE_IP_LAN;//服务器地址
    public static final int PORT = AMI_PROT;//服务器端口号
    public static int TIMEOUT = 70000;
    public static void main1(String[] args) {
        System.out.println("客户端启动...");
        System.out.println("当接收到服务器端字符为 \"OK\" 的时候, 客户端将终止\n");
        while (true) {
            Socket socket = null;
            try {
                //创建一个流套接字并将其连接到指定主机上的指定端口号
                socket = new Socket(DEVICE_IP_LAN, PORT);
                System.out.println(socket.isConnected());
                System.out.println(socket.getInetAddress());
                System.out.println(socket.getInputStream());
                System.out.println(socket.getLocalPort());
                System.out.println(socket.isClosed());
                //读取服务器端数据
                DataInputStream input = new DataInputStream(socket.getInputStream());
                //向服务器端发送数据
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                System.out.print("请输入: \t");
                String action = "action:login";
                String username = "username:admin";
                String password = "secret:passwird";
                String enter = "\n";
                String info="action:login\r\nusername:root\r\nsecret:password\r\n\r\n";
                String str = new BufferedReader(new InputStreamReader(new FileInputStream(info))).readLine();
                out.writeUTF(str);

//                String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
//                out.writeUTF(action);
//                System.out.println("test1111111");
//                out.writeUTF(username);
//                System.out.println("2222222222");
//                out.writeUTF(password);
//                System.out.println("333333");
//                out.writeUTF(str);
                System.out.println("55555555");
                String ret = input.readUTF();
                System.out.println("服务器端返回过来的是: " + ret);
                // 如接收到 "OK" 则断开连接
                if ("OK".equals(ret)) {
                    System.out.println("客户端将关闭连接");
                    Thread.sleep(500);
                    break;
                }

                out.close();
                input.close();
            } catch (Exception e) {
                System.out.println("客户端异常:" + e.getMessage());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                        System.out.println("socket is closed");
                    } catch (IOException e) {
                        socket = null;
                        System.out.println("客户端 finally 异常:" + e.getMessage());
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            //1.建立客户端socket连接，指定服务器位置及端口
            Socket socket =new Socket(DEVICE_IP_LAN,PORT);
            //2.得到socket读写流
//            OutputStream os=socket.getOutputStream();
            PrintWriter pw=new PrintWriter(socket.getOutputStream());
            //输入流
//            InputStream is=socket.getInputStream();
            BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //3.利用流按照一定的操作，对socket进行读写操作
            String info="action:login\r\nusername:admin\r\nsecret:password\r\n\r\n";
            pw.write(info);
            pw.flush();
//            socket.shutdownOutput();
            //接收服务器的相应
            String reply=null;
            while(true){
                reply=br.readLine();
                System.out.println("接收服务器的信息2："+reply);
                Thread.sleep(500);
                if(reply != "null"){
                    if(false)
                        break;
                }
            }
            //4.关闭资源
            br.close();
//            is.close();
            pw.close();
//            os.close();
            socket.close();
        } catch (UnknownHostException e) {
            System.out.println("error1");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("error2");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private Socket socket;
    private BufferedReader br;
    private  PrintWriter pw;
    public void connectToDevice(){
        connectToDevice(TIMEOUT);
    }
    public void connectToDevice(int mssec){
        try {
            //1.建立客户端socket连接，指定服务器位置及端口
            socket =new Socket();
            SocketAddress socketAddress = new InetSocketAddress(DEVICE_IP_LAN,PORT);
            socket.connect(socketAddress, 100);//连不上的0.1毫秒断掉连接
            socket.setSoTimeout(mssec);//响应阻塞超时
            TIMEOUT = mssec;
            //2.得到socket读写流
            pw=new PrintWriter(socket.getOutputStream());
            //得到输入流
            br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //3.利用流按照一定的操作，对socket进行读写操作
            String info="action:login\r\nusername:admin\r\nsecret:password\r\n\r\n";
            pw.write(info);
            pw.flush();
//            socket.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();//
        }
    }

    /**
     *
     * @param KeyWord
     * @return
     * System.out.println("LOCAL_LOG_PATH="+currentPath+"localLog\\"+LOCAL_LOG_FILE);
    WriteStringToFilePath(currentPath+"localLog\\"+LOCAL_LOG_FILE,emptynum+tmpname+message+"\r\n");
    org.testng.Reporter.log(emptynum+tmpname+message);

     */
    public boolean getAsteriskInfo(String KeyWord) {
        //输入流
        int  Duration = TIMEOUT/1000;

        Date date  = new Date();
        long time = date.getTime();
        boolean ret = false;
        try {
            //接收服务器的相应
            String reply=null;
            while(true){
                Date currentDate  = new Date();
                long currentTime = currentDate.getTime();
//                System.out.println("before readLine");
                reply=br.readLine();
                System.out.println("接收服务器的信息1："+reply+" time:"+(currentTime/1000-time/1000));
                WriteStringToFilePath(currentPath+"localLog\\"+AMI_LOG_FILE,reply+"\n");
                if(reply == null){
                    ret = false;
                    break;
                }
                if(reply.contains(KeyWord)){
                    System.out.println("Server Find Keyword....");
                    ret = true;
                    break;
                }
                if(currentTime/1000-time/1000 > Duration){
                    System.out.println("TcpSocket Check Timeout");
                    break;
                }

                Thread.sleep(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("TcpSocket end...");
        return ret;
    }
    public void closeTcpSocket()  {
        //4.关闭资源

        try {
            pw.close();
            br.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void WriteStringToFilePath(String filePath,String content) {
        try {
            File f = new File(filePath);
            if (f.exists()) {
            } else {
//                System.out.print("文件不存在");
                f.createNewFile();// 不存在则创建
            }
            PrintStream ps = new PrintStream(new FileOutputStream(f,true));
//            ps.println(content);// 往文件里写入字符串
            SimpleDateFormat sdf = new SimpleDateFormat("HH时mm分ss秒SSS ");
            String currentTime = String.valueOf(sdf.format(new Date()));
            ps.append(currentTime+content);// 在已有的基础上添加字符串
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test() throws IOException, InterruptedException {
        connectToDevice(10000);
        getAsteriskInfo("fff");
        System.out.println("read out ");
//        getAsteriskInfo();

        closeTcpSocket();
    }
}