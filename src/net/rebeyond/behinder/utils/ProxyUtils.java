 package net.rebeyond.behinder.utils;
 
 import java.io.DataInputStream;
 import java.io.DataOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.net.InetAddress;
 import java.net.ServerSocket;
 import java.net.Socket;
 import java.net.SocketTimeoutException;
 import java.util.Arrays;
 import net.rebeyond.behinder.core.ShellService;
 import org.eclipse.swt.custom.StyleRange;
 import org.eclipse.swt.custom.StyledText;
 import org.eclipse.swt.widgets.Display;
 import org.eclipse.swt.widgets.Label;
 
 
 
 
 
 
 public class ProxyUtils
   extends Thread
 {
   private ShellService currentShellService;
   private String bindAddress;
   private String bindPort;
   private StyledText logContent;
   private Label statusLabel;
   private Thread r;
   private Thread w;
   private Thread proxy;
   private ServerSocket serverSocket;
   public static int bufSize = 65535;
   
   public ProxyUtils(ShellService shellService, String bindAddress, String bindPort, StyledText proxyLogTxt, Label statusLabel) throws Exception
   {
     this.currentShellService = shellService;
     this.bindAddress = bindAddress;
     this.bindPort = bindPort;
     this.logContent = proxyLogTxt;
     this.statusLabel = statusLabel;
   }
   
   private void log(String type, String log) {
     final String logLine = "[" + type + "]" + log + "\n";
     final Display display = Display.getDefault();
     final int color = type.equals("ERROR") ? 3 : 9;
     display.syncExec(new Runnable() {
       public void run() {
         if (ProxyUtils.this.statusLabel.isDisposed())
           return;
         ProxyUtils.this.logContent.append(logLine);
         StyleRange styleRange = new StyleRange();
         styleRange.start = (ProxyUtils.this.logContent.getText().length() - logLine.length());
         styleRange.length = logLine.length();
         styleRange.foreground = display.getSystemColor(color);
         ProxyUtils.this.logContent.setStyleRange(styleRange);
         ProxyUtils.this.logContent.showSelection();
       }
     });
   }
   
   public void shutdown()
   {
     log("INFO", "正在关闭代理服务");
     try
     {
       if (this.r != null)
         this.r.stop();
       if (this.w != null)
         this.w.stop();
       if (this.proxy != null)
         this.proxy.stop();
       this.serverSocket.close();
     }
     catch (IOException e) {
       log("ERROR", "代理服务关闭异常:" + e.getMessage());
     }
     log("INFO", "代理服务已停止");
   }
   
   public void run() {
     try {
       this.proxy = Thread.currentThread();
       this.serverSocket = new ServerSocket(Integer.parseInt(this.bindPort), 50, InetAddress.getByName(this.bindAddress));
       this.serverSocket.setReuseAddress(true);
       log("INFO", "正在监听端口" + this.bindPort);
       for (;;) {
         Socket socket = this.serverSocket.accept();
         log("INFO", "收到客户端连接请求.");
         new Session(socket).start();
       }
     }
     catch (IOException e) {
       log("ERROR", "端口监听失败：" + e.getMessage());
     }
   }
   
   private class Session extends Thread {
     private Socket socket;
     
     public Session(Socket socket) {
       this.socket = socket;
     }
     
     public void run() {
       try {
         if (handleSocks(this.socket))
         {
           ProxyUtils.this.log("INFO", "正在通信...");
           
 
           ProxyUtils.this.r = new Reader(null);
           ProxyUtils.this.w = new Writer(null);
           ProxyUtils.this.r.start();
           ProxyUtils.this.w.start();
           ProxyUtils.this.r.join();
           ProxyUtils.this.w.join();
         }
       } catch (Exception e) {
         try {
           ProxyUtils.this.currentShellService.closeProxy();
         }
         catch (Exception e1) {
           e1.printStackTrace();
         }
       }
     }
     
     private boolean handleSocks(Socket socket) throws Exception {
       int ver = socket.getInputStream().read();
       if (ver == 5)
         return parseSocks5(socket);
       if (ver == 4) {
         return parseSocks4(socket);
       }
       return false;
     }
     
     private boolean parseSocks5(Socket socket) throws Exception
     {
       DataInputStream ins = new DataInputStream(socket.getInputStream());
       DataOutputStream os = new DataOutputStream(socket.getOutputStream());
       int nmethods = ins.read();
       int methods = ins.read();
       os.write(new byte[] { 5 });
       int version = ins.read();
       int atyp; int cmd; int atyp; if (version == 2) {
         version = ins.read();
         int cmd = ins.read();
         int rsv = ins.read();
         atyp = ins.read();
       } else {
         cmd = ins.read();
         int rsv = ins.read();
         atyp = ins.read();
       }
       
       byte[] targetPort = new byte[2];
       String host = "";
       
       if (atyp == 1) {
         byte[] target = new byte[4];
         ins.readFully(target);
         ins.readFully(targetPort);
         String[] tempArray = new String[4];
         for (int i = 0; i < target.length; i++) {
           temp = target[i] & 0xFF;
           tempArray[i] = temp;
         }
         String[] arrayOfString1;
         int i = (arrayOfString1 = tempArray).length; for (int temp = 0; temp < i; temp++) { String temp = arrayOfString1[temp];
           
           host = host + temp + ".";
         }
         host = host.substring(0, host.length() - 1);
       } else if (atyp == 3) {
         int targetLen = ins.read();
         byte[] target = new byte[targetLen];
         ins.readFully(target);
         ins.readFully(targetPort);
         host = new String(target);
       } else if (atyp == 4) {
         byte[] target = new byte[16];
         ins.readFully(target);
         ins.readFully(targetPort);
         host = new String(target);
       }
       int port = (targetPort[0] & 0xFF) * 256 + (targetPort[1] & 0xFF);
       if ((cmd == 2) || (cmd == 3))
         throw new Exception("not implemented");
       if (cmd == 1) {
         host = InetAddress.getByName(host).getHostAddress();
         if (ProxyUtils.this.currentShellService.openProxy(host, port)) {
           os.write(CipherUtils.mergeByteArray(new byte[][] { { 5, 0, 0, 1 }, 
             InetAddress.getByName(host).getAddress(), targetPort }));
           ProxyUtils.this.log("INFO", "隧道建立成功，请求远程地址" + host + ":" + port);
           return true;
         }
         os.write(CipherUtils.mergeByteArray(new byte[][] { { 5, 0, 0, 1 }, 
           InetAddress.getByName(host).getAddress(), targetPort }));
         throw new Exception(String.format("[%s:%d] Remote failed", new Object[] { host, Integer.valueOf(port) }));
       }
       
       throw new Exception("Socks5 - Unknown CMD");
     }
     
 
 
     private boolean parseSocks4(Socket socket) { return false; }
     
     private class Reader extends Thread {
       private Reader() {}
       
       public void run() {
         while (ProxyUtils.Session.this.socket != null) {
           try
           {
             byte[] data = ProxyUtils.this.currentShellService.readProxyData();
             
             if (data == null)
               break;
             if (data.length == 0) {
               Thread.sleep(100L);
             }
             else {
               ProxyUtils.Session.this.socket.getOutputStream().write(data);
               ProxyUtils.Session.this.socket.getOutputStream().flush();
             }
           } catch (Exception e) {
             ProxyUtils.this.log("ERROR", "数据读取异常:" + e.getMessage());
             e.printStackTrace();
           }
         }
       }
     }
     
     private class Writer extends Thread {
       private Writer() {}
       
       public void run() {
         for (;;) {
           if (ProxyUtils.Session.this.socket != null) {
             try
             {
               ProxyUtils.Session.this.socket.setSoTimeout(1000);
               byte[] data = new byte[ProxyUtils.bufSize];
               int length = ProxyUtils.Session.this.socket.getInputStream().read(data);
               if (length != -1)
               {
                 data = Arrays.copyOfRange(data, 0, length);
                 ProxyUtils.this.currentShellService.writeProxyData(data);
               }
             }
             catch (SocketTimeoutException e) {}catch (Exception e) {
               ProxyUtils.this.log("ERROR", "数据写入异常:" + e.getMessage());
               e.printStackTrace();
             }
           }
         }
         try {
           ProxyUtils.this.currentShellService.closeProxy();
           ProxyUtils.this.log("INFO", "隧道关闭成功。");
           ProxyUtils.Session.this.socket.close();
         }
         catch (Exception e) {
           ProxyUtils.this.log("ERROR", "隧道关闭失败:" + e.getMessage());
           e.printStackTrace();
         }
       }
     }
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/net/rebeyond/behinder/utils/ProxyUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */