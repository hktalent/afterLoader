 package org.json;
 
 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.Reader;
 import java.io.StringReader;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class JSONTokener
 {
   private long character;
   private boolean eof;
   private long index;
   private long line;
   private char previous;
   private final Reader reader;
   private boolean usePrevious;
   private long characterPreviousLine;
   
   public JSONTokener(Reader reader)
   {
     this.reader = (reader.markSupported() ? reader : new BufferedReader(reader));
     this.eof = false;
     this.usePrevious = false;
     this.previous = '\000';
     this.index = 0L;
     this.character = 1L;
     this.characterPreviousLine = 0L;
     this.line = 1L;
   }
   
 
 
 
 
   public JSONTokener(InputStream inputStream)
   {
     this(new InputStreamReader(inputStream));
   }
   
 
 
 
 
 
   public JSONTokener(String s)
   {
     this(new StringReader(s));
   }
   
 
 
 
 
 
 
   public void back()
     throws JSONException
   {
     if ((this.usePrevious) || (this.index <= 0L)) {
       throw new JSONException("Stepping back two steps is not supported");
     }
     decrementIndexes();
     this.usePrevious = true;
     this.eof = false;
   }
   
 
 
   private void decrementIndexes()
   {
     this.index -= 1L;
     if ((this.previous == '\r') || (this.previous == '\n')) {
       this.line -= 1L;
       this.character = this.characterPreviousLine;
     } else if (this.character > 0L) {
       this.character -= 1L;
     }
   }
   
 
 
 
 
 
   public static int dehexchar(char c)
   {
     if ((c >= '0') && (c <= '9')) {
       return c - '0';
     }
     if ((c >= 'A') && (c <= 'F')) {
       return c - '7';
     }
     if ((c >= 'a') && (c <= 'f')) {
       return c - 'W';
     }
     return -1;
   }
   
 
 
 
 
   public boolean end()
   {
     return (this.eof) && (!this.usePrevious);
   }
   
 
 
 
 
 
 
   public boolean more()
     throws JSONException
   {
     if (this.usePrevious) {
       return true;
     }
     try {
       this.reader.mark(1);
     } catch (IOException e) {
       throw new JSONException("Unable to preserve stream position", e);
     }
     try
     {
       if (this.reader.read() <= 0) {
         this.eof = true;
         return false;
       }
       this.reader.reset();
     } catch (IOException e) {
       throw new JSONException("Unable to read the next character from the stream", e);
     }
     return true;
   }
   
 
 
 
   public char next()
     throws JSONException
   {
     int c;
     
 
     if (this.usePrevious) {
       this.usePrevious = false;
       c = this.previous;
     } else {
       try {
         c = this.reader.read();
       } catch (IOException exception) { int c;
         throw new JSONException(exception);
       } }
     int c;
     if (c <= 0) {
       this.eof = true;
       return '\000';
     }
     incrementIndexes(c);
     this.previous = ((char)c);
     return this.previous;
   }
   
 
 
 
 
   private void incrementIndexes(int c)
   {
     if (c > 0) {
       this.index += 1L;
       if (c == 13) {
         this.line += 1L;
         this.characterPreviousLine = this.character;
         this.character = 0L;
       } else if (c == 10) {
         if (this.previous != '\r') {
           this.line += 1L;
           this.characterPreviousLine = this.character;
         }
         this.character = 0L;
       } else {
         this.character += 1L;
       }
     }
   }
   
 
 
 
 
 
   public char next(char c)
     throws JSONException
   {
     char n = next();
     if (n != c) {
       if (n > 0) {
         throw syntaxError("Expected '" + c + "' and instead saw '" + n + "'");
       }
       
       throw syntaxError("Expected '" + c + "' and instead saw ''");
     }
     return n;
   }
   
 
 
 
 
 
 
 
 
   public String next(int n)
     throws JSONException
   {
     if (n == 0) {
       return "";
     }
     
     char[] chars = new char[n];
     int pos = 0;
     
     while (pos < n) {
       chars[pos] = next();
       if (end()) {
         throw syntaxError("Substring bounds error");
       }
       pos++;
     }
     return new String(chars);
   }
   
 
 
 
   public char nextClean()
     throws JSONException
   {
     for (;;)
     {
       char c = next();
       if ((c == 0) || (c > ' ')) {
         return c;
       }
     }
   }
   
 
 
 
 
 
 
 
 
 
 
 
   public String nextString(char quote)
     throws JSONException
   {
     StringBuilder sb = new StringBuilder();
     for (;;) {
       char c = next();
       switch (c) {
       case '\000': 
       case '\n': 
       case '\r': 
         throw syntaxError("Unterminated string");
       case '\\': 
         c = next();
         switch (c) {
         case 'b': 
           sb.append('\b');
           break;
         case 't': 
           sb.append('\t');
           break;
         case 'n': 
           sb.append('\n');
           break;
         case 'f': 
           sb.append('\f');
           break;
         case 'r': 
           sb.append('\r');
           break;
         case 'u': 
           try {
             sb.append((char)Integer.parseInt(next(4), 16));
           } catch (NumberFormatException e) {
             throw syntaxError("Illegal escape.", e);
           }
         
         case '"': 
         case '\'': 
         case '/': 
         case '\\': 
           sb.append(c);
           break;
         default: 
           throw syntaxError("Illegal escape.");
         }
         break;
       default: 
         if (c == quote) {
           return sb.toString();
         }
         sb.append(c);
       }
       
     }
   }
   
 
 
 
 
 
 
   public String nextTo(char delimiter)
     throws JSONException
   {
     StringBuilder sb = new StringBuilder();
     for (;;) {
       char c = next();
       if ((c == delimiter) || (c == 0) || (c == '\n') || (c == '\r')) {
         if (c != 0) {
           back();
         }
         return sb.toString().trim();
       }
       sb.append(c);
     }
   }
   
 
 
 
 
 
 
 
 
   public String nextTo(String delimiters)
     throws JSONException
   {
     StringBuilder sb = new StringBuilder();
     for (;;) {
       char c = next();
       if ((delimiters.indexOf(c) >= 0) || (c == 0) || (c == '\n') || (c == '\r'))
       {
         if (c != 0) {
           back();
         }
         return sb.toString().trim();
       }
       sb.append(c);
     }
   }
   
 
 
 
 
 
 
   public Object nextValue()
     throws JSONException
   {
     char c = nextClean();
     
 
     switch (c) {
     case '"': 
     case '\'': 
       return nextString(c);
     case '{': 
       back();
       return new JSONObject(this);
     case '[': 
       back();
       return new JSONArray(this);
     }
     
     
 
 
 
 
 
 
 
 
     StringBuilder sb = new StringBuilder();
     while ((c >= ' ') && (",:]}/\\\"[{;=#".indexOf(c) < 0)) {
       sb.append(c);
       c = next();
     }
     back();
     
     String string = sb.toString().trim();
     if ("".equals(string)) {
       throw syntaxError("Missing value");
     }
     return JSONObject.stringToValue(string);
   }
   
 
 
 
 
 
 
 
 
   public char skipTo(char to)
     throws JSONException
   {
     try
     {
       long startIndex = this.index;
       long startCharacter = this.character;
       long startLine = this.line;
       this.reader.mark(1000000);
       char c;
       do { c = next();
         if (c == 0)
         {
 
 
           this.reader.reset();
           this.index = startIndex;
           this.character = startCharacter;
           this.line = startLine;
           return '\000';
         }
       } while (c != to);
       this.reader.mark(1);
     } catch (IOException exception) {
       throw new JSONException(exception); }
     char c;
     back();
     return c;
   }
   
 
 
 
 
 
   public JSONException syntaxError(String message)
   {
     return new JSONException(message + toString());
   }
   
 
 
 
 
 
 
   public JSONException syntaxError(String message, Throwable causedBy)
   {
     return new JSONException(message + toString(), causedBy);
   }
   
 
 
 
 
 
   public String toString()
   {
     return " at " + this.index + " [character " + this.character + " line " + this.line + "]";
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/json-20180130.jar!/org/json/JSONTokener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */