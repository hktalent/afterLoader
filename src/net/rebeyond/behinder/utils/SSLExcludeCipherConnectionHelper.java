package net.rebeyond.behinder.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class SSLExcludeCipherConnectionHelper {
    private String[] exludedCipherSuites = new String[]{"_DHE_", "_DH_"};
    private String trustCert = null;
    private TrustManagerFactory tmf;

    public void setExludedCipherSuites(String[] exludedCipherSuites) {
        this.exludedCipherSuites = exludedCipherSuites;
    }

    public SSLExcludeCipherConnectionHelper(String trustCert) {
        this.trustCert = trustCert;

        try {
            this.initTrustManager();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    private void initTrustManager() throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = new BufferedInputStream(new FileInputStream(this.trustCert));
        Certificate ca = null;

        try {
            ca = cf.generateCertificate(caInput);
        } finally {
            caInput.close();
        }

        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load((InputStream)null, (char[])null);
        keyStore.setCertificateEntry("ca", ca);
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        this.tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        this.tmf.init(keyStore);
    }

    public String get(URL url) throws Exception {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init((KeyManager[])null, this.tmf.getTrustManagers(), (SecureRandom)null);
        SSLParameters params = context.getSupportedSSLParameters();
        List<String> enabledCiphers = new ArrayList();
        String[] var8;
        int var7 = (var8 = params.getCipherSuites()).length;

        for(int var6 = 0; var6 < var7; ++var6) {
            String cipher = var8[var6];
            boolean exclude = false;
            if (this.exludedCipherSuites != null) {
                for(int i = 0; i < this.exludedCipherSuites.length && !exclude; ++i) {
                    exclude = cipher.indexOf(this.exludedCipherSuites[i]) >= 0;
                }
            }

            if (!exclude) {
                enabledCiphers.add(cipher);
            }
        }

        String[] cArray = new String[enabledCiphers.size()];
        enabledCiphers.toArray(cArray);
        HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
        SSLSocketFactory sf1 = context.getSocketFactory();
        SSLSocketFactory sf = new SSLExcludeCipherConnectionHelper.DOSSLSocketFactory(sf1, cArray, (SSLExcludeCipherConnectionHelper.DOSSLSocketFactory)null);
        urlConnection.setSSLSocketFactory(sf);
        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        StringBuffer buffer = new StringBuffer();

        String inputLine;
        while((inputLine = in.readLine()) != null) {
            buffer.append(inputLine);
        }

        in.close();
        return buffer.toString();
    }

    private class DOSSLSocketFactory extends SSLSocketFactory {
        private SSLSocketFactory sf;
        private String[] enabledCiphers;

        private DOSSLSocketFactory(SSLSocketFactory sf, String[] enabledCiphers, DOSSLSocketFactory dosslSocketFactory) {
            this.sf = null;
            this.enabledCiphers = null;
            this.sf = sf;
            this.enabledCiphers = enabledCiphers;
        }

        private Socket getSocketWithEnabledCiphers(Socket socket) {
            if (this.enabledCiphers != null && socket != null && socket instanceof SSLSocket) {
                ((SSLSocket)socket).setEnabledCipherSuites(this.enabledCiphers);
            }

            return socket;
        }

        public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
            return this.getSocketWithEnabledCiphers(this.sf.createSocket(s, host, port, autoClose));
        }

        public String[] getDefaultCipherSuites() {
            return this.sf.getDefaultCipherSuites();
        }

        public String[] getSupportedCipherSuites() {
            return this.enabledCiphers == null ? this.sf.getSupportedCipherSuites() : this.enabledCiphers;
        }

        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            return this.getSocketWithEnabledCiphers(this.sf.createSocket(host, port));
        }

        public Socket createSocket(InetAddress address, int port) throws IOException {
            return this.getSocketWithEnabledCiphers(this.sf.createSocket(address, port));
        }

        public Socket createSocket(String host, int port, InetAddress localAddress, int localPort) throws IOException, UnknownHostException {
            return this.getSocketWithEnabledCiphers(this.sf.createSocket(host, port, localAddress, localPort));
        }

        public Socket createSocket(InetAddress address, int port, InetAddress localaddress, int localport) throws IOException {
            return this.getSocketWithEnabledCiphers(this.sf.createSocket(address, port, localaddress, localport));
        }
    }
}
