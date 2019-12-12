//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package net.rebeyond.behinder.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

public class ProxyUtils extends Thread {
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

    public ProxyUtils(ShellService shellService, String bindAddress, String bindPort, StyledText proxyLogTxt, Label statusLabel) throws Exception {
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
                if (!ProxyUtils.this.statusLabel.isDisposed()) {
                    ProxyUtils.this.logContent.append(logLine);
                    StyleRange styleRange = new StyleRange();
                    styleRange.start = ProxyUtils.this.logContent.getText().length() - logLine.length();
                    styleRange.length = logLine.length();
                    styleRange.foreground = display.getSystemColor(color);
                    ProxyUtils.this.logContent.setStyleRange(styleRange);
                    ProxyUtils.this.logContent.showSelection();
                }
            }
        });
    }

    public void shutdown() {
        this.log("INFO", "正在关闭代理服务");

        try {
            if (this.r != null) {
                this.r.stop();
            }

            if (this.w != null) {
                this.w.stop();
            }

            if (this.proxy != null) {
                this.proxy.stop();
            }

            this.serverSocket.close();
        } catch (IOException var2) {
            this.log("ERROR", "代理服务关闭异常:" + var2.getMessage());
        }

        this.log("INFO", "代理服务已停止");
    }

    public void run() {
        try {
            this.proxy = Thread.currentThread();
            this.serverSocket = new ServerSocket(Integer.parseInt(this.bindPort), 50, InetAddress.getByName(this.bindAddress));
            this.serverSocket.setReuseAddress(true);
            this.log("INFO", "正在监听端口" + this.bindPort);

            while(true) {
                Socket socket = this.serverSocket.accept();
                this.log("INFO", "收到客户端连接请求.");
                (new ProxyUtils.Session(socket)).start();
            }
        } catch (IOException var2) {
            this.log("ERROR", "端口监听失败：" + var2.getMessage());
        }
    }

    private class Session extends Thread {
        private Socket socket;

        public Session(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                if (this.handleSocks(this.socket)) {
                    ProxyUtils.this.log("INFO", "正在通信...");
                    ProxyUtils.this.r = new ProxyUtils.Session.Reader(null);
                    ProxyUtils.this.w = new ProxyUtils.Session.Writer(null);
                    ProxyUtils.this.r.start();
                    ProxyUtils.this.w.start();
                    ProxyUtils.this.r.join();
                    ProxyUtils.this.w.join();
                }
            } catch (Exception var4) {
                try {
                    ProxyUtils.this.currentShellService.closeProxy();
                } catch (Exception var3) {
                    var3.printStackTrace();
                }
            }

        }

        private boolean handleSocks(Socket socket) throws Exception {
            int ver = socket.getInputStream().read();
            if (ver == 5) {
                return this.parseSocks5(socket);
            } else {
                return ver == 4 ? this.parseSocks4(socket) : false;
            }
        }

        private boolean parseSocks5(Socket socket) throws Exception {
            DataInputStream ins = new DataInputStream(socket.getInputStream());
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());
            int nmethods = ins.read();
            int methods = ins.read();
            os.write(new byte[]{5, 0});
            int version = ins.read();
            int cmd;
            int rsv;
            int atyp;
            if (version == 2) {
                version = ins.read();
                cmd = ins.read();
                rsv = ins.read();
                atyp = ins.read();
            } else {
                cmd = ins.read();
                rsv = ins.read();
                atyp = ins.read();
            }

            byte[] targetPort = new byte[2];
            String host = "";
            byte[] target;
            if (atyp == 1) {
                target = new byte[4];
                ins.readFully(target);
                ins.readFully(targetPort);
                String[] tempArray = new String[4];

                int temp;
                for(int i = 0; i < target.length; ++i) {
                    temp = target[i] & 255;
                    tempArray[i] = String.valueOf(temp);
                }

                String[] var18 = tempArray;
                int var17 = tempArray.length;

                for(temp = 0; temp < var17; ++temp) {
                    String tempx = var18[temp];
                    host = host + tempx + ".";
                }

                host = host.substring(0, host.length() - 1);
            } else if (atyp == 3) {
                int targetLen = ins.read();
                target = new byte[targetLen];
                ins.readFully(target);
                ins.readFully(targetPort);
                host = new String(target);
            } else if (atyp == 4) {
                target = new byte[16];
                ins.readFully(target);
                ins.readFully(targetPort);
                host = new String(target);
            }

            int port = (targetPort[0] & 255) * 256 + (targetPort[1] & 255);
            if (cmd != 2 && cmd != 3) {
                if (cmd == 1) {
                    host = InetAddress.getByName(host).getHostAddress();
                    if (ProxyUtils.this.currentShellService.openProxy(host, String.valueOf(port))) {
                        os.write(CipherUtils.mergeByteArray(new byte[][]{{5, 0, 0, 1}, InetAddress.getByName(host).getAddress(), targetPort}));
                        ProxyUtils.this.log("INFO", "隧道建立成功，请求远程地址" + host + ":" + port);
                        return true;
                    } else {
                        os.write(CipherUtils.mergeByteArray(new byte[][]{{5, 0, 0, 1}, InetAddress.getByName(host).getAddress(), targetPort}));
                        throw new Exception(String.format("[%s:%d] Remote failed", host, port));
                    }
                } else {
                    throw new Exception("Socks5 - Unknown CMD");
                }
            } else {
                throw new Exception("not implemented");
            }
        }

        private boolean parseSocks4(Socket socket) {
            return false;
        }

        private class Reader extends Thread {
            private Reader(Object o) {
            }

            public void run() {
                while(true) {
                    if (Session.this.socket != null) {
                        try {
                            byte[] data = ProxyUtils.this.currentShellService.readProxyData();
                            if (data != null) {
                                if (data.length == 0) {
                                    Thread.sleep(100L);
                                    continue;
                                }

                                Session.this.socket.getOutputStream().write(data);
                                Session.this.socket.getOutputStream().flush();
                                continue;
                            }
                        } catch (Exception var2) {
                            ProxyUtils.this.log("ERROR", "数据读取异常:" + var2.getMessage());
                            var2.printStackTrace();
                            continue;
                        }
                    }

                    return;
                }
            }
        }

        private class Writer extends Thread {
            private Writer(Object o) {
            }

            public void run() {
                while(true) {
                    if (Session.this.socket != null) {
                        try {
                            Session.this.socket.setSoTimeout(1000);
                            byte[] data = new byte[ProxyUtils.bufSize];
                            int length = Session.this.socket.getInputStream().read(data);
                            if (length != -1) {
                                data = Arrays.copyOfRange(data, 0, length);
                                ProxyUtils.this.currentShellService.writeProxyData(data);
                                continue;
                            }
                        } catch (SocketTimeoutException var4) {
                            continue;
                        } catch (Exception var5) {
                            ProxyUtils.this.log("ERROR", "数据写入异常:" + var5.getMessage());
                            var5.printStackTrace();
                        }
                    }

                    try {
                        ProxyUtils.this.currentShellService.closeProxy();
                        ProxyUtils.this.log("INFO", "隧道关闭成功。");
                        Session.this.socket.close();
                    } catch (Exception var3) {
                        ProxyUtils.this.log("ERROR", "隧道关闭失败:" + var3.getMessage());
                        var3.printStackTrace();
                    }

                    return;
                }
            }
        }
    }
}
