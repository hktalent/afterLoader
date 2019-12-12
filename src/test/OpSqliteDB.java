 package test;
 
 import java.io.PrintStream;
 import java.sql.Connection;
 import java.sql.DriverManager;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 
 public class OpSqliteDB
 {
   private static final String Class_Name = "org.sqlite.JDBC";
   private static final String DB_URL = "jdbc:sqlite:F:\\xxxdatabase.db";
   
   /* Error */
   public static void main(String[] args)
   {
     // Byte code:
     //   0: aconst_null
     //   1: astore_1
     //   2: invokestatic 24	test/OpSqliteDB:createConnection	()Ljava/sql/Connection;
     //   5: astore_1
     //   6: aload_1
     //   7: invokestatic 28	test/OpSqliteDB:func1	(Ljava/sql/Connection;)V
     //   10: getstatic 32	java/lang/System:out	Ljava/io/PrintStream;
     //   13: ldc 38
     //   15: invokevirtual 40	java/io/PrintStream:println	(Ljava/lang/String;)V
     //   18: goto +116 -> 134
     //   21: astore_2
     //   22: getstatic 46	java/lang/System:err	Ljava/io/PrintStream;
     //   25: aload_2
     //   26: invokevirtual 49	java/sql/SQLException:getMessage	()Ljava/lang/String;
     //   29: invokevirtual 40	java/io/PrintStream:println	(Ljava/lang/String;)V
     //   32: aload_2
     //   33: invokevirtual 55	java/sql/SQLException:printStackTrace	()V
     //   36: aload_1
     //   37: ifnull +125 -> 162
     //   40: aload_1
     //   41: invokeinterface 58 1 0
     //   46: goto +116 -> 162
     //   49: astore 4
     //   51: getstatic 46	java/lang/System:err	Ljava/io/PrintStream;
     //   54: aload 4
     //   56: invokevirtual 63	java/io/PrintStream:println	(Ljava/lang/Object;)V
     //   59: aload 4
     //   61: invokevirtual 55	java/sql/SQLException:printStackTrace	()V
     //   64: goto +98 -> 162
     //   67: astore_2
     //   68: aload_2
     //   69: invokevirtual 66	java/lang/Exception:printStackTrace	()V
     //   72: aload_1
     //   73: ifnull +89 -> 162
     //   76: aload_1
     //   77: invokeinterface 58 1 0
     //   82: goto +80 -> 162
     //   85: astore 4
     //   87: getstatic 46	java/lang/System:err	Ljava/io/PrintStream;
     //   90: aload 4
     //   92: invokevirtual 63	java/io/PrintStream:println	(Ljava/lang/Object;)V
     //   95: aload 4
     //   97: invokevirtual 55	java/sql/SQLException:printStackTrace	()V
     //   100: goto +62 -> 162
     //   103: astore_3
     //   104: aload_1
     //   105: ifnull +27 -> 132
     //   108: aload_1
     //   109: invokeinterface 58 1 0
     //   114: goto +18 -> 132
     //   117: astore 4
     //   119: getstatic 46	java/lang/System:err	Ljava/io/PrintStream;
     //   122: aload 4
     //   124: invokevirtual 63	java/io/PrintStream:println	(Ljava/lang/Object;)V
     //   127: aload 4
     //   129: invokevirtual 55	java/sql/SQLException:printStackTrace	()V
     //   132: aload_3
     //   133: athrow
     //   134: aload_1
     //   135: ifnull +27 -> 162
     //   138: aload_1
     //   139: invokeinterface 58 1 0
     //   144: goto +18 -> 162
     //   147: astore 4
     //   149: getstatic 46	java/lang/System:err	Ljava/io/PrintStream;
     //   152: aload 4
     //   154: invokevirtual 63	java/io/PrintStream:println	(Ljava/lang/Object;)V
     //   157: aload 4
     //   159: invokevirtual 55	java/sql/SQLException:printStackTrace	()V
     //   162: return
     // Line number table:
     //   Java source line #16	-> byte code offset #0
     //   Java source line #18	-> byte code offset #2
     //   Java source line #19	-> byte code offset #6
     //   Java source line #20	-> byte code offset #10
     //   Java source line #21	-> byte code offset #18
     //   Java source line #22	-> byte code offset #22
     //   Java source line #23	-> byte code offset #32
     //   Java source line #28	-> byte code offset #36
     //   Java source line #29	-> byte code offset #40
     //   Java source line #30	-> byte code offset #46
     //   Java source line #32	-> byte code offset #51
     //   Java source line #33	-> byte code offset #59
     //   Java source line #24	-> byte code offset #67
     //   Java source line #25	-> byte code offset #68
     //   Java source line #28	-> byte code offset #72
     //   Java source line #29	-> byte code offset #76
     //   Java source line #30	-> byte code offset #82
     //   Java source line #32	-> byte code offset #87
     //   Java source line #33	-> byte code offset #95
     //   Java source line #26	-> byte code offset #103
     //   Java source line #28	-> byte code offset #104
     //   Java source line #29	-> byte code offset #108
     //   Java source line #30	-> byte code offset #114
     //   Java source line #32	-> byte code offset #119
     //   Java source line #33	-> byte code offset #127
     //   Java source line #35	-> byte code offset #132
     //   Java source line #28	-> byte code offset #134
     //   Java source line #29	-> byte code offset #138
     //   Java source line #30	-> byte code offset #144
     //   Java source line #32	-> byte code offset #149
     //   Java source line #33	-> byte code offset #157
     //   Java source line #36	-> byte code offset #162
     // Local variable table:
     //   start	length	slot	name	signature
     //   0	163	0	args	String[]
     //   1	138	1	connection	Connection
     //   21	12	2	e	SQLException
     //   67	2	2	e	Exception
     //   103	30	3	localObject	Object
     //   49	11	4	e	SQLException
     //   85	11	4	e	SQLException
     //   117	11	4	e	SQLException
     //   147	11	4	e	SQLException
     // Exception table:
     //   from	to	target	type
     //   2	18	21	java/sql/SQLException
     //   36	46	49	java/sql/SQLException
     //   2	18	67	java/lang/Exception
     //   72	82	85	java/sql/SQLException
     //   2	36	103	finally
     //   67	72	103	finally
     //   104	114	117	java/sql/SQLException
     //   134	144	147	java/sql/SQLException
   }
   
   public static Connection createConnection()
     throws SQLException, ClassNotFoundException
   {
     Class.forName("org.sqlite.JDBC");
     return DriverManager.getConnection("jdbc:sqlite:F:\\xxxdatabase.db");
   }
   
   public static void func1(Connection connection) throws SQLException {
     Statement statement = connection.createStatement();
     Statement statement1 = connection.createStatement();
     statement.setQueryTimeout(30);
     
     ResultSet rs = statement.executeQuery("select * from table_name1");
     while (rs.next()) {
       String col1 = rs.getString("col1_name");
       String col2 = rs.getString("col2_name");
       System.out.println("col1 = " + col1 + "  col2 = " + col2);
       
       System.out.println(col1);
       
       statement1.executeUpdate("insert into table_name2(col2) values('3')");
       
       statement1.executeUpdate("update table_name2 set 字段名1=55 where 字段名2='66'");
     }
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/test/OpSqliteDB.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */