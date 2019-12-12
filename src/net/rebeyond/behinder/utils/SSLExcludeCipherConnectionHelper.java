 package net.rebeyond.behinder.utils;
 
 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.net.InetAddress;
 import java.net.Socket;
 import java.net.URL;
 import java.net.UnknownHostException;
 import java.util.ArrayList;
 import java.util.List;
 import javax.net.ssl.HttpsURLConnection;
 import javax.net.ssl.SSLContext;
 import javax.net.ssl.SSLParameters;
 import javax.net.ssl.SSLSocket;
 import javax.net.ssl.SSLSocketFactory;
 import javax.net.ssl.TrustManagerFactory;
 
 
 
 
 
 
 
 
 public class SSLExcludeCipherConnectionHelper
 {
   private String[] exludedCipherSuites = { "_DHE_", "_DH_" };
   
   private String trustCert = null;
   private TrustManagerFactory tmf;
   
   public void setExludedCipherSuites(String[] exludedCipherSuites)
   {
     this.exludedCipherSuites = exludedCipherSuites;
   }
   
   public SSLExcludeCipherConnectionHelper(String trustCert)
   {
     this.trustCert = trustCert;
     try
     {
       initTrustManager();
     } catch (Exception ex) {
       ex.printStackTrace();
     }
   }
   
   /* Error */
   private void initTrustManager()
     throws Exception
   {
     // Byte code:
     //   0: ldc 45
     //   2: invokestatic 47	java/security/cert/CertificateFactory:getInstance	(Ljava/lang/String;)Ljava/security/cert/CertificateFactory;
     //   5: astore_1
     //   6: new 53	java/io/BufferedInputStream
     //   9: dup
     //   10: new 55	java/io/FileInputStream
     //   13: dup
     //   14: aload_0
     //   15: getfield 31	net/rebeyond/behinder/utils/SSLExcludeCipherConnectionHelper:trustCert	Ljava/lang/String;
     //   18: invokespecial 57	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
     //   21: invokespecial 59	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
     //   24: astore_2
     //   25: aconst_null
     //   26: astore_3
     //   27: aload_1
     //   28: aload_2
     //   29: invokevirtual 62	java/security/cert/CertificateFactory:generateCertificate	(Ljava/io/InputStream;)Ljava/security/cert/Certificate;
     //   32: astore_3
     //   33: goto +12 -> 45
     //   36: astore 4
     //   38: aload_2
     //   39: invokevirtual 66	java/io/InputStream:close	()V
     //   42: aload 4
     //   44: athrow
     //   45: aload_2
     //   46: invokevirtual 66	java/io/InputStream:close	()V
     //   49: ldc 71
     //   51: invokestatic 73	java/security/KeyStore:getInstance	(Ljava/lang/String;)Ljava/security/KeyStore;
     //   54: astore 4
     //   56: aload 4
     //   58: aconst_null
     //   59: aconst_null
     //   60: invokevirtual 78	java/security/KeyStore:load	(Ljava/io/InputStream;[C)V
     //   63: aload 4
     //   65: ldc 82
     //   67: aload_3
     //   68: invokevirtual 84	java/security/KeyStore:setCertificateEntry	(Ljava/lang/String;Ljava/security/cert/Certificate;)V
     //   71: invokestatic 88	javax/net/ssl/TrustManagerFactory:getDefaultAlgorithm	()Ljava/lang/String;
     //   74: astore 5
     //   76: aload_0
     //   77: aload 5
     //   79: invokestatic 94	javax/net/ssl/TrustManagerFactory:getInstance	(Ljava/lang/String;)Ljavax/net/ssl/TrustManagerFactory;
     //   82: putfield 97	net/rebeyond/behinder/utils/SSLExcludeCipherConnectionHelper:tmf	Ljavax/net/ssl/TrustManagerFactory;
     //   85: aload_0
     //   86: getfield 97	net/rebeyond/behinder/utils/SSLExcludeCipherConnectionHelper:tmf	Ljavax/net/ssl/TrustManagerFactory;
     //   89: aload 4
     //   91: invokevirtual 99	javax/net/ssl/TrustManagerFactory:init	(Ljava/security/KeyStore;)V
     //   94: return
     // Line number table:
     //   Java source line #50	-> byte code offset #0
     //   Java source line #51	-> byte code offset #6
     //   Java source line #52	-> byte code offset #25
     //   Java source line #54	-> byte code offset #27
     //   Java source line #55	-> byte code offset #33
     //   Java source line #56	-> byte code offset #38
     //   Java source line #57	-> byte code offset #42
     //   Java source line #56	-> byte code offset #45
     //   Java source line #60	-> byte code offset #49
     //   Java source line #61	-> byte code offset #56
     //   Java source line #62	-> byte code offset #63
     //   Java source line #65	-> byte code offset #71
     //   Java source line #66	-> byte code offset #76
     //   Java source line #67	-> byte code offset #85
     //   Java source line #68	-> byte code offset #94
     // Local variable table:
     //   start	length	slot	name	signature
     //   0	95	0	this	SSLExcludeCipherConnectionHelper
     //   5	23	1	cf	java.security.cert.CertificateFactory
     //   24	22	2	caInput	java.io.InputStream
     //   26	42	3	ca	java.security.cert.Certificate
     //   36	7	4	localObject	Object
     //   54	36	4	keyStore	java.security.KeyStore
     //   74	4	5	tmfAlgorithm	String
     // Exception table:
     //   from	to	target	type
     //   27	36	36	finally
   }
   
   public String get(URL url)
     throws Exception
   {
     SSLContext context = SSLContext.getInstance("TLS");
     context.init(null, this.tmf.getTrustManagers(), null);
     SSLParameters params = context.getSupportedSSLParameters();
     List<String> enabledCiphers = new ArrayList();
     String[] arrayOfString1; int j = (arrayOfString1 = params.getCipherSuites()).length; for (int i = 0; i < j; i++) { String cipher = arrayOfString1[i];
       boolean exclude = false;
       if (this.exludedCipherSuites != null) {
         for (int i = 0; (i < this.exludedCipherSuites.length) && (!exclude); i++) {
           exclude = cipher.indexOf(this.exludedCipherSuites[i]) >= 0;
         }
       }
       if (!exclude) {
         enabledCiphers.add(cipher);
       }
     }
     String[] cArray = new String[enabledCiphers.size()];
     enabledCiphers.toArray(cArray);
     
 
     HttpsURLConnection urlConnection = 
       (HttpsURLConnection)url.openConnection();
     Object sf = context.getSocketFactory();
     sf = new DOSSLSocketFactory((SSLSocketFactory)sf, cArray, null);
     urlConnection.setSSLSocketFactory((SSLSocketFactory)sf);
     BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
     
     StringBuffer buffer = new StringBuffer();
     String inputLine; while ((inputLine = in.readLine()) != null) { String inputLine;
       buffer.append(inputLine); }
     in.close();
     
     return buffer.toString();
   }
   
   private class DOSSLSocketFactory extends SSLSocketFactory
   {
     private SSLSocketFactory sf = null;
     private String[] enabledCiphers = null;
     
     private DOSSLSocketFactory(SSLSocketFactory sf, String[] enabledCiphers)
     {
       this.sf = sf;
       this.enabledCiphers = enabledCiphers;
     }
     
     private Socket getSocketWithEnabledCiphers(Socket socket) {
       if ((this.enabledCiphers != null) && (socket != null) && ((socket instanceof SSLSocket))) {
         ((SSLSocket)socket).setEnabledCipherSuites(this.enabledCiphers);
       }
       return socket;
     }
     
     public Socket createSocket(Socket s, String host, int port, boolean autoClose)
       throws IOException
     {
       return getSocketWithEnabledCiphers(this.sf.createSocket(s, host, port, autoClose));
     }
     
     public String[] getDefaultCipherSuites()
     {
       return this.sf.getDefaultCipherSuites();
     }
     
     public String[] getSupportedCipherSuites()
     {
       if (this.enabledCiphers == null) {
         return this.sf.getSupportedCipherSuites();
       }
       return this.enabledCiphers;
     }
     
     public Socket createSocket(String host, int port)
       throws IOException, UnknownHostException
     {
       return getSocketWithEnabledCiphers(this.sf.createSocket(host, port));
     }
     
     public Socket createSocket(InetAddress address, int port)
       throws IOException
     {
       return getSocketWithEnabledCiphers(this.sf.createSocket(address, port));
     }
     
     public Socket createSocket(String host, int port, InetAddress localAddress, int localPort)
       throws IOException, UnknownHostException
     {
       return getSocketWithEnabledCiphers(this.sf.createSocket(host, port, localAddress, localPort));
     }
     
     public Socket createSocket(InetAddress address, int port, InetAddress localaddress, int localport)
       throws IOException
     {
       return getSocketWithEnabledCiphers(this.sf.createSocket(address, port, localaddress, localport));
     }
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/net/rebeyond/behinder/utils/SSLExcludeCipherConnectionHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */