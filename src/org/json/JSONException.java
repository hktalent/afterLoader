 package org.json;
 
 
 
 
 
 
 public class JSONException
   extends RuntimeException
 {
   private static final long serialVersionUID = 0L;
   
 
 
 
 
 
   public JSONException(String message)
   {
     super(message);
   }
   
 
 
 
 
 
 
 
   public JSONException(String message, Throwable cause)
   {
     super(message, cause);
   }
   
 
 
 
 
 
   public JSONException(Throwable cause)
   {
     super(cause.getMessage(), cause);
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/json-20180130.jar!/org/json/JSONException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */