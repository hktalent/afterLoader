package test;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class test {
    public static void main(String[] args)
            throws Exception {
        String line = "cookie-";

        System.out.println(checkPort("1080"));
    }


    public static boolean checkPort(String portTxt) {
        String port = "([1-9]{1,5})";
        Pattern pattern = Pattern.compile(port);
        Matcher matcher = pattern.matcher(portTxt);
        System.out.println(matcher.matches());
        return (matcher.matches()) && (Integer.parseInt(portTxt) >= 1) && (Integer.parseInt(portTxt) <= 65535);
    }

    private static Map<String, String> parseHeaders(String headerTxt) {
        Map<String, String> headers = new HashMap();
        String[] arrayOfString;
        int j = (arrayOfString = headerTxt.split("\n")).length;
        for (int i = 0; i < j; i++) {
            String line = arrayOfString[i];

            int semiIndex = line.indexOf(":");
            if (semiIndex > 0) {
                String key = line.substring(0, semiIndex);
                key = formatHeaderName(key);
                String value = line.substring(semiIndex + 1);
                if (!value.equals("")) {
                    headers.put(key, value);
                }
            }
        }
        return headers;
    }

    private static String formatHeaderName(String beforeName) {
        String afterName = "";
        String[] arrayOfString;
        int j = (arrayOfString = beforeName.split("-")).length;
        for (int i = 0; i < j; i++) {
            String element = arrayOfString[i];

            element = new StringBuilder(String.valueOf(element.charAt(0))).toString().toUpperCase() + element.substring(1).toLowerCase();
            afterName = afterName + element + "-";
        }
        if ((afterName.length() - beforeName.length() == 1) && (afterName.endsWith("-")))
            afterName = afterName.substring(0, afterName.length() - 1);
        return afterName;
    }
}
