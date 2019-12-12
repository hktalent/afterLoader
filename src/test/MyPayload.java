 package test;
 
 import javax.servlet.jsp.PageContext;
 
 public class MyPayload
 {
   public boolean equals(Object obj)
   {
     PageContext page = (PageContext)obj;
     try {
       String currentPath = new java.io.File("").getAbsolutePath();
       page.getResponse().getWriter().println(currentPath);
     }
     catch (Exception e) {
       e.printStackTrace();
     }
     return true;
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/test/MyPayload.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */