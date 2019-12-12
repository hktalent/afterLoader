 package org.json;
 
 import java.io.StringWriter;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class JSONStringer
   extends JSONWriter
 {
   public JSONStringer()
   {
     super(new StringWriter());
   }
   
 
 
 
 
 
 
 
 
   public String toString()
   {
     return this.mode == 'd' ? this.writer.toString() : null;
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/json-20180130.jar!/org/json/JSONStringer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */