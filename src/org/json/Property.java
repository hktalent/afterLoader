 package org.json;
 
 import java.util.Enumeration;
 import java.util.Properties;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class Property
 {
   public static JSONObject toJSONObject(Properties properties)
     throws JSONException
   {
     JSONObject jo = new JSONObject();
     if ((properties != null) && (!properties.isEmpty())) {
       Enumeration<?> enumProperties = properties.propertyNames();
       while (enumProperties.hasMoreElements()) {
         String name = (String)enumProperties.nextElement();
         jo.put(name, properties.getProperty(name));
       }
     }
     return jo;
   }
   
 
 
 
 
   public static Properties toProperties(JSONObject jo)
     throws JSONException
   {
     Properties properties = new Properties();
     if (jo != null)
     {
       for (String key : jo.keySet()) {
         Object value = jo.opt(key);
         if (!JSONObject.NULL.equals(value)) {
           properties.put(key, value.toString());
         }
       }
     }
     return properties;
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/json-20180130.jar!/org/json/Property.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */