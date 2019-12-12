 package net.rebeyond.behinder.utils;
 
 import java.io.FilterInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.Iterator;
 import java.util.LinkedList;
 
 public class ReplacingInputStream extends FilterInputStream
 {
   LinkedList<Integer> inQueue = new LinkedList();
   LinkedList<Integer> outQueue = new LinkedList();
   final byte[] search;
   final byte[] replacement;
   
   public ReplacingInputStream(InputStream in, byte[] search, byte[] replacement)
   {
     super(in);
     this.search = search;
     this.replacement = replacement;
   }
   
   private boolean isMatchFound() {
     Iterator<Integer> inIter = this.inQueue.iterator();
     for (int i = 0; i < this.search.length; i++)
       if ((!inIter.hasNext()) || (this.search[i] != ((Integer)inIter.next()).intValue()))
         return false;
     return true;
   }
   
   private void readAhead() throws IOException
   {
     while (this.inQueue.size() < this.search.length) {
       int next = super.read();
       this.inQueue.offer(Integer.valueOf(next));
       if (next == -1) {
         break;
       }
     }
   }
   
   public int read() throws IOException
   {
     if (this.outQueue.isEmpty()) {
       readAhead();
       
       if (isMatchFound()) {
         for (int i = 0; i < this.search.length; i++)
           this.inQueue.remove();
         byte[] arrayOfByte;
         int j = (arrayOfByte = this.replacement).length; for (int i = 0; i < j; i++) { byte b = arrayOfByte[i];
           this.outQueue.offer(Integer.valueOf(b));
         }
       } else { this.outQueue.add((Integer)this.inQueue.remove());
       }
     }
     return ((Integer)this.outQueue.remove()).intValue();
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/net/rebeyond/behinder/utils/ReplacingInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */